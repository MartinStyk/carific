package sk.momosi.carific13.util

import android.content.Context
import sk.momosi.carific13.R
import sk.momosi.carific13.model.Car
import sk.momosi.carific13.model.DistanceUnit
import sk.momosi.carific13.model.VolumeUnit
import java.math.BigDecimal
import java.math.RoundingMode

object UnitsUtil {

    val ONE_LITRE_IS_US_GALLONS = BigDecimal.valueOf(0.26417205235815)
    val ONE_LITRE_IS_UK_GALLONS = BigDecimal.valueOf(0.21996923465436)
    val ONE_UK_GALLON_IS_LITRES = BigDecimal.ONE.divide(ONE_LITRE_IS_UK_GALLONS, 14, RoundingMode.HALF_EVEN)
    val ONE_US_GALLON_IS_LITRES = BigDecimal.ONE.divide(ONE_LITRE_IS_US_GALLONS, 14, RoundingMode.HALF_EVEN)

    @JvmStatic
    fun getVolumeUnits(car: Car?, context: Context): String {
        return when (car?.volumeUnit) {
            VolumeUnit.LITRE -> context.getString(R.string.units_litre)
            VolumeUnit.GALLONS_UK, VolumeUnit.GALLONS_US -> context.getString(R.string.units_gallon)
            else -> ""
        }
    }

    @JvmStatic
    fun getConsumptionUnits(car: Car?, context: Context): String {
        return if (car == null)
            ""
        else if (this.getDistanceUnits(car) === sk.momosi.carific13.model.DistanceUnit.MI) {
            context.getString(R.string.units_mpg)
        } else {
            context.getString(R.string.units_litrePer100km)
        }
    }


    @JvmStatic
    fun isDefaultMpg(car: Car?, context: Context): Boolean {
        return getConsumptionUnits(car, context).equals("mpg")
    }

    @JvmStatic
    fun getDistanceUnitsString(car: Car?, context: Context): String {
        return if (car == null || getDistanceUnits(car) === DistanceUnit.KM) {
            context.getString(R.string.units_km)
        } else {
            context.getString(R.string.units_mi)
        }
    }


    @JvmStatic
    fun getDistanceUnits(car: Car): DistanceUnit {
        return if (car.volumeUnit == null || car.volumeUnit === sk.momosi.carific13.model.VolumeUnit.LITRE) DistanceUnit.KM else DistanceUnit.MI
    }

}
