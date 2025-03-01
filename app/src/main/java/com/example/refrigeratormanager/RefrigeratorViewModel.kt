package com.example.refrigeratormanager


import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

// RefrigeratorViewModel.kt
class RefrigeratorViewModel : ViewModel() {
    private val _refrigeratorList = MutableLiveData<List<Refrigerator>>(emptyList())
    val refrigeratorList: LiveData<List<Refrigerator>> get() = _refrigeratorList

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun createRefrigerator(name: String, token: String) {

        val refrigeratorDTO = RefrigeratorDTO(name)

        val apiService = ApiClient.getClient().create(RefrigeratorApi::class.java)
        apiService.createRefrigerator(token, refrigeratorDTO).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    // 성공적으로 냉장고가 추가된 경우 UI 업데이트
                    val updatedList = _refrigeratorList.value.orEmpty() + Refrigerator(name)
                    _refrigeratorList.value = updatedList
                } else {
                    // 실패 시 error 메시지를 전달
                    _refrigeratorList.value = listOf() // 실패 처리 후 빈 리스트로 설정
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                _refrigeratorList.value = listOf() // 실패 처리 후 빈 리스트로 설정
            }
        })
    }
}

