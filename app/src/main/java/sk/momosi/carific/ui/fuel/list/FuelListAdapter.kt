package sk.momosi.carific.ui.fuel.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import sk.momosi.carific.databinding.ListItemFuelBinding
import sk.momosi.carific.model.Refueling
import sk.momosi.carific.ui.ListItemUserInteractionListener

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class FuelListAdapter(var data: List<Refueling> = emptyList(),
                      val viewModel: FuelListViewModel)
    : RecyclerView.Adapter<FuelListAdapter.ViewHolder>() {

    init {
        this.setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ListItemFuelBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        itemBinding.listener = object : ListItemUserInteractionListener<Refueling> {
            override fun onItemClick(item: Refueling) {
                viewModel.refuelClickEvent.value = item
            }
        }

        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = position.toLong()

    inner class ViewHolder(val binding: ListItemFuelBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Refueling) {
            binding.refueling = item
            binding.executePendingBindings()
        }
    }

    fun replaceData(data: List<Refueling>) {
        this.data = data
        notifyDataSetChanged()
    }
}