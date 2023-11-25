package com.tored.bridgelauncher.webview.serve

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.sun.net.httpserver.HttpServer
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.readDir
import com.tored.bridgelauncher.settings.settingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.util.concurrent.Executors

class BridgeHttpServerService : Service()
{
    private val TAG = "HTTPSERVER"

    private var _job = SupervisorJob()
    private var _scope = CoroutineScope(Dispatchers.IO + _job)

    private var _httpServer: HttpServer? = null
    private lateinit var _handler: BridgeHttpServerHandler

    override fun onCreate()
    {
        _handler = BridgeHttpServerHandler(applicationContext)

        _scope.launch {
            settingsDataStore.data.collectLatest { prefs ->
                _handler.projectRoot = prefs.readDir(SettingsState::currentProjDir)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        startServer()
        return START_STICKY
    }

    private fun startServer()
    {
        val server = HttpServer.create(InetSocketAddress(5000), 0)

        server.executor = Executors.newCachedThreadPool()
        server.createContext("/", _handler)
        server.start()

        _httpServer = server

        Log.i(TAG, "running @ ${server.address}")
    }

    override fun onDestroy()
    {
        stopServer()
    }

    private fun stopServer()
    {
        _job.cancel()

        _httpServer?.stop(0)
        _httpServer = null

        Log.i(TAG, "stopped")
    }

    override fun onBind(p0: Intent?): IBinder?
    {
        // no binding
        return null
    }
}