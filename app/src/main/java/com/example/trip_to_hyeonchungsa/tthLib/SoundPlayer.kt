package com.example.myapplication

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val TAG = "SoundPlayer"

/**
 * SoundPlayer - 컴포저블이 활성화된 동안만 오디오를 재생합니다.
 *
 * @param fileName res/raw 폴더에 있는 오디오 파일명 (확장자 제외)
 * @param volume 재생 볼륨 (1-100)
 * @param loop 무한 반복 여부 (true/false)
 * @param content 이 블록이 실행되는 동안만 노래가 재생됩니다
 *
 * 사용 예시:
 * ```
 * SoundPlayer("background_music", 80, true) {
 *     Text("음악이 재생되는 화면")
 * }
 * ```
 */
@Composable
fun SoundPlayer(
    fileName: String,
    volume: Int,
    loop: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // MediaPlayer 인스턴스 생성
    val mediaPlayer = remember(fileName) {
        try {
            // 파일명 검증 (보안)
            if (fileName.isBlank() || !fileName.matches(Regex("^[a-zA-Z0-9_]+$"))) {
                Log.e(TAG, "Invalid file name: $fileName")
                return@remember null
            }
            
            val resourceId = context.resources.getIdentifier(fileName, "raw", context.packageName)
            if (resourceId != 0) {
                MediaPlayer.create(context, resourceId)
            } else {
                Log.e(TAG, "Resource not found: $fileName")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create MediaPlayer for $fileName", e)
            null
        }
    }

    // MediaPlayer 리소스 자동 해제 (컴포저블이 사라질 때)
    DisposableEffect(mediaPlayer) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    // 재생 제어 (이 블록이 활성화된 동안만 재생)
    LaunchedEffect(mediaPlayer, volume, loop) {
        mediaPlayer?.let { player ->
            try {
                // 볼륨 설정 (1-100 범위)
                val scaledVolume = volume.coerceIn(1, 100) / 100f
                player.setVolume(scaledVolume, scaledVolume)
                
                // 무한 반복 설정
                player.isLooping = loop
                
                // 재생 시작
                if (!player.isPlaying) {
                    player.start()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to control MediaPlayer", e)
            }
        }
    }

    // content 블록 실행 (이 블록이 활성화된 동안만 위의 음악이 재생됨)
    content()
}
