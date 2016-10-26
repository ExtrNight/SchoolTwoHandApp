package com.school.twohand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.school.twohand.schooltwohandapp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SecondPageActivity extends AppCompatActivity {

    @InjectView(R.id.goback)
    ImageView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.goback)
    public void onClick() {
        Intent intent=new Intent(SecondPageActivity.this,ShowActivity.class);
        startActivity(intent);
    }
}
