# Quest - 퀘스트 표시 기능

## 개요
현재 액티비티 화면의 오른쪽 상단에 퀘스트를 표시하고, 클릭하면 자세한 내용을 볼 수 있는 기능입니다.

## 표시 형식
```
|퀘스트 제목|
|----------|
|퀘스트 내용|
```

## 함수

### QuestDisplay
퀘스트를 오른쪽 상단에 표시하고, 클릭하면 자세한 내용을 보여주는 Composable 함수입니다.

#### 파라미터
- `questTitle: String` - 퀘스트 제목
- `questContent: String` - 퀘스트 내용 (간단한 설명)
- `questDetailContent: String` - 텍스트 클릭 시 나올 자세한 퀘스트 내용

## 사용 예시

### 기본 사용법
```kotlin
@Composable
fun MyScreen() {
    QuestDisplay(
        questTitle = "첫 번째 퀘스트",
        questContent = "현충사를 방문하세요",
        questDetailContent = "현충사는 충무공 이순신 장군을 기리기 위한 사당입니다. 현충사로 이동하여 역사를 배워보세요."
    )
}
```

### 다른 UI와 함께 사용
```kotlin
@Composable
fun GameScreen() {
    QuestDisplay(
        questTitle = "유물 찾기",
        questContent = "잃어버린 유물 3개를 찾으세요 (0/3)",
        questDetailContent = """
            잃어버린 유물을 찾아주세요!
            
            1. 거북선 모형
            2. 이순신 장군의 칼
            3. 임진왜란 기록서
            
            모든 유물을 찾으면 특별한 보상을 받을 수 있습니다.
        """.trimIndent()
    )
}
```

## 동작 방식

1. **초기 상태**: 오른쪽 상단에 작은 퀘스트 카드가 표시됩니다.
   - 퀘스트 제목과 간단한 내용이 보입니다.
   - 카드는 반투명한 흰색 배경에 그림자 효과가 있습니다.

2. **클릭 시**: 전체 화면으로 자세한 퀘스트 내용이 표시됩니다.
   - Inventory.kt와 동일한 스타일의 전체 화면 모달이 나타납니다.
   - 반투명 검은색 배경 위에 흰색 카드가 표시됩니다.
   - 닫기 버튼을 클릭하면 다시 작은 퀘스트 카드로 돌아갑니다.
