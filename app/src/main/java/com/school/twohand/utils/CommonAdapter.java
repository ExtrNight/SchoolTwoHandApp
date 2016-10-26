package com.school.twohand.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/10/13 0013.
 */
public abstract class CommonAdapter<T> extends BaseAdapter{
    private Context context;//上下文
    private List<T> data;//数据源
    private int itemResId;//item资源id

    /**
     * 构造函数用来传递 上下文，数据源，item资源id
     */
    public CommonAdapter(Context context,List<T> data,int itemResId) {
        this.context = context;
        this.data = data;
        this.itemResId = itemResId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //将convertView当作参数传到viewHolder中初始化，如果convertView不为nullViewHolder中就不重新创建convertView
        ViewHolder viewHolder = ViewHolder.getViewHolder(context,itemResId,convertView,parent);
        //convert中可以对item中的控件赋值
        convert(viewHolder,data.get(position),position);

        return viewHolder.getconvertView();
    }


    public abstract void convert(ViewHolder viewHolder , T t ,int position);

}
