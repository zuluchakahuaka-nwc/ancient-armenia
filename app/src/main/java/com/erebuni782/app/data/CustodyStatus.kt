package com.erebuni782.app.data

/**
 * Цепочка хранения (спека §1):
 * in-situ → agreed-for-transfer → in-transit → handed-to-museum → researched.
 * Переходы вперёд; статус хранится как имя enum в Room.
 */
enum class CustodyStatus(val stage: Int) {
    IN_SITU(0),
    AGREED_FOR_TRANSFER(1),
    IN_TRANSIT(2),
    HANDED_TO_MUSEUM(3),
    RESEARCHED(4);

    fun next(): CustodyStatus? = entries.getOrNull(ordinal + 1)

    companion object {
        fun fromRaw(raw: String?): CustodyStatus =
            entries.firstOrNull { it.name == raw } ?: IN_SITU

        fun canTransition(from: CustodyStatus, to: CustodyStatus): Boolean = to.stage > from.stage
    }
}

/** Категории находок (спека §1). */
enum class ArtifactCategory {
    STONE_BLOCK, MASONRY, POTTERY, OTHER;

    companion object {
        fun fromRaw(raw: String?): ArtifactCategory =
            entries.firstOrNull { it.name == raw } ?: OTHER
    }
}
