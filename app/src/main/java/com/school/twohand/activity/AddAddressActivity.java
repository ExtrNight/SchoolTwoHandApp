package com.school.twohand.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.school.twohand.entity.Receipt;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddAddressActivity extends AppCompatActivity {

    @InjectView(R.id.goback)
    ImageView goback;
    @InjectView(R.id.rl_finish)
    RelativeLayout rlFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.goback, R.id.rl_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goback:
                finish();
                break;
            case R.id.rl_finish:
                EditText etNumber= (EditText) this.findViewById(R.id.editText2);
                EditText etAddress= (EditText) this.findViewById(R.id.editText);
                String number=etNumber.getText().toString();
                String address=etAddress.getText().toString();


                Receipt receipt = new Receipt();
                receipt.setReceiptDetailed(address);
                receipt.setReceiptContactNumber(number);
                receipt.setReceiptUser(((MyApplication)getApplication()).getUser());
                Gson gson = new Gson();
                String receiptString = gson.toJson(receipt);
                //新增地址存入数据库
                RequestParams requestParams = new RequestParams(NetUtil.url+"InsertRecepitAddressServlet");
                requestParams.addQueryStringParameter("receipt",receiptString);
                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        finish();
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
                break;
        }
    }
}
