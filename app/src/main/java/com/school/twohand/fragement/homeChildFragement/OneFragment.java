package com.school.twohand.fragement.homeChildFragement;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by Administrator on 2016/9/25 0025.
 */
public class OneFragment extends Fragment {
    View viewHead ;//显示头部轮播图的view
    ViewPager viewPagerHead;//头部轮播图的内容
    ListView listViewBody;//显示商品信息的listView
    List<Goods> goodsMessage = new ArrayList<>();//服务器获取到的数据源
    //查询方式
    final int TIMEDESC = 0;//时间降序
    final int PRICEUPTODWON = 1;//价格降序
    final int PRICEDOWNTOUP = 2;//价格升序
    final int SUREPRICE = 3;//一口价
    final int NOSUREPRICE = 4;//拍卖
    //定位学校名
    String schoolName;
    private ArrayList<View> viewPagers = new ArrayList<>();

    private ImageHandler handler = new ImageHandler(new WeakReference<OneFragment>(this));

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_child_page,container,false);
        //初始化显示商品详情的listView
        listViewBody = (ListView) view.findViewById(R.id.listViewBody);
        //找到头部轮播的view，并初始化
        initHeadView();
        //服务器获取商品详情将值赋值给listViewBody
        initListBody();
        return view;
    }

    //初始化头部轮播图
    public void initHeadView(){
        viewHead = LayoutInflater.from(getActivity()).inflate(R.layout.head_view,null,false);
        viewPagerHead = (ViewPager) viewHead.findViewById(R.id.viewPagerHead);
        //初始化viewPager的内容
        ImageView view1 = new ImageView(getActivity());
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView view2 = new ImageView(getActivity());
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView view3 = new ImageView(getActivity());
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
            }
        });

        view1.setBackgroundResource(R.drawable.slideshow_1);
        view2.setBackgroundResource(R.drawable.slideshow_2);
        view3.setBackgroundResource(R.drawable.slideshow_3);
        ArrayList<ImageView> views = new ArrayList<ImageView>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        viewPagerHead.setAdapter(new ImageAdapter(views));
        viewPagerHead.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //配合Adapter的currentItem字段进行设置。
            @Override
            public void onPageSelected(int arg0) {
                handler.sendMessage(Message.obtain(handler, ImageHandler.MSG_PAGE_CHANGED, arg0, 0));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            //重写该方法实现轮播效果的暂停和恢复
            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
                        if (handler.hasMessages(ImageHandler.MSG_UPDATE_IMAGE)){
                            handler.removeMessages(ImageHandler.MSG_UPDATE_IMAGE);
                        }
                        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                        break;
                    default:
                        break;
                }
            }
        });
        //viewPagerHead.setCurrentItem(Integer.MAX_VALUE/2);//默认在中间，使用户看不到边界
        int mid = Integer.MAX_VALUE/2;
        viewPagerHead.setCurrentItem(mid - mid/views.size());//改进
        //开始轮播效果
        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
    }

    //服务器获取商品详情将值赋值给listViewBody
    public void initListBody(){
        final RequestParams requestParams = new RequestParams(NetUtil.url+"QueryGoodsServlet");
        QueryGoodsBean queryGoodsBean = new QueryGoodsBean(null,schoolName,null,SUREPRICE,null,null);
        Gson gson = new Gson();
        String queryGoodsBeanString = gson.toJson(queryGoodsBean);
        requestParams.addQueryStringParameter("queryGoodsBean",queryGoodsBeanString);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(final String result) {
                Gson gson = new Gson();
                goodsMessage = gson.fromJson(result,new TypeToken<List<Goods>>(){}.getType());

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
                        if(goods.getGoodsImages().size()!=0){
                            String goodsUrl = NetUtil.imageUrl+goods.getGoodsImages().get(0).getImageAddress();
                            ImageOptions goodsImageOptions= new ImageOptions.Builder()
                                    .build();
                            x.image().bind(goodsImage,goodsUrl,goodsImageOptions);
                        }

                        //给控件赋值
                        userName.setText(goods.getGoodsUser().getUserName());
                        goodsPrice.setText("￥"+goods.getGoodsPrice()+"");

                        userSchool.setVisibility(View.GONE);
                        if(goods.getGoodsUserSchoolName()!=null){
                            userSchool.setVisibility(View.VISIBLE);
                            userSchool.setText("来自 "+goods.getGoodsUserSchoolName());
                        }else{
                            if(goods.getGoodsUser().getUserSchoolName()!=null){
                                userSchool.setVisibility(View.VISIBLE);
                                userSchool.setText("来自 "+goods.getGoodsUser().getUserSchoolName());
                            }
                        }
                        amoyCircle.setVisibility(View.GONE);
                        if(goods.getGoodsAmoyCircle()!=null){
                            amoyCircle.setVisibility(View.VISIBLE);
                            amoyCircle.setText("淘圈丨"+goods.getGoodsAmoyCircle().getCircleName());
                        }

                        like.setText("点赞"+goods.getGoodsLikes().size());
                        messageBoard.setText("留言"+goods.getGoodsMessageBoards().size());
                        goodsText.setText("<"+goods.getGoodsTitle()+">"+goods.getGoodsDescribe());
                    }
                };
                listViewBody.setAdapter(co);
                //添加头部轮播图
                listViewBody.addHeaderView(viewHead);

                //点击跳转详情界面
                listViewBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getContext(),DetailGoodsActivity.class);
                        intent.putExtra("goodsMessage",result);
                        intent.putExtra("position",position);
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

    //自定义ViewPager的适配器
    private class ImageAdapter extends PagerAdapter {

        private ArrayList<ImageView> viewlist;

        public ImageAdapter(ArrayList<ImageView> viewlist) {
            this.viewlist = viewlist;
        }

        @Override
        public int getCount() {
            //设置成最大，使用户看不到边界
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            //Warning：不要在这里调用removeView
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //对ViewPager页号求模取出View列表中要显示的项
            position %= viewlist.size();
            if (position<0){
                position = viewlist.size()+position;
            }
            ImageView view = viewlist.get(position);
            //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            ViewParent vp =view.getParent();
            if (vp!=null){
                ViewGroup parent = (ViewGroup)vp;
                parent.removeView(view);
            }
            container.addView(view);
            //add listeners here if necessary
            return view;
        }
    }

    //自定义的Handler，当轮播图执行的时候将会用到
    private static class ImageHandler extends Handler {
        //请求更新显示的View
        protected static final int MSG_UPDATE_IMAGE  = 1;
        //请求暂停轮播
        protected static final int MSG_KEEP_SILENT   = 2;
        //请求恢复轮播
        protected static final int MSG_BREAK_SILENT  = 3;
        /**
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
         * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
         */
        protected static final int MSG_PAGE_CHANGED  = 4;

        //轮播间隔时间
        protected static final long MSG_DELAY = 4000;  //3000

        //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
        private WeakReference<OneFragment> weakReference;
        private int currentItem = 0;

        //WeakReference:涉及到concept和java的GC，相关参考：http://www.tuicool.com/articles/imyueq
        protected ImageHandler(WeakReference<OneFragment> wk){
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            OneFragment fragment = weakReference.get();
            if (fragment==null){
                //Activity已经回收，无需再处理UI了
                return ;
            }
            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            //移到到每次发送“自动轮播”消息之前,写在ViewPager的onPageScrollStateChanged里了
//            if (fragment.handler.hasMessages(MSG_UPDATE_IMAGE)){
//                fragment.handler.removeMessages(MSG_UPDATE_IMAGE);
//            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    fragment.viewPagerHead.setCurrentItem(currentItem);
                    //准备下次播放
                    fragment.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT:
                    //只要不发送消息就暂停了
                    break;
                case MSG_BREAK_SILENT:
                    fragment.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED:
                    //记录当前的页号，避免播放的时候页面显示不正确。
                    currentItem = msg.arg1;
                    break;
                default:
                    break;
            }
        }
    }


}
