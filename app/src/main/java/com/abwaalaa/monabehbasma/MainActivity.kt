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
import android.graphics.drawable.GradientDrawable
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
        window.statusBarColor = 0xFFF7F9FB.toInt()
        window.navigationBarColor = 0xFFF7F9FB.toInt()
        if (Build.VERSION.SDK_INT >= 23) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
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
        val all = AppointmentStore.all(this)
        val now = startOfDay(System.currentTimeMillis())
        val upcoming = all.filterNot { it.completed }.minByOrNull { it.dateMillis }
        val completed = all.count { it.completed }
        val remaining = all.count { !it.completed && it.dateMillis >= now }
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val monthYear = SimpleDateFormat("d MMMM yyyy", Locale("ar")).format(Date())

        // Header follows Stitch screen _24: compact RTL title row and app identity.
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(dp(2), dp(2), dp(2), dp(12))
        }
        val titleColumn = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_VERTICAL }
        label(titleColumn, "الرئيسية", 25f, ink, true)
        label(titleColumn, "منبّه بصمة · نظام المواعيد", 12f, muted)
        top.addView(titleColumn, LinearLayout.LayoutParams(0, -2, 1f))
        val identity = TextView(this).apply {
            text = "◉"
            textSize = 24f
            gravity = Gravity.CENTER
            setTextColor(0xFFB91C1C.toInt())
            background = GradientDrawable().apply { setColor(0xFFFFFFFF.toInt()); shape = GradientDrawable.OVAL }
        }
        top.addView(identity, LinearLayout.LayoutParams(dp(42), dp(42)))
        body.addView(top)

        // Small system-status panel from the Stitch reference.
        val status = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = GradientDrawable().apply { setColor(0xFFF1F3F5.toInt()); cornerRadius = dp(18).toFloat() }
        }
        val statusText = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_VERTICAL }
        label(statusText, "منبّه بصمة", 20f, ink, true)
        label(statusText, "لا تفوّت موعد بصمتك القانونية", 12f, muted)
        status.addView(statusText, LinearLayout.LayoutParams(0, -2, 1f))
        val active = TextView(this).apply {
            text = "● النظام نشط"
            textSize = 11f
            setTextColor(0xFF006A61.toInt())
            gravity = Gravity.CENTER
            setPadding(dp(10), dp(7), dp(10), dp(7))
            background = GradientDrawable().apply { setColor(0xFF86F2E4.toInt()); cornerRadius = dp(22).toFloat() }
        }
        status.addView(active)
        body.addView(status, LinearLayout.LayoutParams(-1, -2))
        space(body, 16)

        // Dark appointment hero panel, with the same navy/teal hierarchy as Stitch.
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = GradientDrawable().apply {
                setColor(navy); cornerRadius = dp(18).toFloat()
                setStroke(dp(1), 0xFF263E4F.toInt())
            }
        }
        val chips = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; layoutDirection = View.LAYOUT_DIRECTION_RTL }
        val upcomingChip = TextView(this).apply {
            text = if (upcoming == null) "● لا يوجد موعد" else "● موعد اليوم / قادم"
            textSize = 11f; setTextColor(0xFFFFFFFF.toInt()); gravity = Gravity.CENTER
            setPadding(dp(10), dp(7), dp(10), dp(7))
            background = GradientDrawable().apply { setColor(0xFF006A61.toInt()); cornerRadius = dp(24).toFloat() }
        }
        chips.addView(upcomingChip)
        val recurrenceChip = TextView(this).apply {
            text = "يتكرر كل ٢٨ يومًا  ⟳"
            textSize = 11f; setTextColor(0xFFFFFFFF.toInt()); gravity = Gravity.CENTER
            setPadding(dp(10), dp(7), dp(10), dp(7))
            background = GradientDrawable().apply { setColor(0xFF263E4F.toInt()); cornerRadius = dp(24).toFloat() }
        }
        val chipSpace = View(this)
        chips.addView(chipSpace, LinearLayout.LayoutParams(dp(6), 1))
        chips.addView(recurrenceChip)
        hero.addView(chips)
        space(hero, 22)
        label(hero, "موعد البصمة القادم", 13f, 0xFF9FB4C5.toInt())
        space(hero, 4)
        label(hero, upcoming?.let { dateText(it.dateMillis) } ?: "أضف موعدك الأول للبدء", 23f, 0xFFFFFFFF.toInt(), true)
        val days = upcoming?.let { ((startOfDay(it.dateMillis) - now) / 86_400_000L).toInt() }
        label(hero, when {
            upcoming == null -> "أنشئ موعدًا لتفعيل التنبيهات"
            days!! < 0 -> "الموعد بدأ أو مضى · راجع حالة البصمة"
            days == 0 -> "◷ الموعد اليوم"
            days == 1 -> "◷ متبقٍ يوم واحد"
            else -> "◷ متبقٍ $days يومًا"
        }, 14f, 0xFF86F2E4.toInt())
        space(hero, 18)
        val window = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = GradientDrawable().apply { setColor(0xFF263E4F.toInt()); cornerRadius = dp(10).toFloat() }
        }
        label(window, "▦ نافذة الموعد الرسمية (٣ أيام)", 12f, 0xFF86F2E4.toInt(), true)
        label(window, upcoming?.let { "مهلة البصمة: ${dateText(it.dateMillis)} – ${dateText(it.dateMillis + 2 * 86_400_000L)}" } ?: "ستظهر تفاصيل المهلة بعد إضافة موعد", 12f, 0xFFFFFFFF.toInt())
        hero.addView(window)
        space(hero, 16)
        button(hero, "←  تفاصيل الموعد", teal) { page = "appointments"; render() }
        body.addView(hero, LinearLayout.LayoutParams(-1, -2))
        space(body, 18)

        // Summary and progress card.
        card(body) {
            val summaryHeader = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL; layoutDirection = View.LAYOUT_DIRECTION_RTL; gravity = Gravity.CENTER_VERTICAL }
            val heading = TextView(this@MainActivity).apply { text = "البصمات المنجزة  ◈"; textSize = 18f; typeface = android.graphics.Typeface.DEFAULT_BOLD; setTextColor(ink) }
            summaryHeader.addView(heading, LinearLayout.LayoutParams(0, -2, 1f))
            summaryHeader.addView(TextView(this@MainActivity).apply { text = "$completed من أصل ${all.size} بصمة"; textSize = 12f; setTextColor(teal); gravity = Gravity.CENTER })
            addView(summaryHeader)
            space(this, 12)
            val progress = View(this@MainActivity).apply {
                background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(0xFF86F2E4.toInt(), teal)).apply { cornerRadius = dp(8).toFloat() }
            }
            addView(progress, LinearLayout.LayoutParams(-1, dp(8)))
            space(this, 12)
            val stats = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL; layoutDirection = View.LAYOUT_DIRECTION_RTL }
            listOf("المتبقي" to "$remaining موعد", "التنبيه القادم" to (upcoming?.let { dateText(it.dateMillis) } ?: "غير محدد"), "النافذة الحالية" to "اليوم من ٣").forEach { pair ->
                val tile = LinearLayout(this@MainActivity).apply {
                    orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
                    setPadding(dp(4), dp(10), dp(4), dp(10))
                    background = GradientDrawable().apply { setColor(0xFFF1F3F5.toInt()); cornerRadius = dp(10).toFloat() }
                }
                label(tile, pair.first, 11f, muted)
                label(tile, pair.second, 12f, teal, true)
                stats.addView(tile, LinearLayout.LayoutParams(0, -2, 1f).apply { marginEnd = dp(4) })
            }
            addView(stats)
        }
        space(body, 16)

        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; layoutDirection = View.LAYOUT_DIRECTION_RTL }
        val addAction = Button(this).apply {
            text = "⊕ إضافة موعد جديد"; isAllCaps = false; textSize = 14f; setTextColor(0xFFFFFFFF.toInt())
            background = GradientDrawable().apply { setColor(0xFF000F1D.toInt()); cornerRadius = dp(14).toFloat() }
            setOnClickListener { createAppointment() }
        }
        actions.addView(addAction, LinearLayout.LayoutParams(0, dp(54), 1.5f).apply { marginEnd = dp(8) })
        val listAction = Button(this).apply {
            text = "▤ المواعيد"; isAllCaps = false; textSize = 13f; setTextColor(0xFF006A61.toInt())
            background = GradientDrawable().apply { setColor(0xFF86F2E4.toInt()); cornerRadius = dp(14).toFloat() }
            setOnClickListener { page = "appointments"; render() }
        }
        actions.addView(listAction, LinearLayout.LayoutParams(0, dp(54), 1f))
        body.addView(actions)
        space(body, 10)
        val shortcuts = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; layoutDirection = View.LAYOUT_DIRECTION_RTL }
        listOf("سجل البصمات" to "history", "التنبيهات" to "settings", "الإعدادات" to "settings").forEach { (name, target) ->
            val shortcut = TextView(this@MainActivity).apply {
                text = name; textSize = 11f; gravity = Gravity.CENTER
                setTextColor(ink); setPadding(dp(8), dp(9), dp(8), dp(9))
                background = GradientDrawable().apply { setColor(0xFFECEEF0.toInt()); cornerRadius = dp(20).toFloat() }
                setOnClickListener { page = target; render() }
            }
            shortcuts.addView(shortcut, LinearLayout.LayoutParams(0, -2, 1f).apply { marginEnd = dp(4) })
        }
        body.addView(shortcuts)
        space(body, 20)

        val nextHeader = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; layoutDirection = View.LAYOUT_DIRECTION_RTL; gravity = Gravity.CENTER_VERTICAL }
        label(nextHeader, "التنبيهات القادمة  ♙", 20f, ink, true)
        body.addView(nextHeader)
        space(body, 10)
        val list = all.filterNot { it.completed }.sortedBy { it.dateMillis }.take(3)
        if (list.isEmpty()) {
            card(body) { label(this, "لا توجد مواعيد قادمة. أضف موعدك لتفعيل التنبيهات.", 14f, muted) }
        } else list.forEachIndexed { index, appointment ->
            card(body) {
                label(this, when (index) { 0 -> "تنبيه مبكر · موعد بصمة" ; 1 -> "تنبيه اليوم الأول (الرئيسي)" ; else -> "متابعة اليوم الثاني" }, 16f, ink, true)
                label(this, dateText(appointment.dateMillis), 13f, muted)
                label(this, when (index) { 0 -> "نوع التنبيه: قبل الموعد بيوم" ; 1 -> "رنين + اهتزاز مستمر حتى التأكيد" ; else -> "تنبيه متابعة إذا لم تتم البصمة" }, 12f, teal)
                if (index == 0) button(this, "تأكيد إتمام البصمة", teal) { completeAppointment(appointment) }
            }
            space(body, 10)
        }
        card(body) {
            label(this, "عند التوجه للمركز وإتمام البصمة بنجاح، اضغط على «تأكيد الإنجاز» لتحديث السجل وإيقاف التنبيهات اللاحقة.", 12f, muted)
        }
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
        button(body, "إعدادات دقة مواعيد التنبيه", navy) {
            if (Build.VERSION.SDK_INT >= 31) {
                try { startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName"))) }
                catch (_: Exception) { toast("تحقق من أذونات المنبهات في إعدادات أندرويد.") }
            } else toast("هذا الإصدار من أندرويد لا يتطلب هذا الإذن الخاص.")
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
                val manifestUrl = "https://raw.githubusercontent.com/abw3laa/monabeh-basma-updtates/main/update.json"
                val connection = (URL(manifestUrl).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 10000
                    readTimeout = 10000
                    setRequestProperty("Cache-Control", "no-cache")
                }
                if (connection.responseCode !in 200..299) {
                    error = if (connection.responseCode == 404) {
                        "مستودع التحديثات لم يُجهّز بعد أو لم يُنشر ملف update.json."
                    } else {
                        "تعذر الاتصال بمستودع التحديثات (HTTP ${connection.responseCode})."
                    }
                } else {
                    val data = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                    val latestCode = data.optInt("versionCode", 0)
                    version = data.optString("versionName", "")
                    notes = data.optString("releaseNotes", "لا توجد ملاحظات للإصدار.")
                    apkUrl = data.optString("downloadUrl", "").trim().takeIf { it.isNotBlank() }
                    if (latestCode <= 0 || version.isBlank()) {
                        error = "ملف التحديث update.json غير مكتمل أو يحتوي على إصدار غير صالح."
                    } else if (latestCode <= BuildConfig.VERSION_CODE) {
                        error = "أنت تستخدم أحدث إصدار (${BuildConfig.VERSION_NAME})."
                    } else if (apkUrl == null) {
                        error = "يتوفر الإصدار $version، لكن رابط MediaFire لم يُضف بعد إلى مستودع التحديثات."
                    } else if (!apkUrl!!.startsWith("https://")) {
                        error = "رابط التحديث غير آمن أو غير صالح. يجب أن يبدأ بـ https://"
                        apkUrl = null
                    }
                }
                connection.disconnect()
            } catch (_: Exception) {
                error = "تعذر الوصول إلى مستودع التحديثات. تحقق من اتصال الإنترنت ثم أعد المحاولة."
            }
            runOnUiThread {
                if (error != null) {
                    AlertDialog.Builder(this).setTitle("تحديثات التطبيق").setMessage(error)
                        .setPositiveButton("حسنًا", null).show()
                } else {
                    AlertDialog.Builder(this).setTitle("يتوفر تحديث $version")
                        .setMessage(notes.take(1200).ifBlank { "يتوفر إصدار جديد من التطبيق." })
                        .setNegativeButton("لاحقًا", null)
                        .setPositiveButton("تنزيل وتثبيت") { _, _ -> apkUrl?.let { beginUpdate(it) } }.show()
                }
            }
        }.start()
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        fun parts(value: String) = Regex("[0-9]+").findAll(value).mapNotNull { it.value.toIntOrNull() }.toList()
        val a = parts(latest)
        val b = parts(current)
        if (a.isEmpty()) return false
        for (i in 0 until maxOf(a.size, b.size)) {
            val av = a.getOrElse(i) { 0 }
            val bv = b.getOrElse(i) { 0 }
            if (av != bv) return av > bv
        }
        return false
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
                .setDescription("تنزيل تحديث من مستودع منبّه البصمة")
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
            setBackgroundColor(0xFFF7F9FB.toInt())
            setPadding(dp(8), dp(7), dp(8), dp(8))
            elevation = dp(8).toFloat()
        }
        val entries = listOf(
            Triple("home", "⌂", "الرئيسية"),
            Triple("appointments", "▣", "المواعيد"),
            Triple("history", "◴", "السجل"),
            Triple("settings", "⚙", "الإعدادات")
        )
        entries.forEach { (key, icon, title) ->
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(2), dp(4), dp(2), dp(2))
                setOnClickListener { page = key; render() }
            }
            val iconView = TextView(this).apply {
                text = icon
                textSize = 23f
                gravity = Gravity.CENTER
                setTextColor(if (page == key) teal else 0xFF30363B.toInt())
            }
            item.addView(iconView)
            val textView = TextView(this).apply {
                text = title
                textSize = 11f
                gravity = Gravity.CENTER
                setTextColor(if (page == key) teal else ink)
                setPadding(0, dp(2), 0, 0)
            }
            item.addView(textView)
            nav.addView(item, LinearLayout.LayoutParams(0, -2, 1f))
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
            background = GradientDrawable().apply { setColor(color); cornerRadius = dp(20).toFloat(); setStroke(dp(1), if (color == navy) 0xFF19364C.toInt() else 0xFFE2E8F0.toInt()) }
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
            this.text = text; setTextColor(0xFFFFFFFF.toInt()); background = GradientDrawable().apply { setColor(color); cornerRadius = dp(14).toFloat() }; isAllCaps = false
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