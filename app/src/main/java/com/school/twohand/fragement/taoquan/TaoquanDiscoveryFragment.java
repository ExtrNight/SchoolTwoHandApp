package com.school.twohand.fragement.taoquan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.taoquan.CreateTaoquanActivity;
import com.school.twohand.activity.taoquan.EachTaoquanActivity;
import com.school.twohand.activity.taoquan.TaoquanDiscoveryMoreActivity;
import com.school.twohand.activity.taoquan.TaoquanNearbyMapActivity;
import com.school.twohand.activity.taoquan.TaoquanNearbyMoreActivity;
import com.school.twohand.customview.MyGridView;
import com.school.twohand.customview.MyListView;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.MapDistance;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/** 淘圈页面的“发现”的fragment
 * Created by yang on 2016/9/28 0028.
 */
public class TaoquanDiscoveryFragment extends Fragment {

    private PtrClassicFrameLayout ptrFrame;
    ImageButton ib;
    MyListView lv_taoquan_nearby;
    TextView tv_taoquan_nearby_more;
    MyGridView gv_nomissed;
    TextView tv_nomissedMore;
    MyGridView gv_everyday;
    TextView tv_everydayMore;

    TextView tv_circle_address;
    double myLatitude; //当前位置的纬度
    double myLongitude; //当前位置的经度

    //使用TextureMapView可解决第一次进入Fragment显示地图会闪一下黑屏的问题
    TextureMapView mMapView = null;//注：Application里面不能设置android:hardwareAccelerated="true"（硬件加速）
    BaiduMap mBaiduMap;  //获取BaiduMap对象
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());//在使用SDK各组件之前初始化context信息，传入ApplicationContext
        View v = inflater.inflate(R.layout.taoquan_discovery_fragment,null);

        initView(v);
        initData();
        initEvent();

        mLocationClient = new LocationClient(getActivity().getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();

        mLocationClient.start();  //开始定位

        return v;
    }

    void initView(View v){
        ptrFrame = (PtrClassicFrameLayout) v.findViewById(R.id.ultra_ptr_frame);
        ib = (ImageButton) v.findViewById(R.id.ib_createTaoquan);
//        ib.setFocusable(true);
//        ib.setFocusableInTouchMode(true);

        //获取地图控件引用
        mMapView = (TextureMapView) v.findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();  //获取地图控制器
        mBaiduMap.getUiSettings().setAllGesturesEnabled(false); //设置禁止任何手势

        tv_circle_address = (TextView) v.findViewById(R.id.tv_circle_address);
        lv_taoquan_nearby = (MyListView) v.findViewById(R.id.lv_taoquan_nearby);
        tv_taoquan_nearby_more = (TextView) v.findViewById(R.id.tv_taoquan_nearby_more);
        gv_nomissed = (MyGridView) v.findViewById(R.id.taoquan_nomissed_gridview);
        tv_nomissedMore = (TextView) v.findViewById(R.id.tv_taoquan_nomissed_more);
        gv_everyday = (MyGridView) v.findViewById(R.id.taoquan_everyday_gridview);
        tv_everydayMore = (TextView) v.findViewById(R.id.tv_taoquan_everyday_more);
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        //int span=1000;
        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    void initData(){
        //设置“不可错过的淘圈”的GridView，参数1表示按人气排序
        getGridViewData(1,gv_nomissed);

        //设置“每日精选的淘圈”的GridView，参数2表示按淘圈创建时间排序
        getGridViewData(2,gv_everyday);
    }

    //获取“不可错过的淘圈”等等的GridView的数据源并显示,需要的是requirement和GridView对象,显示6个
    void getGridViewData(int orderFlag, final MyGridView myGridView){
        String url = NetUtil.url+"QueryCirclesByServlet";
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("orderFlag",orderFlag+"");
        requestParams.addQueryStringParameter("pageNo",1+"");
        requestParams.addQueryStringParameter("pageSize",6+"");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<AmoyCircle>>(){}.getType();
                List<AmoyCircle> amoyCircles = gson.fromJson(result,type);

                CommonAdapter<AmoyCircle> circlesAdapter = null;
                //设置GridView的数据源
                if(circlesAdapter==null){
                    circlesAdapter = new CommonAdapter<AmoyCircle>(getActivity(), amoyCircles,R.layout.taoquan_gridview_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, AmoyCircle amoyCircle, int position) {
                            //设置淘圈头像
                            ImageView iv_nomissed = viewHolder.getViewById(R.id.taoquan_gridview_item_image);
                            String url = NetUtil.imageUrl+ amoyCircle.getCircleImageUrl();
                            //设置图片样式
                            ImageOptions imageOptions = new ImageOptions.Builder()
                                    /*.setCircular(true)  设为圆形*/
                                    .setFailureDrawableId(R.mipmap.upload_circle_image)
                                    .setLoadingDrawableId(R.mipmap.upload_circle_image)
                                    .setCrop(true).build();          //是否裁剪？
                            x.image().bind(iv_nomissed,url,imageOptions);

                            //设置淘圈名
                            TextView tv_nomissed_name = viewHolder.getViewById(R.id.taoquan_gridview_item_name);
                            tv_nomissed_name.setText(amoyCircle.getCircleName());

                            //设置淘圈人气数
                            TextView tv_nomissed_popularity = viewHolder.getViewById(R.id.taoquan_gridview_item_popularity);
                            tv_nomissed_popularity.setText("人气 "+ amoyCircle.getCircleNumber());


                        }
                    };
                    //设置适配器
                    myGridView.setAdapter(circlesAdapter);
                    //设置GridView的item点击事件
                    myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            AmoyCircle amoyCircle = (AmoyCircle) parent.getItemAtPosition(position);
                            Intent intent = new Intent(getActivity(), EachTaoquanActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("amoyCircle", amoyCircle);
                            intent.putExtras(bundle);
                            startActivity(intent);
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

    void initEvent(){
        ptrFrame.setLastUpdateTimeRelateObject(this);

        //下拉刷新的阻力，下拉时，下拉距离和显示头部的距离比例，值越大，则越不容易滑动
        ptrFrame.setRatioOfHeaderHeightToRefresh(1.2f);

        ptrFrame.setDurationToClose(200);//返回到刷新的位置（暂未找到）

        ptrFrame.setDurationToCloseHeader(1000);//关闭头部的时间 // default is false

        ptrFrame.setPullToRefresh(false);//当下拉到一定距离时，自动刷新（true），显示释放以刷新（false）

        ptrFrame.setKeepHeaderWhenRefresh(true);//见名只意

        //数据刷新的接口回调
        ptrFrame.setPtrHandler(new PtrHandler() {
            //是否能够刷新
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        content, header);
            }
            //开始刷新的回调
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //数据刷新的回调
                ptrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData(); //获取数据并刷新界面
                        if(myLatitude!=0&&myLongitude!=0){
                            getCirclesData(myLatitude,myLongitude);//刷新地图
                        }
                        ptrFrame.refreshComplete();
                    }
                }, 1500);
            }
        });

        //UI更新接口的回调
        ptrFrame.addPtrUIHandler(new PtrUIHandler() {
            //刷新完成之后，UI消失之后的接口回调
            @Override
            public void onUIReset(PtrFrameLayout frame) {
            }
            //开始下拉之前的接口回调
            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {
            }
            //开始刷新的接口回调
            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {
            }
            //刷新完成的接口回调
            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {
            }
            //下拉滑动的接口回调，多次调用
            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
                /**
                 * isUnderTouch ：手指是否触摸
                 * status：状态值
                 * ptrIndicator：滑动偏移量等值的封装对象。
                 */
            }
        });

        //设置ImageButton点击事件，点击后跳转到创建淘圈的页面
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateTaoquanActivity.class);
                startActivity(intent);
            }
        });

        //设置“附近的淘圈”的“更多”点击事件：点击后跳转到显示更多附近淘圈的页面
        tv_taoquan_nearby_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TaoquanNearbyMoreActivity.class);
                intent.putExtra("myLatitude",myLatitude);
                intent.putExtra("myLongitude",myLongitude);
                startActivity(intent);
            }
        });
        //设置“不可错过的淘圈”的“更多”点击事件：点击后跳转到显示更多淘圈的页面
        tv_nomissedMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TaoquanDiscoveryMoreActivity.class);
                intent.putExtra("title","不可错过的淘圈");
                startActivity(intent);
            }
        });
        //设置“每日精选”的“更多”点击事件：点击后跳转到显示更多淘圈的页面
        tv_everydayMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),TaoquanDiscoveryMoreActivity.class);
                intent.putExtra("title","每日精选");
                startActivity(intent);
            }
        });


    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            myLatitude = location.getLatitude(); //纬度
            myLongitude = location.getLongitude(); //经度
            //Receive Location.纬度:location.getLatitude(),经度:location.getLongitude()
//            Log.i("MyLocationListener", "onReceiveLocation: "+location.getAddress().city);
            tv_circle_address.setText(location.getAddrStr().substring(2,location.getAddrStr().length()));
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    //此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
//        Log.i("MyLocationListener", "纬度: "+location.getLatitude()+"经度:"+location.getLongitude());
            //设置定位数据
            mBaiduMap.setMyLocationData(locData);
            //跳转到当前位置
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());//地理坐标数据
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 18.0f);//设置地图中心点以及缩放级别
            mBaiduMap.animateMapStatus(u); //以动画方式更新地图状态，动画耗时 300 ms

            //设置地图点击事件：点击跳转到附近淘圈的地图页面
            mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Gson gson = new Gson();
                    String locationJson = gson.toJson(location);
                    Intent intent = new Intent(getContext(), TaoquanNearbyMapActivity.class);
                    //intent.putExtra("myLatitude",myLatitude);
                    //intent.putExtra("myLongitude",myLongitude);
                    intent.putExtra("locationJson",locationJson);
                    startActivity(intent);
                }
                @Override
                public boolean onMapPoiClick(MapPoi mapPoi) {
                    return false;
                }
            });

            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
//        mCurrentMarker = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_geo);
//        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
//        mBaiduMap.setMyLocationConfiguration();
            // 当不需要定位图层时关闭定位图层
            //mBaiduMap.setMyLocationEnabled(false);

            //查出附近的淘圈集合并显示在ListView
            getCirclesData(myLatitude,myLongitude);
        }
    }

    //根据当前位置的纬度和经度查出附近的淘圈的集合
    private void getCirclesData(final double latitude, final double longitude){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryCirclesByLaLuServlet");
        requestParams.addQueryStringParameter("latitude",latitude+"");
        requestParams.addQueryStringParameter("longitude",longitude+"");
        requestParams.addQueryStringParameter("precision",0.005+""); //精确度，数值越大，显示淘圈的范围越大
        requestParams.addQueryStringParameter("pageNo",1+"");//第一页
        requestParams.addQueryStringParameter("pageSize",2+""); //最多显示2个

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<AmoyCircle>>(){}.getType();
                List<AmoyCircle> nearbyCircles; //附近的淘圈的集合;
                nearbyCircles = gson.fromJson(result,type);

                if(nearbyCircles==null){
                    return;
                }
                getNearbyCircleData(nearbyCircles); //为什么该方法放在显示marker后执行就不会执行？？
                //显示marker
                for(int i = 0;i<=nearbyCircles.size();i++){
                    //定义Maker坐标点
                    LatLng point = new LatLng(nearbyCircles.get(i).getCircleLatitude(), nearbyCircles.get(i).getCircleLongitude());
                    //构建Marker图标
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_mark);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
                    //在地图上添加Marker，并显示
                    mBaiduMap.addOverlay(option);
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

    private void getNearbyCircleData(List<AmoyCircle> nearbyCircles){
        //设置ListView
        CommonAdapter<AmoyCircle> nearbyCirclesAdapter;
        nearbyCirclesAdapter = new CommonAdapter<AmoyCircle>(getActivity(), nearbyCircles,R.layout.taoquan_mine_item) {
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
                if(realDistance>=0.001 && realDistance<1){ //1000米以内，大于1米 0.291154-->取出291，转化为int
                    //String distanceStr = (String.valueOf(realDistance*1000)).substring(0,3); //这样会有bug：距离为2位数的时候
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
        lv_taoquan_nearby.setAdapter(nearbyCirclesAdapter);
        //设置Item点击事件
        lv_taoquan_nearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AmoyCircle amoyCircle = (AmoyCircle) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), EachTaoquanActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("amoyCircle", amoyCircle);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }




}
