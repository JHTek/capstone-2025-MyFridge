package com.example.refrigeratormanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.refrigeratormanager.product.Product

class ExpirationViewModel : ViewModel() {
    private val _expiringProducts = MutableLiveData<List<Product>>(emptyList())
    val expiringProducts: LiveData<List<Product>> get() = _expiringProducts

    fun setExpiringProducts(products: List<Product>) {
        _expiringProducts.postValue(products)
    }
}
