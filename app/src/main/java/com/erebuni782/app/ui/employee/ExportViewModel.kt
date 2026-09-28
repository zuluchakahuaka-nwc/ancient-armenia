package com.erebuni782.app.ui.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.erebuni782.app.AppGraph
import com.erebuni782.app.sync.SyncLoopback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Экспорт/импорт/синк (P5). Статус — машиночитаемые ASCII-токены (e2e-инвариант). */
class ExportViewModel : ViewModel() {

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status.asStateFlow()

    fun export(passphrase: String) {
        viewModelScope.launch {
            runCatching {
                val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val file = java.io.File(AppGraph.export.exportsDir(), "erebuni_$stamp.e782")
                val r = AppGraph.export.exportTo(file, passphrase)
                "EXPORT ok artifacts=${r.artifacts} photos=${r.photos}"
            }.onSuccess { _status.value = it }
                .onFailure { _status.value = "EXPORT fail ${it.message}" }
        }
    }

    fun importLatest(passphrase: String) {
        viewModelScope.launch {
            runCatching {
                val file = AppGraph.export.latestExport()
                    ?: error("нет файлов экспорта")
                val r = AppGraph.export.importFrom(file, passphrase)
                "IMPORT ok artifacts=${r.artifacts} sessions=${r.aerialSessions} verified=${r.verified}"
            }.onSuccess { _status.value = it }
                .onFailure { _status.value = "IMPORT fail ${it.message}" }
        }
    }

    fun loopbackSync() {
        viewModelScope.launch {
            runCatching { SyncLoopback.run() }
                .onSuccess { _status.value = it }
                .onFailure { _status.value = "SYNC fail ${it.message}" }
        }
    }

    fun reportPinChange(ok: Boolean) {
        _status.value = if (ok) "PIN ok changed=1" else "PIN fail wrong"
    }

    companion object {
        fun factory() = viewModelFactory { initializer { ExportViewModel() } }
    }
}
