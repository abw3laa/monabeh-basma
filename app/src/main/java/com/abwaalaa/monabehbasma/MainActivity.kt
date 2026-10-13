package com.abwaalaa.monabehbasma

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    private val navy = 0xFF0F2537.toInt()
    private val darkBg = 0xFF080E14.toInt()
    private val cardDark = 0xFF121C26.toInt()
    private val teal = 0xFF0D9488.toInt()
    private val canvasLight = 0xFFF7F9FB.toInt()
    private val inkLight = 0xFF191C1E.toInt()
    private val mutedLight = 0xFF64748B.toInt()

    private var page = "home"
    private var isDarkMode = true

    // Auto-update variables
    private var pendingApkFile: File? = null
    private var activeDownloadId = -1L
    private var downloadReceiverRegistered = false

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L) != activeDownloadId) return
            val apkFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "monabeh-basma-update.apk")
            if (apkFile.exists() && apkFile.length() > 0) {
                pendingApkFile = apkFile
                promptInstallUpdate(apkFile)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_settings", MODE_PRIVATE)
        val lang = prefs.getString("app_lang", "ar") ?: "ar"
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        isDarkMode = prefs.getBoolean("dark_mode", true)

        updateSystemBarColors()

        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // Initialize sample schedule if empty
        if (AppointmentStore.all(this).isEmpty()) {
            val cal = Calendar.getInstance().apply {
                set(2026, Calendar.OCTOBER, 12, 10, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val first = Appointment(System.currentTimeMillis(), cal.timeInMillis, 28)
            AppointmentStore.add(this, first)
            ReminderScheduler.schedule(this, first)
        }

        render()

        // Background update check
        checkForUpdatesSilent()
    }

    override fun onResume() {
        super.onResume()
        pendingApkFile?.let { apk ->
            if (Build.VERSION.SDK_INT >= 26 && packageManager.canRequestPackageInstalls()) {
                installApk(apk)
                pendingApkFile = null
            }
        }
    }

    override fun onDestroy() {
        if (downloadReceiverRegistered) {
            try { unregisterReceiver(downloadReceiver) } catch (_: Exception) {}
            downloadReceiverRegistered = false
        }
        super.onDestroy()
    }

    private fun updateSystemBarColors() {
        val bg = if (isDarkMode) darkBg else canvasLight
        window.statusBarColor = bg
        window.navigationBarColor = if (isDarkMode) 0xFF0C141D.toInt() else Color.WHITE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility
            flags = if (!isDarkMode) {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            window.decorView.systemUiVisibility = flags
        }
    }

    private fun changeLanguage(langCode: String) {
        getSharedPreferences("app_settings", MODE_PRIVATE).edit().putString("app_lang", langCode).apply()
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }

    private fun toggleTheme() {
        isDarkMode = !isDarkMode
        getSharedPreferences("app_settings", MODE_PRIVATE).edit().putBoolean("dark_mode", isDarkMode).apply()
        updateSystemBarColors()
        render()
    }

    private fun render() {
        val bg = if (isDarkMode) darkBg else canvasLight
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this).apply { isFillViewport = true }
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        when (page) {
            "home" -> renderHome(body)
            "appointments" -> renderAppointments(body)
            "history" -> renderHistory(body)
            "settings" -> renderSettings(body)
        }

        scroll.addView(body)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(bottomNav())
        setContentView(root)
    }

    // ==========================================
    // 1. HOME SCREEN (الرئيسية)
    // ==========================================
    private fun renderHome(body: LinearLayout) {
        val textPrimary = if (isDarkMode) Color.WHITE else inkLight
        val cardBg = if (isDarkMode) cardDark else Color.WHITE

        // Top bar
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val title = TextView(this).apply {
            text = getString(R.string.nav_home)
            textSize = 22f
            setTextColor(textPrimary)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        headerRow.addView(title, LinearLayout.LayoutParams(0, -2, 1f))

        val avatar = TextView(this).apply {
            text = "👆"
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(8), dp(8), dp(8))
            background = GradientDrawable().apply {
                setColor(if (isDarkMode) 0x2214B8A6.toInt() else 0x220D9488.toInt())
                shape = GradientDrawable.OVAL
            }
            setOnClickListener { showPermissionsDialog() }
        }
        headerRow.addView(avatar)
        body.addView(headerRow)

        space(body, 14)

        // Card 1: Smart Alarm Secured
        card(body, cardBg) {
            val row = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            val shield = TextView(this@MainActivity).apply {
                text = "🛡️"
                textSize = 22f
                setPadding(dp(8), dp(8), dp(8), dp(8))
                background = GradientDrawable().apply { setColor(0x2214B8A6.toInt()); cornerRadius = dp(12).toFloat() }
            }
            val textCol = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(10), 0, dp(10), 0) }
            val t1 = TextView(this@MainActivity).apply {
                text = "✔ " + getString(R.string.home_smart_alarm_secured)
                textSize = 15f
                setTextColor(textPrimary)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            val t2 = TextView(this@MainActivity).apply {
                text = getString(R.string.home_smart_alarm_sub)
                textSize = 12f
                setTextColor(if (isDarkMode) 0xFF94A3B8.toInt() else mutedLight)
            }
            textCol.addView(t1)
            textCol.addView(t2)

            row.addView(textCol, LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(shield)
            addView(row)
        }

        space(body, 12)

        // Card 2: Add New Fingerprint Button
        button(body, "＋ " + getString(R.string.home_add_appointment), if (isDarkMode) 0xFF142433.toInt() else Color.WHITE, textPrimary) {
            createAppointmentDialog()
        }

        space(body, 12)

        // Card 3: Upcoming Appointment Hero Card
        val upcoming = AppointmentStore.all(this).filterNot { it.completed }.minByOrNull { it.dateMillis }
        card(body, cardBg) {
            val topRow = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            val timePill = TextView(this@MainActivity).apply {
                text = "⏰ 10:00 AM"
                textSize = 11f
                setTextColor(if (isDarkMode) 0xFFCBD5E1.toInt() else mutedLight)
                setPadding(dp(10), dp(4), dp(10), dp(4))
                background = GradientDrawable().apply {
                    setColor(if (isDarkMode) 0xFF1E293B.toInt() else 0xFFE2E8F0.toInt())
                    cornerRadius = dp(16).toFloat()
                }
            }
            val statusPill = TextView(this@MainActivity).apply {
                text = "● " + getString(R.string.home_status_soon)
                textSize = 11f
                setTextColor(teal)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(dp(10), dp(4), dp(10), dp(4))
                background = GradientDrawable().apply {
                    setColor(0x2214B8A6.toInt())
                    cornerRadius = dp(16).toFloat()
                }
            }
            topRow.addView(timePill)
            topRow.addView(View(this@MainActivity), LinearLayout.LayoutParams(0, 1, 1f))
            topRow.addView(statusPill)
            addView(topRow)

            space(this, 10)

            val subtitle = TextView(this@MainActivity).apply {
                text = getString(R.string.home_next_appointment_title)
                textSize = 12f
                setTextColor(if (isDarkMode) 0xFF94A3B8.toInt() else mutedLight)
            }
            addView(subtitle)

            val dateText = upcoming?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.dateMillis)) } ?: "12/10/2026"
            val bigDateRow = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            val bigDate = TextView(this@MainActivity).apply {
                text = dateText
                textSize = 32f
                setTextColor(textPrimary)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            val remainingPill = TextView(this@MainActivity).apply {
                text = "⏳ " + getString(R.string.home_days_remaining, 4)
                textSize = 12f
                setTextColor(teal)
                setPadding(dp(10), dp(4), dp(10), dp(4))
                background = GradientDrawable().apply {
                    setColor(0x2214B8A6.toInt())
                    cornerRadius = dp(16).toFloat()
                    setStroke(dp(1), teal)
                }
            }
            bigDateRow.addView(bigDate, LinearLayout.LayoutParams(0, -2, 1f))
            bigDateRow.addView(remainingPill)
            addView(bigDateRow)

            space(this, 12)

            // 3-Day Window Badges
            val daysGrid = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL }
            val day1Box = createDayBox(getString(R.string.home_day_1_label), "12", getString(R.string.home_day_1_badge), teal, Color.WHITE)
            val day2Box = createDayBox(getString(R.string.home_day_2_label), "13", getString(R.string.home_day_2_badge), if (isDarkMode) 0xFF1E293B.toInt() else 0xFFE2E8F0.toInt(), if (isDarkMode) 0xFF94A3B8.toInt() else mutedLight)
            val day3Box = createDayBox(getString(R.string.home_day_3_label), "14", getString(R.string.home_day_3_badge), 0xFFDC2626.toInt(), Color.WHITE)

            daysGrid.addView(day1Box, LinearLayout.LayoutParams(0, -2, 1f))
            spaceHorizontal(daysGrid, 8)
            daysGrid.addView(day2Box, LinearLayout.LayoutParams(0, -2, 1f))
            spaceHorizontal(daysGrid, 8)
            daysGrid.addView(day3Box, LinearLayout.LayoutParams(0, -2, 1f))
            addView(daysGrid)

            space(this, 16)

            // Confirm Attendance Button
            button(this, "👆 " + getString(R.string.home_confirm_biometric), teal, Color.WHITE) {
                if (upcoming != null) {
                    AppointmentStore.replace(this@MainActivity, upcoming.copy(completed = true, completedAt = System.currentTimeMillis()))
                    val nextDate = Calendar.getInstance().apply {
                        timeInMillis = upcoming.dateMillis
                        add(Calendar.DAY_OF_YEAR, upcoming.recurrenceDays)
                    }
                    val nextAppt = Appointment(System.currentTimeMillis() + 1, nextDate.timeInMillis, upcoming.recurrenceDays)
                    AppointmentStore.add(this@MainActivity, nextAppt)
                    ReminderScheduler.schedule(this@MainActivity, nextAppt)
                }
                toast(getString(R.string.home_confirm_success))
                render()
            }
        }
    }

    private fun createDayBox(dayLabel: String, dayNum: String, badge: String, badgeBg: Int, badgeText: Int): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(10), dp(8), dp(10))
            background = GradientDrawable().apply {
                setColor(if (isDarkMode) 0xFF0D1822.toInt() else 0xFFF1F5F9.toInt())
                cornerRadius = dp(14).toFloat()
                setStroke(dp(1), if (isDarkMode) 0xFF1E293B.toInt() else 0xFFCBD5E1.toInt())
            }
            addView(TextView(this@MainActivity).apply { text = dayLabel; textSize = 11f; setTextColor(if (isDarkMode) 0xFF94A3B8.toInt() else mutedLight); gravity = Gravity.CENTER })
            addView(TextView(this@MainActivity).apply { text = dayNum; textSize = 22f; setTextColor(if (isDarkMode) Color.WHITE else inkLight); typeface = android.graphics.Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER })
            val b = TextView(this@MainActivity).apply {
                text = badge; textSize = 10f; setTextColor(badgeText); gravity = Gravity.CENTER
                setPadding(dp(6), dp(2), dp(6), dp(2))
                background = GradientDrawable().apply { setColor(badgeBg); cornerRadius = dp(6).toFloat() }
            }
            addView(b)
        }
    }

    // ==========================================
    // 2. SCHEDULE SCREEN (المواعيد)
    // ==========================================
    private fun renderAppointments(body: LinearLayout) {
        val textPrimary = if (isDarkMode) Color.WHITE else inkLight
        val cardBg = if (isDarkMode) cardDark else Color.WHITE

        val headerRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        headerRow.addView(TextView(this).apply { text = getString(R.string.schedule_title); textSize = 22f; setTextColor(textPrimary); typeface = android.graphics.Typeface.DEFAULT_BOLD }, LinearLayout.LayoutParams(0, -2, 1f))
        val addBtn = Button(this).apply {
            text = "＋ بصمة جديدة"
            textSize = 12f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply { setColor(teal); cornerRadius = dp(12).toFloat() }
            setOnClickListener { createAppointmentDialog() }
        }
        headerRow.addView(addBtn)
        body.addView(headerRow)

        space(body, 14)

        val all = AppointmentStore.all(this).sortedBy { it.dateMillis }
        all.forEachIndexed { index, appt ->
            card(body, cardBg) {
                val row = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
                val numBadge = TextView(this@MainActivity).apply {
                    text = "${index + 1}"
                    textSize = 14f
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    setPadding(dp(10), dp(4), dp(10), dp(4))
                    background = GradientDrawable().apply { setColor(if (index == 0) teal else 0xFF334155.toInt()); cornerRadius = dp(8).toFloat() }
                }
                val dateLabel = TextView(this@MainActivity).apply {
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(appt.dateMillis))
                    textSize = 18f
                    setTextColor(textPrimary)
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setPadding(dp(10), 0, dp(10), 0)
                }
                row.addView(dateLabel, LinearLayout.LayoutParams(0, -2, 1f))
                row.addView(numBadge)
                addView(row)

                val cal = Calendar.getInstance().apply { timeInMillis = appt.dateMillis; add(Calendar.DAY_OF_YEAR, 2) }
                val graceEnd = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
                val graceText = TextView(this@MainActivity).apply {
                    text = getString(R.string.schedule_grace_period) + " " + SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(appt.dateMillis)) + " – " + graceEnd
                    textSize = 12f
                    setTextColor(if (isDarkMode) 0xFF94A3B8.toInt() else mutedLight)
                    setPadding(0, dp(4), 0, 0)
                }
                addView(graceText)
            }
            space(body, 10)
        }
    }

    // ==========================================
    // 3. HISTORY SCREEN (السجل)
    // ==========================================
    private fun renderHistory(body: LinearLayout) {
        val textPrimary = if (isDarkMode) Color.WHITE else inkLight
        val cardBg = if (isDarkMode) cardDark else Color.WHITE

        body.addView(TextView(this).apply { text = getString(R.string.history_title); textSize = 22f; setTextColor(textPrimary); typeface = android.graphics.Typeface.DEFAULT_BOLD })
        space(body, 14)

        // Stat cards
        val statsRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val stat1 = createStatCard(getString(R.string.history_compliance_rate), "١٠٠٪", cardBg, textPrimary)
        val stat2 = createStatCard(getString(R.string.history_completed_cycles), "٤", cardBg, textPrimary)
        statsRow.addView(stat1, LinearLayout.LayoutParams(0, -2, 1f))
        spaceHorizontal(statsRow, 8)
        statsRow.addView(stat2, LinearLayout.LayoutParams(0, -2, 1f))
        body.addView(statsRow)

        space(body, 14)

        val all = AppointmentStore.all(this).filter { it.completed }.sortedByDescending { it.completedAt }
        if (all.isEmpty()) {
            card(body, cardBg) {
                addView(TextView(this@MainActivity).apply { text = "الدورة #12 · 14/09/2026\n✔ " + getString(R.string.history_status_success) + "\nتم إثبات الحضور وتبصيم البوابة الآلية بنجاح."; textSize = 13f; setTextColor(textPrimary) })
            }
        } else {
            all.forEachIndexed { i, appt ->
                card(body, cardBg) {
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(appt.completedAt.takeIf { it > 0 } ?: appt.dateMillis))
                    addView(TextView(this@MainActivity).apply { text = "الدورة #${i + 1} · $date\n✔ " + getString(R.string.history_status_success); textSize = 14f; setTextColor(textPrimary); typeface = android.graphics.Typeface.DEFAULT_BOLD })
                }
                space(body, 8)
            }
        }
    }

    private fun createStatCard(title: String, value: String, bg: Int, textCol: Int): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = GradientDrawable().apply { setColor(bg); cornerRadius = dp(16).toFloat() }
            addView(TextView(this@MainActivity).apply { text = title; textSize = 12f; setTextColor(mutedLight) })
            addView(TextView(this@MainActivity).apply { text = value; textSize = 24f; setTextColor(teal); typeface = android.graphics.Typeface.DEFAULT_BOLD })
        }
    }

    // ==========================================
    // 4. SETTINGS SCREEN (الإعدادات) - Exact matching HTML
    // ==========================================
    private fun renderSettings(body: LinearLayout) {
        val textPrimary = if (isDarkMode) Color.WHITE else inkLight
        val cardBg = if (isDarkMode) cardDark else Color.WHITE

        // Title
        val headerRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val titleIcon = TextView(this).apply {
            text = "⚙️"
            textSize = 20f
            setPadding(dp(8), dp(8), dp(8), dp(8))
            background = GradientDrawable().apply { setColor(0x2214B8A6.toInt()); cornerRadius = dp(12).toFloat() }
        }
        val titleText = TextView(this).apply {
            text = getString(R.string.settings_title)
            textSize = 22f
            setTextColor(textPrimary)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(dp(10), 0, dp(10), 0)
        }
        headerRow.addView(titleIcon)
        headerRow.addView(titleText)
        body.addView(headerRow)

        space(body, 14)

        // 1. Language (اللغة)
        settingsButton(body, "🌐", getString(R.string.settings_lang_title), getString(R.string.settings_lang_sub), "العربية", cardBg, textPrimary) {
            showLanguageDialog()
        }

        // 2. Theme (المظهر)
        settingsButton(body, "🎨", getString(R.string.settings_theme_title), getString(R.string.settings_theme_sub), if (isDarkMode) getString(R.string.settings_theme_dark) else getString(R.string.settings_theme_light), cardBg, textPrimary) {
            toggleTheme()
        }

        // 3. Alarm Settings (إعدادات التنبيه)
        settingsButton(body, "🔔", getString(R.string.settings_alarm_title), getString(R.string.settings_alarm_sub), "مخصص", cardBg, textPrimary) {
            showAlarmSettingsDialog()
        }

        // 4. Permissions (الأذونات والإشعارات)
        settingsButton(body, "🛡️", getString(R.string.settings_perms_title), getString(R.string.settings_perms_sub), "مفعلة", cardBg, textPrimary) {
            showPermissionsDialog()
        }

        // 5. About App (حول التطبيق)
        settingsButton(body, "ℹ️", getString(R.string.settings_about_app_title), getString(R.string.settings_about_app_sub), "v2.4.0", cardBg, textPrimary) {
            showAboutAppDialog()
        }

        // 6. About Developer (حول المطور)
        settingsButton(body, "💻", getString(R.string.settings_about_dev_title), getString(R.string.settings_about_dev_sub), "›", cardBg, textPrimary) {
            showAboutDeveloperDialog()
        }

        // 7. Disclaimer (إخلاء المسؤولية)
        settingsButton(body, "⚖️", getString(R.string.settings_disclaimer_title), getString(R.string.settings_disclaimer_sub), "›", cardBg, textPrimary) {
            showDisclaimerDialog()
        }

        // 8. Test Alarm Trigger
        settingsButton(body, "🚨", "تجربة شاشة الرنين الفورية", "محاكاة رنين البصمة فوق شاشة القفل", "تشغيل", cardBg, textPrimary) {
            startActivity(Intent(this, AlarmActivity::class.java))
        }

        space(body, 18)

        // Docked bar at bottom of settings (Image 18 & screen.png)
        val dockBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = GradientDrawable().apply {
                setColor(cardBg)
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), if (isDarkMode) 0xFF14B8A6.toInt() else 0xFFCBD5E1.toInt())
            }
            setOnClickListener { showPermissionsDialog() }
        }
        dockBar.addView(TextView(this).apply { text = "∨"; textSize = 16f; setTextColor(mutedLight) })
        dockBar.addView(View(this), LinearLayout.LayoutParams(0, 1, 1f))
        dockBar.addView(TextView(this).apply { text = getString(R.string.settings_perms_dock_bar); textSize = 14f; setTextColor(textPrimary); typeface = android.graphics.Typeface.DEFAULT_BOLD })
        dockBar.addView(TextView(this).apply { text = " 🛡️"; textSize = 16f })
        body.addView(dockBar)
    }

    private fun settingsButton(parent: LinearLayout, icon: String, title: String, sub: String, pill: String, bg: Int, textCol: Int, onClick: () -> Unit) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = GradientDrawable().apply { setColor(bg); cornerRadius = dp(16).toFloat() }
            setOnClickListener { onClick() }
        }
        val iconTv = TextView(this).apply {
            text = icon; textSize = 20f
            setPadding(dp(8), dp(8), dp(8), dp(8))
            background = GradientDrawable().apply { setColor(0x2214B8A6.toInt()); cornerRadius = dp(12).toFloat() }
        }
        val textColLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, dp(10), 0)
        }
        val t1 = TextView(this).apply { text = title; textSize = 14f; setTextColor(textCol); typeface = android.graphics.Typeface.DEFAULT_BOLD }
        val t2 = TextView(this).apply { text = sub; textSize = 11f; setTextColor(mutedLight) }
        textColLayout.addView(t1)
        textColLayout.addView(t2)

        val pillTv = TextView(this).apply {
            text = pill; textSize = 11f; setTextColor(teal); typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(dp(10), dp(4), dp(10), dp(4))
            background = GradientDrawable().apply { setColor(0x2214B8A6.toInt()); cornerRadius = dp(12).toFloat() }
        }

        row.addView(iconTv)
        row.addView(textColLayout, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(pillTv)
        parent.addView(row)
        space(parent, 10)
    }

    // ==========================================
    // DIALOGS & ACTIONS
    // ==========================================
    private fun showLanguageDialog() {
        val languages = arrayOf("العربية 🇸🇦", "Türkçe 🇹🇷", "English 🇺🇸")
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_lang_title))
            .setItems(languages) { _, which ->
                when (which) {
                    0 -> changeLanguage("ar")
                    1 -> changeLanguage("tr")
                    2 -> changeLanguage("en")
                }
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun showPermissionsDialog() {
        val overlayGranted = if (Build.VERSION.SDK_INT >= 23) Settings.canDrawOverlays(this) else true
        val notifGranted = if (Build.VERSION.SDK_INT >= 33) checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED else true
        val pm = getSystemService(POWER_SERVICE) as? PowerManager
        val batteryGranted = if (Build.VERSION.SDK_INT >= 23) pm?.isIgnoringBatteryOptimizations(packageName) ?: false else true

        val items = arrayOf(
            getString(R.string.perm_overlay_title) + if (overlayGranted) " (مُفعّل)" else " (تفعيل ➔)",
            getString(R.string.perm_notif_title) + if (notifGranted) " (مُفعّل)" else " (تفعيل ➔)",
            getString(R.string.perm_battery_title) + if (batteryGranted) " (مُفعّل)" else " (تفعيل ➔)"
        )

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_perms_title))
            .setItems(items) { _, which ->
                when (which) {
                    0 -> {
                        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                        } else toast("صلاحية الظهور فوق التطبيقات مفعلة بالفعل")
                    }
                    1 -> {
                        if (Build.VERSION.SDK_INT >= 26) {
                            startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName))
                        }
                    }
                    2 -> {
                        if (Build.VERSION.SDK_INT >= 23) {
                            startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName")))
                        }
                    }
                }
            }
            .setPositiveButton("إغلاق", null)
            .show()
    }

    private fun showAlarmSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_alarm_title))
            .setMessage("• وقت التنبيه الافتراضي: 10:00 AM\n• نغمة التنبيه: صفارة الإنذار ونبض البصمة الرسمي\n• مدة الغفوة: 60 دقيقة\n• الإيقاف التلقائي: بعد 5 دقائق\n• مراحل التنبيه: تنبيه مسبق قبل بيوم، يوم البصمة (إنذار صارم)، اليوم الثاني (تذكير)، اليوم الثالث (الفرصة الأخيرة).")
            .setPositiveButton("حفظ وإغلاق", null)
            .show()
    }

    private fun showAboutAppDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_about_app_title))
            .setMessage("نظام متابعة البصمة الدورية\nالإصدار: v2.4.0 (Build 142)\n\nتطبيق يساعدك على عدم تفويت الالتزام بالبصمات والتواقيع الدورية المفروضة عليك، مع إدارة المهل القانونية والتنبيهات المسبقة بدقة متناهية 100% بدون إنترنت.")
            .setPositiveButton("إغلاق", null)
            .show()
    }

    private fun showAboutDeveloperDialog() {
        val options = arrayOf(
            getString(R.string.dev_whatsapp) + ": +905354883886",
            getString(R.string.dev_telegram) + ": abw3laa",
            getString(R.string.dev_facebook)
        )
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dev_name))
            .setMessage(getString(R.string.dev_role))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openUrl("https://wa.me/905354883886")
                    1 -> openUrl("https://t.me/abw3laa")
                    2 -> openUrl("https://www.facebook.com/share/1EykCQDkjB/")
                }
            }
            .setPositiveButton("إغلاق", null)
            .show()
    }

    private fun showDisclaimerDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_disclaimer_title))
            .setMessage("هذا التطبيق هو أداة تنظيم ومساعد تذكير شخصي مستقل غير رسمي، ولا يمثل أي جهة حكومية أو دائرة رسمية. يُرجى التحقق دائمًا من الإشعارات المباشرة لجهات الاختصاص.")
            .setPositiveButton("أوافق وأتفهم", null)
            .show()
    }

    private fun createAppointmentDialog() {
        val selected = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1); set(Calendar.HOUR_OF_DAY, 10); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }
        DatePickerDialog(this, { _, year, month, day ->
            selected.set(year, month, day, 10, 0, 0)
            val appt = Appointment(System.currentTimeMillis(), selected.timeInMillis, 28)
            AppointmentStore.add(this, appt)
            ReminderScheduler.schedule(this, appt)
            toast("تم حفظ الموعد وجدولة التنبيهات بدقة")
            render()
        }, selected.get(Calendar.YEAR), selected.get(Calendar.MONTH), selected.get(Calendar.DAY_OF_MONTH)).show()
    }

    // ==========================================
    // AUTO-UPDATE ENGINE (In-App Silent Download & Prompt)
    // ==========================================
    private fun checkForUpdatesSilent() {
        Thread {
            try {
                val manifestUrl = "https://raw.githubusercontent.com/abw3laa/monabeh-basma-updtates/main/update.json"
                val conn = (URL(manifestUrl).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 8000
                    readTimeout = 8000
                    setRequestProperty("Cache-Control", "no-cache")
                }
                if (conn.responseCode in 200..299) {
                    val json = JSONObject(conn.inputStream.bufferedReader().use { it.readText() })
                    val latestCode = json.optInt("versionCode", 0)
                    val downloadUrl = json.optString("downloadUrl", "")
                    if (latestCode > BuildConfig.VERSION_CODE && downloadUrl.startsWith("https://")) {
                        downloadUpdateInBackground(downloadUrl)
                    }
                }
                conn.disconnect()
            } catch (_: Exception) {}
        }.start()
    }

    private fun downloadUpdateInBackground(url: String) {
        try {
            val destinationFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "monabeh-basma-update.apk")
            if (destinationFile.exists()) destinationFile.delete()

            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle("تحديث منبّه بصمة")
                .setDescription("جارٍ تنزيل الإصدار الأحدث...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(destinationFile))

            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            activeDownloadId = dm.enqueue(request)

            if (!downloadReceiverRegistered) {
                val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                if (Build.VERSION.SDK_INT >= 33) {
                    registerReceiver(downloadReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
                } else {
                    @Suppress("DEPRECATION")
                    registerReceiver(downloadReceiver, filter)
                }
                downloadReceiverRegistered = true
            }
        } catch (_: Exception) {}
    }

    private fun promptInstallUpdate(apkFile: File) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.update_available_title))
                .setMessage(getString(R.string.update_available_msg))
                .setPositiveButton(getString(R.string.update_install_now)) { _, _ ->
                    checkPermissionAndInstall(apkFile)
                }
                .setNegativeButton(getString(R.string.update_later), null)
                .show()
        }
    }

    private fun checkPermissionAndInstall(apkFile: File) {
        if (Build.VERSION.SDK_INT >= 26 && !packageManager.canRequestPackageInstalls()) {
            pendingApkFile = apkFile
            AlertDialog.Builder(this)
                .setTitle("إذن تثبيت التحديث")
                .setMessage(getString(R.string.update_permission_msg))
                .setPositiveButton("فتح الإعدادات") { _, _ ->
                    startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName")))
                }
                .setNegativeButton("إلغاء", null)
                .show()
        } else {
            installApk(apkFile)
        }
    }

    private fun installApk(apkFile: File) {
        try {
            val apkUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "$packageName.fileprovider", apkFile)
            } else {
                Uri.fromFile(apkFile)
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            toast("تعذر تشغيل مثبت الحزمة: ${e.message}")
        }
    }

    // ==========================================
    // BOTTOM NAVIGATION BAR
    // ==========================================
    private fun bottomNav(): LinearLayout {
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(if (isDarkMode) 0xFF0C141D.toInt() else Color.WHITE)
            setPadding(dp(4), dp(8), dp(4), dp(8))
        }
        val items = listOf(
            "home" to getString(R.string.nav_home),
            "appointments" to getString(R.string.nav_appointments),
            "history" to getString(R.string.nav_history),
            "settings" to getString(R.string.nav_settings)
        )
        items.forEach { (key, label) ->
            val b = TextView(this).apply {
                text = (if (page == key) "●\n" else "") + label
                gravity = Gravity.CENTER
                textSize = 12f
                setTextColor(if (page == key) teal else mutedLight)
                typeface = if (page == key) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
                setPadding(dp(4), dp(4), dp(4), dp(4))
                setOnClickListener { page = key; render() }
            }
            nav.addView(b, LinearLayout.LayoutParams(0, -2, 1f))
        }
        return nav
    }

    // ==========================================
    // UI HELPERS
    // ==========================================
    private fun card(parent: LinearLayout, bg: Int, content: LinearLayout.() -> Unit) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = GradientDrawable().apply {
                setColor(bg)
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), if (isDarkMode) 0xFF1E293B.toInt() else 0xFFE2E8F0.toInt())
            }
            content()
        }
        parent.addView(box, LinearLayout.LayoutParams(-1, -2))
    }

    private fun button(parent: LinearLayout, text: String, bg: Int, textCol: Int, onClick: () -> Unit) {
        val b = Button(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(textCol)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            isAllCaps = false
            background = GradientDrawable().apply {
                setColor(bg)
                cornerRadius = dp(14).toFloat()
                if (bg == Color.WHITE && isDarkMode) setStroke(dp(1), 0xFF1E293B.toInt())
            }
            setOnClickListener { onClick() }
        }
        parent.addView(b, LinearLayout.LayoutParams(-1, dp(50)))
    }

    private fun space(parent: LinearLayout, heightDp: Int) {
        parent.addView(View(this), LinearLayout.LayoutParams(1, dp(heightDp)))
    }

    private fun spaceHorizontal(parent: LinearLayout, widthDp: Int) {
        parent.addView(View(this), LinearLayout.LayoutParams(dp(widthDp), 1))
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    private fun openUrl(url: String) {
        try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } catch (_: Exception) { toast("تعذر فتح الرابط") }
    }
}