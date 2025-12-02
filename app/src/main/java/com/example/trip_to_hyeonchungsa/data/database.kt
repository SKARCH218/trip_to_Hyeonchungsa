package com.example.trip_to_hyeonchungsa.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

// Entity: 'owned_items' 테이블의 구조를 정의합니다.
@Entity(tableName = "owned_items")
data class OwnedItem(
    @PrimaryKey
    val itemId: Int
)

// DAO: 데이터베이스와 상호작용하는 함수들(메서드)을 정의합니다.
@Dao
interface InventoryDao {
    // 인벤토리에 아이템 추가 (이미 있으면 무시)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItem(ownedItem: OwnedItem)

    // 획득한 모든 아이템의 ID 목록을 가져옴
    @Query("SELECT * FROM owned_items")
    suspend fun getOwnedItems(): List<OwnedItem>

    // 인벤토리에서 아이템 제거
    @Query("DELETE FROM owned_items WHERE itemId = :itemId")
    suspend fun removeItem(itemId: Int)

    // 인벤토리의 모든 아이템 제거
    @Query("DELETE FROM owned_items")
    suspend fun clearInventory()
}

// Database: Room 데이터베이스의 메인 클래스입니다.
@Database(entities = [OwnedItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
