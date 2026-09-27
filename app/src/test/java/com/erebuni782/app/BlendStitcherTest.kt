package com.erebuni782.app

import com.erebuni782.app.aerial.stitch.BlendStitcher
import com.erebuni782.app.aerial.stitch.TileImage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BlendStitcherTest {

    private fun grayTile(w: Int, h: Int, columnValue: (Int) -> Int): TileImage {
        val px = IntArray(w * h)
        for (y in 0 until h) {
            for (x in 0 until w) {
                val v = columnValue(x)
                px[y * w + x] = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
            }
        }
        return TileImage(w, h, px)
    }

    @Test
    fun `two tiles with identical 16px overlap stitch wider`() {
        // tile1: x 0..63 со значениями v(x); tile2 повторяет v(x-48) → перекрытие 16
        val v: (Int) -> Int = { x -> (x * 3) % 250 + 2 }
        val t1 = grayTile(64, 32) { x -> v(x) }
        val t2 = grayTile(64, 32) { x -> v(x + 48) }   // v(48..111): совпадает с t1 на x=48..63
        val stitcher = BlendStitcher()
        val overlap = stitcher.findOverlap(64, 32, t1.argb, t2)
        assertTrue("перекрытие найдено (=$overlap)", overlap in 12..20)
        val result = stitcher.stitch(listOf(t1, t2))
        assertEquals(64 + 64 - overlap, result.width)
        assertEquals(32, result.height)
    }

    @Test
    fun `single tile returned as is`() {
        val t = grayTile(32, 16) { 100 }
        val r = BlendStitcher().stitch(listOf(t))
        assertEquals(32, r.width)
        assertEquals(16, r.height)
    }

    @Test
    fun `unrelated tiles fallback to zero overlap`() {
        val t1 = grayTile(64, 32) { 10 }
        val t2 = grayTile(64, 32) { 200 }
        val stitcher = BlendStitcher()
        val overlap = stitcher.findOverlap(64, 32, t1.argb, t2)
        assertEquals(0, overlap)
        val r = stitcher.stitch(listOf(t1, t2))
        assertEquals(128, r.width)
    }
}
