package sk.momosi.carific13.ui.statistics.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detailed_statistics.*
import sk.momosi.carific13.R
import sk.momosi.carific13.ui.timeline.list.CarificBaseFragment.Companion.ARGUMENT_CAR
import sk.momosi.carific13.ui.timeline.list.CarificBaseFragment.Companion.ARGUMENT_USER

class DetailedStatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_statistics)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragment = supportFragmentManager.findFragmentByTag(DetailedStatisticsFragment.TAG)
                ?: intent!!.let {
                    DetailedStatisticsFragment.newInstance(
                            car = it.getParcelableExtra(ARGUMENT_CAR)!!,
                            user = it.getParcelableExtra(ARGUMENT_USER)!!
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
