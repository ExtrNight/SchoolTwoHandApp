package com.school.twohand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.school.twohand.schooltwohandapp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class GoodsClassActivity extends AppCompatActivity {

    @InjectView(R.id.RelCar)
    RelativeLayout RelCar;
    @InjectView(R.id.Relphone)
    RelativeLayout Relphone;
    @InjectView(R.id.Relcomputer)
    RelativeLayout Relcomputer;
    @InjectView(R.id.Reldigital)
    RelativeLayout Reldigital;
    @InjectView(R.id.Reldigit)
    RelativeLayout Reldigit;
    @InjectView(R.id.Relelectrical)
    RelativeLayout Relelectrical;
    @InjectView(R.id.Relsport)
    RelativeLayout Relsport;
    @InjectView(R.id.Relclothes)
    RelativeLayout Relclothes;
    @InjectView(R.id.Relbook)
    RelativeLayout Relbook;
    @InjectView(R.id.Relrent)
    RelativeLayout Relrent;
    @InjectView(R.id.Rellife)
    RelativeLayout Rellife;
    @InjectView(R.id.Relother)
    RelativeLayout Relother;
    @InjectView(R.id.tileText)
    TextView titleTet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_class);
        ButterKnife.inject(this);





    }

    @OnClick({R.id.RelCar, R.id.Relphone, R.id.Relcomputer, R.id.Reldigital, R.id.Reldigit, R.id.Relelectrical, R.id.Relsport, R.id.Relclothes, R.id.Relbook, R.id.Relrent, R.id.Rellife, R.id.Relother})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.RelCar:
                intent.putExtra("result","1");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Relphone:
                intent.putExtra("result","2");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Relcomputer:
                intent.putExtra("result","3");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Reldigital:
                intent.putExtra("result","4");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Reldigit:
                intent.putExtra("result","5");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Relelectrical:
                intent.putExtra("result","6");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Relsport:
                intent.putExtra("result","7");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Relclothes:
                intent.putExtra("result","8");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Relbook:
                intent.putExtra("result","9");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Relrent:
                intent.putExtra("result","10");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Rellife:
                intent.putExtra("result","11");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

            case R.id.Relother:
                intent.putExtra("result","12");

                    setResult(RESULT_OK,intent);
                    finish();
                    break;

        }
    }
}
