package sk.momosi.carific.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import sk.momosi.carific.R
import sk.momosi.carific.ui.expense.ExpenseListFragment
import sk.momosi.carific.util.extensions.disableShiftMode
import sk.momosi.carific.util.extensions.requestLogin


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        main_bottom_navigation.disableShiftMode()
        main_bottom_navigation.setOnNavigationItemSelectedListener(this)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth.requestLogin(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment = when (item.itemId) {
            R.id.navigation_fuel -> ProfileFragment()
            R.id.navigation_expenses -> ExpenseListFragment.newInstance("my_car_id")
            R.id.navigation_statistics -> ProfileFragment()
            R.id.navigation_achievements -> ProfileFragment()
            R.id.navigation_account -> ProfileFragment()
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
            R.id.action_settings ->
                return true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
