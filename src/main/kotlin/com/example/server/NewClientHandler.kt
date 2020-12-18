package com.example.server

import com.example.entity.Payload
import com.example.entity.TCPClient
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class NewClientHandler {

    suspend fun handleClient(
        socket: Socket,
        onlineClientsChannel: Channel<TCPClient>,
        offlineClientsChannel: Channel<TCPClient>
    ) {

        val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
        var tcpClient: TCPClient? = null

        while (true) {
            val json = bufferedReader.readLine()
            if (json != null) {
                val payload = Gson().fromJson<Any>(json, Payload::class.java) as Payload
                tcpClient = TCPClient(payload, socket)
                onlineClientsChannel.send(tcpClient)
            } else {
                if (tcpClient != null) {
                    offlineClientsChannel.send(tcpClient)
                    break
                }
            }
        }
    }
}