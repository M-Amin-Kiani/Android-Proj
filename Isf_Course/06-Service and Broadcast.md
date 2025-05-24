---
tags:
  - Android
  - 1402_2
enableOverview: "true"
css:
  - css/shabnam.css
theme: black
---

<!-- slide dir="rtl" -->

![[University-of-Isfahan.png|200]]

## برنامه نویسی دستگاه‌های سیار
#### درسنامه 6: سرویس و برادکست

- ترم بهار 1402-1403 دانشگاه اصفهان
- نوید شیرمحمدی
---
<!-- slide bg="#2d4726"  dir="rtl" -->
- کانال: t.me/android_ui02

---
<!-- slide bg="#d24726" dir="rtl" -->
## در این درسنامه
- آشنایی با broadcast
- آشنایی با foreground service

---
<!-- slide dir="rtl" -->
### برادکست
- مشابه intent، قصد داریم به چند نرم‌افزار بگیم کاری را انجام بدن.
- رویدادهایی که در کل سیستم پخش میشن و برنامه‌ها می‌تونند مصرفشون کنند (وقتی رخ دادند یه کاری انجام بدن) (broadcast receiver).
- این رویدادها ممکنه توسط خود سیستم اندروید ایجاد بشن، ممکن هم هست برنامه‌ها ایجادشون کنند.

---
<!-- slide dir="rtl" -->
### مثال‌
- رویداد boot completed (توسط اندروید) وقتی فرآیند بوت اندوید کامل میشه.
	- برنامه مصرف کننده ممکنه فرآیند سینک داده یا بررسی یه API را شروع کنه. 
- رویداد phone call (باز هم توسط اندروید)
	- برنامه مصرف کننده ممکنه یه نرم‌افزار پخش ویدئو باشه و پخش ویدئو را متوقف کنه.
- رویداد airplane mode (باز هم توسط اندروید)

---
### دریافت برادکست

```kotlin title:AirPlaneModeReceiver
package info.navidlabs.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

class AirPlaneModeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            val airPlaneModeOn = Settings.Global.getInt(
                context?.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON
            ) != 0

            Log.i("Broadcast Receiver", "airplane mode: $airPlaneModeOn")
        }
    }
}
```

---
### رجیستر و آنرجیستر برادکست

```kotlin
class MainActivity : ComponentActivity() {
    private val airPlaneModeReceiver = AirPlaneModeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(
            airPlaneModeReceiver,
            IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        )

        setContent {
            DriveSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text("AirPlane Broadcast Receiver")
                }
            }
        }
    }
    
	override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(airPlaneModeReceiver)
    }
}
```

---
### بررسی لاگ

![[Pasted image 20240331173551.png]]

---
<!-- slide dir="rtl" -->
### اگه برنامه بسته شد چی؟
- روش بالا وقتی کار میکنه که برنامه فعال باشه

### Static Receiver 📶🛌

---
<!-- slide dir="rtl" -->
### ریسیور استاتیک
- مطالعه بیشتر: https://developer.android.com/develop/background-work/background-tasks/broadcasts/broadcast-exceptions
- مثال دریافت پیامک: https://proandroiddev.com/google-sms-retriever-api-in-jetpack-compose-ecf9c1e4c607

---
### محدودیت‌های ریسور استاتیک
- مصرف باتری
- محدودیت گزینه‌ها (توسط اندروید)
	- استثنا: وقتی فقط به رویدادهای یک برنامه میخوایم گوش بدیم
---
### ارسال برادکست

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DriveSyncTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        sendBroadcast(
                            Intent("TEST_ACTION")
                        )
                    }) {
                        Text(text="Send Broadcast")
                    }
                }
            }
        }
    }
}
```

---
### ریسیور

- رجیستر یادتون نره
```kotlin
class TestActionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == "TEST_ACTION") {
            Log.i("Test Receiver", "received custom broadcast")
        }
    }
}
```

---

```kotlin
val filter = IntentFilter("TEST_ACTION")
registerReceiver(customBCR, filter)
```

```kotlin
val filter = IntentFilter("TEST_ACTION")  
ContextCompat.registerReceiver(  
    this,  
    customBCR,  
    filter,  
    ContextCompat.RECEIVER_NOT_EXPORTED  
)
```

---
<!-- slide bg="#d24726" dir="rtl" -->
# Foreground Service

---
<!-- slide dir="rtl" -->
### سرویس

- زمانی که یک تسک بلند مدت (مثل دانلود یا پخش ویدئو) داریم.
- امکان مینیمایز کردن برنامه (بسته شدن اکتیویتی) وجود داره. 

---
<!-- slide dir="rtl" -->
- انواع:
	- سرویس: 
		- کاربر نمیبینه که سرویس داره اجرا میشه.
		- مدت اجرا خیلی طولانی نیست (ممکنه سرویس توسط اندروید بسته بشه).
		- جلسه بعد workmanager
	- سرویس پیش‌زمینه: 
		- کاربر میبینه که سرویس داره اجرا میشه.
		- تا زمانی که نوتیفیکیشن هست، سرویس هم هست.

---
<!-- slide dir="rtl" -->
### توابع onBind و onStartCommand
- onStartCommand
	- کلاینت برای اجرای سرویس از این متود استفاده میکنه
	- برای هندل کارهایی که یک کلاینت قصد داره به کمک سرویس انجام بده
- onBind
	- چند کلاینت قصد استفاده از سرویس را دارند (دریافت داده از API و سنسور)
	- callback
	- کانال ارتباطی

---
### تعریف سرویس
- `ForegroundService.kt`

```kotlin
package info.navidlabs.drivesync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ForegroundService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> start();
            Actions.STOP.toString() -> stop();
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "service_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Service is Running")
            .setContentText("Here is the notification content")
            .build()
        startForeground(1, notification)
    }

    private fun stop() {
        stopSelf()
    }

    enum class Actions {
        START, STOP
    }
}
```

---
<!-- slide dir="rtl" -->
- متود IBinder: امکان استفاده چند کامپوننت از سرویس را فراهم میکنه
- متود onStartCommand: وقتی سرویس Intent دریافت کرد اجرا میشه
- متود start: یه نوتیفیکیشن با شناسه 1 روی کانال دلخواه `service_channel` نمایش میده.
	- کانال را باید خودمون ایجاد کنیم (اسلاید بعد)
---
### تعریف کانال
- از کلاس اپلیکیشن استفاده میکنیم و کانال را تعریف میکنیم
- `MyApplication.kt`

```kotlin
package info.navidlabs.drivesync

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "service_channel",
                "My Notification",
                    NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}
```

---
### رجیستر سرویس
- AndroidManifest.xml

```xml title:AndroidManifest.xml
...
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />  
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<application
	android:name=".MyApplication"
...
        </activity>
        <service android:name=".ForegroundService" />
    </application>
</manifest>
```

---
### اجرای سرویس
- باید اکتیویتی یه اینتنت بفرسته برای شروع سرویس

```kotlin
package info.navidlabs.drivesync

import android.Manifest  
import android.content.Intent  
import android.os.Build  
import android.os.Bundle  
import androidx.activity.ComponentActivity  
import androidx.activity.compose.setContent  
import androidx.compose.foundation.layout.Arrangement  
import androidx.compose.foundation.layout.Column  
import androidx.compose.foundation.layout.fillMaxSize  
import androidx.compose.material3.Button  
import androidx.compose.material3.ExperimentalMaterial3Api  
import androidx.compose.material3.Text  
import androidx.compose.ui.Alignment  
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat  
import info.navidlabs.drivesync.ui.theme.DriveSyncTheme  


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        
        setContent {
            DriveSyncTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        Intent(applicationContext, ForegroundService::class.java).also {
                            it.action = ForegroundService.Actions.START.toString()
                            startService(it)
                        }
                    }) {
                        Text(text="Star Service")
                    }

                    Button(onClick = {
                        Intent(applicationContext, ForegroundService::class.java).also {
                            it.action = ForegroundService.Actions.STOP.toString()
                            startService(it)
                        }
                    }) {
                        Text(text="Stop Service")
                    }
                }
            }
        }
    }
}
```

---

![[Pasted image 20240331203327.png|300]]

---

![[Pasted image 20240331203449.png|300]]

---
<!-- slide dir="rtl" -->
- میتونی برنامه را ببندی و ببینی که نوتیفیکیشن سرویس هنوز داره اجرا میشه
- میتونی دکمه stop service را بزنی تا سرویس متوقف بشه و نوتیفیکیشن بره
- میتونی با تنظیم `android:foregroundServiceType` در فایل `AndroidManifest` نوع سرویستو تعیین کنی.

---

![[Pasted image 20240331202904.png]]

---
### onBind example

```kotlin
class MyMessageService : Service() {

    private val messageListeners = mutableListOf<MessageListener>()

    interface MessageListener {
        fun onMessageReceived(message: String)
    }

    inner class MyBinder : Binder() {
        fun addListener(listener: MessageListener) {
            messageListeners.add(listener)
        }

        fun removeListener(listener: MessageListener) {
            messageListeners.remove(listener)
        }

        fun sendMessage(message: String) {
            // Simulate receiving a message (replace with your logic)
            for (listener in messageListeners) {
                listener.onMessageReceived(message)
            }
        }
    }

    private val binder = MyBinder()

    @Nullable
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}
```

---

```kotlin
class MainActivity : AppCompatActivity(), MyMessageService.MessageListener {

    private var myService: MyMessageService.MyBinder? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            myService = service as MyMessageService.MyBinder
            myService?.addListener(this@MainActivity)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            myService?.removeListener(this@MainActivity)
            myService = null
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, MyMessageService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onMessageReceived(message: String) {
        // Handle received message (update UI, etc.)
        // ...
    }

    fun sendMessage(message: String) {
        if (isBound) {
            myService?.sendMessage(message)
        }
    }
}
```