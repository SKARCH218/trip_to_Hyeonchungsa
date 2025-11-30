package com.example.trip_to_hyeonchungsa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.trip_to_hyeonchungsa.tthLib.Bubble
import com.example.trip_to_hyeonchungsa.tthLib.SetBackground
import com.example.trip_to_hyeonchungsa.tthLib.ScreenTransitionManager
import com.example.trip_to_hyeonchungsa.tthLib.TransitionType
import com.example.trip_to_hyeonchungsa.tthLib.rememberScreenTransitionState
import kotlinx.coroutines.launch

// 실제 MainActivity 클래스
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}

// 첫 번째 화면 - 인사 화면
@Composable
fun Screen1_Greeting(onNext: () -> Unit) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "오누이",
            content = "안녕, 우리 함께 \n현충사를 구해보지 않을래?",
            onClick = onNext
        )
    }
}

// 두 번째 화면 - 역사 소개
@Composable
fun Screen2_History(onNext: () -> Unit) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "오누이",
            content = "좋아! 그럼 먼저 \n현충사의 역사에 대해 알아볼까?",
            onClick = onNext
        )
    }
}

// 세 번째 화면 - 현충사 소개
@Composable
fun Screen3_Introduction(onNext: () -> Unit) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "오누이",
            content = "여기가 바로 이순신 장군을 \n모시는 현충사야!",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Main() {
    // 화면 전환 상태 관리
    val transitionState = rememberScreenTransitionState()
    val coroutineScope = rememberCoroutineScope()

    // 여러 화면 정의
    val screens = listOf<@Composable () -> Unit>(
        {
            Screen1_Greeting {
                coroutineScope.launch {
                    transitionState.transitionTo(
                        screenIndex = 1,
                        type = TransitionType.FADE,
                        durationMillis = 500
                    )
                }
            }
        },
        {
            Screen2_History {
                coroutineScope.launch {
                    transitionState.transitionTo(
                        screenIndex = 2,
                        type = TransitionType.SLIDE_LEFT,
                        durationMillis = 500
                    )
                }
            }
        },
        {
            Screen3_Introduction {
                coroutineScope.launch {
                    transitionState.transitionTo(
                        screenIndex = 0,
                        type = TransitionType.SCALE,
                        durationMillis = 500
                    )
                }
            }
        }
    )

    // 화면 전환 매니저로 화면 전환 처리
    ScreenTransitionManager(
        currentScreen = transitionState.currentScreen,
        transitionType = transitionState.transitionType,
        durationMillis = 500,
        screens = screens
    )
}