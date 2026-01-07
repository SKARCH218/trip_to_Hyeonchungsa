# 🎯 도착 감지 함수 - 초간단 사용 가이드

GPS로 목적지 도착 여부를 자동으로 확인하는 함수입니다. **복잡한 코드 없이 단 2줄로 사용 가능!**

---

## 📦 Import

```kotlin
import com.example.trip_to_hyeonchungsa.tthLib.CheckArrival
```

---

## ⚡ 가장 쉬운 사용법 (추천!)

### 1️⃣ 도착하면 자동으로 다음 화면 이동

```kotlin
@Composable
fun MyScreen(onNext: () -> Unit) {
    // 이 2줄만 추가하면 끝!
    CheckArrival(
        latitude = 36.92906,
        longitude = 127.0426,
        onArrived = { onNext() }  // 도착하면 자동으로 다음 화면
    )
    
    SetBackground(imageName = "navi") {
        QuestDisplay(
            questTitle = "현충사 기념관 방문",
            questContent = "현충사 기념관을 방문하세요"
        )
    }
}
```

**설명:**
- `latitude`, `longitude`: 목적지 좌표
- `onArrived`: 도착했을 때 실행할 코드
- 위 코드만으로 GPS 확인 + 자동 화면 전환 완성!

---

### 2️⃣ 도착 여부에 따라 다른 UI 표시

```kotlin
import com.example.trip_to_hyeonchungsa.tthLib.rememberArrivalState

@Composable
fun MyScreen() {
    val isArrived = rememberArrivalState(36.92906, 127.0426)
    
    when (isArrived.value) {
        true -> Text("✅ 도착했습니다!")
        false -> Text("📍 목적지로 이동하세요")
        null -> Text("⚠️ 위치를 가져올 수 없습니다")
    }
}
```

---

## 📚 더 많은 예시

### 예시 1: 도착 거리 조정 (10m → 50m)

```kotlin
CheckArrival(
    latitude = 36.92906,
    longitude = 127.0426,
    thresholdMeters = 50.0,  // 50m 이내면 도착
    onArrived = { onNext() }
)
```

### 예시 2: 도착/미도착/오류 모두 처리

```kotlin
CheckArrival(
    latitude = 36.92906,
    longitude = 127.0426,
    onArrived = {
        println("✅ 도착!")
    },
    onNotArrived = {
        println("❌ 아직 멀어요")
    },
    onError = {
        println("⚠️ GPS 오류")
    }
)
```

### 예시 3: 도착해야만 버튼 활성화

```kotlin
@Composable
fun QuestScreen(onNext: () -> Unit) {
    val isArrived = rememberArrivalState(36.92906, 127.0426)
    
    Column {
        if (isArrived.value == true) {
            Text("✅ 도착 완료!")
        } else {
            Text("📍 목적지로 이동 중...")
        }
        
        Button(
            onClick = onNext,
            enabled = isArrived.value == true  // 도착해야만 활성화
        ) {
            Text("다음")
        }
    }
}
```

---

## 🔧 파라미터 설명

### CheckArrival

| 파라미터 | 필수? | 기본값 | 설명 |
|---------|------|--------|------|
| `latitude` | ✅ 필수 | - | 목적지 위도 |
| `longitude` | ✅ 필수 | - | 목적지 경도 |
| `thresholdMeters` | ❌ 선택 | 10.0 | 도착 판정 거리 (미터) |
| `onArrived` | ❌ 선택 | {} | 도착 시 실행 |
| `onNotArrived` | ❌ 선택 | {} | 미도착 시 실행 |
| `onError` | ❌ 선택 | {} | 오류 시 실행 |

### rememberArrivalState

| 파라미터 | 필수? | 기본값 | 설명 |
|---------|------|--------|------|
| `latitude` | ✅ 필수 | - | 목적지 위도 |
| `longitude` | ✅ 필수 | - | 목적지 경도 |
| `thresholdMeters` | ❌ 선택 | 10.0 | 도착 판정 거리 |

**반환값:** `State<Boolean?>`
- `true` = 도착
- `false` = 미도착
- `null` = 오류 (권한 없음 또는 GPS 꺼짐)

---

## 💡 꿀팁

### 테스트할 때
```kotlin
// 거리를 1000m로 설정하면 어디서든 테스트 가능!
CheckArrival(
    latitude = 36.92906,
    longitude = 127.0426,
    thresholdMeters = 1000.0,  // 1km
    onArrived = { onNext() }
)
```

### 실내에서 사용할 때
```kotlin
// 실내는 GPS 정확도가 떨어지니 30~50m 추천
CheckArrival(
    latitude = 36.92906,
    longitude = 127.0426,
    thresholdMeters = 50.0,  // 50m
    onArrived = { onNext() }
)
```

---

## ⚠️ 주의사항

1. **위치 권한 필요**: `AndroidManifest.xml`에 권한 추가 필요
   ```xml
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   ```

2. **GPS 켜기**: 기기의 위치 서비스 활성화 필요

3. **실외 사용 권장**: GPS는 실내에서 정확도가 떨어짐

---

## 🆘 문제 해결

### "위치를 가져올 수 없습니다" 오류
1. 위치 권한 확인
2. GPS가 켜져 있는지 확인
3. 실외로 이동해보기
4. `thresholdMeters`를 크게 설정 (예: 100m)

### 도착했는데도 반응 없음
- `thresholdMeters` 값을 크게 설정 (예: 50m)
- GPS 정확도 확인 (실내는 부정확함)

---

## 🎉 완성!

이제 GPS 도착 감지 기능을 쉽게 사용할 수 있습니다!
궁금한 점이 있으면 언제든 질문하세요! 😊
