package com.school.twohand.fragement.homeChildFragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.school.twohand.query.entity.QueryCircleDetailBean;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.MyListView;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by C5-0 on 2016/11/07.
 */
public class CircleFragment extends Fragment {
    ListView lvCircle;
   
    CommonAdapter<QueryCircleDetailBean> queryCircleDetailBeanCommonAdapter;
    List<QueryCircleDetailBean> queryCircleDetailBean=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_circle,null);
        lvCircle= (MyListView) view.findViewById(R.id.li_circle);
        ButterKnife.inject(getActivity());
        getCircleData();
        return view;
    }

    public  void getCircleData(){
        String url= NetUtil.url+"QueryCircleDetailBeanServlet";
        Integer userId=  ((MyApplication)getActivity().getApplication()).getUser().getUserId();

        RequestParams requestParams=new RequestParams(url);

        requestParams.addQueryStringParameter("userId",userId+"");
         x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson=new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type type=new TypeToken<List<QueryCircleDetailBean>>(){
               }.getType();

                List<QueryCircleDetailBean> newQueryCircleDetailBean=gson.fromJson(result,type);


                queryCircleDetailBean.clear();
                queryCircleDetailBean.addAll(newQueryCircleDetailBean);

                if (queryCircleDetailBeanCommonAdapter==null){
                    queryCircleDetailBeanCommonAdapter=new CommonAdapter<QueryCircleDetailBean>(getActivity(),queryCircleDetailBean, R.layout.item_mycircle) {
                        @Override
                        public void convert(ViewHolder viewHolder, QueryCircleDetailBean queryCircleDetailBean, int position) {

                            //设置item中控件的取值
                            ImageView a=viewHolder.getViewById(R.id.iv_cicleImage);
                            x.image().bind(a, NetUtil.imageUrl + "image/" + queryCircleDetailBean.getImageUrl());

                            TextView b=viewHolder.getViewById(R.id.tv_circleName);
                            b.setText(queryCircleDetailBean.getCircleName());

                            TextView c=viewHolder.getViewById(R.id.tv_sumSay);
                            c.setText("人气" + queryCircleDetailBean.getCircleNumber() + "  发布" + queryCircleDetailBean.getReleaseSum());

                        }
                    };
                    lvCircle.setAdapter(queryCircleDetailBeanCommonAdapter);

                }else {
                    queryCircleDetailBeanCommonAdapter.notifyDataSetChanged();
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
