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
#### درسنامه 12: Content Provider

- ترم بهار 1402-1403 دانشگاه اصفهان 
- نوید شیرمحمدی
---
<!-- slide bg="#2d4726"  dir="rtl" -->
- کانال: t.me/android_ui02

---
<!-- slide bg="#d24726" dir="rtl" -->
## در این درسنامه
- آشنایی با content provider

---
### نیاز به content provider
- دسترسی به محتوای یک برنامه از یه برنامه دیگه
- جدا کردن منطق دسترسی به داده از خود داده
- کنترل بهتر روی داده
---
سوال مهم: مزایای این طراحی چیه؟

![[Pasted image 20240506231734.png]]

---
### تامین کننده محتوا
- چند مثال از یه تامین کننده محتواس
	- دیکشنری کاربر
	- گالری
	- دفتر تلفن
---

<!-- slide dir="rtl" -->
### دسترسی به داده با URI

```
<prefix>://<authority>/<data_type>/<id>
```

- prefix: همیشه content
- authority: اسم تامین کننده محتوا
- data_type: نوع داده‌ای (یا کلاسی) که توسط CP ارائه میشه
- id: شناسه داده

```
content://contacts/people/5
```

---
### مراحل کار با یه تامین کننده محتوا
- ContentResolver 
- ContentProvider
- CursorLoader

---
### مثال دیکشنری کاربر

```kotlin
// Queries the user dictionary and returns results
cursor = contentResolver.query(
        UserDictionary.Words.CONTENT_URI,  // The content URI of the words table
        projection,                        // The columns to return for each row
        selectionClause,                   // Selection criteria
        selectionArgs.toTypedArray(),      // Selection criteria
        sortOrder                          // The sort order for the returned rows
)
```

---
### تعریف درخواست

```kotlin
// A "projection" defines the columns that will be returned for each row
private val mProjection: Array<String> = arrayOf(
        UserDictionary.Words._ID,    // Contract class constant for the _ID column name
        UserDictionary.Words.WORD,   // Contract class constant for the word column name
        UserDictionary.Words.LOCALE  // Contract class constant for the locale column name
)

// Defines a string to contain the selection clause
private var selectionClause: String? = null

// Declares an array to contain selection arguments
private lateinit var selectionArgs: Array<String>
```

---
### ادامه

```kotlin
/*
 * This declares String array to contain the selection arguments.
 */
private lateinit var selectionArgs: Array<String>

// Gets a word from the UI
searchString = searchWord.text.toString()

// Remember to insert code here to check for invalid or malicious input.

// If the word is the empty string, gets everything
selectionArgs = searchString?.takeIf { it.isNotEmpty() }?.let {
    selectionClause = "${UserDictionary.Words.WORD} = ?"
    arrayOf(it)
} ?: run {
    selectionClause = null
    emptyArray<String>()
}

// Does a query against the table and returns a Cursor object
mCursor = contentResolver.query(
        UserDictionary.Words.CONTENT_URI,  // The content URI of the words table
        Projection, 		// The columns to return for each row
        selectionClause,      // Either null, or the word the user entered
        selectionArgs,        // Either empty, or the string the user entered
        sortOrder             // The sort order for the returned rows
)

// Some providers return null if an error occurs, others throw an exception
when (mCursor?.count) {
    null -> {
        /*
         * Insert code here to handle the error. Be sure not to use the cursor!
         * You may want to call android.util.Log.e() to log this error.
         *
         */
    }
    0 -> {
        /*
         * Insert code here to notify the user that the search was unsuccessful. This isn't
         * necessarily an error. You may want to offer the user the option to insert a new
         * row, or re-type the search term.
         */
    }
    else -> {
        // Your code to handle the result returned
    }
}
```

---
### نمایش خروجی

```
// Defines a list of columns to retrieve from the Cursor and load into an output row
val wordListColumns : Array<String> = arrayOf(
        UserDictionary.Words.WORD,      // Contract class constant containing the word column name
        UserDictionary.Words.LOCALE     // Contract class constant containing the locale column name
)

// Defines a list of View IDs that will receive the Cursor columns for each row
val wordListItems = intArrayOf(R.id.dictWord, R.id.locale)

// Creates a new SimpleCursorAdapter
cursorAdapter = SimpleCursorAdapter(
        applicationContext,       // The application's Context object
        R.layout.wordlistrow,     // A layout in XML for one row in the ListView
        mCursor,                  // The result from the query
        wordListColumns,          // A string array of column names in the cursor
        wordListItems,            // An integer array of view IDs in the row layout
        0                         // Flags (usually none are needed)
)
```

---
### ادامه نمایش خروجی

```koltin
mCursor?.apply {
    // Determine the column index of the column named "word"
    val index: Int = getColumnIndex(UserDictionary.Words.WORD)

    /*
     * Moves to the next row in the cursor. Before the first movement in the cursor, the
     * "row pointer" is -1, and if you try to retrieve data at that position you will get an
     * exception.
     */
    while (moveToNext()) {
        // Gets the value from the column.
        newWord = getString(index)

        // Insert code here to process the retrieved word.

        ...

        // end of while loop
    }
}
```

---
### اضافه کردن داده

```kotlin
// Defines a new Uri object that receives the result of the insertion
lateinit var newUri: Uri

// Defines an object to contain the new values to insert
val newValues = ContentValues().apply {
    /*
     * Sets the values of each column and inserts the word. The arguments to the "put"
     * method is "column name" and "value"
     */
    put(UserDictionary.Words.APP_ID, "example.user")
    put(UserDictionary.Words.LOCALE, "en_US")
    put(UserDictionary.Words.WORD, "insert")
    put(UserDictionary.Words.FREQUENCY, "100")

}

newUri = contentResolver.insert(
        UserDictionary.Words.CONTENT_URI,   // the user dictionary content URI
        newValues                          // the values to insert
)
```