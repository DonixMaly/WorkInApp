package com.example.workin

import android.content.Context
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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

        //Przechwycenie dzisiejszego dnia tygodnia
        val calendar = Calendar.getInstance()

        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)

        val dayOfTheWeek = when(currentDay){
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

        //SharedPreferences
        val sharedPreferences = getSharedPreferences("RECORDS", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        //resetuje rekordy każdego dnia tygodnia w poniedziałek
        //jak się ponownie otworzy apke w poniedziałek to
        if(currentDay == 2){
            editor.clear()
            editor.apply()
        }

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
        var isBreakTime = false
        var countdown: Long = 1500000
        var isTimerRunning = false

        //alertDialog
        val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val messageWork = "Po wciśnięciu \"Tak\" obecnie w trakcie pomodoro nie zaliczy się do dzisiaj zrobionych pomodoro.\n" +
                "Czy jesteś pewien, że chcesz kontynuować?"
        val messageBreak = "Po wciśnięciu \"Tak\" pominiesz przerwę i będziesz musiał(a) zrobić kolejne pomodoro, aby dostać się do kolejnej przerwy.\n" +
                "Czy jesteś pewien, że chcesz kontynuować?"
        var message = ""

        //manager wifi
        val wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

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
            when(currentDay){
                2 -> {
                    //poniedziałek
                    if(workThisSession >= mostWorkOnMonday) mostWorkOnMonday = workThisSession
                    recordStringBuilder.append("Rekord: " + mostWorkOnMonday)
                    recordString = recordStringBuilder.toString()
                    mondayRecord.text = recordString
                    when(mostWorkOnMonday){
                        in 0..3 -> medalMonday.setImageResource(R.drawable.nomedal)
                        in 4..7 -> medalMonday.setImageResource(R.drawable.bronzemedal)
                        in 8..15 -> medalMonday.setImageResource(R.drawable.silvermedal)
                        in 16..Int.MAX_VALUE -> medalMonday.setImageResource(R.drawable.goldmedal)
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
                        in 0..3 -> medalTuesday.setImageResource(R.drawable.nomedal)
                        in 4..7 -> medalTuesday.setImageResource(R.drawable.bronzemedal)
                        in 8..15 -> medalTuesday.setImageResource(R.drawable.silvermedal)
                        in 16..Int.MAX_VALUE -> medalTuesday.setImageResource(R.drawable.goldmedal)
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
                        in 0..3 -> medalWednesday.setImageResource(R.drawable.nomedal)
                        in 4..7 -> medalWednesday.setImageResource(R.drawable.bronzemedal)
                        in 8..15 -> medalWednesday.setImageResource(R.drawable.silvermedal)
                        in 16..Int.MAX_VALUE -> medalWednesday.setImageResource(R.drawable.goldmedal)
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
                        in 0..3 -> medalThursday.setImageResource(R.drawable.nomedal)
                        in 4..7 -> medalThursday.setImageResource(R.drawable.bronzemedal)
                        in 8..15 -> medalThursday.setImageResource(R.drawable.silvermedal)
                        in 16..Int.MAX_VALUE -> medalThursday.setImageResource(R.drawable.goldmedal)
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
                        in 0..3 -> medalFriday.setImageResource(R.drawable.nomedal)
                        in 4..7 -> medalFriday.setImageResource(R.drawable.bronzemedal)
                        in 8..15 -> medalFriday.setImageResource(R.drawable.silvermedal)
                        in 16..Int.MAX_VALUE -> medalFriday.setImageResource(R.drawable.goldmedal)
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
                        in 0..3 -> medalSaturday.setImageResource(R.drawable.nomedal)
                        in 4..7 -> medalSaturday.setImageResource(R.drawable.bronzemedal)
                        in 8..15 -> medalSaturday.setImageResource(R.drawable.silvermedal)
                        in 16..Int.MAX_VALUE -> medalSaturday.setImageResource(R.drawable.goldmedal)
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
                        in 0..3 -> medalSunday.setImageResource(R.drawable.nomedal)
                        in 4..7 -> medalSunday.setImageResource(R.drawable.bronzemedal)
                        in 8..15 -> medalSunday.setImageResource(R.drawable.silvermedal)
                        in 16..Int.MAX_VALUE -> medalSunday.setImageResource(R.drawable.goldmedal)
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
        setRecord()

        timerStart.setBackgroundColor(Color.parseColor("#845AED"))
        homePage.setBackgroundColor(Color.parseColor("#845AED"))
        recordsPage.setBackgroundColor(Color.parseColor("#845AED"))

        //czasomierze
        //praca
        val workTimer = object: CountDownTimer(3000/*1500000*/, 1000){
            override fun onTick(millisUntilFinished: Long) {
                SetTimer()
                countdown = millisUntilFinished
                wifiManager.isWifiEnabled = false
            }
            override fun onFinish() {
                ifBreakTime()
                isTimerRunning = false
                workThisSession += 1
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
                timerStart.setText("POMIŃ")
                startTimer()
            }
            else{
                //tworzony jest alert potwierdzający decyzję użytkownika na pominięcie pracy/przerwy
                if(isBreakTime != true) message = messageWork
                else message = messageBreak
                alertBuilder
                    .setTitle("Czy masz pewność?")
                    .setMessage(message)
                    .setPositiveButton("Tak") { dialog, which ->
                        isTimerRunning = false
                        workTimer.cancel()
                        breakTimer.cancel()
                        ifBreakTime()
                        SetTimer()
                        uiUponCompletion()
                    }
                    .setNegativeButton("Nie") {dialog, which ->

                    }
                val dialog: AlertDialog = alertBuilder.create()
                dialog.show()
            }
        })
    }
}