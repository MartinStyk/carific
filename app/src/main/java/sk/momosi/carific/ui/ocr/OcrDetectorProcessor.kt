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
package sk.momosi.carific.ui.ocr

import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import sk.momosi.carific.ui.ocr.camera.GraphicOverlay

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
class OcrDetectorProcessor internal constructor(private val mGraphicOverlay: GraphicOverlay<OcrGraphic>) : Detector.Processor<TextBlock> {

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
        mGraphicOverlay.clear()
        val items = detections.getDetectedItems()
        for (i in 0 until items.size()) {
            val item = items.valueAt(i)
            val graphic = OcrGraphic(mGraphicOverlay, item)
            mGraphicOverlay.add(graphic)
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    override fun release() {
        mGraphicOverlay.clear()
    }
}
