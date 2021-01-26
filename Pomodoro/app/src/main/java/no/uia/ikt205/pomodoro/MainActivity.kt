package no.uia.ikt205.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import no.uia.ikt205.pomodoro.util.millisecondsToDescriptiveTime

class MainActivity : AppCompatActivity() {

    lateinit var timer:CountDownTimer
    lateinit var startButton:Button
    lateinit var coutdownDisplay:TextView
    lateinit var timeButton30: Button
    lateinit var timeButton60: Button
    lateinit var timeButton90: Button
    lateinit var timeButton120: Button

    var timeToCountDownInMs = 5000L
    val timeTicks = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById<Button>(R.id.startCountdownButton)
        startButton.setOnClickListener(){
            startCountDown(it)
        }
        coutdownDisplay = findViewById<TextView>(R.id.countDownView)
        timeButton30 = findViewById<Button>(R.id.timeSelect30)
        timeButton30.setOnClickListener(){setCountTime(30)}
        timeButton60 = findViewById<Button>(R.id.timeSelect60)
        timeButton60.setOnClickListener(){setCountTime(60)}
        timeButton90 = findViewById<Button>(R.id.timeSelect90)
        timeButton90.setOnClickListener(){setCountTime(90)}
        timeButton120 = findViewById<Button>(R.id.timeSelect120)
        timeButton120.setOnClickListener(){setCountTime(120)}
    }

    fun setCountTime(t_min: Int){
        timeToCountDownInMs = (t_min * 1000 * 60).toLong()
    }

    fun startCountDown(v: View){
        startButton.isEnabled = false
        timer = object : CountDownTimer(timeToCountDownInMs,timeTicks) {
            override fun onFinish() {
                Toast.makeText(this@MainActivity,"Arbeidsøkt er ferdig", Toast.LENGTH_SHORT).show()
                startButton.isEnabled = true
            }

            override fun onTick(millisUntilFinished: Long) {
               updateCountDownDisplay(millisUntilFinished)
            }
        }

        timer.start()
    }

    fun updateCountDownDisplay(timeInMs:Long){
        coutdownDisplay.text = millisecondsToDescriptiveTime(timeInMs)
    }

}