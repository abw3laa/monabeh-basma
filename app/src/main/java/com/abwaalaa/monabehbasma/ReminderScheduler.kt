package com.abwaalaa.monabehbasma
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar
object ReminderScheduler {
 fun scheduleAll(context:Context) { AppointmentStore.all(context).filterNot { it.completed }.forEach { schedule(context,it) } }
 fun schedule(context:Context,p:Appointment) {
  val am=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  val base=Calendar.getInstance().apply { timeInMillis=p.dateMillis; set(Calendar.HOUR_OF_DAY,9); set(Calendar.MINUTE,0); set(Calendar.SECOND,0); set(Calendar.MILLISECOND,0) }
  val entries=listOf(-1 to "تذكير مسبق: موعد البصمة غدًا",0 to "اليوم الأول: موعد البصمة",1 to "اليوم الثاني: ما زالت مهلة البصمة مستمرة",2 to "اليوم الثالث: آخر يوم في نافذة الموعد")
  entries.forEachIndexed { index,e ->
   val at=(base.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR,e.first) }.timeInMillis
   val code=((p.id % 1000000L).toInt()*10)+index
   val intent=Intent(context,ReminderReceiver::class.java).apply { putExtra(ReminderReceiver.EXTRA_TITLE,e.second); putExtra(ReminderReceiver.EXTRA_APPOINTMENT_ID,p.id); putExtra(ReminderReceiver.EXTRA_REMINDER_INDEX,index) }
   val pi=PendingIntent.getBroadcast(context,code,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
   if(at>System.currentTimeMillis()) { if(android.os.Build.VERSION.SDK_INT>=31 && am.canScheduleExactAlarms()) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pi) else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pi) } else am.cancel(pi)
  }
 }
 fun cancel(context:Context,p:Appointment) {
  val am=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  repeat(4) { i -> val code=((p.id%1000000L).toInt()*10)+i
   val pi=PendingIntent.getBroadcast(context,code,Intent(context,ReminderReceiver::class.java),PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
   if(pi!=null) am.cancel(pi)
  }
 }
}