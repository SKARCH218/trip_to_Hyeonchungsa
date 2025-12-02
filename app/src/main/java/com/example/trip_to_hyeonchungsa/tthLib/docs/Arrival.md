# 도착 감지 함수 (`Arrival`) 사용 안내서

안녕하세요! 이 문서는 `Arrival` 함수를 다른 파일에서 import하여 사용하는 방법을 알려주는 안내서입니다.

## 함수 위치

**파일 경로:** `app/src/main/java/com/example/arrival/MainActivity.kt`

**패키지:** `com.example.arrival`

## 사용하는 방법

### 함수 호출하기

```kotlin
fun checkArrival() {
    CoroutineScope(Dispatchers.Main).launch {
        val arrived = Arrival(
            context = this@MyActivity,  // 현재 Activity의 Context
            targetLatitude = 37.5665,    // 목적지 위도
            targetLongitude = 126.9780   // 목적지 경도
        )
        
        when (arrived) {
            true -> println("목적지에 도착했습니다!")
            false -> println("아직 도착하지 않았습니다.")
            null -> println("위치를 가져올 수 없습니다.")
        }
    }
}
```

### 실전 예시

**예시 1: 버튼 클릭 시 도착 확인**

```kotlin
Button(onClick = {
    CoroutineScope(Dispatchers.Main).launch {
        val result = Arrival(this@MainActivity, 37.5665, 126.9780)
        if (result == true) {
            showNotification("도착!")
        }
    }
}) {
    Text("도착 확인")
}
```

**예시 2: ViewModel에서 사용**

```kotlin
class MyViewModel(private val context: Context) : ViewModel() {
    
    fun checkDestination() {
        viewModelScope.launch {
            val isArrived = Arrival(context, 37.5665, 126.9780)
            // 결과 처리
        }
    }
}
```

## 함수 정보

**함수 기능:**
- 위치 권한 자동 확인
- GPS로 현재 위치 자동 측정
- Haversine 공식으로 정확한 거리 계산
- 도착 여부 자동 판단

**파라미터:**
- `context`: Android Context (필수)
- `targetLatitude`: 목적지 위도 (필수)
- `targetLongitude`: 목적지 경도 (필수)
- `thresholdMeters`: 도착 거리 기준 (선택, 기본값: 10m)

**반환값:**
- `true`: 도착 (목적지 10m 이내)
- `false`: 미도착 (목적지 10m 초과)
- `null`: 위치 권한 없음 또는 GPS 오류

##  끝입니다!

궁금한 점이 있다면 언제든지 다시 물어보세요!
