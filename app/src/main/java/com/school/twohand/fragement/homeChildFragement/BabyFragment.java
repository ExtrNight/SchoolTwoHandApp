package com.school.twohand.fragement.homeChildFragement;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.DetailGoodsActivity;
import com.school.twohand.activity.OrderDetailActivity;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.query.entity.QueryReleaseBean;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by chenglong on 2016/10/25.
 */
public class BabyFragment extends Fragment {

    ListView lvRelease;

    CommonAdapter<QueryReleaseBean> queryReleaseBeanCommonAdapter;
    List<QueryReleaseBean>  queryReleaseBean=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_baby,null);
        lvRelease= (ListView) view.findViewById(R.id.li_baby);
        ButterKnife.inject(getActivity());
        getGoodsData();

     /*   lvRelease.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), DetailGoodsActivity.class);
                Gson gson = new Gson();

                intent.putExtra("goodsJson", queryReleaseBean.get(position).getGoods());
                startActivity(intent);
            }
        });*/
        return view;
    }

    private void getGoodsData() {
        String url = NetUtil.url + "QueryReleaseServlet";
        Integer userId=  ((MyApplication)getActivity().getApplication()).getUser().getUserId();
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("userId", userId + "");

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(final String result) {

                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type type = new TypeToken<List<QueryReleaseBean>>() {
                }.getType();

                List<QueryReleaseBean> newQueryReleaseBean = gson.fromJson(result, type);

                queryReleaseBean.clear();
                queryReleaseBean.addAll(newQueryReleaseBean);

                //设置ListView的数据源
                if (queryReleaseBeanCommonAdapter == null) {
                    queryReleaseBeanCommonAdapter = new CommonAdapter<QueryReleaseBean>(getActivity(), queryReleaseBean, R.layout.item_mybaby) {
                        @Override
                            public void convert(ViewHolder viewHolder, QueryReleaseBean queryReleaseBean, final int position) {

                            TextView a = viewHolder.getViewById(R.id.tv_babyTitle);
                            a.setText(queryReleaseBean.getGoodsDescribe());

                            TextView b = viewHolder.getViewById(R.id.tv_babyPrice1);
                            b.setText("￥" + queryReleaseBean.getGoodsPrice());

                            //设置商品图片
                            LinearLayout LL = viewHolder.getViewById(R.id.LL);
                            LL.setTag(queryReleaseBean);
                            addLLView(LL,position);

                            //设置点赞量和浏览量
                            TextView d = viewHolder.getViewById(R.id.tv_babyPriase);
                            d.setText(queryReleaseBean.getLikeSum()+"赞");
                            Log.i("BabyFragment", "convert: convert"+d);
                            TextView e=viewHolder.getViewById(R.id.tv_babySay);
                            e.setText(queryReleaseBean.getMessageSum()+"留言");
                            Log.i("BabyFragment", "convert: convert"+e);
                            /*TextView f=viewHolder.getViewById(R.id.tv_babyTime);
                            SimpleDateFormat aa=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date d1=new Date(queryReleaseBean.getGoodsReleaseTime().getTime());
                            Date d2=new Date(System.currentTimeMillis());
                            long target = (9 * 1000 * 60 * 60 * 24);
                            long diff=target-(d2.getTime()-d1.getTime());
                            long days = diff / (1000 * 60 * 60 * 24);
                            f.setText(days+"天展示时间");*/

                        }
                    };

                    lvRelease.setAdapter(queryReleaseBeanCommonAdapter);
                }else {
                    queryReleaseBeanCommonAdapter.notifyDataSetChanged();
                }

            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("BabyFragment", "onError: onError"+ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }


        });
    }

    public void addLLView(LinearLayout LL, final int position) {
        Log.i("BabyFragment", "addLLView: addLLView+11");
        LL.removeAllViews(); //加之前要先把之前的remove掉，！！！
          for (int i = 0; i < ((QueryReleaseBean) LL.getTag()).getGoodsImage().size(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.each_baby_image_item, null);
            ImageView iv_goodsImage = (ImageView) view.findViewById(R.id.iv_baby_image_item);
            String url2 = NetUtil.imageUrl + ((QueryReleaseBean) LL.getTag()).getGoodsImage().get(i).getImageAddress();
            ImageOptions imageOptions2 = new ImageOptions.Builder()
                    .setFailureDrawableId(R.mipmap.ic_launcher)
                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                    .setCrop(true).build();
            x.image().bind(iv_goodsImage, url2, imageOptions2);
            LL.addView(view);
        }

    }

}