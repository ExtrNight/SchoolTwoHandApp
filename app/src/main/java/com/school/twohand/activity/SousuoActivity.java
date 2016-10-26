package com.school.twohand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.school.twohand.schooltwohandapp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SousuoActivity extends AppCompatActivity {

    @InjectView(R.id.sousuoText)
    EditText sousuoText;
    @InjectView(R.id.sousuoButton)
    Button sousuoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sousuo);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.sousuoButton)
    public void onClick() {

        Intent intent = new Intent(this,GoodsClassDetailActivity.class);
        intent.putExtra("sousuo",sousuoText.getText().toString());
        startActivity(intent);
    }
}
