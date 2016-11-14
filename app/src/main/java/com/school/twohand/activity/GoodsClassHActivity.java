package com.school.twohand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.school.twohand.schooltwohandapp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GoodsClassHActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_class_h);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.RelCar, R.id.Relphone, R.id.Relcomputer, R.id.Reldigital,
            R.id.Reldigit, R.id.Relelectrical, R.id.Relsport, R.id.Relclothes, R.id.Relbook,
            R.id.Relrent, R.id.Rellife, R.id.Relother, R.id.iv_classify_return})
    public void onClick(View view) {
        if(view.getId()==R.id.iv_classify_return){
            finish();
            return;
        }
        Intent intent = new Intent(this,GoodsClassDetailActivity.class);
        switch (view.getId()) {
            case R.id.RelCar:
                intent.putExtra("result","1");
                break;
            case R.id.Relphone:
                intent.putExtra("result","2");
                break;
            case R.id.Relcomputer:
                intent.putExtra("result","3");
                break;
            case R.id.Reldigital:
                intent.putExtra("result","4");
                break;
            case R.id.Reldigit:
                intent.putExtra("result","5");
                break;
            case R.id.Relelectrical:
                intent.putExtra("result","6");
                break;
            case R.id.Relsport:
                intent.putExtra("result","7");
                break;
            case R.id.Relclothes:
                intent.putExtra("result","8");
                break;
            case R.id.Relbook:
                intent.putExtra("result","9");
                break;
            case R.id.Relrent:
                intent.putExtra("result","10");
                break;
            case R.id.Rellife:
                intent.putExtra("result","11");
                break;
            case R.id.Relother:
                intent.putExtra("result","12");
                break;
        }
        startActivity(intent);
    }




}
