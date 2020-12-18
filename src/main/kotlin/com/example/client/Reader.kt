package com.example.client

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class Reader(private val socket: Socket) {

    fun read() {

        try {
            val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))

            while (true) {
                val msg = bufferedReader.readLine()
                if (msg != null) {
                    println("msg: $msg")
                } else {
                    println(":( Lost connection to server")
                    break
                }
            }
        } catch (ioException: IOException) {
            println(ioException.message)
        }
    }
}