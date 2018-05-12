package sk.momosi.carific.ui.statistics.detail

import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import sk.momosi.carific.R

import kotlinx.android.synthetic.main.activity_detailed_statistics.*
import sk.momosi.carific.ui.timeline.list.CarificBaseFragment.Companion.ARGUMENT_CAR
import sk.momosi.carific.ui.timeline.list.CarificBaseFragment.Companion.ARGUMENT_USER

class DetailedStatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_statistics)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragment = supportFragmentManager.findFragmentByTag(DetailedStatisticsFragment.TAG)
                ?: intent?.let {
                    DetailedStatisticsFragment.newInstance(
                            it.getParcelableExtra(ARGUMENT_CAR),
                            it.getParcelableExtra(ARGUMENT_USER)
                    )
                }

        supportFragmentManager.beginTransaction()
                .replace(R.id.statistics_detailed_content, fragment)
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}
