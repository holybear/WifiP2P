package github.leavesczy.wifip2p.receiver

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.wifip2p.Constants
import github.leavesczy.wifip2p.models.FileTransfer
import github.leavesczy.wifip2p.models.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.ObjectInputStream
import java.net.InetSocketAddress
import java.net.ServerSocket

/**
 * @Author: CZY
 * @Date: 2022/9/26 14:18
 * @Desc:
 */
class FileReceiverViewModel(context: Application) :
    AndroidViewModel(context) {

    private val _viewState = MutableSharedFlow<ViewState>()

    val viewState: SharedFlow<ViewState> = _viewState

    private val _log = MutableSharedFlow<String>()

    val log: SharedFlow<String> = _log

    private var job: Job? = null

    fun startListener() {
        if (job != null) {
            return
        }
        job = viewModelScope.launch(context = Dispatchers.IO) {
            _viewState.emit(value = ViewState.Idle)

            var serverSocket: ServerSocket? = null
            var clientInputStream: InputStream? = null
            var objectInputStream: ObjectInputStream? = null
            var fileOutputStream: FileOutputStream? = null
            try {
                _viewState.emit(value = ViewState.Connecting)
                log(log = "turn on Socket")

                serverSocket = ServerSocket()
                serverSocket.bind(InetSocketAddress(Constants.PORT))
                serverSocket.reuseAddress = true
                serverSocket.soTimeout = 30000

                log(log = "socket accept，三十Disconnect if unsuccessful within seconds")

                val client = serverSocket.accept()

                _viewState.emit(value = ViewState.Receiving)

                clientInputStream = client.getInputStream()
                objectInputStream = ObjectInputStream(clientInputStream)
                val fileTransfer = objectInputStream.readObject() as FileTransfer
                val file = File(getCacheDir(context = getApplication()), fileTransfer.fileName)

                log(log = "The connection is successful, the file to be received: $fileTransfer")
                log(log = "The file will be saved to: $file")
                log(log = "start file transfer")

                fileOutputStream = FileOutputStream(file)
                val buffer = ByteArray(1024 * 100)
                while (true) {
                    val length = clientInputStream.read(buffer)
                    if (length > 0) {
                        fileOutputStream.write(buffer, 0, length)
                    } else {
                        break
                    }
                    log(log = "transferring files，length : $length")
                }
                _viewState.emit(value = ViewState.Success(file = file))
                log(log = "File received successfully")
            } catch (e: Throwable) {
                log(log = "abnormal: " + e.message)
                _viewState.emit(value = ViewState.Failed(throwable = e))
            } finally {
                serverSocket?.close()
                clientInputStream?.close()
                objectInputStream?.close()
                fileOutputStream?.close()
            }
        }
        job?.invokeOnCompletion {
            job = null
        }
    }

    private fun getCacheDir(context: Context): File {
        //val cacheDir = File(context.cacheDir, "FileTransfer")
        //cacheDir.mkdirs()
        //return cacheDir
      /*  val externalDirs: Array<File> = context.getExternalMediaDirs()
        // Create a new File object for the sub folder you want to create.
        val subFolder = File(externalDirs[0], "edumia_file_transfer")
        subFolder.mkdirs()
        return subFolder*/
        // Get the media folder path.
    /*    val decfileDir =  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "edumia_video_enc");

        if (!decfileDir.exists()) {
            val l=  decfileDir.mkdir()
            Log.d("test","log")
        } else {
            decfileDir.delete()
            decfileDir.mkdir()
        }*/
        val mediaFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
        // Create a new file object for the folder.
        val folder = File(mediaFolderPath, "edumia_video_enc")
        // If the folder does not exist, create it.
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

    private suspend fun log(log: String) {
        _log.emit(value = log)
    }

}