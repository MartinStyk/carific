package sk.momosi.carific13.ui.car.achievements

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import sk.momosi.carific13.Carific
import sk.momosi.carific13.R

/**
 * @author Martin Styk
 * @date 04.08.2018.
 */
open class BaseAchievementViewModel(application: Application) : AndroidViewModel(application) {

    internal fun incrementRefuelings() {
        incrementAchievement(R.string.achievement_10_refuelings, 1)
    }

    internal fun incrementExpenses() {
        incrementAchievement(R.string.achievement_10_expenses,1)
    }

    internal fun incrementLitres(amount: Int) {
        incrementAchievement(R.string.achievement_10_litres, amount)
    }

    private fun incrementAchievement(achievementId: Int, incrementBy: Int) {
        val context = getApplication<Carific>().applicationContext

        GoogleSignIn.getLastSignedInAccount(context)?.let {
            Games.getAchievementsClient(context, it)
                    .increment(context.getString(achievementId), incrementBy)
        }
    }

    companion object {
        val TAG = BaseAchievementViewModel::class.java.simpleName
    }
}