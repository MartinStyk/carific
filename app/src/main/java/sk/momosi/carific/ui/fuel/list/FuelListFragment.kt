package sk.momosi.carific.ui.fuel.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_expense_list.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.FragmentFuelListBinding


class FuelListFragment : Fragment() {

    private lateinit var carId: String
    private lateinit var viewModel: FuelListViewModel
    private lateinit var binding: FragmentFuelListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carId = arguments?.getString(ARGUMENT_CAR_ID) ?: throw IllegalArgumentException("Car id argument missing")
        viewModel = ViewModelProviders.of(this).get(FuelListViewModel::class.java)

        viewModel.refuelClickEvent.observe(this, Observer {
            Toast.makeText(activity, it.toString(), Toast.LENGTH_LONG).show()
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fuel_list, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expense_list.adapter = FuelListAdapter(viewModel = viewModel)
        expense_list.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.navigation_fuel)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(true, true)

        viewModel.loadData(carId)
    }

    companion object {

        private const val ARGUMENT_CAR_ID = "car_id"
        private val TAG = FuelListFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(param1: String) =
                FuelListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARGUMENT_CAR_ID, param1)
                    }
                }
    }
}
