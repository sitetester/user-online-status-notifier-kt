package com.example.server

import com.example.entity.OnlineStatus
import com.example.entity.TCPClient
import com.google.gson.Gson
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.*

class OnlineStatusHandler {

    private fun getWriter(socket: Socket): PrintWriter? {

        var writer: PrintWriter? = null

        try {
            writer = PrintWriter(socket.getOutputStream(), true)
        } catch (ex: IOException) {
            println("Error getting output stream: " + ex.message)
            ex.printStackTrace()
        }

        return writer
    }

    fun sendOnlineMsgToConnectedClients(onlineClients: MutableMap<Int, TCPClient>, onlineUser: TCPClient) {

        onlineClients.forEach { (userId, friend) ->

            if (isFriend(onlineUser.payload.userId, friend.payload.friends)) {

                println("Sending `online` msg to friend (userId): " + friend.payload.userId)

                val onlineStatus = OnlineStatus(onlineUser.payload.userId, true);
                val json = Gson().toJson(onlineStatus);
                getWriter(friend.socket)?.println(json);
            }
        }
    }

    fun sendOfflineMsgToOnlineFriends(onlineClients: MutableMap<Int, TCPClient>, offlineUser: TCPClient) {

        onlineClients.forEach { (userId, friend) ->
            if (isFriend(offlineUser.payload.userId, friend.payload.friends)) {

                println("Sending `offline` msg to friend (userId): " + friend.payload.userId)

                val onlineStatus = OnlineStatus(offlineUser.payload.userId, false);
                val json = Gson().toJson(onlineStatus);
                getWriter(friend.socket)?.println(json);
            }
        }
    }


    private fun isFriend(userId: Int, friends: IntArray?): Boolean {
        return Arrays.stream(friends).anyMatch { i: Int -> i == userId }
    }

}