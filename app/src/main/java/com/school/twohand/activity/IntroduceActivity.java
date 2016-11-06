package com.school.twohand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.school.twohand.schooltwohandapp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class IntroduceActivity extends AppCompatActivity {


    @InjectView(R.id.goback)
    ImageView goback;
    @InjectView(R.id.rl_finish)
    RelativeLayout rlFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        ButterKnife.inject(this);
    }


    @OnClick({R.id.goback, R.id.rl_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goback:
                finish();
                break;
            case R.id.rl_finish:
                EditText etProfile= (EditText) this.findViewById(R.id.editText);
                String profile=etProfile.getText().toString();
                Intent intent3=new Intent();
                intent3.putExtra("Profile",profile);
                setResult(RESULT_OK,intent3);
                finish();
                break;
        }
    }
}
