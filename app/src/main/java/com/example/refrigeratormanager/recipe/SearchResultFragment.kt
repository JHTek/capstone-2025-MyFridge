package com.example.refrigeratormanager.recipe
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.refrigeratormanager.ApiClient
import com.example.refrigeratormanager.databinding.FragmentSearchResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchResultAdapter
    private val recipeList = mutableListOf<Recipe>()

    private val recipeApi = ApiClient.getRecipeApi()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ RecyclerView 설정 (유지)
        adapter = SearchResultAdapter(recipeList) { recipe ->
            if (recipe.url.isNullOrBlank()) {
                Toast.makeText(requireContext(), "레시피 링크가 없습니다", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.url))
                startActivity(intent)
            }
        }
        binding.recyclerViewSearchResults.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewSearchResults.adapter = adapter

        // ✅ MainHomeFragment로부터 keyword를 받았는지 확인하고, 바로 검색 실행
        val keyword = arguments?.getString("keyword") ?: ""
        if (keyword.isNotBlank()) {
            binding.editTextSearch.setText(keyword)           // 검색창에 자동 입력
            searchRecipes(keyword)                            // 🔍 검색 실행
        }

        // ✅ 검색창 우측 돋보기 클릭 시 동작
        binding.imageSearchIcon.setOnClickListener {
            val keywordText = binding.editTextSearch.text.toString().trim()
            if (keywordText.isNotEmpty()) {
                searchRecipes(keywordText)
                hideKeyboard()
            }
        }

        // ✅ 키보드 '검색' 버튼 눌렀을 때 동작
        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keywordText = binding.editTextSearch.text.toString().trim()
                if (keywordText.isNotEmpty()) {
                    searchRecipes(keywordText)
                    hideKeyboard()
                }
                true
            } else {
                false
            }
        }
    }

    private fun searchRecipes(keyword: String) {
        val token = getToken()
        if (token == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
            return
        }

        val authHeader = "Bearer $token"

        recipeApi.searchRecipes(authHeader, keyword).enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    val results = response.body() ?: emptyList()
                    recipeList.clear()
                    recipeList.addAll(results)
                    adapter.notifyDataSetChanged()

                    binding.textSearchResultTitle.text = if (results.isEmpty()) "검색 결과 없음" else "검색 결과"
                } else {
                    Toast.makeText(requireContext(), "서버 오류 (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                Toast.makeText(requireContext(), "연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getToken(): String? {
        val prefs = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return prefs.getString("JWT_TOKEN", null)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
