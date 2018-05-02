package sk.momosi.carific.ui.timeline.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import kotlinx.android.synthetic.main.fragment_timeline_list.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.FragmentTimelineListBinding
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.User
import sk.momosi.carific.ui.expense.edit.AddEditExpenseActivity
import sk.momosi.carific.ui.fuel.edit.AddEditFuelActivity
import sk.momosi.carific.view.recycler.decorations.ImageItemDecoration
import sk.momosi.carific.view.recycler.decorations.RecyclerSectionItemDecoration
import sk.momosi.carific.view.recycler.model.SectionInfo
import xyz.sangcomz.stickytimelineview.RoadItemDecoration


class TimelineFragment : Fragment() {

    private lateinit var viewModel: TimelineViewModel
    private lateinit var binding: FragmentTimelineListBinding

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
        val fabMenu = activity?.findViewById<FloatingActionsMenu>(R.id.fab)
        fabMenu?.let {
            it.visibility=View.VISIBLE
            it.collapse()
        }

        activity?.findViewById<FloatingActionButton>(R.id.fab_fuel)?.setOnClickListener {
            val createIntent = Intent(context, AddEditFuelActivity::class.java)
            createIntent.putExtra(AddEditFuelActivity.ARG_CAR_ID, viewModel.carId)
            createIntent.putExtra(AddEditFuelActivity.ARG_USER, viewModel.user)
            startActivity(createIntent)
            fabMenu?.toggle()
        }

        activity?.findViewById<FloatingActionButton>(R.id.fab_expense)?.setOnClickListener {
            val createIntent = Intent(context, AddEditExpenseActivity::class.java)
            createIntent.putExtra(AddEditExpenseActivity.ARG_CAR_ID, viewModel.carId)
            createIntent.putExtra(AddEditExpenseActivity.ARG_USER, viewModel.user)
            startActivity(createIntent)
            fabMenu?.toggle()
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

        viewModel.expenseClickEvent.observe(this, Observer { expense ->
            val editIntent = Intent(context, AddEditExpenseActivity::class.java)
            editIntent.putExtra(AddEditExpenseActivity.ARG_CAR_ID, viewModel.carId)
            editIntent.putExtra(AddEditExpenseActivity.ARG_EXPENSE, expense)
            editIntent.putExtra(AddEditExpenseActivity.ARG_USER, viewModel.user)

            startActivity(editIntent)
        })
    }


    override fun onDestroyView() {
        activity?.findViewById<View>(R.id.fab)?.visibility = View.GONE
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