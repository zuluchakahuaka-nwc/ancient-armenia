package com.erebuni782.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erebuni782.app.data.SettingsRepository
import com.erebuni782.app.data.SettingsStore
import com.erebuni782.app.ui.theme.SkinId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Состояние корневого UI: текущий скин (язык живёт в per-app locales). */
class MainViewModel(private val store: SettingsStore) : ViewModel() {

    val skin: StateFlow<SkinId> = store.skin
        .stateIn(viewModelScope, SharingStarted.Eagerly, SkinId.URARTU)

    fun setSkin(id: SkinId) {
        viewModelScope.launch { store.setSkin(id) }
    }

    /** Первый запуск: выбор раздела (Урарту-Армения/Древняя Армения) вместо режима. */
    fun selectSection(section: String) {
        viewModelScope.launch {
            (store as? SettingsRepository)?.setSection(section)
            (store as? SettingsRepository)?.setModeSelected()
        }
    }

    /** Смена раздела из настроек. */
    fun setSection(section: String) {
        viewModelScope.launch { (store as? SettingsRepository)?.setSection(section) }
    }

    /** Онбординг просмотрен. */
    fun setOnboardingShown() {
        viewModelScope.launch { (store as? SettingsRepository)?.setOnboardingShown() }
    }

    /** Язык выбран. */
    fun setLangSelected() {
        viewModelScope.launch { (store as? SettingsRepository)?.setLangSelected() }
    }

    companion object {
        fun factory(appContext: Context) = viewModelFactory {
            initializer { MainViewModel(SettingsRepository(appContext.applicationContext)) }
        }
    }
}
