package com.erebuni782.app.sync

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

/**
 * Транспорт синка. TCP-реализация (loopback для AVD; железо → Bluetooth-
 * реализация того же интерфейса позже). Кадр: 4 байта длины + UTF-8 JSON.
 */
class SyncServer(private val port: Int) {

    @Volatile
    private var serverSocket: ServerSocket? = null

    /** Принимает ОДНО соединение, отвечает на сообщение, закрывается. */
    fun start(handle: (String) -> String) {
        val ss = ServerSocket(port)
        serverSocket = ss
        thread(name = "sync-server") {
            runCatching {
                ss.accept().use { socket ->
                    val input = DataInputStream(socket.getInputStream().buffered())
                    val output = DataOutputStream(socket.getOutputStream().buffered())
                    val msg = readFrame(input)
                    val reply = handle(msg)
                    writeFrame(output, reply)
                    output.flush()
                }
            }
            runCatching { ss.close() }
        }
    }

    fun stop() {
        runCatching { serverSocket?.close() }
    }
}

class SyncClient {
    fun exchange(host: String, port: Int, message: String, timeoutMs: Int = 10_000): String {
        Socket().use { socket ->
            socket.soTimeout = timeoutMs
            socket.connect(java.net.InetSocketAddress(host, port), timeoutMs)
            val output = DataOutputStream(socket.getOutputStream().buffered())
            val input = DataInputStream(socket.getInputStream().buffered())
            writeFrame(output, message)
            output.flush()
            return readFrame(input)
        }
    }
}

internal fun writeFrame(output: DataOutputStream, text: String) {
    val bytes = text.toByteArray(Charsets.UTF_8)
    output.writeInt(bytes.size)
    output.write(bytes)
}

internal fun readFrame(input: DataInputStream): String {
    val len = input.readInt()
    require(len in 1..50_000_000) { "bad frame length $len" }
    val bytes = ByteArray(len)
    input.readFully(bytes)
    return String(bytes, Charsets.UTF_8)
}
