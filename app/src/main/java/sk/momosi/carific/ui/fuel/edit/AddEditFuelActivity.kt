package sk.momosi.carific.ui.fuel.edit

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_edit_fuel.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.ActivityAddEditFuelBinding
import sk.momosi.carific.model.Refueling
import sk.momosi.carific.model.User
import sk.momosi.carific.util.data.SnackbarMessage
import java.util.*

class AddEditFuelActivity : AppCompatActivity() {

    lateinit var viewModel: AddEditFuelViewModel

    lateinit var binding: ActivityAddEditFuelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_fuel)

        viewModel = ViewModelProviders.of(this).get(AddEditFuelViewModel::class.java)

        binding.viewmodel = viewModel

        setSupportActionBar(toolbar)

        setupNavigation()

        setupAddButton()

        setupDatePicker()

        setupSnackbar()

        loadData()
    }

    private fun setupNavigation() = viewModel.taskFished.observe(this, Observer {
        // TODO remove toast
        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    })

    private fun loadData() = viewModel.start(getCarId(), getRefueling(), getUser())

    private fun setupAddButton() = refueling_add_save.setOnClickListener { viewModel.saveRefueling() }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(this, object : SnackbarMessage.SnackbarObserver {
            override fun onNewMessage(snackbarMessageResourceId: Int) {
                Snackbar.make(binding.root, snackbarMessageResourceId, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupDatePicker() {

        refueling_add_date_picker.setOnClickListener {
            DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, day)
                    viewModel.date.set(calendar.time)
                }
            }, viewModel.date.get()!!.year + 1900, viewModel.date.get()!!.month, viewModel.date.get()!!.day).show()
            // TODO do not use deprecated fields
        }
    }

    private fun removeRefueling() {
        AlertDialog.Builder(this)
                .setTitle(R.string.refueling_delete_dialog_title)
                .setMessage(R.string.refueling_delete_dialog_message)
                .setIcon(R.drawable.ic_delete_black)
                .setPositiveButton(R.string.car_delete, { _: DialogInterface, _: Int ->
                    viewModel.removeRefueling()
                })
                .setNegativeButton(R.string.dismiss, { _: DialogInterface, _: Int ->
                })
                .create()
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return if (getRefueling() != null) {
            menuInflater.inflate(R.menu.menu_add_edit_car, menu)
            return true
        } else {
            super.onCreateOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_remove -> {
                removeRefueling()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getCarId() = intent?.extras?.getString(ARG_CAR_ID)
            ?: throw IllegalArgumentException("Car id passed to AddEditFuelActivity is null")

    private fun getUser() = intent?.extras?.getParcelable<User>(ARG_USER)
            ?: throw IllegalArgumentException("User passed to AddEditFuelActivity is null")

    private fun getRefueling() = intent?.extras?.getParcelable<Refueling>(ARG_REFUELING)

    companion object {
        const val ARG_CAR_ID = "car_id_edit"
        const val ARG_REFUELING = "refueling_edit"
        const val ARG_USER = "user"
    }

}
