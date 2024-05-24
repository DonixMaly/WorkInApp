package com.example.workin

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.lang.StringBuilder

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
        val homePage: Button = findViewById(R.id.HomeButton)
        val recordsPage: Button = findViewById(R.id.RecordsButton)
        val timerStart: Button = findViewById(R.id.TimerButton)

        val bigTimer: TextView = findViewById(R.id.TimerView)
        val smallTimer: TextView = findViewById(R.id.TimerView2)

        var timerSeconds: Int
        var timerMinutes: Int
        var timerString: String
        val timerStringBuilder = StringBuilder()

        var isBreakTime = false
        var countdown: Long = if(isBreakTime == false){
            1500000
        }
        else{
            300000
        }
        var isTimerRunning = false

        fun SetTimer(){
            timerSeconds = countdown.toInt() / 1000
            timerMinutes = timerSeconds / 60
            timerSeconds = timerSeconds - (timerMinutes * 60)
            timerStringBuilder.append(timerMinutes).append(":").append(timerSeconds)
            timerString = timerStringBuilder.toString()
            timerStringBuilder.clear()
            bigTimer.text = timerString
            smallTimer.text = timerString
        }

        fun SetHomeView(){
            smallTimer.visibility = View.GONE
            bigTimer.visibility = View.VISIBLE
            timerStart.visibility = View.VISIBLE
        }
        fun SetRecordsView(){
            bigTimer.visibility = View.GONE
            smallTimer.visibility = View.VISIBLE
            timerStart.visibility = View.GONE
        }
        fun ifBreakTime(){
            if(isBreakTime == false){
                countdown = 300000
                SetTimer()
                isBreakTime = true
            }
            else{
                countdown = 1500000
                SetTimer()
                isBreakTime = false
            }
        }
        SetHomeView()
        SetTimer()

        val Timer = object: CountDownTimer(countdown, 1000){
            override fun onTick(millisUntilFinished: Long) {
                SetTimer()
                countdown = millisUntilFinished
            }

            override fun onFinish() {
                ifBreakTime()
                isTimerRunning = false
                timerStart.setText("START")
            }
        }

        homePage.setOnClickListener({
            SetHomeView()
        })
        recordsPage.setOnClickListener({
            SetRecordsView()
        })
        timerStart.setOnClickListener({
            if(isTimerRunning == false){
                isTimerRunning = true
                timerStart.setText("STOP")
                Timer.start()
            }
            else{
                isTimerRunning = false
                timerStart.setText("START")
                Timer.cancel()
                ifBreakTime()
                SetTimer()
            }
        })


    }
}