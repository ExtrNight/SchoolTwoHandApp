package com.school.twohand.customview;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.school.twohand.schooltwohandapp.R;

/**
 * 单个淘圈页面的ListView
 * Created by yang on 2016/10/28 0028.
 */
public class EachTaoquanListView extends ListView implements AbsListView.OnScrollListener{
    OnLoadChangeListener onLoadChangeListener; //自定义的接口

    private View headView;   //头部布局
    private View footView;  //底部布局
    private ProgressBar footPb;
    private TextView footTv;

    public ImageView iv_taoquan_bg;             //淘圈背景
    public ImageView iv_taoquan_head;           //淘圈头像
    public TextView tv_taoquan_name;            //淘圈名
    public TextView tv_goods_number;            //淘圈发布数
    public TextView tv_taoquan_popularity;     //淘圈人气

    public LinearLayout LL_order_by_heat;      //“热度”排序
    public LinearLayout LL_order_by_time;      //“时间”排序
    public LinearLayout LL_chat_room;          //“聊天室”
    public LinearLayout LL_taoquan_dynamic;   //“淘圈动态”

    boolean isLoading = false;      //是否处于加载状态
    //public int firstVisibleItem;   //第一个显示的Item,不使用了，可通过getFirstVisiblePosition()获得
    //public int totalItemCount;    //Item总数量,不使用了，可通过getCount()获得
    //public int firstItemYToTop;  //头布局相对于顶部的值，{View.getTop(),View相对于它的父控件的top值}
    boolean isCompleteTop = false;      //是否把头部变为完全不透明状态,默认没有

    public EachTaoquanListView(Context context) {
        this(context, null);
    }

    public EachTaoquanListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EachTaoquanListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initHead(context);
        initFoot(context);
        this.setOnScrollListener(this);//这里如果不写的话，是不会监听的，即onScrollStateChanged和onScroll不会执行
    }

    private void initHead(Context context) {
        headView = LayoutInflater.from(context).inflate(R.layout.each_taoquan_head, null);

        iv_taoquan_bg = (ImageView) headView.findViewById(R.id.iv_taoquan_bg);
        iv_taoquan_head = (ImageView) headView.findViewById(R.id.iv_each_taoquan_image);
        tv_taoquan_name = (TextView) headView.findViewById(R.id.tv_each_taoquan_name);
        TextPaint tp = tv_taoquan_name.getPaint();
        tp.setFakeBoldText(true);  //设置中文字体加粗
        tv_goods_number = (TextView) headView.findViewById(R.id.tv_each_taoquan_goodsNum);
        tv_taoquan_popularity = (TextView) headView.findViewById(R.id.tv_each_taoquan_popularity);

        LL_order_by_heat = (LinearLayout) headView.findViewById(R.id.LL_1);
        LL_order_by_time = (LinearLayout) headView.findViewById(R.id.LL_1_time);
        LL_chat_room = (LinearLayout) headView.findViewById(R.id.LL_2);
        LL_taoquan_dynamic = (LinearLayout) headView.findViewById(R.id.LL_3);

        //ListView添加头部
        addHeaderView(headView);
    }

    //初始化底部
    void initFoot(Context context){
        footView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer,null);
        footPb = (ProgressBar) footView.findViewById(R.id.footer_progressbar);
        footTv = (TextView) footView.findViewById(R.id.footer_hint_textview);

        addFooterView(footView);//添加footView
    }

    //实现AbsListView.OnScrollListener接口需要重写的两个方法
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        /**
         * SCROLL_STATE_FLING:开始滚动（包括：手指不动了，但是屏幕还在滚动状态） ==2
         * SCROLL_STATE_TOUCH_SCROLL:正在滚动状态 ==1
         * SCROLL_STATE_IDLE:滚动结束状态（包括：静止状态） ==0
         */
        //滚动状态改变会执行
        if(getLastVisiblePosition()==getCount()-1&&!isLoading){//表示最后一个Item可见，且没有在加载状态
            if(scrollState==OnScrollListener.SCROLL_STATE_TOUCH_SCROLL||scrollState==OnScrollListener.SCROLL_STATE_IDLE){
                //界面改变（开始加载）--》完成刷新--》界面改变（完成加载）
                isLoading = true; //改变加载状态
                changeFootState(); //改变底部界面的状态
                if(onLoadChangeListener!=null){
                    onLoadChangeListener.onLoad();//加载更多数据
                }
            }
        }
//        if(scrollState==OnScrollListener.SCROLL_STATE_FLING&&onLoadChangeListener!=null){
//            if(!isCompleteTop&&getFirstVisiblePosition()!=0){
//                //没有把头部变为全不透明&&第一个可见的Item不是头部
//                Log.i("EachTaoquanListView", "onScrollStateChanged: $$$$$$$$");
//                onLoadChangeListener.onCompleteTop();
//                isCompleteTop = true;  //表示已经将头部变为全不透明
//            }
//        }
        if(scrollState==OnScrollListener.SCROLL_STATE_IDLE&&onLoadChangeListener!=null){ //滑动状态结束
            if(getChildAt(0)!=null){
                if(getChildAt(0).getTop()>=0){
                    onLoadChangeListener.onRestoreTop();//使头部恢复为初始状态
                }
            }
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //不使用了，可通过getFirstVisiblePosition()获得
        //this.firstVisibleItem = firstVisibleItem; //记录第一个可见的Item（并不一定是ListView的第一个Item）
//        this.totalItemCount = totalItemCount;//不使用了，可通过getCount()获得
//        if(getChildAt(0)!=null){
//            this.firstItemYToTop = getChildAt(0).getTop();
//        }
        if(!isCompleteTop&&getFirstVisiblePosition()!=0){
            //没有把头部变为全不透明&&第一个可见的Item不是头部
            onLoadChangeListener.onCompleteTop();
            isCompleteTop = true;  //表示已经将头部变为全不透明
        }else if(getFirstVisiblePosition()==0){
            isCompleteTop = false;   //表示没有将头部变为全透明
        }
    }

    //将事件拦截？？
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        //super.onInterceptTouchEvent(ev)
//        return true;
//    }

    //footView处于不同状态，改变不同的控件状态
    public void changeFootState(){
        if(isLoading){
            //正在加载：ProgressBar显示
            footPb.setVisibility(VISIBLE);
            footTv.setVisibility(GONE);
        }else{
            //没有在加载,进图条隐藏，文本显示
            footPb.setVisibility(GONE);
            footTv.setVisibility(VISIBLE);
        }
    }

    //加载完成
    public void completeLoad(){
        isLoading = false; //加载完成
        changeFootState();
    }

    //定义接口：下拉刷新，上拉加载
    public interface OnLoadChangeListener{
        void onLoad();         //上拉加载
        void onRestoreTop();  //使头部复原，即背景为全透明，控件变为白色
        void onCompleteTop(); //使头部变为全不透明，控件为黑色
    }

    //供其他类实现该接口
    public void setOnLoadChangeListener(OnLoadChangeListener onLoadChangeListener){
        this.onLoadChangeListener = onLoadChangeListener;
    }

    //去掉footView
    public void removeFootViewIfNeed(){
        removeFooterView(footView);
    }


}
