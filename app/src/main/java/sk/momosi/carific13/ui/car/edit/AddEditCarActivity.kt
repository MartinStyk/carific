package sk.momosi.carific13.ui.car.edit

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_add_edit_car.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import sk.momosi.carific13.R
import sk.momosi.carific13.databinding.ActivityAddEditCarBinding
import sk.momosi.carific13.dependencyinjection.utils.ViewModelFactory
import sk.momosi.carific13.model.VehicleType
import sk.momosi.carific13.ui.car.CarTypeSpinnerAdapter
import sk.momosi.carific13.util.data.SnackbarMessage
import sk.momosi.carific13.util.extensions.animateError
import sk.momosi.carific13.util.extensions.animateSuccess
import sk.momosi.carific13.util.extensions.provideViewModel
import java.io.File
import javax.inject.Inject

@RuntimePermissions
class AddEditCarActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: AddEditCarViewModel

    lateinit var binding: ActivityAddEditCarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_car)

        viewModel = provideViewModel(viewModelFactory)

        binding.viewmodel = viewModel

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSuccessListener()

        setupErrorListener()

        setupAddButton()

        setupCarTypeSpinner()

        setupPicture()

        setupSnackbar()

        loadData()

    }

    private fun setupSuccessListener() = viewModel.taskFished.observe(this, Observer {
        binding.carAddSave.animateSuccess()

        Handler().postDelayed({
            finish().apply { setResult(Activity.RESULT_OK) }
        }, 350)
    })

    private fun setupErrorListener() = viewModel.taskError.observe(this, Observer {
        binding.carAddSave.animateError()
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

    private fun setupPicture() {
        viewModel.selectPicture.observe(this, Observer {
            pickImageWithPermissionCheck()
        })
    }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(this, object : SnackbarMessage.SnackbarObserver {
            override fun onNewMessage(snackbarMessageResourceId: Int) {
                Snackbar.make(binding.root, snackbarMessageResourceId, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun pickImage() {
        CropImage.startPickImageActivity(this)
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun cropImage(imageUri: Uri) {
        CropImage.activity(imageUri)
                .setAspectRatio(16, 9)
                .setOutputCompressQuality(50)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputUri(android.net.Uri.fromFile(File(filesDir, System.currentTimeMillis().toString() + ".jpg")))
                .start(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun removeCar() {
        AlertDialog.Builder(this)
                .setTitle(R.string.car_delete_dialog_title)
                .setMessage(R.string.car_delete_dialog_message)
                .setIcon(R.drawable.ic_tow)
                .setPositiveButton(R.string.car_delete, { _: DialogInterface, _: Int ->
                    viewModel.removeCar()
                })
                .setNegativeButton(R.string.dismiss, { _: DialogInterface, _: Int ->
                })
                .create()
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> cropImageWithPermissionCheck(CropImage.getPickImageResultUri(this, data))
                    else -> Snackbar.make(binding.root, R.string.car_create_picture_fail, Snackbar.LENGTH_SHORT).show()
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        viewModel.picturePath.set(CropImage.getActivityResult(data).uri.path)
                    }
                    else -> {
                        Snackbar.make(binding.root, R.string.car_create_picture_fail, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return if (intent?.extras?.getString(ARG_CAR_ID) != null) {
            menuInflater.inflate(R.menu.menu_add_edit_car, menu)
            return true
        } else {
            super.onCreateOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_remove -> {
                removeCar()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val ARG_CAR_ID = "car_id_edit"
    }

}
