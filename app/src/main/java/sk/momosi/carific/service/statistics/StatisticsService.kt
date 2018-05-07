package sk.momosi.carific.service.statistics

import sk.momosi.carific.Statistics
import sk.momosi.carific.model.Expense
import sk.momosi.carific.model.Refueling
import sk.momosi.carific.model.VolumeUnit
import sk.momosi.carific.util.UnitsUtil
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.TimeUnit

class StatisticsService(
        private val refuelings: List<Refueling>,
        private val expenses: List<Expense>,
        private val volumeUnit: VolumeUnit) {


    val statistics: Statistics
        get() {
            val totalCostsFuel = totalPriceOfFillUps
            val totalCostsExpenses = totalPriceOfExpenses
            val totalPrice = totalCostsFuel + totalCostsExpenses

            val totalFuelVolume = totalFuelVolume
            val totalDrivenDistance = totalDrivenDistance

            val totalCostsPerDistance = if (totalDrivenDistance > 0)
                totalPrice.multiply(HUNDRED).divide(totalDrivenDistance.toBigDecimal(), 2, BigDecimal.ROUND_HALF_UP)
            else
                BigDecimal.ZERO
            val fuelCostsPerDistance = if (totalDrivenDistance > 0)
                totalCostsFuel.multiply(HUNDRED).divide(totalDrivenDistance.toBigDecimal(), 2, BigDecimal.ROUND_HALF_UP)
            else
                BigDecimal.ZERO
            val expenseCostsPerDistance = if (totalDrivenDistance > 0)
                totalCostsExpenses.multiply(HUNDRED).divide(totalDrivenDistance.toBigDecimal(), 2, BigDecimal.ROUND_HALF_UP)
            else
                BigDecimal.ZERO
            val averageFuelVolumePerFillUp = if (refuelings.isNotEmpty())
                totalFuelVolume.divide(refuelings.size.toBigDecimal(), 2, BigDecimal.ROUND_HALF_UP)
            else
                BigDecimal.ZERO
            val averageFuelPricePerFillUp = if (refuelings.isNotEmpty())
                totalCostsFuel.divide(refuelings.size.toBigDecimal(), 2, BigDecimal.ROUND_HALF_UP)
            else
                BigDecimal.ZERO

            val trackingDays = trackingDays

            val fuelConsumptionMinMax = fuelConsumptionMinMaxComputation()
            val distanceBetweenFullUps = distanceBetweenFillUpsComputation()
            val fuelUnitPrice = fuelUnitPriceComputation()

            return Statistics(
                    avgConsumption = averageConsumption,
                    avgConsumptionReversed = averageConsumptionReversed,
                    fuelConsumptionBest = fuelConsumptionMinMax.first,
                    fuelConsumptionWorst = fuelConsumptionMinMax.second,
                    totalCosts = totalCostsExpenses + totalCostsExpenses,
                    totalCostsFuel = totalCostsFuel,
                    totalCostsExpenses = totalCostsExpenses,
                    totalCostsPerDistance = totalCostsPerDistance,
                    totalNumberFillUps = refuelings.size,
                    totalNumberExpenses = expenses.size,
                    totalFuelVolume = totalFuelVolume,
                    totalDrivenDistance = totalDrivenDistance,
                    expenseCostsPerDistance = expenseCostsPerDistance,
                    fuelCostsPerDistance = fuelCostsPerDistance,
                    averageTotalCostPerWeek = getAveragePerTime(totalPrice, trackingDays, TimePeriod.WEEK),
                    averageTotalCostPerMonth = getAveragePerTime(totalPrice, trackingDays, TimePeriod.MONTH),
                    averageTotalCostPerYear = getAveragePerTime(totalPrice, trackingDays, TimePeriod.YEAR),
                    averageFuelCostPerWeek = getAveragePerTime(totalCostsFuel, trackingDays, TimePeriod.WEEK),
                    averageFuelCostPerMonth = getAveragePerTime(totalCostsFuel, trackingDays, TimePeriod.MONTH),
                    averageFuelCostPerYear = getAveragePerTime(totalCostsFuel, trackingDays, TimePeriod.YEAR),
                    averageExpenseCostPerWeek = getAveragePerTime(totalCostsExpenses, trackingDays, TimePeriod.WEEK),
                    averageExpenseCostPerMonth = getAveragePerTime(totalCostsExpenses, trackingDays, TimePeriod.MONTH),
                    averageExpenseCostPerYear = getAveragePerTime(totalCostsExpenses, trackingDays, TimePeriod.YEAR),
                    averageFuelVolumePerFillUp = averageFuelVolumePerFillUp,
                    averageFuelPricePerFillUp = averageFuelPricePerFillUp,
                    averageNumberOfFillUpsPerWeek = getAveragePerTime(refuelings.size.toBigDecimal(), trackingDays, TimePeriod.WEEK),
                    averageNumberOfFillUpsPerMonth = getAveragePerTime(refuelings.size.toBigDecimal(), trackingDays, TimePeriod.MONTH),
                    averageNumberOfFillUpsPerYear = getAveragePerTime(refuelings.size.toBigDecimal(), trackingDays, TimePeriod.YEAR),
                    distanceBetweenFillUpsLowest = distanceBetweenFullUps.first,
                    distanceBetweenFillUpsHighest = distanceBetweenFullUps.second,
                    distanceBetweenFillUpsAverage = distanceBetweenFullUps.third,
                    fuelUnitPriceLowest = fuelUnitPrice.first,
                    fuelUnitPriceHighest = fuelUnitPrice.second,
                    fuelUnitPriceAverage = fuelUnitPrice.third,
                    averageDistancePerDay = getAveragePerTime(totalDrivenDistance.toBigDecimal(), trackingDays, TimePeriod.DAY).toLong(),
                    averageDistancePerWeek = getAveragePerTime(totalDrivenDistance.toBigDecimal(), trackingDays, TimePeriod.WEEK).toLong(),
                    averageDistancePerYear = getAveragePerTime(totalDrivenDistance.toBigDecimal(), trackingDays, TimePeriod.YEAR).toLong(),
                    averageNumberOfExpensesPerWeek = getAveragePerTime(expenses.size.toBigDecimal(), trackingDays, TimePeriod.WEEK),
                    averageNumberOfExpensesPerMonth = getAveragePerTime(expenses.size.toBigDecimal(), trackingDays, TimePeriod.MONTH),
                    averageNumberOfExpensesPerYear = getAveragePerTime(expenses.size.toBigDecimal(), trackingDays, TimePeriod.YEAR),
                    trackingDays = trackingDays
            )
        }

    private val averageConsumption: BigDecimal
        get() {
            return getAverageConsumption(refuelings)
        }

    private val averageConsumptionReversed: BigDecimal
        get() {
            val distanceSum = refuelings.filter { it.consumption != null }.fold(0, { acc, refueling -> acc + refueling.distanceFromLast })
            val fuelSum = refuelings.filter { it.consumption != null }.fold(BigDecimal.ZERO, { acc, refueling -> acc + refueling.volume })

            if (distanceSum == 0 || fuelSum == BigDecimal.ZERO)
                return BigDecimal.ZERO

            when (volumeUnit) {
                VolumeUnit.LITRE -> {
                    return BigDecimal(distanceSum).divide(BigDecimal(100) * fuelSum, 2, RoundingMode.HALF_EVEN)
                }
                VolumeUnit.GALLONS_UK, VolumeUnit.GALLONS_US -> {
                    val milesPerOneLitre = averageConsumption.divide(
                            if (volumeUnit === VolumeUnit.GALLONS_UK)
                                UnitsUtil.ONE_UK_GALLON_IS_LITRES
                            else
                                UnitsUtil.ONE_US_GALLON_IS_LITRES,
                            2, RoundingMode.HALF_EVEN)

                    if (milesPerOneLitre.compareTo(BigDecimal.ZERO) == 0)
                        return BigDecimal.ZERO

                    val litrePerOneMile = BigDecimal.ONE.divide(milesPerOneLitre, 2, RoundingMode.HALF_UP)
                    return litrePerOneMile.multiply(HUNDRED)
                }
            }
            return BigDecimal.ZERO
        }

    private val totalPriceOfFillUps = refuelings.fold(BigDecimal.ZERO, { acc, expense -> acc + expense.priceTotal })

    private val totalPriceOfExpenses = expenses.fold(BigDecimal.ZERO, { acc, expense -> acc + expense.price })

    private val totalDrivenDistance: Long = refuelings.fold(0L, { acc, refueling -> acc + refueling.distanceFromLast })

    private val totalFuelVolume = refuelings.fold(BigDecimal.ZERO, { acc, refueling -> acc + refueling.volume })

    private val trackingDays: Long
        get() {
            val oldest = refuelings.minWith(kotlin.Comparator { refueling1, refueling2 -> if (refueling1.date.before(refueling2.date)) -1 else 1 })
                    ?.date?.time
            return if (oldest == null) {
                0
            } else {
                TimeUnit.DAYS.convert(Math.abs(Date().time - oldest), TimeUnit.MILLISECONDS)
            }
        }


    private fun fuelConsumptionMinMaxComputation(): Pair<BigDecimal, BigDecimal> {
        if (refuelings.isEmpty() || refuelings.all { it.consumption == null })
            return Pair(BigDecimal.ZERO, BigDecimal.ZERO)

        var lowest = BigDecimal.valueOf(1000000)
        var highest = BigDecimal.ZERO

        refuelings.filter { it.consumption != null }.forEach {
            if (lowest > it.consumption) lowest = it.consumption
            if (highest < it.consumption) highest = it.consumption
        }

        return Pair(lowest, highest)
    }

    private fun fuelUnitPriceComputation(): Triple<BigDecimal, BigDecimal, BigDecimal> {
        if (refuelings.isEmpty())
            return Triple(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        var fuelUnitPriceLowest = BigDecimal.valueOf(1000000)
        var fuelUnitPriceHighest = BigDecimal.ZERO
        var fuelUnitPriceSum = BigDecimal.ZERO

        refuelings.forEach {
            if (fuelUnitPriceLowest > it.pricePerLitre) fuelUnitPriceLowest = it.pricePerLitre
            if (fuelUnitPriceHighest < it.pricePerLitre) fuelUnitPriceHighest = it.pricePerLitre
            fuelUnitPriceSum += it.pricePerLitre
        }

        val fuelUnitPriceAvg = fuelUnitPriceSum.divide(BigDecimal(refuelings.size), 2, RoundingMode.HALF_EVEN)

        return Triple(fuelUnitPriceLowest, fuelUnitPriceHighest, fuelUnitPriceAvg)
    }


    private fun distanceBetweenFillUpsComputation(): Triple<Int, Int, Int> {
        if (refuelings.isEmpty())
            return Triple(0, 0, 0)

        var distanceBetweenFillUpsLowes = Integer.MAX_VALUE
        var distanceBetweenFillUpsHighest = 0
        var distanceBetweenFillUpsSum = 0

        refuelings.forEach {
            if (distanceBetweenFillUpsLowes > it.distanceFromLast) distanceBetweenFillUpsLowes = it.distanceFromLast
            if (distanceBetweenFillUpsHighest < it.distanceFromLast) distanceBetweenFillUpsHighest = it.distanceFromLast
            distanceBetweenFillUpsSum += it.distanceFromLast
        }

        val distanceBetweenFillUpsAvg = distanceBetweenFillUpsSum / refuelings.size

        return Triple(distanceBetweenFillUpsLowes, distanceBetweenFillUpsHighest, distanceBetweenFillUpsAvg)
    }

    private fun getAveragePerTime(totalCostAllTime: BigDecimal, trackingPeriod: Long, timePeriod: TimePeriod): BigDecimal {
        if (trackingPeriod == 0L) return BigDecimal.ZERO

        val trackingIntervals = trackingPeriod / timePeriod.days.toDouble()
        return totalCostAllTime.divide(BigDecimal.valueOf(trackingIntervals), 2, RoundingMode.HALF_UP)
    }

    private fun getAsArgument(value: Long): Array<String> {
        return arrayOf(value.toString())
    }

    private enum class TimePeriod constructor(val days: Int) {
        DAY(1),
        WEEK(7),
        MONTH(30),
        YEAR(365)
    }

    companion object {
        private val HUNDRED = BigDecimal.valueOf(100)

        fun getAverageConsumption(refuelings: List<Refueling>) : BigDecimal{
            val distanceSum = refuelings.filter { it.consumption != null }.fold(0, { acc, refueling -> acc + refueling.distanceFromLast })
            val fuelSum = refuelings.filter { it.consumption != null }.fold(BigDecimal.ZERO, { acc, refueling -> acc + refueling.volume })

            return if (distanceSum != 0 && fuelSum != BigDecimal.ZERO) {
                (BigDecimal(100) * fuelSum).divide(BigDecimal(distanceSum), 2, RoundingMode.HALF_EVEN)
            } else {
                BigDecimal.ZERO
            }
        }

    }

}

