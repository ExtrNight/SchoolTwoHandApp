package com.school.twohand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.query.entity.QueryGetAddressBean;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GetAddressActivity extends AppCompatActivity {
    User user;

    @InjectView(R.id.li_getAddress)
    ListView liGetAddress;

    CommonAdapter<QueryGetAddressBean> queryGetAddressBeanCommonAdapter;
    List<QueryGetAddressBean> queryGetAddressBean = new ArrayList<>();
    @InjectView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @InjectView(R.id.goback)
    ImageView goback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_address);
        ButterKnife.inject(this);
        getData();
    }

    public void getData() {
        String url = NetUtil.url + "QueryGetAddressBeanServlet";
        Integer userId = ((MyApplication) this.getApplication()).getUser().getUserId();
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("userId", userId + "");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<QueryGetAddressBean>>() {
                }.getType();

                List<QueryGetAddressBean> newQueryGetAddressBean = new ArrayList<QueryGetAddressBean>();
                newQueryGetAddressBean = gson.fromJson(result, type);
                Log.i("GetAddressActivity", "onSuccess: " + result);
                queryGetAddressBean.clear();
                queryGetAddressBean.addAll(newQueryGetAddressBean);

                if (queryGetAddressBeanCommonAdapter == null) {
                    queryGetAddressBeanCommonAdapter = new CommonAdapter<QueryGetAddressBean>(GetAddressActivity.this, queryGetAddressBean, R.layout.item_getaddress) {
                        @Override
                        public void convert(ViewHolder viewHolder, QueryGetAddressBean queryGetAddressBean, int position) {
                            //设置item中控件的取值
                            TextView a = viewHolder.getViewById(R.id.tv_receievename);
                            a.setText("收货人: " + queryGetAddressBean.getUserName());

                            TextView b = viewHolder.getViewById(R.id.tv_phoneNumber);
                            b.setText("联系方式: " + queryGetAddressBean.getReceiptNumber());

                            TextView c = viewHolder.getViewById(R.id.tv_address);
                            c.setText("收货地址: " + queryGetAddressBean.getReceiptDetail());
                        }
                    };
                    liGetAddress.setAdapter(queryGetAddressBeanCommonAdapter);

                } else {
                    queryGetAddressBeanCommonAdapter.notifyDataSetChanged();
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

    @OnClick({R.id.goback, R.id.rl_bottom})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goback:
                finish();
                break;
            case R.id.rl_bottom:
                Intent intent=new Intent(GetAddressActivity.this,AddAddressActivity.class);
                startActivity(intent);
                break;
        }
    }

}
