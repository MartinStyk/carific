package sk.momosi.carific13.ui.statistics.chart

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
import android.view.*
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.fragment_chart_statistics.*
import sk.momosi.carific13.R
import sk.momosi.carific13.databinding.FragmentChartStatisticsBinding
import sk.momosi.carific13.model.Car
import sk.momosi.carific13.model.User
import sk.momosi.carific13.ui.statistics.detail.DetailedStatisticsActivity
import sk.momosi.carific13.ui.timeline.list.CarificBaseFragment
import sk.momosi.carific13.util.chart.ChartDateAxisFormatter
import java.util.*
import kotlin.math.max
import kotlin.math.min


class ChartStatisticsFragment : CarificBaseFragment(), OnChartValueSelectedListener {

    lateinit var binding: FragmentChartStatisticsBinding
    lateinit var viewModel: ChartStatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(this).get(ChartStatisticsViewModel::class.java)
        viewModel.init(car, user)
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

        setupRangeSelection()

        observeConsumptionData()

        viewModel.load()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main_statistics, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_detail_statistics -> {
            startActivity(Intent(context, DetailedStatisticsActivity::class.java).apply {
                putExtra(ARGUMENT_CAR, car)
                putExtra(ARGUMENT_USER, user)
            })
            true
        }
        else -> super.onOptionsItemSelected(item)

    }

    private fun setupActionBar() {
        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.statistics_fuel_consumption)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(false, true)

        statistics_scroller.isNestedScrollingEnabled = false
    }

    private fun setupConsumptionChart() {
        chart_consumption.apply {
            setOnChartValueSelectedListener(this@ChartStatisticsFragment)
            setDrawGridBackground(false)
            isDragEnabled = true
            isScaleXEnabled = true
            isScaleYEnabled = false
        }

        chart_consumption.description.apply {
            isEnabled = false
        }

        chart_consumption.xAxis.apply {
            enableGridDashedLine(10f, 10f, 0f)
            isGranularityEnabled = true
            labelRotationAngle = 90f
            position = XAxis.XAxisPosition.BOTTOM
        }

        chart_consumption.axisLeft.apply {
            enableGridDashedLine(10f, 1f, 0f)
            setDrawZeroLine(false)
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            setDrawLimitLinesBehindData(true)
        }

        chart_consumption.axisRight.apply {
            setEnabled(false)
        }

        chart_consumption.legend.apply {
            form = Legend.LegendForm.LINE
        }

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
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_accent)
                fillDrawable = drawable
            } else {
                fillColor = Color.BLACK
            }
        }

        chart_consumption.data = LineData(ArrayList<ILineDataSet>().apply { add(lineData) })

    }

    private fun setupRangeSelection() {
        statistics_chart_range.setOnClickListener {
            val dpd = DatePickerDialog.newInstance(
                    viewModel,
                    viewModel.selectedRangeStart.get()?.get(Calendar.YEAR) ?: 0,
                    viewModel.selectedRangeStart.get()?.get(Calendar.MONTH) ?: 0,
                    viewModel.selectedRangeStart.get()?.get(Calendar.DAY_OF_MONTH) ?: 0,
                    viewModel.selectedRangeEnd.get()?.get(Calendar.YEAR) ?: 0,
                    viewModel.selectedRangeEnd.get()?.get(Calendar.MONTH) ?: 0,
                    viewModel.selectedRangeEnd.get()?.get(Calendar.DAY_OF_MONTH) ?: 0
            );
            dpd.isAutoHighlight = true
            dpd.show(activity?.fragmentManager, DatePickerDialog::class.java.name)
        }

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
                        LimitLine(it?.averageAllTimeConsumption
                                ?: 0f, getString(R.string.statistics_fuel_consumption_all_time_average))
                                .apply {
                                    lineWidth = 2f
                                    enableDashedLine(10f, 10f, 0f)
                                    labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                                    lineColor = ContextCompat.getColor(requireContext(), R.color.colorRefueling)
                                }
                )
                chart_consumption?.invalidate()
            }, 2000)


            chart_consumption.axisLeft
            chart_consumption.axisLeft.apply {
                resetAxisMinimum()
                resetAxisMaximum()

                calculate(viewModel.consumptionMin(), viewModel.consumptionMax())

                axisMinimum = min(axisMinimum, it?.averageAllTimeConsumption ?: Float.MAX_VALUE)
                axisMaximum = max(axisMaximum, it?.averageAllTimeConsumption ?: Float.MIN_VALUE)
            }


            chart_consumption.invalidate()
            chart_consumption.animateY(1500)
            chart_consumption.zoom(1f, 1f, 1f, 1f, YAxis.AxisDependency.LEFT)
            chart_consumption.resetZoom()

        })
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
//        Toast.makeText(context, "Entry selected" + e.toString(), Toast.LENGTH_SHORT).show()
        Log.i("LOWHIGH", "low: " + chart_consumption.getLowestVisibleX() + ", high: " + chart_consumption.getHighestVisibleX())
        Log.i("MIN MAX", "xmin: " + chart_consumption.getXChartMin() + ", xmax: " + chart_consumption.getXChartMax() + ", ymin: " + chart_consumption.getYChartMin() + ", ymax: " + chart_consumption.getYChartMax())
    }

    override fun onNothingSelected() {}

    companion object {

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
