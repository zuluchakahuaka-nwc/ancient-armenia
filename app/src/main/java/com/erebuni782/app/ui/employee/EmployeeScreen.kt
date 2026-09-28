package com.erebuni782.app.ui.employee

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erebuni782.app.AppGraph
import com.erebuni782.app.R
import com.erebuni782.app.data.ArtifactRepository
import com.erebuni782.app.data.ArtifactUi
import com.erebuni782.app.data.CustodyStatus
import com.erebuni782.app.ui.theme.OrnamentalDivider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun statusLabel(status: CustodyStatus): String = when (status) {
    CustodyStatus.IN_SITU -> stringResource(R.string.status_in_situ)
    CustodyStatus.AGREED_FOR_TRANSFER -> stringResource(R.string.status_agreed_for_transfer)
    CustodyStatus.IN_TRANSIT -> stringResource(R.string.status_in_transit)
    CustodyStatus.HANDED_TO_MUSEUM -> stringResource(R.string.status_handed_to_museum)
    CustodyStatus.RESEARCHED -> stringResource(R.string.status_researched)
}

class EmployeeViewModel : ViewModel() {
    val artifacts: StateFlow<List<ArtifactUi>> = AppGraph.artifacts.observeActive()
        .map { list -> list.map { ArtifactRepository.toUi(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun delete(id: String) {
        viewModelScope.launch { AppGraph.artifacts.softDelete(id) }
    }
}

/** Главное меню режима сотрудника: Учёт / Аэро / Быстрая съёмка / Экспорт. */
@Composable
fun EmployeeScreen(onOpenArtifact: (String) -> Unit, onNewArtifact: () -> Unit, onOpenAerial: () -> Unit) {
    val vm: EmployeeViewModel = viewModel()
    val artifacts by vm.artifacts.collectAsState()
    val keepExif by AppGraph.settings.keepExif.collectAsState(initial = true)

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("employee_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── заголовок ──
        item {
            Text(stringResource(R.string.employee_menu_title), style = MaterialTheme.typography.headlineMedium)
            OrnamentalDivider(Modifier.padding(top = 6.dp))
        }

        // ── БЫСТРАЯ СЪЁМКА ──
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().testTag("quick_capture_card")
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.quick_capture_title), style = MaterialTheme.typography.titleLarge)
                    Text(stringResource(R.string.quick_capture_desc), style = MaterialTheme.typography.bodyMedium)
                    Button(
                        onClick = onOpenAerial,
                        modifier = Modifier.fillMaxWidth().testTag("btn_take_photo")
                    ) {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text(stringResource(R.string.quick_capture_button))
                    }
                }
            }
        }

        // ── УЧЁТ АРТЕФАКТОВ + ДОБАВИТЬ + СПИСОК (единный блок) ──
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.employee_list_title), style = MaterialTheme.typography.titleLarge)
                    Text(stringResource(R.string.employee_registry_count, artifacts.size), style = MaterialTheme.typography.bodySmall)
                }
                Button(
                    onClick = onNewArtifact,
                    modifier = Modifier.testTag("add_artifact")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Text(stringResource(R.string.add_artifact))
                }
            }
        }

        // список записей (сразу после заголовка реестра — до аэро/экспорта)
        items(artifacts, key = { it.id }) { artifact ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onOpenArtifact(artifact.id) }
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(artifact.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        artifact.address.ifBlank { stringResource(R.string.no_address) },
                        style = MaterialTheme.typography.bodySmall
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(statusLabel(artifact.custodyStatus)) }
                    )
                }
            }
        }

        // ── АЭРО-РАЗВЕДКА ──
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.aerial_title), style = MaterialTheme.typography.titleLarge)
                    Text(stringResource(R.string.aerial_subtitle), style = MaterialTheme.typography.bodyMedium)
                    Button(
                        onClick = onOpenAerial,
                        modifier = Modifier.fillMaxWidth().testTag("open_aerial")
                    ) {
                        Text(stringResource(R.string.aerial_open))
                    }
                }
            }
        }

        // ── ЭКСПОРТ/СИНК ──
        item { ExchangeCard() }

        // ── EXIF ──
        item {
            Card {
                Row(
                    Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.exif_toggle_label), style = MaterialTheme.typography.bodyMedium)
                        Text(
                            stringResource(R.string.exif_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = keepExif,
                        onCheckedChange = { enabled -> vm.setKeepExif(enabled) },
                        modifier = Modifier.testTag("exif_toggle")
                    )
                }
            }
        }
    }
}

private fun ViewModel.setKeepExif(enabled: Boolean) {
    viewModelScope.launch { AppGraph.settings.setKeepExif(enabled) }
}

/** P5: подписанный экспорт/импорт (D2) + loopback-синк. Статус = e2e-якорь. */
@Composable
private fun ExchangeCard() {
    val vm: ExportViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = ExportViewModel.factory())
    val status by vm.status.collectAsState()
    var passphrase by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf("erebuni") }
    var showPinChange by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }

    Card {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.exchange_title), style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(R.string.exchange_hint),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            androidx.compose.material3.OutlinedTextField(
                value = passphrase,
                onValueChange = { passphrase = it },
                label = { Text(stringResource(R.string.exchange_pass)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("export_pass")
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                androidx.compose.material3.Button(
                    onClick = { vm.export(passphrase) },
                    modifier = Modifier.testTag("btn_export")
                ) { Text(stringResource(R.string.btn_export)) }
                androidx.compose.material3.OutlinedButton(
                    onClick = { vm.importLatest(passphrase) },
                    modifier = Modifier.testTag("btn_import")
                ) { Text(stringResource(R.string.btn_import)) }
                androidx.compose.material3.OutlinedButton(
                    onClick = { vm.loopbackSync() },
                    modifier = Modifier.testTag("btn_sync")
                ) { Text(stringResource(R.string.btn_sync)) }
            }
            androidx.compose.material3.OutlinedButton(
                onClick = { showPinChange = true },
                modifier = Modifier.fillMaxWidth().testTag("btn_change_pin")
            ) { Text(stringResource(R.string.btn_change_pin)) }
            Text(
                text = status,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.testTag("export_status")
            )
        }
    }

    if (showPinChange) {
        ChangePinDialog(
            onDismiss = { showPinChange = false },
            onResult = { ok ->
                showPinChange = false
                vm.reportPinChange(ok)
            }
        )
    }
}

/** D3: смена общего PIN (текущий + новый с подтверждением). */
@Composable
private fun ChangePinDialog(onDismiss: () -> Unit, onResult: (Boolean) -> Unit) {
    var current by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    var newPin by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    var confirm by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    var error by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    val msgWrong = stringResource(R.string.pin_wrong)
    val msgMismatch = stringResource(R.string.pin_mismatch)
    val msgFormat = stringResource(R.string.pin_bad_format)

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.btn_change_pin)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                androidx.compose.material3.OutlinedTextField(
                    current, { if (it.length <= 8 && it.all { c -> c.isDigit() }) current = it },
                    label = { Text(stringResource(R.string.pin_current)) },
                    singleLine = true,
                    modifier = Modifier.testTag("pin_current")
                )
                androidx.compose.material3.OutlinedTextField(
                    newPin, { if (it.length <= 8 && it.all { c -> c.isDigit() }) newPin = it },
                    label = { Text(stringResource(R.string.pin_new)) },
                    singleLine = true,
                    modifier = Modifier.testTag("pin_new")
                )
                androidx.compose.material3.OutlinedTextField(
                    confirm, { if (it.length <= 8 && it.all { c -> c.isDigit() }) confirm = it },
                    label = { Text(stringResource(R.string.pin_confirm_label)) },
                    singleLine = true,
                    modifier = Modifier.testTag("pin_new_confirm")
                )
                if (error.isNotEmpty()) {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = {
                    when {
                        !com.erebuni782.app.data.PinHasher.isValidPinFormat(newPin) -> error = msgFormat
                        newPin != confirm -> error = msgMismatch
                        else -> onResult(AppGraph.pin.change(current, newPin))
                    }
                },
                modifier = Modifier.testTag("pin_change_apply")
            ) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { Text(stringResource(R.string.back)) }
        }
    )
}
