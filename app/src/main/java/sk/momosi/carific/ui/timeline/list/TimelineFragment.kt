package sk.momosi.carific.ui.timeline.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.fragment_timeline_list.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.FragmentTimelineListBinding
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.User
import sk.momosi.carific.ui.expense.edit.AddEditExpenseActivity
import sk.momosi.carific.ui.fuel.edit.AddEditFuelActivity
import sk.momosi.carific.util.extensions.hideAnimate
import sk.momosi.carific.view.floatingactionbutton.FloatingActionButton
import sk.momosi.carific.view.floatingactionbutton.SpeedDialMenuAdapter
import sk.momosi.carific.view.floatingactionbutton.SpeedDialMenuItem
import sk.momosi.carific.view.recycler.decorations.ImageItemDecoration
import sk.momosi.carific.view.recycler.decorations.RecyclerSectionItemDecoration
import sk.momosi.carific.view.recycler.model.SectionInfo
import xyz.sangcomz.stickytimelineview.RoadItemDecoration

class TimelineFragment : Fragment() {

    private lateinit var viewModel: TimelineViewModel
    private lateinit var binding: FragmentTimelineListBinding
    private var fab: FloatingActionButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TimelineViewModel::class.java)

        setupListItemClicks()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline_list, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()

        setupAddButton()

        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.title_timeline, getCar().name)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(true, true)

        viewModel.init(getCar().id, getUser())
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    private fun setupAddButton() {
        fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.let {
            it.speedDialMenuAdapter = speedDialMenuAdapter
            it.contentCoverEnabled = true
            it.visibility = View.VISIBLE
            it.show()
            it.bringToFront()
        }
        internalSetupButton()
    }

    private fun setupListItemClicks() {
        viewModel.refuelClickEvent.observe(this, Observer { refueling ->
            val editIntent = Intent(context, AddEditFuelActivity::class.java)
            editIntent.putExtra(AddEditFuelActivity.ARG_CAR_ID, viewModel.carId)
            editIntent.putExtra(AddEditFuelActivity.ARG_REFUELING, refueling)
            editIntent.putExtra(AddEditFuelActivity.ARG_USER, viewModel.user)

            startActivity(editIntent)
        })

        viewModel.expenseClickEvent.observe(this, Observer { expense ->
            val editIntent = Intent(context, AddEditExpenseActivity::class.java)
            editIntent.putExtra(AddEditExpenseActivity.ARG_CAR_ID, viewModel.carId)
            editIntent.putExtra(AddEditExpenseActivity.ARG_EXPENSE, expense)
            editIntent.putExtra(AddEditExpenseActivity.ARG_USER, viewModel.user)

            startActivity(editIntent)
        })
    }


    override fun onDestroyView() {
        fab?.hide()

        super.onDestroyView()
    }

    private fun setupList() {
        fuel_list.adapter = TimelineListAdapter(viewModel = viewModel)

        fuel_list.addItemDecoration(RoadItemDecoration(requireContext()))

        fuel_list.addItemDecoration(
                object : RecyclerSectionItemDecoration.SectionCallback {
                    override fun isSection(position: Int) = viewModel.isSection(position)

                    override fun getSectionHeader(position: Int) = SectionInfo(viewModel.sectionName(position), null)
                })

        fuel_list.addItemDecoration(ImageItemDecoration(requireContext()))
    }


    private fun getCar() = arguments?.getParcelable<Car>(ARGUMENT_CAR)
            ?: throw IllegalArgumentException("Car argument missing")

    private fun getUser() = arguments?.getParcelable<User>(ARGUMENT_USER)
            ?: throw IllegalArgumentException("User argument missing")

    private val speedDialMenuAdapter = object : SpeedDialMenuAdapter() {
        override fun getCount(): Int = 2

        override fun getMenuItem(context: Context, position: Int): SpeedDialMenuItem = when (position) {
            0 -> SpeedDialMenuItem(context, R.drawable.ic_expense_white, getString(R.string.expense_create))
            1 -> SpeedDialMenuItem(context, R.drawable.ic_gas_station_white, getString(R.string.refueling_create))
            else -> throw IllegalArgumentException("No menu item: $position")
        }

        override fun onMenuItemClick(position: Int): Boolean {
            val createIntent = when (position) {
                0 -> Intent(context, AddEditExpenseActivity::class.java)
                1 -> Intent(context, AddEditFuelActivity::class.java)
                else -> throw IllegalArgumentException("No menu item: $position")
            }
            createIntent.putExtra(AddEditFuelActivity.ARG_CAR_ID, viewModel.carId)
            createIntent.putExtra(AddEditFuelActivity.ARG_USER, viewModel.user)
            startActivity(createIntent)

            return true
        }

        @ColorInt
        override fun getBackgroundColour(position: Int) =
                when (position) {
                    0 -> ContextCompat.getColor(requireContext(), R.color.colorExpense)
                    1 -> ContextCompat.getColor(requireContext(), R.color.colorRefueling)
                    else -> throw IllegalArgumentException("No menu item: $position")
                }

        override fun fabRotationDegrees(): Float = 135F
    }

    fun internalSetupButton() {

        binding.fuelList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    fab?.hide()
                } else if (dy < 0) {
                    fab?.show()
                }
            }
        })
    }

    companion object {

        private const val ARGUMENT_CAR = "car"
        private const val ARGUMENT_USER = "user"
        private val TAG = TimelineFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(car: Car, user: User) =
                TimelineFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARGUMENT_CAR, car)
                        putParcelable(ARGUMENT_USER, user)
                    }
                }
    }
}
