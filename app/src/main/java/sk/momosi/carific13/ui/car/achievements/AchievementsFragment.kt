package sk.momosi.carific13.ui.car.achievements

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_achievements.*
import sk.momosi.carific13.R
import sk.momosi.carific13.databinding.FragmentAchievementsBinding

class AchievementsFragment : Fragment() {

    companion object {
        private val TAG = AchievementsFragment::class.java.simpleName
        private const val RC_SIGN_IN = 9001
        private const val RC_ACHIEVEMENT_UI = 9003

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
        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.navigation_achievements)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(false, true)

        signInSilently()
        btn_play_games_services_sign_in.setOnClickListener({ _ ->
            startSignInIntent()
        })

        btn_show_achievements.setOnClickListener {
            showAchievements()
        }

        showAchievements()
    }

    override fun onResume() {
        super.onResume()
        signInSilently()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // The signed in account is stored in the result.
                val signedInAccount = result.signInAccount!!
                Log.d(TAG, "GooglePlayServices SignedIn Account: $signedInAccount")
                showAchievements()
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
            signInSilently()
        }
    }

    private val googleSignInClient: GoogleSignInClient?
        get() {
            return GoogleSignIn.getClient(
                    activity!!,
                    GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
            )
        }

    private fun startSignInIntent() {
        val signInClient = googleSignInClient
        val intent = signInClient!!.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    private fun signInSilently() {
        googleSignInClient!!.silentSignIn().addOnCompleteListener(activity!!) { task ->
            if (task.isSuccessful) {
                // The signed in account is stored in the task's result.
                val account = task.result
                Log.d(TAG, account?.displayName.toString())
                btn_play_games_services_sign_in.visibility = View.GONE
                btn_show_achievements.visibility = View.VISIBLE
            } else {
                // Player will need to sign-in explicitly using via UI
                btn_play_games_services_sign_in.visibility = View.VISIBLE
                btn_show_achievements.visibility = View.GONE
                Log.d(TAG, "signInSilently(): failure", task.exception)
            }
        }
    }

    private fun showAchievements() {
        Games.getAchievementsClient(
                activity!!, GoogleSignIn.getLastSignedInAccount(activity!!) ?: return
        )
                .achievementsIntent
                .addOnSuccessListener { intent ->
                    startActivityForResult(intent, RC_ACHIEVEMENT_UI)
                }
    }
}
