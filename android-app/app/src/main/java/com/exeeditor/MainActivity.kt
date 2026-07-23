package com.exeeditor

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val runBtn = findViewById<Button>(R.id.runButton)
        val status = findViewById<TextView>(R.id.status)

        runBtn.setOnClickListener {
            status.text = "Running sample FFmpeg command..."
            // Example: extract audio from a bundled/sample input (replace paths in real use)
            val input = "/sdcard/Download/input_video.mp4"
            val output = "/sdcard/Download/output_audio.aac"
            val cmd = "-i \"$input\" -vn -acodec copy \"$output\""

            Thread {
                val session = FFmpegKit.execute(cmd)
                val returnCode = session.returnCode
                runOnUiThread {
                    if (ReturnCode.isSuccess(returnCode)) {
                        status.text = "FFmpeg finished: audio saved to output_audio.aac"
                    } else {
                        status.text = "FFmpeg failed: rc=$returnCode"
                    }
                }
            }.start()
        }
    }
}
