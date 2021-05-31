package no.uia.ikt205.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import no.uia.ikt205.pomodoro.util.millisecondsToDescriptiveTime

class MainActivity : AppCompatActivity() {

    lateinit var timer:CountDownTimer
    lateinit var startButton:Button
    lateinit var countdownDisplay:TextView
    lateinit var timeWorkSeekBar: SeekBar
    lateinit var timePauseSeekBar: SeekBar
    lateinit var timeWorkTextView: TextView
    lateinit var timePauseTextView: TextView

    var workTimeToCountDownInMs = 5000L
    var pauseTimeToCountDownInMs = 5000L
    val timeTicks = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById<Button>(R.id.startCountdownButton)
        startButton.setOnClickListener {
            startCountDown(it)
        }
        countdownDisplay = findViewById<TextView>(R.id.countDownView)
        timeWorkTextView = findViewById<TextView>(R.id.timeWorkTextView)
        timePauseTextView = findViewById<TextView>(R.id.timePauseTextView)
        timeWorkSeekBar = findViewById<SeekBar>(R.id.timeWorkSeekBar)
        timePauseSeekBar = findViewById<SeekBar>(R.id.timePauseSeekBar)

        timeWorkSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                // Empty
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // Empty
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                Toast.makeText(this@MainActivity,
                    "Chosen work time: " + timeWorkSeekBar.progress + " minutes",
                    Toast.LENGTH_SHORT
                ).show()
                setWorkTime(timeWorkSeekBar.progress)
            }
        })

        timePauseSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                // Empty
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // Empty
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                Toast.makeText(this@MainActivity,
                    "Chosen pause time: " + timePauseSeekBar.progress + " minutes",
                    Toast.LENGTH_SHORT
                ).show()
                setPauseTime(timePauseSeekBar.progress)
            }
        })
    }

    fun setWorkTime(t_min: Int){
        workTimeToCountDownInMs = (t_min * 1000 * 60).toLong()
    }
    fun setPauseTime(t_min: Int){
        pauseTimeToCountDownInMs = (t_min * 1000 * 60).toLong()
    }

    fun startCountDown(v: View){
        startButton.isEnabled = false
        timer = object : CountDownTimer(workTimeToCountDownInMs,timeTicks) {
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
        countdownDisplay.text = millisecondsToDescriptiveTime(timeInMs)
    }

}

