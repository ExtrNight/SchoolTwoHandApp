package com.school.twohand.fragement.taoquan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.school.twohand.entity.AmoyCircleDynamic;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.sql.Timestamp;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 创建动态的页面
 */
public class CreateTaoquanDynamicActivity extends AppCompatActivity {

    @InjectView(R.id.finish)
    ImageView finish;
    @InjectView(R.id.tv_publish_dynamic)
    TextView tvPublishDynamic;
    @InjectView(R.id.et_circle_dynamic_title)
    EditText etCircleDynamicTitle;
    @InjectView(R.id.et_circle_dynamic_content)
    EditText etCircleDynamicContent;
    @InjectView(R.id.iv_circle_dynamic_image)
    ImageView ivCircleDynamicImage;

    ProgressDialog pd;   //进度条，圆形

    private MyApplication myApplication;
    private User user;
    private int circleId;
    public static final int ResultCode = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_taoquan_dynamic);
        ButterKnife.inject(this);

        init();
    }

    private void init() {
        myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();
        Intent intent = getIntent();
        if(intent!=null){
            circleId = intent.getIntExtra("circleId",0);
        }

    }


    @OnClick({R.id.finish, R.id.iv_circle_dynamic_image, R.id.tv_publish_dynamic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.finish:
                finish();
                break;
            case R.id.iv_circle_dynamic_image:

                break;
            case R.id.tv_publish_dynamic:
                String title = etCircleDynamicTitle.getText().toString();
                String content = etCircleDynamicContent.getText().toString();
                if(title.equals("")){
                    Toast.makeText(CreateTaoquanDynamicActivity.this, "请输入标题哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(content.equals("")){
                    Toast.makeText(CreateTaoquanDynamicActivity.this, "请输入内容哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                AmoyCircleDynamic amoyCircleDynamic = new AmoyCircleDynamic(user,circleId,title,content,0,new Timestamp(System.currentTimeMillis()),null);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();//设置日期格式（24小时）
                String amoyCircleDynamicJson = gson.toJson(amoyCircleDynamic);
                RequestParams requestParams = new RequestParams(NetUtil.url+"/InsertCircleDynamicServlet");
                requestParams.addQueryStringParameter("amoyCircleDynamicJson",amoyCircleDynamicJson);
                pd = new ProgressDialog(CreateTaoquanDynamicActivity.this);
                pd.setMessage("发布中..");
                pd.show();
                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        setResult(ResultCode); //设置结果码，在activity里面判断，更新页面数据
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
                        //pd.dismiss();   //两种方式
                        pd.cancel();
                    }
                });

                break;
        }
    }




}
