package com.erebuni782.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.erebuni782.app.R
import com.erebuni782.app.contacts.ContactInfo
import com.erebuni782.app.contacts.DEVELOPER_CONTACT
import com.erebuni782.app.contacts.MUSEUM_CONTACT

/**
 * Две планки в самом низу приложения: «Связь с музеем» и «Связь
 * с разработчиком». Тап по планке сворачивает/разворачивает её.
 * Пустые поля контакта не показываются (Contacts.kt).
 */
@Composable
fun ContactBars() {
    Column(Modifier.fillMaxWidth()) {
        ContactBar(
            tag = "contact_bar_museum",
            titleRes = R.string.contact_museum_title,
            info = MUSEUM_CONTACT
        )
        ContactBar(
            tag = "contact_bar_dev",
            titleRes = R.string.contact_dev_title,
            info = DEVELOPER_CONTACT
        )
    }
}

@Composable
private fun ContactBar(tag: String, titleRes: Int, info: ContactInfo) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    val context = LocalContext.current

    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().testTag(tag)
    ) {
        Column(Modifier.fillMaxWidth().animateContentSize()) {
            // заголовок-планка: тап сворачивает/разворачивает
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (expanded) 180f else 0f),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (expanded) {
                Column(
                    Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                ) {
                    if (info.phone.isNotBlank()) {
                        ContactRow(labelRes = R.string.contact_phone, value = info.phone) {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${info.phone}")))
                        }
                    }
                    if (info.email.isNotBlank()) {
                        ContactRow(labelRes = R.string.contact_email, value = info.email) {
                            context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${info.email}")))
                        }
                    }
                    if (info.web.isNotBlank()) {
                        ContactRow(labelRes = R.string.contact_website, value = info.web) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(info.web)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(labelRes: Int, value: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
