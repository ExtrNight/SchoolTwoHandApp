package com.school.twohand.fragement.homeChildFragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.query.entity.QueryTopicBean;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by  echenglong on 2016/10/25.
 */
public class TopicPriaseFragment extends Fragment {

    ListView liPraiseTopic;

    CommonAdapter<QueryTopicBean> queryTopicBeanCommonAdapter;
    List<QueryTopicBean> queryTopicBean=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_topicpriase,null);
        liPraiseTopic= (ListView) view.findViewById(R.id.li_topicPraise);
        ButterKnife.inject(getActivity());
        getTopicData();
        return view;
    }
    private void getTopicData(){
        String url = NetUtil.url + "QueryPriaseTopicServlet";
        Integer userId=  ((MyApplication)getActivity().getApplication()).getUser().getUserId();
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("userId",userId+"");


        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type type = new TypeToken<List<QueryTopicBean>>() {
                }.getType();
                List<QueryTopicBean> newQueryTopicBean = gson.fromJson(result, type);

                queryTopicBean.clear();
                queryTopicBean.addAll(newQueryTopicBean);

                if (queryTopicBeanCommonAdapter == null) {
                    queryTopicBeanCommonAdapter = new CommonAdapter<QueryTopicBean>(getActivity(), queryTopicBean, R.layout.item_mybabytopic) {

                        @Override
                        public void convert(ViewHolder viewHolder, QueryTopicBean queryTopicBean, int position) {

                            ImageView a = viewHolder.getViewById(R.id.iv_userImage);
                            x.image().bind(a, NetUtil.imageUrl + queryTopicBean.getUserHead());

                            TextView b = viewHolder.getViewById(R.id.tv_userName);
                            b.setText(queryTopicBean.getUserName());

                            TextView c = viewHolder.getViewById(R.id.tv_time);
                            SimpleDateFormat aa = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date d1 = new Date(queryTopicBean.getAmoyCircleDynamicTime().getTime());
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
                            d.setText(queryTopicBean.getAmoyCircleDynamicTitle());


                            ImageView e = viewHolder.getViewById(R.id.iv_image_item);
                            if(queryTopicBean.getImageList().size()>0){
                                x.image().bind(e, NetUtil.imageUrl + queryTopicBean.getImageList().get(0));
                            }


                            TextView f = viewHolder.getViewById(R.id.tv_school);
                            f.setText("来自" + queryTopicBean.getUserAddress() + " 鱼塘|");

                            TextView g = viewHolder.getViewById(R.id.tv_priase);
                            g.setText("点赞" + queryTopicBean.getLikeSum());

                            TextView h = viewHolder.getViewById(R.id.tv_write);
                            h.setText("留言" + queryTopicBean.getMessageSum());

                            TextView i=viewHolder.getViewById(R.id.tv_circle);
                            i.setText(queryTopicBean.getAmoyCircleName());

                        }
                    };
                    liPraiseTopic.setAdapter(queryTopicBeanCommonAdapter);
                } else {
                    queryTopicBeanCommonAdapter.notifyDataSetChanged();
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

}

