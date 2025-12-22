# MoneyManager

## 개요
`MoneyManager`는 앱 내에서 플레이어의 돈을 관리하기 위한 객체입니다. Room 데이터베이스를 사용하여 돈 정보를 영구적으로 저장하고 관리합니다.

## 초기화
초기화가 필요 없습니다! 바로 사용하면 됩니다.

```kotlin
// 바로 사용!
MoneyManager.addMoney(100)
```

## 주요 함수

### addMoney(amount: Int, onComplete: (() -> Unit)?)
돈을 추가하는 함수입니다.

**파라미터:**
- `amount`: 추가할 금액 (양수)
- `onComplete`: 완료 시 실행할 콜백 (선택사항)

**사용 예시:**
```kotlin
// 기본 사용
MoneyManager.addMoney(100) // 100원 추가

// 완료 콜백 사용
MoneyManager.addMoney(100) {
    println("100원이 추가되었습니다!")
}
```

### removeMoney(amount: Int, onSuccess: (() -> Unit)?, onFailure: (() -> Unit)?)
돈을 차감하는 함수입니다.

**파라미터:**
- `amount`: 차감할 금액 (양수)
- `onSuccess`: 차감 성공 시 실행할 콜백 (선택사항)
- `onFailure`: 돈이 부족할 때 실행할 콜백 (선택사항)

**사용 예시:**
```kotlin
// 기본 사용
MoneyManager.removeMoney(50)

// 성공/실패 콜백 사용
MoneyManager.removeMoney(
    amount = 50,
    onSuccess = {
        println("50원이 차감되었습니다!")
    },
    onFailure = {
        println("돈이 부족합니다!")
    }
)
```

### resetMoney(onComplete: (() -> Unit)?)
돈을 0으로 초기화하는 함수입니다.

**파라미터:**
- `onComplete`: 완료 시 실행할 콜백 (선택사항)

**사용 예시:**
```kotlin
// 기본 사용
MoneyManager.resetMoney()

// 완료 콜백 사용
MoneyManager.resetMoney {
    println("돈이 초기화되었습니다!")
}
```

### getMoney(callback: (Int) -> Unit)
현재 보유한 돈을 조회하는 함수입니다 (비동기).

**파라미터:**
- `callback`: 현재 금액을 받을 콜백

**사용 예시:**
```kotlin
MoneyManager.getMoney { amount ->
    println("현재 보유 금액: ${amount}원")
}
```

### getMoneySync(): Int
현재 보유한 돈을 동기적으로 조회하는 함수입니다.

**반환값:**
- 현재 보유 금액 (Int)

**사용 예시:**
```kotlin
// 변수에 바로 할당 가능!
val money = MoneyManager.getMoneySync()
println("현재 보유 금액: ${money}원")

// if문에서 사용
if (MoneyManager.getMoneySync() >= 100) {
    println("돈이 충분합니다!")
}
```

**⚠️ 주의사항:**
- 이 함수는 블로킹 함수이므로 메인 스레드에서 호출 시 UI가 잠시 멈출 수 있습니다.
- 가능하면 `getMoney(callback)` 함수 사용을 권장합니다.
- 하지만 간단한 조회의 경우 충분히 사용 가능합니다.

## 전체 사용 예시

### 비동기 방식 (권장)
```kotlin
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 현재 돈 확인
        MoneyManager.getMoney { money ->
            println("초기 금액: $money")
        }
        
        // 돈 추가
        MoneyManager.addMoney(1000) {
            println("1000원 추가 완료!")
            
            // 추가 후 확인
            MoneyManager.getMoney { money ->
                println("현재 금액: $money")
            }
        }
        
        // 돈 차감
        MoneyManager.removeMoney(
            amount = 300,
            onSuccess = {
                println("300원 차감 성공!")
                MoneyManager.getMoney { money ->
                    println("현재 금액: $money")
                }
            },
            onFailure = {
                println("돈이 부족합니다!")
            }
        )
        
        // 돈 초기화
        MoneyManager.resetMoney {
            println("돈 초기화 완료!")
        }
    }
}
```

### 동기 방식 (간단한 조회)
```kotlin
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 변수에 바로 할당!
        val currentMoney = MoneyManager.getMoneySync()
        println("현재 보유 금액: ${currentMoney}원")
        
        // if문에서 바로 사용
        if (MoneyManager.getMoneySync() >= 100) {
            println("돈이 충분합니다!")
            MoneyManager.removeMoney(100)
        } else {
            println("돈이 부족합니다!")
        }
    }
}
```
