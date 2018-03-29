package sk.momosi.carific.ui.expense


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_expense_list.*
import sk.momosi.carific.R
import sk.momosi.carific.model.Expense


class ExpenseListFragment : Fragment() {

    private lateinit var carId: String
    private lateinit var viewModel: ExpensesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carId = arguments?.getString(ARGUMENT_CAR_ID) ?: throw IllegalArgumentException("Car id argument missing")
        viewModel = ViewModelProviders.of(this).get(ExpensesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expense_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expense_list.adapter = ExpenseListAdapter(emptyList<Expense>())
        expense_list.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.navigation_expenses)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(true, true)
    }

    override fun onResume() {
        super.onResume()

        viewModel.getExpenses().observe(this, Observer {
            Log.d(ExpenseListFragment.TAG, it.toString())
            expense_list.adapter = ExpenseListAdapter(it ?: return@Observer)
        })

    }


    companion object {

        private const val ARGUMENT_CAR_ID = "car_id"
        private val TAG = ExpenseListFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(param1: String) =
                ExpenseListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARGUMENT_CAR_ID, param1)
                    }
                }
    }
}
