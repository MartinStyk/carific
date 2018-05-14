package sk.momosi.carific13.util.extensions

import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import sk.momosi.carific13.ui.login.LoginActivity

/**
 * @author Martin Styk
 * @date 29.03.2018.
 */
fun FirebaseAuth.requestLogin(context: Context) {
    if (currentUser == null) {
        val loginIntent = Intent(context, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(loginIntent)
    }
}