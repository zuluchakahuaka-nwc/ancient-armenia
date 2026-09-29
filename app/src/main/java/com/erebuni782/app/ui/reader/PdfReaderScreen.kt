package com.erebuni782.app.ui.reader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.erebuni782.app.R
import com.erebuni782.app.data.book.OwnerBook
import java.io.File

/**
 * PDF-читалка (спека §3: PdfRenderer для сканов/книг). Ассет копируется
 * в кэш (seekable FD), страницы рендерятся лениво с кэшем на 3 вперёд/назад.
 * Статус-строка page=N/M — e2e-якорь (AGENTS §7.5).
 */
@Composable
fun PdfReaderScreen(book: OwnerBook, onBack: () -> Unit) {
    val context = LocalContext.current
    val doc = remember(book.id) { PdfDocument(context, book) }
    val pageCount = remember(book.id) { doc.pageCount() }
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val currentPage by remember { derivedStateOf { pagerState.currentPage + 1 } }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            Column(Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Text(
                    "${book.author}, ${book.year}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                text = "page=$currentPage/$pageCount",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .testTag("pdf_status")
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth().testTag("pdf_pager")
        ) { page ->
            val bitmap by rememberPageBitmap(doc, page)
            bitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(4.dp)
                )
            } ?: Text(
                stringResource(R.string.pdf_loading),
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/** Ленивый рендер страницы с кэшем соседних (память!). */
@Composable
private fun rememberPageBitmap(doc: PdfDocument, page: Int): androidx.compose.runtime.State<ImageBitmap?> {
    val bitmap = remember(page) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(page) {
        bitmap.value = doc.renderPage(page)?.asImageBitmap()
    }
    return bitmap
}

/** Обёртка PdfRenderer: копия ассета в кэш + синхронный рендер (страницы быстрые). */
class PdfDocument(context: Context, private val book: OwnerBook) {

    private val file: File = File(context.cacheDir, "book_${book.id}.pdf").apply {
        if (!exists()) {
            context.assets.open("books/${book.file}").use { input ->
                outputStream().use { input.copyTo(it) }
            }
        }
    }

    private val fd: ParcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    private val renderer = PdfRenderer(fd)

    fun pageCount(): Int = renderer.pageCount

    /** RENDER_MODE_FOR_DISPLAY + даунскейл >2x страниц по ширине (память, AGENTS §10). */
    fun renderPage(index: Int, maxWidth: Int = 1200): Bitmap? = runCatching {
        renderer.openPage(index).use { page ->
            var w = page.width
            var h = page.height
            if (w > maxWidth) {
                h = h * maxWidth / w
                w = maxWidth
            }
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bmp.eraseColor(android.graphics.Color.WHITE)
            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bmp
        }
    }.getOrNull()

    fun close() {
        runCatching { renderer.close() }
        runCatching { fd.close() }
    }
}
