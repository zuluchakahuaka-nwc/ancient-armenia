package com.erebuni782.app.aerial.detect

/**
 * D5: детектор за интерфейсом — сегодня CV-эвристика, завтра
 * дообученная TFLite-модель подменяется без переделки UI.
 * Три уровня: WEAK (только явные аномалии) / MEDIUM / THOROUGH (всё).
 */
enum class DetectLevel { WEAK, MEDIUM, THOROUGH }

data class Detection(val x: Double, val y: Double, val score: Double)

interface ObjectDetector {
    fun detect(width: Int, height: Int, argb: IntArray, level: DetectLevel): List<Detection>
}

/**
 * Классическая CV-эвристика: сеточная аномалия яркости/текстуры.
 * Чистая (без Android) — тестируется на JVM. Работает на ARGB-пикселях.
 */
class GridAnomalyDetector : ObjectDetector {

    override fun detect(width: Int, height: Int, argb: IntArray, level: DetectLevel): List<Detection> {
        val cell = when (level) {
            DetectLevel.WEAK -> CELL_COARSE
            DetectLevel.MEDIUM -> CELL_MEDIUM
            DetectLevel.THOROUGH -> CELL_FINE
        }
        val cols = (width + cell - 1) / cell
        val rows = (height + cell - 1) / cell
        val means = DoubleArray(cols * rows)
        val stds = DoubleArray(cols * rows)

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                var sum = 0.0; var sumSq = 0.0; var n = 0
                val y0 = r * cell; val y1 = minOf(height, y0 + cell)
                val x0 = c * cell; val x1 = minOf(width, x0 + cell)
                for (y in y0 until y1) {
                    for (x in x0 until x1) {
                        val p = argb[y * width + x]
                        val lum = 0.299 * ((p shr 16) and 0xFF) + 0.587 * ((p shr 8) and 0xFF) + 0.114 * (p and 0xFF)
                        sum += lum; sumSq += lum * lum; n++
                    }
                }
                if (n == 0) continue
                val mean = sum / n
                means[r * cols + c] = mean
                stds[r * cols + c] = kotlin.math.sqrt(maxOf(0.0, sumSq / n - mean * mean))
            }
        }

        // устойчивая мера отклонения: медиана + MAD
        val medMean = median(means)
        val medStd = median(stds)
        val madMean = maxOf(medianAbsDev(means, medMean), 1.0)
        val madStd = maxOf(medianAbsDev(stds, medStd), 1.0)

        val threshold = when (level) {
            DetectLevel.WEAK -> 3.0
            DetectLevel.MEDIUM -> 1.8
            DetectLevel.THOROUGH -> 1.0
        }

        val out = mutableListOf<Detection>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val i = r * cols + c
                if (means[i] == 0.0 && stds[i] == 0.0) continue
                val zMean = kotlin.math.abs(means[i] - medMean) / madMean
                val zStd = kotlin.math.abs(stds[i] - medStd) / madStd
                val score = maxOf(zMean, zStd)
                if (score >= threshold) {
                    out += Detection(
                        x = ((c + 0.5) * cell) / width,
                        y = ((r + 0.5) * cell) / height,
                        score = score
                    )
                }
            }
        }
        return out.sortedByDescending { it.score }
    }

    private fun median(values: DoubleArray): Double {
        if (values.isEmpty()) return 0.0
        val sorted = values.clone().also { it.sort() }
        return sorted[sorted.size / 2]
    }

    private fun medianAbsDev(values: DoubleArray, med: Double): Double {
        val devs = DoubleArray(values.size) { kotlin.math.abs(values[it] - med) }
        return median(devs)
    }

    companion object {
        const val CELL_COARSE = 48
        const val CELL_MEDIUM = 32
        const val CELL_FINE = 16
    }
}
