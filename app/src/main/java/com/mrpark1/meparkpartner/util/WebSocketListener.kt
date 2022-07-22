package com.mrpark1.meparkpartner.util

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketListener(private val onMessageListener: (String) -> Unit) : WebSocketListener() {
    //웹소켓 유틸
    override fun onOpen(webSocket: WebSocket, response: Response) {
        //webSocket.send("{\"type\":\"ticker\", \"symbols\": [\"BTC_KRW\"], \"tickTypes\": [\"30M\"]}")
        //webSocket.close(NORMAL_CLOSURE_STATUS, null)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("Socket@", "Receiving : $text")
        onMessageListener(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("Socket@", "Receiving bytes : $bytes")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("Socket@", "Closing : $code / $reason")
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        webSocket.cancel()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("Socket@", "Error : " + t.message)
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}