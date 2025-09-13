package com.example.refrigeratormanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.refrigeratormanager.databinding.FragmentExpirationListBinding
import com.example.refrigeratormanager.product.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ExpirationListFragment : Fragment() {

    private var _binding: FragmentExpirationListBinding? = null
    private val binding get() = _binding!!
    private val adapter = ExpirationAdapter(emptyList())

    private val sharedViewModel: ExpirationViewModel by activityViewModels()
    private val alertDays = 7

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpirationListBinding.inflate(inflater, container, false)

        // RecyclerView 설정
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.clipToPadding = false

        // WindowInsets 적용
        binding.expirationFragmentLayout.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(
                view.paddingLeft,
                insets.systemWindowInsetTop,   // 상단 상태바 공간 확보
                view.paddingRight,
                insets.systemWindowInsetBottom // 하단 내비게이션 바 공간 확보
            )
            insets.consumeSystemWindowInsets()
        }

        // ViewModel observe
        sharedViewModel.expiringProducts.observe(viewLifecycleOwner) { products ->
            if (products.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmpty.visibility = View.GONE
            }
            adapter.updateList(products.sortedBy { it.expirationDate }) // 유통기한 임박 3개만 표시
        }

        // 서버에서 데이터 가져오기
        fetchExpiringProducts()

        return binding.root
    }

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

                android.util.Log.d("ExpirationFragment", "API response size=${products.size}")

                sharedViewModel.setExpiringProducts(products)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "서버에서 데이터 가져오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserToken(): String {
        val prefs = requireContext().getSharedPreferences("app_preferences", 0)
        return prefs.getString("JWT_TOKEN", "") ?: ""
    }

    private fun getUserId(): String {
        val prefs = requireContext().getSharedPreferences("app_preferences", 0)
        return prefs.getString("USER_ID", "") ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
