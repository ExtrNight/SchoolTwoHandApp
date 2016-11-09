package com.school.twohand.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.TextureMapView;
import com.school.twohand.schooltwohandapp.R;

/** 淘圈“发现”页面的ListView
 * Created by yang on 2016/11/1 0001.
 */
public class TaoquanDiscoveryListView extends ListView implements AbsListView.OnScrollListener{

    View headView;   //头部布局
    View footView;   //底部布局
    private TextView footTv; //底部布局“查看更多”的TextView
    private BounceProgressBar bounceProgressBar;    //带有跳跃动画的ProgressBar

    public ImageButton ib;                          //创建淘圈的ImageButton
    public TextView tv_circle_address;            //用户当前所在的位置
    //使用TextureMapView可解决第一次进入Fragment显示地图会闪一下黑屏的问题
    public TextureMapView mMapView;//注：Application里面不能设置android:hardwareAccelerated="true"（硬件加速）
    public MyListView lv_taoquan_nearby;          //附近的淘圈的ListView,(只显示2条数据)
    public TextView tv_taoquan_nearby_more;      //附近的淘圈“更多”的TextView
    public MyGridView gv_nomissed;                //不可错过的淘圈的GridView
    public TextView tv_nomissedMore;              //不可错过的淘圈的“更多”的TextView
    public MyGridView gv_everyday;                //每日精选的淘圈的GridView
    public TextView tv_everydayMore;             //每日精选的淘圈的“更多”的TextView
    public MyGridView gv_guessYouLike;           //猜你喜欢 的GridView
    public TextView tv_guessYouLike;             //猜你喜欢 的“更多”的TextView
    public MyGridView gv_coldZone;               //高冷地带 的GridView
    public TextView tv_coldZone;                 //高冷地带 的“更多”的TextView

    OnLoadChangeListener onLoadChangeListener; //自定义的接口
    boolean isLoading = false; //是否处于加载状态

    public TaoquanDiscoveryListView(Context context) {
        this(context,null);
    }
    public TaoquanDiscoveryListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public TaoquanDiscoveryListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initHead(context);
        initFoot(context);
        this.setOnScrollListener(this);//监听ListView的Scroll状态
    }

    private void initHead(Context context){
        headView = LayoutInflater.from(context).inflate(R.layout.taoquan_discovery_head,null);
        //初始化头部布局的控件
        ib = (ImageButton) headView.findViewById(R.id.ib_createTaoquan);
        tv_circle_address = (TextView) headView.findViewById(R.id.tv_circle_address);
        mMapView = (TextureMapView) headView.findViewById(R.id.bmapView);
        lv_taoquan_nearby = (MyListView) headView.findViewById(R.id.lv_taoquan_nearby);
        tv_taoquan_nearby_more = (TextView) headView.findViewById(R.id.tv_taoquan_nearby_more);
        gv_nomissed = (MyGridView) headView.findViewById(R.id.taoquan_nomissed_gridview);
        tv_nomissedMore = (TextView) headView.findViewById(R.id.tv_taoquan_nomissed_more);
        gv_everyday = (MyGridView) headView.findViewById(R.id.taoquan_everyday_gridview);
        tv_everydayMore = (TextView) headView.findViewById(R.id.tv_taoquan_everyday_more);
        gv_guessYouLike = (MyGridView) headView.findViewById(R.id.taoquan_guessyoulike_gridview);
        tv_guessYouLike = (TextView) headView.findViewById(R.id.tv_taoquan_guessyoulike_more);
        gv_coldZone = (MyGridView) headView.findViewById(R.id.taoquan_coldzone_gridview);
        tv_coldZone = (TextView) headView.findViewById(R.id.tv_taoquan_coldzone_more);

        //给ListView添加头部布局
        addHeaderView(headView);
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
