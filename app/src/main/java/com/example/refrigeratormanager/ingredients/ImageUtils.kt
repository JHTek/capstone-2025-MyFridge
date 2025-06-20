package com.example.refrigeratormanager.ingredients

import com.example.refrigeratormanager.R

object ImageUtils {
    fun getImageResourceForCategory(category: String): Int {
        return when (category) {
            "곡류" -> R.drawable.ic_grains
            "서류" -> R.drawable.ic_roots
            "두류" -> R.drawable.ic_beans
            "견과류" -> R.drawable.ic_nuts
            "채소류" -> R.drawable.ic_vegetables
            "과일류" -> R.drawable.ic_fruits
            "버섯류" -> R.drawable.ic_mushrooms
            "해조류" -> R.drawable.ic_seaweed
            "육류" -> R.drawable.ic_meat
            "어패류" -> R.drawable.ic_fish
            "알류" -> R.drawable.ic_eggs
            "유제품" -> R.drawable.ic_dairy
            "가공식품" -> R.drawable.ic_processed
            "조미료" -> R.drawable.ic_seasoning
            "기타" -> R.drawable.ic_etc
            else -> R.drawable.ic_etc
        }
    }
}
