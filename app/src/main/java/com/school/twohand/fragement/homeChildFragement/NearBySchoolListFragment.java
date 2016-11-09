package com.school.twohand.fragement.homeChildFragement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
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
import com.school.twohand.ultra.CustomUltraRefreshHeader;
import com.school.twohand.ultra.UltraRefreshListView;
import com.school.twohand.ultra.UltraRefreshListener;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import overlayutil.PoiOverlay;

/**
 * 附近的学校，列表形式
 * Created by yang on 2016/10/31 0031.
 */
public class NearBySchoolListFragment extends Fragment implements UltraRefreshListener{


    @InjectView(R.id.ultra_lv_nearby_school)
    UltraRefreshListView lv_NearbySchool;
    @InjectView(R.id.ultra_ptr_nearby_school)
    PtrClassicFrameLayout ultraPtrNearbySchool;

    BDLocation location;    //从activity传来的BDLocation对象
    PoiSearch mPoiSearch;
    CommonAdapter<PoiInfo> commonAdapter;
    int pageNo = 0;            //当前数据页数
    List<PoiInfo> poiInfoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.nearby_school_list_fragment, null);
        ButterKnife.inject(this, v);

        initView();
        init();
        initData();
        initEvent();
        return v;
    }

    private void initView(){
        //创建我们的自定义头部视图
        CustomUltraRefreshHeader header = new CustomUltraRefreshHeader(getActivity());

        //设置头部视图
        ultraPtrNearbySchool.setHeaderView(header);

        //设置视图修改的回调，因为我们的CustomUltraRefreshHeader实现了PtrUIHandler
        ultraPtrNearbySchool.addPtrUIHandler(header);

        //设置数据刷新的会回调，因为UltraRefreshListView实现了PtrHandler
        ultraPtrNearbySchool.setPtrHandler(lv_NearbySchool);

        //设置数据刷新回调接口
        lv_NearbySchool.setUltraRefreshListener(this);
    }

    private void init(){
        Bundle bundle = getArguments();
        String locationJson = bundle.getString("locationJson");
        Gson gson = new Gson();
        location = gson.fromJson(locationJson,BDLocation.class);

    }

    private void initData(){
        mPoiSearch = PoiSearch.newInstance();  //创建POI检索实例
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
            public void onGetPoiResult(PoiResult result){
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    //获取POI检索结果
                    List<PoiInfo> newPoiInfoList = result.getAllPoi();
                    poiInfoList.addAll(newPoiInfoList);
                    if(poiInfoList!=null){
                        if(poiInfoList.size()<15){ //如果数据少于15条，则去掉低布局
                            lv_NearbySchool.removeFootIfNeed();
                        }
                        setData();//设置ListView数据源
                    }
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

    //设置ListView的数据并展现在ListView上
    private void setData(){
        if(commonAdapter==null){
            commonAdapter = new CommonAdapter<PoiInfo>(getActivity(),poiInfoList,R.layout.nearby_school_list_item) {
                @Override
                public void convert(ViewHolder viewHolder, PoiInfo poiInfo, int position) {
                    TextView schoolName = viewHolder.getViewById(R.id.nearby_school_list_item_schoolName);
                    schoolName.setText(poiInfo.name);
                }
            };
            lv_NearbySchool.setAdapter(commonAdapter);
            lv_NearbySchool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if(position<parent.getCount()-1){//头布局和底布局也算位置的，因此设置点击事件的时候要把它排除掉
                        new AlertDialog.Builder(getActivity()).setMessage("确定将  “"+poiInfoList.get(position).name+"”  设为您的大学？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(((MyApplication) getActivity().getApplication()).getUser()==null){
                                            //是游客,跳转到登陆页面注册身份信息同时Application中的user被赋值
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            startActivity(intent);
                                        }else{
                                            updateUserSchoolName(poiInfoList.get(position).name);
                                        }

                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    }
                }
            });
        }else{
            commonAdapter.notifyDataSetChanged();
        }
    }

    private void initEvent(){
        LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());//地理坐标数据
        //检索附近的poi，关键字为大学，每页20条数据，检索范围半径为10000米
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(ll).keyword("大学").pageCapacity(20).pageNum(pageNo).radius(10000));
    }

    private void updateUserSchoolName(String userSchoolName){
        User updateUser = new User();   //存放修改的信息的User对象
        updateUser.setUserId(((MyApplication) getActivity().getApplication()).getUser().getUserId());
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

    @Override
    public void onRefresh() {
        pageNo = 0;
        ultraPtrNearbySchool.postDelayed(new Runnable() {
            @Override
            public void run() {
                initEvent();
                setData();
                lv_NearbySchool.refreshComplete();
            }
        },1000);
    }

    @Override
    public void addMore() {
        pageNo++;
        ultraPtrNearbySchool.postDelayed(new Runnable() {
            @Override
            public void run() {
                initEvent();
                setData();
                lv_NearbySchool.refreshComplete();
            }
        },1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
