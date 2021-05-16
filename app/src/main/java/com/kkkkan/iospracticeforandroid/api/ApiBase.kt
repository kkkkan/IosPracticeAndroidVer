package com.kkkkan.iospracticeforandroid.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


// core for controller
val service: IApiService = create(IApiService::class.java)

/**
 * Api
 * メソッドにつけられるアノテーション
 * @GET(path)
 * 引数につけられるアノテーション
 * @Path(@Getのpathのなかで{id}のように置いた変数) その変数に入れたい値
 * @Query(URLで?をつけて付与するパラメーター名) そのパラメーターとしてURLに入れたい値
 */
interface IApiService {

    @GET("exec")
    fun getAllData(@Query("apiName") apiName: String): Call<List<MemoData>>
}

// APIのベース部分
private val API_BASE =
    "https://script.google.com/macros/s/AKfycbyQiZJ0i3Ewkz7XdArZGHhpnVvEs27B6JORMV13uDf_0lIzpKFYw2QvVPPYqenzRWTQVA/"

private val httpBuilder: OkHttpClient.Builder
    get() {
        // create http client
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()

                //header
                val request = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())
                    .build()

                return@Interceptor chain.proceed(request)
            })
            .readTimeout(30, TimeUnit.SECONDS)

        // log interceptor
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(loggingInterceptor)

        return httpClient
    }

lateinit var retrofit: Retrofit

private fun <S> create(serviceClass: Class<S>): S {
    val gson = GsonBuilder()
        .serializeNulls()
        .setLenient()
        .create()

    // create retrofit
    retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(API_BASE) // Put your base URL
        .client(httpBuilder.build())
        .build()

    return retrofit.create(serviceClass)
}