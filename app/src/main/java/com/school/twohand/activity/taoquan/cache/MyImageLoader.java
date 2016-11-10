package com.school.twohand.activity.taoquan.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

/** 使用在SelectTaoquanBgActivity里，解决加载多张图片卡顿的问题（使用内存缓存）
 * Created by yang on 2016/9/26 0026.
 */
public class MyImageLoader {

    Context context;
    LruCacheUtil lruCacheUtil;

    public MyImageLoader(Context context){
        this.context = context;
        lruCacheUtil = new LruCacheUtil();
    }

//    //通过url路径把网络图片展现在ImageView上面，并进行缓存处理
//    public void showImageFromResource(int resourceId,ImageView iv){
//        //从内存中取,若取到，显示，没取到再去资源文件中取，取到，显示
//        getBitmapFromLruCache(resourceId,iv);
//
//    }


    //从内存中取,若取到，显示，没取到再去资源文件中取，取到，显示
    public boolean getBitmapFromLruCache(int resourceId,ImageView iv){
        //先从LruCache中取到数据，若取到，显示在iv上
        Bitmap bitmap = lruCacheUtil.readFromLruCache(resourceId);
        if(bitmap!=null){
            //取到图片，直接显示
            iv.setImageBitmap(bitmap);
            Log.i("MyImageLoader", "MyImageLoader: getBitmapFromLruCache:内存缓存取：取到");
            return true;
        }else{
            getImageFromResource(resourceId,iv);
            Log.i("MyImageLoader", "MyImageLoader: getBitmapFromLruCache:内存缓存取：没有取到");
            return false;
        }
    }

    //从资源文件中获取，加载到内存缓存中
    public void getImageFromResource(int resourceId,ImageView iv){
        Bitmap bitmap = BitmapFactory.decodeResource(this.context.getResources(),resourceId);
        iv.setImageBitmap(bitmap);//显示在ImageView上面
        //加到内存缓存中
        lruCacheUtil.writeToLruCache(resourceId,bitmap);
        Log.i("MyImageLoader", "MyImageLoader: getBitmapFromLruCache:资源文件中取：取到并加到内存缓存");
    }

}
