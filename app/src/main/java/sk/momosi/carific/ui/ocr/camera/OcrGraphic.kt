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
package sk.momosi.carific.ui.ocr.camera

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.android.gms.vision.text.Element

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class OcrGraphic internal constructor(overlay: GraphicOverlay<OcrGraphic>, val word: Element?) : GraphicOverlay.Graphic(overlay) {

    var id: Int = 0

    init {
        sRectPaint.color = TEXT_COLOR
        sRectPaint.style = Paint.Style.STROKE
        sRectPaint.strokeWidth = 4.0f

        sTextPaint = Paint()
        sTextPaint.color = TEXT_COLOR
        sTextPaint.textSize = 54.0f

        // Redraw the overlay, as this graphic has been added.
        postInvalidate()
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    override fun contains(x: Float, y: Float): Boolean {
        val text = word ?: return false
        val rect = RectF(text.boundingBox)
        rect.left = translateX(rect.left)
        rect.top = translateY(rect.top)
        rect.right = translateX(rect.right)
        rect.bottom = translateY(rect.bottom)
        return rect.left < x && rect.right > x && rect.top < y && rect.bottom > y
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    override fun draw(canvas: Canvas) {
        val text = word ?: return

        // Draws the bounding box around the TextBlock.
        val rect = RectF(text.boundingBox)
        rect.left = translateX(rect.left)
        rect.top = translateY(rect.top)
        rect.right = translateX(rect.right)
        rect.bottom = translateY(rect.bottom)
        canvas.drawRect(rect, sRectPaint)

        val left = translateX(text.boundingBox.left.toFloat())
        val bottom = translateY(text.boundingBox.bottom.toFloat())
        canvas.drawText(text.value, left, bottom, sTextPaint)
    }

    companion object {

        private val TEXT_COLOR = Color.WHITE

        private var sRectPaint = Paint()
        private var sTextPaint = Paint()
    }
}
