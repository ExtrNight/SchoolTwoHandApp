package com.school.twohand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SousuoActivity extends AppCompatActivity {
    //测试SearchView
    SearchView searchView;
    private ListView mListView;
    //数据源：默认从数据库返回是个模糊搜索结果
    String allNewText;
    List<String> dataList= new ArrayList<>();
    TextView sousuoButton;
    ImageView iv_search_return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sousuo);
        //找寻控件
        iv_search_return = (ImageView) findViewById(R.id.iv_search_return);
        searchView = (SearchView) findViewById(R.id.search_view);
        mListView = (ListView) findViewById(R.id.listView);
        sousuoButton = (TextView) findViewById(R.id.tv_search);

        if (dataList.size()!=0){
            initData();
        }

        iv_search_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(SousuoActivity.this, "您选择的是："+query, Toast.LENGTH_SHORT).show();
                initEvent(query);
                return true;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(final String newText) {
                RequestParams requestParams = new RequestParams(NetUtil.url+"QueryGoodsNameServlet");
                requestParams.addQueryStringParameter("goodsTitle",newText);
                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(final String result) {
                        dataList.clear();
                        Log.i("dataList", "onSuccess: "+result);
                        Gson gson = new Gson();
                        Map<String, Integer> goodsMap = gson.fromJson(result,new TypeToken<Map<String,Integer>>(){}.getType());
                        //Map的循环遍历
                        Iterator iter = goodsMap.keySet().iterator();
                        while (iter.hasNext()){
                            String key = (String) iter.next();
                            dataList.add(key);
                        }
                        if (dataList.size()!=0){
                            initData();
                        }
                        if (!TextUtils.isEmpty(newText)){
                            mListView.setFilterText(newText);
                        }else{
                            mListView.clearTextFilter();
                        }

                        //搜索按钮跳转事件
                        sousuoButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (newText.length()!=0){
                                    //Toast.makeText(SousuoActivity.this,newText, Toast.LENGTH_SHORT).show();
                                    initEvent(newText);
                                }else {
                                    Toast.makeText(SousuoActivity.this, "还未输入", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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

                return false;
            }
        });
    }


    //初始化数据源
    public void initData(){

        mListView.setAdapter(new CommonAdapter<String>(this,dataList,R.layout.search_list_item) {
            @Override
            public void convert(ViewHolder viewHolder, String s, int position) {
                TextView tv = viewHolder.getViewById(R.id.amoyItem);
                tv.setText(s);
            }
        });
        mListView.setTextFilterEnabled(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int id1 = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                EditText textView = (EditText) searchView.findViewById(id1);
                allNewText=dataList.get(position);
                textView.setText(allNewText);
                textView.setSelection(textView.getText().length());
                initEvent(allNewText);
            }
        });
    }

    public void initEvent(String data){
        Intent intent = new Intent(this,GoodsClassDetailActivity.class);
        intent.putExtra("sousuo",data);
        startActivity(intent);
    }
    /*@OnClick(R.id.sousuoButton)
    public void onClick() {

        Intent intent = new Intent(this,GoodsClassDetailActivity.class);
        //intent.putExtra("sousuo",);
        startActivity(intent);
    }*/
}
