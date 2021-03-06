package sk.momosi.carific13.view.recycler.decorations

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import sk.momosi.carific13.util.extensions.DP

abstract class CarificRecyclerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    val defaultOffset = 8.DP(context).toInt()

    val headerOffset = defaultOffset * 8

    val roadWidth = 16.DP(context).toInt()

    val roadCenter = defaultOffset * 5
    val roadStart = roadCenter - roadWidth / 2
    val roadEnd = roadCenter + roadWidth / 2

}