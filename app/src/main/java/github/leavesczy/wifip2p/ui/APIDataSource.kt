package github.leavesczy.wifip2p.ui

import android.database.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface  APIDataSource {
    @POST("index.php?mobile/login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("authenticate") authenticate: Boolean,
        @Field("device") deviceToken: String?
    ): Observable<LoginResponse>

}