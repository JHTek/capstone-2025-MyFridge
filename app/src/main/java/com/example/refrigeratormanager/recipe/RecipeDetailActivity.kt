package com.example.refrigeratormanager.recipe

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.refrigeratormanager.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val recipe = intent.getParcelableExtra<Recipe>("recipe")
        if (recipe == null) {
            finish()
            return
        }

        binding.collapsingToolbar.title = recipe.recipeName

        Glide.with(this)
            .load(recipe.thumbnail)
            .into(binding.imageRecipe)

        binding.textRecipeTitle.text = recipe.recipeName

        val ingredientsText = recipe.ingredients.joinToString("\n") {
            "• ${it.name} ${it.count}${it.unit}".trim()
        }
        binding.textIngredients.text = ingredientsText

        // 🔹 설명 데이터 유무에 따른 처리
        if (recipe.instructions.isEmpty()) {
            binding.textInstructionsTitle.visibility = View.VISIBLE  // "설명 없음" 보이기
            binding.recyclerViewInstructions.visibility = View.GONE
        } else {
            binding.textInstructionsTitle.visibility = View.GONE     // "설명 없음" 숨기기
            binding.recyclerViewInstructions.visibility = View.VISIBLE

            val adapter = InstructionAdapter(recipe.instructions)
            binding.recyclerViewInstructions.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewInstructions.adapter = adapter
        }
    }
}
