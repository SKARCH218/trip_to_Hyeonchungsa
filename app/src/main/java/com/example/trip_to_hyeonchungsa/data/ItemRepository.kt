package com.example.trip_to_hyeonchungsa.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

// 데이터 관리를 총괄하는 저장소 (Repository)
class ItemRepository(private val context: Context) {

    // Room 데이터베이스의 DAO 인스턴스
    private val inventoryDao = AppDatabase.getDatabase(context).inventoryDao()

    // JSON에서 모든 아이템 목록을 불러오는 내부 함수
    private suspend fun getAllItemsFromJson(): List<Item> {
        return withContext(Dispatchers.IO) { // 파일 I/O는 IO 스레드에서 수행
            try {
                val jsonString = context.assets.open("items.json").bufferedReader().use { it.readText() }
                val listType = object : TypeToken<List<Item>>() {}.type
                Gson().fromJson(jsonString, listType)
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                emptyList() // 에러 발생 시 빈 리스트 반환
            }
        }
    }

    // 획득한 아이템들의 상세 정보를 가져오는 함수
    suspend fun getOwnedItemsWithDetails(): List<Item> {
        val allItems = getAllItemsFromJson()
        val ownedItemIds = inventoryDao.getOwnedItems().map { it.itemId }.toSet()
        return allItems.filter { it.id in ownedItemIds }
    }

    // 인벤토리에 아이템을 추가하는 함수
    suspend fun addItemToInventory(itemId: Int) {
        inventoryDao.addItem(OwnedItem(itemId = itemId))
    }

    // 인벤토리에서 아이템을 제거하는 함수
    suspend fun removeItemFromInventory(itemId: Int) {
        inventoryDao.removeItem(itemId)
    }

    // 인벤토리의 모든 아이템을 제거하는 함수
    suspend fun clearInventory() {
        inventoryDao.clearInventory()
    }
}
