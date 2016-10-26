package com.school.twohand.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.school.twohand.schooltwohandapp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NumberPriaseActivity extends AppCompatActivity {

    @InjectView(R.id.goback)
    ImageView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_priase);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.goback)
    public void onClick() {
    finish();
    }
}
