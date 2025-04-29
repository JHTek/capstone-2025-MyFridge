package com.example.refrigeratormanager

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.FragmentBarcodeBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeFragment : Fragment() {
    private lateinit var binding: FragmentBarcodeBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner

    private var isBarcodeDetected = false // ✅ 중복 감지 방지

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBarcodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (isBarcodeDetected) {
                            imageProxy.close()
                            return@setAnalyzer
                        }

                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            barcodeScanner.process(inputImage)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        barcode.rawValue?.let { rawValue ->
                                            Log.d("BarcodeFragment", "바코드 값: $rawValue")
                                            isBarcodeDetected = true
                                            imageProxy.close()

                                            val (productName, quantity) = parseBarcodeToNameQuantity(rawValue)
                                            sendResultToActivity(productName, quantity)
                                            return@addOnSuccessListener
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    Log.e("BarcodeFragment", "분석 실패", it)
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("BarcodeFragment", "카메라 바인딩 실패", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun parseBarcodeToNameQuantity(rawValue: String): Pair<String, String> {
        val parts = rawValue.trim().split("\\s+".toRegex()) // 공백 기준으로 분리

        return if (parts.isNotEmpty()) {
            val last = parts.last()
            val quantity = last.toIntOrNull()

            if (quantity != null && parts.size > 1) {
                val name = parts.dropLast(1).joinToString(" ")
                Pair(name, quantity.toString())
            } else {
                // 수량이 없거나 마지막이 숫자가 아님
                val name = parts.joinToString(" ")
                Pair(name, "1")
            }
        } else {
            Pair("알 수 없음", "1")
        }
    }


    // ✅ Activity로 결과 전달
    private fun sendResultToActivity(name: String, quantity: String) {
        val activity = activity as? CameraActivity
        if (activity != null) {
            activity.onProductNameEntered("$name,$quantity") // 문자열로 전달하거나
            // 또는 필요 시 activity.onProductDetected(name, quantity) 식으로 따로 정의 가능
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}
