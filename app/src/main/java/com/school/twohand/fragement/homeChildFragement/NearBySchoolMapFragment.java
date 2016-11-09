package com.school.twohand.fragement.homeChildFragement;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
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
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.school.twohand.activity.login.LoginActivity;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import overlayutil.PoiOverlay;

/** 附近学校的Fragment，地图页面
 * Created by yang on 2016/10/31 0031.
 */
public class NearBySchoolMapFragment extends Fragment{

    User user;
    MapView mapView;
    BaiduMap baiduMap;
    BDLocation location;    //从activity传来的BDLocation对象
    LatLng ll;
    PoiSearch mPoiSearch;
    ImageView iv_refresh;
    int pageNo = 0;
    ProgressDialog pd;   //进度条，圆形

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        SDKInitializer.initialize(getActivity().getApplicationContext());//在使用SDK各组件之前初始化context信息，传入ApplicationContext
        View v = inflater.inflate(R.layout.nearby_school_map_fragment,null);

        init();
        initView(v);
        initData();
        initEvent();

        return v;
    }

    private void init(){
        Bundle bundle = getArguments();
        String locationJson = bundle.getString("locationJson");
        Gson gson = new Gson();
        location = gson.fromJson(locationJson,BDLocation.class);
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        user = myApplication.getUser();
    }

    private void initView(View v){

        mapView = (MapView) v.findViewById(R.id.mv_nearBy_school);
        if(mapView!=null){
            baiduMap = mapView.getMap();
//            baiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);//设置地图类型为普通地图
        }
        iv_refresh = (ImageView) v.findViewById(R.id.iv_nearBy_school_refresh);

    }

    private void initData(){
        mPoiSearch = PoiSearch.newInstance();  //创建POI检索实例
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
            public void onGetPoiResult(PoiResult result){
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    //获取POI检索结果
                    List<PoiInfo> poiInfoList = result.getAllPoi();
                    if(poiInfoList!=null){
                        baiduMap.clear();
                        PoiOverlay overlay = new MyPoiOverlay(baiduMap);
                        baiduMap.setOnMarkerClickListener(overlay);
                        overlay.setData(result);    //设置POI数据
                        overlay.addToMap();         //将所有Overlay 添加到地图上
                        overlay.zoomToSpan();       //缩放地图，使所有Overlay都在合适的视野内

                        //设置一个popupWindow
                        TextView tv = new TextView(getActivity());
                        tv.setBackgroundColor(Color.WHITE);
                        tv.setTextColor(Color.BLACK);
                        tv.setText("没找到你的学校？请点击右侧刷新按钮哦~");
                        PopupWindow popupWindow = new PopupWindow(tv,ViewGroup.LayoutParams.WRAP_CONTENT
                                ,ViewGroup.LayoutParams.WRAP_CONTENT);
                        //设置在外部触摸的时候可以消失掉
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.setBackgroundDrawable(new BitmapDrawable());//据说新版本不设置该方法也可以？
                        //popupWindow.showAsDropDown(iv_refresh,-100,-120);
                        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.TOP,
                                0,300);
                    }
                }else{ //没有更多数据，继续从第一页开始加载
                    pageNo = 0;
                    doSearch();
                }
                if(pd!=null){
                    pd.cancel();
                }
            }
            public void onGetPoiDetailResult(PoiDetailResult result){
                //获取Place详情页检索结果
                Log.i("NearBySchoolActivity", "onGetPoiDetailResult2: "+result);
            }
            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

    }

    private void initEvent(){
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                //此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.getDirection()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
//        Log.i("MyLocationListener", "纬度: "+location.getLatitude()+"经度:"+location.getLongitude());
        //设置定位数据
        baiduMap.setMyLocationData(locData);
        //跳转到当前位置
        ll = new LatLng(location.getLatitude(),location.getLongitude());//地理坐标数据
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 18.0f);//设置地图中心点以及缩放级别
        baiduMap.animateMapStatus(u); //以动画方式更新地图状态，动画耗时 300 ms

        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
//        mCurrentMarker = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_geo);
//        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
//        mBaiduMap.setMyLocationConfiguration();
        // 当不需要定位图层时关闭定位图层
        //mBaiduMap.setMyLocationEnabled(false);

        //开始检索,在指定城市内检索
//        mPoiSearch.searchInCity((new PoiCitySearchOption())
//                .city(location.getCity())
//                .keyword("大学")
//                .pageNum(0));
        //进行检索
        doSearch();

        //刷新再次发起检索
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(getActivity());
                pd.setMessage("更新地图中..");
                pd.show();
                pageNo++;
                doSearch();
            }
        });
    }

    private void doSearch(){
        //开始检索，检索附近的poi，关键字为大学，每页20条数据，检索范围半径为10000米
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(ll).keyword("大学").pageCapacity(10).pageNum(pageNo).radius(10000));
    }

    private void updateUserSchoolName(String userSchoolName){
        User updateUser = new User();   //存放修改的信息的User对象
        updateUser.setUserId(user.getUserId());
        updateUser.setUserSchoolName(userSchoolName);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String userJson = gson.toJson(updateUser);
        RequestParams requestParams = new RequestParams(NetUtil.url+"UpdateUserInfoServlet");
        requestParams.addQueryStringParameter("userJson",userJson);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                getActivity().finish();
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

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            final PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            new AlertDialog.Builder(getActivity()).setMessage("确定将  “"+poi.name+"”  设为您的大学？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(((MyApplication) getActivity().getApplication()).getUser()==null){
                                //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            }else{
                                updateUserSchoolName(poi.name);
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
            //mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
            // }
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        mPoiSearch.destroy();      //释放POI检索实例
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }


}
