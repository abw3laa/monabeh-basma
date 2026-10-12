package com.abwaalaa.monabehbasma

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var remindersContainer: LinearLayout
    private val prefs by lazy { getSharedPreferences("reminders", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        renderScreen()
    }

    private fun renderScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(dp(20), dp(24), dp(20), dp(20))
            setBackgroundColor(0xFFF4F7F6.toInt())
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        content.addView(TextView(this).apply {
            text = "منبّه البصمة"
            textSize = 28f
            setTextColor(0xFF123D35.toInt())
            gravity = Gravity.RIGHT
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        content.addView(TextView(this).apply {
            text = "نظّم مواعيدك وتذكيراتك في مكان واحد"
            textSize = 15f
            setTextColor(0xFF58716B.toInt())
            setPadding(0, dp(6), 0, dp(22))
            gravity = Gravity.RIGHT
        })
        content.addView(Button(this).apply {
            text = "＋ إضافة تذكير"
            setOnClickListener { chooseReminderTime() }
        }, matchWrap())
        content.addView(TextView(this).apply {
            text = "تذكيراتي"
            textSize = 20f
            setTextColor(0xFF123D35.toInt())
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, dp(26), 0, dp(12))
        })
        remindersContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        content.addView(remindersContainer)
        content.addView(TextView(this).apply {
            text = "حول التطبيق · الإصدار 1.0.0"
            textSize = 13f
            setTextColor(0xFF6C817B.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(32), 0, dp(10))
        })
        content.addView(Button(this).apply {
            text = "البحث عن تحديثات"
            setOnClickListener {
                Toast.makeText(this@MainActivity, "سيتم ربط خدمة التحديثات بعد إعداد مستودع الإصدارات.", Toast.LENGTH_LONG).show()
            }
        }, matchWrap())
        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)
        renderReminders()
    }

    private fun chooseReminderTime() {
        val now = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            val whenToFire = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
            val label = "تذكير"
            scheduleReminder(id, label, whenToFire.timeInMillis)
            val current = prefs.getStringSet("items", emptySet())!!.toMutableSet()
            current.add("\${id}|\${label}|\${whenToFire.timeInMillis}")
            prefs.edit().putStringSet("items", current).apply()
            renderReminders()
            Toast.makeText(this, "تمت إضافة التذكير", Toast.LENGTH_SHORT).show()
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
    }

    private fun scheduleReminder(id: Int, title: String, triggerAt: Long) {
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
        }
        val pending = PendingIntent.getBroadcast(this, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
    }

    private fun renderReminders() {
        if (!::remindersContainer.isInitialized) return
        remindersContainer.removeAllViews()
        val items = prefs.getStringSet("items", emptySet()).orEmpty().toList()
            .sortedBy { it.substringAfterLast("|").toLongOrNull() ?: Long.MAX_VALUE }
        if (items.isEmpty()) {
            remindersContainer.addView(TextView(this).apply {
                text = "لا توجد تذكيرات بعد. اضغط على «إضافة تذكير» للبدء."
                textSize = 15f
                setTextColor(0xFF58716B.toInt())
                setPadding(dp(16), dp(20), dp(16), dp(20))
                setBackgroundColor(0xFFFFFFFF.toInt())
            })
            return
        }
        items.forEach { entry ->
            val parts = entry.split("|")
            if (parts.size < 3) return@forEach
            val id = parts[0].toIntOrNull() ?: return@forEach
            val title = parts[1]
            val time = parts[2].toLongOrNull() ?: return@forEach
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutDirection = View.LAYOUT_DIRECTION_RTL
                setPadding(dp(14), dp(12), dp(8), dp(12))
                setBackgroundColor(0xFFFFFFFF.toInt())
            }
            row.addView(TextView(this).apply {
                text = "$" + "title\n" + SimpleDateFormat("EEE، hh:mm a", Locale("ar")).format(Date(time))
                textSize = 16f
                setTextColor(0xFF123D35.toInt())
            }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            row.addView(Button(this).apply {
                text = "حذف"
                setOnClickListener {
                    val intent = Intent(this@MainActivity, ReminderReceiver::class.java)
                    val pending = PendingIntent.getBroadcast(this@MainActivity, id, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                    (getSystemService(ALARM_SERVICE) as AlarmManager).cancel(pending)
                    val updated = prefs.getStringSet("items", emptySet()).orEmpty()
                        .filterNot { it.startsWith("\${id}|") }.toSet()
                    prefs.edit().putStringSet("items", updated).apply()
                    renderReminders()
                }
            })
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8) }
            remindersContainer.addView(row, params)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
    }

    private fun matchWrap() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
