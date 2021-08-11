package com.swein.okhttpexample.constants

import com.swein.okhttpexample.framework.parsing.ParsingUtility
import org.json.JSONObject

object WebConstants {

    private const val DOMAIN = "your domain here"
    const val IMAGE_DOMAIN = "your static image domain here"

    fun getJSONObjectFromResponse(jsonObjectString: String): JSONObject {
        return JSONObject(jsonObjectString)
    }

    fun isResponseSuccess(jsonObject: JSONObject): Boolean {
        return ParsingUtility.parsingBoolean(jsonObject, "success")
    }

    fun getValueFromResponse(jsonObject: JSONObject): String {
        return ParsingUtility.parsingString(jsonObject, "value")
    }

    fun getDictionaryFromResponse(jsonObject: JSONObject): JSONObject {
        return ParsingUtility.parsingJSONObject(jsonObject, "dictionary")
    }

    fun getSignUpUrl(): String {
        return "$DOMAIN/post_test/member/create/form_data"
    }

    fun getSignUpWithJsonBodyUrl(): String {
        return "$DOMAIN/post_test/member/create/json_body"
    }

    fun getModifyUrl(): String {
        return "$DOMAIN/put_test/member/update/form_data"
    }

    fun getSignInUrl(id: String, password: String): String {
        return "$DOMAIN/get_test/member/token?id=$id&password=$password"
    }

    fun getProfileInfoUrl(): String {
        return "$DOMAIN/get_test/member/search/token"
    }

}