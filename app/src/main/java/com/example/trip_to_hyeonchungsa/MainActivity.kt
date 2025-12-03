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


@Preview(showBackground = true) // 시작대화
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

@Preview(showBackground = true) // 시작대화
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

@Preview(showBackground = true) // 시작대화
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

@Preview(showBackground = true) // 시작대화
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

@Preview(showBackground = true) // 기념관 가기 위한 나침반
@Composable
fun Screen2_1_Greeting(/*onNext: () -> Unit = {}*/) {
    SetBackground(imageName = "navi") {
        //Compass(37.5665, 126.9780)
    }
}

@Preview(showBackground = true)  // 기념관 앞 미션 제공
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

@Preview(showBackground = true) // 현판 힌트
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

@Preview(showBackground = true) // 현판 찾고 잔 찾기
@Composable
fun Screen3_1_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "pan") {
        Bubble(
            name = "누이",
            content = "오! 찾았구나 이 현판은 이 현판은 숙종 임금이 직접 쓴 것으로 구현충사에 걸려 있던거야 그럼 복숭아 모양처럼 생긴 잔 을 찾아볼까? 이 근처에 있어!",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true) // 잔 찾고 설명
@Composable
fun Screen3_2_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "cup") {
        Bubble(
            name = "오라비",
            content = "잘 찾았어 이 복숭아 모양 술잔은 난중일기의 ‘화주배 한 쌍’으로 추정되는 얇은 금도금 술잔으로",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true) // 임진일기 찾기
@Composable
fun Screen3_3_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "cup") {
        Bubble(
            name = "오라비",
            content = """
                지금은 손잡이 조각의 깊은 홈에서만 금 흔적이 남아 있어
                그럼 다음 임진일기를 찾아보자 근처에 있으니 찾아보자! 
                    """.trimIndent(),
            onClick = onNext
        )
    }
}

@Preview(showBackground = true) // 임진일기 힌트
@Composable
fun Screen3_4_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "history_book") {
        Bubble(
            name = "누이",
            content = """
                암호를 해독하면 임진일기를 얻을 수 있어!
                암호는 ■■ 해전 과 ■■ 해전 당시 함대의 기동과 전투 경위등이 기술되어 있다
                빈칸이 뭔지 생각해봐
                    """.trimIndent(),
            onClick = onNext
        )
    }
}

@Preview(showBackground = true) // 임진일기 획득 조총 찾기
@Composable
fun Screen3_5_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "history_book") {
        Bubble(
            name = "오누이",
            content = """ 맞아 정답은 명량, 노량이야
 임진일기에는 그 밖의 공문이나 편지도 수록되어 있어.
 자, 그럼 왜군(일본군)이 사용한 조총을 찾아보자
                   """.trimIndent(),
            onClick = onNext
        )
    }
}

@Preview(showBackground = true) // 조총 설명 하고 거북선 그림 찾기
@Composable
fun Screen3_6_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "gun") {
        Bubble(
            name = "오라비",
            content = """ 조총은 총배에 화약과 탄을 넣고 화승(느리게 타는 끈)에 불을 붙여 방아쇠를 당겨 발화시키는 방식인 총이야 
이제 거북선(그림)을 찾으러 가자!
거북선(그림),(모형) 찾으면 과거로 갈 수 있어!
                   """.trimIndent(),
            onClick = onNext
        )
    }
}


@Preview(showBackground = true) // 거북선 그림 설명
@Composable
fun Screen3_7_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "turtle") {
        Bubble(
            name = "누이",
            content = """
거북선은 조선 시대 임진왜란(1592~ 1598) 때 사용한 전투용 군함이야

                   """.trimIndent(),
            onClick = onNext
        )
    }
}

@Preview(showBackground = true) // 아이템 다 찾고 이제 정려 가야됨
@Composable
fun Screen3_8_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "give") {
        Bubble(
            name = "오누이",
            content = """
 드디어 다 찾았어! 이제 정려로 가기 위해서 준비는 다 했어

                   """.trimIndent(),
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
            //{ Screen2_1_Greeting{ transitionState.goTo(5, TransitionType.SCALE) } },
            { Screen2_2_Greeting{ transitionState.goTo(6, TransitionType.SCALE) } },
            { Screen2_3_Greeting{ transitionState.goTo(7, TransitionType.SCALE) } },
            { Screen3_1_Greeting{ transitionState.goTo(8, TransitionType.SCALE) } },
            { Screen3_2_Greeting{ transitionState.goTo(9, TransitionType.SCALE) } },
            { Screen3_3_Greeting{ transitionState.goTo(10, TransitionType.SCALE) } },
            { Screen3_4_Greeting{ transitionState.goTo(11, TransitionType.SCALE) } },
            { Screen3_5_Greeting{ transitionState.goTo(12, TransitionType.SCALE) } },
            { Screen3_5_Greeting{ transitionState.goTo(13, TransitionType.SCALE) } },
            { Screen3_6_Greeting{ transitionState.goTo(14, TransitionType.SCALE) } },
            { Screen3_7_Greeting{ transitionState.goTo(15, TransitionType.SCALE) } },
            { Screen3_8_Greeting{ transitionState.goTo(16, TransitionType.SCALE) } },

        )
    )
}