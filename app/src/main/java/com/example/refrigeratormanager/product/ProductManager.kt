package com.example.refrigeratormanager.product

object ProductManager {
    val productList = mutableListOf<Product>()
    private val productMap = mutableMapOf<Int, List<Product>>() // key = refrigeratorId

    fun updateProducts(refrigeratorId: Int, products: List<Product>) {
        productMap[refrigeratorId] = products
    }

    // 제품 삭제
    fun removeProduct(refrigeratorId: Int, product: Product) {
        val originalList = productMap[refrigeratorId] ?: return
        productMap[refrigeratorId] = originalList.filterNot { it == product }
    }

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

    // 🔥 추가된 부분
    fun getAllProducts(): List<Product> {
        return productMap.values.flatten()
    }
}

