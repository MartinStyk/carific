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
package sk.momosi.carific.ui.ocr

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import sk.momosi.carific.R
import sk.momosi.carific.ui.ocr.camera.CameraSource
import sk.momosi.carific.ui.ocr.camera.CameraSourcePreview
import sk.momosi.carific.ui.ocr.camera.GraphicOverlay
import java.io.IOException

/**
 * Activity for the multi-tracker app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
@RuntimePermissions
class OcrCaptureActivity : AppCompatActivity() {

    private var cameraSource: CameraSource? = null
    private lateinit var preview: CameraSourcePreview
    private lateinit var graphicOverlay: GraphicOverlay<OcrGraphic>

    // Helper objects for detecting taps and pinches.
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_ocr_capture)

        preview = findViewById(R.id.preview)
        graphicOverlay = findViewById(R.id.graphicOverlay)

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        createCameraSourceWithPermissionCheck()


        gestureDetector = GestureDetector(this, CaptureGestureListener())
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        Snackbar.make(graphicOverlay, "Tap to capture. Pinch/Stretch to zoom",
                Snackbar.LENGTH_LONG)
                .show()
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

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        val textRecognizer = TextRecognizer.Builder(context).build()
        textRecognizer.setProcessor(OcrDetectorProcessor(graphicOverlay))

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = registerReceiver(null, lowstorageFilter) != null

            if (hasLowStorage) {
                Toast.makeText(this, R.string.read_text_low_storage_error, Toast.LENGTH_LONG).show()
                Log.w(TAG, getString(R.string.read_text_low_storage_error))
            }
        }

        // Creates and starts the camera.
        cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                .build()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraPermissionDenied() = finish().apply { setResult(CommonStatusCodes.CANCELED) }


    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
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
        var text: TextBlock? = null
        if (graphic != null) {
            text = graphic.textBlock
            if (text?.value != null) {
                val data = Intent()
                data.putExtra(TEXT_BLOCK_OBJECT, text.value)
                setResult(CommonStatusCodes.SUCCESS, data)
                finish()
            } else {
                Log.d(TAG, "text data is null")
            }
        } else {
            Log.d(TAG, "no text detected")
        }
        return text != null
    }

    private inner class CaptureGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return onTap(e.rawX, e.rawY) || super.onSingleTapConfirmed(e)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return false
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         *
         *
         * Once a scale has ended, [ScaleGestureDetector.getFocusX]
         * and [ScaleGestureDetector.getFocusY] will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         */
        override fun onScaleEnd(detector: ScaleGestureDetector) {
            cameraSource?.doZoom(detector.scaleFactor)
        }
    }

    companion object {
        private val TAG = OcrCaptureActivity::class.java.simpleName

        // Intent request code to handle updating play services if needed.
        private val RC_HANDLE_GMS = 9001

        // Constants used to pass extra data in the intent
        const val TEXT_BLOCK_OBJECT = "String"
    }
}
