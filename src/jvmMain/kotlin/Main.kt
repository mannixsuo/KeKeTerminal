// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.pty4j.PtyProcessBuilder
import shell.LocalPty
import shell.Shell
import java.io.IOException
import java.io.InputStreamReader

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
    val localShell: Shell = LocalPty(PtyProcessBuilder(arrayOf("cmd.exe")).start())
    val channelInputStreamReader = localShell.getChannelInputStreamReader()
    val channelOutputStreamWriter = localShell.getChannelOutputStreamWriter()
    Thread {
        val inputStreamReader = InputStreamReader(System.`in`)
        val buf = CharArray(1024)
        var length: Int
        try {
            while (inputStreamReader.read(buf).also { length = it } != -1) {
                val s = String(buf, 0, length)
                if ("\n" == s) {
                    channelOutputStreamWriter.write(13)
                } else {
                    channelOutputStreamWriter.write(buf, 0, length)
                }
                channelOutputStreamWriter.flush()
            }
        } catch (ignore: IOException) {
        }
    }.start()

    Thread {
        val buf = CharArray(1024)
        var length: Int
        while (channelInputStreamReader.read(buf).also { length = it } != -1) {
            print(String(buf, 0, length))
        }
    }.start()
}
