package sk.momosi.carific.ui.expense

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import sk.momosi.carific.databinding.ListItemExpenseBinding
import sk.momosi.carific.model.Expense
import java.math.BigDecimal
import java.util.*

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class ExpenseListAdapter() : RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {

    private val data = listOf(
            Expense(price = BigDecimal.ONE,
                    date = Date(20, 1, 20),
                    info = "my Infooooo"),
            Expense(price = BigDecimal.TEN,
                    date = Date(30, 2, 22),
                    info = "secooond expense")
    )


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
}