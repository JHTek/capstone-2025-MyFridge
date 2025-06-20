package com.example.refrigeratormanager.recipe

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.refrigeratormanager.ApiClient
import com.example.refrigeratormanager.R
import com.example.refrigeratormanager.databinding.FragmentRecipeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecipeFragment : Fragment() {

    private lateinit var binding: FragmentRecipeBinding
    val recipeApi = ApiClient.getRecipeApi()

    // JWT 토큰을 SharedPreferences에서 가져오기
    private fun getToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 키보드에서 검색 버튼 눌렀을 때 처리
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

        // 돋보기 버튼 클릭 처리
        binding.imageSearchIcon.setOnClickListener {
            val keyword = binding.editTextSearch.text.toString().trim()
            if (keyword.isNotEmpty()) {
                searchRecipesFromServer(keyword)
                hideKeyboard()
            }
        }

        binding.imageSearchIcon.setOnClickListener {
            val keyword = binding.editTextSearch.text.toString().trim()
            if (keyword.isNotEmpty()) {
                searchRecipesFromServer(keyword)
            } else {
                Toast.makeText(requireContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 🍱 예시 데이터 (서버에서 받아온다고 가정)
        val data = listOf(
            IngredientSection(
                "양파", listOf(
                    Recipe(
                        recipe_name = "중화풍 마파두부 덮밥",
                        thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2015/05/18/48091bec0fcebd49cd4b979735068298.jpg",
                        id = "1",
                        url = "1",
                        ingredients = listOf(
                            Ingredient("두부"),
                            Ingredient("다진 돼지고기"),
                            Ingredient("양파"),
                            Ingredient("고추기름")
                        )

                    ),
                    Recipe(
                        recipe_name = "떡볶이", thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2018/01/15/593e123714a3af6752388583567427cb1_m.jpg",
                        id = "1",
                        url = "1",
                        ingredients = listOf(
                            Ingredient("두부"),
                            Ingredient("다진 돼지고기"),
                            Ingredient("양파"),
                            Ingredient("고추기름")
                        )
                    )
                )
            ),
            IngredientSection(
                "시금치", listOf(
                    Recipe(
                        recipe_name = "두부시금치무침",
                        thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2016/12/08/34ba5bf522b2964e97b70dcd5b62dcb31_m.jpg",
                        id ="1",
                        url = "1",
                        ingredients = listOf(
                            Ingredient("두부"),
                            Ingredient("다진 돼지고기"),
                            Ingredient("양파"),
                            Ingredient("고추기름")
                        )
                    ),
                    Recipe(
                        recipe_name = "건새우 시금치 된장국",
                        thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2015/10/20/895c415659fff90e1f493ab1d86357731_m.jpg",
                        id = "1",
                        url = "1",
                        ingredients = listOf(
                            Ingredient("두부"),
                            Ingredient("다진 돼지고기"),
                            Ingredient("양파"),
                            Ingredient("고추기름")
                        )
                    )
                )
            )
        )

        bindSection1(data[0]) // 양파 섹션
        bindSection2(data[1]) // 시금치 섹션
    }

    private fun bindSection1(section: IngredientSection) {
        binding.textOnionTitle.text = "${section.ingredientName}를 사용하는 추천 요리"
        binding.textOnionRecipe1.text = section.recipes[0].recipe_name
        binding.textOnionRecipe2.text = section.recipes[1].recipe_name

        Glide.with(this)
            .load(section.recipes[0].thumbnail)
            .into(binding.imageOnionRecipe1)

        Glide.with(this)
            .load(section.recipes[1].thumbnail)
            .into(binding.imageOnionRecipe2)

        binding.textOnionMore.setOnClickListener {
            Toast.makeText(requireContext(), "${section.ingredientName} 더보기", Toast.LENGTH_SHORT).show()
        }

        binding.imageOnionRecipe1.setOnClickListener {
            Toast.makeText(requireContext(), "${section.recipes[0].recipe_name} 클릭됨", Toast.LENGTH_SHORT).show()
        }

        binding.imageOnionRecipe2.setOnClickListener {
            Toast.makeText(requireContext(), "${section.recipes[1].recipe_name} 클릭됨", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindSection2(section: IngredientSection) {
        binding.textSpinachTitle.text = "${section.ingredientName}를 사용하는 추천 요리"
        binding.textSpinachRecipe1.text = section.recipes[0].recipe_name
        binding.textSpinachRecipe2.text = section.recipes[1].recipe_name

        Glide.with(this)
            .load(section.recipes[0].thumbnail)
            .into(binding.imageSpinachRecipe1)

        Glide.with(this)
            .load(section.recipes[1].thumbnail)
            .into(binding.imageSpinachRecipe2)

        binding.textSpinachMore.setOnClickListener {
            Toast.makeText(requireContext(), "${section.ingredientName} 더보기", Toast.LENGTH_SHORT).show()
        }

        binding.imageSpinachRecipe1.setOnClickListener {
            Toast.makeText(requireContext(), "${section.recipes[0].recipe_name} 클릭됨", Toast.LENGTH_SHORT).show()
        }

        binding.imageSpinachRecipe2.setOnClickListener {
            Toast.makeText(requireContext(), "${section.recipes[1].recipe_name} 클릭됨", Toast.LENGTH_SHORT).show()
        }
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

    //검색 요청 함수
    private fun searchRecipesFromServer(keyword: String) {
        moveToSearchResultFragment(keyword)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
    }

}
