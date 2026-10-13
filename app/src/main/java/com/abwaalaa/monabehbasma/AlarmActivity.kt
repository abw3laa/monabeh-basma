package com.abwaalaa.monabehbasma

import android.app.Activity
import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmActivity : Activity() {

    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var isMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wake screen and show over lockscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val km = getSystemService(KEYGUARD_SERVICE) as? KeyguardManager
            km?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        window.statusBarColor = 0xFF080E14.toInt()
        window.navigationBarColor = 0xFF080E14.toInt()

        startAlarmSoundAndVibration()
        renderAlarmUi()
    }

    private fun startAlarmSoundAndVibration() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

            ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)?.apply {
                audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                play()
            }
        } catch (_: Exception) {}

        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getSystemService(VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(VIBRATOR_SERVICE) as? Vibrator
            }

            val pattern = longArrayOf(0, 800, 400, 800, 400)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (_: Exception) {}
    }

    private fun stopSoundAndVibrate() {
        try {
            ringtone?.stop()
            vibrator?.cancel()
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSoundAndVibrate()
    }

    private fun renderAlarmUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF080E14.toInt())
            setPadding(dp(20), dp(24), dp(20), dp(24))
            gravity = Gravity.CENTER_HORIZONTAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }

        val scroll = ScrollView(this).apply { isFillViewport = true }
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // Top Status Pills
        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        val statusPill = TextView(this).apply {
            text = getString(R.string.alarm_active_now)
            setTextColor(0xFF5EEAD4.toInt())
            textSize = 12f
            setPadding(dp(14), dp(6), dp(14), dp(6))
            background = GradientDrawable().apply {
                setColor(0x330D9488.toInt())
                cornerRadius = dp(20).toFloat()
                setStroke(dp(1), 0xFF14B8A6.toInt())
            }
        }
        topRow.addView(statusPill)
        body.addView(topRow)

        space(body, 24)

        // Alarm Clock Icon Circle
        val iconCircle = TextView(this).apply {
            text = "⏰"
            textSize = 42f
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                setColor(0xFFDC2626.toInt())
                shape = GradientDrawable.OVAL
            }
        }
        body.addView(iconCircle, LinearLayout.LayoutParams(dp(88), dp(88)).apply { gravity = Gravity.CENTER_HORIZONTAL })

        space(body, 16)

        // Big Time text
        val timeLabel = TextView(this).apply {
            text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            textSize = 44f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        body.addView(timeLabel)

        // Due title
        val dueTitle = TextView(this).apply {
            text = getString(R.string.alarm_time_arrived)
            textSize = 22f
            setTextColor(0xFFF1F5F9.toInt())
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        body.addView(dueTitle)

        space(body, 18)

        // 3-Day Window Card
        val windowCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = GradientDrawable().apply {
                setColor(0xFF121C26.toInt())
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), 0xFF1E293B.toInt())
            }
        }
        val windowHeader = TextView(this).apply {
            text = "نافذة الـ 3 أيام المتاحة"
            textSize = 14f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        windowCard.addView(windowHeader)
        val windowDays = TextView(this).apply {
            text = "اليوم 1 (الأساسي) · اليوم 2 (تذكير) · اليوم 3 (الفرصة الأخيرة)"
            textSize = 12f
            setTextColor(0xFF94A3B8.toInt())
            setPadding(0, dp(4), 0, dp(6))
        }
        windowCard.addView(windowDays)

        val warningNotice = TextView(this).apply {
            text = "تنبيه: إيقاف صوت المنبه لا يعتبر تأكيداً لحضور الموعد. لإثبات حضورك اضغط (تمت البصمة)."
            textSize = 11f
            setTextColor(0xFFFCA5A5.toInt())
            setPadding(dp(10), dp(8), dp(10), dp(8))
            background = GradientDrawable().apply {
                setColor(0x33B91C1C.toInt())
                cornerRadius = dp(10).toFloat()
            }
        }
        windowCard.addView(warningNotice)
        body.addView(windowCard, LinearLayout.LayoutParams(-1, -2))

        space(body, 28)

        // Button 1: Done (تمت البصمة)
        val btnDone = Button(this).apply {
            text = getString(R.string.alarm_btn_done)
            textSize = 16f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                setColor(0xFF006A61.toInt())
                cornerRadius = dp(16).toFloat()
            }
            setOnClickListener {
                stopSoundAndVibrate()
                // Mark latest active appointment as done
                val upcoming = AppointmentStore.all(this@AlarmActivity).filterNot { it.completed }.minByOrNull { it.dateMillis }
                if (upcoming != null) {
                    AppointmentStore.replace(this@AlarmActivity, upcoming.copy(completed = true, completedAt = System.currentTimeMillis()))
                }
                finish()
            }
        }
        body.addView(btnDone, LinearLayout.LayoutParams(-1, dp(56)))

        space(body, 10)

        // Button 2: Snooze 60 min (ذكرني لاحقاً)
        val btnSnooze = Button(this).apply {
            text = getString(R.string.alarm_btn_snooze) + " (60 دقيقة)"
            textSize = 14f
            setTextColor(0xFF2DD4BF.toInt())
            background = GradientDrawable().apply {
                setColor(0xFF0F2537.toInt())
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), 0xFF14B8A6.toInt())
            }
            setOnClickListener {
                stopSoundAndVibrate()
                scheduleSnooze60Min()
                finish()
            }
        }
        body.addView(btnSnooze, LinearLayout.LayoutParams(-1, dp(50)))

        space(body, 8)

        // Button 3: Mute Only
        val btnMute = TextView(this).apply {
            text = getString(R.string.alarm_btn_mute)
            textSize = 12f
            setTextColor(0xFF94A3B8.toInt())
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setOnClickListener {
                if (!isMuted) {
                    stopSoundAndVibrate()
                    isMuted = true
                    text = "تم كتم الصوت · الشاشة نشطة حتى تأكيد الموعد"
                    setTextColor(0xFF2DD4BF.toInt())
                }
            }
        }
        body.addView(btnMute)

        scroll.addView(body)
        root.addView(scroll, LinearLayout.LayoutParams(-1, -1))
        setContentView(root)
    }

    private fun scheduleSnooze60Min() {
        val am = getSystemService(ALARM_SERVICE) as? AlarmManager ?: return
        val triggerAt = System.currentTimeMillis() + (60 * 60 * 1000L)
        val intent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            this,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= 31 && am.canScheduleExactAlarms()) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    private fun space(parent: LinearLayout, heightDp: Int) {
        parent.addView(View(this), LinearLayout.LayoutParams(1, dp(heightDp)))
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
