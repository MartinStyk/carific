package sk.momosi.carific.ui.statistics.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import sk.momosi.carific.R

import kotlinx.android.synthetic.main.activity_detailed_statistics.*

class DetailedStatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_statistics)
        setSupportActionBar(toolbar)

        val fragment = supportFragmentManager.findFragmentByTag(DetailedStatisticsFragment.TAG)
                ?: intent?.let {
                    DetailedStatisticsFragment.newInstance(
                            it.getParcelableExtra(DetailedStatisticsFragment.ARGUMENT_CAR),
                            it.getParcelableExtra(DetailedStatisticsFragment.ARGUMENT_USER)
                    )
                }

        supportFragmentManager.beginTransaction()
                .replace(R.id.statistics_detailed_content, fragment)
                .commit()
    }

}
