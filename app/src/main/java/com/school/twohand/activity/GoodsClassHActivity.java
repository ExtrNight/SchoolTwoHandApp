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

public class GoodsClassHActivity extends AppCompatActivity {

    @InjectView(R.id.tileText)
    TextView tileText;
    @InjectView(R.id.car)
    ImageView car;
    @InjectView(R.id.carText)
    TextView carText;
    @InjectView(R.id.RelCar)
    RelativeLayout RelCar;
    @InjectView(R.id.phone)
    ImageView phone;
    @InjectView(R.id.phoneText)
    TextView phoneText;
    @InjectView(R.id.Relphone)
    RelativeLayout Relphone;
    @InjectView(R.id.computer)
    ImageView computer;
    @InjectView(R.id.computerText)
    TextView computerText;
    @InjectView(R.id.Relcomputer)
    RelativeLayout Relcomputer;
    @InjectView(R.id.digital)
    ImageView digital;
    @InjectView(R.id.digitalText)
    TextView digitalText;
    @InjectView(R.id.Reldigital)
    RelativeLayout Reldigital;
    @InjectView(R.id.digit)
    ImageView digit;
    @InjectView(R.id.digitText)
    TextView digitText;
    @InjectView(R.id.Reldigit)
    RelativeLayout Reldigit;
    @InjectView(R.id.electrical)
    ImageView electrical;
    @InjectView(R.id.electricalText)
    TextView electricalText;
    @InjectView(R.id.Relelectrical)
    RelativeLayout Relelectrical;
    @InjectView(R.id.sport)
    ImageView sport;
    @InjectView(R.id.sportText)
    TextView sportText;
    @InjectView(R.id.Relsport)
    RelativeLayout Relsport;
    @InjectView(R.id.clothes)
    ImageView clothes;
    @InjectView(R.id.clothesText)
    TextView clothesText;
    @InjectView(R.id.Relclothes)
    RelativeLayout Relclothes;
    @InjectView(R.id.book)
    ImageView book;
    @InjectView(R.id.bookText)
    TextView bookText;
    @InjectView(R.id.Relbook)
    RelativeLayout Relbook;
    @InjectView(R.id.rent)
    ImageView rent;
    @InjectView(R.id.rentText)
    TextView rentText;
    @InjectView(R.id.Relrent)
    RelativeLayout Relrent;
    @InjectView(R.id.life)
    ImageView life;
    @InjectView(R.id.lifeText)
    TextView lifeText;
    @InjectView(R.id.Rellife)
    RelativeLayout Rellife;
    @InjectView(R.id.other)
    ImageView other;
    @InjectView(R.id.otherText)
    TextView otherText;
    @InjectView(R.id.Relother)
    RelativeLayout Relother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_class_h);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.RelCar, R.id.Relphone, R.id.Relcomputer, R.id.Reldigital, R.id.Reldigit, R.id.Relelectrical, R.id.Relsport, R.id.Relclothes, R.id.Relbook, R.id.Relrent, R.id.Rellife, R.id.Relother})
    public void onClick(View view) {
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
