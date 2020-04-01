package sk.momosi.carific13.ui.car.achievements

import androidx.lifecycle.ViewModel
import sk.momosi.carific13.R

/**
 * @author Martin Styk
 * @date 04.08.2018.
 */
open class BaseAchievementViewModel : ViewModel() {

    internal fun incrementRefuelings() {
        incrementAchievement(R.string.achievement_10_refuelings, 1)
    }

    internal fun incrementExpenses() {
        incrementAchievement(R.string.achievement_10_expenses,1)
    }

    internal fun incrementLitres(amount: Int) {
        incrementAchievement(R.string.achievement_10_litres, amount)
    }

    //TODO
    private fun incrementAchievement(achievementId: Int, incrementBy: Int) {
//        val context = getApplication<Carific>().applicationContext
//
//        GoogleSignIn.getLastSignedInAccount(context)?.let {
//            Games.getAchievementsClient(context, it)
//                    .increment(context.getString(achievementId), incrementBy)
//        }
    }

    companion object {
        val TAG = BaseAchievementViewModel::class.java.simpleName
    }
}