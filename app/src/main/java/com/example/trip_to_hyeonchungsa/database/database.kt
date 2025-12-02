package com.example.trip_to_hyeonchungsa.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object InventoryContract {
    object Entry : BaseColumns {
        const val TABLE_NAME = "inventory"
        const val COLUMN_NAME_ITEM_NAME = "item_name"
    }
}

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${InventoryContract.Entry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${InventoryContract.Entry.COLUMN_NAME_ITEM_NAME} TEXT UNIQUE NOT NULL)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${InventoryContract.Entry.TABLE_NAME}"

class InventoryDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Inventory.db"
    }
}
