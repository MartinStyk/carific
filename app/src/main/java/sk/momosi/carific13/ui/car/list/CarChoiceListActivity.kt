package sk.momosi.carific13.ui.car.list

import android.arch.lifecycle.Observer

class CarChoiceListActivity : CarListActivity() {

    override fun setupListItemClicks() {
        super.viewModel.carClickEvent.observe(this, Observer { it ->
            it?.let {
                viewModel.setDefaultCar(it, this)
                finish()
            }
        })
    }

}
