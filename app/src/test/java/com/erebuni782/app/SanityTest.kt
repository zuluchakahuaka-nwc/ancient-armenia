package com.erebuni782.app

import org.junit.Assert.assertEquals
import org.junit.Test

/** P0 sanity: JVM-тесты работают. */
class SanityTest {

    @Test
    fun arithmeticSanity() {
        assertEquals(4, 2 + 2)
    }
}
