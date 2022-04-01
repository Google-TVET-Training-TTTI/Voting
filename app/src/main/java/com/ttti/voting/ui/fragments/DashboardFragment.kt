package com.ttti.voting.ui.fragments


//import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloSubscriptionCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.subscription.SubscriptionConnectionParams
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.coreict.models.NewUserSubscription
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.model.GradientColor
import com.github.mikephil.charting.utils.ColorTemplate
import com.ttti.voting.R
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MyXAxisFormatter : ValueFormatter() {
    private val days = arrayOf("Mo", "Tu")
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return days.getOrNull(value.toInt()) ?: value.toString()
    }
}


class DashboardFragment : Fragment() {

    private var BASE_URL = ""
    private var WS_URL= ""
    private var chart: BarChart? = null
    private var scoreList = ArrayList<Score>()

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

    private fun getScoreList(): ArrayList<Score> {
        scoreList.add(Score("John", 56))
        scoreList.add(Score("Rey", 75))
        scoreList.add(Score("Steve", 85))
        scoreList.add(Score("Kevin", 45))
        scoreList.add(Score("Jeff", 63))
        return scoreList
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


        chart = root.findViewById(R.id.chart1) as BarChart
        val entries: ArrayList<BarEntry> = ArrayList()
        scoreList = getScoreList()
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
        //draw chart
        chart?.invalidate()
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