package com.exeeditor

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private val PICK_VIDEO_REQUEST = 101
    private val PICK_AUDIO_REQUEST = 102

    private var selectedVideoUri: Uri? = null
    private var selectedAudioUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pickVideoBtn = findViewById<Button>(R.id.pickVideoButton)
        val pickAudioBtn = findViewById<Button>(R.id.pickAudioButton)
        val extractBtn = findViewById<Button>(R.id.extractButton)
        val overlayBtn = findViewById<Button>(R.id.overlayButton)
        val status = findViewById<TextView>(R.id.status)

        pickVideoBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(Intent.createChooser(intent, "Select video"), PICK_VIDEO_REQUEST)
        }

        pickAudioBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            startActivityForResult(Intent.createChooser(intent, "Select audio"), PICK_AUDIO_REQUEST)
        }

        extractBtn.setOnClickListener {
            val uri = selectedVideoUri
            if (uri == null) {
                status.text = "Select a video first"
                return@setOnClickListener
            }
            status.text = "Extracting audio (stub)..."
            Thread {
                try {
                    val inFile = copyUriToFile(uri, "input_video")
                    val downloads = getExternalDownloadsDir()
                    val outFile = File(downloads, "extracted_audio_stub_${System.currentTimeMillis()}.aac")
                    // Stub: just copy the input file to a .aac named file as a placeholder
                    inFile.inputStream().use { input ->
                        outFile.outputStream().use { out ->
                            input.copyTo(out)
                        }
                    }
                    runOnUiThread { status.text = "Audio extracted (stub): ${outFile.absolutePath}" }
                } catch (e: Exception) {
                    runOnUiThread { status.text = "Extraction failed: ${e.message}" }
                }
            }.start()
        }

        overlayBtn.setOnClickListener {
            val videoUri = selectedVideoUri
            val audioUri = selectedAudioUri
            if (videoUri == null || audioUri == null) {
                status.text = "Select both video and audio first"
                return@setOnClickListener
            }
            status.text = "Merging audio into video..."
            Thread {
                try {
                    val videoFile = copyUriToFile(videoUri, "target_video")
                    val audioFile = copyUriToFile(audioUri, "source_audio")
                    val downloads = getExternalDownloadsDir()
                    val outFile = File(downloads, "EXE-Editor-output-stub-${System.currentTimeMillis()}.mp4")
                    // Stub: copy the video file to output as placeholder
                    videoFile.inputStream().use { input ->
                        outFile.outputStream().use { out ->
                            input.copyTo(out)
                        }
                    }
                    runOnUiThread { status.text = "Exported (stub): ${outFile.absolutePath}" }
                } catch (e: Exception) {
                    runOnUiThread { status.text = "Export failed: ${e.message}" }
                }
            }.start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        val uri = data?.data ?: return
        when (requestCode) {
            PICK_VIDEO_REQUEST -> {
                selectedVideoUri = uri
                findViewById<TextView>(R.id.status).text = "Video selected: ${getFileName(uri)}"
            }
            PICK_AUDIO_REQUEST -> {
                selectedAudioUri = uri
                findViewById<TextView>(R.id.status).text = "Audio selected: ${getFileName(uri)}"
            }
        }
    }

    private fun copyUriToFile(uri: Uri, prefix: String): File {
        val input = contentResolver.openInputStream(uri) ?: throw IllegalStateException("Cannot open URI")
        val ext = getFileExtension(uri)
        val file = File(cacheDir, "$prefix-${System.currentTimeMillis()}${if (ext.isNullOrEmpty()) "" else "." + ext}")
        FileOutputStream(file).use { out ->
            input.copyTo(out)
        }
        return file
    }

    private fun getFileExtension(uri: Uri): String? {
        var name = getFileName(uri) ?: return null
        val dot = name.lastIndexOf('.')
        return if (dot >= 0) name.substring(dot + 1) else null
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) result = cursor.getString(idx)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) result = result?.substring(cut + 1)
        }
        return result
    }

    private fun getExternalDownloadsDir(): File {
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloads.exists()) downloads.mkdirs()
        return downloads
    }
}
