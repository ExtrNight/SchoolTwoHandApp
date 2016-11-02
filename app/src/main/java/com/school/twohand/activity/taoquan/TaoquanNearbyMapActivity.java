package com.school.twohand.activity.taoquan;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 附近淘圈的地图页面，从淘圈“发现”页面跳转过来
 */
public class TaoquanNearbyMapActivity extends AppCompatActivity {

    ImageView iv_return;
    MapView mMapView;
    BaiduMap mBaiduMap;  //获取BaiduMap对象
//    public LocationClient mLocationClient = null;
//    public BDLocationListener myListener = new MyLocationListener();
//    double myLatitude; //当前位置的纬度
//    double myLongitude; //当前位置的经度
    BDLocation location; //当前位置的BDLocation对象
    List<AmoyCircle> amoyCircles = new ArrayList<>();
    SparseArray<Integer> flags =  new SparseArray<>(); //存放淘圈所在的marker是否被点亮的Map，1为点亮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_taoquan_nearby_map);

        initView();
        initData();
        initEvent();

    }

    private void initView(){
        iv_return = (ImageView) findViewById(R.id.iv_taoquan_discovery_return);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mv_nearby_taoquan);
        if(mMapView!=null){
            mBaiduMap = mMapView.getMap();  //获取地图控制器
        }
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setOverlookingGesturesEnabled(false);//不允许允许俯视手势
        uiSettings.setRotateGesturesEnabled(false); //不允许旋转手势
        uiSettings.setScrollGesturesEnabled(true);//允许拖拽手势
        uiSettings.setZoomGesturesEnabled(true); //允许缩放手势

        //initLocation(); //不再定位，而是使用上个页面传来的经纬度值
    }

//    private void initLocation(){
//        mLocationClient = new LocationClient(getApplicationContext()); //声明LocationClient类
//        mLocationClient.registerLocationListener(myListener);    //注册监听函数
//
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        //int span=1000;
//        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
//        mLocationClient.setLocOption(option);
//    }

    private void initData(){
        Intent intent = getIntent();
        if(intent!=null){
            Bundle bundle = intent.getExtras();
//            myLatitude = bundle.getDouble("myLatitude"); //纬度
//            myLongitude = bundle.getDouble("myLongitude"); //经度
            String locationJson = bundle.getString("locationJson");
            Gson gson = new Gson();
            location = gson.fromJson(locationJson,BDLocation.class);
        }
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
        mBaiduMap.animateMapStatus(u);

        //查出附近的淘圈集合
        getCirclesData(location.getLatitude(),location.getLongitude());
    }

    private void initEvent(){
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //marker.setToTop(); //设置marker在最上层
//                Log.i("Taoquan", "onMarkerClick: "+marker.getTitle());
                Gson gson = new Gson();
                AmoyCircle amoyCircle = gson.fromJson(marker.getTitle(),AmoyCircle.class);
                if(flags.get(amoyCircle.getCircleId())==0){ //第一次点击
                    flags.put(amoyCircle.getCircleId(),1);
                    View v = LayoutInflater.from(TaoquanNearbyMapActivity.this).inflate(R.layout.taoquan_nearby_map_marker,null);
                    TextView tv_circle_name = (TextView) v.findViewById(R.id.tv_circle_name);
                    tv_circle_name.setText(amoyCircle.getCircleName()+" >");
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(v);
                    marker.setIcon(bitmap);
                }else if(flags.get(amoyCircle.getCircleId())==1){ //不是第一次点击
                    Intent intent = new Intent(TaoquanNearbyMapActivity.this, EachTaoquanActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("amoyCircle", amoyCircle);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                return false;
            }
        });
        //mLocationClient.start();  //开始定位  //不再定位，而是使用上个页面传来的经纬度值

        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

//    public class MyLocationListener implements BDLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            myLatitude = location.getLatitude(); //纬度
//            myLongitude = location.getLongitude(); //经度
//            //Receive Location.纬度:location.getLatitude(),经度:location.getLongitude()
////            Log.i("MyLocationListener", "onReceiveLocation: "+location.getAddress().city);
//            // 开启定位图层
//            mBaiduMap.setMyLocationEnabled(true);
//            // 构造定位数据
//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
//                    //此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(location.getDirection()).latitude(location.getLatitude())
//                    .longitude(location.getLongitude()).build();
////        Log.i("MyLocationListener", "纬度: "+location.getLatitude()+"经度:"+location.getLongitude());
//            //设置定位数据
//            mBaiduMap.setMyLocationData(locData);
//            //跳转到当前位置
//            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());//地理坐标数据
//            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 18.0f);//设置地图中心点以及缩放级别
//            mBaiduMap.animateMapStatus(u); //以动画方式更新地图状态，动画耗时 300 ms
//
//            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
////        mCurrentMarker = BitmapDescriptorFactory
////                .fromResource(R.drawable.icon_geo);
////        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
////        mBaiduMap.setMyLocationConfiguration();
//            // 当不需要定位图层时关闭定位图层
//            //mBaiduMap.setMyLocationEnabled(false);
//
//            //查出附近的淘圈集合
//            getCirclesData(myLatitude,myLongitude);
//        }
//    }

    //根据当前位置的纬度和经度查出附近的淘圈的集合
    private void getCirclesData(final double latitude, final double longitude){
        RequestParams requestParams = new RequestParams(NetUtil.url+"QueryCirclesByLaLuServlet");
        requestParams.addQueryStringParameter("latitude",latitude+"");
        requestParams.addQueryStringParameter("longitude",longitude+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<AmoyCircle>>(){}.getType();
                amoyCircles = gson.fromJson(result,type);

                for(int i = 0;i<=amoyCircles.size();i++){
                    flags.put(amoyCircles.get(i).getCircleId(),0);
                    //定义Maker坐标点
                    LatLng point = new LatLng(amoyCircles.get(i).getCircleLatitude(), amoyCircles.get(i).getCircleLongitude());
                    //构建Marker图标
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_mark);
                    Gson gson1 = new Gson();
                    String circleJson = gson1.toJson(amoyCircles.get(i));
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions().position(point).icon(bitmap).title(circleJson);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
