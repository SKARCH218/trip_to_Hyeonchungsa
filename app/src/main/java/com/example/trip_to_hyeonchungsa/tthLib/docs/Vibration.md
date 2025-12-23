# 진동 기능 함수 (`Vibration`) 사용 안내서

안녕하세요! 이 문서는 `Vibration` 함수를 사용해서 앱에서 핸드폰 진동을 울리는 방법을 알려주는 안내서입니다.

## 사용법 요약

1.  **코드에서 함수 호출:** `Vibration(강도)` 형태로 함수를 호출합니다.
2.  **강도 선택:** 1부터 10까지의 숫자로 진동 강도를 선택합니다.
3.  **실제 기기에서 테스트:** 에뮬레이터가 아닌 실제 안드로이드 기기에서 테스트합니다.

---

## 단계별 상세 설명

### 0단계: 권한 설정 확인하기

진동 기능을 사용하려면 안드로이드 앱에 권한이 필요합니다.

**필수 권한:**
```xml
<uses-permission android:name="android.permission.VIBRATE" />
```

**설정 위치:**
`app/src/main/AndroidManifest.xml` 파일의 `<manifest>` 태그 안에 위 권한을 추가해야 합니다.

**예시:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        ...
    </application>

</manifest>
```

> ✅ **이미 권한이 추가되어 있다면 이 단계는 건너뛰세요!**

### 1단계: 함수 이해하기

`Vibration` 함수는 핸드폰의 진동 모터를 작동시키는 함수입니다.

**파라미터:**
-   `intensity` (강도): 1부터 10까지의 정수
    -   **1**: 가장 약한 진동 (100ms, 약한 세기)
    -   **5**: 중간 강도 진동 (500ms, 중간 세기)
    -   **10**: 가장 강한 진동 (1000ms, 최대 세기)

### 2단계: 기본 사용 예시

버튼을 클릭했을 때 진동을 울리는 예시입니다.

**예시 코드:**

```kotlin
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column

@Composable
fun MyScreen(activity: MainActivity) {
    Column {
        // 약한 진동 버튼
        Button(onClick = {
            activity.Vibration(3)
        }) {
            Text("약한 진동")
        }
        
        // 중간 진동 버튼
        Button(onClick = {
            activity.Vibration(5)
        }) {
            Text("중간 진동")
        }
        
        // 강한 진동 버튼
        Button(onClick = {
            activity.Vibration(10)
        }) {
            Text("강한 진동")
        }
    }
}
```

### 3단계: MainActivity에서 직접 사용하기

`MainActivity` 내부에서 함수를 호출하는 경우, `this.Vibration()` 또는 그냥 `Vibration()`으로 사용할 수 있습니다.

**예시 코드:**

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Button(onClick = {
                            // 진동 실행
                            Vibration(7)
                        }) {
                            Text("진동 테스트")
                        }
                    }
                }
            }
        }
    }
}
```

### 4단계: 다양한 활용 예시

#### 알림이 왔을 때
```kotlin
// 메시지가 도착했을 때 짧은 진동
fun onMessageReceived() {
    Vibration(2)  // 짧고 약한 진동
}
```

#### 에러가 발생했을 때
```kotlin
// 에러 발생 시 강한 진동으로 경고
fun onError() {
    Vibration(10)  // 강한 진동으로 주의를 끕니다
}
```

#### 게임에서 충돌했을 때
```kotlin
// 게임 캐릭터가 부딪혔을 때
fun onCollision(collisionStrength: Int) {
    Vibration(collisionStrength)  // 충돌 강도에 따라 진동
}
```

#### 터치 피드백
```kotlin
// 화면을 터치했을 때 가벼운 진동
Box(
    modifier = Modifier
        .clickable {
            Vibration(1)  // 아주 약한 진동
            // 다른 동작...
        }
) {
    Text("터치해보세요")
}
```

---

## 중요한 참고 사항

### ✅ 권한 설정 완료
이미 `AndroidManifest.xml` 파일에 진동 권한(`VIBRATE`)이 추가되어 있으므로, 별도로 권한을 설정할 필요가 없습니다.

```xml
<uses-permission android:name="android.permission.VIBRATE" />
```

### 📱 실제 기기에서 테스트하세요
-   **에뮬레이터에서는 진동이 작동하지 않습니다!**
-   반드시 실제 안드로이드 기기에 앱을 설치해서 테스트해야 합니다.
-   USB로 연결하거나 APK를 빌드해서 설치하면 됩니다.

### 🔊 무음 모드 / 진동 모드
-   기기의 진동 설정과 무관하게 작동합니다.
-   무음 모드에서도 진동이 울립니다.

### ⚙️ 안드로이드 버전 호환성
이 함수는 모든 안드로이드 버전에서 작동합니다:
-   **Android 12 이상**: `VibratorManager` 사용
-   **Android 8.0 ~ 11**: `VibrationEffect` 사용
-   **Android 7.1 이하**: 기본 `Vibrator` 사용

---

## 진동 강도 가이드

| 강도 | 지속 시간 | 권장 용도 |
|------|----------|----------|
| 1 | 100ms | 터치 피드백, 미세한 반응 |
| 2-3 | 200-300ms | 버튼 클릭, 가벼운 알림 |
| 4-6 | 400-600ms | 일반 알림, 메시지 수신 |
| 7-8 | 700-800ms | 중요한 알림, 경고 |
| 9-10 | 900-1000ms | 긴급 알림, 강력한 피드백 |

---

## 문제 해결

### Q: 진동이 울리지 않아요!
**A:** 다음을 확인해주세요:
1. 실제 안드로이드 기기에서 테스트하고 있나요? (에뮬레이터 X)
2. 기기의 배터리 절약 모드가 켜져 있나요?
3. 기기 설정에서 진동이 활성화되어 있나요?

### Q: 진동이 너무 약해요!
**A:** 강도를 8~10으로 높여보세요: `Vibration(10)`

### Q: 연속으로 진동을 울리고 싶어요!
**A:** 반복문이나 지연을 사용하면 됩니다:
```kotlin
Button(onClick = {
    // 3번 연속 진동
    repeat(3) {
        Vibration(5)
        Thread.sleep(500)  // 0.5초 대기
    }
}) {
    Text("연속 진동")
}
```

---

## 끝입니다!

이제 앱에서 진동 기능을 자유롭게 활용할 수 있습니다.
사용자 경험을 향상시키는 멋진 기능을 만들어보세요!

궁금한 점이 있다면 언제든지 다시 물어보세요! 🎉
