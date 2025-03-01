package com.example.refrigeratormanager


import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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

    fun loadRefrigerators(token: String) {
        viewModelScope.launch {
            _loading.value = true // 로딩 시작
            try {
                val response = ApiClient.getRefrigeratorApi().getRefrigerators(token)
                if (response.isSuccessful) {
                    Log.d("RefrigeratorViewModel", "성공: ${response.body()}")
                    _refrigeratorList.value = response.body() // 냉장고 목록 업데이트
                } else {
                    _errorMessage.value = "냉장고 목록 불러오기 실패: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "네트워크 오류: ${e.message}"
            } finally {
                _loading.value = false // 로딩 종료
            }
        }
    }

    fun createRefrigerator(name: String, token: String) {

        val refrigeratorDTO = RefrigeratorDTO(name)

        val apiService = ApiClient.getClient().create(RefrigeratorApi::class.java)
        apiService.createRefrigerator(token, refrigeratorDTO).enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    // 냉장고 추가 성공 시 목록 다시 불러오기
                    loadRefrigerators(token)

                } else {
                    _errorMessage.value = "냉장고 생성 실패: ${response.message()}"
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                _errorMessage.value = "네트워크 오류: ${t.message}"
            }
        })
    }

}

