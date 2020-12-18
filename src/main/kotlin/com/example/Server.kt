package com.example

import com.example.entity.TCPClient
import com.example.server.NewClientHandler
import com.example.server.OnlineStatusHandler
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

suspend fun main() {
    Server().execute()
}

class Server {

    private val port = 8081

    suspend fun execute() {

        // https://play.kotlinlang.org/hands-on/Introduction%20to%20Coroutines%20and%20Channels/08_Channels
        val onlineClientsChannel = Channel<TCPClient>(UNLIMITED)
        val offlineClientsChannel = Channel<TCPClient>(UNLIMITED)

        val onlineClients: MutableMap<Int, TCPClient> = mutableMapOf()

        try {
            ServerSocket(port).use { serverSocket ->
                println("Server listening on port $port")

                while (true) {
                    val socket: Socket = serverSocket.accept()

                    GlobalScope.launch {
                        NewClientHandler().handleClient(socket, onlineClientsChannel, offlineClientsChannel)
                    }

                    GlobalScope.launch {
                        val onlineUser = onlineClientsChannel.receive()
                        val payload = Gson().toJson(onlineUser.payload)
                        println("Client joined with address: ${onlineUser.socket.remoteSocketAddress} & payload: $payload")

                        onlineClients[onlineUser.payload.userId] = onlineUser
                        println("Total online clients: " + onlineClients.size)

                        OnlineStatusHandler().sendOnlineMsgToConnectedClients(onlineClients, onlineUser)
                    }

                    GlobalScope.launch {
                        val offlineUser = offlineClientsChannel.receive()
                        println("userId:${offlineUser.payload.userId} is now offline")

                        onlineClients.remove(offlineUser.payload.userId)
                        println("Total online clients: ${onlineClients.size}")

                        OnlineStatusHandler().sendOfflineMsgToOnlineFriends(onlineClients, offlineUser)
                    }
                }
            }
        } catch (ex: IOException) {
            println("Error in the server: " + ex.message)
        }
    }
}