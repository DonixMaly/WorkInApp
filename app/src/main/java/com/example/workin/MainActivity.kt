package com.example.workin

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
        //pobranie ID każdego potrzebnego elementu
        val main: ConstraintLayout = findViewById(R.id.main)

        val homePage: Button = findViewById(R.id.HomeButton)
        val recordsPage: Button = findViewById(R.id.RecordsButton)
        val timerStart: Button = findViewById(R.id.TimerButton)

        val bigTimer: TextView = findViewById(R.id.TimerView)
        val smallTimer: TextView = findViewById(R.id.TimerView2)
        val currentDayOfWeek: TextView = findViewById(R.id.dayOfWeek)

        val medalMonday: ImageView = findViewById(R.id.medalMonday)
        val medalTuesday: ImageView = findViewById(R.id.medalTuesday)
        val medalWednesday: ImageView = findViewById(R.id.medalWednesday)
        val medalThursday: ImageView = findViewById(R.id.medalThursday)
        val medalFriday: ImageView = findViewById(R.id.medalFriday)
        val medalSaturday: ImageView = findViewById(R.id.medalSaturday)
        val medalSunday: ImageView  = findViewById(R.id.medalSunday)

        val mondayRecord: TextView = findViewById(R.id.RecordSessionsMonday)
        val tuesdayRecord: TextView = findViewById(R.id.RecordSessionsTuesday)
        val wednesdayRecord: TextView = findViewById(R.id.RecordSessionsWednesday)
        val thursdayRecord: TextView = findViewById(R.id.RecordSessionsThursday)
        val fridayRecord: TextView = findViewById(R.id.RecordSessionsFriday)
        val saturdayRecord: TextView = findViewById(R.id.RecordSessionsSaturday)
        val sundayRecord: TextView = findViewById(R.id.RecordSessionsSunday)

        val recordList: LinearLayout = findViewById(R.id.RecordList)

        //SharedPreferences
        val sharedPreferences = getSharedPreferences("RECORDS", Context.MODE_PRIVATE)
        var editor = sharedPreferences.edit()

        //Stringi do czasomierzu i tabeli rekordów
        var timerString: String
        var recordString: String

        val timerStringBuilder = StringBuilder()
        val recordStringBuilder = StringBuilder()

        //rekordy dla każdego dnia tygodnia
        var workThisSession = 0
        var mostWorkOnMonday = sharedPreferences.getInt("MondayRecord", 0)
        var mostWorkOnTuesday = sharedPreferences.getInt("TuesdayRecord", 0)
        var mostWorkOnWednesday = sharedPreferences.getInt("WednesdayRecord", 0)
        var mostWorkOnThursday = sharedPreferences.getInt("ThursdayRecord", 0)
        var mostWorkOnFriday = sharedPreferences.getInt("FridayRecord", 0)
        var mostWorkOnSaturday = sharedPreferences.getInt("SaturdayRecord", 0)
        var mostWorkOnSunday = sharedPreferences.getInt("SundayRecord", 0)

        //do czasomierza
        var timerSeconds: Int
        var timerMinutes: Int

        //inne zmienne
        var isBreakTime = false
        var countdown: Long = 1500000
        var isTimerRunning = false

        val calendar = Calendar.getInstance()

        var currentDay = calendar.get(Calendar.DAY_OF_WEEK)

        var dayOfTheWeek = when(currentDay){
            2 -> "Dziś jest poniedziałek"
            3 -> "Dziś jest Wtorek"
            4 -> "Dziś jest Środa"
            5 -> "Dziś jest Czwartek"
            6 -> "Dziś jest Piątek"
            0 -> "Dziś jest Sobota"
            1 -> "Dziś jest Niedziela"
            else -> "Brak daty"
        }
        currentDayOfWeek.text = dayOfTheWeek

        //funkcje
        //wyświetlanie czasu do zakończenia pracy/przerwy
        fun SetTimer(){
            timerSeconds = countdown.toInt() / 1000
            timerMinutes = timerSeconds / 60
            timerSeconds = timerSeconds - (timerMinutes * 60)
            timerStringBuilder.append(timerMinutes).append(":").append(timerSeconds)
            if(timerSeconds < 9) timerStringBuilder.append("0")
            timerString = timerStringBuilder.toString()
            timerStringBuilder.clear()
            bigTimer.text = timerString
            smallTimer.text = timerString
        }
        //ustawienie widoku na główny widok
        fun SetHomeView(){
            smallTimer.visibility = View.GONE
            bigTimer.visibility = View.VISIBLE
            timerStart.visibility = View.VISIBLE
            currentDayOfWeek.visibility = View.GONE
            recordList.visibility = View.GONE
            homePage.setBackgroundColor(Color.parseColor("#9b74fc"))
            recordsPage.setBackgroundColor(Color.parseColor("#845AED"))
        }
        //ustawienie widoku na listę rekordu dla każdego dnia tygodnia
        fun SetRecordsView(){
            bigTimer.visibility = View.GONE
            smallTimer.visibility = View.VISIBLE
            timerStart.visibility = View.GONE
            currentDayOfWeek.visibility = View.VISIBLE
            recordList.visibility = View.VISIBLE
            recordsPage.setBackgroundColor(Color.parseColor("#9b74fc"))
            homePage.setBackgroundColor(Color.parseColor("#845AED"))
        }
        //czy czas na przerwę czy pracę
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
        //ustawienie rekordu dla każdego dnia tygodnia
        fun setRecord(){
            workThisSession += 1
            when(currentDay){
                2 -> {
                    //poniedziałek
                    if(workThisSession >= mostWorkOnMonday) mostWorkOnMonday = workThisSession
                    recordStringBuilder.append("Rekord: " + mostWorkOnMonday)
                    recordString = recordStringBuilder.toString()
                    mondayRecord.text = recordString
                    when(mostWorkOnMonday){
                        in 0..3 -> medalMonday.setImageResource(R.drawable.pizza_time)
                        in 4..7 -> medalMonday.setImageResource(R.drawable.screenshot_from_2024_03_17_12_54_47)
                        in 8..Int.MAX_VALUE -> medalMonday.setImageResource(R.drawable.cato)
                    }
                    editor.putInt("MondayRecord", mostWorkOnMonday)
                }
                3 -> {
                    //wtorek
                    if(workThisSession >= mostWorkOnTuesday) mostWorkOnTuesday = workThisSession
                    recordStringBuilder.append("Rekord: " + mostWorkOnTuesday)
                    recordString = recordStringBuilder.toString()
                    tuesdayRecord.text = recordString
                    when(mostWorkOnTuesday){
                        in 0..3 -> medalTuesday.setImageResource(R.drawable.pizza_time)
                        in 4..7 -> medalTuesday.setImageResource(R.drawable.screenshot_from_2024_03_17_12_54_47)
                        in 8..Int.MAX_VALUE -> medalTuesday.setImageResource(R.drawable.cato)
                    }
                    editor.putInt("TuesdayRecord", mostWorkOnTuesday)
                }
                //środa
                4 -> {
                    if(workThisSession >= mostWorkOnWednesday) mostWorkOnWednesday = workThisSession
                    recordStringBuilder.append("Rekord: " + mostWorkOnWednesday)
                    recordString = recordStringBuilder.toString()
                    wednesdayRecord.text = recordString
                    when(mostWorkOnWednesday){
                        in 0..3 -> medalWednesday.setImageResource(R.drawable.pizza_time)
                        in 4..7 -> medalWednesday.setImageResource(R.drawable.screenshot_from_2024_03_17_12_54_47)
                        in 8..Int.MAX_VALUE -> medalWednesday.setImageResource(R.drawable.cato)
                    }
                    editor.putInt("WednesdayRecord", mostWorkOnWednesday)
                }
                //czwartek
                5 -> {
                    if(workThisSession >= mostWorkOnThursday) mostWorkOnThursday = workThisSession
                    recordStringBuilder.append("Rekord: " + mostWorkOnThursday)
                    recordString = recordStringBuilder.toString()
                    thursdayRecord.text = recordString
                    when(mostWorkOnThursday){
                        in 0..3 -> medalThursday.setImageResource(R.drawable.pizza_time)
                        in 4..7 -> medalThursday.setImageResource(R.drawable.screenshot_from_2024_03_17_12_54_47)
                        in 8..Int.MAX_VALUE -> medalThursday.setImageResource(R.drawable.cato)
                    }
                    editor.putInt("ThursdayRecord", mostWorkOnThursday)
                }
                //piątek
                6 -> {
                    if(workThisSession >= mostWorkOnFriday) mostWorkOnFriday = workThisSession
                    recordStringBuilder.append("Rekord: " + mostWorkOnFriday)
                    recordString = recordStringBuilder.toString()
                    fridayRecord.text = recordString
                    when(mostWorkOnFriday){
                        in 0..3 -> medalFriday.setImageResource(R.drawable.pizza_time)
                        in 4..7 -> medalFriday.setImageResource(R.drawable.screenshot_from_2024_03_17_12_54_47)
                        in 8..Int.MAX_VALUE -> medalFriday.setImageResource(R.drawable.cato)
                    }
                    editor.putInt("FridayRecord", mostWorkOnFriday)
                }
                //sobota
                0 -> {
                    if(workThisSession >= mostWorkOnSaturday) mostWorkOnSaturday = workThisSession
                    recordStringBuilder.append("Rekord: " + mostWorkOnSaturday)
                    recordString = recordStringBuilder.toString()
                    saturdayRecord.text = recordString
                    when(mostWorkOnSaturday){
                        in 0..3 -> medalSaturday.setImageResource(R.drawable.pizza_time)
                        in 4..7 -> medalSaturday.setImageResource(R.drawable.screenshot_from_2024_03_17_12_54_47)
                        in 8..Int.MAX_VALUE -> medalSaturday.setImageResource(R.drawable.cato)
                    }
                    editor.putInt("SaturdayRecord", mostWorkOnSaturday)
                }
                //niedziela
                1 -> {
                    if(workThisSession >= mostWorkOnSunday) mostWorkOnSunday = workThisSession
                    recordStringBuilder.append("Rekord: " + mostWorkOnSunday)
                    recordString = recordStringBuilder.toString()
                    sundayRecord.text = recordString
                    when(mostWorkOnSunday){
                        in 0..3 -> medalSunday.setImageResource(R.drawable.pizza_time)
                        in 4..7 -> medalSunday.setImageResource(R.drawable.screenshot_from_2024_03_17_12_54_47)
                        in 8..Int.MAX_VALUE -> medalSunday.setImageResource(R.drawable.cato)
                    }
                    editor.putInt("SundayRecord", mostWorkOnSunday)
                }
                else -> {}
            }
            editor.apply()
            recordStringBuilder.clear()
        }

        //ustawienie elementów UI przy zakończeniu czasomierza
        fun uiUponCompletion(){
            timerStart.setText("START")
            main.setBackgroundColor(Color.parseColor("#DED9FF"))
            timerStart.setBackgroundColor(Color.parseColor("#845AED"))
        }

        //operacje do zrobienia przy otwarciu aplikacji
        SetHomeView()
        SetTimer()

        timerStart.setBackgroundColor(Color.parseColor("#845AED"))
        homePage.setBackgroundColor(Color.parseColor("#845AED"))
        recordsPage.setBackgroundColor(Color.parseColor("#845AED"))

        //czasomierze
        //praca
        val workTimer = object: CountDownTimer(1000/*1500000*/, 1000){
            override fun onTick(millisUntilFinished: Long) {
                SetTimer()
                countdown = millisUntilFinished
            }
            override fun onFinish() {
                ifBreakTime()
                isTimerRunning = false
                setRecord()
                uiUponCompletion()
            }
        }
        //przerwa
        val breakTimer = object: CountDownTimer(300000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                SetTimer()
                countdown = millisUntilFinished
            }
            override fun onFinish() {
                ifBreakTime()
                isTimerRunning = false
                uiUponCompletion()
            }
        }
        //funkcja od rozpoczęcia czasomierza
        fun startTimer(){
            if(isBreakTime == false){
                workTimer.start()
                main.setBackgroundColor(Color.parseColor("#FCBDDE"))
                timerStart.setBackgroundColor(Color.parseColor("#FC5353"))
            }
            else{
                breakTimer.start()
                main.setBackgroundColor(Color.parseColor("#B3FCE8"))
                timerStart.setBackgroundColor(Color.parseColor("#4CFA34"))
            }
        }

        //przyciski od zmiany widoku
        homePage.setOnClickListener({
            SetHomeView()
        })
        recordsPage.setOnClickListener({
            SetRecordsView()
        })
        //przycisk od aktywowania/zatrzymywania czasomierza
        timerStart.setOnClickListener({
            if(isTimerRunning == false){
                isTimerRunning = true
                timerStart.setText("STOP")
                startTimer()
            }
            else{
                isTimerRunning = false
                workTimer.cancel()
                breakTimer.cancel()
                ifBreakTime()
                SetTimer()
                uiUponCompletion()
            }
        })
    }
}