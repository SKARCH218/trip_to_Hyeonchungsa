# 나침반 함수 (`Compass`) 사용 안내서

안녕하세요! 이 문서는 `Compass` 함수를 사용해서 앱에서 특정 목적지를 가리키는 나침반을 만드는 방법을 알려주는 안내서입니다.

## 사용법 요약

> 1.  **화살표 이미지 준비:** 나침반 화살표로 쓰고 싶은 이미지 파일을 준비합니다.
> 2.  **폴더에 추가:** 준비한 이미지 파일을 `drawable` 폴더에 `arrow.png`라는 이름으로 넣습니다.
> 3.  **코드 작성:** `Compass` 함수를 호출하고, 가고 싶은 목적지의 위도와 경도를 입력합니다.

--- 

## 단계별 상세 설명

### 1단계: 화살표 이미지 준비하기

먼저, 나침반 화살표로 사용할 이미지(예: `arrow.png`)를 컴퓨터에 준비해주세요.

*화살표 이미지는 위쪽을 가리키는 모양이어야 합니다!*

### 2단계: 프로젝트에 이미지 추가하기

> 1.  안드로이드 스튜디오 왼쪽의 프로젝트 탐색기에서 `app` > `src` > `main` > `res` > `drawable` 폴더를 찾아주세요.
> 2.  준비한 화살표 이미지 파일을 이 `drawable` 폴더 안으로 드래그 앤 드롭해서 복사해 넣습니다.
> 3.  **중요:** 이미지 파일 이름을 반드시 `arrow.png`로 변경해주세요!

    *이제 프로젝트에서 이 화살표 이미지를 나침반으로 사용할 수 있게 되었습니다!*

### 3단계: 코드로 나침반 설정하기

이제 코드에서 `Compass` 함수를 사용할 차례입니다.

아래 예시 코드처럼 `Compass` 함수를 호출하고, 가고 싶은 **목적지의 위도**와 **경도**를 입력해주세요.

**예시 코드:**

만약 **서울 시청**으로 가는 나침반을 만들고 싶다면, 아래와 같이 코드를 작성합니다.

```kotlin
// 화면을 구성하는 Composable 함수 안에서...

// 서울 시청 (위도: 37.5665, 경도: 126.9780)을 가리키는 나침반
Compass(37.5665, 126.9780)
```

**다른 예시:**

```kotlin
// 현충사 (위도: 36.7772, 경도: 127.1470)을 가리키는 나침반
Compass(36.7772, 127.1470)

// 부산역 (위도: 35.1155, 경도: 129.0416)을 가리키는 나침반
Compass(35.1155, 129.0416)
```

---

## 함수 설명

### `Compass(위도, 경도)`

**파라미터:**
- **첫 번째 숫자 (위도):** 목적지의 위도 값 (예: 37.5665)
- **두 번째 숫자 (경도):** 목적지의 경도 값 (예: 126.9780)

**동작 방식:**
> 1. 스마트폰의 GPS로 현재 위치를 자동으로 찾습니다.
> 2. 스마트폰의 센서로 현재 바라보는 방향을 감지합니다.
> 3. 화살표 이미지가 목적지 방향을 정확하게 가리킵니다.
> 4. 스마트폰을 돌리면 화살표도 따라 움직여서 항상 목적지를 가리킵니다!

---

## 위도/경도 찾는 방법

목적지의 위도와 경도를 모르시나요? 아래 방법으로 쉽게 찾을 수 있습니다:

> 1. **구글 지도** 앱이나 웹사이트에 접속합니다.
> 2. 원하는 장소를 검색하거나 지도에서 길게 누릅니다.
> 3. 화면 하단에 나타나는 좌표 정보를 확인합니다.
>    - 예: `37.5665, 126.9780`
>    - 왼쪽 숫자가 위도, 오른쪽 숫자가 경도입니다!

---

## 필요한 권한

이 나침반 함수는 다음 권한이 필요합니다:

- **위치 권한 (GPS):** 현재 위치를 알기 위해 필요합니다.
- **센서 권한:** 스마트폰이 바라보는 방향을 감지하기 위해 필요합니다.

*처음 앱을 실행하면 자동으로 위치 권한을 요청하니 "허용"을 눌러주세요!*

### 권한 설정 방법 (AndroidManifest.xml)

프로젝트에서 위치 권한을 사용하려면, `AndroidManifest.xml` 파일에 권한을 추가해야 합니다.

**파일 위치:**  
`app` > `src` > `main` > `AndroidManifest.xml`

**추가할 코드:**

`AndroidManifest.xml` 파일을 열고, `<manifest>` 태그 안에 다음 코드를 추가하세요:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- 위치 권한 추가 -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    
    <application
        ...
    </application>
</manifest>
```

**설명:**
- `ACCESS_FINE_LOCATION`: GPS를 이용한 정밀한 위치 정보를 받습니다.
- `ACCESS_COARSE_LOCATION`: 네트워크를 이용한 대략적인 위치 정보를 받습니다.

**완료 예시:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.YourApp">
        
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
    </application>

</manifest>
```

이제 앱을 실행하면 자동으로 위치 권한을 요청하게 됩니다!

---

## 주의사항

⚠️ **이미지 파일 이름:** 화살표 이미지는 반드시 `arrow.png` 또는 `arrow.jpg`로 저장해야 합니다.

⚠️ **화살표 방향:** 이미지 속 화살표는 위쪽(↑)을 가리키고 있어야 정확하게 작동합니다.

⚠️ **GPS 필수:** 나침반이 작동하려면 GPS가 켜져 있어야 하고, 위치 권한이 허용되어 있어야 합니다.

---

##  끝입니다!

이제 앱을 실행해보면, 여러분이 설정한 목적지를 정확히 가리키는 나침반 화살표가 나타날 거예요! 🧭

궁금한 점이 있다면 언제든지 다시 물어보세요!
