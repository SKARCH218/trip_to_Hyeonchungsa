package com.example.trip_to_hyeonchungsa.tthLib

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * 캐릭터를 화면에 표시하는 함수
 * @param horizontalPosition 가로 위치 (0~100: 0이 가장 왼쪽, 50이 중앙, 100이 가장 오른쪽)
 * @param verticalPosition 세로 위치 (0~100: 0이 가장 위, 50이 중앙, 100이 가장 아래)
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
    
    // 명도를 ColorMatrix로 변환 (0~200 범위를 0.0~2.0으로 변환)
    val brightnessValue = brightness.coerceIn(0, 200) / 100f
    val colorMatrix = ColorMatrix().apply {
        setToScale(brightnessValue, brightnessValue, brightnessValue, 1f)
    }
    
    if (imageResId != 0) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            // 화면 크기 (픽셀로 변환)
            val screenWidthPx = constraints.maxWidth
            val screenHeightPx = constraints.maxHeight

            // 이미지 크기
            val imageSizeDp = size.dp

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Character",
                modifier = Modifier
                    .size(imageSizeDp)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)

                        // 위치 계산 (픽셀 단위로 정확하게 계산)
                        val targetX = (screenWidthPx * horizontalPosition.coerceIn(0, 100) / 100f).toInt()
                        val targetY = (screenHeightPx * verticalPosition.coerceIn(0, 100) / 100f).toInt()

                        // 이미지 중심을 목표 위치에 배치
                        val x = targetX - (placeable.width / 2)
                        val y = targetY - (placeable.height / 2)

                        layout(placeable.width, placeable.height) {
                            placeable.place(x, y)
                        }
                    },
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.colorMatrix(colorMatrix)
            )
        }
    }
}
