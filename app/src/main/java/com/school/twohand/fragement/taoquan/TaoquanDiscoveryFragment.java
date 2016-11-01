package com.school.twohand.fragement.taoquan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.DetailGoodsActivity;
import com.school.twohand.activity.taoquan.CreateTaoquanActivity;
import com.school.twohand.activity.taoquan.EachTaoquanActivity;
import com.school.twohand.activity.taoquan.TaoquanDiscoveryMoreActivity;
import com.school.twohand.activity.taoquan.TaoquanNearbyMapActivity;
import com.school.twohand.activity.taoquan.TaoquanNearbyMoreActivity;
import com.school.twohand.customview.MyGridView;

import com.school.twohand.customview.TaoquanDiscoveryListView;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.entity.Goods;
import com.school.twohand.query.entity.QueryGoodsBean;
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
import java.util.ArrayList;
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
public class TaoquanDiscoveryFragment extends Fragment implements TaoquanDiscoveryListView.OnLoadChangeListener {

    private PtrClassicFrameLayout ptrFrame;
    TaoquanDiscoveryListView lv_taoquan_discovery;

    BaiduMap mBaiduMap;  //获取BaiduMap对象
    double myLatitude; //当前位置的纬度
    double myLongitude; //当前位置的经度
    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListener();

    CommonAdapter<Goods> goodsAdapter;
    List<Goods> goodsList = new ArrayList<>();
    int pageNo = 1;
    QueryGoodsBean queryGoodsBean = new QueryGoodsBean(null,null,null,0,pageNo,6);
    Gson gson = new Gson();
    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());//在使用SDK各组件之前初始化context信息，传入ApplicationContext
        View v = inflater.inflate(R.layout.taoquan_discovery_fragment,null);

        initView(v);
        initData();
        initEvent();

        return v;
    }

    void initView(View v){
        ptrFrame = (PtrClassicFrameLayout) v.findViewById(R.id.ultra_ptr_frame);
        lv_taoquan_discovery = (TaoquanDiscoveryListView) v.findViewById(R.id.lv_taoquan_discovery);
        //ib = (ImageButton) v.findViewById(R.id.ib_createTaoquan);
        //获取地图控件引用
        lv_taoquan_discovery.mMapView = (TextureMapView) v.findViewById(R.id.bmapView);
        lv_taoquan_discovery.mMapView.showZoomControls(false);
        mBaiduMap = lv_taoquan_discovery.mMapView.getMap();  //获取地图控制器
        mBaiduMap.getUiSettings().setAllGesturesEnabled(false); //设置禁止任何手势

//        tv_circle_address = (TextView) v.findViewById(R.id.tv_circle_address);
//        lv_taoquan_nearby = (MyListView) v.findViewById(R.id.lv_taoquan_nearby);
//        tv_taoquan_nearby_more = (TextView) v.findViewById(R.id.tv_taoquan_nearby_more);
//        gv_nomissed = (MyGridView) v.findViewById(R.id.taoquan_nomissed_gridview);
//        tv_nomissedMore = (TextView) v.findViewById(R.id.tv_taoquan_nomissed_more);
//        gv_everyday = (MyGridView) v.findViewById(R.id.taoquan_everyday_gridview);
//        tv_everydayMore = (TextView) v.findViewById(R.id.tv_taoquan_everyday_more);

        mLocationClient = new LocationClient(getActivity().getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocationClient.start();  //开始定位

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
        //设置“不可错过的淘圈”的GridView，参数1表示按人气排序,降序
        getGridViewData(1,lv_taoquan_discovery.gv_nomissed);

        //设置“每日精选的淘圈”的GridView，参数2表示按淘圈创建时间排序，降序
        getGridViewData(2,lv_taoquan_discovery.gv_everyday);

        //设置“猜你喜欢”的GridView，（还没想好，暂时按淘圈创建时间排序，升序）
        getGridViewData(3,lv_taoquan_discovery.gv_guessYouLike);

        //设置“高冷地带”的GridView，参数4表示按人气排序，升序
        getGridViewData(4,lv_taoquan_discovery.gv_coldZone);

        //设置商品的ListView
        pageNo = 1;
        queryGoodsBean.setPageNo(pageNo);
        getGoodsData();
    }

    //获取“不可错过的淘圈”等等的GridView的数据源并显示,需要的是requirement和GridView对象,显示6个
    void getGridViewData(int orderFlag, final MyGridView myGridView){
        String url = NetUtil.url+"QueryCirclesByServlet";
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("orderFlag",orderFlag+"");
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",6+""); //只获取6条记录
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<AmoyCircle>>(){}.getType();
                List<AmoyCircle> amoyCircles = gson.fromJson(result,type);

                //设置GridView的数据源
                CommonAdapter<AmoyCircle> circlesAdapter = new CommonAdapter<AmoyCircle>(getActivity(), amoyCircles,R.layout.taoquan_gridview_item) {
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

    //获取商品信息,并显示在ListView上
    private void getGoodsData(){
        String url = NetUtil.url + "QueryGoodsServlet";
        RequestParams requestParams = new RequestParams(url);
        String queryGoodsBeanString = gson.toJson(queryGoodsBean);
        requestParams.addQueryStringParameter("queryGoodsBean", queryGoodsBeanString);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(final String result) {
                Type type = new TypeToken<List<Goods>>() {}.getType();
                List<Goods> newGoodsList = gson.fromJson(result, type);
                if(newGoodsList.size()<=4){ //商品数量小于等于4，则移除底部布局
                    lv_taoquan_discovery.removeFootViewIfNeed();
                }
                goodsList.clear();
                goodsList.addAll(newGoodsList);
                //设置ListView的数据源
                if (goodsAdapter == null) {
                    goodsAdapter = new CommonAdapter<Goods>(getActivity(), goodsList, R.layout.each_taoquan_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, Goods goods, final int position) {
                            //显示用户头像
                            ImageView iv_userImage = viewHolder.getViewById(R.id.each_taoquan_item_userImage);
                            String url = NetUtil.imageUrl + goods.getGoodsUser().getUserHead();
                            //设置图片样式
                            ImageOptions imageOptions = new ImageOptions.Builder()
                                    .setCircular(true)  /*设为圆形*/
                                    .setFailureDrawableId(R.mipmap.ic_launcher)
                                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                                    .setCrop(true).build();          //是否裁剪？
                            x.image().bind(iv_userImage, url, imageOptions);
                            //设置用户名
                            TextView tv_userName = viewHolder.getViewById(R.id.each_taoquan_item_userName);
                            tv_userName.setText(goods.getGoodsUser().getUserName());
                            //设置价格
                            TextView tv_price = viewHolder.getViewById(R.id.each_taoquan_item_price);
                            tv_price.setText("￥ " + goods.getGoodsPrice());
                            //设置商品描述
                            TextView tv_describe = viewHolder.getViewById(R.id.each_taoquan_item_describe);
                            if(goods.getGoodsTitle()!=null){
                                tv_describe.setText(goods.getGoodsTitle()+" "+goods.getGoodsDescribe());
                            }
                            //设置商品图片
                            LinearLayout LL = viewHolder.getViewById(R.id.LL);
                            LL.setTag(goods);
                            addLLView(LL,position);
                            //设置商品所属用户的学校
                            TextView tv_goods_user_school = viewHolder.getViewById(R.id.tv_goods_user_school);
                            String goodsUserSchool = goods.getGoodsUser().getUserSchoolName();
                            if(goodsUserSchool!=null){
                                tv_goods_user_school.setText("来自 "+goodsUserSchool+"  淘圈|"+goods.getGoodsAmoyCircle().getCircleName());
                            }
                            //设置点赞量和浏览量
                            TextView tv_likes_pageview = viewHolder.getViewById(R.id.tv_likes_pageview);
                            tv_likes_pageview.setText("点赞 "+goods.getGoodsLikes().size()+" · 浏览 "+goods.getGoodsPV());
                            //点击跳转详情界面
                            LinearLayout LL_click_to_details = viewHolder.getViewById(R.id.LL_click_to_details);
//                            LL_click_to_details.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Log.i("EachTaoquanActivity", "onClick: "+position);
//                                    Intent intent = new Intent(EachTaoquanActivity.this, DetailGoodsActivity.class);
//                                    intent.putExtra("goodsMessage", result);
//                                    intent.putExtra("position", position + 1);
//                                    startActivity(intent);
//                                }
//                            });
                        }
                    };
                    lv_taoquan_discovery.setAdapter(goodsAdapter);
                    //设置Item点击事件，因为LinearLayout不生效，所以单独给里面每个图片也设置了点击事件
                    lv_taoquan_discovery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(position<=goodsList.size()){
                                Log.i("EachTaoquanActivity", "1111onClick: "+position);
                                Intent intent = new Intent(getActivity(), DetailGoodsActivity.class);
                                intent.putExtra("goodsMessage", gson.toJson(goodsList));
                                intent.putExtra("position", position ); //  position+ 1,头部也算位置,区别于346行的position？？为什么不一样
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    goodsAdapter.notifyDataSetChanged();
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

    //给ListView的Item的图片位置添加图片
    public void addLLView(LinearLayout LL, final int position) {
        LL.removeAllViews(); //加之前要先把之前的remove掉，！！！
//        Log.i("EachTaoquanActivity", "addLLView: ((EachCircleItem)LL.getTag()).getGoodsImages():"+((EachCircleItem)LL.getTag()).getGoodsImages());
        for (int i = 0; i < ((Goods) LL.getTag()).getGoodsImages().size(); i++) {
//                                View view = LayoutInflater.from(EachTaoquanActivity.this).inflate(
//                                        R.layout.each_taoquan_image_item, LL, false);
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.each_taoquan_image_item, null);
            ImageView iv_goodsImage = (ImageView) view.findViewById(R.id.iv_goods_image_item);
            iv_goodsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DetailGoodsActivity.class);
                    intent.putExtra("goodsMessage", gson.toJson(goodsList));
                    intent.putExtra("position", position + 1);
                    startActivity(intent);
                }
            });
            String url2 = NetUtil.imageUrl + ((Goods) LL.getTag()).getGoodsImages().get(i).getImageAddress();
            ImageOptions imageOptions2 = new ImageOptions.Builder()
                    .setFailureDrawableId(R.mipmap.ic_launcher)
                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                    .setCrop(true).build();
            x.image().bind(iv_goodsImage, url2, imageOptions2);
            LL.addView(view);
        }
    }

    //加载更多商品信息
    private void loadMoreGoodsData(){
        String url = NetUtil.url + "QueryGoodsServlet";
        RequestParams requestParams = new RequestParams(url);
        String queryGoodsBeanString = gson.toJson(queryGoodsBean);
        requestParams.addQueryStringParameter("queryGoodsBean", queryGoodsBeanString);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(final String result) {
                Type type = new TypeToken<List<Goods>>() {}.getType();
                List<Goods> newGoodsList = gson.fromJson(result, type);
                if(newGoodsList.size()==0){  //没有更多的数据了
                    pageNo--; //下一次继续加载这一页
                    Toast.makeText(getActivity(), "没有更多数据了~", Toast.LENGTH_SHORT).show();
                    return;
                }
                goodsList.addAll(newGoodsList);
                //因为goodsAdapter一定不为null，所以直接调用notifyDataSetChanged()
                goodsAdapter.notifyDataSetChanged();
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
                            getCirclesData(myLatitude,myLongitude);
                            //刷新地图,这里有个bug，刷新完之后地图消失，解决方案如下
                            //lv_taoquan_discovery.mMapView.onResume();//bug已经解决,不用这种方式,123-126代码提前,只执行一次
                        }
                        ptrFrame.refreshComplete();   //完成刷新后，页面恢复
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
        lv_taoquan_discovery.ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateTaoquanActivity.class);
                startActivity(intent);
            }
        });

        //设置加载事件的监听
        lv_taoquan_discovery.setOnLoadChangeListener(this);

        //设置“附近的淘圈”的“更多”点击事件：点击后跳转到显示更多附近淘圈的页面
        lv_taoquan_discovery.tv_taoquan_nearby_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TaoquanNearbyMoreActivity.class);
                intent.putExtra("myLatitude",myLatitude);
                intent.putExtra("myLongitude",myLongitude);
                startActivity(intent);
            }
        });
        //设置“不可错过的淘圈”的“更多”点击事件：点击后跳转到显示更多淘圈的页面
        lv_taoquan_discovery.tv_nomissedMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TaoquanDiscoveryMoreActivity.class);
                intent.putExtra("title","不可错过的淘圈");
                startActivity(intent);
            }
        });
        //设置“每日精选”的“更多”点击事件：点击后跳转到显示更多淘圈的页面
        lv_taoquan_discovery.tv_everydayMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),TaoquanDiscoveryMoreActivity.class);
                intent.putExtra("title","每日精选");
                startActivity(intent);
            }
        });
        //设置“猜你喜欢”的“更多”点击事件：点击后跳转到显示更多淘圈的页面
        lv_taoquan_discovery.tv_guessYouLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),TaoquanDiscoveryMoreActivity.class);
                intent.putExtra("title","猜你喜欢");
                startActivity(intent);
            }
        });
        //设置“高冷地带”的“更多”点击事件：点击后跳转到显示更多淘圈的页面
        lv_taoquan_discovery.tv_coldZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),TaoquanDiscoveryMoreActivity.class);
                intent.putExtra("title","高冷地带");
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
            lv_taoquan_discovery.tv_circle_address.setText(location.getAddrStr().substring(2,location.getAddrStr().length()));
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    //此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
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
        lv_taoquan_discovery.lv_taoquan_nearby.setAdapter(nearbyCirclesAdapter);
        //设置Item点击事件
        lv_taoquan_discovery.lv_taoquan_nearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    //加载更多商品
    @Override
    public void onLoad() {
        pageNo++;
        //原来数据基础上增加
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                queryGoodsBean.setPageNo(pageNo);
                loadMoreGoodsData();
                lv_taoquan_discovery.completeLoad();  //没获取到数据也要改变界面
            }
        },1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        lv_taoquan_discovery.mMapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        lv_taoquan_discovery.mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        lv_taoquan_discovery.mMapView.onPause();
    }

}
