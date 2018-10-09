package sk.momosi.carific13.ui.car.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_car_list.*
import sk.momosi.carific13.R
import sk.momosi.carific13.databinding.ActivityCarListBinding
import sk.momosi.carific13.ui.car.edit.AddEditCarActivity

open class CarListActivity : AppCompatActivity() {

    lateinit var binding: ActivityCarListBinding
    lateinit var viewModel: CarListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(CarListViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_car_list)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupAddButton()

        setupList()

        setupListItemClicks()

        viewModel.loadData()

    }

    private fun setupAddButton() {
        car_list_add.setOnClickListener {
            startActivity(Intent(this, AddEditCarActivity::class.java))
        }
    }

    private fun setupList() {
        car_list.adapter = CarListAdapter(viewModel = viewModel)
    }

    open fun setupListItemClicks() {
        viewModel.carClickEvent.observe(this, Observer {
            it?.let {
                val editIntent = Intent(this, AddEditCarActivity::class.java)
                editIntent.putExtra(AddEditCarActivity.ARG_CAR_ID, it.id)

                startActivity(editIntent)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
