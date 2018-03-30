package sk.momosi.carific.ui.expense

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import sk.momosi.carific.databinding.ListItemExpenseBinding
import sk.momosi.carific.model.Expense

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class ExpenseListAdapter(var data: List<Expense> = emptyList()) : RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {

    init {
        this.setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ListItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = position.toLong()

    inner class ViewHolder(val binding: ListItemExpenseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Expense) {
            binding.expense = item
            binding.executePendingBindings()
        }
    }

    fun replaceData(data: List<Expense>){
        this.data = data
        notifyDataSetChanged()
    }
}