package sk.momosi.carific.ui.expense.list

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
import kotlinx.android.synthetic.main.fragment_expense_list.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.FragmentExpenseListBinding
import sk.momosi.carific.model.Car
import sk.momosi.carific.model.User
import sk.momosi.carific.ui.expense.edit.AddEditExpenseActivity
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.model.SectionInfo


class ExpenseListFragment : Fragment() {

    private lateinit var viewModel: ExpensesViewModel
    private lateinit var binding: FragmentExpenseListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ExpensesViewModel::class.java)

        setupListItemClicks()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_expense_list, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()

        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.title_expense)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(true, true)

        viewModel.init(getCar().id, getUser())

        viewModel.loadData(getCar().id)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupAddButton()
    }

    private fun setupAddButton() {
        val addBtn = activity?.findViewById<FloatingActionButton>(R.id.fab)

        addBtn?.setOnClickListener {
            val createIntent = Intent(context, AddEditExpenseActivity::class.java)
            createIntent.putExtra(AddEditExpenseActivity.ARG_CAR_ID, viewModel.carId)
            createIntent.putExtra(AddEditExpenseActivity.ARG_USER, viewModel.user)

            startActivity(createIntent)
        }
    }

    private fun setupListItemClicks() {
        viewModel.expenseClickEvent.observe(this, Observer { expense ->
            val editIntent = Intent(context, AddEditExpenseActivity::class.java)
            editIntent.putExtra(AddEditExpenseActivity.ARG_CAR_ID, viewModel.carId)
            editIntent.putExtra(AddEditExpenseActivity.ARG_EXPENSE, expense)
            editIntent.putExtra(AddEditExpenseActivity.ARG_USER, viewModel.user)

            startActivity(editIntent)
        })
    }


    private fun setupList() {
        expense_list.adapter = ExpenseListAdapter(viewModel = viewModel)
        expense_list.addItemDecoration(
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
        private val TAG = ExpenseListFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(car: Car, user: User) =
                ExpenseListFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARGUMENT_CAR, car)
                        putParcelable(ARGUMENT_USER, user)
                    }
                }
    }
}
