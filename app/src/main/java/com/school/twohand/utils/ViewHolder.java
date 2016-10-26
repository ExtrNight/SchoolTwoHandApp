package com.school.twohand.utils;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/10/13 0013.
 */
public class ViewHolder {
    private View convertView;
    private SparseArray<View> sparseArray;///key:int;value:View用来存item中x个控件的view

    /**
     * ViewHolder构造函数
     * @param context
     * @param itemResId
     * @param parent
     */
    private ViewHolder(Context context, int itemResId , ViewGroup parent){
        //解析布局文件，赋值给convertView
        convertView = LayoutInflater.from(context).inflate(itemResId,null);
        //convertView绑定ViewHolder对象
        convertView.setTag(this);
        //初始化SpareArray
        sparseArray = new SparseArray<View>();
    }

    /**
     * 得到ViewHolder
     * @param context 上下文
     * @param itemResId 对应条目的资源文件id
     * @param convertView 复用view
     * @param parent
     * @return
     */
    public static ViewHolder getViewHolder(Context context, int itemResId, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        //如果convertView为空就创建新的，并且将新的Viewholder对象与传来的convertView绑定。不为空就从convertView中取之前绑定的Viewholder对象
        if (convertView == null){
            viewHolder = new ViewHolder(context,itemResId,parent);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return viewHolder;
    }

    /**
     * 复用item中的控件
     * @param resId
     * @param <T>
     * @return
     */
    public <T extends View> T getViewById(int resId){
        //在sparseArray中先找控件
        View view = sparseArray.get(resId);
        //如果convertView中找不到控件，就将找到的控件付到spareArray中
        if (view == null){
            view = convertView.findViewById(resId);
            sparseArray.put(resId,view);
        }
        return (T)view;
    }

    public View getconvertView(){
        return convertView;
    }
}
