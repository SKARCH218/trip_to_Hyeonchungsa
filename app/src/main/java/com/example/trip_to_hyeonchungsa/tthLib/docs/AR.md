# ARFunction - Android AR 이미지 인식 라이브러리

ARCore를 사용하여 이미지를 인식하고 3D 모델을 표시하는 Android Compose 함수입니다.  
**단 하나의 함수**로 AR 기능을 다른 프로젝트에 쉽게 통합할 수 있습니다.

## ✨ 기능

- **이미지 인식**: ARCore를 사용한 실시간 이미지 추적
- **3D 모델 렌더링**: OBJ 형식의 3D 모델을 인식된 이미지 위에 표시
- **자동 추적 관리**: 이미지가 보이지 않으면 자동으로 모델 제거
- **정확도 향상**: FULL_TRACKING 방식으로 어두운 환경에서의 오인식 방지
- **커스텀 크기**: 이미지별로 다른 모델 크기 설정 가능
- **모델 클릭 이벤트**: 화면 중앙을 터치하여 모델 클릭 감지
- **인식 제어**: 이미지 인식 일시 정지/재개 기능
- **세션 관리**: AR 세션 종료 및 모델 제거 함수 제공

## 📱 요구사항

- **Android Studio**: Hedgehog (2023.1.1) 이상
- **Android SDK**: API 24 (Android 7.0) 이상
- **ARCore 지원 기기**: [ARCore 지원 기기 목록](https://developers.google.com/ar/devices)
- **카메라 권한**: 런타임에서 자동 요청

## ARCore 이미지 데이터베이스 생성 방법

```
# 해당 링크에서 arcore-android-sdk-1.51.0.zip 파일 다운로드 후 압축 해제
https://github.com/google-ar/arcore-android-sdk/releases
```

```powershell
# PowerShell에서 실행
.\build_ar_database.ps1
```

## 📖 사용 방법

### 기본 사용법

`AugmentedImageArView` 함수 하나로 AR 기능을 사용합니다:

```kotlin
setContent {
    AugmentedImageArView(
        imageName = "image",           // 인식할 이미지 이름 (확장자 제외)
        modelPath = "models/model.obj", // 표시할 3D 모델 경로
        scale = 0.1f                   // 모델 크기 (1.0 = 원본 크기)
    )
}
```

### 모델 클릭 이벤트 처리

```kotlin
AugmentedImageArView(
    imageName = "image",
    modelPath = "models/mofel.obj",
    scale = 0.1f,
    onModelClick = { 
        // 클릭 시 실행될 코드
    }
)
```

### 새로운 이미지 추가하기

1. **이미지 파일 추가**:
   - `app/src/main/assets/augmented_images/` 폴더에 `.jpg` 또는 `.png` 파일 복사
   - 파일 이름은 영문 소문자, 숫자, 밑줄(_)만 사용 (예: `my_image.jpg`)

2. **데이터베이스 재생성**:
   ```powershell
   .\build_ar_database.ps1
   ```

### 3D 모델 추가하기

1. OBJ 형식의 3D 모델을 `app/src/main/assets/models/` 폴더에 복사

**주의**: 현재 앱은 **OBJ 파일 형식만** 지원합니다. GLB/GLTF 형식은 별도의 로더 라이브러리가 필요합니다.

## 🎮 제어 함수

AR 기능을 제어하는 독립 함수들입니다. 어디서든 호출 가능합니다.

### stopARSession()
AR 세션을 완전히 종료하고 모든 리소스를 해제합니다.

```kotlin
Button(onClick = { stopARSession() }) {
    Text("AR 종료")
}
```

**효과:**
- 모든 모델 제거 및 앵커 해제
- AR 세션 종료
- 이미지 인식 설정 초기화 (`isTrackingEnabled = true`)

**주의:** 세션 종료 후 AR을 다시 사용하려면 앱을 재시작하거나 새로운 `AugmentedImageArView`를 생성해야 합니다.

---

### clearAllModels()
현재 표시된 모든 3D 모델을 제거합니다. (AR 세션은 유지)

```kotlin
Button(onClick = { clearAllModels() }) {
    Text("모델 제거")
}
```

**효과:**
- 표시 중인 모든 모델 제거
- AR 세션 유지 (카메라 계속 작동)
- 이미지 인식이 자동으로 중단되므로 다시 인식하기 위해서는 resumeImageTracking() 함수를 사용해야 합니다.

---

### pauseImageTracking()
새로운 이미지 인식을 중단합니다. (기존 모델은 유지)

```kotlin
Button(onClick = { pauseImageTracking() }) {
    Text("인식 중단")
}

// 또는 모델 클릭 시 자동 중단
AugmentedImageArView(
    imageName = "test",
    modelPath = "models/andy.obj",
    scale = 0.1f,
    onModelClick = {
        pauseImageTracking()
        Toast.makeText(context, "인식 중단됨", Toast.LENGTH_SHORT).show()
    }
)
```

**효과:**
- 새로운 이미지 인식 중단
- 기존에 표시된 모델은 그대로 유지
- `clearAllModels()`와 조합하여 사용 가능

---

### resumeImageTracking()
이미지 인식을 재개합니다.

```kotlin
Button(onClick = { resumeImageTracking() }) {
    Text("인식 재개")
}
```

**효과:**
- 이미지 인식 다시 활성화
- 새로운 이미지를 인식할 수 있게 됨

---

## 🔧 자동화 도구

### build_ar_database.ps1

AR 이미지 데이터베이스를 자동으로 생성하는 PowerShell 스크립트입니다.

**기능**:
- `augmented_images` 폴더의 모든 이미지 스캔
- `image_list.txt` 자동 생성
- `arcoreimg.exe`를 사용하여 `.imgdb` 파일 생성
- 각 이미지의 인식 품질 점수 표시

**사용법**:
```powershell
.\build_ar_database.ps1
```

**선행 조건**:
- ARCore SDK의 `arcoreimg.exe`를 다운로드하여 프로젝트 루트 또는 PATH에 추가
- PowerShell 실행 정책 설정: `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`

## 🛠 문제 해결

### 앱이 실행되지 않음
- ARCore 지원 기기인지 확인: [지원 기기 목록](https://developers.google.com/ar/devices)
- Google Play 스토어에서 "ARCore Services" 앱 업데이트

### 이미지를 인식하지 못함
- **이미지 품질 확인**: 특징점(모서리, 패턴)이 많고 명암 대비가 뚜렷한 이미지를 사용
- **조명 환경**: 충분히 밝은 환경에서 테스트
- **데이터베이스 재생성**: `.\build_ar_database.ps1` 실행
- **품질 점수 확인**: arcoreimg 실행 시 품질 점수가 75점 이상이어야 인식률이 높음

### 검은 화면만 표시됨
- 카메라 권한 허용 확인
- 빌드 후 앱 재시작

### 어두운 곳에서 오인식 발생
- FULL_TRACKING 체크가 이미 적용되어 있어 대부분 방지됨
- 더 밝은 조명이나 더 선명한 이미지를 사용

### Gradle 빌드 오류
```powershell
# R.jar 파일 잠금 오류 시
.\gradlew --stop
# Android Studio에서 Build > Clean Project > Rebuild Project
```

### 모델이 이미지 위치에 정확히 나타나지 않음
- 이미지 중심이 모델의 기준점입니다
- `scale` 값을 조정하여 적절한 크기로 설정

## 📚 주요 API 설명

### AugmentedImageArView

AR 기능을 제공하는 Composable 함수입니다.

**파라미터**:
- `imageName: String` - 인식할 이미지 이름 (확장자 제외)
- `modelPath: String` - 렌더링할 3D 모델 경로 (assets 폴더 기준)
- `scale: Float` - 모델 크기 배율 (기본 1.0)
- `modifier: Modifier` - Compose Modifier (선택사항)

**추적 상태**:
- `TRACKING + FULL_TRACKING`: 모델 표시
- `PAUSED`: 이미지 일시적으로 안 보임, 모델 제거
- `STOPPED`: 추적 완전히 중단, 모델 제거

## 🔑 핵심 구성 요소

### AR.kt
- `AugmentedImageArView`: AR 뷰 Composable 함수
- `setupAugmentedImageDatabase`: .imgdb 파일 로드
- `handleFrame`: AR 프레임 처리 및 이미지 추적

### BackgroundRenderer.kt
- 카메라 피드를 AR 배경으로 렌더링
- External texture sampler 사용

### ObjectRenderer.kt
- OBJ 파일 로딩 (de.javagl.obj 라이브러리)
- OpenGL ES 2.0으로 3D 모델 렌더링
- 조명 및 재질 속성 처리

### DisplayRotationHelper.kt
- 기기 회전 감지 및 처리
- ARCore 세션에 디스플레이 변경 사항 전달

## 📝 라이선스

이 프로젝트는 교육 목적으로 제작되었습니다.

## 🙋 기여

이슈 및 개선 제안은 언제든지 환영합니다!

---

### 필수 설정

**build.gradle.kts (app 레벨)**에 ARCore 의존성 추가:

```kotlin
dependencies {
    implementation("com.google.ar:core:1.41.0")
    implementation("de.javagl:obj:0.4.0")
    // ... 기타 의존성
}
```

**AndroidManifest.xml**에 ARCore 및 카메라 권한 추가:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera.ar" android:required="true" />

<application>
    <meta-data android:name="com.google.ar.core" android:value="required" />
    <!-- ... -->
</application>
```

**libs.versions.toml**에 ARCore 라이브러리 및 버전 추가:

```toml
[versions]
arcore = "1.44.0"
javagl-obj = "0.4.0"

[libraries]
ar-core = { group = "com.google.ar", name = "core", version.ref = "arcore" }
javagl-obj = { group = "de.javagl", name = "obj", version.ref = "javagl-obj" }
```

**마지막 업데이트**: 2025년 11월 27일
