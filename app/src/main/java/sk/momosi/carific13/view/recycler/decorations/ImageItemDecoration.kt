package sk.momosi.carific13.view.recycler.decorations

import android.content.Context
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sk.momosi.carific13.R
import sk.momosi.carific13.model.ListItem

class ImageItemDecoration(context: Context) : CarificRecyclerItemDecoration(context) {

    private lateinit var decorationRefueling: View
    private lateinit var decorationExpense: View

    private var imageWidth: Int = 0
    private var imageHeight: Int = 0

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        // go through all the recycler views child
        for (i in 0 until parent.childCount) {
            val recyclerViewItem = parent.getChildAt(i)

            val decoration = getImageDecoration(parent, parent.getChildLayoutPosition(recyclerViewItem))
            c.save()

            val xMove = roadCenter - imageWidth.toFloat() / 2
            val yMove = recyclerViewItem.top - (recyclerViewItem.top - recyclerViewItem.bottom) / 2 - (imageWidth / 2).toFloat()

            c.translate(xMove, yMove)

            decoration.draw(c)

            c.restore()
        }
    }

    private fun getImageDecoration(recyclerView: RecyclerView, currentPosition: Int): View {
        return when (recyclerView.adapter!!.getItemViewType(currentPosition)) {
            ListItem.REFUELING -> {
                if (!::decorationRefueling.isInitialized) {
                    decorationRefueling = LayoutInflater.from(recyclerView.context)
                            .inflate(R.layout.recycler_image_fuel_decoration, recyclerView, false)
                    fixLayoutSize(decorationRefueling, recyclerView)
                }
                decorationRefueling
            }
            ListItem.EXPENSE -> {
                if (!::decorationExpense.isInitialized) {
                    decorationExpense = LayoutInflater.from(recyclerView.context)
                            .inflate(R.layout.recycler_image_expense_decoration, recyclerView, false)
                    fixLayoutSize(decorationExpense, recyclerView)
                }
                decorationExpense
            }
            else -> throw IllegalStateException("Unsupported view type")

        }
    }

    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        // Check if the view has a layout parameter and if it does not create one for it
        if (view.layoutParams == null) {
            view.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        // Create a width and height spec using the parent as an example:
        // For width we make sure that the item matches exactly what it measures from the parent.
        //  IE if layout says to match_parent it will be exactly parent.getWidth()
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        // For the height we are going to create a spec that says it doesn't really care what is calculated,
        //  even if its larger than the screen
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Get the child specs using the parent spec and the padding the parent has
        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.paddingTop + parent.paddingBottom, view.layoutParams.height)

        // Finally we measure the sizes with the actual view which does margin and padding changes to the sizes calculated
        view.measure(childWidth, childHeight)

        imageHeight = view.measuredHeight
        imageWidth = view.measuredWidth

        // And now we setup the layout for the view to ensure it has the correct sizes.
        view.layout(0, 0, imageWidth, imageHeight)
    }
}