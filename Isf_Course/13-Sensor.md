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
#### درسنامه 13: Sensor

- ترم بهار 1402-1403 دانشگاه اصفهان 
- نوید شیرمحمدی
---
<!-- slide bg="#2d4726"  dir="rtl" -->
- کانال: t.me/android_ui02

---
<!-- slide bg="#d24726" dir="rtl" -->
## در این درسنامه
- آشنایی با سنسور و انواع آن
- کار با سنسورهای شتاب و میدان مغناطیسی

---
<!-- slide dir="rtl"-->
### سنسور چرا؟
- در بیشتر گوشی‌ها دستگاه‌های اندازیه‌گیری مختلف هستن

![[Pasted image 20240510201638.png]]

---
### کاربردها
- حرکات و موقعیت گوشی در سه بعد را تشخیص بدیم
	- برنامه‌های ورزشی، بازی، جعبه ابزارهای اندازه‌گیر
- ثبت بر دما و رطوبت

![[Pasted image 20240510202106.png]]

---
<!--slide dir="rtl"-->
### انواع سنسور‌های استاندارد
- سنسور‌های حرکت: شتاب و چرخش
	- accelerometers, gravity sensor, gyroscopes و rotational vector sensor
- سنسورهای محیطی: دما، رطوبت، فشار و روشنایی
	- barometers, photometers و thermometers
- سنسور موقعیت: وضعیت دستگاه در فضا
	- orientation sensor و magnetometers
---
### فریم‌ورک سنسور
خود اندروید ارائه میده (پیش‌نیاز نداریم 🙂)
- تشخیص سنسورهای موجود
- تعیین قابلیت‌های سنسور
- دستیابی به داده‌های سنسور
- رجیستر و آنرجیستر لیستنر
---
### بعضی سنسورها و نوعشون

![[Pasted image 20240510202827.png]]

---
### کی چه سنسوری اومد

![[Pasted image 20240510202909.png]]
---
### لیست کردن سنسورها

```kotlin
private lateinit var sensorManager: SensorManager

sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
```

---
### دیدن یه سنسور خاص

```kotlin
private lateinit var sensorManager: SensorManager
private var mSensor: Sensor? = null

...

sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
    val gravSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_GRAVITY)
    // Use the version 3 gravity sensor.
    mSensor = gravSensors.firstOrNull { it.vendor.contains("Google LLC") && it.version == 3 }
}
if (mSensor == null) {
    // Use the accelerometer.
    mSensor = if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    } else {
        // Sorry, there are no accelerometers on your device.
        // You can't play this game.
        null
    }
}
```

---
### نمونه کد

```kotlin
package info.navidlabs.androidprogrammingclass  
  
import android.content.Context  
import android.hardware.Sensor  
import android.hardware.SensorEvent  
import android.hardware.SensorEventListener  
import android.hardware.SensorManager  
import android.os.Bundle  
import android.util.Log  
import androidx.activity.ComponentActivity  
import androidx.activity.compose.setContent  
import info.navidlabs.androidprogrammingclass.ui.theme.AndroidProgrammingClassTheme  
  
class MainActivity : ComponentActivity(), SensorEventListener {  
    private lateinit var sensorManager: SensorManager  
    private val accelerometerReading = FloatArray(3)  
    private val magnetometerReading = FloatArray(3)  
      
    override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)  
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager  
  
        setContent {  
            AndroidProgrammingClassTheme {  
  
            }  
        }  
    }  
  
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {  
        // Do something here if sensor accuracy changes.  
    }  
  
    override fun onResume() {  
        super.onResume()  
  
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->  
            sensorManager.registerListener(  
                this,  
                accelerometer,  
                SensorManager.SENSOR_DELAY_NORMAL,  
                SensorManager.SENSOR_DELAY_UI  
            )  
        }  
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->  
            sensorManager.registerListener(  
                this,  
                magneticField,  
                SensorManager.SENSOR_DELAY_NORMAL,  
                SensorManager.SENSOR_DELAY_UI  
            )  
        }  
    }  
  
    override fun onPause() {  
        super.onPause()  
        sensorManager.unregisterListener(this)  
    }  
  
    override fun onSensorChanged(event: SensorEvent) {  
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {  
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)  
            Log.i("Sensor:ACCELEROMETER", "${event.values[0]}")  
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {  
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)  
            Log.i("Sensor:MAGNETIC_FIELD", "${event.values[0]}")  
        }  
    }  
}
```
