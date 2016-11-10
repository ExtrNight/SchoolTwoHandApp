package com.school.twohand.activity.taoquan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.school.twohand.customview.myrecyclerview.MyItemDecoration;
import com.school.twohand.customview.myrecyclerview.MyRecyclerViewItemClickListener;
import com.school.twohand.customview.myrecyclerview.MyRecyclerViewAdapter;
import com.school.twohand.schooltwohandapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 选择淘圈背景,使用了RecyclerView,加载图片使用的缓存机制
 */
public class SelectTaoquanBgActivity extends AppCompatActivity {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    public static final int ResultCode = 10;

    MyRecyclerViewAdapter myRecyclerViewAdapter;
    List<Integer> integerList = new ArrayList<>();  //存放资源Id的数据源
//    List<Integer> currentIntegerList = new ArrayList<>();  //当前的数据源
//    int pageNo = 1;  //第一页
//    int pageSize = 3;  //每页3条数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_taoquan_bg);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
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
//        CommonAdapter<Integer> taoquanBgAdapter = new CommonAdapter<Integer>(this,integerList,R.layout.taoquan_background_item) {
//            @Override
//            public void convert(ViewHolder viewHolder, Integer integer, int position) {
//                ImageView iv_taoquan_bg = viewHolder.getViewById(R.id.iv_taoquan_bg);
//                iv_taoquan_bg.setImageResource(integer);
//            }
//        };
//        lvSelectSystemBg.setAdapter(taoquanBgAdapter);
//        lvSelectSystemBg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent();
//                intent.putExtra("defaultBgId",position+1);
//                setResult(ResultCode,intent);
//                finish();
//            }
//        });

//        for(int i = (pageNo-1)*pageSize;i<2; i++){
//            currentIntegerList.add(integerList.get(i));
//        }

        initRecyclerView(integerList);

    }

    private void initRecyclerView(List<Integer> integerList){
        //设置布局:第3个参数，true:设置数据源是反的；
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置adapter
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(SelectTaoquanBgActivity.this, integerList);
        recyclerView.setAdapter(myRecyclerViewAdapter);
        //设置分隔符
        recyclerView.addItemDecoration(new MyItemDecoration(this, MyItemDecoration.VERTICAL_LIST));
        //设置动画：添加，删除的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //设置点击事件
        myRecyclerViewAdapter.setMyRecyclerViewItemClickListener(new MyRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //设置点击事件
//                Log.i("LinearRecycleView", "onItemClick: " + v + ",position:" + position);
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
//            case R.id.btn_add:
//                if(pageNo>0&&pageNo<=5){
//                    Log.i("SelectTaoquanBgActivity", "onClick: 1111");
//                    pageNo++;
////                    for(int i = (pageNo-1)*pageSize;i<3; i++){
////                        currentIntegerList.add(integerList.get(i));
////                    }
//                    currentIntegerList.add(integerList.get(3));
//                    myRecyclerViewAdapter.notifyItemInserted(3);
//                }
//                break;
//            case R.id.btn_remove:
//
//                break;
        }
    }


}
