package sk.momosi.carific13.view.recycler

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import sk.momosi.carific13.R
import sk.momosi.carific13.view.recycler.decorations.RecyclerSectionItemDecoration
import sk.momosi.carific13.view.recycler.model.RecyclerViewAttr

class CarificRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    private var recyclerViewAttr: RecyclerViewAttr? = null

    init {
        attrs?.let {
            val a = context.theme?.obtainStyledAttributes(
                    attrs,
                    R.styleable.CarificRecyclerView,
                    0, 0)

            a?.let {
                recyclerViewAttr =
                        RecyclerViewAttr(it.getColor(R.styleable.CarificRecyclerView_sectionBackgroundColor,
                                ContextCompat.getColor(context, R.color.colorDefaultBackground)),
                                it.getColor(R.styleable.CarificRecyclerView_sectionTitleTextColor,
                                        ContextCompat.getColor(context, R.color.colorPrimary)),
                                it.getColor(R.styleable.CarificRecyclerView_sectionSubTitleTextColor,
                                        ContextCompat.getColor(context, R.color.colorPrimaryDark)),
                                it.getColor(R.styleable.CarificRecyclerView_timeLineCircleColor,
                                        ContextCompat.getColor(context, R.color.colorPrimary)),
                                it.getColor(R.styleable.CarificRecyclerView_timeLineCircleStrokeColor,
                                        ContextCompat.getColor(context, R.color.grey_100)),
                                it.getDimension(R.styleable.CarificRecyclerView_sectionTitleTextSize,
                                        context.resources.getDimension(R.dimen.title_text_size)),
                                it.getDimension(R.styleable.CarificRecyclerView_sectionSubTitleTextSize,
                                        context.resources.getDimension(R.dimen.sub_title_text_size)),
                                it.getBoolean(R.styleable.CarificRecyclerView_isSticky, true))
            }

        }
    }

    /**
     * Add RecyclerSectionItemDecoration for Sticky TimeLineView
     *
     * @param callback SectionCallback
     */
    fun addItemDecoration(callback: RecyclerSectionItemDecoration.SectionCallback) {
        recyclerViewAttr?.let {
            this.addItemDecoration(RecyclerSectionItemDecoration(context,
                    callback,
                    it))
        }
    }
}