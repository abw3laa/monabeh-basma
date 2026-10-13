package com.abwaalaa.monabehbasma

import android.app.Activity
import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class AlarmActivity : Activity() {
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var isMuted = false
    private var isDark = true
    private val navy = Color.rgb(0, 15, 29)
    private val darkBg = Color.rgb(7, 19, 29)
    private val teal = Color.rgb(0, 106, 97)
    private val urgent = Color.rgb(220, 38, 38)
    private val mutedDark = Color.rgb(120, 141, 163)
    private val mutedLight = Color.rgb(67, 71, 76)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDark = getSharedPreferences("app_settings", MODE_PRIVATE).getBoolean("dark_mode", true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            (getSystemService(KEYGUARD_SERVICE) as? KeyguardManager)?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        window.statusBarColor = if (isDark) darkBg else Color.rgb(247, 249, 251)
        window.navigationBarColor = if (isDark) darkBg else Color.WHITE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = if (isDark) 0 else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        startAlarmSoundAndVibration()
        renderAlarmUi()
    }

    private fun startAlarmSoundAndVibration() {
        try {
            val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
            val savedUri = prefs.getString("alarm_sound_uri", "") ?: ""
            val uri = if (savedUri.isNotBlank()) android.net.Uri.parse(savedUri) else
                (RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
            ringtone = RingtoneManager.getRingtone(applicationContext, uri)?.apply {
                audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
                play()
            }
        } catch (_: Exception) { }
        if (getSharedPreferences("app_settings", MODE_PRIVATE).getBoolean("alarm_vibration_enabled", true)) try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) (getSystemService(VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator else {
                @Suppress("DEPRECATION")
                getSystemService(VIBRATOR_SERVICE) as? Vibrator
            }
            val pattern = longArrayOf(0, 800, 400, 800, 400)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0)) else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (_: Exception) { }
    }

    private fun stopSound() { try { ringtone?.stop() } catch (_: Exception) { }; ringtone = null }
    private fun stopSoundAndVibration() { stopSound(); try { vibrator?.cancel() } catch (_: Exception) { } }
    override fun onDestroy() { stopSoundAndVibration(); super.onDestroy() }

    private fun renderAlarmUi() {
        val pageBg = if (isDark) darkBg else Color.rgb(247, 249, 251)
        val heroBg = if (isDark) navy else Color.WHITE
        val primaryText = if (isDark) Color.WHITE else Color.rgb(0, 15, 29)
        val secondaryText = if (isDark) mutedDark else mutedLight
        val accentText = if (isDark) Color.rgb(137, 245, 231) else teal
        val surface = if (isDark) Color.argb(20, 255, 255, 255) else Color.rgb(242, 244, 246)
        val upcoming = AppointmentStore.all(this).filterNot { it.completed }.minByOrNull { it.dateMillis }
        val appointmentTime = upcoming?.dateMillis ?: System.currentTimeMillis()
        val now = System.currentTimeMillis()
        val appointmentDay = Calendar.getInstance().apply { timeInMillis = appointmentTime; set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
        val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
        val dayIndex = (1 + ((today.timeInMillis - appointmentDay.timeInMillis) / 86400000L).toInt()).coerceIn(1, 3)
        val dateFmt = SimpleDateFormat("EEEE، d MMMM yyyy", Locale.getDefault())
        val shortDateFmt = SimpleDateFormat("d MMM", Locale.getDefault())
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        val hour = Calendar.getInstance().apply { timeInMillis = appointmentTime }.get(Calendar.HOUR_OF_DAY)
        val timeSuffix = when (Locale.getDefault().language) {
            "ar" -> if (hour < 12) "صباحًا" else "مساءً"
            "tr" -> ""
            else -> SimpleDateFormat("a", Locale.getDefault()).format(Date(appointmentTime))
        }

        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(pageBg)
            setPadding(dp(16), dp(10), dp(16), dp(18))
            layoutDirection = resources.configuration.layoutDirection
        }
        val scroll = ScrollView(this).apply { isFillViewport = true; overScrollMode = View.OVER_SCROLL_NEVER }
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            background = rounded(heroBg, dp(26), if (isDark) null else Color.rgb(225, 229, 233))
        }

        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val status = label(getString(R.string.alarm_active_now), 11f, if (isDark) accentText else urgent, true).apply {
            setPadding(dp(11), dp(7), dp(11), dp(7))
            background = rounded(if (isDark) Color.argb(25, 137, 245, 231) else Color.rgb(255, 235, 235), dp(24), null)
        }
        top.addView(status, LinearLayout.LayoutParams(0, -2, 1f))
        top.addView(label("◖  " + getString(R.string.alarm_sound_vibrate), 10f, secondaryText, false).apply { gravity = Gravity.END or Gravity.CENTER_VERTICAL })
        hero.addView(top, matchWrap())

        space(hero, 18)
        val emblem = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            background = rounded(if (isDark) Color.argb(24, 220, 38, 38) else Color.argb(18, 220, 38, 38), dp(88), null)
        }
        emblem.addView(FingerprintMark(this, isDark), LinearLayout.LayoutParams(dp(112), dp(112)))
        hero.addView(emblem, LinearLayout.LayoutParams(dp(176), dp(176)).apply { gravity = Gravity.CENTER })
        space(hero, 10)

        val timeRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER }
        timeRow.addView(label(timeFmt.format(Date(appointmentTime)), 48f, primaryText, true))
        if (timeSuffix.isNotBlank()) timeRow.addView(label("  " + timeSuffix, 15f, accentText, true).apply { gravity = Gravity.BOTTOM; setPadding(0, 0, 0, dp(5)) })
        hero.addView(timeRow, matchWrap())
        hero.addView(label(dateFmt.format(Date(appointmentTime)), 12f, secondaryText, false).apply { gravity = Gravity.CENTER; setPadding(0, dp(5), 0, 0) }, matchWrap())
        space(hero, 16)
        hero.addView(label(getString(R.string.alarm_time_arrived), 22f, primaryText, true).apply { gravity = Gravity.CENTER }, matchWrap())

        space(hero, 22)
        val timeline = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(15), dp(14), dp(15))
            background = rounded(surface, dp(18), if (isDark) null else Color.rgb(226, 230, 234))
        }
        val timelineHead = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        timelineHead.addView(label(getString(R.string.alarm_window_title), 14f, primaryText, true), LinearLayout.LayoutParams(0, -2, 1f))
        timelineHead.addView(label(getString(R.string.alarm_day_progress, dayIndex), 10f, if (isDark) Color.rgb(255, 218, 214) else urgent, true).apply {
            setPadding(dp(9), dp(5), dp(9), dp(5))
            background = rounded(if (isDark) Color.argb(50, 220, 38, 38) else Color.rgb(255, 226, 226), dp(20), null)
        })
        timeline.addView(timelineHead, matchWrap())
        space(timeline, 14)
        val dayRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val dayLabels = listOf(getString(R.string.alarm_day_active), getString(R.string.alarm_day_reminder), getString(R.string.alarm_day_last))
        for (index in 0..2) {
            val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            col.addView(View(this).apply { background = rounded(if (index + 1 == dayIndex) urgent else if (isDark) Color.argb(45, 224, 227, 229) else Color.rgb(224, 227, 229), dp(5), null) }, LinearLayout.LayoutParams(-1, dp(7)))
            space(col, 8)
            col.addView(label(dayLabels[index], 10f, if (index + 1 == dayIndex) primaryText else secondaryText, index + 1 == dayIndex))
            val date = Calendar.getInstance().apply { timeInMillis = appointmentTime; add(Calendar.DAY_OF_YEAR, index) }
            col.addView(label(shortDateFmt.format(date.time), 10f, secondaryText, false).apply { setPadding(0, dp(3), 0, 0) })
            dayRow.addView(col, LinearLayout.LayoutParams(0, -2, 1f).apply { if (index != 2) marginEnd = dp(8) })
        }
        timeline.addView(dayRow, matchWrap())
        space(timeline, 14)
        val closeDate = Calendar.getInstance().apply { timeInMillis = appointmentTime; add(Calendar.DAY_OF_YEAR, 3); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }
        val hoursLeft = max(0L, closeDate.timeInMillis - now) / 3600000L
        val remainingText = when (Locale.getDefault().language) {
            "ar" -> "$hoursLeft ساعة"
            "tr" -> "$hoursLeft saat"
            else -> "$hoursLeft hours"
        }
        val foot = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(10), dp(4), 0)
            background = rounded(if (isDark) Color.argb(10, 255, 255, 255) else Color.rgb(236, 238, 240), dp(10), null)
        }
        foot.addView(label(getString(R.string.alarm_closes_in, remainingText), 10.5f, secondaryText, false))
        foot.addView(label(shortDateFmt.format(Date(appointmentTime)) + " – " + shortDateFmt.format(Date(appointmentTime + 2 * 86400000L)), 10.5f, accentText, true).apply { gravity = Gravity.END; setPadding(0, dp(4), 0, 0) })
        timeline.addView(foot, matchWrap())
        hero.addView(timeline, matchWrap())

        space(hero, 12)
        val warning = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = rounded(if (isDark) Color.argb(34, 220, 38, 38) else Color.rgb(255, 239, 239), dp(13), null)
        }
        warning.addView(label("ⓘ", 18f, urgent, true).apply { setPadding(0, 0, dp(8), 0) })
        warning.addView(label(getString(R.string.alarm_warning), 11f, if (isDark) Color.WHITE else Color.rgb(25, 28, 30), false))
        hero.addView(warning, matchWrap())

        space(hero, 20)
        hero.addView(actionButton("✓   " + getString(R.string.alarm_btn_done), teal, Color.WHITE, 16f, true).apply {
            setOnClickListener { confirmAppointment(upcoming) }
        }, LinearLayout.LayoutParams(-1, dp(56)))
        space(hero, 10)
        hero.addView(actionButton("◷   " + getString(R.string.alarm_btn_snooze), if (isDark) Color.argb(25, 255, 255, 255) else Color.WHITE, primaryText, 14f, false).apply {
            background = rounded(if (isDark) Color.argb(25, 255, 255, 255) else Color.WHITE, dp(16), if (isDark) Color.argb(40, 255, 255, 255) else Color.rgb(195, 199, 205))
            setOnClickListener {
                stopSoundAndVibration()
                scheduleSnooze60Min()
                Toast.makeText(this@AlarmActivity, getString(R.string.alarm_snooze_success), Toast.LENGTH_SHORT).show()
                finish()
            }
        }, LinearLayout.LayoutParams(-1, dp(52)))
        space(hero, 4)
        hero.addView(label("◖   " + getString(R.string.alarm_btn_mute), 11.5f, secondaryText, false).apply {
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(10), dp(8), dp(10))
            setOnClickListener {
                if (!isMuted) {
                    stopSound()
                    isMuted = true
                    text = getString(R.string.alarm_muted_success)
                    setTextColor(accentText)
                    Toast.makeText(this@AlarmActivity, getString(R.string.alarm_muted_success), Toast.LENGTH_SHORT).show()
                }
            }
        }, matchWrap())
        space(hero, 12)
        hero.addView(label("◷  " + getString(R.string.alarm_snooze_note), 10f, secondaryText, false).apply {
            gravity = Gravity.CENTER
            setLineSpacing(dp(2).toFloat(), 1f)
        }, matchWrap())

        scroll.addView(hero, ScrollView.LayoutParams(-1, -2))
        outer.addView(scroll, LinearLayout.LayoutParams(-1, -1))
        setContentView(outer)
    }

    private fun confirmAppointment(current: Appointment?) {
        stopSoundAndVibration()
        if (current != null) {
            AppointmentStore.replace(this, current.copy(completed = true, completedAt = System.currentTimeMillis()))
            val nextDate = Calendar.getInstance().apply { timeInMillis = current.dateMillis; add(Calendar.DAY_OF_YEAR, current.recurrenceDays) }
            val next = Appointment(System.currentTimeMillis(), nextDate.timeInMillis, current.recurrenceDays)
            AppointmentStore.add(this, next)
            ReminderScheduler.schedule(this, next)
        }
        Toast.makeText(this, getString(R.string.alarm_confirm_success), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun scheduleSnooze60Min() {
        val manager = getSystemService(ALARM_SERVICE) as? AlarmManager ?: return
        val triggerAt = System.currentTimeMillis() + 60 * 60 * 1000L
        val intent = Intent(this, AlarmActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }
        val pending = PendingIntent.getActivity(this, 9999, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        if (Build.VERSION.SDK_INT >= 31 && manager.canScheduleExactAlarms()) manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        else manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
    }

    private fun label(value: String, size: Float, color: Int, bold: Boolean): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) typeface = android.graphics.Typeface.DEFAULT_BOLD
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        includeFontPadding = true
        setLineSpacing(dp(1).toFloat(), 1f)
    }
    private fun actionButton(value: String, bg: Int, fg: Int, size: Float, bold: Boolean) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(fg)
        gravity = Gravity.CENTER
        if (bold) typeface = android.graphics.Typeface.DEFAULT_BOLD
        background = rounded(bg, dp(16), null)
        isClickable = true
        isFocusable = true
    }
    private fun rounded(color: Int, radius: Int, stroke: Int?): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = radius.toFloat()
        if (stroke != null) setStroke(dp(1), stroke)
    }
    private fun matchWrap() = LinearLayout.LayoutParams(-1, -2)
    private fun space(parent: LinearLayout, height: Int) { parent.addView(View(this), LinearLayout.LayoutParams(1, dp(height))) }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private class FingerprintMark(context: android.content.Context, private val dark: Boolean) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f * resources.displayMetrics.density
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            color = Color.rgb(220, 38, 38)
        }
        private val badge = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(220, 38, 38) }
        private val badgeText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 13f * resources.displayMetrics.density
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val d = resources.displayMetrics.density
            val cx = width / 2f
            val cy = height / 2f
            val radius = min(width, height) * 0.43f
            val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = if (dark) Color.rgb(0, 15, 29) else Color.WHITE }
            canvas.drawCircle(cx, cy, radius, fill)
            val s = radius / 50f
            canvas.save()
            canvas.translate(cx - 50f * s, cy - 50f * s)
            canvas.scale(s, s)
            val p = Path().apply { moveTo(17f, 45f); cubicTo(17f, 22f, 34f, 12f, 50f, 16f); cubicTo(68f, 19f, 77f, 34f, 76f, 49f) }
            canvas.drawPath(p, paint)
            val p2 = Path().apply { moveTo(25f, 47f); cubicTo(25f, 30f, 36f, 24f, 49f, 25f); cubicTo(62f, 26f, 68f, 36f, 67f, 48f) }
            canvas.drawPath(p2, paint)
            val p3 = Path().apply { moveTo(34f, 47f); cubicTo(34f, 37f, 41f, 33f, 49f, 34f); cubicTo(57f, 35f, 59f, 41f, 58f, 48f) }
            canvas.drawPath(p3, paint)
            val p4 = Path().apply { moveTo(19f, 53f); cubicTo(19f, 65f, 15f, 74f, 11f, 81f); moveTo(29f, 54f); cubicTo(29f, 67f, 25f, 77f, 21f, 84f); moveTo(40f, 54f); cubicTo(40f, 68f, 36f, 79f, 32f, 87f); moveTo(51f, 54f); cubicTo(51f, 67f, 49f, 76f, 45f, 83f); moveTo(62f, 53f); cubicTo(62f, 62f, 61f, 68f, 58f, 75f) }
            canvas.drawPath(p4, paint)
            canvas.restore()
            val bx = cx + radius * 0.62f
            val by = cy + radius * 0.55f
            canvas.drawCircle(bx, by, 12f * d, badge)
            canvas.drawText("!", bx, by + 5f * d, badgeText)
        }
    }
}
