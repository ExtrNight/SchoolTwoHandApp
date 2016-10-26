package com.school.twohand.activity.taoquan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.ultra.CustomUltraRefreshHeader;
import com.school.twohand.ultra.UltraRefreshListView;
import com.school.twohand.ultra.UltraRefreshListener;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.MapDistance;
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
 * “附近的淘圈”点击“更多”后的页面，包含有附近的所有淘圈
 */
public class TaoquanNearbyMoreActivity extends AppCompatActivity implements UltraRefreshListener{

    ImageView iv_return;
    LinearLayout ll_create_circle;
    UltraRefreshListView URlv;
    private PtrClassicFrameLayout mPtrFrame; //PtrClassicFrameLayout

    double myLatitude; //当前位置的纬度
    double myLongitude; //当前位置的经度
    CommonAdapter<AmoyCircle> circlesAdapter;
    List<AmoyCircle> amoyCircles = new ArrayList<>();
    int pageNo = 1; //默认从第一页开始

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taoquan_nearby_more);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        iv_return = (ImageView) findViewById(R.id.iv_taoquan_nearby_return);
        ll_create_circle = (LinearLayout) findViewById(R.id.ll_create_circle);
        URlv = (UltraRefreshListView) findViewById(R.id.ultra_taoquan_nearby);

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.ultra_ptr);
        //创建我们的自定义头部视图
        CustomUltraRefreshHeader header = new CustomUltraRefreshHeader(this);

        //设置头部视图
        mPtrFrame.setHeaderView(header);

        //设置视图修改的回调，因为我们的CustomUltraRefreshHeader实现了PtrUIHandler
        mPtrFrame.addPtrUIHandler(header);

        //设置数据刷新的会回调，因为UltraRefreshListView实现了PtrHandler
        mPtrFrame.setPtrHandler(URlv);

        //设置数据刷新回调接口
        URlv.setUltraRefreshListener(this);
    }

    private void initData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null) {
            myLatitude = bundle.getDouble("myLatitude"); //纬度
            myLongitude = bundle.getDouble("myLongitude"); //经度
        }
        getData();
    }

    //获取数据,用于第一次获取数据
    private void getData(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryCirclesByLaLuServlet");
        requestParams.addQueryStringParameter("latitude",myLatitude+"");
        requestParams.addQueryStringParameter("longitude",myLongitude+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");//第一页
        requestParams.addQueryStringParameter("pageSize",6+""); //显示6个
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result==null){
                   return;
                }
                Gson gson = new Gson();
                Type type = new TypeToken<List<AmoyCircle>>(){}.getType();
                List<AmoyCircle> newAmoyCircles = gson.fromJson(result,type);
                amoyCircles.clear();
                amoyCircles.addAll(newAmoyCircles);
                if(circlesAdapter==null){
                    circlesAdapter = new CommonAdapter<AmoyCircle>(TaoquanNearbyMoreActivity.this,amoyCircles,R.layout.taoquan_mine_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, AmoyCircle amoyCircle, int position) {
                            //设置淘圈名
                            TextView taoquan_mine_item_name = viewHolder.getViewById(R.id.taoquan_mine_item_name);
                            taoquan_mine_item_name.setText(amoyCircle.getCircleName());

                            //设置名下面的描述
                            TextView taoquan_mine_item_describe = viewHolder.getViewById(R.id.taoquan_mine_item_describe);
                            taoquan_mine_item_describe.setText("人气 "+amoyCircle.getCircleNumber());

                            //设置淘圈与当前位置的距离
                            TextView taoquan_mine_item_popularity = viewHolder.getViewById(R.id.taoquan_mine_item_popularity);
                            taoquan_mine_item_popularity.setTextColor(Color.BLACK);
                            taoquan_mine_item_popularity.setTextSize(12);
                            double realDistance = MapDistance.getDistance(myLongitude,myLatitude,amoyCircle.getCircleLongitude(),amoyCircle.getCircleLatitude());
                            String realDistanceStr = realDistance+"";
                            if(realDistance>=0.001 && realDistance<1){ //1000米以内,大于1米 0.291154-->取出291，转化为int
                                //String distanceStr = (String.valueOf(realDistance*1000)).substring(0,3);
                                String distanceStr = realDistanceStr.substring(realDistanceStr.indexOf(".")+1,realDistanceStr.indexOf(".")+4);
                                int distance = Integer.parseInt(distanceStr);
                                taoquan_mine_item_popularity.setText(distance+" m");
                            }else if (realDistance>=1){ //大于等于1km： 1.25864 ->1.2，转化为double
                                String distanceStr = (String.valueOf(realDistance)).substring(0,3);
                                double distance = Double.parseDouble(distanceStr);
                                taoquan_mine_item_popularity.setText(distance+" km");
                            } else if(realDistance<0.001){ //距离在1米以内
                                taoquan_mine_item_popularity.setText("1 m 以内");
                            }

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
                    URlv.setAdapter(circlesAdapter);
                    URlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(position>=0&&position<parent.getCount()-1) { //当出现底部布局的时候，底部布局也占一个位置
                                AmoyCircle amoyCircle = (AmoyCircle) parent.getItemAtPosition(position);
                                Intent intent = new Intent(TaoquanNearbyMoreActivity.this, EachTaoquanActivity.class);
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
    private void loadMoreData(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryCirclesByLaLuServlet");
        requestParams.addQueryStringParameter("latitude",myLatitude+"");
        requestParams.addQueryStringParameter("longitude",myLongitude+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");//第一页
        requestParams.addQueryStringParameter("pageSize",6+""); //最多显示2个
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result==null){
                    return;
                }
                Gson gson = new Gson();
                Type type = new TypeToken<List<AmoyCircle>>(){}.getType();
                List<AmoyCircle> newAmoyCircles = gson.fromJson(result,type);
                if(newAmoyCircles.size()==0){//服务器没有返回新的数据
                    pageNo--; //下一次继续加载这一页
                    //mLv.completeLoad();//没获取到数据也要改变界面
                    return;
                }
                //amoyCircles.clear();  //加载更多不能清空
                amoyCircles.addAll(newAmoyCircles);
                if(circlesAdapter==null){
                    circlesAdapter = new CommonAdapter<AmoyCircle>(TaoquanNearbyMoreActivity.this,amoyCircles,R.layout.taoquan_mine_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, AmoyCircle amoyCircle, int position) {
                            //设置淘圈名
                            TextView taoquan_mine_item_name = viewHolder.getViewById(R.id.taoquan_mine_item_name);
                            taoquan_mine_item_name.setText(amoyCircle.getCircleName());

                            //设置名下面的描述
                            TextView taoquan_mine_item_describe = viewHolder.getViewById(R.id.taoquan_mine_item_describe);
                            taoquan_mine_item_describe.setText("人气 "+amoyCircle.getCircleNumber());

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
                    URlv.setAdapter(circlesAdapter);
                    URlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(position>=0&&position<parent.getCount()-1) { //当出现底部布局的时候，底部布局也占一个位置
                                AmoyCircle amoyCircle = (AmoyCircle) parent.getItemAtPosition(position);
                                Intent intent = new Intent(TaoquanNearbyMoreActivity.this, EachTaoquanActivity.class);
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

    private void initEvent(){
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_create_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(TaoquanNearbyMoreActivity.this,CreateTaoquanActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRefresh() {
        pageNo = 1; //每次刷新，让pageNo变成初始值1
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                URlv.refreshComplete();
                initData(); //在再次获取数据并刷新ListView
            }
        },1000);
    }

    @Override
    public void addMore() {
        pageNo++;
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreData();
                URlv.refreshComplete();
            }
        },1000);
    }
}
