package com.example.trip_to_hyeonchungsa

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 목적지 도착 여부를 확인하는 함수
 * 현재 위치를 자동으로 측정하여 목적지와 비교합니다.
 * 
 * 다른 파일에서 사용 예시:
 * ```
 * import com.example.arrival.Arrival
 * 
 * val result = Arrival(context, 37.5665, 126.9780)
 * ```
 * 
 * @param context Android Context (위치 서비스 접근에 필요)
 * @param targetLatitude 목적지 위도
 * @param targetLongitude 목적지 경도
 * @param thresholdMeters 도착으로 판단할 거리 임계값 (미터 단위, 기본값: 10m)
 * @return 목적지에 도착했으면 true, 아니면 false, 위치를 가져올 수 없으면 null
 */
suspend fun Arrival(
    context: Context,
    targetLatitude: Double,
    targetLongitude: Double,
    thresholdMeters: Double = 10.0
): Boolean? {
    // 위치 권한 확인
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return null // 권한 없음
    }
    
    // 현재 위치 가져오기
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    
    return try {
        val location = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).await()
        
        if (location != null) {
            // 거리 계산 (Haversine 공식)
            val earthRadiusKm = 6371.0
            val currentLat = location.latitude
            val currentLon = location.longitude
            
            val dLat = Math.toRadians(targetLatitude - currentLat)
            val dLon = Math.toRadians(targetLongitude - currentLon)
            
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(currentLat)) * cos(Math.toRadians(targetLatitude)) *
                    sin(dLon / 2) * sin(dLon / 2)
            
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val distance = earthRadiusKm * c * 1000 // 미터 단위로 변환
            
            distance <= thresholdMeters
        } else {
            null // 위치를 가져올 수 없음
        }
    } catch (e: Exception) {
        null // 오류 발생
    }
}