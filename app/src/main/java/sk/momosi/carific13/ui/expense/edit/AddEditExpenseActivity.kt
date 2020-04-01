package sk.momosi.carific13.ui.expense.edit

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_add_edit_expense.*
import sk.momosi.carific13.R
import sk.momosi.carific13.databinding.ActivityAddEditExpenseBinding
import sk.momosi.carific13.dependencyinjection.utils.ViewModelFactory
import sk.momosi.carific13.model.Expense
import sk.momosi.carific13.ui.car.achievements.BaseAchievementActivity
import sk.momosi.carific13.util.data.SnackbarMessage
import sk.momosi.carific13.util.extensions.animateError
import sk.momosi.carific13.util.extensions.animateSuccess
import sk.momosi.carific13.util.extensions.provideViewModel
import java.util.*
import javax.inject.Inject

class AddEditExpenseActivity : BaseAchievementActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: AddEditExpenseViewModel

    lateinit var binding: ActivityAddEditExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = provideViewModel(viewModelFactory)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_expense)

        binding.viewmodel = viewModel

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSuccessListener()

        setupErrorListener()

        setupAddButton()

        setupDatePicker()

        setupTimePicker()

        setupSnackbar()

        loadData()

    }

    private fun setupSuccessListener() = viewModel.taskFinished.observe(this, Observer {

        binding.expenseAddSave.animateSuccess()

        Handler().postDelayed({
            finish().apply { setResult(Activity.RESULT_OK) }
        }, 350)
    })

    private fun setupErrorListener() = viewModel.taskError.observe(this, Observer {
        binding.expenseAddSave.animateError()
    })

    private fun loadData() = viewModel.start(getCarId(), getExpenses(), getCurrency())

    private fun setupAddButton() = expense_add_save.setOnClickListener {
        viewModel.saveExpense()
    }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(this, object : SnackbarMessage.SnackbarObserver {
            override fun onNewMessage(snackbarMessageResourceId: Int) {
                Snackbar.make(binding.root, snackbarMessageResourceId, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupDatePicker() {

        expense_add_date_picker.setOnClickListener {
            DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
                    viewModel.setDate(year, month, day)
                }
            }, viewModel.date.get()!!.get(Calendar.YEAR), viewModel.date.get()!!.get(Calendar.MONTH), viewModel.date.get()!!.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupTimePicker() {

        expense_add_time_picker.setOnClickListener {
            TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
                    viewModel.setTime(hourOfDay, minute)
                }
            }, viewModel.date.get()!!.get(Calendar.HOUR_OF_DAY), viewModel.date.get()!!.get(Calendar.MINUTE), DateFormat.is24HourFormat(this)).show()
        }

    }

    private fun removeExpense() {
        AlertDialog.Builder(this)
                .setTitle(R.string.expense_delete_dialog_title)
                .setMessage(R.string.expense_delete_dialog_message)
                .setIcon(R.drawable.ic_delete_black)
                .setPositiveButton(R.string.car_delete) { _: DialogInterface, _: Int ->
                    viewModel.removeExpense()
                }
                .setNegativeButton(R.string.dismiss) { _: DialogInterface, _: Int ->
                }
                .create()
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return if (getExpenses() != null) {
            menuInflater.inflate(R.menu.menu_add_edit_car, menu)
            return true
        } else {
            super.onCreateOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_remove -> {
                removeExpense()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getCarId() = intent?.extras?.getString(ARG_CAR_ID)
            ?: throw IllegalArgumentException("Car id passed to AddEditExpenseActivity is null")

    private fun getCurrency() = intent?.extras?.getString(ARG_CURRENCY)
            ?: throw IllegalArgumentException("Currency of ser passed to AddEditExpenseActivity is null")

    private fun getExpenses() = intent?.extras?.getParcelable<Expense>(ARG_EXPENSE)

    companion object {
        const val ARG_CAR_ID = "car_id_edit"
        const val ARG_EXPENSE = "expense_edit"
        const val ARG_CURRENCY = "user_currency"
    }
}
