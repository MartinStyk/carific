package sk.momosi.carific13.ui.profile

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.mynameismidori.currencypicker.CurrencyPicker
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import sk.momosi.carific13.R
import sk.momosi.carific13.databinding.FragmentProfileBinding
import sk.momosi.carific13.ui.car.list.CarListActivity
import sk.momosi.carific13.util.extensions.requestLogin


class ProfileFragment : Fragment() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val googleAuth: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(activity as Activity,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .build())
    }

    lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profile_sign_out.setOnClickListener { signOut() }

        profile_manage_cars.setOnClickListener { manageCars() }

        profile_currency.setOnClickListener { selectCurrency() }

        profile_scroller.isNestedScrollingEnabled = false

        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.navigation_profile)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(false, true)

        updateUI(firebaseAuth.currentUser)
    }

    private fun selectCurrency() {
        val picker = CurrencyPicker.newInstance(getString(R.string.profile_currency_select))
        picker.setListener { name, code, symbol, flagDrawableResID ->
            // TODO use viewmodel
            FirebaseDatabase.getInstance()
                    .getReference("user/${firebaseAuth.currentUser?.uid}")
                    .updateChildren(
                            mapOf(
                                    Pair("currencySymbol", symbol),
                                    Pair("currencyCode", code)
                            )
                    )

            Snackbar.make(requireActivity().findViewById<View>(android.R.id.content),
                    getString(R.string.profile_currency_selected, name),
                    Snackbar.LENGTH_SHORT).show()
            picker.dismiss()
        }
        picker.show(requireActivity().supportFragmentManager, CurrencyPicker::class.java.simpleName)
    }

    private fun manageCars() {
        startActivity(Intent(context, CarListActivity::class.java))
    }

    private fun signOut() {
        firebaseAuth.signOut()
        googleAuth.signOut().addOnCompleteListener(activity as Activity) { firebaseAuth.requestLogin(requireActivity()) }
    }

    private fun updateUI(user: FirebaseUser?) {
        login_user_name.text = user?.displayName
        login_user_email.text = user?.email
        Picasso.get()
                .load(user?.photoUrl)
                .placeholder(R.drawable.ic_account_circle_100dp)
                .into(login_user_image)
    }

    companion object {
        private val TAG = ProfileFragment::class.java.simpleName

        @JvmStatic
        fun newInstance() =
                ProfileFragment()
    }

}
