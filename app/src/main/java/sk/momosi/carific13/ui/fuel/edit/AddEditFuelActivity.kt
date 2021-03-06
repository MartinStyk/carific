package sk.momosi.carific13.ui.fuel.edit

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_add_edit_fuel.*
import sk.momosi.carific13.R
import sk.momosi.carific13.databinding.ActivityAddEditFuelBinding
import sk.momosi.carific13.dependencyinjection.utils.ViewModelFactory
import sk.momosi.carific13.model.Refueling
import sk.momosi.carific13.ui.car.achievements.BaseAchievementActivity
import sk.momosi.carific13.ui.ocr.BillCaptureActivity
import sk.momosi.carific13.util.data.SnackbarMessage
import sk.momosi.carific13.util.extensions.animateError
import sk.momosi.carific13.util.extensions.animateSuccess
import sk.momosi.carific13.util.extensions.provideViewModel
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class AddEditFuelActivity : BaseAchievementActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: AddEditFuelViewModel

    lateinit var binding: ActivityAddEditFuelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = provideViewModel(viewModelFactory)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_fuel)
        binding.viewmodel = viewModel

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        setupSuccessListener()

        setupErrorListener()

        setupAddButton()

        setupOcrButtons()

        setupDatePicker()

        setupTimePicker()

        setupSnackbar()

        loadData()

        if (isEnabledShortcutToOcr()) {
            startActivityForResult(Intent(this, BillCaptureActivity::class.java), OCR_FUEL_CAPTURE_RESULT)
        }
    }

    private fun setupSuccessListener() = viewModel.taskFished.observe(this, Observer {
        binding.refuelingAddSave.animateSuccess()

        Handler().postDelayed({
            finish().apply { setResult(Activity.RESULT_OK) }
        }, 350)
    })

    private fun setupErrorListener() = viewModel.taskError.observe(this, Observer {
        binding.refuelingAddSave.animateError()
    })

    private fun loadData() = viewModel.start(getCarId(), getRefueling(), getCurrency())

    private fun setupAddButton() = refueling_add_save.setOnClickListener {
        viewModel.saveRefueling()
    }


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
                .setPositiveButton(R.string.car_delete) { _: DialogInterface, _: Int ->
                    viewModel.removeRefueling()
                }
                .setNegativeButton(R.string.dismiss) { _: DialogInterface, _: Int ->
                }
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
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getCarId() = intent?.extras?.getString(ARG_CAR_ID)
            ?: throw IllegalArgumentException("Car id passed to AddEditFuelActivity is null")

    private fun getCurrency() = intent?.extras?.getString(ARG_CURRENCY)
            ?: throw IllegalArgumentException("Currency of user passed to AddEditFuelActivity is null")

    private fun getRefueling() = intent?.extras?.getParcelable<Refueling>(ARG_REFUELING)

    private fun isEnabledShortcutToOcr() = intent?.extras?.getBoolean(ARG_OPEN_OCR) ?: false

    companion object {

        private val TAG = AddEditFuelActivity::class.java.simpleName

        const val ARG_CAR_ID = "car_id_edit"
        const val ARG_REFUELING = "refueling_edit"
        const val ARG_CURRENCY = "user_currency"
        const val ARG_OPEN_OCR = "open_ocr_camera"

        const val OCR_FUEL_CAPTURE_RESULT = 123

    }

}
