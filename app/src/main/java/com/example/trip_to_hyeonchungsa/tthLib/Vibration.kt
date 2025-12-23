package com.example.trip_to_hyeonchungsa.tthLib

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Composable에서 바로 사용 가능한 진동 함수
 * @param intensity 진동 강도 (1~10)
 */
@Composable
fun Vibration(intensity: Int) {
    val context = LocalContext.current
    context.vibrate(intensity)
}

/**
 * Context 확장 함수로 진동을 발생시키는 함수
 * @param intensity 진동 강도 (1~10)
 */
fun Context.vibrate(intensity: Int) {
    // 진동 강도를 1~10 범위로 제한
    val validIntensity = intensity.coerceIn(1, 10)
    
    // 진동 시간 계산 (100ms ~ 1000ms)
    val duration = validIntensity * 100L
    
    // 진동 강도 계산 (1 ~ 255)
    val amplitude = (validIntensity * 25.5).toInt().coerceIn(1, 255)
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12 이상
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibratorManager.defaultVibrator
        val effect = VibrationEffect.createOneShot(duration, amplitude)
        vibrator.vibrate(effect)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Android 8.0 이상
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val effect = VibrationEffect.createOneShot(duration, amplitude)
        vibrator.vibrate(effect)
    } else {
        // Android 8.0 미만
        @Suppress("DEPRECATION")
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(duration)
    }
}
