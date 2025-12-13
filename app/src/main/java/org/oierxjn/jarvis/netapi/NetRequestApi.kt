package org.oierxjn.jarvis.netapi

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

object NetRequestApi {
    val okHttpClient = OkHttpClient.Builder()
        .build()

    /**
     * GET 请求
     * @param url 请求地址
     * @param onSuccess 成功回调
     * @param onFailure 失败回调
     */
    fun getRequest(
        url: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // 构建 Request
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        // 异步执行请求
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "请求失败")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    onSuccess(responseBody)
                } else {
                    onFailure("请求失败：${response.code}")
                }
            }
        })
    }
    /**
     * POST JSON 请求
     * @param url 请求地址
     * @param jsonParams JSON 字符串参数
     * @param onSuccess 成功回调
     * @param onFailure 失败回调
     */
    fun postJsonRequest(
        url: String,
        jsonParams: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // 构建 JSON 请求体
        val requestBody = jsonParams
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // 构建 Request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // 异步执行
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e.message ?: "POST 请求失败")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    onSuccess(responseBody)
                } else {
                    onFailure("POST 请求失败：${response.code}")
                }
            }
        })
    }
}