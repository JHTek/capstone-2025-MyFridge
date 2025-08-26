package com.example.refrigeratormanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.refrigeratormanager.product.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AlarmWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "expiration_channel"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 설정 불러오기
           //SharedPreferences(settings)에서 사용자 설정 불러오기
            //alert_days: 유통기한 임박 알림 기준일 (기본 7일)
            //userid: 로그인된 사용자 ID
            val prefs = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val alertDays = prefs.getInt("alert_days", 7)
            val userId = prefs.getString("userid", "") ?: ""

            if (userId.isEmpty()) {
                Log.e("AlarmWorker", "User ID is empty") //사용자 ID가 비어있으면 실패 처리
                return@withContext Result.failure()
            }

            createNotificationChannel() //알림 채널 생성

            // Retrofit + suspend API
            //서버 IP는 BuildConfig.SERVER_IP 에서 가져옴
            //ExpirationApi 인터페이스를 구현한 객체 생성
            val retrofit = Retrofit.Builder()
                .baseUrl("http://${BuildConfig.SERVER_IP}:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(ExpirationApi::class.java)

            // suspend 함수 호출 (네트워크 요청) ,Retrofit suspend API 호출 ,특정 사용자(userId)의 유통기한 임박 제품 목록을 받아옴
            val productList = api.getExpiringProducts(userId, alertDays)

            if (productList.isEmpty()) {
                return@withContext Result.success() //서버 응답이 빈 리스트면 → 알림 필요 없음, 바로 성공 종료
            }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // 알림을 띄우기 위한 NotificationManager 가져오기
            // 현재 시간(now)과 포맷(formatter) 준비 (로그 저장용)
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

            //  제품별 알림 생성 , 서버에서 받은 각 제품 정보(Product)를 메시지 문자열로 구성
            for (product in productList) {
                val message = "냉장고 ${product.refrigeratorId}의 ${product.ingredientsName} (${product.quantity}개)\n" +
                        "유통기한: ${product.expirationDate} (${getStorageLocationName(product.storageLocation)})"

                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                } //알림 클릭 시 MainActivity 실행되도록 설정

                val pendingIntent = PendingIntent.getActivity(
                    applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("유통기한 임박")
                    .setContentText(message)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(NOTIFICATION_ID + product.hashCode(), notification)

                //  로그 저장
                //SharedPreferences(alert_log)에 로그를 누적 저장
                //나중에 NotificationActivity에서 보여줄 수 있음
                val log = "${now.format(formatter)} - ${product.ingredientsName} (${product.quantity}개), 유통기한 ${product.expirationDate}"
                val oldLog = prefs.getString("alert_log", "") ?: ""
                val newLog = "$oldLog\n$log".trim()
                prefs.edit().putString("alert_log", newLog).apply()
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("AlarmWorker", "Exception in worker", e)
            Result.failure()
        }
    }

    // 알림 채널 생성
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "유통기한 알림"
            val descriptionText = "유통기한 임박 제품에 대한 알림 채널입니다."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 저장 위치 코드 → 텍스트 변환 , 저장 위치 코드(0,1,2)를 사람이 읽을 수 있는 문자열로 변환
    private fun getStorageLocationName(code: Int): String {
        return when (code) {
            0 -> "냉장"
            1 -> "냉동"
            2 -> "실온"
            else -> "알 수 없음"
        }
    }
}
