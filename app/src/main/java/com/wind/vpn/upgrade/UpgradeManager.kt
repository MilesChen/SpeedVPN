package com.wind.vpn.upgrade

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.ui.DAY_TIME
import com.github.kr328.clash.service.util.sendBroadcastSelf
import com.wind.vpn.WindGlobal
import com.wind.vpn.data.DomainManager
import com.wind.vpn.data.RequestManager
import com.wind.vpn.data.bean.ClientLastVersion
import com.wind.vpn.util.getFileMD5
import com.wind.vpn.util.getMd5
import java.io.File

const val APK_NAME = "wind.apk"

class UpgradeManager {
    val appStore = UiStore(Global.application)

    @Synchronized
    suspend fun checkVersion() {
        val lastVersions = DomainManager.ossBean.clientLastVersion
        lastVersions?.let {
            var tartVersion: ClientLastVersion? = findTargetVersion(lastVersions)
            tartVersion?.let {
                if (!tartVersion.isValid()) {
                    return
                }
                try {
                    val path = Global.application.cacheDir.absolutePath
                    val apkFile = File("$path/$APK_NAME")
                    if (hasValidCache(apkFile, tartVersion.md5!!)) {
                        senBroadCast()
                    } else {
                        val newFile = RequestManager.startDownload(apkFile, tartVersion.targetUrl!!)
                        newFile?.let {
                            if (hasValidCache(newFile, tartVersion.md5!!)) {
                                senBroadCast()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

    fun shouldPromptNewVersion() {
        if (System.currentTimeMillis() - appStore.lastPromptTime < DAY_TIME) {
            return
        }
        val lastVersions = DomainManager.ossBean.clientLastVersion
        lastVersions?.let {
            var tartVersion: ClientLastVersion? = findTargetVersion(lastVersions)
            tartVersion?.let {
                val path = Global.application.cacheDir.absolutePath
                val apkFile = File("$path/$APK_NAME")
                if (hasValidCache(apkFile, tartVersion.md5!!)) {
                    senBroadCast()
                }
            }
        }
    }

    private fun senBroadCast() {
        if (System.currentTimeMillis() - appStore.lastPromptTime < DAY_TIME) {
            return
        }
        Global.application.sendBroadcastSelf(Intent(Intents.ACTION_HAS_NEW_VERSION))
    }

    fun getApkFile(): File {
        val path = Global.application.cacheDir.absolutePath
        return File("$path/$APK_NAME")
    }

    private fun findTargetVersion(lastVersions: List<ClientLastVersion>): ClientLastVersion? {
        var tartVersion: ClientLastVersion? = null
        for (vInfo in lastVersions) {
            if (vInfo.clientType == WindGlobal.CLIENT_TYPE && vInfo.versionCode > WindGlobal.vCode) {
                tartVersion = vInfo
                break;
            }
        }
        return tartVersion
    }

}


private fun hasValidCache(apkFile: File, targetMd5: String): Boolean {
    return apkFile.exists() && apkFile.getFileMD5() == targetMd5 && isSignOk(apkFile)
}

private fun isSignOk(apkFile: File):Boolean {
    val apkPackageInfo = Global.application.packageManager.getPackageArchiveInfo(
        apkFile.absolutePath,
        PackageManager.GET_SIGNATURES or PackageManager.GET_SIGNING_CERTIFICATES
    ) ?: return false
    val selfPackageInfo = Global.application.packageManager.getPackageInfo(Global.application.packageName,
        PackageManager.GET_SIGNATURES or PackageManager.GET_SIGNING_CERTIFICATES
    )
    var isVersionOk = true;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        isVersionOk = apkPackageInfo.longVersionCode > selfPackageInfo.longVersionCode
    } else {
        isVersionOk = apkPackageInfo.versionCode > selfPackageInfo.versionCode
    }
    val apkMd5 = getApkMd5(apkPackageInfo)
    val selfMd5 = getApkMd5(selfPackageInfo)
    Log.d("chenchao", "apkMd5:$apkMd5 selfMd5:$selfMd5 isVersionOk:$isVersionOk")
    return apkMd5 == selfMd5 && isVersionOk
}


private fun getApkMd5(packageInfo: PackageInfo?):String {
    packageInfo?.let {
        if (Build.VERSION.SDK_INT >= 28) {
            val signatures = packageInfo.signingInfo.apkContentsSigners
            signatures?.let {
                return signatures[0].toByteArray().getMd5()
            }
        } else {
            return packageInfo.signatures[0].toByteArray().getMd5()
        }
    }
    return ""
}
