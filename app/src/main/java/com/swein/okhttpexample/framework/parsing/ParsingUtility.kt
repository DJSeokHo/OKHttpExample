package com.swein.okhttpexample.framework.parsing

import org.json.JSONArray
import org.json.JSONObject

object ParsingUtility {

    fun parsingString(jsonObject: JSONObject, key: String, defaultValue: String = ""): String {

        if (!jsonObject.has(key)) {
            return defaultValue
        }

        return if (jsonObject.isNull(key)) {
            defaultValue
        }
        else {
            val result = jsonObject.getString(key)
            result ?: defaultValue
        }
    }

    fun parsingBoolean(jsonObject: JSONObject, key: String, defaultValue: Boolean = false): Boolean {

        if (!jsonObject.has(key)) {
            return defaultValue
        }

        return if (jsonObject.isNull(key)) {
            defaultValue
        }
        else {
            jsonObject.getBoolean(key)
        }
    }

    fun parsingInt(jsonObject: JSONObject, key: String, defaultValue: Int = 0): Int {

        if (!jsonObject.has(key)) {
            return defaultValue
        }

        return if (jsonObject.isNull(key)) {
            defaultValue
        }
        else {
            jsonObject.getInt(key)
        }
    }

    fun parsingDouble(jsonObject: JSONObject, key: String, defaultValue: Double = 0.0): Double {

        if (!jsonObject.has(key)) {
            return defaultValue
        }

        return if (jsonObject.isNull(key)) {
            defaultValue
        }
        else {
            jsonObject.getDouble(key)
        }
    }

    fun parsingLong(jsonObject: JSONObject, key: String, defaultValue: Long = 0): Long {

        if (!jsonObject.has(key)) {
            return defaultValue
        }

        return if (jsonObject.isNull(key)) {
            defaultValue
        }
        else {
            jsonObject.getLong(key)
        }
    }

    fun parsingJSONObject(jsonObject: JSONObject, key: String): JSONObject {
        if (!jsonObject.has(key)) {
            return JSONObject()
        }

        return if (jsonObject.isNull(key)) {
            JSONObject()
        }
        else {
            jsonObject.getJSONObject(key)
        }
    }

    fun parsingJSONArray(jsonObject: JSONObject, key: String): JSONArray {
        if (!jsonObject.has(key)) {
            return JSONArray()
        }

        return if (jsonObject.isNull(key)) {
            JSONArray()
        }
        else {
            jsonObject.getJSONArray(key)
        }
    }

}