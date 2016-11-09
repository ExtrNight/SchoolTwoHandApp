package com.school.twohand.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.school.twohand.schooltwohandapp.R;

/** 主页面的ListView,只有加载效果
 * Created by yang on 2016/11/8 0008.
 */
public class HomePageListView extends ListView implements AbsListView.OnScrollListener{

    View footView;   //底部布局
    private TextView footTv; //底部布局“查看更多”的TextView
    private BounceProgressBar bounceProgressBar;    //带有跳跃动画的ProgressBar

    OnLoadChangeListener onLoadChangeListener; //自定义的接口
    boolean isLoading = false; //是否处于加载状态

    public HomePageListView(Context context) {
        this(context,null);
    }
    public HomePageListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public HomePageListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initFoot(context);
        this.setOnScrollListener(this);//监听ListView的Scroll状态
    }

    private void initFoot(Context context){
        footView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer,null);
        bounceProgressBar = (BounceProgressBar) footView.findViewById(R.id.footer_progressbar);
        footTv = (TextView) footView.findViewById(R.id.footer_hint_textview);

        addFooterView(footView);//添加footView
    }

    //实现AbsListView.OnScrollListener接口所需要实现的两个方法
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
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
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    //footView处于不同状态，改变不同的控件状态
    public void changeFootState(){
        if(isLoading){
            //正在加载：ProgressBar显示
            bounceProgressBar.setVisibility(VISIBLE);
            footTv.setVisibility(GONE);
        }else{
            //没有在加载,进图条隐藏，文本显示
            bounceProgressBar.setVisibility(GONE);
            footTv.setVisibility(VISIBLE);
            footTv.setText("查看更多");
        }
    }

    //加载完成
    public void completeLoad(){
        isLoading = false; //加载完成
        changeFootState();  //改变控件状态
    }

    //定义接口：下拉刷新，上拉加载
    public interface OnLoadChangeListener{
        void onLoad();  //上拉加载
    }

    //供其他类实现该接口
    public void setOnLoadChangeListener(OnLoadChangeListener onLoadChangeListener){
        this.onLoadChangeListener = onLoadChangeListener;
    }

    public void removeFootViewIfNeed(){
        removeFooterView(footView);
    }


}
