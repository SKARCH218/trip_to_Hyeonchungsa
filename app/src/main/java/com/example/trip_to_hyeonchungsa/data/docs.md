# 데이터 관리 구조 문서

## 1. 개요

이 프로젝트의 데이터는 **아이템 리스트**와 **인벤토리**라는 두 가지 주요 개념으로 나뉩니다.

- **아이템 리스트 (Item List)**: 앱에 존재할 수 있는 모든 아이템의 정보를 담고 있습니다. 이 데이터는 변경되지 않는 정적 데이터(Static Data)입니다.
- **인벤토리 (Inventory)**: 사용자가 획득한 아이템의 목록입니다. 이 데이터는 사용자의 행동에 따라 변경됩니다.

이 구조는 데이터의 역할에 따라 저장 방식을 분리하여 관리의 효율성과 확장성을 높입니다.

- **아이템 리스트 저장소**: `assets/items.json` (JSON 파일)
- **인벤토리 저장소**: `AppDatabase` (Room 데이터베이스)

> **핵심 원칙**: 앱의 모든 데이터 접근은 `ItemRepository`를 통해서만 이루어져야 합니다. ViewModel이나 UI에서는 DB나 JSON 파일에 직접 접근하지 않습니다.

---

## 2. 파일 구조 및 역할

- `assets/items.json`
  - 앱에 존재하는 모든 아이템의 `id`, `name`, `lore`, `image` 정보를 정의하는 순수 데이터 파일입니다.

- `data/item.kt`
  - `items.json` 파일의 각 아이템 객체 구조와 일치하는 Kotlin `data class` 입니다. `Gson`이 JSON을 파싱할 때 이 클래스를 사용합니다.

- `data/database.kt`
  - **Room 라이브러리**를 사용한 인벤토리 데이터베이스의 모든 설정을 포함합니다.
  - `OwnedItem`: DB의 `owned_items` 테이블 구조를 정의하는 Entity 클래스. 획득한 아이템의 `itemId`만 저장합니다.
  - `InventoryDao`: DB에 데이터를 추가(`Insert`)하거나 조회(`Query`)하는 함수들을 정의한 인터페이스입니다.
  - `AppDatabase`: 데이터베이스의 전체적인 설정을 담당하는 메인 클래스입니다.

- `data/ItemRepository.kt`
  - **데이터 관리의 핵심**입니다. 앱의 다른 부분(ViewModel 등)은 이 클래스하고만 상호작용합니다.
  - `items.json`에서 전체 아이템 목록을 가져오는 로직과 `AppDatabase`에서 사용자의 인벤토리 정보를 가져오는 로직을 모두 포함하고, 이를 조합하여 의미 있는 데이터를 제공합니다.

---

## 3. 사용 방법 (How to use)

모든 데이터 관련 작업은 `ItemRepository`를 통해 수행합니다. 모든 주요 함수는 `suspend` 함수이므로 코루틴 스코프 내에서 호출해야 합니다.

### 3.1. Repository 인스턴스 생성

`Context`를 필요로 합니다. 보통 Activity, Fragment, 또는 Hilt/Koin 같은 DI 프레임워크를 통해 주입받습니다.

```kotlin
// Activity나 Composable에서
val itemRepository = ItemRepository(context)
```

### 3.2. 획득한 아이템 목록 상세 정보 가져오기

사용자가 획득한 모든 아이템의 상세 정보(`Item` 객체의 리스트)를 가져옵니다. `ItemRepository`가 내부적으로 DB에서 획득한 아이템 ID 목록을 가져온 뒤, JSON의 전체 아이템 목록과 매칭하여 반환해 줍니다.

```kotlin
// ViewModel 또는 Composable에서
LaunchedEffect(Unit) {
    val myItems: List<Item> = itemRepository.getOwnedItemsWithDetails()
    // 이제 myItems 리스트를 UI에 표시할 수 있습니다.
}
```

### 3.3. 인벤토리에 아이템 추가하기

사용자가 특정 아이템을 획득했을 때 호출합니다. 아이템의 고유 `id`만 넘겨주면 됩니다.

```kotlin
// 예: 사용자가 AR에서 2번 아이템을 찾았을 경우
suspend fun userFoundAnItem() {
    val foundItemId = 2 // '난중일기'의 ID
    itemRepository.addItemToInventory(foundItemId)
}
```
