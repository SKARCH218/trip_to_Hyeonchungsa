package com.example.trip_to_hyeonchungsa

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * 캐릭터를 화면에 표시하는 함수
 * @param horizontalPosition 가로 위치 (1~10: 1이 가장 왼쪽, 10이 가장 오른쪽)
 * @param verticalPosition 세로 위치 (1~10: 1이 가장 위, 10이 가장 아래)
 * @param size 이미지 크기 (기본값: 200)
 * @param brightness 명도 (0~200: 0은 완전 어둡게, 100은 보통, 200은 매우 밝게, 기본값: 100)
 * @param imageName 이미지 파일 이름 (예: "character")
 */
@Composable
fun Character(
    horizontalPosition: Int,
    verticalPosition: Int,
    size: Int,
    brightness: Int,
    imageName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(
        imageName,
        "drawable",
        context.packageName
    )
    
    // 가로 위치를 -0.9 ~ 0.9 범위로 변환
    val horizontalBias = when (horizontalPosition) {
        1 -> -0.9f
        2 -> -0.7f
        3 -> -0.5f
        4 -> -0.3f
        5 -> -0.1f
        6 -> 0.1f
        7 -> 0.3f
        8 -> 0.5f
        9 -> 0.7f
        10 -> 0.9f
        else -> 0f
    }
    
    // 세로 위치를 -0.9 ~ 0.9 범위로 변환
    val verticalBias = when (verticalPosition) {
        1 -> -0.9f
        2 -> -0.7f
        3 -> -0.5f
        4 -> -0.3f
        5 -> -0.1f
        6 -> 0.1f
        7 -> 0.3f
        8 -> 0.5f
        9 -> 0.7f
        10 -> 0.9f
        else -> 0f
    }
    
    // 명도를 ColorMatrix로 변환 (0~200 범위를 0.0~2.0으로 변환)
    val brightnessValue = brightness.coerceIn(0, 200) / 100f
    val colorMatrix = ColorMatrix().apply {
        setToScale(brightnessValue, brightnessValue, brightnessValue, 1f)
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = BiasAlignment(horizontalBias, verticalBias)
    ) {
        if (imageResId != 0) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Character",
                modifier = Modifier
                    .size(size.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.colorMatrix(colorMatrix)
            )
        }
    }
}