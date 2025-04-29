package com.example.refrigeratormanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.refrigeratormanager.databinding.FragmentRecipeBinding

class RecipeFragment : Fragment() {

    private lateinit var binding: FragmentRecipeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🍱 예시 데이터 (서버에서 받아온다고 가정)
        val data = listOf(
            IngredientSection(
                "양파", listOf(
                    Recipe(
                        name = "중화풍 마파두부 덮밥",
                        thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2015/05/18/48091bec0fcebd49cd4b979735068298.jpg",
                        id = "1",
                        url = "1",
                        ingredients = listOf("두부", "다진 돼지고기", "양파", "고추기름"),
                    ),
                    Recipe(
                        name = "떡볶이", thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2018/01/15/593e123714a3af6752388583567427cb1_m.jpg",
                        id = "1",
                        url = "1",
                        ingredients = listOf("두부", "다진 돼지고기", "양파", "고추기름")
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
                        ingredients = listOf("두부", "다진 돼지고기", "양파", "고추기름")
                    ),
                    Recipe(
                        name = "건새우 시금치 된장국",
                        thumbnail = "https://recipe1.ezmember.co.kr/cache/recipe/2015/10/20/895c415659fff90e1f493ab1d86357731_m.jpg",
                        id = "1",
                        url = "1",
                        ingredients = listOf("두부", "다진 돼지고기", "양파", "고추기름")
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
}
