package com.school.twohand.activity.taoquan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 搜索淘圈的界面
 */
public class SearchActivity extends AppCompatActivity {

    @InjectView(R.id.iv_taoquan_search_return)
    ImageView ivTaoquanSearchReturn;
    @InjectView(R.id.et_query)
    EditText etQuery;
    @InjectView(R.id.ib_search_clear)
    ImageButton ibSearchClear;
    @InjectView(R.id.tv_search)
    TextView tvSearch;
    @InjectView(R.id.lv_search)
    ListView lvSearch;
    @InjectView(R.id.tv_result_null)
    TextView tvResultNull;

    CommonAdapter<AmoyCircle> circlesAdapter;
    List<AmoyCircle> amoyCircles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        initView();
        initEvent();

    }

    void initView() {
        ibSearchClear.setVisibility(View.GONE);  //开始将清空文本按钮隐藏
    }

    void initEvent() {
        //添加EditText的文本监听事件
        etQuery.addTextChangedListener(new MyTextChange());

    }

    //获取网络数据并显示
    void getData(String circleName) {
        String url = NetUtil.url + "QueryCirclesByServlet";
        RequestParams requestParams = new RequestParams(url);
        requestParams.addQueryStringParameter("circleName", circleName);

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    if (result.equals("[]")) { //如果没有搜索到数据
                        tvResultNull.setVisibility(View.VISIBLE);
                        return;
                    }
                    //根据不同的数据源，显示不同的淘圈信息在ListView上面
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<AmoyCircle>>() {
                    }.getType();
                    List<AmoyCircle> newAmoyCircles = gson.fromJson(result, type);
                    amoyCircles.clear();
                    amoyCircles.addAll(newAmoyCircles);

                    //设置ListView的数据源
                    if (circlesAdapter == null) {
                        circlesAdapter = new CommonAdapter<AmoyCircle>(SearchActivity.this, amoyCircles, R.layout.taoquan_mine_item) {
                            @Override
                            public void convert(ViewHolder viewHolder, AmoyCircle amoyCircle, int position) {
                                //设置淘圈名
                                TextView taoquan_mine_item_name = viewHolder.getViewById(R.id.taoquan_mine_item_name);
                                taoquan_mine_item_name.setText(amoyCircle.getCircleName());

                                //设置名下面的描述
                                TextView taoquan_mine_item_describe = viewHolder.getViewById(R.id.taoquan_mine_item_describe);
                                taoquan_mine_item_describe.setText("快进入淘圈看看吧~");

                                //设置淘圈人气
                                TextView taoquan_mine_item_popularity = viewHolder.getViewById(R.id.taoquan_mine_item_popularity);
                                taoquan_mine_item_popularity.setText("");

                                //设置淘圈头像
                                ImageView taoquan_image = viewHolder.getViewById(R.id.taoquan_mine_item_image);
                                String url = NetUtil.imageUrl + amoyCircle.getCircleImageUrl();

                                //设置图片样式
                                ImageOptions imageOptions = new ImageOptions.Builder()
                                        .setFailureDrawableId(R.mipmap.upload_circle_image)
                                        .setLoadingDrawableId(R.mipmap.upload_circle_image)
                                        .setCrop(true).build();          //是否裁剪？
                                x.image().bind(taoquan_image, url, imageOptions);
                            }
                        };
                        lvSearch.setAdapter(circlesAdapter);
                        //设置Item点击事件
                        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //lv,item,点击的item所在的位置（从0开始）,item的id
                                AmoyCircle amoyCircle = (AmoyCircle) parent.getItemAtPosition(position);
                                Intent intent = new Intent(SearchActivity.this, EachTaoquanActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("amoyCircle", amoyCircle);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    } else {
                        circlesAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }

    @OnClick({R.id.iv_taoquan_search_return, R.id.ib_search_clear, R.id.tv_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_taoquan_search_return: //点击返回
                finish();
                break;
            case R.id.ib_search_clear: //点击清空输入框
                etQuery.setText("");
                break;
            case R.id.tv_search: //点击搜索
                tvResultNull.setVisibility(View.GONE);
                if (etQuery.getText().toString().equals("")) {
                    Toast.makeText(SearchActivity.this, "搜索内容不能为空哦~", Toast.LENGTH_SHORT).show();
                } else {
                    getData(etQuery.getText().toString());
                }
                break;
        }
    }

    //TextWatcher:监听EditText文本改变
    class MyTextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) { //搜索框文本不为空，就将清空按钮显示
                if (ibSearchClear != null) {
                    ibSearchClear.setVisibility(View.VISIBLE);
                }
            } else {   //搜索框文本为空，隐藏清空按钮
                if (ibSearchClear != null) {
                    ibSearchClear.setVisibility(View.GONE);
                }
            }
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    }



}
