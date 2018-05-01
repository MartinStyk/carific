package sk.momosi.carific.ui.timeline.list

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import sk.momosi.carific.databinding.ListItemExpenseBinding
import sk.momosi.carific.databinding.ListItemFuelBinding
import sk.momosi.carific.model.Expense
import sk.momosi.carific.model.ListItem
import sk.momosi.carific.model.Refueling
import sk.momosi.carific.ui.ListItemUserInteractionListener

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class TimelineListAdapter(var data: List<ListItem> = emptyList(),
                          val viewModel: TimelineViewModel)
    : RecyclerView.Adapter<TimelineListAdapter.ViewHolder>() {

    init {
        this.setHasStableIds(true)
    }


    override fun getItemViewType(position: Int): Int {
        return viewModel.items[position].listItemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            ListItem.REFUELING -> {
                val fuelBinding = ListItemFuelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                fuelBinding.listener = object : ListItemUserInteractionListener<Refueling> {
                    override fun onItemClick(item: Refueling) {
                        viewModel.refuelClickEvent.value = item
                    }
                }
                fuelBinding.user = viewModel.user

                return RefuelingViewHolder(fuelBinding)
            }
            ListItem.EXPENSE -> {
                val expenseBinding = ListItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                expenseBinding.listener = object : ListItemUserInteractionListener<Expense> {
                    override fun onItemClick(item: Expense) {
                        viewModel.expenseClickEvent.value = item
                    }
                }
                expenseBinding.user = viewModel.user

                return ExpenseViewHolder(expenseBinding)
            }
            else -> throw IllegalStateException("Unsupported view holder")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = position.toLong()

    abstract inner class ViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(item: ListItem)
    }

    inner class RefuelingViewHolder(val binding: ListItemFuelBinding) : ViewHolder(binding) {

        override fun bind(item: ListItem) {
            item as Refueling
            binding.refueling = item
            binding.executePendingBindings()
        }
    }

    inner class ExpenseViewHolder(val binding: ListItemExpenseBinding) : ViewHolder(binding) {

        override fun bind(item: ListItem) {
            item as Expense
            binding.expense = item
            binding.executePendingBindings()
        }
    }

    fun replaceData(data: List<ListItem>) {
        this.data = data
        notifyDataSetChanged()
    }
}