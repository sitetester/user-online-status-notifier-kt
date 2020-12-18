package com.example.client

import com.example.entity.Payload
import com.google.gson.Gson
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import java.util.stream.Stream

class Writer(socket: Socket) {

    private var printWriter = getPrintWriter(socket)

    private fun getPrintWriter(socket: Socket): PrintWriter? {

        var printWriter: PrintWriter? = null

        try {
            printWriter = PrintWriter(socket.getOutputStream(), true)
        } catch (ex: IOException) {
            println("Error getting output stream: " + ex.message)
            ex.printStackTrace()
        }

        return printWriter
    }

    fun write() {

        val scanner = Scanner(System.`in`)
        print("Enter userId: ")

        val userId = scanner.nextLine()
        print("Enter friends(comma separated integers): ")

        val friendsStr = scanner.nextLine()

        var payload: Payload = if (friendsStr.trim { it <= ' ' }.isNotEmpty()) {
            Payload(
                userId.toInt(),
                Stream.of(*friendsStr.split(",").toTypedArray()).mapToInt { s: String -> s.toInt() }
                    .toArray()
            )
        } else {
            val friends = IntArray(0)
            Payload(
                userId.toInt(),
                friends
            )
        }

        val json = Gson().toJson(payload)
        println("Sending JSON payload to server: $json")

        printWriter?.println(json)
    }
}