package com.example.refrigeratormanager

import java.util.*

object ProductManager {
    private val productList = mutableListOf<Product>()


    // 제품 삭제
    fun removeProduct(refrigeratorName: String, product: Product) {
        // refrigeratorName을 통해 해당 냉장고에 속하는 제품을 찾고 삭제
        productList.remove(product)
    }

    // 냉장고의 저장 위치(냉장, 냉동, 실온)에 따라 제품을 반환
    fun getSortedProductsByExpiration(refrigeratorName: String?): List<List<Product>> {
        val refrigerated = mutableListOf<Product>()
        val frozen = mutableListOf<Product>()
        val roomTemp = mutableListOf<Product>()

        // 냉장고 이름에 맞는 제품을 카테고리별로 분류
        productList.forEach { product ->
            when (product.storageLocation) {
                0 -> refrigerated.add(product)  // 냉장
                1 -> frozen.add(product)        // 냉동
                2 -> roomTemp.add(product)      // 실온
            }
        }

        // 유통기한에 맞게 정렬
        refrigerated.sortBy { it.expirationDate }
        frozen.sortBy { it.expirationDate }
        roomTemp.sortBy { it.expirationDate }

        return listOf(refrigerated, frozen, roomTemp)
    }
}

