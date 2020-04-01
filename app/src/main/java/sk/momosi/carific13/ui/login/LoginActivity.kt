package sk.momosi.carific13.ui.login

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import sk.momosi.carific13.R
import sk.momosi.carific13.model.User
import sk.momosi.carific13.ui.main.MainActivity
import sk.momosi.carific13.util.ConnectivityUtils
import java.util.*

@SuppressLint("RestrictedApi")
class LoginActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val googleAuth: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(
                this,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .build()
        )
    }

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sign_in_button.setOnClickListener { signIn() }
    }

    private fun signIn() {
        if (ConnectivityUtils.isNetworkAvailable(this)) {
            startActivityForResult(googleAuth.signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN)
        } else {
            Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Snackbar.make(login_parent, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct?.id)
        showProgressDialog()

        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")

                        FirebaseDatabase.getInstance().getReference("user/${firebaseAuth.currentUser?.uid}")
                                .addListenerForSingleValueEvent(object : ValueEventListener {

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            FirebaseDatabase.getInstance().getReference("user")
                                                    .child(firebaseAuth.currentUser?.uid ?: "error")
                                                    .setValue(
                                                            User(
                                                                    id = firebaseAuth.currentUser?.uid
                                                                            ?: "",
                                                                    currencyCode = Currency.getInstance(Locale.getDefault()).currencyCode,
                                                                    currencySymbol = Currency.getInstance(Locale.getDefault()).symbol
                                                            ).toMap())
                                                    .addOnSuccessListener {
                                                        startMainActivity()
                                                    }
                                        } else {
                                            startMainActivity()
                                        }
                                    }

                                    override fun onCancelled(p0: DatabaseError) = Unit
                                }
                                )
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException())
                        Snackbar.make(login_parent, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    }
                    hideProgressDialog()
                }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showProgressDialog() {
        if (progressDialog == null)
            progressDialog = ProgressDialog(this)

        progressDialog?.let {
            it.show()
            it.setMessage("Loading")
            it.isIndeterminate = true
        }
    }

    private fun hideProgressDialog() {
        if (progressDialog?.isShowing == true)
            progressDialog?.dismiss()
    }

    override fun onStop() {
        hideProgressDialog()
        super.onStop()
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        const val REQUEST_CODE_GOOGLE_SIGN_IN = 9001
    }
}
