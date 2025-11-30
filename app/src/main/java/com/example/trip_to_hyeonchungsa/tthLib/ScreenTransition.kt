// filepath: c:\Users\User\AndroidStudioProjects\trip_to_Hyeonchungsa\app\src\main\java\com\example\trip_to_hyeonchungsa\tthLib\ScreenTransition.kt
package com.example.trip_to_hyeonchungsa.tthLib

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

/**
 * 화면 전환 타입
 */
enum class TransitionType {
    FADE,           // 페이드 인/아웃
    SLIDE_LEFT,     // 왼쪽으로 슬라이드
    SLIDE_RIGHT,    // 오른쪽으로 슬라이드
    SLIDE_UP,       // 위로 슬라이드
    SLIDE_DOWN,     // 아래로 슬라이드
    SCALE,          // 확대/축소
    CROSS_FADE      // 크로스 페이드
}

/**
 * 화면 전환 관리 상태 클래스
 */
class ScreenTransitionState {
    var currentScreen by mutableStateOf(0)
    var isTransitioning by mutableStateOf(false)
    var transitionType by mutableStateOf(TransitionType.FADE)

    suspend fun transitionTo(
        screenIndex: Int,
        type: TransitionType = TransitionType.FADE,
        durationMillis: Long = 500
    ) {
        if (isTransitioning) return

        isTransitioning = true
        transitionType = type
        delay(durationMillis)
        currentScreen = screenIndex
        delay(durationMillis)
        isTransitioning = false
    }
}

/**
 * 화면 전환 상태를 기억하는 함수
 */
@Composable
fun rememberScreenTransitionState(): ScreenTransitionState {
    return remember { ScreenTransitionState() }
}

/**
 * 페이드 인/아웃 효과
 */
@Composable
fun FadeTransition(
    visible: Boolean,
    durationMillis: Int = 500,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = durationMillis)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = durationMillis)
        )
    ) {
        content()
    }
}

/**
 * 슬라이드 전환 효과
 */
@Composable
fun SlideTransition(
    visible: Boolean,
    direction: TransitionType = TransitionType.SLIDE_LEFT,
    durationMillis: Int = 500,
    content: @Composable () -> Unit
) {
    when (direction) {
        TransitionType.SLIDE_LEFT -> {
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = durationMillis)
                ) + fadeIn(animationSpec = tween(durationMillis = durationMillis)),
                exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(durationMillis = durationMillis)
                ) + fadeOut(animationSpec = tween(durationMillis = durationMillis))
            ) {
                content()
            }
        }
        TransitionType.SLIDE_RIGHT -> {
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(durationMillis = durationMillis)
                ) + fadeIn(animationSpec = tween(durationMillis = durationMillis)),
                exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = durationMillis)
                ) + fadeOut(animationSpec = tween(durationMillis = durationMillis))
            ) {
                content()
            }
        }
        TransitionType.SLIDE_UP -> {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = durationMillis)
                ) + fadeIn(animationSpec = tween(durationMillis = durationMillis)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = durationMillis)
                ) + fadeOut(animationSpec = tween(durationMillis = durationMillis))
            ) {
                content()
            }
        }
        TransitionType.SLIDE_DOWN -> {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = durationMillis)
                ) + fadeIn(animationSpec = tween(durationMillis = durationMillis)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = durationMillis)
                ) + fadeOut(animationSpec = tween(durationMillis = durationMillis))
            ) {
                content()
            }
        }
        else -> {
            FadeTransition(visible, durationMillis, content)
        }
    }
}

/**
 * 확대/축소 전환 효과
 */
@Composable
fun ScaleTransition(
    visible: Boolean,
    durationMillis: Int = 500,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = tween(durationMillis = durationMillis)
        ) + fadeIn(
            animationSpec = tween(durationMillis = durationMillis)
        ),
        exit = scaleOut(
            animationSpec = tween(durationMillis = durationMillis)
        ) + fadeOut(
            animationSpec = tween(durationMillis = durationMillis)
        )
    ) {
        content()
    }
}

/**
 * 크로스 페이드 전환 - 두 화면 사이의 부드러운 전환
 */
@Composable
fun <T> CrossFadeTransition(
    targetState: T,
    durationMillis: Int = 500,
    content: @Composable (T) -> Unit
) {
    Crossfade(
        targetState = targetState,
        animationSpec = tween(durationMillis = durationMillis),
        label = "CrossFade"
    ) { state ->
        content(state)
    }
}

/**
 * 화면 전환 컨테이너 - 자동으로 전환 타입에 맞게 처리
 */
@Composable
fun ScreenTransitionContainer(
    visible: Boolean,
    transitionType: TransitionType = TransitionType.FADE,
    durationMillis: Int = 500,
    content: @Composable () -> Unit
) {
    when (transitionType) {
        TransitionType.FADE -> {
            FadeTransition(visible, durationMillis, content)
        }
        TransitionType.SLIDE_LEFT,
        TransitionType.SLIDE_RIGHT,
        TransitionType.SLIDE_UP,
        TransitionType.SLIDE_DOWN -> {
            SlideTransition(visible, transitionType, durationMillis, content)
        }
        TransitionType.SCALE -> {
            ScaleTransition(visible, durationMillis, content)
        }
        TransitionType.CROSS_FADE -> {
            FadeTransition(visible, durationMillis, content)
        }
    }
}

/**
 * 페이드 아웃/인 오버레이 - 화면 전체를 검은색으로 덮는 효과
 */
@Composable
fun FadeOverlay(
    visible: Boolean,
    color: Color = Color.Black,
    durationMillis: Int = 500
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = durationMillis)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = durationMillis)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
        )
    }
}

/**
 * 화면 전환 관리자 - 여러 화면을 관리하고 전환
 */
@Composable
fun ScreenTransitionManager(
    currentScreen: Int,
    transitionType: TransitionType = TransitionType.FADE,
    durationMillis: Int = 500,
    screens: List<@Composable () -> Unit>
) {
    CrossFadeTransition(
        targetState = currentScreen,
        durationMillis = durationMillis
    ) { screenIndex ->
        if (screenIndex in screens.indices) {
            screens[screenIndex]()
        }
    }
}

/**
 * 애니메이션 진행률을 제공하는 함수
 */
@Composable
fun rememberTransitionProgress(
    isTransitioning: Boolean,
    durationMillis: Int = 500
): Float {
    val progress by animateFloatAsState(
        targetValue = if (isTransitioning) 1f else 0f,
        animationSpec = tween(durationMillis = durationMillis),
        label = "TransitionProgress"
    )
    return progress
}

