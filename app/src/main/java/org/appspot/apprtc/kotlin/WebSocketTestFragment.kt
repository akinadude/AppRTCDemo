package org.appspot.apprtc.kotlin

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_websocket_test.*
import okhttp3.*
import okio.ByteString


class WebSocketTestFragment : Fragment() {

    companion object {
        val TAG = WebSocketTestFragment::class.java.simpleName
        const val NORMAL_CLOSURE_STATUS = 1000

        fun newInstance(): WebSocketTestFragment {
            return WebSocketTestFragment()
        }
    }

    private val client: OkHttpClient? = OkHttpClient()
    private val webSocket: WebSocket? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(org.appspot.apprtc.R.layout.fragment_websocket_test, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val request = Request.Builder().url("ws://echo.websocket.org").build()
        val listener = EchoWebSocketListener()
        val webSocket = client?.newWebSocket(request, listener)
        client?.dispatcher()?.executorService()?.shutdown()

        //ws://10.0.2.2:8080/websocket/chat
        //ws://echo.websocket.org

        val pingHandler = Handler()
        val pingRunnable = object : Runnable {
            override fun run() {
                val ping = "{\"type\":\"ping\",\"message\":\"hello\"}"
                print("Tx: $ping")
                webSocket?.send(ping)
                pingHandler.postDelayed(this, 5000)
            }
        }
        //pingHandler.post(pingRunnable)

    }

    //todo нужно подумать, как связать это с RxJava и с работой в отдельном потоке

    //todo Видимо понадобится механизм pub/sub/event service для обработки входящих сообщений?

    //todo можно попробовать сделать на subject'ах. Адрес вебсокета можно взять отсюда https://github.com/mayuroks/android-mvp-realtime-chat

    /*
    todo Можем обернуть отправку сообщения на сервер во что-то вроде этого
    public Flowable<ChatMessage> sendMessage(@NonNull final ChatMessage chatMessage) {
        return Flowable.create(new FlowableOnSubscribe<ChatMessage>() {
            @Override
            public void subscribe(FlowableEmitter<ChatMessage> emitter) throws Exception {
                mSocket.emit(EVENT_NEW_MESSAGE, chatMessage.getMessage());
                emitter.onNext(chatMessage);
            }
        }, BackpressureStrategy.BUFFER);
    }
    */

    //todo server events
    /*
    /**
    * Main interface to listen to server events.
    */
    public interface EventListener {

        void onConnect(Object... args);

        void onDisconnect(Object... args);

        void onNewMessage(Object... args);
    }
    */

    //todo У rx есть singleThreadScheduler. Он подойдет для запуска на нем вебсокета?

    //todo нужно представить входящие сообщения потоком, который будет кто-то обзервить.

    override fun onDestroyView() {
        super.onDestroyView()

        webSocket?.close(0, "Closing the screen")
    }

    private fun print(txt: String) {
        activity?.runOnUiThread {
            output.text = output.text.toString() + "\n\n" + txt
        }
    }

    inner class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)

            webSocket.send("Hello, it's SSaurel !")
            webSocket.send("What's up ?")
            webSocket.send(ByteString.decodeHex("deadbeef"))
            webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.d(TAG, "Receiving : $text")
            print("Receiving : $text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            Log.d(TAG, "Receiving : ${bytes.hex()}")
            print("Receiving : ${bytes.hex()}")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            Log.d(TAG, "Closing : $code/$reason")
            print("Closing : $code/$reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
        }
    }
}