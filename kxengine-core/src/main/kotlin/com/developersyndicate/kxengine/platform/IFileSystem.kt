package com.developersyndicate.kxengine.platform

interface IFileSystem {
    fun readBytes(path: String): ByteArray
    fun readText(path: String): String
    fun exists(path: String): Boolean
    fun lastModified(path: String): Long
}
