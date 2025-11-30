# ScreenTransition - 화면 전환 라이브러리

화면 전환 효과를 쉽게 구현할 수 있는 라이브러리입니다. 페이드 인/아웃, 슬라이드, 확대/축소 등 다양한 전환 효과를 제공합니다.

## 📋 목차
- [기본 사용법](#기본-사용법)
- [전환 타입](#전환-타입)
- [화면 전환 관리](#화면-전환-관리)
- [고급 사용법](#고급-사용법)
- [예제](#예제)

---

## 기본 사용법

### 1. 화면 전환 상태 생성

```kotlin
@Composable
fun MyScreen() {
    val transitionState = rememberScreenTransitionState()
    val coroutineScope = rememberCoroutineScope()
    
    // 화면 전환 실행
    Button(onClick = {
        coroutineScope.launch {
            transitionState.transitionTo(
                screenIndex = 1,
                type = TransitionType.FADE,
                durationMillis = 500
            )
        }
    }) {
        Text("다음 화면으로")
    }
}
```

---

## 전환 타입

### TransitionType 종류

```kotlin
enum class TransitionType {
    FADE,           // 페이드 인/아웃
    SLIDE_LEFT,     // 왼쪽으로 슬라이드
    SLIDE_RIGHT,    // 오른쪽으로 슬라이드
    SLIDE_UP,       // 위로 슬라이드
    SLIDE_DOWN,     // 아래로 슬라이드
    SCALE,          // 확대/축소
    CROSS_FADE      // 크로스 페이드
}
```

### 1. 페이드 전환 (FADE)
화면이 부드럽게 사라지고 나타나는 효과

```kotlin
transitionState.transitionTo(
    screenIndex = 1,
    type = TransitionType.FADE,
    durationMillis = 500
)
```

### 2. 슬라이드 전환 (SLIDE_LEFT, SLIDE_RIGHT, SLIDE_UP, SLIDE_DOWN)
화면이 특정 방향으로 슬라이드하는 효과

```kotlin
// 왼쪽으로 슬라이드
transitionState.transitionTo(
    screenIndex = 1,
    type = TransitionType.SLIDE_LEFT,
    durationMillis = 500
)

// 위로 슬라이드
transitionState.transitionTo(
    screenIndex = 2,
    type = TransitionType.SLIDE_UP,
    durationMillis = 500
)
```

### 3. 확대/축소 전환 (SCALE)
화면이 확대되거나 축소되면서 전환

```kotlin
transitionState.transitionTo(
    screenIndex = 1,
    type = TransitionType.SCALE,
    durationMillis = 500
)
```

### 4. 크로스 페이드 (CROSS_FADE)
두 화면이 동시에 페이드되면서 교체

```kotlin
transitionState.transitionTo(
    screenIndex = 1,
    type = TransitionType.CROSS_FADE,
    durationMillis = 500
)
```

---

## 화면 전환 관리

### ScreenTransitionManager 사용

여러 화면을 관리하고 전환할 수 있습니다.

```kotlin
@Composable
fun MyApp() {
    val transitionState = rememberScreenTransitionState()
    val coroutineScope = rememberCoroutineScope()
    
    // 화면 리스트 정의
    val screens = listOf<@Composable () -> Unit>(
        { Screen1() },
        { Screen2() },
        { Screen3() }
    )
    
    // 화면 전환 관리
    ScreenTransitionManager(
        currentScreen = transitionState.currentScreen,
        transitionType = transitionState.transitionType,
        durationMillis = 500,
        screens = screens
    )
}
```

---

## 고급 사용법

### 1. 개별 전환 컴포넌트 사용

특정 컴포넌트에만 전환 효과를 적용할 수 있습니다.

```kotlin
@Composable
fun MyComponent() {
    var visible by remember { mutableStateOf(true) }
    
    // 페이드 전환
    FadeTransition(visible = visible, durationMillis = 500) {
        Text("페이드 효과가 적용된 텍스트")
    }
    
    // 슬라이드 전환
    SlideTransition(
        visible = visible,
        direction = TransitionType.SLIDE_LEFT,
        durationMillis = 500
    ) {
        Box { /* 내용 */ }
    }
    
    // 확대/축소 전환
    ScaleTransition(visible = visible, durationMillis = 500) {
        Image(...)
    }
}
```

### 2. 페이드 오버레이

화면 전체를 덮는 페이드 효과

```kotlin
@Composable
fun ScreenWithOverlay() {
    var showOverlay by remember { mutableStateOf(false) }
    
    Box {
        // 메인 콘텐츠
        MainContent()
        
        // 페이드 오버레이
        FadeOverlay(
            visible = showOverlay,
            color = Color.Black.copy(alpha = 0.7f),
            durationMillis = 300
        )
    }
}
```

### 3. 전환 진행률 추적

```kotlin
@Composable
fun TransitionWithProgress() {
    val transitionState = rememberScreenTransitionState()
    val progress = rememberTransitionProgress(
        isTransitioning = transitionState.isTransitioning,
        durationMillis = 500
    )
    
    // progress 값(0f~1f)을 활용한 커스텀 애니메이션
    Box(
        modifier = Modifier
            .alpha(1f - progress)
            .scale(1f - progress * 0.2f)
    ) {
        // 내용
    }
}
```

---

## 예제

### 예제 1: 간단한 대화 시스템

```kotlin
@Composable
fun DialogueScreen() {
    val transitionState = rememberScreenTransitionState()
    val coroutineScope = rememberCoroutineScope()
    
    val dialogues = listOf(
        "안녕하세요!" to "첫 번째 대사",
        "반갑습니다!" to "두 번째 대사",
        "감사합니다!" to "세 번째 대사"
    )
    
    val screens = dialogues.mapIndexed { index, (name, text) ->
        @Composable {
            SetBackground(imageName = "bg_image") {
                Bubble(
                    name = name,
                    content = text,
                    onClick = {
                        coroutineScope.launch {
                            val nextIndex = (index + 1) % dialogues.size
                            transitionState.transitionTo(
                                screenIndex = nextIndex,
                                type = TransitionType.FADE,
                                durationMillis = 500
                            )
                        }
                    }
                )
            }
        }
    }
    
    ScreenTransitionManager(
        currentScreen = transitionState.currentScreen,
        screens = screens
    )
}
```

### 예제 2: 다양한 전환 효과 사용

```kotlin
@Composable
fun MultiTransitionScreen() {
    val transitionState = rememberScreenTransitionState()
    val coroutineScope = rememberCoroutineScope()
    
    val screens = listOf<@Composable () -> Unit>(
        {
            Screen1 {
                coroutineScope.launch {
                    transitionState.transitionTo(1, TransitionType.FADE)
                }
            }
        },
        {
            Screen2 {
                coroutineScope.launch {
                    transitionState.transitionTo(2, TransitionType.SLIDE_LEFT)
                }
            }
        },
        {
            Screen3 {
                coroutineScope.launch {
                    transitionState.transitionTo(3, TransitionType.SCALE)
                }
            }
        },
        {
            Screen4 {
                coroutineScope.launch {
                    transitionState.transitionTo(0, TransitionType.SLIDE_DOWN)
                }
            }
        }
    )
    
    ScreenTransitionManager(
        currentScreen = transitionState.currentScreen,
        screens = screens
    )
}
```

### 예제 3: 조건부 화면 전환

```kotlin
@Composable
fun ConditionalTransitionScreen() {
    val transitionState = rememberScreenTransitionState()
    val coroutineScope = rememberCoroutineScope()
    var userChoice by remember { mutableStateOf("") }
    
    val screens = listOf<@Composable () -> Unit>(
        {
            // 선택 화면
            SelectionScreen { choice ->
                userChoice = choice
                coroutineScope.launch {
                    val nextScreen = when (choice) {
                        "A" -> 1
                        "B" -> 2
                        else -> 3
                    }
                    transitionState.transitionTo(
                        screenIndex = nextScreen,
                        type = TransitionType.FADE
                    )
                }
            }
        },
        { ResultScreenA() },
        { ResultScreenB() },
        { ResultScreenC() }
    )
    
    ScreenTransitionManager(
        currentScreen = transitionState.currentScreen,
        screens = screens
    )
}
```

---

## 📌 주의사항

1. **코루틴 스코프**: `transitionTo` 함수는 suspend 함수이므로 코루틴 스코프 내에서 호출해야 합니다.
2. **전환 중 클릭 방지**: `transitionState.isTransitioning`을 확인하여 전환 중에는 클릭을 무시할 수 있습니다.
3. **메모리 관리**: 화면 리스트가 많을 경우 메모리 사용량에 주의하세요.

---

## 🎨 커스터마이징

### 전환 속도 조절

```kotlin
// 빠른 전환 (300ms)
transitionState.transitionTo(1, TransitionType.FADE, durationMillis = 300)

// 느린 전환 (1000ms)
transitionState.transitionTo(1, TransitionType.FADE, durationMillis = 1000)
```

### 전환 중 상태 확인

```kotlin
if (transitionState.isTransitioning) {
    // 전환 중일 때의 처리
    CircularProgressIndicator()
} else {
    // 전환이 완료되었을 때의 처리
    Button(onClick = { /* ... */ })
}
```

---

## 🔗 관련 문서

- [Bubble (말풍선)](./Talk.md)
- [SetBackground (배경 설정)](./background.md)
- [Android Compose Animation 공식 문서](https://developer.android.com/jetpack/compose/animation)

