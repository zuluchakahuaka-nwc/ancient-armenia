package com.erebuni782.app.aerial.geo

/** Экспорт разметки сессии в GeoJSON (WGS84; при отсутствии гео — null-координаты). */
object GeoJsonExporter {

    data class MarkerPoint(
        val number: Int,
        val x: Double,
        val y: Double,
        val source: String,
        val confidence: Double?
    )

    fun export(sessionTitle: String, lat: Double?, lon: Double?, markers: List<MarkerPoint>): String {
        val features = markers.joinToString(",\n") { m ->
            val props = buildString {
                append("\"number\": ${m.number}")
                append(", \"source\": \"${m.source}\"")
                m.confidence?.let { append(", \"confidence\": ${"%.3f".format(it)}") }
                append(", \"image_x\": ${"%.5f".format(m.x)}, \"image_y\": ${"%.5f".format(m.y)}")
            }
            """
            {"type": "Feature",
             "geometry": {"type": "Point", "coordinates": [${lon ?: "null"}, ${lat ?: "null"}]},
             "properties": {$props}}
            """.trimIndent()
        }
        return """
        {"type": "FeatureCollection",
         "metadata": {"title": "$sessionTitle"},
         "features": [
        $features
         ]}
        """.trimIndent()
    }
}
