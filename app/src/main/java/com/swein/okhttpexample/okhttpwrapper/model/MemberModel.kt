package com.swein.okhttpexample.okhttpwrapper.model

import com.swein.okhttpexample.framework.parsing.ParsingUtility
import org.json.JSONObject

class MemberModel {

    var uuid: String = ""
    var id: String = ""
    var nickname: String = ""
    var email: String = ""
    var profileImageUrl: String = ""
    var createDate: String = ""
    var createBy: String = ""
    var modifyDate: String = ""
    var modifyBy: String = ""

    fun initWithJSONObject(jsonObject: JSONObject) {
        // you should get the key from your server developer~
        uuid = ParsingUtility.parsingString(jsonObject, "uuid")
        id = ParsingUtility.parsingString(jsonObject, "id")
        nickname = ParsingUtility.parsingString(jsonObject, "nickname")
        email = ParsingUtility.parsingString(jsonObject, "email")
        profileImageUrl = ParsingUtility.parsingString(jsonObject, "profile_image_url")
        createDate = ParsingUtility.parsingString(jsonObject, "create_date")
        createBy = ParsingUtility.parsingString(jsonObject, "create_by")
        modifyDate = ParsingUtility.parsingString(jsonObject, "modify_date")
        modifyBy = ParsingUtility.parsingString(jsonObject, "modify_by")

    }

}