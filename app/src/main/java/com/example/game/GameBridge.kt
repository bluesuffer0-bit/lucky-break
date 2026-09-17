package com.example.game

import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class GameBridge(
    private val hapticManager: HapticFeedbackManager,
    private val onEngineLoaded: () -> Unit
) {
    private var webViewRef: WebView? = null

    private val _playerStats = MutableStateFlow(PlayerStats())
    val playerStats: StateFlow<PlayerStats> = _playerStats.asStateFlow()

    private val _isEngineReady = MutableStateFlow(false)
    val isEngineReady: StateFlow<Boolean> = _isEngineReady.asStateFlow()

    fun attachWebView(webView: WebView) {
        this.webViewRef = webView
    }

    fun detachWebView() {
        this.webViewRef = null
    }

    @JavascriptInterface
    fun onEngineReady() {
        _isEngineReady.value = true
        onEngineLoaded()
    }

    @JavascriptInterface
    fun onSfx(name: String) {
        hapticManager.handleSfx(name)
    }

    @JavascriptInterface
    fun onPlayerState(jsonStr: String) {
        try {
            val obj = JSONObject(jsonStr)
            _playerStats.value = PlayerStats(
                hp = obj.optInt("hp", 20),
                maxHp = obj.optInt("maxHp", 20),
                hunger = obj.optInt("hunger", 20),
                x = obj.optInt("x", 0),
                y = obj.optInt("y", 0),
                z = obj.optInt("z", 0),
                dead = obj.optBoolean("dead", false),
                onGround = obj.optBoolean("onGround", true),
                selectedSlot = obj.optInt("selectedSlot", 0)
            )
        } catch (_: Exception) {}
    }

    private fun eval(js: String) {
        webViewRef?.post {
            webViewRef?.evaluateJavascript(js, null)
        }
    }

    fun setMovement(forward: Boolean, back: Boolean, left: Boolean, right: Boolean) {
        eval("window.__luckyMobile && window.__luckyMobile.setMovement($forward, $back, $left, $right);")
    }

    fun setJump(down: Boolean) {
        eval("window.__luckyMobile && window.__luckyMobile.setJump($down);")
    }

    fun setSprint(down: Boolean) {
        eval("window.__luckyMobile && window.__luckyMobile.setSprint($down);")
    }

    fun setSneak(down: Boolean) {
        eval("window.__luckyMobile && window.__luckyMobile.setSneak($down);")
    }

    fun setMine(down: Boolean) {
        eval("window.__luckyMobile && window.__luckyMobile.setMine($down);")
    }

    fun setPlace(down: Boolean) {
        eval("window.__luckyMobile && window.__luckyMobile.setPlace($down);")
    }

    fun selectHotbar(slot: Int) {
        eval("window.__luckyMobile && window.__luckyMobile.selectHotbar($slot);")
    }

    fun openInventory() {
        eval("window.__luckyMobile && window.__luckyMobile.openInventory();")
    }

    fun openQuests() {
        eval("window.__luckyMobile && window.__luckyMobile.openQuests();")
    }

    fun openCodex() {
        eval("window.__luckyMobile && window.__luckyMobile.openCodex();")
    }

    fun dropItem(stack: Boolean = false) {
        eval("window.__luckyMobile && window.__luckyMobile.dropItem($stack);")
    }

    fun interactWagon() {
        eval("window.__luckyMobile && window.__luckyMobile.interactWagon();")
    }

    fun unstuck() {
        eval("window.__luckyMobile && window.__luckyMobile.unstuck();")
    }

    fun saveGame() {
        eval("window.__luckyMobile && window.__luckyMobile.saveGame();")
    }

    fun setSensitivity(sensitivity: Float) {
        eval("window.__luckyMobile && window.__luckyMobile.setSensitivity($sensitivity);")
    }

    fun setInvertY(invertY: Boolean) {
        eval("window.__luckyMobile && window.__luckyMobile.setInvertY($invertY);")
    }
}
