package github.leavesczy.wifip2p.ui

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import github.leavesczy.wifip2p.BaseActivity
import github.leavesczy.wifip2p.R
import github.leavesczy.wifip2p.RetrofitHelper



class LoginActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getUserList("misaj","123456",true,"zdsfdarg")
    }


    fun getUserList(email:String,password:String,flag:Boolean,tocken:String) {
        var retrofit = RetrofitHelper.getInstance()
        /*var apiInterface = retrofit.create(APIDataSource ::class.java)
        lifecycleScope.launchWhenCreated {
            try {
                val response = apiInterface.login(email,password,flag,tocken) as LoginResponse
                if (response.status == "success") {
                Log.d("rsult","re"+response.status)
                } else {
                    Log.d("rsult","re"+response.status)
                }
            }catch (Ex:Exception){
                Log.e("Error",Ex.localizedMessage)
            }
        }*/
/*

        val apiInterface = APIDataSource.create()

        //apiInterface.enqueue( Callback<List<Movie>>())
        apiInterface.enqueue( object : Callback<List<Movie>>{
            override fun onResponse(call: Call<List<Movie>>?, response: Response<List<Movie>>?) {

                if(response?.body() != null)
                    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<List<Movie>>?, t: Throwable?) {

            }
        })
*/

    }

}