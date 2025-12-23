package com.example.trip_to_hyeonchungsa.tthLib

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * 터치 미니게임 컴포저블
 * 화면을 터치하면 게이지가 오르고, 자동으로 감소합니다.
 * 게이지가 가득 차면 onComplete 콜백이 실행됩니다.
 *
 * @param onComplete 게이지가 100%에 도달했을 때 실행될 콜백
 * @param modifier Modifier
 * @param gaugeDecreaseRate 게이지 감소 속도 (초당 감소량, 기본값: 5f)
 * @param gaugeIncreaseAmount 터치당 증가량 (기본값: 8f)
 * @param message 하단에 표시될 메시지
 */
@Composable
fun Massage(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    gaugeDecreaseRate: Float = 5f,
    gaugeIncreaseAmount: Float = 8f,
    message: String = "화면을 눌러 안마를 해주세요!"
) {
    // 게이지 상태 (0f ~ 100f)
    var gaugeValue by remember { mutableStateOf(0f) }

    // 터치 효과 리스트
    var touchEffects by remember { mutableStateOf<List<TouchEffect>>(emptyList()) }

    // 게임 완료 여부
    var isCompleted by remember { mutableStateOf(false) }

    // 게이지 자동 감소 및 완료 체크
    LaunchedEffect(Unit) {
        while (true) {
            delay(100) // 0.1초마다 체크

            // 완료 조건 체크
            if (gaugeValue >= 99f && !isCompleted) {
                isCompleted = true
                gaugeValue = 100f // 확실하게 100%로 설정
                delay(300) // 잠깐 대기 후 콜백 실행
                onComplete()
                break // 루프 종료
            }

            // 게이지 감소 (완료되지 않았을 때만)
            if (!isCompleted && gaugeValue > 0f) {
                gaugeValue = (gaugeValue - (gaugeDecreaseRate / 10f)).coerceIn(0f, 100f)
            }
        }
    }

    // 터치 효과 제거
    LaunchedEffect(touchEffects.size) {
        if (touchEffects.isNotEmpty()) {
            delay(500) // 0.5초 후 효과 제거
            touchEffects = touchEffects.drop(1)
        }
    }


    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 터치 영역 (전체 화면)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (!isCompleted) {
                            // 게이지 증가
                            gaugeValue = (gaugeValue + gaugeIncreaseAmount).coerceIn(0f, 100f)

                            // 터치 효과 추가
                            touchEffects = touchEffects + TouchEffect(
                                position = offset,
                                id = System.currentTimeMillis()
                            )
                        }
                    }
                }
        )

        // 상단 게이지 UI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 64.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 게이지 바
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(Color.LightGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(gaugeValue / 100f)
                        .fillMaxHeight()
                        .background(
                            when {
                                gaugeValue >= 100f -> Color(0xFF4CAF50) // 초록색
                                gaugeValue >= 70f -> Color(0xFFFFC107) // 노란색
                                else -> Color(0xFF2196F3) // 파란색
                            }
                        )
                )
            }

            // 퍼센트 표시
            Text(
                text = "${gaugeValue.toInt()}%",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.8f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // 중앙 메시지
        Text(
            text = message,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
        )

        // 터치 효과 렌더링
        touchEffects.forEach { effect ->
            TouchEffectAnimation(effect = effect)
        }
    }
}

/**
 * 터치 효과 데이터 클래스
 */
data class TouchEffect(
    val position: Offset,
    val id: Long
)

/**
 * 터치 효과 애니메이션
 */
@Composable
fun TouchEffectAnimation(effect: TouchEffect) {
    // 애니메이션 시작 트리거
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(effect.id) {
        animationStarted = true
    }

    // 크기 애니메이션 (작게 시작해서 커지다가 사라짐)
    val scale by animateFloatAsState(
        targetValue = if (animationStarted) 2f else 0f,
        animationSpec = tween(500, easing = LinearOutSlowInEasing),
        label = "scale_${effect.id}"
    )

    // 투명도 애니메이션 (점점 사라짐)
    val alpha by animateFloatAsState(
        targetValue = if (animationStarted) 0f else 1f,
        animationSpec = tween(500, easing = LinearEasing),
        label = "alpha_${effect.id}"
    )

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        // 원형 효과
        drawCircle(
            color = Color(0xFFFFEB3B).copy(alpha = alpha),
            radius = 20.dp.toPx() * scale,
            center = effect.position
        )

        // 내부 원
        drawCircle(
            color = Color(0xFFFFC107).copy(alpha = alpha * 0.7f),
            radius = 15.dp.toPx() * scale,
            center = effect.position
        )

        // 중심 점
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = 5.dp.toPx() * scale,
            center = effect.position
        )
    }
}
