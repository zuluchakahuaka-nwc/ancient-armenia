package com.erebuni782.app.ui.employee

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erebuni782.app.AppGraph
import com.erebuni782.app.R
import com.erebuni782.app.data.ArtifactCategory
import com.erebuni782.app.data.ArtifactRepository
import com.erebuni782.app.data.ArtifactUi
import com.erebuni782.app.data.CustodyStatus
import com.erebuni782.app.data.db.AuditEntryEntity
import com.erebuni782.app.photos.PhotoStore
import com.erebuni782.app.work.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ArtifactEditViewModel(private val artifactId: String) : ViewModel() {

    private val blank = ArtifactUi(
        id = "", category = ArtifactCategory.OTHER, title = "", description = "",
        latitude = null, longitude = null, address = "", discoveryEpochDay = null,
        custodian = "", custodyStatus = CustodyStatus.IN_SITU,
        nextInspectionEpochDay = null, photoPaths = emptyList(),
        createdAt = 0, updatedAt = 0
    )

    val isNew: Boolean = artifactId.isEmpty()

    private val _draft = MutableStateFlow(blank)
    val draft: StateFlow<ArtifactUi> = _draft.asStateFlow()

    val audit: StateFlow<List<AuditEntryEntity>> =
        if (isNew) MutableStateFlow(emptyList<AuditEntryEntity>())
        else AppGraph.artifacts.auditFor(artifactId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        if (!isNew) {
            viewModelScope.launch {
                AppGraph.artifacts.byId(artifactId)?.let { _draft.value = ArtifactRepository.toUi(it) }
            }
        }
    }

    fun update(transform: (ArtifactUi) -> ArtifactUi) {
        _draft.value = transform(_draft.value)
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            val d = _draft.value
            if (d.id.isEmpty()) {
                val id = AppGraph.artifacts.create(d)
                scheduleReminder(id, d.title, d.nextInspectionEpochDay)
            } else {
                AppGraph.artifacts.update(d, ArtifactRepository.ACTION_UPDATED, "fields")
                scheduleReminder(d.id, d.title, d.nextInspectionEpochDay)
            }
            onDone()
        }
    }

    fun advanceStatus(onDone: () -> Unit) {
        viewModelScope.launch {
            // гонка: кнопка рендерится до загрузки драфта — дозагружаем
            if (_draft.value.id.isEmpty()) {
                val loaded = AppGraph.artifacts.byId(artifactId) ?: return@launch
                _draft.value = ArtifactRepository.toUi(loaded)
            }
            val d = _draft.value
            val next = d.custodyStatus.next() ?: return@launch
            AppGraph.artifacts.setStatus(d.id, next)
            _draft.value = d.copy(custodyStatus = next)
            onDone()
        }
    }

    fun addPhoto(path: String) {
        val id = _draft.value.id
        if (id.isEmpty()) return
        viewModelScope.launch {
            AppGraph.artifacts.addPhoto(id, path)
            AppGraph.artifacts.byId(id)?.let { _draft.value = ArtifactRepository.toUi(it) }
        }
    }

    private fun scheduleReminder(id: String, title: String, epochDay: Long?) {
        // напоминание живёт в процессе приложения; контекст берётся при вызове из экрана
        reminderHook?.invoke(id, title, epochDay)
    }

    companion object {
        /** Планировщик подключает экран (нужен Context); тесты подменяют. */
        var reminderHook: ((String, String, Long?) -> Unit)? = null

        fun factory(artifactId: String) = viewModelFactory {
            initializer { ArtifactEditViewModel(artifactId) }
        }
    }
}

private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

private fun epochDayToText(epochDay: Long?): String =
    epochDay?.let { LocalDate.ofEpochDay(it).format(DATE_FORMAT) } ?: ""

private fun textToEpochDay(text: String): Long? = runCatching {
    LocalDate.parse(text.trim(), DATE_FORMAT).toEpochDay()
}.getOrNull()

/** Создание/просмотр/редактирование записи реестра + аудит + фото. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArtifactEditScreen(artifactId: String, onBack: () -> Unit) {
    val vm: ArtifactEditViewModel = viewModel(
        key = "artifact_$artifactId",
        factory = ArtifactEditViewModel.factory(artifactId)
    )
    val draft by vm.draft.collectAsState()
    val audit by vm.audit.collectAsState()
    val context = LocalContext.current
    val keepExif by AppGraph.settings.keepExif.collectAsState(initial = true)

    LaunchedEffect(Unit) {
        ArtifactEditViewModel.reminderHook = { id, title, epochDay ->
            ReminderScheduler.scheduleNextInspection(context, id, title, epochDay)
        }
    }

    var title by rememberSaveable(draft.id) { mutableStateOf(draft.title) }
    var description by rememberSaveable(draft.id) { mutableStateOf(draft.description) }
    var address by rememberSaveable(draft.id) { mutableStateOf(draft.address) }
    var custodian by rememberSaveable(draft.id) { mutableStateOf(draft.custodian) }
    var discoveryText by rememberSaveable(draft.id) { mutableStateOf(epochDayToText(draft.discoveryEpochDay)) }
    var inspectionText by rememberSaveable(draft.id) { mutableStateOf(epochDayToText(draft.nextInspectionEpochDay)) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val path = PhotoStore.importPhoto(context, it, draft.id.ifEmpty { "tmp" }, keepExif)
            if (path != null) vm.addPhoto(path)
        }
    }

    fun commitToDraft() {
        vm.update {
            it.copy(
                title = title, description = description, address = address,
                custodian = custodian,
                discoveryEpochDay = textToEpochDay(discoveryText),
                nextInspectionEpochDay = textToEpochDay(inspectionText)
            )
        }
    }

    LaunchedEffect(title, description, address, custodian, discoveryText, inspectionText) {
        commitToDraft()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("edit_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                stringResource(if (vm.isNew) R.string.new_artifact_title else R.string.edit_artifact_title),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text(stringResource(R.string.field_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("field_title")
            )
        }

        item {
            Text(stringResource(R.string.field_category), style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ArtifactCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = draft.category == cat,
                        onClick = { vm.update { it.copy(category = cat) } },
                        label = { Text(categoryLabel(cat)) },
                        modifier = Modifier.testTag("category_${cat.name}")
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text(stringResource(R.string.field_description)) },
                modifier = Modifier.fillMaxWidth().testTag("field_description"),
                minLines = 2
            )
        }

        item {
            OutlinedTextField(
                value = address, onValueChange = { address = it },
                label = { Text(stringResource(R.string.field_address)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("field_address")
            )
        }

        item {
            OutlinedTextField(
                value = custodian, onValueChange = { custodian = it },
                label = { Text(stringResource(R.string.field_custodian)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("field_custodian")
            )
        }

        item {
            OutlinedTextField(
                value = discoveryText, onValueChange = { discoveryText = it },
                label = { Text(stringResource(R.string.field_discovery_date)) },
                supportingText = { Text(stringResource(R.string.date_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("field_discovery")
            )
            OutlinedTextField(
                value = inspectionText, onValueChange = { inspectionText = it },
                label = { Text(stringResource(R.string.field_next_inspection)) },
                supportingText = { Text(stringResource(R.string.date_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("field_inspection")
            )
        }

        if (!vm.isNew) {
            item {
                Card {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                                            stringResource(R.string.status_label) + ": " + statusLabel(draft.custodyStatus),
                            style = MaterialTheme.typography.titleSmall
                        )
                        draft.custodyStatus.next()?.let { next ->
                            OutlinedButton(
                                onClick = { vm.advanceStatus({}) },
                                modifier = Modifier.testTag("advance_status")
                            ) {
                                Text(stringResource(R.string.advance_status) + " → " + statusLabel(next))
                            }
                        }
                    }
                }
            }

            item {
                Text(stringResource(R.string.photos_label, draft.photoPaths.size), style = MaterialTheme.typography.titleSmall)
                OutlinedButton(
                    onClick = {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.testTag("add_photo")
                ) {
                    Text(stringResource(R.string.add_photo))
                }
            }

            item {
                Text(stringResource(R.string.audit_label), style = MaterialTheme.typography.titleSmall)
            }
            items(audit, key = { it.id }) { entry ->
                Column(Modifier.padding(vertical = 2.dp)) {
                    Text(
                        "${entry.action} · ${java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(entry.timestamp))}",
                        style = MaterialTheme.typography.labelMedium
                    )
                    if (entry.details.isNotBlank()) {
                        Text(entry.details, style = MaterialTheme.typography.bodySmall)
                    }
                    HorizontalDivider()
                }
            }
        }

        item {
            Button(
                onClick = { vm.save(onBack) },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth().testTag("save_artifact")
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

@Composable
fun categoryLabel(category: ArtifactCategory): String = when (category) {
    ArtifactCategory.STONE_BLOCK -> stringResource(R.string.category_stone_block)
    ArtifactCategory.MASONRY -> stringResource(R.string.category_masonry)
    ArtifactCategory.POTTERY -> stringResource(R.string.category_pottery)
    ArtifactCategory.OTHER -> stringResource(R.string.category_other)
}
