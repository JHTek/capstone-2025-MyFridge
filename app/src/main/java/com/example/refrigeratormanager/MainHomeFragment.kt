package com.example.refrigeratormanager

import com.example.refrigeratormanager.voiceAndgpt.chatGPTDTO.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.*
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.FragmentMainHomeBinding
import java.util.Locale
import android.Manifest
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.refrigeratormanager.voiceAndgpt.ChatGPTApi
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.appcompat.app.AlertDialog


class MainHomeFragment : Fragment() {
    private var _binding: FragmentMainHomeBinding? = null
    private val binding get() = _binding!!
    private val RECORD_AUDIO_PERMISSION_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainHomeBinding.inflate(inflater, container, false)

        // 체크리스트 미리보기 로드
        loadChecklistPreview()

        // 바코드 이동
        binding.btnBarcode.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BarcodeFragment())
                .addToBackStack(null)
                .commit()
        }

        // 카메라 이동
        binding.btnCamera.setOnClickListener {
            startActivity(Intent(requireContext(), CameraActivity::class.java))
        }

        // 음성 버튼 클릭 시 음성 인식 시작
        binding.btnVoice.setOnClickListener {
            checkAudioPermissionAndStartSpeech()
        }

        // 유통기한 더보기 이동

        // 체크리스트 추가 이동
        binding.btnAddChecklist.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ChecklistFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun loadChecklistPreview() {
        val sharedPreferences = requireContext().getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
        val items = sharedPreferences.getString("checklist", "") ?: ""

        val previewContainer = binding.checklistPreviewContainer
        previewContainer.removeAllViews()

        if (items.isNotEmpty()) {
            items.split("#").forEach { item ->
                val parts = item.split("|")
                if (parts.size == 2) {
                    val text = parts[0]
                    val checked = parts[1].toBoolean()

                    val itemLayout = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 0, 12) // 하단 간격 12dp
                        }
                    }

                    val checkBox = CheckBox(requireContext()).apply {
                        this.text = text
                        this.isChecked = checked
                        this.setTextColor(Color.BLACK)
                        this.textSize = 20f // 텍스트 크기
                    }

                    itemLayout.addView(checkBox)
                    previewContainer.addView(itemLayout)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkAudioPermissionAndStartSpeech() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_CODE
            )
        } else {
            startSpeechRecognition()
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        val recognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val resultText = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (resultText != null) {
                    //Toast.makeText(requireContext(), "음성 인식 결과: $resultText", Toast.LENGTH_SHORT).show()
                    // 🧠 ChatGPT 호출 연결
                    callChatGPT(resultText)
                }
            }

            override fun onError(error: Int) {
                Toast.makeText(requireContext(), "음성 인식 실패: $error", Toast.LENGTH_SHORT).show()
            }

            // 필수는 아님: 아래는 생략 가능
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer.startListening(intent)
    }

    private fun callChatGPT(ingredientInput: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ChatGPTApi::class.java)

        val messages = listOf(
            Message("system", "너는 식재료 등록을 도와주는 어시스턴트야. 사용자의 문장에서 식재료명과 갯수를 JSON 형태로 추출해줘. 예: '감자 3개, 당근 2개' → {\"감자\": 3, \"당근\": 2}. 다른 말은 절대 하지 마."),
            Message("user", ingredientInput)
        )

        val request = ChatRequest(messages = messages)

        lifecycleScope.launch {
            try {
                val response = api.getChatResponse("Bearer ${BuildConfig.OPENAI_API_KEY}", request)
                val reply = response.choices.firstOrNull()?.message?.content

                if (reply != null) {
                    val parsedMap = parseServerResponse(reply)
                    if (parsedMap.isNotEmpty()) {
                        showResponseDialog("GPT 응답", reply) {
                            moveToProductUpload(parsedMap)
                        }
                    } else {
                        showResponseDialog("GPT 응답 실패", "JSON 파싱 실패: $reply")
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "GPT 호출 실패: ${e.message}", Toast.LENGTH_SHORT).show()

                // 🧪 [임시 하드코딩 시작]
                val simulatedReply = """{"사과": 2, "바나나": 3}"""
                val parsedMap = parseServerResponse(simulatedReply)
                showResponseDialog("임시 응답 (GPT 실패)", simulatedReply) {
                    moveToProductUpload(parsedMap)
                }
                // 🧪 [임시 하드코딩 끝]
            }
        }
    }

    private fun parseServerResponse(response: String): Map<String, Int> {
        return try {
            val jsonObject = JSONObject(response)
            val resultMap = mutableMapOf<String, Int>()
            jsonObject.keys().forEach { key ->
                resultMap[key] = jsonObject.getInt(key)
            }
            resultMap
        } catch (e: Exception) {
            Log.e("MainHomeFragment", "JSON 파싱 오류", e)
            emptyMap()
        }
    }

    private fun showResponseDialog(title: String, message: String, onDismiss: (() -> Unit)? = null) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인") { _, _ ->
                onDismiss?.invoke()
            }
            .show()
    }

    private fun moveToProductUpload(data: Map<String, Int>) {
        val fragment = ProductUploadFragment().apply {
            arguments = Bundle().apply {
                putSerializable("productData", HashMap(data)) // Serializable로 전달
            }
        }

        fragment.show(parentFragmentManager, "ProductUploadFragment")
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startSpeechRecognition()
        } else {
            Toast.makeText(requireContext(), "음성 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
