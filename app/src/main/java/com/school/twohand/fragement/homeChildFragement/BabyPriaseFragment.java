package com.school.twohand.fragement.homeChildFragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.query.entity.QueryPriaseBean;
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
public class BabyPriaseFragment extends Fragment {
    ListView liPriase;

    CommonAdapter<QueryPriaseBean> queryPriaseBeanCommonAdapter;
    List<QueryPriaseBean> queryPriaseBean=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_babypriase,null);
        liPriase= (ListView) view.findViewById(R.id.li_babyPraise);
        ButterKnife.inject(getActivity());
        getPriaseData();
        return view;
    }

    private void getPriaseData(){
        String url = NetUtil.url + "QueryPriaseServlet";
        Integer userId=  ((MyApplication)getActivity().getApplication()).getUser().getUserId();
        RequestParams requestParams = new RequestParams(url);

        requestParams.addQueryStringParameter("userId", userId + "");


        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("BabyPriaseFragment", "onSuccess: "+result);
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type type = new TypeToken<List<QueryPriaseBean>>() {
                }.getType();
                List<QueryPriaseBean> newQueryPriaseBean = gson.fromJson(result, type);

                queryPriaseBean.clear();
                queryPriaseBean.addAll(newQueryPriaseBean);

                if (queryPriaseBeanCommonAdapter == null) {
                    queryPriaseBeanCommonAdapter = new CommonAdapter<QueryPriaseBean>(getActivity(), queryPriaseBean, R.layout.item_mybabypriase) {

                        @Override
                        public void convert(ViewHolder viewHolder, QueryPriaseBean queryPriaseBean, int position) {
                            Log.i("TopicFragment", "convert: convert+11");
                            ImageView a = viewHolder.getViewById(R.id.iv_userImage);
                            x.image().bind(a, NetUtil.imageUrl + queryPriaseBean.getUserHead());

                            TextView b = viewHolder.getViewById(R.id.tv_userName);
                            b.setText(queryPriaseBean.getUserName());

                            TextView c = viewHolder.getViewById(R.id.tv_time);
                            SimpleDateFormat aa = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date d1 = new Date(queryPriaseBean.getGoodsReleaseTime().getTime());
                            Date d2 = new Date(System.currentTimeMillis());
                            long diff = d2.getTime() - d1.getTime();
                            long days = diff / (1000 * 60 * 60 * 24);
                            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                            if (days == 0) {
                                c.setText(hours + "小时前");
                            } else {
                                c.setText(days + "天" + hours + "小时前");
                            }

                            TextView d = viewHolder.getViewById(R.id.tv_describe);
                            d.setText(queryPriaseBean.getGoodsDescribe());

                            TextView e=viewHolder.getViewById(R.id.tv_Price);
                            e.setText("￥" + queryPriaseBean.getGoodsPrice());


                            LinearLayout LL = viewHolder.getViewById(R.id.LL);
                            LL.setTag(queryPriaseBean);
                            addLLView(LL, position);

                            TextView f = viewHolder.getViewById(R.id.tv_school);
                            f.setText("来自" + queryPriaseBean.getUserAddress() + " 鱼塘|");

                            TextView i=viewHolder.getViewById(R.id.tv_circle);
                            i.setText(queryPriaseBean.getAmoyCircleName());

                            TextView g = viewHolder.getViewById(R.id.tv_priase);
                            g.setText("点赞" + queryPriaseBean.getLikeSum());

                            TextView h = viewHolder.getViewById(R.id.tv_write);
                            h.setText("留言" + queryPriaseBean.getMessageSum());

                        }
                    };
                    liPriase.setAdapter(queryPriaseBeanCommonAdapter);
                } else {
                    queryPriaseBeanCommonAdapter.notifyDataSetChanged();
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

    public void addLLView(LinearLayout LL, final int position) {
        LL.removeAllViews(); //加之前要先把之前的remove掉，！！！
        for (int i = 0; i < ((QueryPriaseBean) LL.getTag()).getGoodsImage().size(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.each_babypraise_image_item, null);
            ImageView iv_goodsImage = (ImageView) view.findViewById(R.id.iv_babypriase_image_item);
            String url2 = NetUtil.imageUrl+((QueryPriaseBean) LL.getTag()).getGoodsImage().get(i).getImageAddress();
            ImageOptions imageOptions2 = new ImageOptions.Builder()
                    .setFailureDrawableId(R.mipmap.ic_launcher)
                    .setLoadingDrawableId(R.mipmap.ic_launcher)
                    .setCrop(true).build();
            x.image().bind(iv_goodsImage, url2, imageOptions2);
            LL.addView(view);
        }

    }

}