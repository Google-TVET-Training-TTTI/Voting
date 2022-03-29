package com.ttti.voting.ui.fragments

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloSubscriptionCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.subscription.SubscriptionConnectionParams
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.coreict.models.NewUserSubscription
import com.ttti.voting.R
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class DashboardFragment : Fragment() {

    private var BASE_URL = ""
    private var WS_URL= ""
    private lateinit var client: ApolloClient

    fun createSubscriptionApolloClient(): ApolloClient {
        val okHttpClient = OkHttpClient
            .Builder()
            .build()
        val subscriptionTransportFactory = WebSocketSubscriptionTransport.Factory(WS_URL, okHttpClient)
        val connectionParams: MutableMap<String, Any> = HashMap()
        connectionParams["Authorization"] = "Bearer Mugambi M."
        return ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .subscriptionHeartbeatTimeout(1000, TimeUnit.SECONDS)
            .subscriptionConnectionParams(SubscriptionConnectionParams(connectionParams))
            .subscriptionTransportFactory(subscriptionTransportFactory)
            .build()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //dashboard stuff here
        BASE_URL = getString(R.string.graphql_server)
        WS_URL = getString(R.string.graphql_server_ws)

        val client = createSubscriptionApolloClient()
        val call = client.subscribe(
            NewUserSubscription
            .builder()
            .build()
        )
        call?.execute(object: ApolloSubscriptionCall.Callback<NewUserSubscription.Data> {
            override fun onResponse(response: Response<NewUserSubscription.Data>) {
                Log.d("Voting App", response?.data()?.data()?.id().toString())
            }
            override fun onCompleted() {
                Log.d("Voting App", "Completed!")
            }
            override fun onConnected() {
                Log.d("Voting App", "Connected to WS" )
            }
            override fun onFailure(e: ApolloException) {
                Log.d("Voting App", e.toString())
            }
            override fun onTerminated() {
                Log.d("Voting App", "Dis-connected from WS" )
            }
        })



        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }
}