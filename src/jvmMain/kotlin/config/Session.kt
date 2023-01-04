package config

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class Session() {
    var host: String? = null;
    var port: Number? = null;
    var user: String? = null;
    var password: String? = null
}

class Theme {
    val colors: Color = Color()
}

class Color {
    var black: String = ""
    var red: String = ""
    var green: String = ""
    var yellow: String = ""
    var blue: String = ""
    var magenta: String = ""
    var cyan: String = ""
    var white: String = ""
}

class Ui {
    val columns = 120
    val rows = 30
    val theme: Theme = Theme()
}

class TerminalConfig {
    val sessions: List<Session> = emptyList()
    val ui: Ui = Ui()
}

val mapper: ObjectMapper = ObjectMapper();
val configFile = File("E:\\project\\KeKeTerminal\\KekeTerminal\\src\\jvmMain\\resources\\config.json")

fun readConfigFromFile(): TerminalConfig {
    return mapper.readValue(configFile, TerminalConfig::class.java)
}

fun writeConfigToFile(config: TerminalConfig) {
    mapper.writeValue(configFile, config)
}