package com.school.twohand.activity.taoquan.cache;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/** 内存缓存(??在当前activity有效，若当前activity被销毁，则内存缓存也被清除)
 *      1)创建对象
 *      2)写入数据
 *      3)读取数据
 * Created by yang on 2016/9/26 0026.
 */
public class LruCacheUtil {
    //key:图片的资源Id,value:Bitmap
    LruCache<Integer,Bitmap> lruCache;

    public LruCacheUtil(){
        int maxSize = (int)Runtime.getRuntime().maxMemory()/8;//设置应用程序允许所占最大内存为当前总内存的1/8
        Log.i("MyImageLoader", "maxSize#####"+maxSize);
        //创建LruCache对象，参数为应用程序所允许占的最大内存
        lruCache = new LruCache<Integer,Bitmap>(maxSize){
            //
//            @Override
//            protected int sizeOf(Integer key, Bitmap value) {
//                //默认返回：内存缓存中元素个数
//                //一般这里我们会重写该方法，使其返回的是某个元素的大小
//                Log.i("MyImageLoader", "sizeOf-----"+value.getByteCount());
//                return value.getByteCount();
//            }
        };
    }

    //写入数据：key-value
    public void writeToLruCache(Integer key,Bitmap bitmap){
        if(bitmap!=null) {//判断从网络或硬盘内存中获取的数据不为null才能执行，否则会报空指针异常
            lruCache.put(key, bitmap);
        }
    }

    //读取数据(根据key获得value，这里的value是Bitmap)
    public Bitmap readFromLruCache(Integer key){
//        Log.i("MyImageLoader", "readFromLruCache: ******"+lruCache);
        return lruCache.get(key);
    }



}
