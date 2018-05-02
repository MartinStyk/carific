package sk.momosi.carific.ui.fuel.edit

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.android.synthetic.main.activity_add_edit_fuel.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.ActivityAddEditFuelBinding
import sk.momosi.carific.model.Refueling
import sk.momosi.carific.model.User
import sk.momosi.carific.ui.main.MainActivity
import sk.momosi.carific.ui.ocr.BillCaptureActivity
import sk.momosi.carific.util.data.SnackbarMessage
import java.math.BigDecimal
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        setupCompletionListener()

        setupAddButton()

        setupOcrButtons()

        setupDatePicker()

        setupTimePicker()

        setupSnackbar()

        loadData()
    }

    private fun setupCompletionListener() = viewModel.taskFished.observe(this, Observer {
        finish().apply { setResult(Activity.RESULT_OK) }
    })

    private fun loadData() = viewModel.start(getCarId(), getRefueling(), getUser())

    private fun setupAddButton() = refueling_add_save.setOnClickListener { viewModel.saveRefueling() }


    private fun setupOcrButtons() {
        refueling_add_ocr.setOnClickListener {
            startActivityForResult(Intent(this, BillCaptureActivity::class.java), OCR_FUEL_CAPTURE_RESULT)
        }
    }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(this, object : SnackbarMessage.SnackbarObserver {
            override fun onNewMessage(snackbarMessageResourceId: Int) {
                Snackbar.make(binding.root, snackbarMessageResourceId, Snackbar.LENGTH_SHORT).show()
            }

            override fun onNewMessage(text: String) {
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            OCR_FUEL_CAPTURE_RESULT -> {
                val priceTotal = data?.getSerializableExtra(BillCaptureActivity.OCR_PRICE_TOTAL)
                val priceUnit = data?.getSerializableExtra(BillCaptureActivity.OCR_PRICE_UNIT)
                val volume = data?.getSerializableExtra(BillCaptureActivity.OCR_VOLUME)

                if (resultCode == CommonStatusCodes.SUCCESS
                        && priceTotal != null
                        && priceUnit != null
                        && volume != null) {

                    viewModel.ocrCapturedFuel(priceTotal as BigDecimal, priceUnit as BigDecimal,
                            volume as BigDecimal)

                } else {
                    Log.d(TAG, "No Text captured, intent data is null")
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun setupDatePicker() {

        refueling_add_date_picker.setOnClickListener {
            DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
                    viewModel.setDate(year, month, day)
                }
            }, viewModel.date.get()!!.get(Calendar.YEAR), viewModel.date.get()!!.get(Calendar.MONTH), viewModel.date.get()!!.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupTimePicker() {

        refueling_add_time_picker.setOnClickListener {
            TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
                    viewModel.setTime(hourOfDay, minute)
                }
            }, viewModel.date.get()!!.get(Calendar.HOUR_OF_DAY), viewModel.date.get()!!.get(Calendar.MINUTE), DateFormat.is24HourFormat(this)).show()
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

            android.R.id.home -> {
//                NavUtils.navigateUpTo(this, Intent(this, MainActivity::class.java))
                NavUtils.navigateUpFromSameTask(this)
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

        private val TAG = AddEditFuelActivity::class.java.simpleName

        const val ARG_CAR_ID = "car_id_edit"
        const val ARG_REFUELING = "refueling_edit"
        const val ARG_USER = "user"

        const val OCR_FUEL_CAPTURE_RESULT = 123

    }

}
