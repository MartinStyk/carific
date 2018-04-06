package sk.momosi.carific.ui.car.list

import android.arch.lifecycle.Observer

class CarChoiceListActivity : CarListActivity() {

    override fun setupListItemClicks() {
        super.viewModel.carClickEvent.observe(this, Observer {
            it?.let {
                viewModel.setDefaultCar(it)
                finish()
            }
        })
    }


}
