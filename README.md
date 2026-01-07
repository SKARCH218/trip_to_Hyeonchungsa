# trip_to_Hyeonchungsa
아산스마트팩토리마이스터고등학교 역사앱개발반 방과후 현충사 역사체험앱 제작 프로젝트

## 📱 프로젝트 소개
현충사를 배경으로 한 역사 체험 앱입니다. 사용자는 과거로 시간여행을 떠나 현충사를 구하는 미션을 수행합니다.

## 🚀 주요 기능

### 1. 나침반 (Compass)
목적지까지 방향을 안내하는 나침반 기능입니다.

**기본 사용법:**
```kotlin
Compass(
    destinationLat = 36.929529,  // 목적지 위도
    destinationLon = 127.043517   // 목적지 경도
) {
    // 나침반이 표시되는 동안 실행할 코드
    QuestDisplay(
        questTitle = "현충사 기념관 방문",
        questContent = "나침반을 따라 현충사 기념관으로 이동하세요"
    )
}
```

**자동 화면 전환 예제:**
```kotlin
@Composable
fun Screen2_1_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "navi", onClick = onNext) {
        Compass(
            destinationLat = 36.929529,
            destinationLon = 127.043517
        ) {
            QuestDisplay(
                questTitle = "현충사 기념관 방문",
                questContent = "나침반을 따라 이동하세요",
                questDetailContent = "20m 이내 도착시 자동으로 다음 화면으로 이동합니다"
            )
        }
    }
}
```

### 2. 대화 버블 (Bubble)
캐릭터의 대화를 표시하는 말풍선입니다.

**기본 사용법:**
```kotlin
Bubble(
    name = "오라비",
    content = "안녕하세요!",
    onClick = { /* 클릭시 실행할 코드 */ }
)
```

**다음 화면으로 이동 예제:**
```kotlin
@Composable
fun Screen1_1_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "talk") {
        Bubble(
            name = "오누이",
            content = "같이 현충사를 구하러 가지 않을래?",
            onClick = onNext  // 클릭시 다음 화면으로
        )
    }
}
```

### 3. 캐릭터/이미지 요소 (Character)
배경이 아닌 다른 요소 이미지를 화면에 표시합니다. 위치, 크기, 밝기를 조절할 수 있습니다.

**기본 사용법:**
```kotlin
Character(
    horizontalPosition = 50,  // 가로 위치 (0~100: 0=왼쪽, 50=중앙, 100=오른쪽)
    verticalPosition = 50,    // 세로 위치 (0~100: 0=위, 50=중앙, 100=아래)
    size = 400,               // 이미지 크기 (픽셀)
    brightness = 100,         // 명도 (0~200: 0=어둡게, 100=보통, 200=밝게)
    imageName = "again"       // 이미지 파일명
)
```

**오답 시 3초간 이미지 표시 예제:**
```kotlin
@Composable
fun Screen3_4_Greeting(onNext: () -> Unit = {}) {
    var showAgainImage by remember { mutableStateOf(false) }
    
    SetBackground(imageName = "history_book") {
        if (showAgainImage) {
            // 어게인 이미지를 화면 중앙에 표시
            Character(
                horizontalPosition = 50,
                verticalPosition = 50,
                size = 400,
                brightness = 100,
                imageName = "again"
            )
            
            // 3초 후 이미지 제거
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showAgainImage = false
            }
        } else {
            // 일반 화면 내용
            Bubble(...)
        }
    }
}
```

**위치 조절 예제:**
```kotlin
// 왼쪽 상단에 작은 이미지 표시
Character(
    horizontalPosition = 20,
    verticalPosition = 20,
    size = 150,
    brightness = 100,
    imageName = "small_icon"
)

// 오른쪽 하단에 큰 이미지 표시
Character(
    horizontalPosition = 80,
    verticalPosition = 80,
    size = 500,
    brightness = 120,
    imageName = "big_image"
)
```

### 4. 선택지 (Choice)
사용자에게 두 가지 선택지를 제공합니다.

**기본 사용법:**
```kotlin
var showChoice by remember { mutableStateOf(false) }

if (!showChoice) {
    Bubble(
        name = "누이",
        content = "퀴즈: 정답을 맞춰봐!",
        onClick = { showChoice = true }
    )
} else {
    Choice("정답", "오답") { selectedOption ->
        // selectedOption: 1 = 첫번째 선택지, 2 = 두번째 선택지
        if (selectedOption == 1) {
            onNext() // 정답이면 다음 화면으로
        } else {
            // 오답 처리
        }
    }
}
```

**완전한 예제 (오답 시 이미지 표시):**
```kotlin
@Composable
fun Screen3_4_Greeting(onNext: () -> Unit = {}) {
    var showChoice by remember { mutableStateOf(false) }
    var showAgainImage by remember { mutableStateOf(false) }
    
    SetBackground(imageName = "history_book") {
        if (showAgainImage) {
            // 오답 시 어게인 이미지 표시
            Character(
                horizontalPosition = 50,
                verticalPosition = 50,
                size = 400,
                brightness = 100,
                imageName = "again"
            )
            
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showAgainImage = false
                showChoice = false
            }
        } else if (!showChoice) {
            Bubble(
                name = "누이",
                content = "암호는 ■■ 해전과 ■■ 해전이야. 빈칸을 맞춰봐!",
                onClick = { showChoice = true }
            )
        } else {
            Choice("사천.당포", "명량.노량") { selectedOption ->
                if (selectedOption == 1) {
                    onNext() // 정답이면 다음 화면으로
                } else if (selectedOption == 2) {
                    showAgainImage = true // 오답이면 이미지 표시
                }
            }
        }
    }
}
```

### 5. 이미지 인식 (ImageSensing)
AR을 통해 특정 이미지를 인식합니다.

**기본 사용법:**
```kotlin
var showQuest by remember { mutableStateOf(false) }

if (!showQuest) {
    Bubble(
        name = "누이",
        content = "현판을 찾아봐!",
        onClick = { showQuest = true }
    )
} else {
    QuestDisplay(
        "현충사 현판을 찾아라",
        "현판을 스캔하세요"
    ) {
        if (ImageSensing("이미지 이름").value == true) {
            onNext()  // 인식되면 다음 화면으로
        }
    }
}
```

## 📋 화면 구성 패턴

### 기본 화면 구조
```kotlin
@Preview(showBackground = true)
@Composable
fun ScreenX_Y_Greeting(onNext: () -> Unit = {}) {
    SetBackground(imageName = "배경이미지명") {
        // 여기에 화면 내용 작성
        Bubble(
            name = "캐릭터명",
            content = "대사 내용",
            onClick = onNext
        )
    }
}
```

### Main 함수에 화면 추가
```kotlin
@Composable
fun Main() {
    val transitionState = rememberScreenTransitionState()

    ScreenTransitionManager(
        state = transitionState,
        screens = listOf(
            { Screen1_1_Greeting { transitionState.goTo(1, TransitionType.FADE) } },
            { Screen1_2_Greeting { transitionState.goTo(2, TransitionType.SLIDE_LEFT) } },
            // 새로운 화면 추가...
        )
    )
}
```

## 🎯 전환 효과 종류
- `TransitionType.FADE` - 페이드 효과
- `TransitionType.SLIDE_LEFT` - 왼쪽으로 슬라이드
- `TransitionType.SCALE` - 확대/축소 효과

## ⚠️ 주의사항

1. **Composable 함수는 Composable 컨텍스트에서만 호출**
   - ❌ 잘못된 예: `onClick = { MyComposableFunction() }`
   - ✅ 올바른 예: 상태를 사용하여 조건부 렌더링

2. **상태 관리 사용**
   ```kotlin
   var showSomething by remember { mutableStateOf(false) }
   
   if (showSomething) {
       // Composable 표시
   }
   ```

3. **context 파라미터**
   - Compass와 CheckArrival 같은 위치 기반 함수들은 자동으로 context를 처리합니다.
   - 직접 context를 전달할 필요가 없습니다.

## 🛠️ 빌드 및 실행

1. Android Studio에서 프로젝트 열기
2. Gradle 동기화 완료 대기
3. 실제 Android 기기 연결 또는 에뮬레이터 실행
4. Run 버튼 클릭

## 📞 문의
아산스마트팩토리마이스터고등학교 역사앱개발반

