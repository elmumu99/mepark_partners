package com.mrpark1.meparkpartner.util

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.exifinterface.media.ExifInterface
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ImageUtil @Inject constructor(@ApplicationContext private val appContext: Context) {
    //이미지 변환 관련 유틸

    //uri에서 이미지를 bitmap으로 받아옴
    private fun uriToBitmap(uri: Uri): Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(appContext.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(appContext.contentResolver, uri)
        }

    //bitmap을 base64로 변환
    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray: ByteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP).trim()
    }

    //위 작업을 동시에 수행
    fun uriToBase64(uri: Uri?): String {
        if (uri == null) return ""
        return bitmapToBase64(uriToBitmap(uri))
    }

    fun uriResizeToBase64(uri: Uri?): String {
        if (uri == null) return ""
        try{
            val base64String =  bitmapToBase64(resizeBitmapFormUri(uri)!!)
            return base64String
        }catch (e: Exception){return ""}
    }
    // 최적화 bitmap 반환
    fun resizeBitmapFormUri(uri: Uri): Bitmap? {
        val input = appContext.contentResolver.openInputStream(uri)

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        var bitmap: Bitmap?
        BitmapFactory.Options().run {
            inSampleSize = calculateInSampleSize(options)
            bitmap = BitmapFactory.decodeStream(input, null, this)
        }

        // 아래에 회전된 이미지 되돌리기에서 다시 언급할게용 :)
        bitmap = bitmap?.let {
            rotateImageIfRequired(appContext, bitmap!!, uri)
        }

        input?.close()

        return bitmap
    }

    // 리샘플링 값 계산
    fun calculateInSampleSize(options: BitmapFactory.Options): Int {
        var height = options.outHeight
        var width = options.outWidth

        var inSampleSize = 1

        while (height > MAX_HEIGHT || width > MAX_WIDTH) {
            height /= 2
            width /= 2
            inSampleSize *= 2
        }

        return inSampleSize
    }


    fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(uri) ?: return null

        val exif = if (Build.VERSION.SDK_INT > 23) {
            ExifInterface(input)
        } else {
            ExifInterface(uri.path!!)
        }

        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            else -> bitmap
        }
    }

    fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
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

    companion object {
        // 원하는 크기로 지정해주세요!
        const val MAX_WIDTH = 100
        const val MAX_HEIGHT = 100
    }
}