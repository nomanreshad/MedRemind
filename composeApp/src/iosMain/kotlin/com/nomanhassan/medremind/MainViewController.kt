package com.nomanhassan.medremind

import androidx.compose.ui.window.ComposeUIViewController
import com.nomanhassan.medremind.app.App
import com.nomanhassan.medremind.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) { App() }