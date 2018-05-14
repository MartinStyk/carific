package sk.momosi.carific13

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@SuppressLint("ParcelCreator")
public data class Statistics(
        // Consumption
        val avgConsumption: BigDecimal,
        val avgConsumptionReversed: BigDecimal,
        val isConsumptionReversedUnitMpg: Boolean = false,
        val fuelConsumptionBest: BigDecimal,
        val fuelConsumptionWorst: BigDecimal,

        // Totals
        val totalCosts: BigDecimal,
        val totalCostsFuel: BigDecimal,
        val totalCostsExpenses: BigDecimal,
        val totalNumberFillUps: Int = 0,
        val totalNumberExpenses: Int = 0,
        val totalFuelVolume: BigDecimal,
        val totalDrivenDistance: Long = 0,

        // Costs per distance
        val totalCostsPerDistance: BigDecimal,
        val fuelCostsPerDistance: BigDecimal,
        val expenseCostsPerDistance: BigDecimal,

        // Costs per time
        val averageTotalCostPerWeek: BigDecimal,
        val averageFuelCostPerWeek: BigDecimal,
        val averageExpenseCostPerWeek: BigDecimal,
        val averageTotalCostPerMonth: BigDecimal,
        val averageFuelCostPerMonth: BigDecimal,
        val averageExpenseCostPerMonth: BigDecimal,
        val averageTotalCostPerYear: BigDecimal,
        val averageFuelCostPerYear: BigDecimal,
        val averageExpenseCostPerYear: BigDecimal,

        // Refuelling statistics
        val averageFuelVolumePerFillUp: BigDecimal,
        val averageFuelPricePerFillUp: BigDecimal,
        val averageNumberOfFillUpsPerWeek: BigDecimal,
        val averageNumberOfFillUpsPerMonth: BigDecimal,
        val averageNumberOfFillUpsPerYear: BigDecimal,
        val distanceBetweenFillUpsAverage: Int = 0,
        val distanceBetweenFillUpsLowest: Int = 0,
        val distanceBetweenFillUpsHighest: Int = 0,

        // Fuel unit price
        val fuelUnitPriceAverage: BigDecimal,
        val fuelUnitPriceLowest: BigDecimal,
        val fuelUnitPriceHighest: BigDecimal,

        // Distance per time
        val averageDistancePerDay: Long = 0,
        val averageDistancePerWeek: Long = 0,
        val averageDistancePerMonth: Long = 0,
        val averageDistancePerYear: Long = 0,

        // Expense statistics
        val averageNumberOfExpensesPerWeek: BigDecimal,
        val averageNumberOfExpensesPerMonth: BigDecimal,
        val averageNumberOfExpensesPerYear: BigDecimal,

        // FuelUp usage
        val trackingDays: Long = 0
) : Parcelable