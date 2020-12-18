package com.example

import com.example.client.Reader
import com.example.client.Writer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException


fun main() {
    Client().run()
}

class Client {

    private val serverPort = 8081

    fun run() {

        runBlocking {
            try {
                val socket = Socket("localhost", serverPort)
                println("Connected to server")

                val writeJob = GlobalScope.launch {
                    Writer(socket).write()
                }

                val readJob = GlobalScope.launch {
                    Reader(socket).read()
                }

                writeJob.join()
                readJob.join()

            } catch (ex: UnknownHostException) {
                println("Server not found: " + ex.message)

            } catch (ex: IOException) {
                println("I/O Error: " + ex.message + "\nHint: Most probably server is not running.")
            }
        }
    }
}