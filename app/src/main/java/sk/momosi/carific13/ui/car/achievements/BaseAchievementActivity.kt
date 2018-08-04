package sk.momosi.carific13.ui.car.achievements

import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

open class BaseAchievementActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        signInSilently()
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
}
