package com.example.refrigeratormanager

import java.util.*

object ProductManager {
    private val productList = mutableListOf<Product>()
    private val productMap = mutableMapOf<Int, List<Product>>() // key = refrigeratorId

    fun updateProducts(refrigeratorId: Int, products: List<Product>) {
        productMap[refrigeratorId] = products
    }

    // 제품 삭제
    fun removeProduct(refrigeratorId: Int, product: Product) {
        val originalList = productMap[refrigeratorId] ?: return
        productMap[refrigeratorId] = originalList.filterNot { it == product }
    }
//    fun removeProduct(refrigeratorName: String, product: Product) {
//        // refrigeratorName을 통해 해당 냉장고에 속하는 제품을 찾고 삭제
//        productList.remove(product)
//    }

    // 냉장고의 저장 위치(냉장, 냉동, 실온)에 따라 제품을 반환
    fun getSortedProductsByExpiration(refrigeratorId: Int): List<List<Product>> {
        val all = productMap[refrigeratorId] ?: return listOf(emptyList(), emptyList(), emptyList())

        val refrigerated = mutableListOf<Product>()
        val frozen = mutableListOf<Product>()
        val roomTemp = mutableListOf<Product>()

        all.forEach { product ->
            when (product.storageLocation) {
                0 -> refrigerated.add(product)
                1 -> frozen.add(product)
                2 -> roomTemp.add(product)
            }
        }

        return listOf(
            refrigerated.sortedBy { it.expirationDate },
            frozen.sortedBy { it.expirationDate },
            roomTemp.sortedBy { it.expirationDate }
        )
    }
//    fun getSortedProductsByExpiration(refrigeratorName: String?): List<List<Product>> {
//        val refrigerated = mutableListOf<Product>()
//        val frozen = mutableListOf<Product>()
//        val roomTemp = mutableListOf<Product>()
//
//        // 냉장고 이름에 맞는 제품을 카테고리별로 분류
//        productList.forEach { product ->
//            when (product.storageLocation) {
//                0 -> refrigerated.add(product)  // 냉장
//                1 -> frozen.add(product)        // 냉동
//                2 -> roomTemp.add(product)      // 실온
//            }
//        }
//
//        // 유통기한에 맞게 정렬
//        refrigerated.sortBy { it.expirationDate }
//        frozen.sortBy { it.expirationDate }
//        roomTemp.sortBy { it.expirationDate }
//
//        return listOf(refrigerated, frozen, roomTemp)
//    }
}

