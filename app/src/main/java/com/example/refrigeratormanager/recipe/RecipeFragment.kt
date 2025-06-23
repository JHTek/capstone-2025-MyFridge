package com.example.refrigeratormanager.recipe

import android.content.Context
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
        loadSampleData() // TODO: 실제 서버 연동으로 대체 예정
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

    private fun loadSampleData() {
        val sampleData = listOf(
            IngredientSection(
                ingredientName = "양파",
                recipes = listOf(
                    Recipe(
                        recipeName = "중화풍 마파두부 덮밥",
                        thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2015/05/18/48091bec0fcebd49cd4b979735068298.jpg",
                        id = "1",
                        cookTime = "15분",
                        ingredients = listOf(
                            Ingredient("두부"),
                            Ingredient("다진 돼지고기"),
                            Ingredient("양파"),
                            Ingredient("고추기름")
                        )
                    ),
                    Recipe(
                        recipeName = "떡볶이",
                        thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2018/01/15/593e123714a3af6752388583567427cb1_m.jpg",
                        id = "2",
                        cookTime = "20분",
                        ingredients = listOf(
                            Ingredient("떡"),
                            Ingredient("고추장"),
                            Ingredient("양파"),
                            Ingredient("어묵")
                        )
                    )
                )
            ),
            IngredientSection(
                ingredientName = "시금치",
                recipes = listOf(
                    Recipe(
                        recipeName = "두부시금치무침",
                        thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2016/12/08/34ba5bf522b2964e97b70dcd5b62dcb31_m.jpg",
                        id = "3",
                        cookTime = "10분",
                        ingredients = listOf(
                            Ingredient("두부"),
                            Ingredient("시금치"),
                            Ingredient("참기름"),
                            Ingredient("소금")
                        )
                    ),
                    Recipe(
                        recipeName = "건새우 시금치 된장국",
                        thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2015/10/20/895c415659fff90e1f493ab1d86357731_m.jpg",
                        id = "4",
                        cookTime = "15분",
                        ingredients = listOf(
                            Ingredient("건새우"),
                            Ingredient("시금치"),
                            Ingredient("된장"),
                            Ingredient("다시마")
                        )
                    )
                )
            )
        )

        bindSection(sampleData[0], isOnion = true)
        bindSection(sampleData[1], isOnion = false)
    }

    private fun bindSection(section: IngredientSection, isOnion: Boolean) {
        if (isOnion) {
            binding.textOnionTitle.text = "${section.ingredientName}를 사용하는 추천 요리"
            binding.textOnionRecipe1.text = section.recipes[0].recipeName
            binding.textOnionRecipe2.text = section.recipes[1].recipeName

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
                Toast.makeText(requireContext(), "${section.recipes[0].recipeName} 클릭됨", Toast.LENGTH_SHORT).show()
            }

            binding.imageOnionRecipe2.setOnClickListener {
                Toast.makeText(requireContext(), "${section.recipes[1].recipeName} 클릭됨", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.textSpinachTitle.text = "${section.ingredientName}를 사용하는 추천 요리"
            binding.textSpinachRecipe1.text = section.recipes[0].recipeName
            binding.textSpinachRecipe2.text = section.recipes[1].recipeName

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
                Toast.makeText(requireContext(), "${section.recipes[0].recipeName} 클릭됨", Toast.LENGTH_SHORT).show()
            }

            binding.imageSpinachRecipe2.setOnClickListener {
                Toast.makeText(requireContext(), "${section.recipes[1].recipeName} 클릭됨", Toast.LENGTH_SHORT).show()
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
