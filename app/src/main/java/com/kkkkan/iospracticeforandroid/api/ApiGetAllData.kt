package com.kkkkan.iospracticeforandroid.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import java.io.Serializable

/**
 * 全てのデータを取得する
 */
fun getAllData(): Call<List<MemoData>> {
    return service.getAllData("getAllData")
}


data class MemoData(
    @SerializedName("title") var title: String,
    @SerializedName("content") var contents: List<String>
) : Serializable