package com.erebuni782.app.aerial.gen3d

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Base64

/** Экспорт меша в glTF 2.0 (JSON + data-URI base64 buffer). Чистая JVM-логика. */
object GltfWriter {

    private const val COMPONENT_FLOAT = 5126
    private const val COMPONENT_UINT32 = 5125

    fun writeGltf(parts: List<MeshPart>): ByteArray {
        val out = ByteArrayOutputStream()
        val viewsMeta = mutableListOf<Triple<Int, Int, Int>>() // offset, length, kind: 0=pos,1=idx

        parts.forEach { part ->
            val posBytes = part.positions.size * 4
            val idxBytes = part.indices.size * 4
            val posBuf = ByteBuffer.allocate(posBytes).order(ByteOrder.LITTLE_ENDIAN)
            part.positions.forEach { posBuf.putFloat(it) }
            out.write(posBuf.array())
            viewsMeta += Triple(0, posBytes, 0)
            val idxBuf = ByteBuffer.allocate(idxBytes).order(ByteOrder.LITTLE_ENDIAN)
            part.indices.forEach { idxBuf.putInt(it) }
            out.write(idxBuf.array())
            viewsMeta += Triple(0, idxBytes, 1)
        }
        val binary = out.toByteArray()
        val b64 = Base64.getEncoder().encodeToString(binary)

        // пересчёт офсетов последовательно
        var offset = 0
        val viewsJson = StringBuilder()
        viewsMeta.forEachIndexed { i, (_, len, _) ->
            if (i > 0) viewsJson.append(",")
            viewsJson.append("{\"buffer\":0,\"byteOffset\":$offset,\"byteLength\":$len}")
            offset += len
        }

        val accessors = StringBuilder()
        val meshes = StringBuilder()
        val nodes = StringBuilder()
        var accessorId = 0
        parts.forEachIndexed { p, part ->
            val posAccessor = accessorId++
            val idxAccessor = accessorId++
            if (accessors.isNotEmpty()) accessors.append(",")
            accessors.append(
                "{\"bufferView\":${p * 2},\"componentType\":$COMPONENT_FLOAT," +
                    "\"count\":${part.positions.size / 3},\"type\":\"VEC3\"}"
            )
            accessors.append(
                ",{\"bufferView\":${p * 2 + 1},\"componentType\":$COMPONENT_UINT32," +
                    "\"count\":${part.indices.size},\"type\":\"SCALAR\"}"
            )
            if (meshes.isNotEmpty()) {
                meshes.append(",")
                nodes.append(",")
            }
            meshes.append(
                "{\"primitives\":[{\"attributes\":{\"POSITION\":$posAccessor},\"indices\":$idxAccessor,\"mode\":4}]}"
            )
            nodes.append("{\"mesh\":$p,\"name\":\"${part.name}\"}")
        }

        val nodeIds = (0 until parts.size).joinToString(",")

        val gltf = """
        {"asset": {"version": "2.0", "generator": "Erebuni782 EraParametricGenerator"},
         "scene": 0,
         "scenes": [{"nodes": [$nodeIds]}],
         "nodes": [$nodes],
         "meshes": [$meshes],
         "accessors": [$accessors],
         "bufferViews": [$viewsJson],
         "buffers": [{"byteLength": ${binary.size}, "uri": "data:application/octet-stream;base64,$b64"}]}
        """.trimIndent()

        return gltf.toByteArray()
    }
}
