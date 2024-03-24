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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.ActSafeLossBinding
import com.wind.vpn.data.DomainManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SafeLossActivity:BaseActivity() {
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
        Glide.with(this).load(DomainManager.ossBean.qrCodeUrl).error(R.drawable.icon_qrcode_net).placeholder(R.drawable.icon_qrcode_net).into(binding.ivQrcodeTop)
    }

    private fun saveImage() {
        saveBitmap()
    }


    private fun saveBitmap() {
        saveBitmapAsync()
    }

    private fun saveBitmapAsync() {
        Glide.with(this).asBitmap().load(DomainManager.ossBean.qrCodeUrl).addListener(object:RequestListener<Bitmap>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>,
                isFirstResource: Boolean
            ): Boolean {
                val ips = assets.open("icon_qrcode_net.png")
                val bitmap = BitmapFactory.decodeStream(ips)
                trySave(bitmap)
                return true
            }

            override fun onResourceReady(
                resource: Bitmap,
                model: Any,
                target: Target<Bitmap>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                trySave(resource)
                return true

            }

        }).preload()

    }

    private fun trySave(bitmap: Bitmap) {
        launch(Dispatchers.IO) {
            val result = saveBitmap(bitmap)
            withContext(Dispatchers.Main) {
                showToast(if (result) getString(R.string.toast_save_suc) else getString(R.string.toast_error))
            }
        }

    }

    private fun saveBitmap(bitmap: Bitmap): Boolean {
        try {
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
                return false
            }
            val outputStream = contentResolver.openOutputStream(saveUri!!)
            //将位图写出到指定的位置
            //第一个参数：格式JPEG 是可以压缩的一个格式 PNG 是一个无损的格式
            //第二个参数：保留原图像90%的品质，压缩10% 这里压缩的是存储大小
            //第三个参数：具体的输出流
            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_safe_loss
    }
}