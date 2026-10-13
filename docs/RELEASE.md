# إصدار APK موقّع

يستخدم المشروع مفتاح توقيع ثابتًا لكل إصدار حتى يتمكن Android من تثبيت التحديث فوق النسخة السابقة. لا ترفع ملف keystore إلى المستودع.

## إعداد GitHub Actions

من إعدادات المستودع افتح **Settings → Secrets and variables → Actions → New repository secret**، ثم أضف الأسرار التالية:

- `ANDROID_KEYSTORE_BASE64`: محتوى ملف keystore مشفّرًا بصيغة Base64 (سطر واحد).
- `ANDROID_STORE_PASSWORD`: كلمة مرور مخزن المفاتيح.
- `ANDROID_KEY_ALIAS`: اسم المفتاح داخل keystore.
- `ANDROID_KEY_PASSWORD`: كلمة مرور المفتاح.

احتفظ بنسخة احتياطية آمنة من keystore وكلمات المرور. فقدان المفتاح يعني عدم القدرة على إصدار تحديثات قابلة للتثبيت فوق النسخة الموقعة به.

## إنشاء الإصدار

1. حدّث `versionCode` و`versionName` في `app/build.gradle.kts`.
2. أنشئ tag مطابقًا للإصدار مثل `v1.0.1` وادفعه إلى GitHub، أو شغّل workflow يدويًا بعد إعداد الأسرار.
3. ينشئ workflow ملف APK موقّعًا ويرفعه كـ artifact، وينشئ GitHub Release عند تشغيله بواسطة tag.
4. ارفع ملف APK إلى MediaFire، واحصل على رابط تنزيل مباشر HTTPS.
5. حدّث `update.json` في مستودع `abw3laa/monabeh-basma-updtates` بقيم `versionCode` و`versionName` و`downloadUrl` و`releaseNotes`.

لا يتم تثبيت التحديث تلقائيًا؛ Android يعرض شاشة التثبيت ويحتاج موافقة المستخدم.
