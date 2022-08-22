package com.mrpark1.meparkpartner.ui.qr

import android.Manifest
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityQrBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.CommonDialog
import com.mrpark1.meparkpartner.util.Constants

class QrActivity : BaseActivity<ActivityQrBinding>(R.layout.activity_qr) {

    var downloadid: Long = 0

    private val uri: Uri = Uri.parse(Constants.selectedParkingLot.QRCodeUrl)
    private var dm: DownloadManager? = null

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Glide.with(this).load(getQrCodeBitmap(Constants.selectedParkingLot.ParkingLN)).into(binding.ivQr)

        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        binding.ivQr.setOnClickListener{
            //복사기능
//            val clipBoardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//            clipBoardManager.setPrimaryClip(ClipData.newPlainText("qr",Constants.selectedParkingLot.QRCodeUrl))
//            Toast.makeText(this,"QR코드 url 복사 완료!",Toast.LENGTH_SHORT).show()

//            startActivity(Intent(Intent.ACTION_VIEW).apply {
//                data = uri
//            })
            CommonDialog(this,"QR이미지 다운로드","다운로드 하시겠습니까?","다운로드",true){
                 checkPermissionBeforeDownload()
            }.show()
        }


    }

    private fun checkPermissionBeforeDownload(){
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            download()
        } else {
            // Permission is not granted
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE),
                1001)
        }
    }

    private fun download() {
        dm?.enqueue(
            DownloadManager.Request(uri)
                .setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_MOBILE or
                            DownloadManager.Request.NETWORK_WIFI
                )
                .setTitle("${Constants.selectedParkingLot.Name + "QR"}.jpg")
                .setDescription("QR 이미지")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                        .setDestinationInExternalFilesDir(
//                                applicationContext , Environment.DIRECTORY_DOWNLOADS,   "managerDownload24.mp4"
//                        )
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "managerDownload24"
                )
        )
    }
    fun getQrCodeBitmap(text: String): Bitmap {
        val size = 512 //pixels
        val hints = hashMapOf<EncodeHintType, Int>().also {
            it[EncodeHintType.MARGIN] = 1
        } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1001 -> {  // 1
                if (grantResults.isEmpty()) {  // 2
                    throw RuntimeException("Empty permission result")
                }
                var isSuccess = true
                for(result in grantResults){
                    if(result != PackageManager.PERMISSION_GRANTED) isSuccess = false
                }
                if (isSuccess) {  // 3
                    download()
                } else {
                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) { // 4
                       //User declined, but i can still ask for more
                        requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            1001)
                    } else {
                        //User declined and i can't ask
                        CommonDialog(this,"","권한을 허용해주세요","확인",false).show()  // 5
                    }
                }
            }
        }
    }
}