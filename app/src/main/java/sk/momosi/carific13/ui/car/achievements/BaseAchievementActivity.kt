package sk.momosi.carific13.ui.car.achievements

import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games
import sk.momosi.carific13.R

open class BaseAchievementActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        signInSilently()
    }

    internal fun incrementRefuelings() {
        incrementAchievement(R.string.achievement_10_refuelings, 1)
    }

    internal fun incrementExpenses() {
        incrementAchievement(R.string.achievement_10_expenses,1)
    }

    internal fun incrementLitres(amount: Int) {
        incrementAchievement(R.string.achievement_10_litres, amount)
    }

    private fun signInSilently() {
        val signInClient = GoogleSignIn.getClient(
                this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
        signInClient.silentSignIn().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // The signed in account is stored in the task's result.
                val signedInAccount = task.result
            } else {
                // Player will need to sign-in explicitly using via UI
            }
        }
    }

    private fun incrementAchievement(achievementId: Int, incrementBy: Int) {
        GoogleSignIn.getLastSignedInAccount(this)?.let {
            Games.getAchievementsClient(this, it)
                    .increment(getString(achievementId), incrementBy)
        }
    }
}
