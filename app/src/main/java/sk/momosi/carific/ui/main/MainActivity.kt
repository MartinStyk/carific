package sk.momosi.carific.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import sk.momosi.carific.R
import sk.momosi.carific.databinding.ActivityMainBinding
import sk.momosi.carific.ui.car.edit.AddEditCarActivity
import sk.momosi.carific.ui.car.list.CarChoiceListActivity
import sk.momosi.carific.ui.timeline.list.TimelineFragment
import sk.momosi.carific.ui.profile.ProfileFragment
import sk.momosi.carific.ui.statistics.StatisticsFragment
import sk.momosi.carific.util.extensions.disableShiftMode
import sk.momosi.carific.util.extensions.requestLogin


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var viewModel: MainActivityViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        binding.viewmodel = viewModel
        binding.setLifecycleOwner(this)

        setupBottomNavigation()

        setupRequestLoginAction()

        setupCarActions()

        setupCarChangeActions()

        viewModel.load()
    }

    override fun onResume() {
        super.onResume()
        viewModel.ensureUserLoggedIn()
    }

    private fun setupCarActions() {
        viewModel.noCarExists.observe(this, Observer {
            startActivity(Intent(this, AddEditCarActivity::class.java))
        })
    }

    private fun setupCarChangeActions() {
        viewModel.carChange.observe(this, Observer {
            val timelineFragment = TimelineFragment.newInstance(
                    viewModel.car.get()!!,
                    viewModel.user.get()!!
            )

            supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, timelineFragment)
                    .commit()
            main_bottom_navigation.selectedItemId = R.id.navigation_timeline
        })
    }

    private fun setupRequestLoginAction() {
        viewModel.requestLogin.observe(this, Observer {
            FirebaseAuth.getInstance().requestLogin(this)
        })
    }


    private fun setupBottomNavigation() {
        main_bottom_navigation.disableShiftMode()
        main_bottom_navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment = when (item.itemId) {
            R.id.navigation_timeline -> TimelineFragment.newInstance(
                    viewModel.car.get()!!,
                    viewModel.user.get()!!
            )
            R.id.navigation_statistics -> StatisticsFragment.newInstance(
                    viewModel.car.get()!!,
                    viewModel.user.get()!!)
            R.id.navigation_achievements -> AchievementsFragment.newInstance()
            R.id.navigation_account -> ProfileFragment.newInstance()
            else -> throw IllegalStateException()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit()

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_change_car -> {
                startActivity(Intent(this, CarChoiceListActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
