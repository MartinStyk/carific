package sk.momosi.carific.ui.statistics

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sk.momosi.carific.R
import sk.momosi.carific.databinding.FragmentStatisticsBinding
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.User


class StatisticsFragment : Fragment() {

    lateinit var binding: FragmentStatisticsBinding
    lateinit var viewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StatisticsViewModel::class.java)
        viewModel.load(getCar(), getUser())
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.navigation_statistics)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(true, true)
    }


    private fun getCar() = arguments?.getParcelable<Car>(ARGUMENT_CAR)
            ?: throw IllegalArgumentException("Car argument missing")

    private fun getUser() = arguments?.getParcelable<User>(ARGUMENT_USER)
            ?: throw IllegalArgumentException("User argument missing")

    companion object {
        private const val ARGUMENT_CAR = "car"
        private const val ARGUMENT_USER = "user"


        private val TAG = StatisticsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(car: Car, user: User) =
                StatisticsFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARGUMENT_CAR, car)
                        putParcelable(ARGUMENT_USER, user)
                    }
                }
    }

}
