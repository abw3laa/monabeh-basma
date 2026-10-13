package com.abwaalaa.monabehbasma
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Appointment(val id: Long, val dateMillis: Long, val recurrenceDays: Int = 28, val completed: Boolean = false, val completedAt: Long = 0L)
object AppointmentStore {
 private const val PREFS = "fingerprint_appointments"
 private const val KEY = "items"
 fun all(context: Context): List<Appointment> {
  val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
  return try { val a=JSONArray(raw); (0 until a.length()).mapNotNull { i -> val o=a.optJSONObject(i) ?: return@mapNotNull null
   Appointment(o.optLong("id"),o.optLong("dateMillis"),o.optInt("recurrenceDays",28),o.optBoolean("completed",false),o.optLong("completedAt",0L))
  }.sortedBy { it.dateMillis } } catch (_:Exception) { emptyList() }
 }
 fun save(context: Context, items: List<Appointment>) { val a=JSONArray(); items.forEach { p -> a.put(JSONObject().apply {
  put("id",p.id); put("dateMillis",p.dateMillis); put("recurrenceDays",p.recurrenceDays); put("completed",p.completed); put("completedAt",p.completedAt)
 }) }; context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putString(KEY,a.toString()).apply() }
 fun add(context: Context, p: Appointment) = save(context, all(context)+p)
 fun replace(context: Context, p: Appointment) = save(context, all(context).map { if(it.id==p.id) p else it })
 fun remove(context: Context,id:Long) = save(context,all(context).filterNot { it.id==id })
}