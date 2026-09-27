package com.erebuni782.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.erebuni782.app.ui.MainShell
import com.erebuni782.app.ui.theme.Erebuni782Theme

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModel.factory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val skin by viewModel.skin.collectAsState()
            Erebuni782Theme(skinId = skin) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainShell(mainViewModel = viewModel)
                }
            }
        }
    }
}
