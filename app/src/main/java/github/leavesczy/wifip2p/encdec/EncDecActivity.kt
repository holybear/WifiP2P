package github.leavesczy.wifip2p.encdec

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import github.leavesczy.wifip2p.R
import github.leavesczy.wifip2p.databinding.ActivityEncDecBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


class EncDecActivity : AppCompatActivity() {
    private var downLoadPercentage = 0
    private var connection: HttpURLConnection? = null
    lateinit var progressBar: ProgressBar
    lateinit var sourceDir: File
    lateinit var destinationDir: File
    lateinit var videoView: VideoView
    lateinit var playerTargetLocaiton: File
    val PICK_VIDEO_REQUEST = 989
    // This property is only valid between onCreateView and
    // onDestroyView.

    lateinit var layout: androidx.constraintlayout.widget.ConstraintLayout

    private var result = MutableLiveData<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enc_dec)

        val binding = ActivityEncDecBinding.inflate(layoutInflater)
        videoView = binding.idVideoView
        setContentView(binding.root)
        layout = findViewById(R.id.layout) as ConstraintLayout
        requestReadExternalStoragePermission()
        binding.buttonFirst.setOnClickListener {

            videoView.visibility = View.VISIBLE
            // creating and initializing variable for media controller on below line.
            val mediaController = MediaController(this)
            // setting media controller anchor view to video view on below line.
            mediaController.setAnchorView(videoView)
            // on below line we are getting uri for the video file which we are adding in our project.
            val mediaFile =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val targetDir = File(mediaFile, "edumia_video_dec");
            val targetLocation = File(targetDir, "/hbVideos_dec.mp4")

            val uri: Uri = Uri.parse(targetLocation.toString())
            //on below line we are setting media controller for our video view.
            videoView.setMediaController(mediaController);
            // on below line we are setting video uri for our video view.
            videoView.setVideoURI(uri);
            // on below line we are requesting focus for our video view.
            videoView.requestFocus();
            // on below line we are calling start  method to start our video view.
            videoView.start();
        }
        binding.buttonFirst.visibility=View.GONE
        binding.buttonSecond.setOnClickListener {
            progressDialogueSHow()
            val job = CoroutineScope(Dispatchers.Default).launch {
                download(
                    FileDownloadStatus(
                        "hbVideos_enc.mp4",
                        0,
                        "internal url",
                        "https://edmspace.sgp1.digitaloceanspaces.com/uik/EST123.mp4"
                    )
                )
            }
        }
        binding.buttonThird.setOnClickListener {
            progressDialogueSHow()
            val mediaFolderPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath

            //dirRequest.launch(mediaFolderPath.toUri())
            openFilePicker()
        }



        result.observe(this, Observer { status ->
            if (status == "loading") {
                progressBar.visibility = View.VISIBLE
                binding.buttonSecond.isEnabled = false
                binding.buttonThird.isEnabled = false
            } else if (status == "loadingEnd") {
                progressBar.visibility = View.GONE
                binding.buttonSecond.isEnabled = true
                binding.buttonThird.isEnabled = true
                binding.idVideoView.visibility = View.VISIBLE
            } else if (status == "readytoplay") {
                progressBar.visibility = View.GONE
                binding.buttonSecond.isEnabled = true
                binding.buttonThird.isEnabled = true
                binding.idVideoView.visibility = View.VISIBLE
                playMediaFile(playerTargetLocaiton)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            //val treeUri = data.data.data
            // Store the tree URI for future use

        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"  // Adjust the MIME type if needed
        }
        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedFileUri = result.data?.data ?: return@registerForActivityResult

                val job = CoroutineScope(Dispatchers.Default).launch {

                    val listofurls = selectedFileUri.toString().split("%2F")
                    copyDirectory(listofurls[listofurls.size - 1])
                }
                // Access the selected file using the URI
                // ... (your code to handle the file)
            }
        }

    private suspend fun download(ob: FileDownloadStatus) {

        /* val decfileDir =
             File(context?.filesDir.toString() + "/edumia_video-enc/")*/

        //val decfileDir =  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "edumia_video_enc");
        val mediaFolderPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
        // Create a new file object for the folder.
        val decfileDir = File(mediaFolderPath, "edumia_video_enc")
        if (!decfileDir.exists()) {
            val l = decfileDir.mkdir()
            Log.d("test", "log")
        } else {

        }

        /*  val values = ContentValues().apply {
              put(MediaStore.MediaColumns.DISPLAY_NAME, "edumia_video_enc")
              put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
              put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4") // Specify video MIME type
          }

          val decfileDir = context?.contentResolver?.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
         */

        val futureStudioIconFile = File(decfileDir, (ob.namefile))
        sourceDir = futureStudioIconFile
        var output = FileOutputStream(futureStudioIconFile, true)
        var input: InputStream? = null
        // var output: OutputStream? = null
        val initialLength = futureStudioIconFile.length().toInt()
        downLoadPercentage = 0
        val job = CoroutineScope(Dispatchers.Default).launch {

            try {
                var url = URL(ob.urlExternal)
                connection = url.openConnection() as HttpURLConnection
                connection!!.requestMethod = "GET"
                connection!!.instanceFollowRedirects = false
                connection!!.connectTimeout = 15000
                connection!!.readTimeout = 15000
                connection!!.allowUserInteraction = true
                connection!!.setRequestProperty(
                    "Range",
                    "bytes=" + futureStudioIconFile.length().toInt() + "-"
                )


                connection!!.setRequestProperty("Accept-Encoding", "identity")
                connection!!.doInput = true
                connection!!.doOutput = false
                connection!!.connect()
                //connection!!.setConnectTimeout(15000)
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection!!.responseCode !== HttpURLConnection.HTTP_OK && connection!!.responseCode !== HttpURLConnection.HTTP_PARTIAL) {
                    /*return ("Server returned HTTP " + connection!!.getResponseCode()
                            + " " + connection!!.getResponseMessage())*/
                    val code = connection!!.responseCode
                    Log.d("test", "test$code")
                    val redirectHeader = connection!!.getHeaderField("Location")
                    url = URL(redirectHeader)
                    connection = url.openConnection() as HttpURLConnection
                    connection!!.instanceFollowRedirects = false
                    connection!!.connectTimeout = 15000
                    connection!!.readTimeout = 15000

                    connection!!.allowUserInteraction = true
                    connection!!.setRequestProperty(
                        "Range",
                        "bytes=" + futureStudioIconFile.length().toInt() + "-"
                    )


                    connection!!.doInput = true
                    connection!!.doOutput = false
                    connection!!.connect()


                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                var fileLength = connection!!.contentLength
                fileLength += initialLength
                // download the file
                input = connection!!.inputStream
                //  output = FileOutputStream("/sdcard/file_name.extension")

                val data = ByteArray(4096)
                var total: Long = initialLength.toLong()
                var count: Int
                //encryptionbit next two lines
                val data1 = ByteArray(1)
                output.write(data1, 0, 1)
                do {
                    // allow canceling with back button
                    count = input!!.read(data)
                    if (count != -1) {
                        total += count.toLong()
                        // publishing the progress....
                        if (fileLength > 0)
                        // only if total length is known
                            downLoadPercentage = ((total * 100 / fileLength).toInt())

                        ob.namefile?.let {
                            this?.let { it1 ->
                                // notification update here
                            }
                        }

                        // publishProgress((total * 100 / fileLength).toInt())
                        output.write(data, 0, count)
                    }
                    Log.d(" downLoadPercentage-------", "" + downLoadPercentage)
                } while (count != -1)


            } catch (e: Exception) {
                Log.d("downLoadPercentage-------exce--", e.message + "")
                output.close()

                progressadialogueHide()
            } finally {
                try {
                    output.close()
                    input?.close()
                    progressadialogueHide()
                } catch (ignored: IOException) {
                    progressadialogueHide()
                }

                if (connection != null)
                    connection!!.disconnect()

            }
        }

    }

    private fun skipBytes(
        `is`: InputStream, count: Int
    ): Boolean {
        val buff = ByteArray(count)
        /*
   * offset could be used in "is.read" as the second arg
   */try {
            val r = `is`.read(buff, 0, count)
            return r == count
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }


    @Throws(IOException::class)
    fun copyDirectory(filepath: String) {

        val mediaFolderPath1 =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
        val targetDir1 = File(mediaFolderPath1, "edumia_video_enc");
        val source1 = File(targetDir1, filepath)
        val source = source1
        //val targetDir=  File(context?.filesDir.toString() + "/edumia_video_dec/")
        /* val targetLocation =
         File(context?.filesDir.toString() + "/edumia_video-dec/hbVideos_dec.mp4")*/


        val mediaFolderPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath


        val targetDir = File(mediaFolderPath, "edumia_video_dec");


        if (!targetDir.exists()) {
            targetDir.mkdir()
        } else {
            targetDir.delete()
            targetDir.mkdir()
        }
        val targetLocation = File(targetDir, "/hbVideos_dec.mp4")
        //  val targetLocation = File(filepath)
        destinationDir = targetLocation
        val `in`: InputStream = FileInputStream(source)
        val out: OutputStream = FileOutputStream(targetLocation)

        skipBytes(`in`, 1)
        // Copy the bits from instream to outstream
        val buf = ByteArray(1024)
        var len: Int
        while (`in`.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        `in`.close()
        out.close()

        runBlocking {
            progressadialogueHide()

        }
        //  playMediaFile(targetLocation)
        playerTargetLocaiton = targetLocation
        result.postValue("readytoplay")

    }

    fun playMediaFile(targetLocation: File) {
        videoView.visibility = View.VISIBLE
        // creating and initializing variable for media controller on below line.
        val mediaController = MediaController(this)
        // setting media controller anchor view to video view on below line.
        mediaController.setAnchorView(videoView)
        // on below line we are getting uri for the video file which we are adding in our project.
        val uri: Uri = Uri.parse(targetLocation.toString())
        //on below line we are setting media controller for our video view.
        videoView.setMediaController(mediaController);
        // on below line we are setting video uri for our video view.
        videoView.setVideoURI(uri);
        // on below line we are requesting focus for our video view.
        videoView.requestFocus();
        // on below line we are calling start  method to start our video view.
        videoView.start();
    }


    fun progressDialogueSHow() {
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(100, 100)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        layout.addView(progressBar, params)
        result.postValue("loading")
    }

    suspend fun progressadialogueHide() {

        result.postValue("loadingEnd")

    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        // Permission Granted for android 11+
                    } else {
                        showAndroid11PlusPermissionDialog()
                    }
                } else {
                    // Permission Granted for android marshmallow+
                }
            } else {
                // Permission not granted
            }
        }

    private val android11PlusSettingResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Environment.isExternalStorageManager()
                } else {
                    TODO("VERSION.SDK_INT < R")
                }
            ) {
                // Permission Granted for android 11+
            } else {
                // Permission not granted
            }
        }

    fun requestReadExternalStoragePermission() {
        when {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        // Permission Granted for android 11+
                        showAndroid11PlusPermissionDialog()
                    } else {
                        showAndroid11PlusPermissionDialog()
                    }
                } else {
                    // Permission Granted
                }
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        // Permission Granted
                        return
                    }
                }
                // show rational dialog
            }
        }
    }

    private fun showAndroid11PlusPermissionDialog() {
        this?.applicationContext?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Allow access")
                .setMessage("Access details")
                .setPositiveButton("open settings") { dialog, _ ->
                    val intent = Intent().apply {
                        action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                        // data = Uri.fromParts("package", this, null)
                    }
                    dialog.dismiss()
                    android11PlusSettingResultLauncher.launch(intent)
                }
                .setNegativeButton("not now") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun checkPermission(permission: String) =
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
}