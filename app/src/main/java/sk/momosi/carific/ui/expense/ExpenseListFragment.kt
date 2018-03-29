package sk.momosi.carific.ui.expense


import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_expense_list.*

import sk.momosi.carific.R

class ExpenseListFragment : Fragment() {

    private lateinit var param1: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        param1 = arguments?.getString(ARGUMENT_CAR_ID) ?: throw IllegalArgumentException("Car id argument missing")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expense_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expense_list.adapter = ExpenseListAdapter()
        expense_list.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.navigation_expenses)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(true, true)
    }


    companion object {

        private const val ARGUMENT_CAR_ID = "car_id"

        @JvmStatic
        fun newInstance(param1: String) =
                ExpenseListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARGUMENT_CAR_ID, param1)
                    }
                }
    }
}
