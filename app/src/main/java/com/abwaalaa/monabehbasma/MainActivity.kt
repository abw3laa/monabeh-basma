package com.abwaalaa.monabehbasma

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val navy = 0xFF0F2537.toInt()
    private val teal = 0xFF0D9488.toInt()
    private val canvas = 0xFFF7F9FB.toInt()
    private val ink = 0xFF191C1E.toInt()
    private val muted = 0xFF64748B.toInt()
    private var page = "home"
    private var pendingDownloadUrl: String? = null
    private var downloadReceiverRegistered = false
    private var activeDownloadId = -1L

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L) != activeDownloadId) return
            val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val uri = manager.getUriForDownloadedFile(activeDownloadId)
            if (uri == null) {
                toast("تعذر تنزيل ملف التحديث. حاول مرة أخرى.")
                return
            }
            val install = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try { startActivity(install) } catch (_: Exception) { toast("تعذر فتح مثبت الحزمة على هذا الجهاز.") }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = navy
        window.navigationBarColor = navy
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }
        render()
        showWhatsNewOnce()
    }

    override fun onResume() {
        super.onResume()
        if (pendingDownloadUrl != null && Build.VERSION.SDK_INT >= 26 && packageManager.canRequestPackageInstalls()) {
            val url = pendingDownloadUrl
            pendingDownloadUrl = null
            if (url != null) downloadUpdate(url)
        }
    }

    override fun onDestroy() {
        if (downloadReceiverRegistered) {
            try { unregisterReceiver(downloadReceiver) } catch (_: Exception) {}
            downloadReceiverRegistered = false
        }
        super.onDestroy()
    }

    private fun showWhatsNewOnce() {
        val prefs = getSharedPreferences("app_meta", MODE_PRIVATE)
        if (prefs.getInt("seen_whats_new", 0) >= BuildConfig.VERSION_CODE) return
        AlertDialog.Builder(this)
            .setTitle("مرحبًا بك في منبّه البصمة")
            .setMessage("• تنظيم مواعيد البصمة وسجلها.\n• تنبيهات قبل الموعد وخلال نافذة الأيام الثلاثة.\n• تكرار المواعيد كل 28 يومًا أو أسبوعيًا.\n• البحث عن تحديثات التطبيق من الإعدادات.")
            .setPositiveButton("ابدأ الاستخدام") { _, _ -> prefs.edit().putInt("seen_whats_new", BuildConfig.VERSION_CODE).apply() }
            .setCancelable(false)
            .show()
    }

    private fun render() {
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(canvas) }
        val scroll = ScrollView(this)
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }
        when (page) {
            "home" -> renderHome(body)
            "appointments" -> renderAppointments(body)
            "history" -> renderHistory(body)
            "settings" -> renderSettings(body)
            "profile" -> renderProfile(body)
        }
        scroll.addView(body)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(bottomNav())
        setContentView(root)
    }

    private fun renderHome(body: LinearLayout) {
        heading(body, "الرئيسية", "منبّه بصمة · نظام المواعيد")
        val upcoming = AppointmentStore.all(this).filterNot { it.completed }.minByOrNull { it.dateMillis }
        val all = AppointmentStore.all(this)
        val done = all.count { it.completed }
        card(body, navy) {
            label(this, "منبّه البصمة", 24f, 0xFFFFFFFF.toInt(), true)
            label(this, if (upcoming == null) "لا يوجد موعد قادم مسجل" else "موعد البصمة القادم", 14f, 0xFFB9CBD9.toInt())
            space(this, 12)
            label(this, upcoming?.let { dateText(it.dateMillis) } ?: "أضف موعدك الأول للبدء", 22f, 0xFFFFFFFF.toInt(), true)
            if (upcoming != null) {
                val days = ((startOfDay(upcoming.dateMillis) - startOfDay(System.currentTimeMillis())) / 86_400_000L).toInt()
                label(this, when { days < 0 -> "الموعد ضمن نافذة الأيام الثلاثة أو متأخر"; days == 0 -> "الموعد اليوم"; days == 1 -> "متبقٍ يوم واحد"; else -> "متبقٍ $days يومًا" }, 14f, 0xFF89F5E7.toInt())
                space(this, 12)
                button(this, "تأكيد إتمام البصمة", teal) { completeAppointment(upcoming) }
            }
        }
        space(body, 14)
        card(body) {
            label(this, "ملخص المواعيد", 19f, ink, true)
            space(this, 10)
            label(this, "المواعيد المسجلة: ${all.size}", 15f, ink)
            label(this, "البصمات المكتملة: $done", 15f, teal)
            label(this, "المواعيد القادمة: ${all.count { !it.completed && it.dateMillis >= startOfDay(System.currentTimeMillis()) }}", 15f, muted)
        }
        space(body, 14)
        button(body, "＋ إضافة موعد بصمة جديد", teal) { createAppointment() }
        space(body, 8)
        button(body, "فتح سجل المواعيد", navy) { page = "appointments"; render() }
        space(body, 18)
        label(body, "التنبيهات القادمة", 20f, ink, true)
        val upcomingList = all.filterNot { it.completed && it.dateMillis < System.currentTimeMillis() }
            .filter { it.dateMillis >= startOfDay(System.currentTimeMillis()) || !it.completed }
            .take(3)
        if (upcomingList.isEmpty()) label(body, "لا توجد مواعيد قادمة. أضف موعدًا لتفعيل التنبيهات.", 14f, muted)
        upcomingList.forEach { appointmentCard(body, it, showActions = true) }
    }

    private fun renderAppointments(body: LinearLayout) {
        heading(body, "مواعيدي", "إدارة مواعيد البصمة ونافذة الأيام الثلاثة")
        button(body, "＋ جدولة موعد جديد", teal) { createAppointment() }
        space(body, 12)
        val all = AppointmentStore.all(this).filterNot { it.completed }.sortedBy { it.dateMillis }
        if (all.isEmpty()) label(body, "لا توجد مواعيد نشطة. أضف موعدك الأول.", 15f, muted)
        all.forEach { appointmentCard(body, it, true) }
    }

    private fun renderHistory(body: LinearLayout) {
        heading(body, "سجل المواعيد", "المواعيد المكتملة والسابقة")
        val all = AppointmentStore.all(this).sortedByDescending { if (it.completed) it.completedAt else it.dateMillis }
        if (all.isEmpty()) label(body, "سيظهر سجل مواعيدك هنا بعد إضافة المواعيد.", 15f, muted)
        all.forEach { p ->
            card(body) {
                label(this, dateText(p.dateMillis), 17f, ink, true)
                label(this, if (p.completed) "تمت البصمة · ${if (p.completedAt > 0) dateText(p.completedAt) else "مكتملة"}" else if (p.dateMillis < startOfDay(System.currentTimeMillis())) "موعد سابق غير مؤكد" else "قادم", 14f, if (p.completed) teal else muted)
                label(this, "التكرار: ${if (p.recurrenceDays == 7) "أسبوعي" else "كل 28 يومًا"}", 13f, muted)
                space(this, 8)
                if (!p.completed) button(this, "تأكيد إتمام البصمة", teal) { completeAppointment(p) }
                button(this, "حذف من السجل", 0xFFB91C1C.toInt()) {
                    AlertDialog.Builder(this@MainActivity).setMessage("هل تريد حذف هذا الموعد نهائيًا؟")
                        .setNegativeButton("إلغاء", null).setPositiveButton("حذف") { _, _ -> ReminderScheduler.cancel(this@MainActivity,p); AppointmentStore.remove(this@MainActivity,p.id); render() }.show()
                }
            }
            space(body, 10)
        }
    }

    private fun renderSettings(body: LinearLayout) {
        heading(body, "الإعدادات", "التنبيهات والموثوقية")
        card(body) {
            label(this, "مراحل تنبيه البصمة", 18f, ink, true)
            label(this, "• تنبيه مسبق قبل الموعد بيوم.\n• تنبيه في اليوم الأول من نافذة الموعد.\n• متابعة في اليوم الثاني.\n• تنبيه أخير في اليوم الثالث.", 14f, muted)
        }
        space(body, 12)
        button(body, "البحث عن تحديثات", navy) { checkForUpdates(true) }
        space(body, 8)
        button(body, "إعدادات إشعارات أندرويد", teal) {
            try { startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)) }
            catch (_: Exception) { toast("افتح إعدادات التطبيق من إعدادات الهاتف.") }
        }
        space(body, 8)
        button(body, "السماح بتثبيت التحديثات من هذا التطبيق", navy) {
            if (Build.VERSION.SDK_INT >= 26) {
                try { startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName"))) }
                catch (_: Exception) { toast("يمكنك السماح من إعدادات تثبيت التطبيقات غير المعروفة.") }
            } else toast("يمكنك تثبيت التحديث بعد تنزيله عبر مثبت أندرويد.")
        }
        space(body, 8)
        button(body, "عرض ما الجديد؟", teal) {
            getSharedPreferences("app_meta", MODE_PRIVATE).edit().putInt("seen_whats_new", 0).apply()
            showWhatsNewOnce()
        }
        space(body, 8)
        button(body, "حول المطوّر وروابط التواصل", navy) { page = "profile"; render() }
        space(body, 14)
        label(body, "ملاحظة: قد يؤخر أندرويد بعض التنبيهات إذا قيّد النظام عمل التطبيق في الخلفية. اسمح بالإشعارات، وفعّل المنبهات الدقيقة إن طلب النظام ذلك.", 13f, muted)
    }

    private fun renderProfile(body: LinearLayout) {
        heading(body, "حول التطبيق", "منبّه البصمة · الإصدار ${BuildConfig.VERSION_NAME}")
        card(body) {
            label(this, "منبّه البصمة", 23f, navy, true)
            label(this, "تطبيق لمتابعة مواعيد البصمة، تذكير المستخدم بنافذة الموعد، وحفظ سجل المواعيد المكتملة محليًا على الهاتف.", 15f, ink)
            space(this, 8)
            label(this, "المطوّر: ياسر الأحمد", 15f, ink, true)
        }
        space(body, 12)
        button(body, "GitHub · مستودع المشروع", navy) { openUrl("https://github.com/abw3laa/monabeh-basma") }
        space(body, 8)
        button(body, "GitHub · المطوّر", teal) { openUrl("https://github.com/abw3laa") }
        space(body, 8)
        button(body, "Telegram", navy) { openUrl("https://t.me/abw3laa") }
        space(body, 8)
        button(body, "Facebook", teal) { openUrl("https://www.facebook.com/share/1DbyZafE2j/") }
    }

    private fun appointmentCard(parent: LinearLayout, p: Appointment, showActions: Boolean) {
        card(parent) {
            label(this, dateText(p.dateMillis), 17f, ink, true)
            val days = ((startOfDay(p.dateMillis) - startOfDay(System.currentTimeMillis())) / 86_400_000L).toInt()
            label(this, when { days < 0 -> "الموعد بدأ أو مضى · راجع حالة البصمة"; days == 0 -> "اليوم · اليوم الأول"; days == 1 -> "غدًا · تنبيه مسبق"; else -> "بعد $days يومًا" }, 14f, if (days <= 0) 0xFFB91C1C.toInt() else teal)
            label(this, "نافذة الموعد: ${dateText(p.dateMillis)} – ${dateText(p.dateMillis + 2 * 86_400_000L)}", 13f, muted)
            label(this, "التكرار: ${if (p.recurrenceDays == 7) "أسبوعي" else "كل 28 يومًا"}", 13f, muted)
            if (showActions) {
                space(this, 8)
                button(this, "تأكيد إتمام البصمة", teal) { completeAppointment(p) }
                button(this, "حذف الموعد", 0xFFB91C1C.toInt()) {
                    AlertDialog.Builder(this@MainActivity).setMessage("حذف الموعد وإلغاء تنبيهاته؟")
                        .setNegativeButton("إلغاء", null).setPositiveButton("حذف") { _, _ -> ReminderScheduler.cancel(this@MainActivity,p); AppointmentStore.remove(this@MainActivity,p.id); render() }.show()
                }
            }
        }
        space(parent, 10)
    }

    private fun createAppointment() {
        val selected = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1); set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }
        DatePickerDialog(this, { _, year, month, day ->
            selected.set(year, month, day, 9, 0, 0); selected.set(Calendar.MILLISECOND, 0)
            val options = arrayOf("كل 28 يومًا", "أسبوعيًا")
            AlertDialog.Builder(this).setTitle("تكرار الموعد")
                .setItems(options) { _, which ->
                    val recurrence = if (which == 1) 7 else 28
                    val p = Appointment(System.currentTimeMillis(), selected.timeInMillis, recurrence)
                    AppointmentStore.add(this, p)
                    ReminderScheduler.schedule(this, p)
                    toast("تم حفظ الموعد وجدولة التنبيهات")
                    page = "appointments"; render()
                }.setNegativeButton("إلغاء", null).show()
        }, selected.get(Calendar.YEAR), selected.get(Calendar.MONTH), selected.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.minDate = startOfDay(System.currentTimeMillis())
        }.show()
    }

    private fun completeAppointment(p: Appointment) {
        AlertDialog.Builder(this).setTitle("تأكيد إتمام البصمة")
            .setMessage("سيتم تسجيل الموعد كمكتمل وإنشاء الموعد التالي حسب التكرار المحدد.")
            .setNegativeButton("إلغاء", null).setPositiveButton("تأكيد") { _, _ ->
                ReminderScheduler.cancel(this, p)
                AppointmentStore.replace(this, p.copy(completed = true, completedAt = System.currentTimeMillis()))
                val next = Calendar.getInstance().apply { timeInMillis = p.dateMillis; add(Calendar.DAY_OF_YEAR, p.recurrenceDays) }
                while (next.timeInMillis <= System.currentTimeMillis()) next.add(Calendar.DAY_OF_YEAR, p.recurrenceDays)
                val nextAppointment = Appointment(System.currentTimeMillis() + 1, next.timeInMillis, p.recurrenceDays)
                AppointmentStore.add(this, nextAppointment)
                ReminderScheduler.schedule(this, nextAppointment)
                toast("تم تسجيل البصمة وإنشاء الموعد التالي")
                render()
            }.show()
    }

    private fun checkForUpdates(manual: Boolean) {
        toast("جارٍ التحقق من وجود تحديث...")
        Thread {
            var error: String? = null
            var version = ""
            var notes = ""
            var apkUrl: String? = null
            try {
                val connection = (URL("https://api.github.com/repos/abw3laa/monabeh-basma/releases/latest").openConnection() as HttpURLConnection).apply {
                    connectTimeout = 10000; readTimeout = 10000
                    setRequestProperty("Accept", "application/vnd.github+json")
                    setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
                }
                if (connection.responseCode == 404) error = "لا يوجد إصدار منشور بعد. سيتم إتاحة التحديثات بعد نشر أول نسخة Release."
                else if (connection.responseCode !in 200..299) error = "تعذر الاتصال بخدمة الإصدارات (HTTP ${connection.responseCode})."
                else {
                    val data = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                    version = data.optString("tag_name", "")
                    notes = data.optString("body", "لا توجد ملاحظات للإصدار.")
                    val assets = data.optJSONArray("assets")
                    if (assets != null) for (i in 0 until assets.length()) {
                        val asset = assets.optJSONObject(i) ?: continue
                        if (asset.optString("name").endsWith(".apk", true)) { apkUrl = asset.optString("browser_download_url"); break }
                    }
                    val latestCode = Regex("\\d+").find(version)?.value?.toIntOrNull() ?: 0
                    if (latestCode <= BuildConfig.VERSION_CODE) error = "أنت تستخدم الإصدار الحالي (${BuildConfig.VERSION_NAME})."
                    else if (apkUrl == null) error = "عُثر على إصدار $version، لكن ملف APK غير مرفق به بعد."
                }
            } catch (_: Exception) { error = "تعذر الوصول إلى GitHub. تحقق من اتصال الإنترنت ثم أعد المحاولة." }
            runOnUiThread {
                if (error != null) {
                    AlertDialog.Builder(this).setTitle("تحديثات التطبيق").setMessage(error).setPositiveButton("حسنًا", null).show()
                } else {
                    AlertDialog.Builder(this).setTitle("يتوفر تحديث $version")
                        .setMessage(notes.take(1200).ifBlank { "يتوفر إصدار جديد من التطبيق." })
                        .setNegativeButton("لاحقًا", null)
                        .setPositiveButton("تنزيل وتثبيت") { _, _ -> apkUrl?.let { beginUpdate(it) } }.show()
                }
            }
        }.start()
    }

    private fun beginUpdate(url: String) {
        if (Build.VERSION.SDK_INT >= 26 && !packageManager.canRequestPackageInstalls()) {
            pendingDownloadUrl = url
            AlertDialog.Builder(this).setTitle("السماح بتثبيت التحديث")
                .setMessage("سيطلب أندرويد إذنًا للسماح لهذا التطبيق بفتح مثبت الحزمة. لن يتم تثبيت التحديث دون موافقتك في شاشة النظام.")
                .setNegativeButton("إلغاء", null)
                .setPositiveButton("فتح الإعدادات") { _, _ ->
                    try { startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName"))) }
                    catch (_: Exception) { pendingDownloadUrl = null; toast("تعذر فتح إعدادات التثبيت.") }
                }.show()
        } else downloadUpdate(url)
    }

    private fun downloadUpdate(url: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle("تحديث منبّه البصمة")
                .setDescription("تنزيل ملف التحديث من GitHub Releases")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "monabeh-basma-update.apk")
            activeDownloadId = (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
            if (!downloadReceiverRegistered) {
                val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                if (Build.VERSION.SDK_INT >= 33) registerReceiver(downloadReceiver, filter, Context.RECEIVER_NOT_EXPORTED) else @Suppress("DEPRECATION") registerReceiver(downloadReceiver, filter)
                downloadReceiverRegistered = true
            }
            toast("بدأ تنزيل التحديث. سيطلب أندرويد تأكيد التثبيت بعد اكتمال التنزيل.")
        } catch (_: Exception) { toast("تعذر بدء تنزيل التحديث.") }
    }

    private fun bottomNav(): LinearLayout {
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setBackgroundColor(0xFFFFFFFF.toInt())
            setPadding(dp(4), dp(6), dp(4), dp(6))
        }
        listOf("home" to "الرئيسية", "appointments" to "المواعيد", "history" to "السجل", "settings" to "الإعدادات").forEach { (key, title) ->
            val b = TextView(this).apply {
                text = (if (page == key) "●\n" else "") + title
                gravity = Gravity.CENTER
                textSize = 12f
                setTextColor(if (page == key) teal else muted)
                setPadding(dp(3), dp(5), dp(3), dp(5))
                setOnClickListener { page = key; render() }
            }
            nav.addView(b, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        }
        return nav
    }

    private fun heading(parent: LinearLayout, title: String, subtitle: String) {
        label(parent, title, 27f, navy, true)
        label(parent, subtitle, 14f, muted)
        space(parent, 16)
    }

    private fun card(parent: LinearLayout, color: Int = 0xFFFFFFFF.toInt(), content: LinearLayout.() -> Unit) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            setBackgroundColor(color)
            content()
        }
        parent.addView(box, LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT))
    }

    private fun label(parent: LinearLayout, text: String, size: Float, color: Int, bold: Boolean = false) {
        parent.addView(TextView(this).apply {
            this.text = text; textSize = size; setTextColor(color); gravity = Gravity.RIGHT
            if (bold) typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, dp(2), 0, dp(3))
        })
    }

    private fun button(parent: LinearLayout, text: String, color: Int, action: () -> Unit) {
        parent.addView(Button(this).apply {
            this.text = text; setTextColor(0xFFFFFFFF.toInt()); setBackgroundColor(color); isAllCaps = false
            setOnClickListener { action() }
        }, LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT))
    }

    private fun space(parent: LinearLayout, height: Int) {
        parent.addView(View(this), LinearLayout.LayoutParams(1, dp(height)))
    }

    private fun startOfDay(millis: Long) = Calendar.getInstance().apply { timeInMillis = millis; set(Calendar.HOUR_OF_DAY,0); set(Calendar.MINUTE,0); set(Calendar.SECOND,0); set(Calendar.MILLISECOND,0) }.timeInMillis
    private fun dateText(millis: Long) = SimpleDateFormat("d MMMM yyyy", Locale("ar")).format(Date(millis))
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    private fun openUrl(url: String) { try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } catch (_: Exception) { toast("تعذر فتح الرابط.") } }
}