package com.swein.okhttpexample.modelserivce

import android.util.Log
import com.swein.okhttpexample.constants.WebConstants
import com.swein.okhttpexample.okhttpwrapper.OKHttpWrapper
import com.swein.okhttpexample.okhttpwrapper.OkHttpWrapperDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object ModelService {

    interface ModelServiceDelegate {
        fun onResponse(response: String)
        fun onException(e: Exception)
    }

    private const val TAG = "ModelService"

    fun signUp(
        id: String,
        password: String,
        nickname: String,
        imageFilePath: String,
        imageFileName: String,
        modelServiceDelegate: ModelServiceDelegate
    ) {

        val url = WebConstants.getSignUpUrl()
        Log.d("???", url)

        val formData = mutableMapOf<String, String>()
        formData["id"] = id
        formData["password"] = password
        formData["nickname"] = nickname

        var fileList: MutableList<String>? = null
        var fileNameList: MutableList<String>? = null

        if (imageFilePath != "" && imageFilePath != "") {
            fileList = mutableListOf()
            fileNameList = mutableListOf()

            fileList.add(imageFilePath)
            fileNameList.add(imageFileName)
        }

        OKHttpWrapper.requestPost(url, formData = formData,
            fileList = fileList, fileNameList = fileNameList,
            fileKey = "file",
            okHttpWrapperDelegate = object : OkHttpWrapperDelegate {
                override fun onFailure(call: Call, e: IOException) {
                    OKHttpWrapper.cancelCall(call)
                    modelServiceDelegate.onException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseString = OKHttpWrapper.getStringResponse(response)
                        modelServiceDelegate.onResponse(responseString)
                    }
                    catch (e: Exception) {
                        modelServiceDelegate.onException(e)
                    }
                    finally {
                        OKHttpWrapper.cancelCall(call)
                    }
                }

            }
        )
    }

    // sign up with json body
    fun signUp(
        id: String,
        password: String,
        nickname: String,
        modelServiceDelegate: ModelServiceDelegate
    ) {

        val url = WebConstants.getSignUpWithJsonBodyUrl()
        Log.d("???", url)

        val jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("password", password)
        jsonObject.put("nickname", nickname)


        OKHttpWrapper.requestPost(url, jsonObject = jsonObject,
            okHttpWrapperDelegate = object : OkHttpWrapperDelegate {
                override fun onFailure(call: Call, e: IOException) {
                    OKHttpWrapper.cancelCall(call)
                    modelServiceDelegate.onException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseString = OKHttpWrapper.getStringResponse(response)
                        modelServiceDelegate.onResponse(responseString)
                    }
                    catch (e: Exception) {
                        modelServiceDelegate.onException(e)
                    }
                    finally {
                        OKHttpWrapper.cancelCall(call)
                    }
                }

            }
        )

    }

    fun updateProfile(
        token: String,
        nickname: String,
        imageFilePath: String,
        imageFileName: String,
        modelServiceDelegate: ModelServiceDelegate
    ) {

        val url = WebConstants.getModifyUrl()
        Log.d("???", url)

        val header = mutableMapOf<String, String>()
        header["X-AUTH-TOKEN"] = token

        val formData = mutableMapOf<String, String>()
        formData["nickname"] = nickname

        var fileList: MutableList<String>? = null
        var fileNameList: MutableList<String>? = null

        if (imageFilePath != "" && imageFilePath != "") {
            fileList = mutableListOf()
            fileNameList = mutableListOf()

            fileList.add(imageFilePath)
            fileNameList.add(imageFileName)
        }

        OKHttpWrapper.requestPut(url, header = header, formData = formData,
            fileList = fileList, fileNameList = fileNameList,
            fileKey = "file",
            okHttpWrapperDelegate = object : OkHttpWrapperDelegate {
                override fun onFailure(call: Call, e: IOException) {
                    OKHttpWrapper.cancelCall(call)
                    modelServiceDelegate.onException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseString = OKHttpWrapper.getStringResponse(response)
                        modelServiceDelegate.onResponse(responseString)
                    }
                    catch (e: Exception) {
                        modelServiceDelegate.onException(e)
                    }
                    finally {
                        OKHttpWrapper.cancelCall(call)
                    }
                }

            }
        )
    }

    fun signIn(
        id: String,
        password: String,
        modelServiceDelegate: ModelServiceDelegate
    ) {

        val url = WebConstants.getSignInUrl(id, password)
        Log.d(TAG, url)

        OKHttpWrapper.requestGet(url, okHttpWrapperDelegate = object : OkHttpWrapperDelegate {
                override fun onFailure(call: Call, e: IOException) {
                    OKHttpWrapper.cancelCall(call)
                    modelServiceDelegate.onException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseString = OKHttpWrapper.getStringResponse(response)
                        modelServiceDelegate.onResponse(responseString)
                    }
                    catch (e: Exception) {
                        modelServiceDelegate.onException(e)
                    }
                    finally {
                        OKHttpWrapper.cancelCall(call)
                    }
                }

            }
        )

    }

    /**
     * coroutine
     * return token
     */
    suspend fun signIn(
        id: String,
        password: String
    ): String = withContext(Dispatchers.IO) {

        var token = ""

        val url = WebConstants.getSignInUrl(id, password)
        Log.d(TAG, url)

        val coroutineResponse = OKHttpWrapper.requestGet(url)
        val responseString = OKHttpWrapper.getStringResponse(coroutineResponse.response)

        OKHttpWrapper.cancelCall(coroutineResponse.call)

        val responseJSONObject = WebConstants.getJSONObjectFromResponse(responseString)
        token = WebConstants.getValueFromResponse(responseJSONObject)

        return@withContext token
    }

    fun profileInfo(
        token: String,
        modelServiceDelegate: ModelServiceDelegate) {

        val url = WebConstants.getProfileInfoUrl()
        Log.d(TAG, url)

        val header = mutableMapOf<String, String>()
        header["X-AUTH-TOKEN"] = token

        OKHttpWrapper.requestGet(url, header = header, okHttpWrapperDelegate = object : OkHttpWrapperDelegate {
                override fun onFailure(call: Call, e: IOException) {
                    OKHttpWrapper.cancelCall(call)
                    modelServiceDelegate.onException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseString = OKHttpWrapper.getStringResponse(response)
                        modelServiceDelegate.onResponse(responseString)
                    }
                    catch (e: Exception) {
                        modelServiceDelegate.onException(e)
                    }
                    finally {
                        OKHttpWrapper.cancelCall(call)
                    }
                }

            }
        )
    }

}