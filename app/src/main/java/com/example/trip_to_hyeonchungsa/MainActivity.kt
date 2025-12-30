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

//기념관 퀘스트
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
            content = "오! 찾았구나 이 현판은 숙종 임금이 직접 쓴 것으로 구현충사에 걸려 있던거야 그럼 복숭아 모양처럼 생긴 잔 을 찾아볼까? 이 근처에 있어!",
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

//정려이동 퀘스트
@Preview(showBackground = true)
@Composable
fun Screen7_1_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "오라비",
            content = "지금부터 우린 정려 근처 다리로 이동할거야",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen7_2_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "누이",
            content = "정려는 덕수 이씨 집안에서 충신, 효자, 열녀분들의 \n" +
                    "이름을 기록해놓은 곳이야",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen7_3_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "플레이어",
            content = "거기엔 왜 가는데?",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen7_4_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "누이",
            content = "현충사를 구하려면 모금운동을 해야해",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen7_5_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "오라비",
            content = "너가 주도로 모금운동을 해서 현충사를 구해줬으면 해",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen7_6_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "누이",
            content = "정려 근처 다리에 가면 과거로 이동할수 있어",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen7_7_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "오라비",
            content = "너가 과거로 가서 현충사를 구해줘",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen7_8_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "누이",
            content = "하지만 일반적인 방법으로는 과거로 갈수 없어",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen7_9_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "img_example") {
        Bubble(
            name = "오라비",
            content = "다리 근처에 할아버지 한분이 계실꺼야. 그분께 우리가 모은 유물을 드리면 과거로 갈 수 있게 도와주실꺼야",
            onClick = onNext
        )
    }
}

//할아버지와 대화
@Preview(showBackground = true)
@Composable
fun Screen8_1_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "hal") {
         Bubble(
            name = "할아버지",
            content = "자네 왜? 여기에 왔는가?",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen8_2_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "hal") {
        Bubble(
            name = "플레이어",
            content = "저는 현충사를 구하기 위해서 과거로 갈거에요!",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen8_3_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "hal") {
        Bubble(
            name = "할아버지",
            content = "아이고 기특하지..!",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen8_4_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "hal") {
        Bubble(
            name = "할아버지",
            content = "그럼 너가 기념관에서 찾은 유물을 주렴",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen8_5_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "hal") {
        Bubble(
            name = "할아버지",
            content = "그럼 내가 과거로 갈 수 있게 도와주겠네",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen8_6_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "hal") {
        Bubble(
            name = "플레이어",
            content = "자! 여기요!",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen8_7_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "hal") {
        Bubble(
            name = "할아버지",
            content = "그럼 과거로 가보자구나..",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen8_8_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "hal") {
        Bubble(
            name = "플레이어",
            content = "감사합니다 할아버지!",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen8_9_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "hal") {
        Bubble(
            name = "할아버지",
            content = "그래 과거로 가서 꼭 현충사를 찾아주게!",
            onClick = onNext
        )
    }
}

//이순신 고택 퀘스트
@Preview(showBackground = true)
@Composable
fun Screen9_1_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "yee_hal") {
        Bubble(
            name = "이순신",
            content = "이곳까지 오는데 정말 수고했네..",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen9_2_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "yee_hal") {
        Bubble(
            name = "이순신",
            content = "전쟁중 이 몹쓸 아비때문에 목숨을 잃은 내 아들 이면에게 편지를 전해줬으면 하네..",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen9_3_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "yee_hal") {
        Bubble(
            name = "이순신",
            content = "보다시피 내 몸이 이런지라 직접 전할수도 편지를 쓸수도 없다네.. 그러니 대신 편지를 써줬으면 하네..",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen9_4_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "yee_hal") {
        Bubble(
            name = "플레이어",
            content = "네 꼭 가져다 드리겠습니다.",
            onClick = onNext
        )
    }
}

//이면공 묘소 퀘스트

@Preview(showBackground = true)
@Composable
fun Screen10_2_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "yee_ramyon") {
        Bubble(
            name = "이면공",
            content = "여기까지는 어쩐 일이오?",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen10_3_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "yee_ramyon") {
        Bubble(
            name = "플레이어",
            content = "이순신 장군님께서 편지를 전해달라 하셨습니다.",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen10_4_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "yee_ramyon") {
        Bubble(
            name = "이면공",
            content = "아버지께서 편지를?",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen10_5_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "yee_ramyon") {
        Bubble(
            name = "이면공",
            content = "[눈물을 흘리며] 아버지께서... 소인은 그저, 아버지께서 나라를 구하셨듯이 가족을 지킨 것 뿐이온데... 어찌 아버지께서 이런생각을...",
            onClick = onNext
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Screen10_6_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "yee_ramyon") {
        Bubble(
            name = "이면공",
            content = "...현충사에 가게 되면 영정이 하나 있을것이오. 영정 앞에서 참배를 하여 아버지를 향한 나의 마음을 전해주었으면 하네",
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
            { Screen1_4_Greeting { transitionState.goTo(4, TransitionType.SCALE) } },

            //기념관 퀘스트
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

            //정려이동 퀘스트
            { Screen7_1_Greeting{ transitionState.goTo(17, TransitionType.SCALE) } },
            { Screen7_2_Greeting{ transitionState.goTo(18, TransitionType.SCALE) } },
            { Screen7_3_Greeting{ transitionState.goTo(19, TransitionType.SCALE) } },
            { Screen7_4_Greeting{ transitionState.goTo(20, TransitionType.SCALE) } },
            { Screen7_5_Greeting{ transitionState.goTo(21, TransitionType.SCALE) } },
            { Screen7_6_Greeting{ transitionState.goTo(22, TransitionType.SCALE) } },
            { Screen7_7_Greeting{ transitionState.goTo(23, TransitionType.SCALE) } },
            { Screen7_8_Greeting{ transitionState.goTo(24, TransitionType.SCALE) } },
            { Screen7_9_Greeting{ transitionState.goTo(25, TransitionType.SCALE) } },

            //할아버지와의 대화
            { Screen8_1_Greeting{ transitionState.goTo(26, TransitionType.SCALE) } },
            { Screen8_2_Greeting{ transitionState.goTo(27, TransitionType.SCALE) } },
            { Screen8_3_Greeting{ transitionState.goTo(28, TransitionType.SCALE) } },
            { Screen8_4_Greeting{ transitionState.goTo(29, TransitionType.SCALE) } },
            { Screen8_5_Greeting{ transitionState.goTo(30, TransitionType.SCALE) } },
            { Screen8_6_Greeting{ transitionState.goTo(31, TransitionType.SCALE) } },
            { Screen8_7_Greeting{ transitionState.goTo(32, TransitionType.SCALE) } },
            { Screen8_8_Greeting{ transitionState.goTo(33, TransitionType.SCALE) } },
            { Screen8_9_Greeting{ transitionState.goTo(34, TransitionType.SCALE) } },

            //이순신 고택 퀘스트
            { Screen9_1_Greeting{ transitionState.goTo(35, TransitionType.SCALE) } },
            { Screen9_2_Greeting{ transitionState.goTo(36, TransitionType.SCALE) } },
            { Screen9_3_Greeting{ transitionState.goTo(37, TransitionType.SCALE) } },
            { Screen9_4_Greeting{ transitionState.goTo(38, TransitionType.SCALE) } },

            //

        )
    )
}

