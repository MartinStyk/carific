package sk.momosi.carific.ui.ocr

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import sk.momosi.carific.R
import sk.momosi.carific.util.data.SingleLiveEvent
import sk.momosi.carific.util.data.SnackbarMessage
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
class OcrCaptureViewModel(application: Application) : AndroidViewModel(application) {

    private enum class Step { VOLUME, TOTAL_PRICE }

    private var currentStep: Step = Step.VOLUME

    val snackbarMessage = SnackbarMessage()
    val dataReadComplete = SingleLiveEvent<Triple<BigDecimal, BigDecimal, BigDecimal>>()


    private var priceTotal = BigDecimal.ZERO
    private var pricePerUnit = BigDecimal.ZERO
    private var volume = BigDecimal.ZERO

    fun start() {
        currentStep = Step.VOLUME
        snackbarMessage.value = R.string.read_text_enter_fuel_volume
    }

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
                snackbarMessage.value = R.string.read_text_enter_price_total
            }
            Step.TOTAL_PRICE -> {
                priceTotal = number
                pricePerUnit = priceTotal.divide(volume, 2, RoundingMode.HALF_EVEN)
                dataReadComplete.value = Triple(priceTotal, pricePerUnit, volume)
            }
        }
    }

    companion object {
        val TAG = OcrCaptureViewModel::class.java.simpleName
    }

}