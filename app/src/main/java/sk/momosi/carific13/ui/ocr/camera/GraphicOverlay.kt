/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.momosi.carific13.ui.ocr.camera

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

import java.util.HashSet

/**
 * A view which renders a series of custom graphics to be overlaid on top of an associated preview
 * (i.e., the camera preview).  The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.
 *
 *
 *
 * Supports scaling and mirroring of the graphics relative the camera's preview properties.  The
 * idea is that detection items are expressed in terms of a preview size, but need to be scaled up
 * to the full view size, and also mirrored in the case of the front-facing camera.
 *
 *
 *
 * Associated [Graphic] items should use the following methods to convert to view coordinates
 * for the graphics that are drawn:
 *
 *  1. [Graphic.scaleX] and [Graphic.scaleY] adjust the size of the
 * supplied value from the preview scale to the view scale.
 *  1. [Graphic.translateX] and [Graphic.translateY] adjust the coordinate
 * from the preview's coordinate system to the view coordinate system.
 *
 */
class GraphicOverlay<T : GraphicOverlay.Graphic>(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val mLock = Any()
    private var mPreviewWidth: Int = 0
    private var mWidthScaleFactor = 1.0f
    private var mPreviewHeight: Int = 0
    private var mHeightScaleFactor = 1.0f
    private var mFacing = CameraSource.CAMERA_FACING_BACK
    private val mGraphics = HashSet<T>()

    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay.  Subclass
     * this and implement the [Graphic.draw] method to define the
     * graphics element.  Add instances to the overlay using [GraphicOverlay.add].
     */
    abstract class Graphic(private val mOverlay: GraphicOverlay<*>) {

        /**
         * Draw the graphic on the supplied canvas.  Drawing should use the following methods to
         * convert to view coordinates for the graphics that are drawn:
         *
         *  1. [Graphic.scaleX] and [Graphic.scaleY] adjust the size of
         * the supplied value from the preview scale to the view scale.
         *  1. [Graphic.translateX] and [Graphic.translateY] adjust the
         * coordinate from the preview's coordinate system to the view coordinate system.
         *
         *
         * @param canvas drawing canvas
         */
        abstract fun draw(canvas: Canvas)

        /**
         * Returns true if the supplied coordinates are within this graphic.
         */
        abstract fun contains(x: Float, y: Float): Boolean

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view
         * scale.
         */
        fun scaleX(horizontal: Float): Float {
            return horizontal * mOverlay.mWidthScaleFactor
        }

        /**
         * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
         */
        fun scaleY(vertical: Float): Float {
            return vertical * mOverlay.mHeightScaleFactor
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        fun translateX(x: Float): Float {
            return if (mOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT) {
                mOverlay.width - scaleX(x)
            } else {
                scaleX(x)
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        fun translateY(y: Float): Float {
            return scaleY(y)
        }

        fun postInvalidate() {
            mOverlay.postInvalidate()
        }
    }

    /**
     * Removes all graphics from the overlay.
     */
    fun clear() {
        synchronized(mLock) {
            mGraphics.clear()
        }
        postInvalidate()
    }

    /**
     * Adds a graphic to the overlay.
     */
    fun add(graphic: T) {
        synchronized(mLock) {
            mGraphics.add(graphic)
        }
        postInvalidate()
    }

    /**
     * Removes a graphic from the overlay.
     */
    fun remove(graphic: T) {
        synchronized(mLock) {
            mGraphics.remove(graphic)
        }
        postInvalidate()
    }

    /**
     * Returns the first graphic, if any, that exists at the provided absolute screen coordinates.
     * These coordinates will be offset by the relative screen position of this view.
     * @return First graphic containing the point, or null if no text is detected.
     */
    fun getGraphicAtLocation(rawX: Float, rawY: Float): T? {
        synchronized(mLock) {
            // Get the position of this View so the raw location can be offset relative to the view.
            val location = IntArray(2)
            this.getLocationOnScreen(location)
            for (graphic in mGraphics) {
                if (graphic.contains(rawX - location[0], rawY - location[1])) {
                    return graphic
                }
            }
            return null
        }
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform
     * image coordinates later.
     */
    fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int) {
        synchronized(mLock) {
            mPreviewWidth = previewWidth
            mPreviewHeight = previewHeight
            mFacing = facing
        }
        postInvalidate()
    }

    /**
     * Draws the overlay with its associated graphic objects.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(mLock) {
            if (mPreviewWidth != 0 && mPreviewHeight != 0) {
                mWidthScaleFactor = canvas.width.toFloat() / mPreviewWidth.toFloat()
                mHeightScaleFactor = canvas.height.toFloat() / mPreviewHeight.toFloat()
            }

            for (graphic in mGraphics) {
                graphic.draw(canvas)
            }
        }
    }
}
