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

// 실제 MainActivity 클래스
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Screen1_1_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "talk") {
        Bubble(
            name = "오누이",
            content = "일제 강점기 때 현충사가 완전히 사라지게 될 뻔한 것에 대해 알고 있니?",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen1_2_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "talk") {
        Bubble(
            name = "누이",
            content = "같이 과거로 가서 현충사를 구해보지 않을래?",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen1_3_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "talk") {
        Bubble(
            name = "누이",
            content = "현충사를 구하기 위해선 기념관을 가서 유물들을 찾아야해!",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen1_4_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "talk") {
        Bubble(
            name = "오라비",
            content = "내가 나침반을 줄게 같이 기념관을 찾아보자.",
            onClick = onNext
        )
    }
}
// 첫 번째 화면 - 인사 화면
@Preview(showBackground = true)
@Composable
fun Screen1_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "오누이",
            content = "일제 강점기 때 현충사가 완전히 사라지게 될 뻔한 것에 대해 알고 있니?",
            onClick = onNext
        )
    }
}

// 두 번째 화면 - 역사 소개
@Preview(showBackground = true)
@Composable
fun Screen2_History(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "오누이",
            content = "좋아! 그럼 먼저 \n현충사의 역사에 대해 알아볼까?",
            onClick = onNext
        )
    }
}

// 세 번째 화면 - 현충사 소개
@Preview(showBackground = true)
@Composable
fun Screen3_Introduction(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "오누이",
            content = "여기가 바로 이순신 장군을 \n모시는 현충사야!",
            onClick = onNext
        )
    }
}

@Composable
fun Main() {
    // 화면 전환 상태 관리
    val transitionState = rememberScreenTransitionState()

    // 화면 전환 매니저로 화면 관리
    ScreenTransitionManager(
        state = transitionState,
        durationMillis = 500,
        screens = listOf(
            { Screen1_Greeting { transitionState.goTo(1, TransitionType.FADE) } },
            { Screen2_History { transitionState.goTo(2, TransitionType.SLIDE_LEFT) } },
            { Screen3_Introduction { transitionState.goTo(0, TransitionType.SCALE) } }
        )
    )
}