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
#### درسنامه8: Retrofit

- ترم بهار 1402-1403 دانشگاه اصفهان 
- نوید شیرمحمدی
---
<!-- slide bg="#2d4726"  dir="rtl" -->
- کانال: t.me/android_ui02

---
<!-- slide bg="#d24726" dir="rtl" -->
## در این درسنامه
- آشنایی با دریافت داده از API با Retrofit

---
<!-- slide dir="rtl" -->
### آشنایی با RESTful API
- یک الگو‌ی معماری نرم‌افزار که امکان دسترسی به داده را به کمک درخواست‌های HTTP فراهم می‌کنه.
- وب سرور درخواست را پردازش میکنه (جواب میده) و اتصال را میبنده.

![[Pasted image 20240420074112.png]]

---
### مثالی از API و Endpoint

![[Pasted image 20240420075856.png]]

---
### عملیات‌های HTTP
- GET: معمولا برای دریافت اطلاعات به کار میره ولی میتونه برای نوشتن اطلاعات هم مورد استفاده قرار بگیره (مثل تلگرام). در پاسخ یک شی یا لیستی از اشیا را بر می‌گردونه
- Post: برای ایجاد داده مورد استفاده قرار می‌گیره و در پاسخ شی ایجاد شده را برمیگردونه
- Put/Patch: برای تغییر داده مورد استفاده قرار می‌گیرند (یکی کامل و یکی جزئی) و در پاسخ شی آپدیت شده را برمیگردونن.
- Delete: برای حذف داده مورد استفاده قرار میگیره و اگه موفقیت آمیز باشه کد 204 برمیگرده

---
### OpenAPI, Swagger و Redocs

![[Pasted image 20240420080522.png|200]]

![[Pasted image 20240420080649.png]]

---
### Retrofit

![[Pasted image 20240420080852.png]]

---
### وابستگی‌ها
- AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

- build.gradle (:app)

```kotlin
// retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
```

---
<!--slide dir="rtl"-->
### افزونه json to kotlin class

![[Pasted image 20240420081332.png]]

---
<!--slide dir="rtl"-->
### مراحل استفاده از Retrofit
- کلاس‌های متناظر با نوع داده API را با افزونه j2k بساز (`models/Poem.kt` , `models/PoemItem.kt`)
- مسیر وب سرور API را یجا نگه دار (`utils/Config.kt`)
- مسیرهای API را به کلاینت معرفی کن (`ApiInterface.kt`)
- یه نمونه از retrofit بساز (`utils/RetrofitInstance.kt`)
- از نمونه‌ای که ساختی استفاده کن و با جوابی که از API میگیری یه کاری کن (`MainActivity.kt`)

---
### نمونه خروجی
https://poetrydb.org/random

```json
[
  {
    "title": "Dream On",
    "author": "Edward Taylor",
    "lines": [
      "Some people go their whole lives",
      "without ever writing a single poem.",
      "Extraordinary people who don't hesitate",
      "to cut somebody's heart or skull open.",
      "They go to baseball games with the greatest of ease.",
      "and play a few rounds of golf as if it were nothing.",
      "These same people stroll into a church",
      "as if that were a natural part of life.",
      "Investing money is second nature to them.",
      "They contribute to political campaigns",
      "that have absolutely no poetry in them",
      "and promise none for the future.",
      "They sit around the dinner table at night",
      "and pretend as though nothing is missing.",
      "Their children get caught shoplifting at the mall",
      "and no one admits that it is poetry they are missing.",
      "The family dog howls all night,",
      "lonely and starving for more poetry in his life.",
      "Why is it so difficult for them to see",
      "that, without poetry, their lives are effluvial.",
      "Sure, they have their banquets, their celebrations,",
      "croquet, fox hunts, their sea shores and sunsets,",
      "their cocktails on the balcony, dog races,",
      "and all that kissing and hugging, and don't",
      "forget the good deeds, the charity work,",
      "nursing the baby squirrels all through the night,",
      "filling the birdfeeders all winter,",
      "helping the stranger change her tire.",
      "Still, there's that disagreeable exhalation",
      "from decaying matter, subtle but everpresent.",
      "They walk around erect like champions.",
      "They are smooth-spoken and witty.",
      "When alone, rare occasion, they stare",
      "into the mirror for hours, bewildered.",
      "There was something they meant to say, but didn't:",
      "\"And if we put the statue of the rhinoceros",
      "next to the tweezers, and walk around the room three times,",
      "learn to yodel, shave our heads, call",
      "our ancestors back from the dead--\"",
      "poetrywise it's still a bust, bankrupt.",
      "You haven't scribbled a syllable of it.",
      "You're a nowhere man misfiring",
      "the very essence of your life, flustering",
      "nothing from nothing and back again.",
      "The hereafter may not last all that long.",
      "Radiant childhood sweetheart,",
      "secret code of everlasting joy and sorrow,",
      "fanciful pen strokes beneath the eyelids:",
      "all day, all night meditation, knot of hope,",
      "kernel of desire, pure ordinariness of life",
      "seeking, through poetry, a benediction",
      "or a bed to lie down on, to connect, reveal,",
      "explore, to imbue meaning on the day's extravagant labor.",
      "And yet it's cruel to expect too much.",
      "It's a rare species of bird",
      "that refuses to be categorized.",
      "Its song is barely audible.",
      "It is like a dragonfly in a dream--",
      "here, then there, then here again,",
      "low-flying amber-wing darting upward",
      "then out of sight.",
      "And the dream has a pain in its heart",
      "the wonders of which are manifold,",
      "or so the story is told."
    ],
    "linecount": "64"
  }
]
```

---
### ساخت کلاس

![[Pasted image 20240420082238.png]]

---

![[Pasted image 20240420082317.png]]

---
### کلاس‌های ایجاد شده
- `models/PoemItem.kt`

```kotlin title:PoemItem.kt
package info.navidlabs.androidprogrammingclass.models  
  
data class PoemItem(  
	// set default values yourself!!
    val author: String = "",  
    val linecount: String = "",  
    val lines: List<String> = List<String>(1){""},  
    val title: String = ""  
)
```

- `model/Poem.kt`

```kotlin title:Poem.kt
package info.navidlabs.androidprogrammingclass.models  
  
class Poem : ArrayList<PoemItem>()
```

---
### تعریف مسیر وب سرور

- `utils/Config.kt`

```kotlin
package info.navidlabs.androidprogrammingclass.utils  
  
object Config {  
    val baseURL = "https://poetrydb.org"  
}
```

---
### Endpoints
- `ApiInterface.kt`

```kotlin title:ApiInterface.kt
package info.navidlabs.androidprogrammingclass.data  
  
import info.navidlabs.androidprogrammingclass.models.Poem
import retrofit2.Response
import retrofit2.http.GET
  
interface ApiInterface {
    @GET("/random")
    suspend fun getRandomPoem(): Response<Poem>
}
```

---
### ساخت نمونه رتروفیت

- `RetrofitInstance.kt`

```kotlin title:RetrofitInstance.kt
package info.navidlabs.androidprogrammingclass.utils

import info.navidlabs.androidprogrammingclass.data.ApiInterface
import info.navidlabs.androidprogrammingclass.utils.Config.baseURL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}
```

---
### استفاده در برنامه

```kotlin title:MainActivity.kt
@Composable
fun PoemCard(poem: PoemItem) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text= poem.title, fontSize = 20.sp)
        Text(text="By ${poem.author}", fontSize = 12.sp)
        Text(text=poem.lines.joinToString(separator = "\n"), fontSize = 16.sp)
    }
}
```

---

```kotlin title:MainActivity.kt
package info.navidlabs.androidprogrammingclass

import android.net.http.HttpException
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.navidlabs.androidprogrammingclass.models.PoemItem
import info.navidlabs.androidprogrammingclass.ui.theme.AndroidProgrammingClassTheme
import info.navidlabs.androidprogrammingclass.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProgrammingClassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var poem by remember { mutableStateOf(PoemItem()) }
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(key1 = true) {
                        scope.launch(Dispatchers.IO) {
                            val response = try {
                                RetrofitInstance.api.getRandomPoem()
                            } catch (e: Exception) {
                                Log.e("Retrofit", "Error::${e.message}")
                                return@launch
                            }

                            if(response.isSuccessful && response.body() != null) {
                                withContext(Dispatchers.Main) {
                                    poem = response.body()!!.first()
                                    Log.i("Retrofit", response.code().toString())
                                    Log.i("Retrofit", response.body().toString())
                                }
                            }
                            else {
                                Log.e("Retrofit", response.code().toString())
                            }
                        }
                    }

                    PoemCard(poem)
                }
            }
        }
    }
}
```

