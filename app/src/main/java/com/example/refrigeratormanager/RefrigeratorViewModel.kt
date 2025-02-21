package com.example.refrigeratormanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RefrigeratorViewModel : ViewModel() {
    private val _refrigeratorList = MutableLiveData<List<Refrigerator>>(emptyList())
    val refrigeratorList: LiveData<List<Refrigerator>> get() = _refrigeratorList

    fun addRefrigerator(name: String) {
        if (name.isNotEmpty() && (_refrigeratorList.value?.none { it.name == name } == true)) {
            val updatedList = _refrigeratorList.value.orEmpty() + Refrigerator(name)
            _refrigeratorList.value = updatedList // ✅ LiveData 업데이트
        }
    }
}
