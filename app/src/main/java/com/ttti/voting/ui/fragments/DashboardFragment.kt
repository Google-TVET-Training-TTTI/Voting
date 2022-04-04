package com.ttti.voting.ui.fragments


//import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloSubscriptionCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.subscription.SubscriptionConnectionParams
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.coreict.models.GetVotesQuery
import com.coreict.models.NewUserSubscription
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.ttti.voting.R
import okhttp3.OkHttpClient
import org.json.JSONException
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors


class DashboardFragment : Fragment() {

    private var BASE_URL = ""
    private var WS_URL= ""
    private var chart: BarChart? = null
    private var scoreList = ArrayList<Score>()
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
    inner class MyAxisFormatter : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < scoreList.size) {
                scoreList[index].name
            } else {
                ""
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root: View =  inflater.inflate(R.layout.fragment_dashboard, container, false)
        val activity = context
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
        /////////////////////////////////////////////////////////////////////////get votes from graph
        client.query(
            GetVotesQuery
                .builder()
                .build()
         )
            .enqueue(object : ApolloCall.Callback<GetVotesQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.e("DEBUG",e.message.toString())
                }
                override fun onResponse(response: Response<GetVotesQuery.Data>) {
                    getActivity()?.runOnUiThread {
                        try {

                            val dataCount : Int = response.data()?.data()?.nodes()?.count() ?:0
                            if(dataCount > 0){
                                for (i in 0 until dataCount) {
                                    var candidateName : String =
                                        response?.data()?.data()?.nodes()?.get(i)?.candidate()?.firstName().toString()
                                    var candidateVotes : Int = response?.data()?.data()?.nodes()?.get(i)?.votes()!!
                                    scoreList.add(Score(candidateName, candidateVotes))

                                }
                                scoreList = scoreList.stream().distinct().collect(Collectors.toList()) as ArrayList

                                ////////////////draw graph
                                chart = root.findViewById(R.id.chart1) as BarChart
                                val entries: ArrayList<BarEntry> = ArrayList()
                                for (i in scoreList.indices) {
                                    val score = scoreList[i]
                                    entries.add(BarEntry(i.toFloat(), score.score.toFloat()))
                                }
                                val barDataSet = BarDataSet(entries, "")
                                barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
                                val data = BarData(barDataSet)
                                chart!!.data = data
                                chart?.axisLeft?.setDrawGridLines(true)
                                chart?.xAxis?.setDrawGridLines(true)
                                chart?.xAxis?.setDrawAxisLine(false)
                                val xAxis: XAxis = chart!!.xAxis
                                xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
                                xAxis.valueFormatter = MyAxisFormatter()
                                xAxis.setDrawLabels(true)
                                xAxis.granularity = 1f
                                xAxis.labelRotationAngle = +90f
                                chart?.axisRight?.isEnabled = true
                                chart?.legend?.isEnabled = false
                                chart?.description?.isEnabled = false
                                chart?.animateY(3000)
                                chart?.invalidate()
                                ////////////////draw graph
                            }else{
                              //no data fetched!
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        //////////////////////////////////////////////////////////////////////////////////
        return root
    }
}

data class Score(
    val name:String,
    val score: Int,
)


// https://intensecoder.com/bar-chart-tutorial-in-android-using-kotlin/
// and
// charts Ref : https://medium.com/@shehanatuk/how-to-use-mpandroidchart-in-android-studio-c01a8150720f