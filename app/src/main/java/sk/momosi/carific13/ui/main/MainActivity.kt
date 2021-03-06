package sk.momosi.carific13.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import sk.momosi.carific13.R
import sk.momosi.carific13.databinding.ActivityMainBinding
import sk.momosi.carific13.dependencyinjection.utils.ViewModelFactory
import sk.momosi.carific13.ui.car.achievements.AchievementsFragment
import sk.momosi.carific13.ui.car.edit.AddEditCarActivity
import sk.momosi.carific13.ui.car.list.CarChoiceListActivity
import sk.momosi.carific13.ui.profile.ProfileFragment
import sk.momosi.carific13.ui.statistics.chart.ChartStatisticsFragment
import sk.momosi.carific13.ui.timeline.list.TimelineFragment
import sk.momosi.carific13.util.extensions.disableShiftMode
import sk.momosi.carific13.util.extensions.provideViewModel
import sk.momosi.carific13.util.extensions.requestLogin
import javax.inject.Inject


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: MainActivityViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = provideViewModel(viewModelFactory)

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
            R.id.navigation_statistics -> ChartStatisticsFragment.newInstance(
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
