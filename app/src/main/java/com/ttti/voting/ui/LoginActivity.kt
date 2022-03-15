package com.ttti.voting.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.coreict.models.GetUserQuery
import com.ttti.voting.MainActivity
import com.ttti.voting.R
import okhttp3.OkHttpClient
import org.json.JSONException

class LoginActivity : AppCompatActivity() {

    private var BASE_URL = ""
    private lateinit var client: ApolloClient

    private fun setUpApolloClient(): ApolloClient {
        val okHttp = OkHttpClient
            .Builder()
        return ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttp.build())
            .build()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        BASE_URL = getString(R.string.graphql_server)
        client = setUpApolloClient()
        val  txtUsername: EditText = findViewById(R.id.txtUsername)
        val  txtPassword: EditText = findViewById(R.id.txtPassword)
        val  btnLogin: Button = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            client.query(
                GetUserQuery
                    .builder()
                    .email(txtUsername.text.toString())
                    .password(txtPassword.text.toString())
                    .build()
            )
                .enqueue(object : ApolloCall.Callback<GetUserQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Log.e("DEBUG",e.message.toString())
                    }
                    override fun onResponse(response: Response<GetUserQuery.Data>) {
                        runOnUiThread {
                            try {
                                val dataCount : Int = response.data()?.data()?.nodes()?.count() ?:0
                                if(dataCount > 0){
                                    txtUsername.setError(null)
                                    txtPassword.setError(null)
                                    val intent = Intent(applicationContext,MainActivity::class.java).apply {
                                        putExtra("USER",response.data()?.data()?.nodes().toString())
                                    }
                                    startActivity(intent)
                                }else{
                                    txtUsername.setError("Invalid credentials")
                                    txtPassword.setError("Invalid credentials")
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                })

        }


        val  btnRegister: TextView = findViewById(R.id.btnNewVoter)
        btnRegister.setOnClickListener {
            val intent = Intent(applicationContext,RegisterActivity::class.java).apply {
            }
            startActivity(intent)
        }


    }
}