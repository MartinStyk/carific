package sk.momosi.carific13.ui.car.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sk.momosi.carific13.databinding.ListItemCarBinding
import sk.momosi.carific13.model.Car
import sk.momosi.carific13.ui.ListItemUserInteractionListener

/**
 * @author Martin Styk
 * @date 01.04.2018.
 */
class CarListAdapter(var data: List<Car> = emptyList(),
                         val viewModel: CarListViewModel)
    : RecyclerView.Adapter<CarListAdapter.ViewHolder>() {

    init {
        this.setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ListItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        itemBinding.listener = object : ListItemUserInteractionListener<Car> {
            override fun onItemClick(item: Car) {
                viewModel.carClickEvent.value = item
            }
        }

        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = position.toLong()

    inner class ViewHolder(val binding: ListItemCarBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Car) {
            binding.car = item
            binding.executePendingBindings()
        }
    }

    fun replaceData(data: List<Car>) {
        this.data = data
        notifyDataSetChanged()
    }
}