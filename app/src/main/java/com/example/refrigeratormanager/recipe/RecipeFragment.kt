package com.example.refrigeratormanager.recipe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.refrigeratormanager.ApiClient
import com.example.refrigeratormanager.R
import com.example.refrigeratormanager.databinding.FragmentRecipeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeFragment : Fragment() {

    private lateinit var binding: FragmentRecipeBinding
    private val recipeApi = ApiClient.getRecipeApi()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearch()
        loadRecommendedRecipes() // 서버 연동으로 교체됨
    }

    private fun setupSearch() {
        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword = binding.editTextSearch.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    searchRecipesFromServer(keyword)
                    hideKeyboard()
                }
                true
            } else {
                false
            }
        }

        binding.imageSearchIcon.setOnClickListener {
            val keyword = binding.editTextSearch.text.toString().trim()
            if (keyword.isNotEmpty()) {
                searchRecipesFromServer(keyword)
                hideKeyboard()
            } else {
                Toast.makeText(requireContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadRecommendedRecipes() {
        val sharedPref = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val token = sharedPref.getString("JWT_TOKEN", null)


        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
            return
        }

        recipeApi.getRecommendedRecipes("Bearer $token").enqueue(object : Callback<List<IngredientSection>> {
            override fun onResponse(
                call: Call<List<IngredientSection>>,
                response: Response<List<IngredientSection>>
            ) {
                if (response.isSuccessful) {
                    val sections = response.body() ?: emptyList()
                    bindSectionsDynamically(sections)
                } else {
                    Toast.makeText(requireContext(), "레시피 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<IngredientSection>>, t: Throwable) {
                Toast.makeText(requireContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun bindSectionsDynamically(sections: List<IngredientSection>) {
        val layouts = listOf(
            binding.section1 to Pair(binding.textSection1Title, listOf(
                Triple(binding.imageSection1Recipe1, binding.textSection1Recipe1, binding.layoutSection1Recipe1),
                Triple(binding.imageSection1Recipe2, binding.textSection1Recipe2, binding.layoutSection1Recipe2)
            )),
            binding.section2 to Pair(binding.textSection2Title, listOf(
                Triple(binding.imageSection2Recipe1, binding.textSection2Recipe1, binding.layoutSection2Recipe1),
                Triple(binding.imageSection2Recipe2, binding.textSection2Recipe2, binding.layoutSection2Recipe2)
            )),
            binding.section3 to Pair(binding.textSection3Title, listOf(
                Triple(binding.imageSection3Recipe1, binding.textSection3Recipe1, binding.layoutSection3Recipe1),
                Triple(binding.imageSection3Recipe2, binding.textSection3Recipe2, binding.layoutSection3Recipe2)
            )),
            binding.section4 to Pair(binding.textSection4Title, listOf(
                Triple(binding.imageSection4Recipe1, binding.textSection4Recipe1, binding.layoutSection4Recipe1),
                Triple(binding.imageSection4Recipe2, binding.textSection4Recipe2, binding.layoutSection4Recipe2)
            )),
            binding.section5 to Pair(binding.textSection5Title, listOf(
                Triple(binding.imageSection5Recipe1, binding.textSection5Recipe1, binding.layoutSection5Recipe1),
                Triple(binding.imageSection5Recipe2, binding.textSection5Recipe2, binding.layoutSection5Recipe2)
            ))
        )

        for (i in sections.indices) {
            val section = sections[i]

            // ⭐ 방어 코드: null 체크와 최소 2개 레시피 조건 확인
            if (section.recipes == null || section.recipes.size < 2) {
                continue
            }

            val (sectionLayout, pair) = layouts[i]
            val (titleView, recipeViews) = pair

            sectionLayout.visibility = View.VISIBLE
            titleView.text = "${section.ingredientName}를 사용하는 추천 요리"

            for (j in 0..1) {
                val recipe = section.recipes[j]
                val (imageView, textView, layoutView) = recipeViews[j]

                textView.text = recipe.recipeName
                Glide.with(this).load(recipe.thumbnail).into(imageView)

                layoutView.setOnClickListener {
                    val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
                    intent.putExtra("recipe", recipe)
                    startActivity(intent)
                }
            }
        }
    }


    private fun searchRecipesFromServer(keyword: String) {
        moveToSearchResultFragment(keyword)
    }

    private fun moveToSearchResultFragment(keyword: String) {
        val fragment = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putString("keyword", keyword)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
    }
}
