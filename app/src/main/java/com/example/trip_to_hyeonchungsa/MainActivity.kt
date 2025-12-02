package com.example.trip_to_hyeonchungsa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.trip_to_hyeonchungsa.tthLib.Bubble
import com.example.trip_to_hyeonchungsa.tthLib.Compass
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

@Preview(showBackground = true)
@Composable
fun Screen2_1_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "navi") {
        Compass(37.5665, 126.9780)
    }

}

@Preview(showBackground = true)
@Composable
fun Screen2_2_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "give") {
        Bubble(
            name = "오라비",
            content = "기념관 안에 현충사 현판이 있어 찾아줄래?.",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen2_3_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "give") {
        Bubble(
            name = "누이",
            content = "현판은 입구 근처에 있어 찾아봐!",
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
            { Screen1_1_Greeting { transitionState.goTo(1, TransitionType.FADE) } },
            { Screen1_2_Greeting { transitionState.goTo(2, TransitionType.SLIDE_LEFT) } },
            { Screen1_3_Greeting { transitionState.goTo(3, TransitionType.SCALE) } },
            {Screen1_4_Greeting { transitionState.goTo(4, TransitionType.SCALE) } },
            { Screen2_1_Greeting{ transitionState.goTo(5, TransitionType.SCALE) } },
            { Screen2_2_Greeting{ transitionState.goTo(6, TransitionType.SCALE) } },
            { Screen2_3_Greeting{ transitionState.goTo(0, TransitionType.SCALE) } }
        )
    )
}