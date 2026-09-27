package com.erebuni782.app.aerial.stitch

/**
 * Сшивка кадров в одно ортофото. Интерфейс: сегодня — чистая Kotlin-сшивка
 * с автопоиском перекрытия (BlendStitcher), слот под OpenCV detail-pipeline
 * (подмена реализации без UI-изменений). Перед сшивкой обязателен
 * даунскейл-пайплайн (AGENTS.md §10).
 */
interface Stitcher {
    fun stitch(images: List<TileImage>): StitchResult
}

/** Чистые пиксельные данные кадра (JVM-тестируемо). */
data class TileImage(val width: Int, val height: Int, val argb: IntArray)

data class StitchResult(val width: Int, val height: Int, val argb: IntArray, val seams: List<Int>)

/**
 * Горизонтальная сшивка с поиском перекрытия по корреляции яркостных
 * профилей и линейным смешиванием шва. Полностью offline, без нативных либ.
 */
class BlendStitcher(private val maxOverlapRatio: Double = 0.75) : Stitcher {

    override fun stitch(images: List<TileImage>): StitchResult {
        require(images.isNotEmpty()) { "нет кадров" }
        if (images.size == 1) {
            return StitchResult(images[0].width, images[0].height, images[0].argb.copyOf(), listOf(0))
        }

        var accW = images[0].width
        val accH = images[0].height
        var acc = images[0].argb.copyOf()

        for (i in 1 until images.size) {
            val next = images[i]
            require(next.height == accH) { "высоты кадров должны совпадать" }
            val overlap = findOverlap(accW, accH, acc, next)
            acc = merge(accW, accH, acc, next, overlap)
            accW = accW + next.width - overlap
        }
        return StitchResult(accW, accH, acc, emptyList())
    }

    /** Ищем перекрытие: правый край акка ≈ левый край следующего кадра. */
    internal fun findOverlap(accW: Int, accH: Int, acc: IntArray, next: TileImage): Int {
        val maxOverlap = minOf(
            (next.width * maxOverlapRatio).toInt(),
            accW / 2
        ).coerceAtLeast(0)
        if (maxOverlap == 0) return 0

        val rightProfile = columnProfile(accW, accH, acc, accW - maxOverlap, accW)
        var best = 0
        var bestCost = Double.MAX_VALUE
        for (ov in maxOverlap downTo 4) {
            val leftProfile = columnProfile(next.width, next.height, next.argb, 0, ov)
            val cost = profileDistance(rightProfile, leftProfile, ov)
            if (cost < bestCost) {
                bestCost = cost
                best = ov
            }
        }
        return if (bestCost < COST_LIMIT) best else 0
    }

    private fun columnProfile(w: Int, h: Int, px: IntArray, x0: Int, x1: Int): DoubleArray {
        val width = x1 - x0
        val profile = DoubleArray(width)
        for (x in 0 until width) {
            var sum = 0.0
            for (y in 0 until h step 4) {
                val p = px[y * w + (x0 + x)]
                sum += 0.299 * ((p shr 16) and 0xFF) + 0.587 * ((p shr 8) and 0xFF) + 0.114 * (p and 0xFF)
            }
            profile[x] = sum / (h / 4)
        }
        return profile
    }

    private fun profileDistance(a: DoubleArray, b: DoubleArray, n: Int): Double {
        // сравниваем хвост a с головой b одинаковой длины
        var cost = 0.0
        for (i in 0 until n) {
            val av = a[a.size - n + i]
            val bv = b[i]
            cost += kotlin.math.abs(av - bv)
        }
        return cost / n
    }

    private fun merge(accW: Int, accH: Int, acc: IntArray, next: TileImage, overlap: Int): IntArray {
        val outW = accW + next.width - overlap
        val out = IntArray(outW * accH)
        // левая часть как есть
        System.arraycopy(acc, 0, out, 0, accW * accH)
        // зона перекрытия: линейный альфа-блендинг
        for (y in 0 until accH) {
            for (x in 0 until overlap) {
                val a = (x.toDouble() / overlap).coerceIn(0.0, 1.0)
                val p1 = acc[y * accW + (accW - overlap + x)]
                val p2 = next.argb[y * next.width + x]
                out[y * outW + (accW - overlap + x)] = blend(p1, p2, a)
            }
            // правая часть следующего кадра
            for (x in overlap until next.width) {
                out[y * outW + (accW - overlap + x)] = next.argb[y * next.width + x]
            }
        }
        return out
    }

    private fun blend(p1: Int, p2: Int, a: Double): Int {
        val r = (((p1 shr 16) and 0xFF) * (1 - a) + ((p2 shr 16) and 0xFF) * a).toInt()
        val g = (((p1 shr 8) and 0xFF) * (1 - a) + ((p2 shr 8) and 0xFF) * a).toInt()
        val b = ((p1 and 0xFF) * (1 - a) + (p2 and 0xFF) * a).toInt()
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }

    companion object {
        const val COST_LIMIT = 18.0
    }
}
