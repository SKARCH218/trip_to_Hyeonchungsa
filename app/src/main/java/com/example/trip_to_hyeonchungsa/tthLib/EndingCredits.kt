package com.example.trip_to_hyeonchungsa.tthLib

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * 엔딩 크레딧 함수
 *
 * 화면 하단에서 상단으로 텍스트가 올라가는 엔딩 크레딧 애니메이션을 표시합니다.
 *
 * @param text 표시할 크레딧 텍스트 (줄바꿈은 \n 사용)
 * @param speed 크레딧이 올라가는 속도 (픽셀/초, 기본값: 50f)
 * @param fontSize 텍스트 크기 (sp 단위, 기본값: 20)
 * @param textColor 텍스트 색상 (기본값: White)
 * @param onFinished 크레딧이 모두 올라간 후 호출되는 콜백
 */
@Composable
fun EndingCredits(
    text: String,
    speed: Float = 50f,
    fontSize: Int = 20,
    textColor: Color = Color.White,
    onFinished: (() -> Unit)? = null
) {
    // 애니메이션 시작 플래그
    var startAnimation by remember { mutableStateOf(false) }

    // 화면 높이를 추정 (실제로는 BoxWithConstraints로 측정 가능)
    val density = LocalDensity.current

    // 크레딧이 올라가는 총 거리 추정 (화면 높이 + 텍스트 높이)
    // 텍스트 한 줄당 약 fontSize * 1.5 높이를 가정
    val lineCount = text.count { it == '\n' } + 1
    val estimatedTextHeight = with(density) { (fontSize * 1.5f * lineCount).dp }

    // 애니메이션 지속 시간 계산
    val durationMillis = remember(speed) {
        // 대략적인 화면 높이(800dp)와 텍스트 높이를 기준으로 계산
        val totalDistance = 800f + (fontSize * 1.5f * lineCount)
        ((totalDistance / speed) * 1000).toInt().coerceAtLeast(1000)
    }

    // Y 오프셋 애니메이션
    val offsetY: Dp by animateDpAsState(
        targetValue = if (startAnimation) -(800.dp + estimatedTextHeight) else 800.dp,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = LinearEasing
        ),
        label = "credits_animation",
        finishedListener = {
            onFinished?.invoke()
        }
    )

    // 애니메이션 시작
    LaunchedEffect(Unit) {
        delay(100) // 짧은 지연 후 시작
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            lineHeight = (fontSize * 1.5f).sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .offset(y = offsetY)
        )
    }
}

/**
 * 엔딩 크레딧 상태 관리 클래스
 *
 * 크레딧 재생 상태를 관리하고 제어할 수 있는 기능을 제공합니다.
 */
class EndingCreditsState {
    var isPlaying by mutableStateOf(false)
        private set

    var isFinished by mutableStateOf(false)
        private set

    /**
     * 크레딧 재생 시작
     */
    fun start() {
        isPlaying = true
        isFinished = false
    }

    /**
     * 크레딧 재생 중지
     */
    fun stop() {
        isPlaying = false
    }

    /**
     * 크레딧 재생 완료 처리
     */
    fun finish() {
        isFinished = true
        isPlaying = false
    }

    /**
     * 상태 초기화
     */
    fun reset() {
        isPlaying = false
        isFinished = false
    }
}

/**
 * 엔딩 크레딧 상태를 기억
 */
@Composable
fun rememberEndingCreditsState(): EndingCreditsState {
    return remember { EndingCreditsState() }
}

/**
 * 제어 가능한 엔딩 크레딧 함수
 *
 * 상태를 통해 재생을 제어할 수 있는 크레딧 애니메이션을 표시합니다.
 *
 * @param state 크레딧 상태 관리 객체
 * @param text 표시할 크레딧 텍스트
 * @param speed 크레딧이 올라가는 속도 (픽셀/초)
 * @param fontSize 텍스트 크기 (sp 단위)
 * @param textColor 텍스트 색상
 * @param onFinished 크레딧이 모두 올라간 후 호출되는 콜백
 */
@Composable
fun ControlledEndingCredits(
    state: EndingCreditsState,
    text: String,
    speed: Float = 50f,
    fontSize: Int = 20,
    textColor: Color = Color.White,
    onFinished: (() -> Unit)? = null
) {
    if (state.isPlaying) {
        EndingCredits(
            text = text,
            speed = speed,
            fontSize = fontSize,
            textColor = textColor,
            onFinished = {
                state.finish()
                onFinished?.invoke()
            }
        )
    }
}

