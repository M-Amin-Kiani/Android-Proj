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
#### درسنامه 10: رمزنگاری

- ترم بهار 1402-1403 دانشگاه اصفهان 
- نوید شیرمحمدی
---
<!-- slide bg="#2d4726"  dir="rtl" -->
- کانال: t.me/android_ui02

---
<!-- slide bg="#d24726" dir="rtl" -->
## در این درسنامه
- آشنایی مقدماتی با رمزنگاری
- رمز کردن فایل در اندروید

---
<!-- slide dir="rtl" -->
### رمزنگاری در عمل
- داده را به صورت امن ذخیره کنیم (bitwarden)
- داده را به صورت امن روی شبکه بفرستیم (https)
- مطمئن باشیم داده عوض نشده (https)
- مطمئن باشیم یه فرد خاص داده را فرستاده (امضای دیجیتال)
- بدون اشتراک داده با هم دیگه یه تابع را به صورت گروهی حساب کنیم (مثلا یه مدل را آموزش بدیم)

---

![[Pasted image 20240422194100.png]]

- نمی‌خوایم جاسوس متن پیام را بفهمه: رمزنگاری
- نمی‌خوایم جاسوس پیام را عوض کنه: رمزنگاری
- نمی‌خوایم جاسوس بفهمه پیامی فرستادیم: پنهان‌نگاری

---
### اهداف رمزنگاری
- **محرمانگی:** داده برای فرد غیر مجاز قابل درک نباشه
- **صحت:** داده توسط فرد غیر مجاز نتونه عوض بشه (اگه شد، گیرنده بفهمه)
- **احراز هویت فرستنده:** گیرنده مطمئن باشه فرستنده واقعا خودشه
- **عدم تکرار:** دشمن نتونه پیام قبلی را بفرسته و گیرنده را فریب بده
- **گمنامی:** گیرنده نفهمه کدوم فرد از یک گروه از فرستنده‌ها، پیام را فرستاده

---
### ابزارهایی که داریم
- الگوریتم‌های رمزگذاری
- الگوریتم‌های امضا
- توابع هش
- الگوریتم‌های توافق کلید
- الگوریتم‌های احراز هویت

---
<!--slide dir="rtl"-->
### توابع هش
هدف اینه که اگه تغییری در داده رخ داد بفهمیم
- MD5, SHA256, SHA512
- خیلی سریع و سبک
- ویژگی توابع هش رمزنگاری
	- pre-image resistance: از روی هش نمیشه گفت چه رشته‌ای اونو تولید کرده
	- collision resistance: احتمال اینکه دو تا رشته هش یکسان تولید کنند ناچیزه
	- avalanche effect: با یه تغییر کوچیک خروجی خیل تغییر کنه
---
### سوال

با **10 هزار میلیار** سرور که هر کدوم **در هر ثانیه 10 میلیار میلیار هش** میگیرند چند سال طول میکشه یه عدد پیدا کرد که یه هش مشخص را تولید کنه؟

---
<!--slide dir="rtl"-->
### جواب
3.67e39 🤦‍♂

ص.ج.ا:
- وزن منظوره شمسی: تقریبا 2e30 کیلوگرم
- وزن NVIDIA DGX A100: حدود 123 کیلوگرم
- توان محاسباتی: 2.5 میلیارد هش بر ثانیه
- اگه کل منظومه شمسی را GPU کنیم:  2e28 تا GPU

---
### کاربرد هش
- بررسی عدم تغییر فایل
	- virus total
	- امضای برنامه اندروید
	- pipfile.lock
- اثبات کار در بیتکوین
- رمز کردن داده بزرگ و استریم
- OTP
---
### توابع رمز متقارن و نامتقارن
هدف اینه که فقط گیرنده متن با بفهمه

**متقارن:** گیرنده و فرستنده یه کلید دارند
- DES
- AES (Advanced Encryption Standard)
**نامتقارن:** رمزکردن پیام با کلید عمومی گیرنده انجام میشه و رمزگشایی با کلید خصوصی گیرنده
- الجمال
- RSA
- خم بیضوی
---
### رمزنامتقارن
**مساله اول:** اگه یه عدد بزرگ در پیمانه یه عددی اول داشته باشی و بدونی از ضرب دو تا عدد اول بدست اومده چطور اون دو تا عدد اول را پیدا میکنی؟

**مساله دوم:** اگه توان یه عدد (مثلا 100 به توان 331) را در پیمانه یه عدد بزرگ اول داشته باشی، ولی ندونی توان چی بوده (331) چیکار میتونی بکنی؟

---
### RSA
1. First, the receiver chooses two large **prime numbers** 𝑝p and 𝑞q. Their product, 𝑛=𝑝𝑞n=pq, will be half of the public key.
2. The receiver calculates 𝜙(𝑝𝑞)=(𝑝−1)(𝑞−1)ϕ(pq)=(p−1)(q−1) and chooses a number 𝑒e **relatively prime** to 𝜙(𝑝𝑞)ϕ(pq). In practice, 𝑒e is often chosen to be 216+1=65537216+1=65537, though it can be as small as 33 in some cases. 𝑒e will be the other half of the public key.
3. The receiver calculates the **modular inverse** "modular inverse") 𝑑d of 𝑒e modulo 𝜙(𝑛)ϕ(n). In other words, 𝑑𝑒≡1(mod𝜙(𝑛))de≡1(modϕ(n)). 𝑑d is the **private key**.
4. The receiver distributes both parts of the public key: 𝑛n and 𝑒e. 𝑑d is kept secret.

---
### توافق کلید دیفی-هلمن

![[DH.excalidraw.png]]
- کلید خصوصی را میتونی تعیین کنی؟
- کلید عمومی را میتونی تعیین کنی؟
- جاسوس چی میدونه؟
- جاسوس چیکار میتونه بکنه؟

---
### کارای باحال دیگه
- **امضای دیجیتال:** اگه فرستنده با کلید خصوصی داده را رمز کنه و همه کلید عمومی را بدونند چی میشه؟
- **امضای گروهی:** اگه با فرستنده با کلید خصوصی خودش و کلید عمومی بقیه امضا کنه چی میشه؟
- **رمز گروهی:** میشه یه داده را برای یه عده با کلید‌های مختلف رمز کنیم طوری که همه بتونند به داده اصلی برسند؟
- **تسهیم راز:** اگه چند تا نقطه از یه خم درجه n را رمز کنیم و به m نفر بدیم چی میشه؟
- **برونسپاری محاسبات:** میشه محاسبه یه ماتریس را به یه نفر دیگه سپرد بدون اینکه داده یا خروجی را بفهمه؟
- **انتقال مبهم:** میشه به یه نفر دقیقا یکی از دو داده را داد طوری که خودمون نفهمیم کدوم بوده؟

---
### رمزنگاری در کاتلین: AES

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProgrammingClassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val k = generateAESKey()
                    val p = "Hello Android"
                    val c = aesEncrypt(p.toByteArray(), k)
                    val d = aesDecrypt(c, k)

                    Log.i("AES", "k: ${Base64.encodeToString(k.encoded, 0)}")
                    Log.i("AES", "c: ${Base64.encodeToString(c, 0)}")
                    Log.i("AES", "d: ${Base64.encodeToString(d, 0)}")

                    Text(text = d.toString())
                }
            }
        }
    }

    fun generateAESKey(keySize: Int = 256): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(keySize)
        return keyGenerator.generateKey()
    }

    fun aesEncrypt(data: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(ByteArray(16))
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(data)
    }

    fun aesDecrypt(encryptedData: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(ByteArray(16))
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(encryptedData)
    }
}
```

---
### رمزنگاری در کاتلین: SHA-256

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProgrammingClassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val p = "Hello Android"

                    val md5 = MessageDigest.getInstance("MD5")
                    val sha256 = MessageDigest.getInstance("SHA-256")

                    val hMD5 = md5.digest(p.toByteArray())
                    val hSHA256 = sha256.digest(p.toByteArray())

                    Log.i("Hash", "md5: ${BigInteger(1, hMD5).toString(16).padStart(32, '0')}")
                    Log.i("Hash", "sha256: ${BigInteger(1, hSHA256).toString(16).padStart(32, '0')}")
                }
            }
        }
    }
}
```

---
### میخ آخر
- OAuth: دسترسی به منابع را چیطور کنترل میکنیم؟
- OpenIDC: احراز هویت را چطور هندل میکنیم؟
- KeyCloak
- Cerbos
