package com.developersyndicate.kxengine

import java.io.File
import java.util.Properties

object EngineConfig {
    var windowWidth = 800
    var windowHeight = 600
    var windowTitle = "KXEngine"
    var profilerEnabled = true
    var hotReloadEnabled = true

    fun load(path: String = "config.properties") {
        val file = File(path)
        if (file.exists()) {
            val props = Properties()
            try {
                file.inputStream().use { props.load(it) }
                windowWidth = props.getProperty("window.width", "800").toInt()
                windowHeight = props.getProperty("window.height", "600").toInt()
                windowTitle = props.getProperty("window.title", "KXEngine")
                profilerEnabled = props.getProperty("profiler.enabled", "true").toBoolean()
                hotReloadEnabled = props.getProperty("hotreload.enabled", "true").toBoolean()
                println("EngineConfig: Loaded configuration from $path")
            } catch (e: Exception) {
                println("Warning: Error loading configuration from $path: ${e.message}. Using defaults.")
            }
        } else {
            println("EngineConfig: $path not found. Using defaults.")
        }
    }
}
