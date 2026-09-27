package com.erebuni782.app

import com.erebuni782.app.aerial.detect.DetectLevel
import com.erebuni782.app.aerial.detect.GridAnomalyDetector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GridAnomalyDetectorTest {

    private val detector = GridAnomalyDetector()

    /** 256×256: равномерный серый фон + тёмный «блок» в одном углу. */
    private fun synthetic(w: Int, h: Int, withBlock: Boolean): IntArray {
        val px = IntArray(w * h) { gray(150) }
        if (withBlock) {
            for (y in 40 until 100) {
                for (x in 40 until 100) {
                    px[y * w + x] = gray(40)
                }
            }
        }
        return px
    }

    private fun gray(v: Int) = (0xFF shl 24) or (v shl 16) or (v shl 8) or v

    @Test
    fun `uniform image has no anomalies even at THOROUGH`() {
        val d = detector.detect(256, 256, synthetic(256, 256, withBlock = false), DetectLevel.THOROUGH)
        assertEquals(0, d.size)
    }

    @Test
    fun `obvious block detected even at WEAK near its position`() {
        val d = detector.detect(256, 256, synthetic(256, 256, withBlock = true), DetectLevel.WEAK)
        assertTrue("хоть что-то найдено", d.isNotEmpty())
        // детекция внутри блока (нормированные 40..100 из 256 ≈ 0.15..0.40)
        val inside = d.any { it.x in 0.10..0.45 && it.y in 0.10..0.45 }
        assertTrue("детекция близко к блоку: $d", inside)
    }

    @Test
    fun `finer grid finds not fewer cells`() {
        val px = synthetic(256, 256, withBlock = true)
        val weak = detector.detect(256, 256, px, DetectLevel.WEAK)
        val thorough = detector.detect(256, 256, px, DetectLevel.THOROUGH)
        assertTrue(thorough.size >= weak.size)
    }
}
