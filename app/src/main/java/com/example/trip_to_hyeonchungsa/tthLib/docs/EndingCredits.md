# EndingCredits - 엔딩 크레딧 라이브러리

화면 하단에서 상단으로 텍스트가 올라가는 엔딩 크레딧 애니메이션을 구현할 수 있는 라이브러리입니다.

## 📋 목차
- [기본 사용법](#기본-사용법)
- [파라미터 설명](#파라미터-설명)

---

## 기본 사용법

### 1. 간단한 엔딩 크레딧

```kotlin
@Composable
fun MyEndingScreen() {
    EndingCredits(
        text = """
            게임 제작
            
            기획
            홍길동
            
            프로그래밍
            김개발
            
            디자인
            이디자인
            
            음악
            박음악
            
            감사합니다!
        """.trimIndent(),
        speed = 50f,
        fontSize = 24
    )
}
```

### 2. 완료 콜백 사용

```kotlin
@Composable
fun MyEndingScreen() {
    var showMenu by remember { mutableStateOf(false) }
    
    if (!showMenu) {
        EndingCredits(
            text = "엔딩 크레딧 내용...",
            speed = 50f,
            fontSize = 20,
            onFinished = {
                // 크레딧이 끝나면 메뉴 표시
                showMenu = true
            }
        )
    } else {
        MainMenuScreen()
    }
}
```

---

## 파라미터 설명

### EndingCredits 함수

| 파라미터 | 타입 | 기본값 | 설명 |
|---------|------|--------|------|
| `text` | String | (필수) | 표시할 크레딧 텍스트. `\n`으로 줄바꿈 |
| `speed` | Float | 50f | 크레딧이 올라가는 속도 (픽셀/초) |
| `fontSize` | Int | 20 | 텍스트 크기 (sp 단위) |
| `textColor` | Color | Color.White | 텍스트 색상 |
| `onFinished` | (() -> Unit)? | null | 크레딧이 모두 올라간 후 호출되는 콜백 |

### 속도 가이드라인

- **매우 느림**: 20-30 픽셀/초
- **느림**: 40-50 픽셀/초 (기본값)
- **보통**: 60-80 픽셀/초
- **빠름**: 90-120 픽셀/초
- **매우 빠름**: 130+ 픽셀/초

### 폰트 크기 가이드라인

- **작은 크기**: 14-18sp
- **중간 크기**: 20-24sp (기본값)
- **큰 크기**: 26-32sp
- **매우 큰 크기**: 34sp 이상

---

## ⚠️ 주의사항

1. `text` 파라미터는 필수입니다.
2. `speed`가 너무 빠르면(150+) 텍스트를 읽기 어려울 수 있습니다.
3. `fontSize`가 너무 크면(40+) 화면을 벗어날 수 있습니다.

---

## 📝 버전 정보

- **Version**: 1.0.0
- **Last Updated**: 2026-01-06
- **Author**: TTH Studio

