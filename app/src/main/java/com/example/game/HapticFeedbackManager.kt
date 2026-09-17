package com.example.game

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticFeedbackManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var enabled: Boolean = true

    fun playLightTap() {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(18)
        }
    }

    fun playBlockBreak() {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(30)
        }
    }

    fun playHit() {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(45)
        }
    }

    fun playJackpot() {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return
        val pattern = longArrayOf(0, 40, 50, 40, 50, 100)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    fun playHurt() {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return
        val pattern = longArrayOf(0, 60, 40, 80)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    fun handleSfx(sfxName: String) {
        when {
            sfxName.startsWith("break_") || sfxName == "thwack" -> playBlockBreak()
            sfxName == "hit" || sfxName == "hurt" || sfxName == "death" -> playHurt()
            sfxName == "jackpot" || sfxName == "wc_confetti" || sfxName == "craft_reveal" -> playJackpot()
            sfxName == "pickup" || sfxName == "click" || sfxName == "pop" -> playLightTap()
            sfxName == "place" -> playLightTap()
            sfxName == "rex_roar" || sfxName == "harold_squeal" || sfxName == "boom_big" -> playHit()
        }
    }
}
