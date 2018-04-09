package sk.momosi.carific.ui.fuel.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_fuel_list.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.FragmentFuelListBinding
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.User
import sk.momosi.carific.ui.fuel.edit.AddEditFuelActivity
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.model.SectionInfo


class FuelListFragment : Fragment() {

    private lateinit var viewModel: FuelListViewModel
    private lateinit var binding: FragmentFuelListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FuelListViewModel::class.java)

        setupListItemClicks()
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

        setupList()

        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.title_fuel, getCar().name)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(true, true)

        viewModel.init(getCar().id, getUser())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupAddButton()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData(getCar().id)
    }

    private fun setupAddButton() {
        val addBtn = activity?.findViewById<FloatingActionButton>(R.id.fab)

        addBtn?.setOnClickListener {
            val createIntent = Intent(context, AddEditFuelActivity::class.java)
            createIntent.putExtra(AddEditFuelActivity.ARG_CAR_ID, viewModel.carId)
            createIntent.putExtra(AddEditFuelActivity.ARG_USER, viewModel.user)

            startActivity(createIntent)
        }
    }

    private fun setupListItemClicks() {
        viewModel.refuelClickEvent.observe(this, Observer { refueling ->
            val editIntent = Intent(context, AddEditFuelActivity::class.java)
            editIntent.putExtra(AddEditFuelActivity.ARG_CAR_ID, viewModel.carId)
            editIntent.putExtra(AddEditFuelActivity.ARG_REFUELING, refueling)
            editIntent.putExtra(AddEditFuelActivity.ARG_USER, viewModel.user)

            startActivity(editIntent)
        })
    }


    private fun setupList() {
        fuel_list.adapter = FuelListAdapter(viewModel = viewModel)
        fuel_list.addItemDecoration(
                object : RecyclerSectionItemDecoration.SectionCallback {
                    override fun isSection(position: Int) = viewModel.isSection(position)

                    override fun getSectionHeader(position: Int): SectionInfo? =
                            SectionInfo(viewModel.sectionName(position), null)
                })
    }


    private fun getCar() = arguments?.getParcelable<Car>(ARGUMENT_CAR)
            ?: throw IllegalArgumentException("Car argument missing")

    private fun getUser() = arguments?.getParcelable<User>(ARGUMENT_USER)
            ?: throw IllegalArgumentException("User argument missing")


    companion object {

        private const val ARGUMENT_CAR = "car"
        private const val ARGUMENT_USER = "user"
        private val TAG = FuelListFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(car: Car, user: User) =
                FuelListFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARGUMENT_CAR, car)
                        putParcelable(ARGUMENT_USER, user)
                    }
                }
    }
}
