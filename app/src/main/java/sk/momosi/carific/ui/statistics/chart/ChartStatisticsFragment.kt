package sk.momosi.carific.ui.statistics.chart

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.fragment_chart_statistics.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.FragmentChartStatisticsBinding
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.User
import sk.momosi.carific.ui.statistics.detail.DetailedStatisticsActivity
import sk.momosi.carific.util.chart.ChartDateAxisFormatter
import java.util.ArrayList


class ChartStatisticsFragment : Fragment(), OnChartValueSelectedListener, OnChartGestureListener {

    lateinit var binding: FragmentChartStatisticsBinding
    lateinit var viewModel: ChartStatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChartStatisticsViewModel::class.java)
        viewModel.load(getCar(), getUser())
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chart_statistics, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()

        setupConsumptionChart()

        setupDetailButton()

        observeConsumptionData()
    }

    private fun setupActionBar(){
        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.navigation_statistics)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(false, true)

//        statistics_scroller.isNestedScrollingEnabled = false
    }

    private fun setupConsumptionChart() {
        chart_consumption.setOnChartGestureListener(this)
        chart_consumption.setOnChartValueSelectedListener(this)
        chart_consumption.setDrawGridBackground(false)
        // enable touch gestures
        chart_consumption.setTouchEnabled(true)

        // enable scaling and dragging
        chart_consumption.isDragEnabled = true
//        chart_consumption.setScaleEnabled(true)
        chart_consumption.isScaleXEnabled = true;
        chart_consumption.isScaleYEnabled = false;


        // no description text
        chart_consumption.description.apply {
            isEnabled = true
            text = getString(R.string.statistics_fuel_consumption)
        }



        // if disabled, scaling can be done on x- and y-axis separately
//        chart_consumption.setPinchZoom(true)

        // set an alternative background color
        // chart_consumption.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
//        // to use for it
//        val mv = MyMarkerView(this, R.layout.custom_marker_view)
//        mv.setChartView(chart_consumption) // For bounds control
//        chart_consumption.setMarker(mv) // Set the marker to the chart

        // x axis values
        chart_consumption.xAxis.apply {
            enableGridDashedLine(10f, 10f, 0f)
//        chart_consumption.xAxis.position = XAxis.XAxisPosition.BOTTOM
            isGranularityEnabled = true
            labelRotationAngle = 90f
            position = XAxis.XAxisPosition.BOTTOM
        }
//        chart_consumption.axisLeft.setAxisMinimum(-50f)
//        chart_consumption.axisLeft.setYOffset(20f);
        chart_consumption.axisLeft.apply {
            enableGridDashedLine(10f, 1f, 0f)
            setDrawZeroLine(false)
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            // limit lines are drawn behind data (and not on top)
            setDrawLimitLinesBehindData(true)
        }

        chart_consumption.axisRight.apply {
            setEnabled(false)
        }

        //chart_consumption.getViewPortHandler().setMaximumScaleY(2f);
        //chart_consumption.getViewPortHandler().setMaximumScaleX(2f);

//        chart_consumption.setVisibleXRange(0f,5f);
//        chart_consumption.setVisibleYRange(20f, AxisDependency.LEFT);
//        chart_consumption.centerViewTo(20, 50, AxisDependency.LEFT);

        chart_consumption.animateY(1500)
        //chart_consumption.invalidate();

        chart_consumption.legend.apply{
            form = Legend.LegendForm.LINE
        }


        // // dont forget to refresh the drawing
        // chart_consumption.invalidate();

        val lineData = LineDataSet(listOf(Entry(1f, 1f)), getString(R.string.statistics_fuel_consumption_average)).apply {
            setDrawIcons(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER

            // set the line to be drawn like this "- - - - - -"
            enableDashedLine(10f, 5f, 0f)
            enableDashedHighlightLine(10f, 5f, 0f)
            color = Color.BLACK
            setCircleColor(Color.BLACK)
            lineWidth = 1f
            circleRadius = 3f
            setDrawCircleHole(false)
            valueTextSize = 9f
            setDrawFilled(true)
            formLineWidth = 1f
            formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            formSize = 15f

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_red)
                fillDrawable = drawable
            } else {
                fillColor = Color.BLACK
            }
        }


        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineData) // add the datasets

        // create a data object with the datasets
        val data = LineData(dataSets)

        // set data
        chart_consumption.setData(data)

    }

    private fun observeConsumptionData() {
        viewModel.data.observe(this, Observer {
            if (it?.consumptionChartData?.isEmpty() == true) {
                return@Observer
            }

            val set1 = chart_consumption.data.getDataSetByIndex(0) as LineDataSet
            set1.values = it?.consumptionChartData
            chart_consumption.data.notifyDataChanged()
            chart_consumption.notifyDataSetChanged()

            chart_consumption.xAxis.labelCount = it?.xAxis?.size ?: 0
            chart_consumption.xAxis.valueFormatter = ChartDateAxisFormatter(it?.xAxis
                    ?: emptyList())

            chart_consumption.axisLeft.limitLines.clear()

            Handler().postDelayed({
                chart_consumption?.axisLeft?.limitLines?.add(
                        LimitLine(it?.averageConsumption ?: 0f, "All time average").apply {
                            lineWidth = 2f
                        }
                )
                chart_consumption?.invalidate()
            }, 2000)


//            chart_consumption.setVisibleXRange(0f, min(5f, it?.consumptionChartData?.size?.toFloat() ?: 1f))

            chart_consumption.invalidate()
            chart_consumption.fitScreen()
            chart_consumption.zoom(1f,1f,1f,1f,YAxis.AxisDependency.LEFT)
            chart_consumption.resetZoom()

        })
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        Toast.makeText(context, "Entry selected" + e.toString(), Toast.LENGTH_SHORT).show()
        Log.i("LOWHIGH", "low: " + chart_consumption.getLowestVisibleX() + ", high: " + chart_consumption.getHighestVisibleX())
        Log.i("MIN MAX", "xmin: " + chart_consumption.getXChartMin() + ", xmax: " + chart_consumption.getXChartMax() + ", ymin: " + chart_consumption.getYChartMin() + ", ymax: " + chart_consumption.getYChartMax())
    }

    override fun onNothingSelected() {
        Toast.makeText(context, "Nothing selected", Toast.LENGTH_SHORT).show()
    }


    override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
        Log.i("Gesture", "START, x: " + me.x + ", y: " + me.y)
    }

    override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
        Log.i("Gesture", "END, lastGesture: $lastPerformedGesture")

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            chart_consumption.highlightValues(null) // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    override fun onChartLongPressed(me: MotionEvent) {
        Log.i("LongPress", "Chart longpressed.")
    }

    override fun onChartDoubleTapped(me: MotionEvent) {
        Log.i("DoubleTap", "Chart double-tapped.")
    }

    override fun onChartSingleTapped(me: MotionEvent) {
        Log.i("SingleTap", "Chart single-tapped.")
    }

    override fun onChartFling(me1: MotionEvent, me2: MotionEvent, velocityX: Float, velocityY: Float) {
        Log.i("Fling", "Chart flinged. VeloX: $velocityX, VeloY: $velocityY")
    }

    override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {
        Log.i("Scale / Zoom", "ScaleX: $scaleX, ScaleY: $scaleY")
    }

    override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
        Log.i("Translate / Move", "dX: $dX, dY: $dY")
    }


    private fun setupDetailButton() {
        statistics_chart_button_detail.setOnClickListener {

            val intent = Intent(context, DetailedStatisticsActivity::class.java)
            intent.putExtra(ARGUMENT_CAR, getCar())
            intent.putExtra(ARGUMENT_USER, getUser())

            startActivity(intent)
        }
    }

    private fun getCar() = arguments?.getParcelable<Car>(ARGUMENT_CAR)
            ?: throw IllegalArgumentException("Car argument missing")

    private fun getUser() = arguments?.getParcelable<User>(ARGUMENT_USER)
            ?: throw IllegalArgumentException("User argument missing")

    companion object {
        const val ARGUMENT_CAR = "car"
        const val ARGUMENT_USER = "user"

        val TAG = ChartStatisticsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(car: Car, user: User) =
                ChartStatisticsFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARGUMENT_CAR, car)
                        putParcelable(ARGUMENT_USER, user)
                    }
                }
    }

}
