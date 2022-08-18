package com.mrpark1.meparkpartner.ui.qr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityQrBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.util.Constants

class QrActivity : BaseActivity<ActivityQrBinding>(R.layout.activity_qr) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Glide.with(this).load(getQrCodeBitmap(Constants.selectedParkingLot.ParkingLN)).into(binding.ivQr)



        binding.ivQr.setOnClickListener{
            //복사기능
//            val clipBoardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//            clipBoardManager.setPrimaryClip(ClipData.newPlainText("qr",Constants.selectedParkingLot.QRCodeUrl))
//            Toast.makeText(this,"QR코드 url 복사 완료!",Toast.LENGTH_SHORT).show()

            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(Constants.selectedParkingLot.QRCodeUrl)
            })
        }
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
}