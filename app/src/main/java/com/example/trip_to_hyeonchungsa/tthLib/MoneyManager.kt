package com.example.trip_to_hyeonchungsa.tthLib

import com.example.trip_to_hyeonchungsa.data.AppDatabase
import com.example.trip_to_hyeonchungsa.data.Money
import com.example.trip_to_hyeonchungsa.tthLib.context.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 돈 관리 객체 (코루틴 스코프 불필요) ⭐ 가장 간편함!
 *
 * Context와 코루틴 스코프 없이 바로 사용 가능합니다.
 * 내부적으로 자동으로 비동기 처리됩니다.
 *
 * 사용 예시:
 * ```
 * // 코루틴 없이 바로 사용!
 * MoneyManager.addMoney(100)
 * MoneyManager.removeMoney(50)
 * MoneyManager.resetMoney()
 *
 * // 현재 돈 조회 (콜백 사용)
 * MoneyManager.getMoney { amount ->
 *     println("현재 보유 금액: ${amount}원")
 * }
 *
 * // 현재 돈 조회 (동기 방식)
 * val money = MoneyManager.getMoneySync()
 * println("현재 보유 금액: ${money}원")
 * ```
 */
object MoneyManager {
    private fun getMoneyDao() = AppDatabase.getDatabase(App.getContext()).moneyDao()

    /**
     * 돈을 추가하는 함수
     * @param amount 추가할 금액 (양수)
     * @param onComplete 완료 시 실행할 콜백 (선택사항)
     */
    fun addMoney(amount: Int, onComplete: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val moneyDao = getMoneyDao()
                val currentMoney = moneyDao.getMoney()
                val currentAmount = currentMoney?.amount ?: 0
                val newAmount = (currentAmount + amount).coerceAtLeast(0) // 음수 방지
                moneyDao.saveMoney(Money(id = 1, amount = newAmount))
                onComplete?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 돈을 차감하는 함수
     * @param amount 차감할 금액 (양수)
     * @param onSuccess 차감 성공 시 실행할 콜백 (선택사항)
     * @param onFailure 돈이 부족할 때 실행할 콜백 (선택사항)
     */
    fun removeMoney(
        amount: Int,
        onSuccess: (() -> Unit)? = null,
        onFailure: (() -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val moneyDao = getMoneyDao()
                val currentMoney = moneyDao.getMoney()
                val currentAmount = currentMoney?.amount ?: 0

                if (currentAmount >= amount) {
                    val newAmount = currentAmount - amount
                    moneyDao.saveMoney(Money(id = 1, amount = newAmount))
                    CoroutineScope(Dispatchers.Main).launch {
                        onSuccess?.invoke()
                    }
                } else {
                    // 돈이 부족함
                    CoroutineScope(Dispatchers.Main).launch {
                        onFailure?.invoke()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                CoroutineScope(Dispatchers.Main).launch {
                    onFailure?.invoke()
                }
            }
        }
    }

    /**
     * 돈을 0으로 초기화하는 함수
     * @param onComplete 완료 시 실행할 콜백 (선택사항)
     */
    fun resetMoney(onComplete: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val moneyDao = getMoneyDao()
                moneyDao.saveMoney(Money(id = 1, amount = 0))
                onComplete?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 현재 돈을 조회하는 함수
     * @param callback 현재 금액을 받을 콜백
     */
    fun getMoney(callback: (Int) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val moneyDao = getMoneyDao()
                val money = moneyDao.getMoney()
                val amount = money?.amount ?: 0
                CoroutineScope(Dispatchers.Main).launch {
                    callback(amount)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                CoroutineScope(Dispatchers.Main).launch {
                    callback(0)
                }
            }
        }
    }

    /**
     * 현재 돈을 동기적으로 조회하는 함수
     * @return 현재 보유 금액
     *
     * ⚠️ 주의: 이 함수는 블로킹 함수이므로 메인 스레드에서 호출 시 UI가 잠시 멈출 수 있습니다.
     * 가능하면 getMoney(callback) 함수 사용을 권장합니다.
     */
    fun getMoneySync(): Int {
        return runBlocking(Dispatchers.IO) {
            try {
                val moneyDao = getMoneyDao()
                val money = moneyDao.getMoney()
                money?.amount ?: 0
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }
    }
}

