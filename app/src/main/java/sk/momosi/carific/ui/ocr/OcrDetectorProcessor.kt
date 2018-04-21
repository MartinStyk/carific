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

import android.util.Log
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.Element
import com.google.android.gms.vision.text.Line
import com.google.android.gms.vision.text.TextBlock
import sk.momosi.carific.ui.ocr.camera.GraphicOverlay

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
class OcrDetectorProcessor internal constructor(private val mGraphicOverlay: GraphicOverlay<OcrGraphic>) : Detector.Processor<TextBlock> {

    private val numberRegex = Regex("^\\d*.\\d+|\\d+\\.\\d* ")

    /**
     * Called by the detector to deliver detection results.
     */
    override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
        mGraphicOverlay.clear()

        val wordsMap = getWords(detections)

        fixCommonErrors(wordsMap)

        val numberMap = getNumbers(wordsMap)

        Log.d(TAG, numberMap.values.toString())

        numberMap.map { entry ->
            val graphic = OcrGraphic(mGraphicOverlay, entry.key)
            mGraphicOverlay.add(graphic)
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    override fun release() {
        mGraphicOverlay.clear()
    }

    private fun getWords(detections: Detector.Detections<TextBlock>): MutableMap<Element, String> {
        val resultWords = HashMap<Element, String>()

        val items = detections.detectedItems

        for (i in 0 until items.size()) {
            val textBlock = items.valueAt(i)
            val sentence: List<Line> = textBlock.components as List<Line>

            sentence.forEach {
                val words = it.components as List<Element>
                words.forEach {
                    resultWords.put(it, it.value)
                }
            }
        }

        return resultWords
    }

    private fun getNumbers(words: Map<Element, String>): Map<Element, Double> {
        val resultNumbers = HashMap<Element, Double>()

        words.filter { entry ->
            numberRegex.matches(entry.value)
        }.forEach { entry ->
            try {
                resultNumbers[entry.key] = entry.value.toDouble()
            } catch (e: NumberFormatException) {
                Log.d(TAG, "Can not parse double: ${entry.value} ", e)
            }
        }

        return resultNumbers
    }

    private fun fixCommonErrors(detections: MutableMap<Element, String>) {
        detections.forEach { element, string ->
            detections[element] = string.replace("O", "0")
            detections[element] = string.replace(",", ".")
        }
    }

    companion object {
        private val TAG = OcrDetectorProcessor::class.java.simpleName
    }
}
