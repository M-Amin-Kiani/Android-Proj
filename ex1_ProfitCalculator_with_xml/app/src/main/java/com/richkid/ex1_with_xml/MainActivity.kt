package com.richkid.ex1_with_xml

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textbuttonMohasebe = findViewById<Button>(R.id.buttonMohasebe)
        textbuttonMohasebe.setOnClickListener(){

            //findViewById
            val textMablagh = findViewById<EditText>(R.id.editTextMablagh)
            val mablagh = textMablagh.text.toString().toInt()

            var mresult = 0 // result

            val textradiosod10 = findViewById<RadioButton>(R.id.radiosod10)
            val textradiosod12 = findViewById<RadioButton>(R.id.radiosod12)

            // validate
            if (textMablagh.text.length == 0){
                Toast.makeText(this, "لطفا مبلغ را مشخص کنید!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (textradiosod10.isChecked == false && textradiosod12.isChecked == false ){
                Toast.makeText(this, "لطفا مدت سرمایه گذاری را مشخص کنید!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // calculate
            if (textradiosod10.isChecked) mresult = mablagh * 10 / 100
            else if (textradiosod12.isChecked) mresult = (mablagh * 12 / 100) * 3

            Toast.makeText(this, mresult.toString(), Toast.LENGTH_LONG).show()  // print
        }

    }
}