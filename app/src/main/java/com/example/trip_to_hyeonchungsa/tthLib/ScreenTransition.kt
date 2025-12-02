package com.example.trip_to_hyeonchungsa.tthLib

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*

/**
 * 화면 전환 타입
 */
enum class TransitionType {
    FADE,           // 페이드 인/아웃
    SLIDE_LEFT,     // 왼쪽으로 슬라이드
    SLIDE_RIGHT,    // 오른쪽으로 슬라이드
    SLIDE_UP,       // 위로 슬라이드
    SLIDE_DOWN,     // 아래로 슬라이드
    SCALE           // 확대/축소
}

/**
 * 화면 전환 상태 관리
 */
class ScreenTransitionState {
    var currentScreen by mutableIntStateOf(0)
        private set

    var transitionType by mutableStateOf(TransitionType.FADE)
        private set

    /**
     * 화면 전환 실행
     * @param screenIndex 이동할 화면 번호
     * @param type 전환 효과 타입 (기본값: FADE)
     */
    fun goTo(screenIndex: Int, type: TransitionType = TransitionType.FADE) {
        transitionType = type
        currentScreen = screenIndex
    }
}

/**
 * 화면 전환 상태를 기억
 */
@Composable
fun rememberScreenTransitionState(): ScreenTransitionState {
    return remember { ScreenTransitionState() }
}

/**
 * 화면 전환 관리자 - 메인 함수
 *
 * 사용법:
 * ```
 * val transitionState = rememberScreenTransitionState()
 *
 * ScreenTransitionManager(
 *     state = transitionState,
 *     screens = listOf(
 *         { Screen1 { transitionState.goTo(1, TransitionType.FADE) } },
 *         { Screen2 { transitionState.goTo(2, TransitionType.SLIDE_LEFT) } }
 *     )
 * )
 * ```
 */
@Composable
fun ScreenTransitionManager(
    state: ScreenTransitionState,
    durationMillis: Int = 500,
    screens: List<@Composable () -> Unit>
) {
    Crossfade(
        targetState = state.currentScreen,
        animationSpec = tween(durationMillis = durationMillis),
        label = "ScreenTransition"
    ) { screenIndex ->
        if (screenIndex in screens.indices) {
            screens[screenIndex]()
        }
    }
}

