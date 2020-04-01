/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.momosi.carific13.ui.ocr

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.snackbar.Snackbar
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import sk.momosi.carific13.R
import sk.momosi.carific13.ui.ocr.camera.CameraSource
import sk.momosi.carific13.ui.ocr.camera.CameraSourcePreview
import sk.momosi.carific13.ui.ocr.camera.GraphicOverlay
import sk.momosi.carific13.ui.ocr.camera.OcrGraphic
import sk.momosi.carific13.util.data.SnackbarMessage
import java.io.IOException

@RuntimePermissions
class BillCaptureActivity : AppCompatActivity() {

    private var cameraSource: CameraSource? = null
    private lateinit var preview: CameraSourcePreview
    private lateinit var graphicOverlay: GraphicOverlay<OcrGraphic>

    // Helper objects for detecting taps and pinches.
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector

    private lateinit var viewModel: BillCaptureViewModel

    private var messageSnackbar: Snackbar? = null

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_bill_capture)

        preview = findViewById(R.id.preview)
        graphicOverlay = findViewById(R.id.graphicOverlay)

        viewModel = ViewModelProviders.of(this).get(BillCaptureViewModel::class.java)

        gestureDetector = GestureDetector(this, CaptureGestureListener())
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        createCameraSourceWithPermissionCheck()

        setupSnackBar()

        setupCompletionListener()

        viewModel.start()
    }


    override fun onTouchEvent(e: MotionEvent): Boolean {
        val scaleGesture = scaleGestureDetector.onTouchEvent(e)
        val gesture = gestureDetector.onTouchEvent(e)

        return scaleGesture || gesture || super.onTouchEvent(e)
    }

    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        preview.stop()
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        preview.release()
    }

    private fun setupSnackBar() {

        viewModel.snackbarMessage.observe(this, object : SnackbarMessage.SnackbarObserver {
            override fun onNewMessage(snackbarMessageResourceId: Int) {
                messageSnackbar?.dismiss()
                messageSnackbar = Snackbar
                        .make(findViewById(android.R.id.content), snackbarMessageResourceId, Snackbar.LENGTH_INDEFINITE)
                messageSnackbar?.show()
            }
        })

        viewModel.autoDetectionSnackbar.observe(this, object : SnackbarMessage.SnackbarObserver {
            override fun onNewMessage(snackbarMessageResourceId: Int) {
                messageSnackbar = Snackbar.make(findViewById(android.R.id.content), snackbarMessageResourceId, Snackbar.LENGTH_INDEFINITE)

                messageSnackbar?.setAction(R.string.bill_capture_detection_manual, {
                    messageSnackbar?.dismiss()
                    viewModel.switchToManualDetection()
                })

                messageSnackbar?.show()
            }
        })
    }

    private fun setupCompletionListener() {
        viewModel.dataReadComplete.observe(this, Observer {
            val data = Intent().apply {
                putExtra(OCR_PRICE_TOTAL, it?.first)
                putExtra(OCR_PRICE_UNIT, it?.second)
                putExtra(OCR_VOLUME, it?.third)

            }
            setResult(CommonStatusCodes.SUCCESS, data)
            finish()
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    /**
     * Creates and starts the camera.
     */
    @NeedsPermission(Manifest.permission.CAMERA)
    @SuppressLint("InlinedApi")
    fun createCameraSource() {
        val context = applicationContext

        val textRecognizer = TextRecognizer.Builder(context).build()
                .apply { setProcessor(BillDetectorProcessor(graphicOverlay, viewModel)) }

        if (!textRecognizer.isOperational) {
            Log.w(TAG, "Detector dependencies are not yet available.")

            val hasLowStorage = registerReceiver(null, IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)) != null

            if (hasLowStorage) {
                Toast.makeText(this, R.string.bill_capture_low_storage_error, Toast.LENGTH_LONG).show()
                Log.w(TAG, getString(R.string.bill_capture_low_storage_error))
            }
        }

        // Creates and starts the camera.
        cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(1.0f)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                .build()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraPermissionDenied() = finish().apply { setResult(CommonStatusCodes.CANCELED) }

    @Throws(SecurityException::class)
    private fun startCameraSource() {
        // Check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS).show()
        }

        cameraSource?.let {
            try {
                preview.start(it, graphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource?.release()
                cameraSource = null
            }
        }

    }

    /**
     * onTap is called to capture the first TextBlock under the tap location and return it to
     * the Initializing Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private fun onTap(rawX: Float, rawY: Float): Boolean {
        val graphic = graphicOverlay.getGraphicAtLocation(rawX, rawY)
        if (graphic != null) {
            val text = graphic.word
            if (text?.value != null) {
                viewModel.setCapturedData(text = text.value)
            } else {
                Log.d(TAG, "text data is null")
            }
        } else {
            Log.d(TAG, "no text detected")
        }

        return graphic?.word != null
    }

    private inner class CaptureGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return onTap(e.rawX, e.rawY) || super.onSingleTapConfirmed(e)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {

        override fun onScale(detector: ScaleGestureDetector) = false

        override fun onScaleBegin(detector: ScaleGestureDetector) = true

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            cameraSource?.doZoom(detector.scaleFactor)
        }
    }

    companion object {
        private val TAG = BillCaptureActivity::class.java.simpleName

        // Intent request code to handle updating play services if needed.
        private val RC_HANDLE_GMS = 9001

        // Constants used to pass extra data in the intent
        const val OCR_PRICE_TOTAL = "ocr_total_price"
        const val OCR_PRICE_UNIT = "ocr_unit_price"
        const val OCR_VOLUME = "ocr_volume"

    }
}
