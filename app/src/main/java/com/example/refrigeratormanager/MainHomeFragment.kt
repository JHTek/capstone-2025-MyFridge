package com.example.refrigeratormanager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.refrigeratormanager.databinding.FragmentMainHomeBinding
import com.example.refrigeratormanager.product.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import android.speech.RecognizerIntent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.refrigeratormanager.product.ProductUploadFragment
import com.example.refrigeratormanager.voiceAndgpt.ChatGPTApi
import com.example.refrigeratormanager.voiceAndgpt.chatGPTDTO.ChatRequest
import com.example.refrigeratormanager.voiceAndgpt.chatGPTDTO.Message
import org.json.JSONObject

class MainHomeFragment : Fragment() {

    private var _binding: FragmentMainHomeBinding? = null
    private val binding get() = _binding!!
    private val RECORD_AUDIO_PERMISSION_CODE = 101
    private var listeningDialog: AlertDialog? = null

    // ExpirationListFragment와 공유 ViewModel
    private val sharedViewModel: ExpirationViewModel by activityViewModels()
    private val alertDays = 7

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainHomeBinding.inflate(inflater, container, false)

        // 체크리스트 미리보기
        loadChecklistPreview()

        // 서버에서 유통기한 임박 데이터 바로 가져오기
        fetchExpiringProducts()

        // ViewModel observe → 최대 3개 미리보기
        sharedViewModel.expiringProducts.observe(viewLifecycleOwner) { products ->
            showExpirationPreview(products.sortedBy { it.expirationDate }.take(3))
        }

        // 버튼 클릭 이벤트
        binding.btnBarcode.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BarcodeFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnCamera.setOnClickListener {
            startActivity(Intent(requireContext(), CameraActivity::class.java))
        }

        binding.btnVoice.setOnClickListener {
            showListeningDialog()
            checkAudioPermissionAndStartSpeech()
        }

        binding.btnMoreExpiry.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ExpirationListFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnAddChecklist.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ChecklistFragment())
                .addToBackStack(null)
                .commit()
        }

        // WindowInsets 적용: 상태바 / 내비게이션 바 공간 확보
        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(
                view.paddingLeft,
                insets.systemWindowInsetTop,
                view.paddingRight,
                insets.systemWindowInsetBottom
            )
            insets.consumeSystemWindowInsets()
        }

        return binding.root
    }

    // 🔹 체크리스트 미리보기
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
                        ).apply { setMargins(0, 0, 0, 12) }
                    }
                    val checkBox = CheckBox(requireContext()).apply {
                        this.text = text
                        this.isChecked = checked
                        this.setTextColor(Color.BLACK)
                        this.textSize = 20f
                    }
                    itemLayout.addView(checkBox)
                    previewContainer.addView(itemLayout)
                }
            }
        }
    }

    // 🔹 유통기한 임박 미리보기
    private fun showExpirationPreview(products: List<Product>) {
        val previewContainer = binding.expirationPreviewContainer
        previewContainer.removeAllViews()

        if (products.isEmpty()) {
            val tv = TextView(requireContext()).apply {
                text = "유통기한 임박 상품이 없습니다"
                textSize = 16f
                setTextColor(Color.GRAY)
            }
            previewContainer.addView(tv)
        } else {
            products.forEach { product ->
                val tv = TextView(requireContext()).apply {
                    text = "${product.ingredientsName} | ${product.refrigeratorName} | ${product.expirationDate} | 수량: ${product.quantity}"
                    textSize = 16f
                }
                val layout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 0, 0, 12) }
                    addView(tv)
                }
                previewContainer.addView(layout)
            }
        }
    }

    // 🔹 서버에서 유통기한 임박 상품 가져오기
    private fun fetchExpiringProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = getUserToken()
                val userId = getUserId()

                val logging = HttpLoggingInterceptor { message -> android.util.Log.d("Retrofit", message) }
                    .apply { level = HttpLoggingInterceptor.Level.BODY }

                val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://${BuildConfig.SERVER_IP}:8080/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val api = retrofit.create(ExpirationApi::class.java)

                val products: List<Product> = withContext(Dispatchers.IO) {
                    api.getExpiringProducts("Bearer $token", userId, alertDays)
                }

                // 날짜 가까운 순 정렬 후 ViewModel에 저장
                sharedViewModel.setExpiringProducts(products.sortedBy { it.expirationDate })

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "유통기한 임박 데이터 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserToken(): String {
        val prefs = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return prefs.getString("JWT_TOKEN", "") ?: ""
    }

    private fun getUserId(): String {
        val prefs = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return prefs.getString("USER_ID", "") ?: ""
    }

    // 🔹 음성 인식 관련
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
                if (resultText != null) callChatGPT(resultText)
                else {
                    dismissListeningDialog()
                    Toast.makeText(requireContext(), "음성 인식 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(error: Int) {
                dismissListeningDialog()
                Toast.makeText(requireContext(), "음성 인식 실패: $error", Toast.LENGTH_SHORT).show()
            }

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
            Message("system", "너는 식재료 등록을 도와주는 어시스턴트야. 사용자의 문장에서 식재료명과 갯수를 JSON 형태로 추출해줘. 예: '감자 3개, 당근 2개 샀어' → {\"감자\": 3, \"당근\": 2}. 다른 말은 절대 하지 마."),
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
                        dismissListeningDialog() // ✅ 응답 성공 시 대화상자 닫기
                        moveToProductUpload(parsedMap)
                    } else {
                        dismissListeningDialog()
                        showResponseDialog("GPT 응답 실패", "JSON 파싱 실패: $reply")
                    }
                }

            } catch (e: Exception) {
                dismissListeningDialog()
                Toast.makeText(requireContext(), "GPT 호출 실패: ${e.message}", Toast.LENGTH_SHORT).show()
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

        fragment.show(parentFragmentManager, "ProductUploadFragment")// ChatGPT 호출 코드
    }

    private fun showListeningDialog() {
        listeningDialog = AlertDialog.Builder(requireContext())
            .setTitle("음성 인식 중")
            .setMessage("말씀하신 내용을 인식하고 있어요...")
            .setCancelable(false)
            .create()
        listeningDialog?.show()
    }

    private fun dismissListeningDialog() {
        listeningDialog?.dismiss()
        listeningDialog = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) startSpeechRecognition()
        else Toast.makeText(requireContext(), "음성 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
