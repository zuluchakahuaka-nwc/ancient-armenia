package com.erebuni782.app.ui.topbar

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erebuni782.app.R
import com.erebuni782.app.ui.library.LibraryViewModel
import java.io.File

/**
 * Верхняя панель-действий: [i] (справка) — [Urartu.fm] (плеер) — [!] (логи в Downloads).
 * «!» сохраняет ПОЛНЫЙ лог приложения в /sdcard/Download/Erebuni782.txt
 * и копирует в буфер обмена.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TopActionBar(
    libraryViewModel: LibraryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val playerState by libraryViewModel.playerState.collectAsState()
    val stationEnabled by libraryViewModel.stationEnabled.collectAsState()
    var showOnboarding by rememberSaveable { mutableStateOf(false) }
    var logResult by rememberSaveable { mutableStateOf("") }

    Surface(tonalElevation = 2.dp, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── зелёная «i» — онбординг (повторно) ──
            IconButton(
                onClick = { showOnboarding = true },
                modifier = Modifier.testTag("btn_help")
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF2E7D32), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("i", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // ── Urartu.fm транспорт: ⏮ ⏯ ⏭ (долгое нажатие ⏯ = стоп) ──
            if (playerState.hasContent) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // prev
                    IconButton(
                        onClick = { libraryViewModel.stationPrevious() },
                        modifier = Modifier.size(36.dp).testTag("btn_fm_prev")
                    ) {
                        Icon(
                            Icons.Filled.SkipPrevious,
                            contentDescription = "prev",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    // play/pause (короткое = пауза/плей, долгое = стоп)
                    IconButton(
                        onClick = { libraryViewModel.stationPlayPause() },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("btn_fm_player")
                            .combinedClickable(
                                onClick = { libraryViewModel.stationPlayPause() },
                                onLongClick = { libraryViewModel.stationStop() }
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    if (playerState.isPlaying) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (playerState.isPlaying) {
                                Icon(Icons.Filled.Pause, contentDescription = "pause", tint = Color.White)
                            } else {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "play",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    // next
                    IconButton(
                        onClick = { libraryViewModel.stationNext() },
                        modifier = Modifier.size(36.dp).testTag("btn_fm_next")
                    ) {
                        Icon(
                            Icons.Filled.SkipNext,
                            contentDescription = "next",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                // станция не играет — одна кнопка ▶
                IconButton(
                    onClick = { libraryViewModel.setStation(true) },
                    modifier = Modifier.testTag("btn_fm_player")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Urartu.fm",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // ── красная «!» — логи в Downloads/Erebuni782.txt + буфер ──
            IconButton(
                onClick = {
                    val logs = readAppLogs(context)
                    val saved = saveLogToDownloads(context, logs)
                    val copied = copyToClipboard(context, logs)
                    logResult = buildString {
                        append(if (saved) "SAVED: Download/Erebuni782.txt" else "SAVE FAIL")
                        append(" | ")
                        append(if (copied) "CLIPBOARD ok" else "CLIPBOARD fail")
                        append(" | ${logs.length} chars")
                    }
                },
                modifier = Modifier.testTag("btn_copy_logs")
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFC62828), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("!", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // ── онбординг по кнопке «i» (полноэкранный оверлей) ──
    if (showOnboarding) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            com.erebuni782.app.ui.OnboardingScreen(
                onFinished = { showOnboarding = false }
            )
        }
    }
}

/** Полный лог приложения (logcat по PID, ошибки + предупреждения + info). */
private fun readAppLogs(context: Context): String {
    return try {
        val pid = android.os.Process.myPid()
        val process = ProcessBuilder()
            .command("logcat", "-d", "--pid=$pid", "-t", "500")
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        val header = "=== Erebuni 782 — Log Dump ${java.util.Date()} ===\n" +
            "Device: ${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})\n" +
            "PID: $pid\n${"=".repeat(60)}\n\n"
        header + output.trim()
    } catch (e: Exception) {
        "Error reading logs: ${e.message}"
    }
}

/**
 * Сохраняет лог в /sdcard/Download/Erebuni782.txt.
 * Android 10+: через MediaStore (без разрешения); старые: прямой файл.
 */
private fun saveLogToDownloads(context: Context, content: String): Boolean {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+: MediaStore Downloads (не требует WRITE_EXTERNAL_STORAGE)
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, "Erebuni782.txt")
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: return false
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(content.toByteArray(Charsets.UTF_8))
            }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
            true
        } else {
            // Android < 10: прямой файл
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            dir.mkdirs()
            val file = File(dir, "Erebuni782.txt")
            file.writeText(content, Charsets.UTF_8)
            true
        }
    } catch (e: Exception) {
        // запасной путь — приватная папка приложения
        try {
            val file = File(context.getExternalFilesDir("logs"), "Erebuni782.txt")
            file.parentFile?.mkdirs()
            file.writeText(content, Charsets.UTF_8)
            true
        } catch (e2: Exception) {
            false
        }
    }
}

private fun copyToClipboard(context: Context, text: String): Boolean {
    return try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Erebuni782 logs", text))
        true
    } catch (e: Exception) {
        false
    }
}
