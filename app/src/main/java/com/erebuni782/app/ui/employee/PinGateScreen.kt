package com.erebuni782.app.ui.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.erebuni782.app.AppGraph
import com.erebuni782.app.R
import com.erebuni782.app.data.EmployeeSession
import com.erebuni782.app.data.PinHasher

/**
 * D3: PIN-гейт. Первый запуск — установка (двойной ввод),
 * далее — ввод. Разблокировка живёт до смерти процесса (EmployeeSession).
 */
@Composable
fun PinGateScreen(onUnlocked: () -> Unit) {
    val setupMode = !AppGraph.pin.hasPin()
    var pin by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    val msgBadFormat = stringResource(R.string.pin_bad_format)
    val msgMismatch = stringResource(R.string.pin_mismatch)
    val msgWrong = stringResource(R.string.pin_wrong)

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(if (setupMode) R.string.pin_setup_title else R.string.pin_enter_title),
            style = MaterialTheme.typography.headlineSmall
        )

        TextField(
            value = pin,
            onValueChange = { if (it.length <= 8 && it.all { c -> c.isDigit() }) pin = it },
            label = { Text(stringResource(R.string.pin_label)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier.testTag("pin_input")
        )

        if (setupMode) {
            TextField(
                value = confirm,
                onValueChange = { if (it.length <= 8 && it.all { c -> c.isDigit() }) confirm = it },
                label = { Text(stringResource(R.string.pin_confirm_label)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.testTag("pin_confirm")
            )
        }

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = {
                val ok = if (setupMode) {
                    when {
                        !PinHasher.isValidPinFormat(pin) -> {
                            error = msgBadFormat; false
                        }
                        pin != confirm -> {
                            error = msgMismatch; false
                        }
                        else -> {
                            AppGraph.pin.setPin(pin); true
                        }
                    }
                } else {
                    AppGraph.pin.verify(pin).also { if (!it) error = msgWrong }
                }
                if (ok) {
                    EmployeeSession.unlocked = true
                    onUnlocked()
                }
            },
            enabled = pin.isNotEmpty(),
            modifier = Modifier.testTag("pin_submit")
        ) {
            Text(stringResource(if (setupMode) R.string.pin_setup_button else R.string.pin_enter_button))
        }
    }
}
