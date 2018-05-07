package sk.momosi.carific.ui.car.achievements

import android.app.AlertDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.fragment_achievements.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.FragmentAchievementsBinding

class AchievementsFragment : Fragment() {

    companion object {
        private val TAG = AchievementsFragment::class.java.simpleName
        private const val RC_SIGN_IN = 9001

        @JvmStatic
        fun newInstance() = AchievementsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentAchievementsBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_achievements,
                container,
                false
        )
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_play_games_services_sign_in.setOnClickListener({ _ ->
            startSignInIntent()
        })
    }

    private fun startSignInIntent() {
        val signInClient = GoogleSignIn.getClient(
                activity!!,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
        val intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // The signed in account is stored in the result.
                val signedInAccount = result.signInAccount!!
                Toast.makeText(context,signedInAccount.toJson(), Toast.LENGTH_SHORT).show()
            } else {
                var message = result.status.statusMessage
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error)
                }

                AlertDialog.Builder(activity!!)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show()
                Log.d(TAG, "Error status code: ${result.status.statusCode}")
            }
        }
    }

    private fun signInSilently() {
        val signInClient = GoogleSignIn.getClient(
                activity!!,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
        signInClient.silentSignIn().addOnCompleteListener(activity!!, { task ->
            if (task.isSuccessful) {
                // The signed in account is stored in the task's result.
                val account = task.result
                Log.d(TAG, account.displayName.toString())
            } else {
                // Player will need to sign-in explicitly using via UI
                Log.d(TAG, "signInSilently(): failure", task.exception)
            }
        })
    }

    private fun signOut() {
        val signInClient = GoogleSignIn.getClient(
                activity!!,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
        signInClient.signOut().addOnCompleteListener(
                activity!!, {
            // at this point, the user is signed out.
        })
    }

    override fun onResume() {
        super.onResume()
//        signInSilently()
    }
}
