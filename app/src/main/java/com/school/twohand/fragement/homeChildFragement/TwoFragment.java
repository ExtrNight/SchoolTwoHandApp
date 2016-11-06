package com.school.twohand.fragement.homeChildFragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.school.twohand.activity.DetailGoodsActivity;
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

/**
 * Created by Administrator on 2016/9/25 0025.
 */
public class TwoFragment extends Fragment {

    ListView listViewBody;//显示商品信息的listView
    List<Goods> goodsMessage = new ArrayList<>();//服务器获取到的数据源
    //查询方式
    final int TIMEDESC = 0;//时间降序
    final int PRICEUPTODWON = 1;//价格降序
    final int PRICEDOWNTOUP = 2;//价格升序
    final int SUREPRICE = 3;//一口价
    final int NOSUREPRICE = 4;//拍卖
    //定位学校id
    String schoolName;
    private ArrayList<View> viewPagers = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_child_page, container, false);
        //初始化显示商品详情的listView
        listViewBody = (ListView) view.findViewById(R.id.listViewBody);

        //服务器获取商品详情将值赋值给listViewBody
        initListBody();
        return view;
    }



    /**
     * 服务器获取商品详情将值赋值给listViewBody
     */
    public void initListBody(){
        final RequestParams requestParams = new RequestParams(NetUtil.url+"QueryGoodsServlet");
        QueryGoodsBean queryGoodsBean = new QueryGoodsBean(null,schoolName,null,NOSUREPRICE,null,null);
        Gson gson = new Gson();
        String queryGoodsBeanString = gson.toJson(queryGoodsBean);
        requestParams.addQueryStringParameter("queryGoodsBean",queryGoodsBeanString);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(final String result) {
                Gson gson = new Gson();
                goodsMessage = gson.fromJson(result,new TypeToken<List<Goods>>(){}.getType());
                Log.i("goodsMessage", "onSuccess: "+goodsMessage);
                //用通用适配器将数据源显示在listView上
                CommonAdapter<Goods> co = new CommonAdapter<Goods>(getActivity(),goodsMessage,R.layout.goods_message) {
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
                        goodsPrice.setText("起拍￥"+goods.getGoodsPrice()+"");
                        userSchool.setText("贵校丶"+goods.getGoodsUser().getUserSchoolName());
                        amoyCircle.setText("淘圈丨"+goods.getGoodsAmoyCircle().getCircleName());
                        like.setText("点赞"+goods.getGoodsLikes().size());
                        messageBoard.setText("留言"+goods.getGoodsMessageBoards().size());
                        goodsText.setText("<"+goods.getGoodsTitle()+">"+goods.getGoodsDescribe());
                    }
                };
                listViewBody.setAdapter(co);


                //点击跳转详情界面
                listViewBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getContext(),DetailGoodsActivity.class);
                        intent.putExtra("goodsMessage",result);
                        intent.putExtra("position",position+1);

                        startActivity(intent);
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
    }
}
