package com.example.trip_to_hyeonchungsa.data

import com.google.gson.annotations.SerializedName

// items.json 파일의 구조와 일치하는 데이터 클래스
data class Item(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("lore")
    val lore: String,

    @SerializedName("image")
    val image: String
)
