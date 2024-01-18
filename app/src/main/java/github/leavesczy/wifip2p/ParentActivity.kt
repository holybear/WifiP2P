package github.leavesczy.wifip2p

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import github.leavesczy.wifip2p.encdec.EncDecActivity


class ParentActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent)
        val btn= findViewById<Button>(R.id.btn_player)
        btn.setOnClickListener {
            val intent=Intent(this,EncDecActivity::class.java)
            startActivity(intent)
        }

        val btn2= findViewById<Button>(R.id.btn_filetransfer)
        btn2.setOnClickListener {
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

    }
}