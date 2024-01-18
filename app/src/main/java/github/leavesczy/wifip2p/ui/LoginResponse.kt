package github.leavesczy.wifip2p.ui

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("authentication_key")
    var authenticationKey: String,
    @SerializedName("class_id")
    var classId: String,
    @SerializedName("login_type")
    var loginType: String,
    @SerializedName("login_user_id")
    var loginUserId: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("section_id")
    var sectionId: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("message")
    var message:String

)
