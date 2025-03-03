package com.example.refrigeratormanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.FragmentBarcodeBinding

class BarcodeFragment : Fragment() {
    private lateinit var binding: FragmentBarcodeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBarcodeBinding.inflate(inflater, container, false)

        // 카메라 미리보기만 표시
        val surfaceView: SurfaceView = binding.cameraPreview
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // 카메라 미리보기 로직
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })
        return binding.root
    }
}
