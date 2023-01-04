package config

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

data class Session(val host: String, val port: Number, val user: String, val password: String)


data class TerminalConfig(val sessions: List<Session>)

val mapper: ObjectMapper = ObjectMapper();
val configFile = File("E:\\project\\KeKeTerminal\\KekeTerminal\\src\\jvmMain\\resources\\config.json")

fun readConfigFromFile(): TerminalConfig {
    return mapper.readValue(configFile, TerminalConfig::class.java)
}

fun writeConfigToFile(config: TerminalConfig) {
    mapper.writeValue(configFile, config)
}