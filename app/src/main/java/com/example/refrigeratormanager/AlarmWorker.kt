package com.example.refrigeratormanager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.refrigeratormanager.product.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class AlarmWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "expiration_alert_channel"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val prefs =
                applicationContext.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val alertDays = prefs.getInt("ALERT_DAYS", 7)
            val userId = prefs.getString("USER_ID", null)
            val token = prefs.getString("JWT_TOKEN", null)

            if (userId.isNullOrBlank()) return@withContext Result.failure()

            createNotificationChannel()

            // Retrofit + OkHttp 설정
            val httpLogging = HttpLoggingInterceptor { message -> println(message) }
            httpLogging.level = HttpLoggingInterceptor.Level.BASIC

            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(httpLogging)

            if (!token.isNullOrBlank()) {
                clientBuilder.addInterceptor { chain ->
                    val req = chain.request().newBuilder()
                    req.addHeader("Authorization", "Bearer $token")
                    chain.proceed(req.build())
                }
            }

            val client = clientBuilder.build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://${BuildConfig.SERVER_IP}:8080/") // 서버 IP 수정
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(ExpirationApi::class.java)

            val productList: List<Product> = try {
                api.getExpiringProducts(userId, alertDays)
            } catch (e: Exception) {
                return@withContext Result.retry()
            }

            if (productList.isEmpty()) return@withContext Result.success()

            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

            for (product in productList) {
                sendNotification(product)

                val oldLog = prefs.getString("alert_log", "") ?: ""
                val newLog =
                    "$oldLog\n${now.format(formatter)} - ${product.refrigeratorName} / ${product.ingredientsName} (${product.quantity}개), 유통기한 ${product.expirationDate}".trim()
                prefs.edit().putString("alert_log", newLog).apply()
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel =
                NotificationChannel(CHANNEL_ID, "유통기한 알림", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "유통기한 임박 제품 알림"
            manager.createNotificationChannel(channel)
        }
    }

    private fun getStorageLocationName(code: Int) = when (code) {
        0 -> "냉장"
        1 -> "냉동"
        2 -> "실온"
        else -> "알 수 없음"
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(product: Product) {
        val context = applicationContext

        // PendingIntent에서 flags 수정: NEW_TASK / CLEAR_TASK 제거
        val intent = Intent(context, NotificationActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            product.ingredientsId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = """
        냉장고: ${product.refrigeratorName}
        재료: ${product.ingredientsName} (${product.quantity}개)
        위치: ${getStorageLocationName(product.storageLocation)}
        유통기한: ${product.expirationDate}
    """.trimIndent()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("유통기한 임박 알림")
            .setContentText("${product.ingredientsName} (${product.quantity}개) - ${product.refrigeratorName}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // 클릭 시 NotificationActivity 실행
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(
            (product.refrigeratorId * 100000) + product.ingredientsId,
            builder.build()
        )
    }
}