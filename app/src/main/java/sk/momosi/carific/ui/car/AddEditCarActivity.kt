package sk.momosi.carific.ui.car

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_edit_car.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.ActivityAddEditCarBinding
import sk.momosi.carific.model.VehicleType


class AddEditCarActivity : AppCompatActivity() {

    lateinit var viewModel: AddEditCarViewModel

    lateinit var binding: ActivityAddEditCarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_car)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(AddEditCarViewModel::class.java)

        binding.viewmodel = viewModel

        setupNavigation()

        setupAddButton()

        setupCarTypeSpinner()

        loadData()

    }

    private fun setupNavigation() = viewModel.taskFished.observe(this, Observer {
        // TODO remove toast
        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    })

    private fun setupAddButton() = car_add_save.setOnClickListener { viewModel.saveCar() }

    private fun setupCarTypeSpinner() {
        binding.carCreateType.adapter = CarTypeSpinnerAdapter(this)

        binding.carCreateType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, number: Int, id: Long) = viewModel.type.set(VehicleType.values()[number])
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        viewModel.type.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                binding.carCreateType.setSelection(VehicleType.values().indexOf(viewModel.type.get()))
            }
        })
    }

    private fun loadData() = viewModel.start(intent?.extras?.getString(ARG_CAR_ID))


    companion object {
        const val ARG_CAR_ID = "car_id_edit"
    }

}
