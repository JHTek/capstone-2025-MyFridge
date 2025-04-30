package com.example.refrigeratormanager.recipe

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.refrigeratormanager.ApiClient
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
                        name = "중화풍 마파두부 덮밥",
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
                        name = "떡볶이", thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2018/01/15/593e123714a3af6752388583567427cb1_m.jpg",
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
                        name = "두부시금치무침",
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
                        name = "건새우 시금치 된장국",
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
        binding.textOnionRecipe1.text = section.recipes[0].name
        binding.textOnionRecipe2.text = section.recipes[1].name

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
            Toast.makeText(requireContext(), "${section.recipes[0].name} 클릭됨", Toast.LENGTH_SHORT).show()
        }

        binding.imageOnionRecipe2.setOnClickListener {
            Toast.makeText(requireContext(), "${section.recipes[1].name} 클릭됨", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindSection2(section: IngredientSection) {
        binding.textSpinachTitle.text = "${section.ingredientName}를 사용하는 추천 요리"
        binding.textSpinachRecipe1.text = section.recipes[0].name
        binding.textSpinachRecipe2.text = section.recipes[1].name

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
            Toast.makeText(requireContext(), "${section.recipes[0].name} 클릭됨", Toast.LENGTH_SHORT).show()
        }

        binding.imageSpinachRecipe2.setOnClickListener {
            Toast.makeText(requireContext(), "${section.recipes[1].name} 클릭됨", Toast.LENGTH_SHORT).show()
        }
    }

    //검색 요청 함수
    private fun searchRecipesFromServer(keyword: String) {
        val token = getToken()
        Log.d("RecipeSearch", "토큰: $token")
        if (token == null) {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        val authHeader = "Bearer $token"
        recipeApi.searchRecipes(authHeader, keyword).enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                Log.d("RecipeSearch", "응답 코드: ${response.code()}")
                Log.d("RecipeSearch", "응답 메시지: ${response.message()}")
                Log.d("RecipeSearch", "요청 URL: ${call.request().url}")
                Log.d("RecipeSearch", "요청 헤더: ${call.request().headers}")

                if (response.isSuccessful) {
                    val recipes = response.body() ?: emptyList()
                    displaySearchResults(recipes)
                } else {
                    Toast.makeText(requireContext(), "서버 오류 (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                Log.e("RecipeSearch", "연결 실패", t) // ✅ 전체 스택트레이스 출력
                Toast.makeText(requireContext(), "연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    //응답을 화면에 반영
    private fun displaySearchResults(recipes: List<Recipe>) {
        if (recipes.isEmpty()) {
            binding.textOnionTitle.text = "검색 결과 없음"
            binding.textOnionRecipe1.text = ""
            binding.textOnionRecipe2.text = ""
            binding.imageOnionRecipe1.setImageDrawable(null)
            binding.imageOnionRecipe2.setImageDrawable(null)
            return
        }

        binding.textOnionTitle.text = "검색 결과"
        binding.textOnionRecipe1.text = recipes.getOrNull(0)?.name ?: ""
        binding.textOnionRecipe2.text = recipes.getOrNull(1)?.name ?: ""

        Glide.with(this).load(recipes.getOrNull(0)?.thumbnail).into(binding.imageOnionRecipe1)
        Glide.with(this).load(recipes.getOrNull(1)?.thumbnail).into(binding.imageOnionRecipe2)
    }
}
