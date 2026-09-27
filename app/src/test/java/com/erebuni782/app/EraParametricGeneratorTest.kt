package com.erebuni782.app

import com.erebuni782.app.aerial.gen3d.Era
import com.erebuni782.app.aerial.gen3d.EraParametricGenerator
import com.erebuni782.app.aerial.gen3d.GltfWriter
import com.erebuni782.app.aerial.gen3d.ReconstructionInput
import com.erebuni782.app.aerial.geo.GeoJsonExporter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64

class EraParametricGeneratorTest {

    private val input = ReconstructionInput(
        contour = listOf(0.1 to 0.1, 0.9 to 0.1, 0.9 to 0.9, 0.1 to 0.9),
        era = Era.URARTU, year = -782, place = "Arin-Berd"
    )

    @Test
    fun `urartu citadel has walls towers and citadel`() {
        val parts = EraParametricGenerator.generate(input)
        assertEquals(9, parts.size) // 4 стены + 4 башни + цитадель
        assertTrue(parts.all { it.positions.isNotEmpty() && it.indices.size % 3 == 0 })
        assertTrue(parts.any { it.name == "citadel" })
        assertTrue(parts.any { it.name == "tower_ne" })
    }

    @Test
    fun `achaemenid hall has 24 columns plus podium and architrave`() {
        val parts = EraParametricGenerator.generate(input.copy(era = Era.ACHAEMENID))
        assertEquals(1 + 24 + 1, parts.size)
    }

    @Test
    fun `hellenistic temple has pediment`() {
        val parts = EraParametricGenerator.generate(input.copy(era = Era.HELLENISTIC))
        assertTrue(parts.any { it.name == "pediment" })
    }

    @Test
    fun `gltf is valid JSON with base64 buffer and meshes`() {
        val parts = EraParametricGenerator.generate(input)
        val bytes = GltfWriter.writeGltf(parts)
        val json = String(bytes, Charsets.UTF_8)
        assertTrue(json.contains("\"asset\""))
        assertTrue(json.contains("\"version\": \"2.0\"") || json.contains("\"version\":\"2.0\""))
        assertTrue(json.contains("base64,"))
        // буфер декодируется и его длина = сумма позиций+индексов
        val b64 = Regex("base64,([A-Za-z0-9+/=]+)").find(json)!!.groupValues[1]
        val buffer = Base64.getDecoder().decode(b64)
        val expected = parts.sumOf { it.positions.size * 4 + it.indices.size * 4 }
        assertEquals(expected, buffer.size)
    }
}

class GeoJsonExporterTest {

    @Test
    fun `markers become feature collection`() {
        val out = GeoJsonExporter.export(
            sessionTitle = "Karmir Blur 1",
            lat = 40.1872, lon = 44.4642,
            markers = listOf(
                GeoJsonExporter.MarkerPoint(1, 0.25, 0.5, "MANUAL", null),
                GeoJsonExporter.MarkerPoint(2, 0.7, 0.3, "AUTO", 3.14)
            )
        )
        assertTrue(out.contains("\"FeatureCollection\""))
        assertTrue(out.contains("44.4642"))
        assertTrue(out.contains("\"number\": 1"))
        assertTrue(out.contains("\"source\": \"AUTO\""))
        assertTrue(out.contains("confidence"))
        assertTrue(out.contains("Karmir Blur 1"))
    }

    @Test
    fun `no geo gives null coordinates`() {
        val out = GeoJsonExporter.export("S", null, null, listOf(GeoJsonExporter.MarkerPoint(1, 0.1, 0.1, "MANUAL", null)))
        assertTrue(out.contains("[null, null]"))
    }
}
