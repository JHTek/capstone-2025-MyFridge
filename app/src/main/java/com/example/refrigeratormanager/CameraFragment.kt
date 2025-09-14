package com.example.refrigeratormanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.FragmentCameraBinding
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.app.AlertDialog
import com.example.refrigeratormanager.product.ProductUploadFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
//import com.example.myapp.BuildConfig

interface ApiService {
    @Multipart
    @POST("detect") // 🔹 Flask 서버의 엔드포인트 경로
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>

//    @POST("/ingredients/add")
//    fun addIngredients(@Body ingredientsDtoList: List<IngredientRequestDTO>): Call<ResponseBody>
}

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private var serverResponse: String? = null  // ✅ 서버 응답을 저장할 변수 추가

    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var photoFile: File? = null // ✅ 전역 변수로 이동
    //private val baseUrl = "http://${BuildConfig.SERVER_IP}:5000/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // ✅ 촬영 버튼 클릭 시 사진 촬영 및 서버 전송
        binding.btnCapture.setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder().build()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraFragment", "카메라 실행 실패: ", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // ✅ 사진 촬영 및 즉시 서버 전송
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        photoFile = File(
            requireContext().externalMediaDirs.firstOrNull(),
            SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile!!).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(requireContext(), "사진 저장됨", Toast.LENGTH_SHORT).show()

                    // ✅ 촬영 후 서버에 업로드
                    uploadPhoto(photoFile!!)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraFragment", "사진 촬영 실패", exception)
                }
            }
        )
    }

    // ✅ 서버로 사진 업로드
    private fun uploadPhoto(photoFile: File) {
        val requestBody = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("image", photoFile.name, requestBody)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.45.193:5000/") // 🔹 서버 URL 설정**************https://36db-39-115-67-181.ngrok-free.app/***************
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.uploadImage(multipartBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let { jsonResponse ->
                        serverResponse = jsonResponse  // ✅ 서버 응답 저장
                        Log.d("CameraFragment", "서버 응답: $serverResponse")

//                        showResponseDialog("업로드 성공", serverResponse!!) {
//                            // ✅ 서버 응답과 함께 페이지 이동
//                            moveToProductUpload(serverResponse!!)
//                        }
                        moveToProductUpload(serverResponse!!)
                    }
                } else {
                    Log.e("CameraFragment", "서버 응답 실패: ${response.code()}")
                    showResponseDialog("업로드 실패", "서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CameraFragment", "사진 업로드 실패", t)
                showResponseDialog("업로드 실패", "네트워크 오류: ${t.localizedMessage}")
            }
        })
    }

    private fun showResponseDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }

    private fun moveToProductUpload(serverResponse: String) {
        val productMap = parseServerResponse(serverResponse) // ✅ JSON을 Map으로 변환

        val productUploadFragment = ProductUploadFragment().apply {
            arguments = Bundle().apply {
                putSerializable("productData", HashMap(productMap)) // ✅ Map을 Bundle에 저장
            }
        }

        productUploadFragment.show(parentFragmentManager, "ProductUploadFragment")
    }


    private fun showResponseDialog(title: String, message: String, onDismiss: (() -> Unit)? = null) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인") { _, _ ->
                onDismiss?.invoke() // ✅ 다이얼로그가 닫힐 때 실행할 함수 호출
            }
            .show()
    }

    // ✅ JSON을 Map<String, Int>로 변환하는 함수 추가
    private fun parseServerResponse(response: String): Map<String, Int> {
        return try {
            val jsonObject = JSONObject(response)
            val resultMap = mutableMapOf<String, Int>()

            jsonObject.keys().forEach { key ->
                resultMap[key] = jsonObject.getInt(key)
            }
            resultMap
        } catch (e: Exception) {
            Log.e("CameraFragment", "JSON 파싱 오류", e)
            emptyMap()
        }
    }

}
