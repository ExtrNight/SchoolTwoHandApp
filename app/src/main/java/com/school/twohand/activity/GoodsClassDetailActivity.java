package com.school.twohand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.entity.Goods;
import com.school.twohand.query.entity.QueryGoodsBean;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.NetUtil;
import com.school.twohand.utils.ViewHolder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GoodsClassDetailActivity extends AppCompatActivity {

    @InjectView(R.id.goodsList)
    ListView goodsList;//显示商品信息的listView
    //定位学校id
    Integer schoolId;
    //分类id
    String classId;
    List<Goods> goodsMessage = new ArrayList<>();//服务器获取到的数据源
    String sousuo ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_class_detail);
        ButterKnife.inject(this);
        classId = getIntent().getStringExtra("result");
        sousuo = getIntent().getStringExtra("sousuo");
        initListBody();
    }

    public void initListBody(){
        final RequestParams requestParams = new RequestParams(NetUtil.url+"QueryGoodsServlet");
        QueryGoodsBean queryGoodsBean = null;
        if (sousuo!=null&&classId==null){
            queryGoodsBean = new QueryGoodsBean(sousuo,schoolId,null,0,null,null);
        }else if (sousuo==null&&classId!=null){
             queryGoodsBean = new QueryGoodsBean(null,schoolId,classId,0,null,null);
        }

        Gson gson = new Gson();
        String queryGoodsBeanString = gson.toJson(queryGoodsBean);
        requestParams.addQueryStringParameter("queryGoodsBean",queryGoodsBeanString);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(final String result) {
                Gson gson = new Gson();
                goodsMessage = gson.fromJson(result,new TypeToken<List<Goods>>(){}.getType());

                //用通用适配器将数据源显示在listView上
                CommonAdapter<Goods> co = new CommonAdapter<Goods>(GoodsClassDetailActivity.this,goodsMessage,R.layout.goods_message) {
                    @Override
                    public void convert(ViewHolder viewHolder, Goods goods, int position) {
                        ImageView userHeadView = viewHolder.getViewById(R.id.user_head_t);//用户头像
                        TextView userName = viewHolder.getViewById(R.id.user_name_t);//用户名
                        TextView goodsPrice = viewHolder.getViewById(R.id.goods_price_t);//商品价格
                        ImageView goodsImage = viewHolder.getViewById(R.id.goods_image_t);//商品图片
                        TextView userSchool = viewHolder.getViewById(R.id.user_school_t);//用户学校
                        TextView amoyCircle = viewHolder.getViewById(R.id.amoy_circle_t);//淘圈名
                        TextView like = viewHolder.getViewById(R.id.like_t);//点赞
                        TextView messageBoard = viewHolder.getViewById(R.id.message_t);//留言
                        TextView goodsText = viewHolder.getViewById(R.id.goods_text_t);//商品描述
                        //从数据库获取头像
                        String userHeadUrl=NetUtil.imageUrl+goods.getGoodsUser().getUserHead();
                        ImageOptions userImageOptions=new ImageOptions.Builder()
                                .setCircular(true)
                                .build();
                        x.image().bind(userHeadView,userHeadUrl,userImageOptions);

                        //从数据库获取商品图片
                        /**
                         * 修改为多图片滑动滑到最后一张的时候可以进入详情或者点击进入详情
                         */
                        String goodsUrl = NetUtil.imageUrl+goods.getGoodsImages().get(0).getImageAddress();
                        ImageOptions goodsImageOptions= new ImageOptions.Builder()
                                .build();
                        x.image().bind(goodsImage,goodsUrl,goodsImageOptions);

                        //给控件赋值
                        userName.setText(goods.getGoodsUser().getUserName());
                        goodsPrice.setText("￥"+goods.getGoodsPrice()+"");
                        userSchool.setText("贵校丶"+goods.getGoodsUserSchoolName());
                        amoyCircle.setText("淘圈丨"+goods.getGoodsAmoyCircle().getCircleName());
                        like.setText("点赞"+goods.getGoodsLikes().size());
                        messageBoard.setText("留言"+goods.getGoodsMessageBoards().size());
                        goodsText.setText("<"+goods.getGoodsTitle()+">"+goods.getGoodsDescribe());
                    }
                };
                goodsList.setAdapter(co);


                //点击跳转详情界面
                goodsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(GoodsClassDetailActivity.this,DetailGoodsActivity.class);
                        intent.putExtra("goodsMessage",result);
                        intent.putExtra("position",position+1);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("oneFragment", "onError: ");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                Log.i("oneFragment", "onFinished: ");
            }
        });
    }
}
