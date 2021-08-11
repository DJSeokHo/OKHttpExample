package com.swein.okhttpexample.okhttpwrapper

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.File
import java.io.IOException


/**
 *
 * After Android 9.0
 * OKHttp can not access http
 *
 * so add this in AndroidManifest.xml between the <application></application>
 *
 * <uses-library android:name="org.apache.http.legacy" android:required="false"/>
 *
 * and add android:usesCleartextTraffic="true" in the <application>
 */

interface OkHttpWrapperDelegate {
    fun onFailure(call: Call, e: IOException)
    fun onResponse(call: Call, response: Response)
}

data class CoroutineResponse(val call: Call, val response: Response)


private var okHttpClient = OkHttpClient.Builder().build()

object OKHttpWrapper {

    const val TAG = "OKHttpWrapper"

    fun cancelCall(call: Call) {
        if (!call.isCanceled()) {
            call.cancel()
        }
    }

    fun getStringResponse(response: Response, defaultResponse: String = ""): String {
        Log.d(TAG, "onResponse: $response")

        val responseBody = response.body

        responseBody?.let {
            return try {
                it.string()
            }
            catch (e: Exception) {
                "exception ${e.message.toString()}"
            }
        } ?: run {
            return defaultResponse
        }
    }

    fun requestGet(
        url: String,
        header: MutableMap<String, String>? = null,
        okHttpWrapperDelegate: OkHttpWrapperDelegate
    ) {

        val builder = Request.Builder()

        // if header
        header?.let {
            for ((key, value) in it) {
                builder.addHeader(key, value)
            }
        }

        val request = builder.get().url(url).build()
        val call = okHttpClient.newCall(request)

        call.enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                okHttpWrapperDelegate.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                okHttpWrapperDelegate.onResponse(call, response)
            }

        })
    }

    /**
     * coroutine
     */
    fun requestGet(
        url: String,
        header: MutableMap<String, String>? = null
    ): CoroutineResponse {

        val builder = Request.Builder()

        // if header
        header?.let {
            for ((key, value) in it) {
                builder.addHeader(key, value)
            }
        }

        val request = builder.get().url(url).build()
        val call = okHttpClient.newCall(request)

        val result = call.execute()
        return CoroutineResponse(call, result)
    }

    fun requestPost(
        url: String,
        header: MutableMap<String, String>? = null,
        formData: MutableMap<String, String>? = null,
        fileList: MutableList<String>? = null,
        fileNameList: MutableList<String>? = null,
        fileKey: String = "", // get file key from your server developer
        jsonObject: JSONObject? = null,
        okHttpWrapperDelegate: OkHttpWrapperDelegate
    ) {

        val builder = Request.Builder()

        // if have header
        header?.let {
            for ((key, value) in it) {
                builder.addHeader(key, value)
            }
        }

        val requestBody: RequestBody

        // if have form data
        if (formData != null) {

            val multipartBodyBuilder = MultipartBody.Builder()
            multipartBodyBuilder.setType(MultipartBody.FORM)

            // if have form data, of course have
            formData.let {
                Log.d(TAG, "add form data")
                for ((key, value) in formData) {
                    Log.d(TAG, "$key $value")
                    multipartBodyBuilder.addFormDataPart(key, value)
                }
            }

            // if have file path and file name string
            if (fileList != null && fileNameList != null) {
                Log.d(TAG, "add files")
                var file: File
                for (i in fileList.indices) {

                    Log.d(TAG, fileNameList[i])

                    val mediaType = if (fileList[i].endsWith("png")) {
                        "image/png".toMediaType()
                    }
                    else {
                        "image/jpeg".toMediaType()
                    }
                    // you can add other type

                    file = File(fileList[i])

                    multipartBodyBuilder.addFormDataPart(fileKey, fileNameList[i], RequestBody.create(mediaType, file))
                }
            }

            requestBody = multipartBodyBuilder.build()
        }
        else {

            val mediaType = "application/json; charset=utf-8".toMediaType()

            // if have json body
            requestBody = if (jsonObject == null) {
                RequestBody.create(mediaType, "")
            }
            else {
                RequestBody.create(mediaType, jsonObject.toString())
            }
        }

        val request = builder.post(requestBody).url(url).build()
        val call = okHttpClient.newCall(request)

        call.enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                okHttpWrapperDelegate.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                okHttpWrapperDelegate.onResponse(call, response)
            }

        })
    }

    fun requestPut(
        url: String,
        header: MutableMap<String, String>? = null,
        formData: MutableMap<String, String>? = null,
        fileList: MutableList<String>? = null,
        fileNameList: MutableList<String>? = null,
        fileKey: String = "", // get file key from your server developer
        jsonObject: JSONObject? = null,
        okHttpWrapperDelegate: OkHttpWrapperDelegate
    ) {

        val builder = Request.Builder()

        // if have header
        header?.let {
            for ((key, value) in it) {
                builder.addHeader(key, value)
            }
        }

        val requestBody: RequestBody

        // if have form data
        if (formData != null) {

            val multipartBodyBuilder = MultipartBody.Builder()
            multipartBodyBuilder.setType(MultipartBody.FORM)

            // if have form data, of course have
            formData.let {
                Log.d(TAG, "add form data")
                for ((key, value) in formData) {
                    Log.d(TAG, "$key $value")
                    multipartBodyBuilder.addFormDataPart(key, value)
                }
            }

            // if have file path and file name string
            if (fileList != null && fileNameList != null) {
                Log.d(TAG, "add files")
                var file: File
                for (i in fileList.indices) {

                    Log.d(TAG, fileNameList[i])

                    val mediaType = if (fileList[i].endsWith("png")) {
                        "image/png".toMediaType()
                    }
                    else {
                        "image/jpeg".toMediaType()
                    }
                    // you can add other type

                    file = File(fileList[i])

                    multipartBodyBuilder.addFormDataPart(fileKey, fileNameList[i], RequestBody.create(mediaType, file))
                }
            }

            requestBody = multipartBodyBuilder.build()
        }
        else {

            val mediaType = "application/json; charset=utf-8".toMediaType()

            // if have json body
            requestBody = if (jsonObject == null) {
                RequestBody.create(mediaType, "")
            }
            else {
                RequestBody.create(mediaType, jsonObject.toString())
            }
        }

        val request = builder.put(requestBody).url(url).build()
        val call = okHttpClient.newCall(request)

        call.enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                okHttpWrapperDelegate.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                okHttpWrapperDelegate.onResponse(call, response)
            }

        })
    }
}