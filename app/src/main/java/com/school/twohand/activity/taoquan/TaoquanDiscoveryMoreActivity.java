package com.school.twohand.activity.taoquan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.ultra.CustomUltraRefreshHeader;
import com.school.twohand.ultra.UltraRefreshListView;
import com.school.twohand.ultra.UltraRefreshListener;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * 淘圈“发现”界面点击更多后跳转到的页面
 */
public class TaoquanDiscoveryMoreActivity extends AppCompatActivity implements UltraRefreshListener {

    String title = "";
    CommonAdapter<AmoyCircle> circlesAdapter;
    TextView tv_title;
    UltraRefreshListView mLv;
    ImageView iv_return;
    List<AmoyCircle> amoyCircles = new ArrayList<>();
    int pageNo = 1; //默认从第一页开始
    int orderFlag = 1; // 1表示"不可错过的淘圈"，2表示"每日精选"

    private PtrClassicFrameLayout mPtrFrame; //PtrClassicFrameLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taoquan_discovery_more);

        initView();
        init();
        initData();
    }



    void initView(){
        tv_title = (TextView) findViewById(R.id.tv_taoquan_discovery_name);
        mLv = (UltraRefreshListView) findViewById(R.id.ultra_lv);
        iv_return = (ImageView) findViewById(R.id.iv_taoquan_discovery_return);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        //设置标题
        tv_title.setText(title);

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.ultra_ptr);
        //创建我们的自定义头部视图
        CustomUltraRefreshHeader header = new CustomUltraRefreshHeader(this);

        //设置头部视图
        mPtrFrame.setHeaderView(header);

        //设置视图修改的回调，因为我们的CustomUltraRefreshHeader实现了PtrUIHandler
        mPtrFrame.addPtrUIHandler(header);

        //设置数据刷新的会回调，因为UltraRefreshListView实现了PtrHandler
        mPtrFrame.setPtrHandler(mLv);

        //设置数据刷新回调接口
        mLv.setUltraRefreshListener(this);
    }

    //初始化，用来判断该页面是显示"不可错过的淘圈"还是"每日精选"等等
    void init(){
        switch (title){
            case "不可错过的淘圈":
                orderFlag=1;
                break;
            case "每日精选":
                orderFlag=2;
                break;
            case "猜你喜欢":
                orderFlag=3;
                break;
            case "高冷地带":
                orderFlag=4;
                break;
        }
    }

    void initData(){
        //orderFlag==1表示 不可错过的淘圈:按人气排序（降序）
        //orderFlag==2表示 每日精选:按时间排序（降序）
        //orderFlag==3表示 猜你喜欢:按时间排序（升序）(暂定)
        //orderFlag==4表示 高冷地带:按人气排序（升序）
        getData();
    }

    void getData(){
        String url = NetUtil.url+"QueryCirclesByServlet";
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("orderFlag",orderFlag+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",10+"");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result!=null){
                    //根据不同的数据源，显示不同的淘圈信息在ListView上面
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<AmoyCircle>>(){}.getType();
                    List<AmoyCircle> newAmoyCircles = gson.fromJson(result, type);
                    amoyCircles.clear();
                    amoyCircles.addAll(newAmoyCircles);

                    //设置ListView的数据源
                    if(circlesAdapter==null){
                        circlesAdapter = new CommonAdapter<AmoyCircle>(TaoquanDiscoveryMoreActivity.this, amoyCircles, R.layout.taoquan_mine_item) {
                            @Override
                            public void convert(ViewHolder viewHolder, AmoyCircle amoyCircle, int position) {
                                //设置淘圈名
                                TextView taoquan_mine_item_name = viewHolder.getViewById(R.id.taoquan_mine_item_name);
                                taoquan_mine_item_name.setText(amoyCircle.getCircleName());

                                //设置名下面的描述
                                TextView taoquan_mine_item_describe = viewHolder.getViewById(R.id.taoquan_mine_item_describe);
                                taoquan_mine_item_describe.setText("快进入淘圈看看吧~");

                                //设置淘圈人气
                                TextView taoquan_mine_item_popularity = viewHolder.getViewById(R.id.taoquan_mine_item_popularity);
                                taoquan_mine_item_popularity.setText("");

                                //设置淘圈头像
                                ImageView taoquan_image = viewHolder.getViewById(R.id.taoquan_mine_item_image);
                                String url = NetUtil.imageUrl + amoyCircle.getCircleImageUrl();

                                //设置图片样式
                                ImageOptions imageOptions = new ImageOptions.Builder()
                                        .setFailureDrawableId(R.mipmap.upload_circle_image)
                                        .setLoadingDrawableId(R.mipmap.upload_circle_image)
                                        .setCrop(true).build();          //是否裁剪？
                                x.image().bind(taoquan_image, url, imageOptions);
                            }
                        };
                        mLv.setAdapter(circlesAdapter);
                        //设置Item点击事件
                        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //lv,item,点击的item所在的位置（从0开始）,item的id
                                if(position>=0&&position<parent.getCount()-1) { //当出现底部布局的时候，底部布局也占一个位置
                                    AmoyCircle amoyCircle = (AmoyCircle) parent.getItemAtPosition(position);
                                    Intent intent = new Intent(TaoquanDiscoveryMoreActivity.this, EachTaoquanActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("amoyCircle", amoyCircle);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        });
                    }else{
                        circlesAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }

    //加载更多数据
    void loadMoreData(){
        String url = NetUtil.url+"QueryCirclesByServlet";
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("orderFlag",orderFlag+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",6+"");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result!=null){
                    //根据不同的数据源，显示不同的淘圈信息在ListView上面
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<AmoyCircle>>(){}.getType();
                    List<AmoyCircle> newAmoyCircles = gson.fromJson(result, type);
                    if(newAmoyCircles.size()==0){//服务器没有返回新的数据
                        pageNo--; //下一次继续加载这一页
                        //mLv.completeLoad();//没获取到数据也要改变界面
                        return;
                    }
                    //amoyCircles.clear();   //加载更多不能清空
                    amoyCircles.addAll(newAmoyCircles);
                    //设置ListView的数据源
                    if(circlesAdapter==null){
                        circlesAdapter = new CommonAdapter<AmoyCircle>(TaoquanDiscoveryMoreActivity.this, amoyCircles, R.layout.taoquan_mine_item) {
                            @Override
                            public void convert(ViewHolder viewHolder, AmoyCircle amoyCircle, int position) {
                                //设置淘圈名
                                TextView taoquan_mine_item_name = viewHolder.getViewById(R.id.taoquan_mine_item_name);
                                taoquan_mine_item_name.setText(amoyCircle.getCircleName());

                                //设置名下面的描述
                                TextView taoquan_mine_item_describe = viewHolder.getViewById(R.id.taoquan_mine_item_describe);
                                taoquan_mine_item_describe.setText("快进入淘圈看看吧~");

                                //设置淘圈人气
                                TextView taoquan_mine_item_popularity = viewHolder.getViewById(R.id.taoquan_mine_item_popularity);
                                taoquan_mine_item_popularity.setText("");

                                //设置淘圈头像
                                ImageView taoquan_image = viewHolder.getViewById(R.id.taoquan_mine_item_image);
                                String url = NetUtil.imageUrl + amoyCircle.getCircleImageUrl();

                                //设置图片样式
                                ImageOptions imageOptions = new ImageOptions.Builder()
                                        .setFailureDrawableId(R.mipmap.upload_circle_image)
                                        .setLoadingDrawableId(R.mipmap.upload_circle_image)
                                        .setCrop(true).build();          //是否裁剪？
                                x.image().bind(taoquan_image, url, imageOptions);
                            }
                        };
                        mLv.setAdapter(circlesAdapter);
                        //设置Item点击事件
                        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //lv,item,点击的item所在的位置（从0开始）,item的id
                                if(position>=0&&position<parent.getCount()-1) { //当出现底部布局的时候，底部布局也占一个位置
                                    AmoyCircle amoyCircle = (AmoyCircle) parent.getItemAtPosition(position);
                                    Intent intent = new Intent(TaoquanDiscoveryMoreActivity.this, EachTaoquanActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("amoyCircle", amoyCircle);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        });
                    }else{
                        circlesAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        pageNo = 1; //每次刷新，让pageNo变成初始值1
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLv.refreshComplete();
                initData(); //在再次获取数据并刷新ListView
            }
        },1000);
    }

    //上拉加载
    @Override
    public void addMore() {
        pageNo++;
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreData();
                mLv.refreshComplete();
            }
        },1000);
    }



}
