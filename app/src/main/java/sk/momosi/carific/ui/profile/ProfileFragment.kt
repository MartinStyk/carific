package sk.momosi.carific.ui.profile

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.FragmentProfileBinding
import sk.momosi.carific.ui.car.AddEditCarActivity
import sk.momosi.carific.util.extensions.requestLogin

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
    lateinit var viewModel: ProfileFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProfileFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.viewmodel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profile_sign_out.setOnClickListener { signOut() }

        profile_car_edit.setOnClickListener { updateCar() }

        profile_scroller.isNestedScrollingEnabled = false

        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.navigation_profile)
        activity?.findViewById<AppBarLayout>(R.id.app_bar)?.setExpanded(false, true)

        updateUI(firebaseAuth.currentUser)

        viewModel.start(arguments?.getString(ARGUMENT_CAR_ID)
                ?: throw IllegalArgumentException("Car id argument missing"))

    }

    private fun createCar() {
        startActivity(Intent(activity, AddEditCarActivity::class.java))
    }

    private fun updateCar() {
        val intent = Intent(activity, AddEditCarActivity::class.java)
        intent.putExtra(AddEditCarActivity.ARG_CAR_ID,
                arguments?.getString(ARGUMENT_CAR_ID)
                        ?: throw IllegalArgumentException("Car id argument missing"))
        startActivity(intent)
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

        private const val ARGUMENT_CAR_ID = "car_id"
        private val TAG = ProfileFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(param1: String) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARGUMENT_CAR_ID, param1)
                    }
                }
    }

}
