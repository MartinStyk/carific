package xyz.sangcomz.stickytimelineview

import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import sk.momosi.carific13.R
import sk.momosi.carific13.view.recycler.decorations.CarificRecyclerItemDecoration

class RoadItemDecoration(
        context: Context
) : CarificRecyclerItemDecoration(context) {

    private var roadBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.road)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDrawOver(c, parent, state)
        drawRoad(c, parent)
    }

    /**
     * Draw a line in the timeline.
     */
    private fun drawRoad(c: Canvas, parent: RecyclerView) {
        val paint = Paint()
        paint.shader = BitmapShader(roadBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

        val rectangle = Rect(roadStart,
                0,
                roadEnd,
                parent.height)

        c.drawRect(rectangle, paint)

    }

}


