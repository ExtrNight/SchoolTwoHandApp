package com.school.twohand.activity.taoquan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 选择淘圈背景
 */
public class SelectTaoquanBgActivity extends AppCompatActivity {

    @InjectView(R.id.lv_select_system_bg)
    ListView lvSelectSystemBg;

    public static final int ResultCode = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_taoquan_bg);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        List<Integer> integerList = new ArrayList<>();
        integerList.add(R.drawable.taoquan_bg_1);
        integerList.add(R.drawable.taoquan_bg_2);
        integerList.add(R.drawable.taoquan_bg_3);
        integerList.add(R.drawable.taoquan_bg_4);
        integerList.add(R.drawable.taoquan_bg_5);
        integerList.add(R.drawable.taoquan_bg_6);
        integerList.add(R.drawable.taoquan_bg_7);
        integerList.add(R.drawable.taoquan_bg_8);
        integerList.add(R.drawable.taoquan_bg_9);
        integerList.add(R.drawable.taoquan_bg_10);
        integerList.add(R.drawable.taoquan_bg_11);
        integerList.add(R.drawable.taoquan_bg_12);
        integerList.add(R.drawable.taoquan_bg_13);
        integerList.add(R.drawable.taoquan_bg_14);
        integerList.add(R.drawable.taoquan_bg_15);
        CommonAdapter<Integer> taoquanBgAdapter = new CommonAdapter<Integer>(this,integerList,R.layout.taoquan_background_item) {
            @Override
            public void convert(ViewHolder viewHolder, Integer integer, int position) {
                ImageView iv_taoquan_bg = viewHolder.getViewById(R.id.iv_taoquan_bg);
                iv_taoquan_bg.setImageResource(integer);
            }
        };
        lvSelectSystemBg.setAdapter(taoquanBgAdapter);
        lvSelectSystemBg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("defaultBgId",position+1);
                setResult(ResultCode,intent);
                finish();
            }
        });
    }

    @OnClick({R.id.iv_return, R.id.LL_albumOrCamera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.LL_albumOrCamera:
                Intent intent = new Intent();
                intent.putExtra("isFromAlbumOrCamera","t");
                setResult(ResultCode,intent);
                finish();
                break;
        }
    }
}
