package com.example.runfromuri

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSIONS = 1001
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("fdsa","これからパーミッション確認。")

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )

        }


        Log.d("fdsa","アプリが起動できました。")
        createDirectory()
        createHTML()

        //画面にURIパラメータを入れてみる
        // IntentからURIを取得
        val uri: Uri? = intent?.data

        // URIからパラメータを取得
        val param1 = uri?.getQueryParameter("param1")
        val param2 = uri?.getQueryParameter("param2")

        val text = findViewById<TextView>(R.id.test)

        text.text = "$param1,$param2"

        // URLを指定します。
        val picturesDirectory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val newDirectory = File(picturesDirectory, "TatenoApps")
        val newDirectory2 = File(newDirectory, "ShashinChecker")
        val htmlfile = File(newDirectory2,"test.htm")
        //val url = newDirectory2.toPath().to//"file:///TatenoApps/test.htm"

        // Intentを作成し、指定したURLをブラウザで開きます。
        if(htmlfile.exists()){
            val intent = Intent(Intent.ACTION_VIEW)
            val htmlfileuri: Uri = FileProvider.getUriForFile(
                this,
                "${this.getApplicationContext().getPackageName()}.provider",
                htmlfile
            )
            intent.setDataAndType(htmlfileuri, "text/html");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent)
        }else{
            Log.d("fdsa","ファイルが見つかりません。${htmlfile.toUri().toString()}")
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun createDirectory() {
        val picturesDirectory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val newDirectory = File(picturesDirectory, "TatenoApps")
        if (!newDirectory.exists()) {
            newDirectory.mkdirs()
        }
        //さらにその下に
        val newAppDirectory = File(newDirectory, "ShashinChecker")
        if (!newAppDirectory.exists()) {
            newAppDirectory.mkdirs()
        }
    }

    fun createHTML(){
        // URLを指定します。
        val picturesDirectory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val newDirectory = File(picturesDirectory, "TatenoApps")
        val newDirectory2 = File(newDirectory, "ShashinChecker")
        val htmlfile = File(newDirectory2,"test.htm")
        val fileName = htmlfile.toString()
        Log.d("fdsa","これでHTMLをつくります${fileName}")
        val content = """
            <html>
            <body>
            <h1>Hello, World!</h1>
            <a href="shashinchekerapp://test/sample?param1=fdsa1&param2=fdsa2">run App</a>
            </body>
            </html>
        """.trimIndent()

        //val file = File(getExternalFilesDir(null), fileName)
        try {
            val fos = FileOutputStream(htmlfile)
            fos.write(content.toByteArray())
            fos.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }

}