package com.school.twohand.fragement.taoquan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.taoquan.EachTaoquanActivity;
import com.school.twohand.entity.AmoyCircle;
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
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/** 淘圈页面的“我的”的fragment
 * Created by yang on 2016/9/28 0028.
 */
public class TaoquanMineFragment extends Fragment implements UltraRefreshListener {

    MyApplication myApplication;
    User user;
    CommonAdapter<AmoyCircle> circlesAdapter;
    List<AmoyCircle> amoyCircles = new ArrayList<>();
    UltraRefreshListView mLv;

    int pageNo = 1;
    int pageSize = 8;

    private PtrClassicFrameLayout mPtrFrame; //PtrClassicFrameLayout

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myApplication = (MyApplication) getActivity().getApplication();
        user = myApplication.getUser();
        View v = inflater.inflate(R.layout.taoquan_mine_fragment,null);
        initView(v);
        initData();

        return v;
    }

    //初始化界面
    void initView(View v){
        mLv = (UltraRefreshListView) v.findViewById(R.id.ultra_lv);

        mPtrFrame = (PtrClassicFrameLayout) v.findViewById(R.id.ultra_ptr);
        //创建我们的自定义头部视图
        CustomUltraRefreshHeader header = new CustomUltraRefreshHeader(getActivity());

        //设置头部视图
        mPtrFrame.setHeaderView(header);

        //设置视图修改的回调，因为我们的CustomUltraRefreshHeader实现了PtrUIHandler
        mPtrFrame.addPtrUIHandler(header);

        //设置数据刷新的会回调，因为UltraRefreshListView实现了PtrHandler
        mPtrFrame.setPtrHandler(mLv);

        //设置数据刷新回调接口
        mLv.setUltraRefreshListener(this);
    }

    //初始化数据
    void initData(){
        getData();
    }

    //获取数据
    void getData(){
        final int userId = user.getUserId();
        String url = NetUtil.url+"QueryCirclesServlet";
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("userId",userId+"");
        requestParams.addQueryStringParameter("orderFlag",0+"");//按照淘圈人气排序
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",pageSize+"");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<AmoyCircle>>(){}.getType();
                List<AmoyCircle> newAmoyCircles = gson.fromJson(result,type);
                if(newAmoyCircles.size()<=6){
                    mLv.removeFootIfNeed(); //淘圈小于等于6个，就去掉底部布局
                }
                amoyCircles.clear(); //清空数据
                amoyCircles.addAll(newAmoyCircles); //将一个集合所有数据添加到集合

                //设置listView的数据源
                if(circlesAdapter==null){
                    circlesAdapter = new CommonAdapter<AmoyCircle>(getActivity(), amoyCircles,R.layout.taoquan_mine_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, AmoyCircle amoyCircle, int position) {
                            //设置淘圈名
                            TextView taoquan_mine_item_name = viewHolder.getViewById(R.id.taoquan_mine_item_name);
                            taoquan_mine_item_name.setText(amoyCircle.getCircleName());

                            //设置淘圈人气
                            TextView taoquan_mine_item_popularity = viewHolder.getViewById(R.id.taoquan_mine_item_popularity);
                            taoquan_mine_item_popularity.setText("人气+ "+ amoyCircle.getCircleNumber());

                            //设置淘圈头像
                            ImageView taoquan_image = viewHolder.getViewById(R.id.taoquan_mine_item_image);
                            String url = NetUtil.imageUrl+ amoyCircle.getCircleImageUrl();

                            //设置图片样式
                            ImageOptions imageOptions = new ImageOptions.Builder()
                                    /*.setCircular(true)  设为圆形*/
                                    .setFailureDrawableId(R.mipmap.upload_circle_image)
                                    .setLoadingDrawableId(R.mipmap.upload_circle_image)
                                    .setCrop(true).build();          //是否裁剪？
                            x.image().bind(taoquan_image,url,imageOptions);

                            //设置是否显示为圈主
                            TextView isCircleManager = viewHolder.getViewById(R.id.isCircleManager);
                            isCircleManager.setText("");
                            if(amoyCircle.getCircleUserId()==userId){//若该淘圈userId等于该用户id，则设置显示为圈主
                                isCircleManager.setText("主");
                            }
                        }
                    };
                    mLv.setAdapter(circlesAdapter);

                    //设置listView的item点击事件
                    mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Log.i("Taoquan", "position: "+position+"---"+parent.getCount());
                            if(position>=0&&position<parent.getCount()&&amoyCircles.size()<=6){ //没有底部布局
                                AmoyCircle amoyCircle = (AmoyCircle) parent.getItemAtPosition(position);
                                Intent intent = new Intent(getActivity(), EachTaoquanActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("amoyCircle", amoyCircle);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                            if(position>=0&&position<parent.getCount()-1&&amoyCircles.size()>6) { //当出现底部布局的时候，底部布局也占一个位置
                                AmoyCircle amoyCircle = (AmoyCircle) parent.getItemAtPosition(position);
                                Intent intent = new Intent(getActivity(), EachTaoquanActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("amoyCircle", amoyCircle);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    });
                }else{
                    //只有当数据改变而不是引用改变才会执行该方法，因此不能改变引用，只改变数据内容，将list提为全局变量
                    circlesAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), "无法获取网络数据，请检查网络连接", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }

    private void loadMoreData(){
        final int userId = user.getUserId();
        String url = NetUtil.url+"QueryCirclesServlet";
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("userId",userId+"");
        requestParams.addQueryStringParameter("orderFlag",0+"");//按照淘圈人气排序
        requestParams.addQueryStringParameter("pageNo",pageNo+"");
        requestParams.addQueryStringParameter("pageSize",pageSize+"");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<AmoyCircle>>(){}.getType();
                List<AmoyCircle> newAmoyCircles = gson.fromJson(result,type);
                if(newAmoyCircles.size()==0){//服务器没有返回新的数据
                    pageNo--; //下一次继续加载这一页
                    return;
                }
                //amoyCircles.clear(); //这里不能清空数据
                amoyCircles.addAll(newAmoyCircles); //将一个集合所有数据添加到集合

                //设置listView的数据源
                if(circlesAdapter==null){
                    circlesAdapter = new CommonAdapter<AmoyCircle>(getActivity(), amoyCircles,R.layout.taoquan_mine_item) {
                        @Override
                        public void convert(ViewHolder viewHolder, AmoyCircle amoyCircle, int position) {
                            //设置淘圈名
                            TextView taoquan_mine_item_name = viewHolder.getViewById(R.id.taoquan_mine_item_name);
                            taoquan_mine_item_name.setText(amoyCircle.getCircleName());

                            //设置淘圈人气
                            TextView taoquan_mine_item_popularity = viewHolder.getViewById(R.id.taoquan_mine_item_popularity);
                            taoquan_mine_item_popularity.setText("人气+ "+ amoyCircle.getCircleNumber());
                            //taoquan_mine_item_popularity.setText(amoyCircle.getCircleCreateTime().toString());

                            //设置淘圈头像
                            ImageView taoquan_image = viewHolder.getViewById(R.id.taoquan_mine_item_image);
                            String url = NetUtil.imageUrl+ amoyCircle.getCircleImageUrl();

                            //设置图片样式
                            ImageOptions imageOptions = new ImageOptions.Builder()
                                    /*.setCircular(true)  设为圆形*/
                                    .setFailureDrawableId(R.mipmap.upload_circle_image)
                                    .setLoadingDrawableId(R.mipmap.upload_circle_image)
                                    .setCrop(true).build();          //是否裁剪？
                            x.image().bind(taoquan_image,url,imageOptions);

                            //设置是否显示为圈主
                            TextView isCircleManager = viewHolder.getViewById(R.id.isCircleManager);
                            isCircleManager.setText("");
                            if(amoyCircle.getCircleUserId()==userId){//若该淘圈userId等于该用户id，则设置显示为圈主
                                isCircleManager.setText("主");
                            }
                        }
                    };
                    mLv.setAdapter(circlesAdapter);

                    //设置listView的item点击事件
                    mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Log.i("Taoquan", "position: "+position+"---"+parent.getCount());
                            //当出现底部布局的时候，底部布局也占一个位置,这里去掉了底部布局，就不需要&&position<parent.getCount()-1判断
                            AmoyCircle amoyCircle = (AmoyCircle) parent.getItemAtPosition(position);
                            Intent intent = new Intent(getActivity(), EachTaoquanActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("amoyCircle", amoyCircle);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                }else{
                    //只有当数据改变而不是引用改变才会执行该方法，因此不能改变引用，只改变数据内容，将list提为全局变量
                    circlesAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), "无法获取网络数据，请检查网络连接", Toast.LENGTH_SHORT).show();
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
