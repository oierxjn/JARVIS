package org.oierxjn.jarvis.netapi

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resumeWithException

object NetRequestApi {
    val okHttpClient = OkHttpClient.Builder()
        .build()

    suspend fun enqueueRequest(request: Request): String = suspendCancellableCoroutine { continuation ->
        val call = okHttpClient.newCall(request)

        continuation.invokeOnCancellation {
            call.cancel()
        }
        call.enqueue(object : Callback{
            override fun onResponse(call: Call, response: Response) {
                try {
                    if(response.isSuccessful){
                        continuation.resume(response.body?.string() ?: "") { cause, _, _ ->
                        }
                    }else{
                        continuation.resumeWithException(IOException("请求失败：${response.code}\n${response.message}"))
                    }
                } catch (_: Exception) {
                    continuation.resumeWithException(IOException("请求失败：应用错误"))
                } finally {
                    response.close()
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }
        })
    }
    /**
     * GET 请求
     * @param url 请求地址
     * @param headers 请求头
     *
     * @return 请求结果
     */
    suspend fun getRequest(
        url: String,
        queryParams: Map<String, String?>? = null,
        headers: Headers = Headers.Builder().build()
    ): String {
        val baseUrl = url.toHttpUrlOrNull() ?: throw IllegalArgumentException("无效的 URL")
        val requestUrlBuilder = baseUrl.newBuilder()

        queryParams?.forEach { (key, value)->
            value?.apply {
                requestUrlBuilder.addQueryParameter(key, value)
            }
        }
        val requestUrl = requestUrlBuilder.build()

        val request = Request.Builder()
            .url(requestUrl)
            .get()
            .headers(headers)
            .build()

        return enqueueRequest(request)
    }

    fun buildPostRequest(
        url: String,
        body: String,
        contentType: String? = null,
        queryParams: Map<String, String?>? = null,
        headers: Headers = Headers.Builder().build()
    ): Request{
        val baseUrl = url.toHttpUrlOrNull() ?: throw IllegalArgumentException("无效的 URL")
        val requestUrlBuilder = baseUrl.newBuilder()
        queryParams?.forEach { (key, value)->
            value?.apply {
                requestUrlBuilder.addQueryParameter(key, value)
            }
        }
        val requestUrl = requestUrlBuilder.build()
        val requestBody = body.toRequestBody(contentType?.toMediaTypeOrNull())

        return Request.Builder()
            .url(requestUrl)
            .post(requestBody)
            .headers(headers)
            .build()

    }
    /**
     * POST 请求
     */
    suspend fun postRequest(
        url: String,
        body: String,
        contentType: String? = null,
        queryParams: Map<String, String?>? = null,
        headers: Headers = Headers.Builder().build()
    ): String{
        val request = buildPostRequest(url, body, contentType, queryParams, headers)
        return enqueueRequest(request)
    }

    /**
     * POST JSON 请求
     * @param url 请求地址
     * @param jsonParams JSON 字符串参数
     * @param onSuccess 成功回调
     * @param onFailure 失败回调
     */
    suspend fun postJsonRequest(
        url: String,
        jsonParams: String,
        contentType: String? = "application/json; charset=utf-8",
        headers: Headers = Headers.Builder().build(),
    ): String {
        // 构建 Request
        val request = buildPostRequest(url, jsonParams, contentType, headers = headers)

        return enqueueRequest(request)
    }
}