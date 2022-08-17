package com.sariaydinalparslan.qrcodescanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.sariaydinalparslan.qrcodescanner.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(),
    EasyPermissions.PermissionCallbacks,EasyPermissions.RationaleCallbacks {

    var hide : Animation? = null
    var reveal : Animation? = null
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        hide=AnimationUtils.loadAnimation(this,android.R.anim.fade_in)
        reveal=AnimationUtils.loadAnimation(this,android.R.anim.fade_out)

        tvText!!.startAnimation(reveal)
        cardView2!!.startAnimation(reveal)
        tvText!!.setText("Scan QR Code Here")
        cardView2!!.visibility = View.VISIBLE

        scan.setOnClickListener {
            tvText!!.startAnimation(reveal)
            cardView1!!.startAnimation(hide)
            cardView2!!.startAnimation(reveal)

            cardView2!!.visibility = View.VISIBLE
            cardView1!!.visibility = View.GONE
            tvText!!.setText("Scan QR Code Here")



        }
        cardView2.setOnClickListener{
            cameraTask()
        }
        enter.setOnClickListener {
            tvText!!.startAnimation(reveal)
            cardView1!!.startAnimation(reveal)
            cardView2!!.startAnimation(hide)

            cardView2!!.visibility = View.GONE
            cardView1!!.visibility = View.VISIBLE
            tvText!!.setText("Enter QR Code Here")
        }
        enterHere.setOnClickListener {
            if (edtCode!!.text.toString().isNullOrEmpty()){
                Toast.makeText(this, "Please Enter Code", Toast.LENGTH_SHORT).show()
            }else{
                var value = edtCode.text.toString()
                Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hasCameraAccess():Boolean{
        return EasyPermissions.hasPermissions(this,android.Manifest.permission.CAMERA)
    }

    private fun cameraTask(){
        if (hasCameraAccess()){
            var qrScanner = IntentIntegrator(this)
            qrScanner.setPrompt("Scan a QR Code")
            qrScanner.setCameraId(0)
            qrScanner.setOrientationLocked(true)
            qrScanner.setBeepEnabled(true)
            qrScanner.captureActivity = CaptureActivity::class.java
            qrScanner.initiateScan()

        }else{
            EasyPermissions.requestPermissions(this,
                "This app needs access to your camera",
                123,
                android.Manifest.permission.CAMERA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if (result !=null){
            if (result.contents ==null){
                Toast.makeText(this, "Result not found", Toast.LENGTH_SHORT).show()
                edtCode.setText("")
            }else{
                try {
                    cardView1!!.startAnimation(reveal)
                    cardView2!!.startAnimation(hide)
                    cardView2.visibility = View.GONE
                    cardView1.visibility = View.VISIBLE
                    edtCode.setText(result.contents.toString())

                }catch (exception : JSONException){
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    edtCode.setText("")
                }

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode ==AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this@MainActivity,perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }

}