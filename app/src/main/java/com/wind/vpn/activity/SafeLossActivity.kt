package com.wind.vpn.activity

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.ActSafeLossBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SafeLossActivity:BaseActivity() {
    private val REQUEST_CODE: Int = 1
    private lateinit var binding:ActSafeLossBinding
    override fun genCustomView(): View {
        binding = ActSafeLossBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.btnSaveQrcode.setOnClickListener{
            saveImage()
        }
    }

    private fun saveImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE);
        } else {
            saveBitmap()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveBitmap()
        }
    }

    private fun saveBitmap() {
        launch(Dispatchers.IO) {
            val result = saveBitmapAsync()
            withContext(Dispatchers.Main) {
                showToast(if (result) getString(R.string.toast_save_suc) else getString(R.string.toast_error))
            }
        }
    }

    private fun saveBitmapAsync():Boolean {
        try {
            val ips = assets.open("icon_qrcode_net.png")
            val bitmap = BitmapFactory.decodeStream(ips)
            //获取要保存的图片的位图
            //创建一个保存的Uri
            val values = ContentValues()
            //设置图片名称
            values.put(
                MediaStore.Images.Media.DISPLAY_NAME,
                System.currentTimeMillis().toString() + "code.png"
            )
            //设置图片格式
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            //设置图片路径
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            val saveUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (TextUtils.isEmpty(saveUri.toString())) {
                Toast.makeText(this, "保存失败！", Toast.LENGTH_SHORT).show()
                return false
            }
            val outputStream = contentResolver.openOutputStream(saveUri!!)
            //将位图写出到指定的位置
            //第一个参数：格式JPEG 是可以压缩的一个格式 PNG 是一个无损的格式
            //第二个参数：保留原图像90%的品质，压缩10% 这里压缩的是存储大小
            //第三个参数：具体的输出流
            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_safe_loss
    }
}