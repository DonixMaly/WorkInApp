package com.example.workin

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.lang.StringBuilder
import java.util.Calendar

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
        val currentDayOfWeek: TextView = findViewById(R.id.dayOfWeek)

        val medalMonday: ImageView = findViewById(R.id.medalMonday)

        val mondaysRecord: TextView = findViewById(R.id.RecordSessionsMonday)

        var timerSeconds: Int
        var timerMinutes: Int

        var mostWorkOnMonday = 0

        var timerString: String
        val timerStringBuilder = StringBuilder()

        var isBreakTime = false
        var countdown: Long = 1500000
        var isTimerRunning = false

        val calendar = Calendar.getInstance()

        var currentDay = calendar.get(Calendar.DAY_OF_WEEK)

        var dayOfTheWeek = when(currentDay){
            2 -> "Poniedziałek"
            3 -> "Wtorek"
            4 -> "Środa"
            5 -> "Czwartek"
            6 -> "Piątek"
            0 -> "Sobota"
            1 -> "Niedziela"
            else -> "Brak daty"
        }
        currentDayOfWeek.text = dayOfTheWeek

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
            currentDayOfWeek.visibility = View.GONE
        }
        fun SetRecordsView(){
            bigTimer.visibility = View.GONE
            smallTimer.visibility = View.VISIBLE
            timerStart.visibility = View.GONE
            currentDayOfWeek.visibility = View.VISIBLE
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

        val workTimer = object: CountDownTimer(1500/*000*/, 1000){
            override fun onTick(millisUntilFinished: Long) {
                SetTimer()
                countdown = millisUntilFinished
            }
            override fun onFinish() {
                ifBreakTime()
                isTimerRunning = false
                timerStart.setText("START")
                mostWorkOnMonday += 1
                mondaysRecord.text = mostWorkOnMonday.toString()
                if(mostWorkOnMonday >= 4){
                    medalMonday.setImageResource(R.drawable.screenshot_from_2024_03_17_12_54_47)
                }
            }
        }
        val breakTimer = object: CountDownTimer(300000, 1000){
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
        fun startTimer(){
            if(isBreakTime == false){
                workTimer.start()
            }
            else{
                breakTimer.start()
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
                startTimer()
            }
            else{
                isTimerRunning = false
                timerStart.setText("START")
                workTimer.cancel()
                breakTimer.cancel()
                ifBreakTime()
                SetTimer()
            }
        })
    }
}