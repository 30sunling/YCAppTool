package com.yc.store.config

import android.app.Application
import android.util.Log
import com.tencent.mmkv.MMKV
import com.yc.appfilelib.AppFileUtils
import com.yc.store.StoreToolHelper
import com.yc.applrudisk.DiskHelperUtils
import com.yc.toolutils.AppToolUtils
import java.io.File


/**
 * @author yangchong
 * email  : yangchong211@163.com
 * time   : 2022/5/11
 * desc   : 初始化配置
 * revise :
 */
object CacheInitHelper {

    private var config : CacheConfig?= null
    private var mmkvPath: String? = null
    private var filePath: String? = null
    private var externalFilePath: String? = null
    private var maxLruSize: Int? = null

    @Synchronized
    fun init(context: Application,config: CacheConfig?) {
        if (config == null){
            this.config = CacheConfig.newBuilder().build()
        }
        StoreToolHelper.instance.setApp(context)
        CacheInitHelper.config = config
        val logFile = config?.logDir
        val extraFile = config?.extraLogDir
        if (logFile == null) {
            val cacheFiles = AppFileUtils.getCachePath(AppToolUtils.getApp())
            filePath = cacheFiles +  File.separator + "ycCache"
            //路径：/data/user/0/你的包名/cache/ycCache
        } else {
            filePath = logFile
        }
        if (extraFile == null) {
            val externalCacheFiles = AppFileUtils.getExternalCachePath(AppToolUtils.getApp())
            externalFilePath = externalCacheFiles +  File.separator + "ycCache"
            //路径：/storage/emulated/0/Android/data/你的包名/cache/ycCache
        } else {
            externalFilePath = logFile
        }
        maxLruSize = config?.maxCacheSize
        Log.d("CacheHelper : " , "file path : $filePath")
        //初始化腾讯mmkv
        mmkvPath = filePath + File.separator + "mmkv"
        //路径：/storage/emulated/0/Android/data/你的包名/cache/ycCache/mmkv
        Log.d("CacheHelper : " , "mmkv path : $mmkvPath")
        MMKV.initialize(mmkvPath)
        //设置disk缓存
        DiskHelperUtils.setBaseCachePath(filePath)
        DiskHelperUtils.setMaxLruSize(maxLruSize ?: 1024)
    }

    /**
     * 获取mmkv设置的缓存路径，建议放到机身内存缓存文件
     * 内部存储，举个例子：
     * file:data/user/0/包名/files
     * cache:/data/user/0/包名/cache
     */
    fun getMmkvPath() : String{
        if (mmkvPath == null || mmkvPath?.length == 0){
            throw NullPointerException("please init store lib at fist")
        }
        return mmkvPath as String
    }

    /**
     * 获取该库机身内存文件的总路径
     * 内部存储，举个例子：
     * file:data/user/0/包名/files
     * cache:/data/user/0/包名/cache
     */
    fun getBaseCachePath() : String{
        if (filePath == null || filePath?.length == 0){
            throw NullPointerException("please init store lib at fist")
        }
        return filePath as String
    }

    /**
     * 获取该库机身外部存储总路径
     * 外部存储根目录，举个例子
     * files:/storage/emulated/0/Android/data/包名/files
     * cache:/storage/emulated/0/Android/data/包名/cache
     */
    fun getExternalCachePath() : String{
        if (externalFilePath == null || externalFilePath?.length == 0){
            throw NullPointerException("please init store lib at fist")
        }
        return externalFilePath as String
    }

    /**
     * 获取最大lru缓存大小
     */
    fun getMaxLruSize() : Int{
        if (maxLruSize == null){
            maxLruSize = 100
        }
        return maxLruSize as Int
    }
}