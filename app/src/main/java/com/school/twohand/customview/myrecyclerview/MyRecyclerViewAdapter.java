package com.school.twohand.customview.myrecyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.school.twohand.activity.taoquan.cache.MyImageLoader;
import com.school.twohand.schooltwohandapp.R;

import java.util.List;

/**
 * Created by young on 2016/10/19.
 * 每个item的点击事件不一致
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter {

    Context context;
    List<Integer> datas;
    MyRecyclerViewItemClickListener myRecyclerViewItemClickListener;

    MyImageLoader myImageLoader;  //自定义的图片加载器，缓存机制

    public MyRecyclerViewAdapter(Context context, List<Integer> datas){
        this.context=context;
        this.datas=datas;
        myImageLoader = new MyImageLoader(context); //new 出MyImageLoader对象
    }

    //创建viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //如果是复用的view，不会再次执行onCreateViewHolder
        Log.i("MyRecyclerViewAdapter", "onCreateViewHolder: ");
        View view= LayoutInflater.from(context).inflate(R.layout.taoquan_background_item,parent,false);
        //设置根布局的点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LlAdapterClick", "onClick: 进入点击事件1");
                if(myRecyclerViewItemClickListener !=null) {
                    Log.i("LlAdapterClick", "onClick: 进入点击事件");
                    //回调自己定义的接口方法
                    myRecyclerViewItemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });

        return new MyLinearViewHolder(view);
    }

    //viewholder中控件赋值
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //一直调用:从第14条开始，复用第一条的viewholder中数据
        MyLinearViewHolder myLinearViewHolder= (MyLinearViewHolder) holder;
        //Log.i("MyRecyclerViewAdapter", "onBindViewHolder: "+position+",tv:"+myLinearViewHolder.tv);
        //控件赋值：设置Bitmap的资源图片
        //myLinearViewHolder.iv_taoquan_bg.setImageResource(datas.get(position));
        //使用Bitmap设置
//        Bitmap bitmap = BitmapFactory.decodeResource(this.context.getResources(),datas.get(position));
//        myLinearViewHolder.iv_taoquan_bg.setImageBitmap(bitmap);
        //缓存机制
        myImageLoader.getBitmapFromLruCache(datas.get(position),myLinearViewHolder.iv_taoquan_bg);

        //获取viewholder对应的view,设置view的tag；
        myLinearViewHolder.itemView.setTag(position);
    }

    //总共的Item的个数
    @Override
    public int getItemCount() {
        return datas.size();
    }

    //保存显示的控件对象
    class MyLinearViewHolder extends RecyclerView.ViewHolder{

        //item中的控件
        ImageView iv_taoquan_bg;

        //传一个view：每个item对应的view
        public MyLinearViewHolder(View itemView) {
            super(itemView);
            Log.i("MyRecyclerViewAdapter", "MyLinearViewHolder: "+itemView);
            //构造方法中控件赋值
            iv_taoquan_bg = (ImageView) itemView.findViewById(R.id.iv_taoquan_bg);

        }

    }

    //赋值给接口
    public void setMyRecyclerViewItemClickListener(MyRecyclerViewItemClickListener myRecyclerViewItemClickListener){
        this.myRecyclerViewItemClickListener = myRecyclerViewItemClickListener;
    }


}
