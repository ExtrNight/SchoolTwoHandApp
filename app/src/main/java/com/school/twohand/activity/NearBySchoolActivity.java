package com.school.twohand.activity;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.google.gson.Gson;
import com.school.twohand.fragement.homeChildFragement.NearBySchoolListFragment;
import com.school.twohand.fragement.homeChildFragement.NearBySchoolMapFragment;
import com.school.twohand.schooltwohandapp.R;

import java.util.List;

import overlayutil.PoiOverlay;

/**
 * 附近的学校
 */
public class NearBySchoolActivity extends AppCompatActivity {

    ImageView iv_nearBy_school_return;
    ImageView iv_nearBy_school_map;
    ImageView iv_nearBy_school_list;
    Fragment[] fragments;
    NearBySchoolListFragment nearbySchoolListFragment;   //附近学校列表的Fragment
    NearBySchoolMapFragment nearbySchoolMapFragment;     //附近学校地图的Fragment

    int oldIndex;   //旧的索引
    int newIndex;

    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_near_by_school);

        initView();
        initData();
        initEvent();

    }

    private void initView(){
        iv_nearBy_school_return = (ImageView)findViewById(R.id.iv_nearBy_school_return);
        iv_nearBy_school_map = (ImageView) findViewById(R.id.iv_nearBy_school_map);
        iv_nearBy_school_list = (ImageView) findViewById(R.id.iv_nearBy_school_list);

    }

    private void initData(){
        initLocation();  //初始化定位数据
        mLocationClient.start();  //开启定位
    }

    private void initLocation(){
        mLocationClient = new LocationClient(getApplicationContext()); //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

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

    private void initEvent(){
        iv_nearBy_school_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFragmentEvent(){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        //点击地图图片显示出地图页面的Fragment
        iv_nearBy_school_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_nearBy_school_map.setVisibility(View.GONE);
                iv_nearBy_school_list.setVisibility(View.VISIBLE);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(fragments[0]);
                //这里要进行判断，若没有添加过Map的Fragment(第一次)，就添加Map的Fragment
                if(!fragments[1].isAdded()){
                    fragmentTransaction.add(R.id.FL_listAndMap,fragments[1]);
                }
                fragmentTransaction.show(fragments[1]).commit();
            }
        });
        //点击列表图片显示出列表页面的Fragment
        iv_nearBy_school_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_nearBy_school_list.setVisibility(View.GONE);
                iv_nearBy_school_map.setVisibility(View.VISIBLE);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(fragments[1]);
                //这里其实就不需要判断，因为第一个Fragment一定添加过（在第一次进入页面的时候）
                if(!fragments[0].isAdded()){
                    fragmentTransaction.add(R.id.FL_listAndMap,fragments[0]);
                }
                fragmentTransaction.show(fragments[0]).commit();
            }
        });
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location.纬度:location.getLatitude(),经度:location.getLongitude()
            Gson gson = new Gson();
            String locationJson = gson.toJson(location);//将BDLocation对象转化为Json传给Fragment
            //初始化Fragment并把BDLocation传给Fragment
            initFragment(locationJson);
            initFragmentEvent();
        }
    }

    private void initFragment(String locationJson){
        Bundle bundle = new Bundle(); //Activity给Fragment传值
        bundle.putString("locationJson",locationJson);
        //初始化Fragment并添加到数组里
        nearbySchoolListFragment = new NearBySchoolListFragment();
        nearbySchoolListFragment.setArguments(bundle); //Activity给Fragment传值
        nearbySchoolMapFragment = new NearBySchoolMapFragment();
        nearbySchoolMapFragment.setArguments(bundle);  //Activity给Fragment传值
        fragments = new Fragment[]{nearbySchoolListFragment,nearbySchoolMapFragment};

        //界面初始显示第一个fragment;添加第一个fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.FL_listAndMap,fragments[0]);
        fragmentTransaction.commit();
    }



}
