package sk.momosi.carific13.ui.statistics.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import sk.momosi.carific13.R
import sk.momosi.carific13.databinding.FragmentDetailedStatisticsBinding
import sk.momosi.carific13.model.Car
import sk.momosi.carific13.model.User
import sk.momosi.carific13.ui.timeline.list.CarificBaseFragment


class DetailedStatisticsFragment : CarificBaseFragment() {

    lateinit var binding: FragmentDetailedStatisticsBinding
    lateinit var viewModel: DetailedStatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DetailedStatisticsViewModel::class.java)
        viewModel.load(car, user)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_statistics, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.navigation_statistics)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(true, true)
    }


    companion object {

        val TAG = DetailedStatisticsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(car: Car, user: User) =
                DetailedStatisticsFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARGUMENT_CAR, car)
                        putParcelable(ARGUMENT_USER, user)
                    }
                }
    }

}
