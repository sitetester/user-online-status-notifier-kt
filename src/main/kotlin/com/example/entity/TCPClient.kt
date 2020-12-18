package com.example.entity

import java.net.Socket

data class TCPClient(var payload: Payload, var socket: Socket)