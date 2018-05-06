package sk.momosi.carific.ui.statistics.chart

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_chart_statistics.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.FragmentChartStatisticsBinding
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.User
import sk.momosi.carific.ui.statistics.detail.DetailedStatisticsActivity
import sk.momosi.carific.ui.statistics.detail.DetailedStatisticsViewModel


class ChartStatisticsFragment : Fragment() {

    lateinit var binding: FragmentChartStatisticsBinding
    lateinit var viewModel: DetailedStatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DetailedStatisticsViewModel::class.java)
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
        setupDetailButton()
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
