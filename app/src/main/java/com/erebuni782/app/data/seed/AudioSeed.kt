package com.erebuni782.app.data.seed

import com.erebuni782.app.data.db.AudioTrackEntity

/** Аудиогиды P2 — заглушки без файлов (добавятся с контентом P6). */
val GUIDE_SEED: List<AudioTrackEntity> = listOf(
    AudioTrackEntity(
        kind = "GUIDE", uri = null,
        titleEn = "Guide: gates of the citadel", titleRu = "Гид: ворота цитадели", titleHy = "Ուղեցույց. ամրոցի դարպասներ",
        durationSec = 180
    ),
    AudioTrackEntity(
        kind = "GUIDE", uri = null,
        titleEn = "Guide: hall of Sarduri", titleRu = "Гид: зал Сардури", titleHy = "Ուղեցույց. Սարդուրի դահլիճ",
        durationSec = 240
    )
)
