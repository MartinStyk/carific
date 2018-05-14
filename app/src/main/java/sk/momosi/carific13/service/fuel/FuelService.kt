package sk.momosi.carific13.service.fuel

import com.google.firebase.database.FirebaseDatabase
import sk.momosi.carific13.model.Refueling
import sk.momosi.carific13.model.VolumeUnit
import java.math.BigDecimal
import java.math.RoundingMode

class FuelService {


    fun insertRefueling(vehicleId: String, refueling: Refueling, allRefuelings: List<Refueling>) {
        if (refueling.isFull) {
            insertFullRefueling(vehicleId, refueling, allRefuelings)
        } else {
            insertNotFullRefueling(vehicleId, refueling, allRefuelings)
        }
    }

    /**
     * Insert new refueling with complete logic of consumption recalculation
     *
     * @param refueling insertedRefueling, not added to list so far
     * @param allRefuelings sorted list of all vehicle items
     */
    fun insertFullRefueling(vehicleId: String, refueling: Refueling, allRefuelings: List<Refueling>) {


        // find first full older fillUp
        var olderFuelUpsVol = refueling.volume
        var olderFuelUpsDistance = refueling.distanceFromLast
        var existsOlderFullFillUp = false

        val olderRefuelings = mutableListOf<Refueling>()


        run loop@{
            allRefuelings.filter { it.date < refueling.date }.sorted().forEach {
                if (it.isFull) {
                    existsOlderFullFillUp = true
                    return@loop
                }
                olderRefuelings.add(it)
                olderFuelUpsDistance += it.distanceFromLast
                olderFuelUpsVol = olderFuelUpsVol.plus(it.volume)
            }
        }

        val avgOlderConsumption = if (existsOlderFullFillUp) getConsumptionFromVolumeDistance(olderFuelUpsVol, olderFuelUpsDistance) else null

        // insert new fillUp with fuel consumption
        refueling.consumption = avgOlderConsumption
        insertNew(refueling, vehicleId)

        if (existsOlderFullFillUp) {
            updateConsumption(olderRefuelings, avgOlderConsumption, vehicleId)
        }

        // find first full newer fillUp in case not inserting the newest fillUp
        var newerFuelUpsVol = BigDecimal.ZERO
        var newerFuelUpsDistance = 0
        var existsNewerFullFillUp = false

        val newerRefuelings = mutableListOf<Refueling>()

        run loop@{
            allRefuelings.filter { it.date > refueling.date }.sorted().reversed().forEach {
                newerRefuelings.add(it)
                newerFuelUpsDistance += it.distanceFromLast
                newerFuelUpsVol = newerFuelUpsVol.plus(it.volume)

                if (it.isFull) {
                    existsNewerFullFillUp = true
                    return@loop
                }
            }
        }

        val avgNewerConsumption = if (!existsNewerFullFillUp) null else getConsumptionFromVolumeDistance(newerFuelUpsVol, newerFuelUpsDistance)

        if (existsNewerFullFillUp) {
            updateConsumption(newerRefuelings, avgNewerConsumption, vehicleId)
        }
    }

    fun insertNotFullRefueling(vehicleId: String, refueling: Refueling, allRefuelings: List<Refueling>) {

        // find first full newer fillUp in case not inserting the newest fillUp
        var newerFuelUpsVol = BigDecimal.ZERO
        var newerFuelUpsDistance = 0
        var existsNewerFullFillUp = false

        val newerRefuelings = mutableListOf<Refueling>()

        run loop@{
            allRefuelings.filter { it.date > refueling.date }.sorted().reversed().forEach {
                newerRefuelings.add(it)
                newerFuelUpsDistance += it.distanceFromLast
                newerFuelUpsVol = newerFuelUpsVol.plus(it.volume)

                if (it.isFull) {
                    existsNewerFullFillUp = true
                    return@loop
                }
            }
        }

        // if there is no newer full fill up, we do not compute consumption, only insert it
        if (!existsNewerFullFillUp) {
            insertNew(refueling, vehicleId)
            return
        }

        // secondly count older fillUps until full (respecting this)
        // find fillUps until first full older fillUp (without that one full)
        val olderRefuelings = mutableListOf<Refueling>()
        var olderFuelUpsVol = BigDecimal.ZERO
        var olderFuelUpsDistance = 0
        var existsOlderFullFillUp = false

        run loop@{
            allRefuelings.filter { it.date < refueling.date }.sorted().forEach {
                if (it.isFull) {
                    existsOlderFullFillUp = true
                    return@loop
                }
                olderRefuelings.add(it)
                olderFuelUpsDistance += it.distanceFromLast
                olderFuelUpsVol = olderFuelUpsVol.plus(it.volume)

            }
        }

        // if there is no older full fill up, we do not compute consumption, only insert refueling
        if (!existsOlderFullFillUp) {
            insertNew(refueling, vehicleId)
            return
        }

        // update all neighbouring fillUps with new consumption
        // compute consumption for all
        val fuelVol = newerFuelUpsVol.add(olderFuelUpsVol).add(refueling.volume)
        val distance = newerFuelUpsDistance + olderFuelUpsDistance + refueling.distanceFromLast
        val avgConsumption = getConsumptionFromVolumeDistance(fuelVol, distance)

        refueling.consumption = avgConsumption
        insertNew(refueling, vehicleId)
        updateConsumption(newerRefuelings + olderRefuelings, avgConsumption, vehicleId)
    }

    fun deleteFillUpInTransaction(vehicleId: String, refueling: Refueling, allRefuelings: List<Refueling>) {

        val fuelUps = mutableListOf<Refueling>()
        var fuelUpsVol = BigDecimal.ZERO
        var fuelUpsDistance = 0

        var existsOlderFullFillUp = false

        // find first full older fillUp
        run loop@{
            allRefuelings.filter { it.date < refueling.date }.sorted().forEach {
                if (it.isFull) {
                    existsOlderFullFillUp = true
                    return@loop
                }
                fuelUps.add(it)
                fuelUpsDistance += it.distanceFromLast
                fuelUpsVol = fuelUpsVol.plus(it.volume)
            }
        }


        // find first newer full FillUp
        var existsNewerFullFillUp = false

        run loop@{
            allRefuelings.filter { it.date > refueling.date }.sorted().reversed().forEach {
                fuelUps.add(it)
                fuelUpsDistance += it.distanceFromLast
                fuelUpsVol = fuelUpsVol.plus(it.volume)
                if (it.isFull) {
                    existsNewerFullFillUp = true
                    return@loop
                }
            }
        }

        val avgConsumption = if (existsNewerFullFillUp && existsOlderFullFillUp) {
            // when newer or older does not exist, we set null to all fillUps
            getConsumptionFromVolumeDistance(fuelUpsVol, fuelUpsDistance)
        } else null


        delete(refueling, vehicleId)
        updateConsumption(fuelUps, avgConsumption, vehicleId)
    }

    private fun insertNew(refueling: Refueling, carId: String) {
        FirebaseDatabase.getInstance()
                .getReference("fuel/$carId")
                .push()
                .setValue(refueling.toMap())
    }

    private fun updateConsumption(refuelings: List<Refueling>, consumption: BigDecimal?, carId: String) {
        val updates = mutableMapOf<String, Any?>()
        refuelings.forEach {
            updates.put("${it.id}/consumption", consumption?.toString())
        }

        FirebaseDatabase.getInstance()
                .getReference("fuel/$carId")
                .updateChildren(updates)

    }

    private fun delete(refueling: Refueling, carId: String) {
        FirebaseDatabase.getInstance()
                .getReference("fuel/$carId/${refueling.id}")
                .removeValue()
    }

    private fun getConsumptionFromVolumeDistance(volume: BigDecimal, distance: Int, volumeUnit: VolumeUnit = VolumeUnit.LITRE): BigDecimal {
        return if (volumeUnit === VolumeUnit.LITRE) {
            volume.multiply(BigDecimal(100)).divide(BigDecimal(distance), 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.valueOf(distance.toLong()).divide(volume, 2, RoundingMode.HALF_UP)
        }
    }


}