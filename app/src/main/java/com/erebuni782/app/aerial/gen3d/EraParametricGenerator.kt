package com.erebuni782.app.aerial.gen3d

/**
 * D6: параметрический генератор 3D-реконструкции по эпохам.
 * Вход: контур фундамента (нормированные точки 0..1) + эпоха/год/место.
 * Выход: меш (позиции + индексы) → glTF. SfM-фотограмметрия — отдельным
 * этапом позже; SceneView-вьюер подключается к готовому glTF-файлу.
 */
enum class Era { URARTU, ACHAEMENID, HELLENISTIC }

data class MeshPart(
    val name: String,
    val positions: FloatArray,   // тройки XYZ
    val indices: IntArray
)

data class ReconstructionInput(
    val contour: List<Pair<Double, Double>>,  // нормированный контур 0..1
    val era: Era,
    val year: Int,
    val place: String
)

object EraParametricGenerator {

    /** Строит структуру по эпохе внутри bounding-бокса контура. */
    fun generate(input: ReconstructionInput): List<MeshPart> {
        val (minX, minY, maxX, maxY) = bounds(input.contour)
        val w = (maxX - minX).coerceAtLeast(0.05)
        val h = (maxY - minY).coerceAtLeast(0.05)
        return when (input.era) {
            Era.URARTU -> urartuCitadel(w, h)
            Era.ACHAEMENID -> achaemenidHall(w, h)
            Era.HELLENISTIC -> hellenisticTemple(w, h)
        }
    }

    private fun bounds(contour: List<Pair<Double, Double>>): DoubleArray {
        var minX = 1.0; var minY = 1.0; var maxX = 0.0; var maxY = 0.0
        contour.forEach { (x, y) ->
            minX = minOf(minX, x); minY = minOf(minY, y)
            maxX = maxOf(maxX, x); maxY = maxOf(maxY, y)
        }
        return doubleArrayOf(minX, minY, maxX, maxY)
    }

    /** Урарту: массивные стены по периметру + 4 угловые башни + цитадель. */
    private fun urartuCitadel(w: Double, h: Double): List<MeshPart> {
        val parts = mutableListOf<MeshPart>()
        parts += box("wall_north", -w / 2, -h / 2, w, WALL_THICK, WALL_H)
        parts += box("wall_south", -w / 2, h / 2 - WALL_THICK, w, WALL_THICK, WALL_H)
        parts += box("wall_west", -w / 2, -h / 2, WALL_THICK, h, WALL_H)
        parts += box("wall_east", w / 2 - WALL_THICK, -h / 2, WALL_THICK, h, WALL_H)
        val tw = TOWER_RATIO * minOf(w, h)
        parts += box("tower_sw", -w / 2, -h / 2, tw, tw, WALL_H * 1.6)
        parts += box("tower_se", w / 2 - tw, -h / 2, tw, tw, WALL_H * 1.6)
        parts += box("tower_nw", -w / 2, h / 2 - tw, tw, tw, WALL_H * 1.6)
        parts += box("tower_ne", w / 2 - tw, h / 2 - tw, tw, tw, WALL_H * 1.6)
        parts += box("citadel", -w / 6, -h / 6, w / 3, h / 3, WALL_H * 2.0)
        return parts
    }

    /** Ахемениды: стилобат + колоннада (6×4). */
    private fun achaemenidHall(w: Double, h: Double): List<MeshPart> {
        val parts = mutableListOf<MeshPart>()
        parts += box("stylobate", -w / 2, -h / 2, w, h, PLATFORM_H)
        val cols = 6; val rows = 4
        val cw = w * 0.7 / (cols - 1)
        val ch = h * 0.7 / (rows - 1)
        for (i in 0 until cols) {
            for (j in 0 until rows) {
                val cx = -w * 0.35 + i * cw
                val cy = -h * 0.35 + j * ch
                parts += box("col_$i$j", cx - COL_R, cy - COL_R, COL_R * 2, COL_R * 2, COLUMN_H)
            }
        }
        parts += box("architrave", -w / 2, -h / 2, w, h, ROOF_H, baseZ = PLATFORM_H + COLUMN_H)
        return parts
    }

    /** Эллинизм: ступенчатый подиум + периптер + фронтон-призма. */
    private fun hellenisticTemple(w: Double, h: Double): List<MeshPart> {
        val parts = mutableListOf<MeshPart>()
        parts += box("step1", -w / 2, -h / 2, w, h, STEP_H)
        parts += box("step2", -w * 0.4, -h * 0.4, w * 0.8, h * 0.8, STEP_H, baseZ = STEP_H)
        val cols = 8; val rows = 3
        val cw = w * 0.64 / (cols - 1)
        val ch = h * 0.5 / (rows - 1)
        for (i in 0 until cols) {
            for (j in 0 until rows) {
                val cx = -w * 0.32 + i * cw
                val cy = -h * 0.25 + j * ch
                parts += box("col_$i$j", cx - COL_R, cy - COL_R, COL_R * 2, COL_R * 2, COLUMN_H, baseZ = STEP_H * 2)
            }
        }
        parts += prism("pediment", w * 0.8, h * 0.6, PEDIMENT_H, baseZ = STEP_H * 2 + COLUMN_H)
        return parts
    }

    /** Бокс (12 треугольников). baseZ — нижняя грань. */
    fun box(name: String, x: Double, y: Double, w: Double, h: Double, height: Double, baseZ: Double = 0.0): MeshPart {
        val x1 = x + w; val y1 = y + h; val z2 = baseZ + height
        val v = floatArrayOf(
            x.toFloat(), y.toFloat(), baseZ.toFloat(), x1.toFloat(), y.toFloat(), baseZ.toFloat(),
            x1.toFloat(), y1.toFloat(), baseZ.toFloat(), x.toFloat(), y1.toFloat(), baseZ.toFloat(),
            x.toFloat(), y.toFloat(), z2.toFloat(), x1.toFloat(), y.toFloat(), z2.toFloat(),
            x1.toFloat(), y1.toFloat(), z2.toFloat(), x.toFloat(), y1.toFloat(), z2.toFloat()
        )
        val i = intArrayOf(
            0, 3, 1, 0, 2, 3,   // низ
            4, 5, 6, 4, 6, 7,   // верх
            0, 1, 5, 0, 5, 4,   // юг
            2, 7, 6, 2, 3, 6,   // север
            0, 4, 7, 0, 7, 3,   // запад
            1, 2, 6, 1, 6, 5    // восток
        )
        return MeshPart(name, v, i)
    }

    /** Треугольная призма (фронтон): 8 вершин, 10 треугольников. */
    private fun prism(name: String, w: Double, h: Double, height: Double, baseZ: Double): MeshPart {
        val x0 = -w / 2; val x1 = w / 2
        val y0 = -h / 2; val y1 = h / 2
        val z0 = baseZ; val zRidge = baseZ + height
        val v = floatArrayOf(
            x0.toFloat(), y0.toFloat(), z0.toFloat(), x1.toFloat(), y0.toFloat(), z0.toFloat(),
            x1.toFloat(), y1.toFloat(), z0.toFloat(), x0.toFloat(), y1.toFloat(), z0.toFloat(),
            x0.toFloat(), ((y0 + y1) / 2).toFloat(), zRidge.toFloat(),
            x1.toFloat(), ((y0 + y1) / 2).toFloat(), zRidge.toFloat()
        )
        val i = intArrayOf(
            0, 2, 1, 0, 3, 2,   // низ
            0, 1, 4, 1, 5, 4,   // южный скат
            2, 3, 4, 2, 4, 5,   // северный скат
            0, 4, 3, 1, 2, 5    // торцы
        )
        return MeshPart(name, v, i)
    }

    private const val WALL_THICK = 0.06
    private const val WALL_H = 0.30
    private const val TOWER_RATIO = 0.16
    private const val PLATFORM_H = 0.06
    private const val COLUMN_H = 0.35
    private const val COL_R = 0.015
    private const val ROOF_H = 0.05
    private const val STEP_H = 0.05
    private const val PEDIMENT_H = 0.12
}
