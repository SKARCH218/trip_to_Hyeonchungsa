# 인벤토리 함수 사용 설명서

---

## 사용법

### 아이템 추가

`items.json` 파일을 통해 아이템을 추가합니다.

```json
[
  {
    "id": 1, - 무조건 숫자
    "name": "아이템 이름",
    "lore": "아이템 클릭시 나올 설명",
    "image": "아이템 이미지 이름" - 확장자 X
  },
  {
    "id": 2, - 예시
    "name": "난중일기",
    "lore": "난중일기 사본이다",
    "image": "nanjungilgi_image"
  }
]
```

---

### 함수

1. InventoryButton()

우측 상단에 인벤토리 버튼을 생성하는 함수입니다.

```kotlin
import com.example.trip_to_hyeonchungsa.tthLib.InventoryButton
```

---

2. InventoryManager()

인벤토리 아이템을 관리하는 클래스입니다.
```kotlin
import com.example.trip_to_hyeonchungsa.tthLib.InventoryManager
```

- add
   
인벤토리에 아이템을 추가하는 함수입니다.

```kotlin
InventoryManager().add(아이템ID)
```
- remove
   
인벤토리에 아이템을 제거하는 함수입니다.

```kotlin
InventoryManager().remove(아이템ID)
```

- clear

인벤토리에 있는 모든 아이템을 제거하는 함수입니다.

```kotlin
InventoryManager().clear()
```

---
   
## 예시코드

```kotlin
import com.example.trip_to_hyeonchungsa.tthLib.InventoryButton
import com.example.trip_to_hyeonchungsa.tthLib.InventoryManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                InventoryButton() // 인벤토리 버튼 - 화면 우측 상단에 플로팅 버튼으로 표시
                InventoryManager().clear() // 인벤토리 내 모든 아이템 제거
                InventoryManager().add(1) // 인벤토리 내 items.json 파일 1번 ID 아이템 추가
                InventoryManager().remove(1) // 인벤토리 내 items.json 파일 1번 ID 아이템 삭제
            }
        }
    }
}
```