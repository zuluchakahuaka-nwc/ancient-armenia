package com.erebuni782.app.contacts

/**
 * Контакты для двух планок внизу приложения (ContactBars).
 * Владелец заполняет значения — пустые строки скрывают соответствующую строку.
 */
data class ContactInfo(
    val phone: String = "",
    val email: String = "",
    val web: String = ""
)

/** Планка «Связь с музеем». */
val MUSEUM_CONTACT = ContactInfo(
    phone = "",
    email = "",
    web = ""
)

/** Планка «Связь с разработчиком». */
val DEVELOPER_CONTACT = ContactInfo(
    phone = "",
    email = "",
    web = ""
)
