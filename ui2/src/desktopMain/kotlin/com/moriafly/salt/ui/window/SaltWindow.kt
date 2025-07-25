/*
 * Salt UI
 * Copyright (C) 2025 Moriafly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package com.moriafly.salt.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowDecoration
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.moriafly.salt.ui.UnstableSaltUiApi
import java.awt.Dimension
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

/**
 * # Salt Window
 *
 * @param properties [SaltWindowProperties]
 *
 * @see [Window]
 */
@UnstableSaltUiApi
@ExperimentalComposeUiApi
@Composable
fun SaltWindow(
    onCloseRequest: () -> Unit,
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    title: String = "Untitled",
    icon: Painter? = null,
    decoration: WindowDecoration = WindowDecoration.SystemDefault,
    transparent: Boolean = false,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    properties: SaltWindowProperties<ComposeWindow> = SaltWindowProperties(),
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable FrameWindowScope.() -> Unit
) {
    Window(
        onCloseRequest = onCloseRequest,
        state = state,
        visible = visible,
        title = title,
        icon = icon,
        decoration = decoration,
        transparent = transparent,
        resizable = resizable,
        enabled = enabled,
        focusable = focusable,
        alwaysOnTop = alwaysOnTop,
        onPreviewKeyEvent = onPreviewKeyEvent,
        onKeyEvent = onKeyEvent
    ) {
        val minSize = properties.minSize
        LaunchedEffect(minSize) {
            require(minSize.width.isSpecified && minSize.height.isSpecified) {
                "minSize.width and minSize.height must be specified"
            }

            // TODO Is this correct?
            window.minimumSize =
                Dimension(minSize.width.value.toInt(), minSize.height.value.toInt())
        }

        DisposableEffect(window) {
            val adapter = object : WindowAdapter(), ComponentListener {
                override fun windowActivated(e: WindowEvent?) {
                }

                override fun windowDeactivated(e: WindowEvent?) {
                }

                override fun windowIconified(e: WindowEvent?) {
                }

                override fun windowDeiconified(e: WindowEvent?) {
                }

                override fun windowStateChanged(e: WindowEvent) {
                }

                override fun componentResized(e: ComponentEvent?) {
                }

                override fun componentMoved(e: ComponentEvent?) {
                }

                override fun componentShown(e: ComponentEvent?) {
                    properties.onVisibleChanged(window, true)
                }

                override fun componentHidden(e: ComponentEvent?) {
                    properties.onVisibleChanged(window, false)
                }
            }

            window.addWindowListener(adapter)
            window.addWindowStateListener(adapter)
            window.addComponentListener(adapter)

            onDispose {
                window.removeWindowListener(adapter)
                window.removeWindowStateListener(adapter)
                window.removeComponentListener(adapter)
            }
        }

        content()
    }
}
