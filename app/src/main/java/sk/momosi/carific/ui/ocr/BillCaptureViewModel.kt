package sk.momosi.carific.ui.ocr

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.support.annotation.WorkerThread
import android.util.Log
import com.google.android.gms.vision.text.Element
import sk.momosi.carific.R
import sk.momosi.carific.util.data.SingleLiveEvent
import sk.momosi.carific.util.data.SnackbarMessage
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.absoluteValue

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class BillCaptureViewModel(application: Application) : AndroidViewModel(application) {

    enum class Step { AUTO_DETECT, VOLUME, TOTAL_PRICE }
    var currentStep: Step = Step.AUTO_DETECT

    val snackbarMessage = SnackbarMessage()
    val autoDetectionSnackbar = SnackbarMessage()

    val dataReadComplete = SingleLiveEvent<Triple<BigDecimal, BigDecimal, BigDecimal>>()

    private var priceTotal = BigDecimal.ZERO
    private var pricePerUnit = BigDecimal.ZERO
    private var volume = BigDecimal.ZERO

    fun start() {
        if (currentStep == Step.AUTO_DETECT) {
            currentStep = Step.AUTO_DETECT
            autoDetectionSnackbar.value = R.string.bill_capture_detection_auto
        }
    }

    fun switchToManualDetection() {
        currentStep = Step.VOLUME
        snackbarMessage.value = R.string.bill_capture_enter_fuel_volume
    }


    /**
     * Called when user selects detected value on manual detection
     */
    fun setCapturedData(text: String) {
        val fixedText = text.replace("O", "0").replace(",", ".")

        val number: BigDecimal = try {
            BigDecimal(fixedText)
        } catch (e: NumberFormatException) {
            Log.d(TAG, "Can not parse big decimal: ${fixedText} ", e)
            return
        }


        when (currentStep) {
            Step.VOLUME -> {
                volume = number
                currentStep = Step.TOTAL_PRICE
                snackbarMessage.value = R.string.bill_capture_enter_price_total
            }
            Step.TOTAL_PRICE -> {
                priceTotal = number
                pricePerUnit = priceTotal.divide(volume, 2, RoundingMode.HALF_EVEN)
                dataReadComplete.value = Triple(priceTotal, pricePerUnit, volume)
            }
        }
    }

    /**
     * Called by BillDetectorProcessor during automatic value recognition
     */
    @WorkerThread
    fun autoOcrHandling(values: Map<Element, Double>) {
        val sortedValues = values.entries.sortedBy { (_, value) -> value }

        var product: Map.Entry<Element, Double>? = null
        var a: Map.Entry<Element, Double>? = null
        var b: Map.Entry<Element, Double>? = null


        for (productEntry in sortedValues) {
            for (aEntry in sortedValues) {
                for (bEntry in sortedValues) {
                    if (isCorrectDetection(totalPrice = productEntry, unitPrice = aEntry, volume = bEntry)) {
                        product = productEntry
                        a = aEntry
                        b = bEntry
                    }
                }
            }
        }

        if (a == null || b == null || product == null) {
            Log.d(TAG, "Auto detection not successful")
            return
        }

        val totalPriceDecimal = product.value.toBigDecimal()
        val unitPriceDecimal: BigDecimal
        val volumeDecimal: BigDecimal

        // volume should be on left side of a bill. Unit price is on right side
        if (a.key.boundingBox.left < b.key.boundingBox.left) {
            volumeDecimal = a.value.toBigDecimal()
            unitPriceDecimal = b.value.toBigDecimal()
        } else {
            volumeDecimal = b.value.toBigDecimal()
            unitPriceDecimal = a.value.toBigDecimal()
        }

        dataReadComplete.postValue(Triple(totalPriceDecimal, unitPriceDecimal, volumeDecimal))
    }

    private fun isCorrectDetection(totalPrice: Map.Entry<Element, Double>, unitPrice: Map.Entry<Element, Double>,
                                   volume: Map.Entry<Element, Double>): Boolean {
        // check of math criteria
        val isCorrectProduct = (unitPrice.value * volume.value - totalPrice.value).absoluteValue < 0.1
        if (!isCorrectProduct) return false

        // additional checks on common errors
        val isNotOne = totalPrice.value != 1.0 && volume.value != 1.0
        val isNotTheSame = totalPrice.value != unitPrice.value && volume.value != unitPrice.value

        // volume and unit price should be on one line
        val isOnOneLine = unitPrice.key.boundingBox.centerY() - volume.key.boundingBox.centerY() < 2

        return isNotOne && isNotTheSame && isOnOneLine
    }

    companion object {
        val TAG = BillCaptureViewModel::class.java.simpleName
    }

}