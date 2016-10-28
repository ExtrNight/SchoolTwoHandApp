package com.school.twohand.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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

    public ImageView iv_return;  //返回键
    public ImageView iv_share;   //分享
    public ImageView iv_search;  //搜索
    public ImageView iv_exit;     //退出淘圈

    public ImageView iv_taoquan_head;          //淘圈头像
    public TextView tv_taoquan_name;          //淘圈名
    public TextView tv_goods_number;     //淘圈发布数
    public TextView tv_taoquan_popularity;     //淘圈人气

    public LinearLayout LL_order_by_heat;      //“热度”排序
    public LinearLayout LL_order_by_time;      //“时间”排序
    public LinearLayout LL_chat_room;          //“聊天室”
    public LinearLayout LL_taoquan_dynamic;   //“淘圈动态”

    boolean isLoading = false; //是否处于加载状态

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
        //初始化头部控件
        iv_return = (ImageView) headView.findViewById(R.id.iv_each_taoquan_return);
        iv_share = (ImageView) headView.findViewById(R.id.iv_each_taoquan_share);
        iv_search = (ImageView) headView.findViewById(R.id.iv_each_taoquan_search);
        iv_exit = (ImageView) headView.findViewById(R.id.iv_each_taoquan_more);

        iv_taoquan_head = (ImageView) headView.findViewById(R.id.iv_each_taoquan_image);
        tv_taoquan_name = (TextView) headView.findViewById(R.id.tv_each_taoquan_name);
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
        //滚动状态改变会执行
        if(getLastVisiblePosition()==getCount()-1&&isLoading==false){//表示最后一个Item可见，且没有在加载状态
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
        void onLoad();  //上拉加载
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
