package com.school.twohand.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.school.twohand.schooltwohandapp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import c.b.BP;
import c.b.PListener;

/**
 * Created by dliu on 2016/10/8.
 */
public class GoPayActivity extends AppCompatActivity {

    @InjectView(R.id.gopay_prodmoney)
    TextView gopayProdmoney;
    @InjectView(R.id.gopay_servicemoney)
    TextView gopayServicemoney;
    @InjectView(R.id.gopay_yunfumoney)
    TextView gopayYunfumoney;
    @InjectView(R.id.gopay_youhuimoney)
    TextView gopayYouhuimoney;
    @InjectView(R.id.gopay_shifumoney)
    TextView gopayShifumoney;
    @InjectView(R.id.gopay_order_info)
    LinearLayout gopayOrderInfo;
    @InjectView(R.id.gopay_weixinpay)
    ImageView gopayWeixinpay;
    @InjectView(R.id.gopay_select_weixin)
    ImageView gopaySelectWeixin;
    @InjectView(R.id.gopay_rl_weixinpay)
    RelativeLayout gopayRlWeixinpay;
    @InjectView(R.id.gopay_pay)
    Button gopayPay;
    ProgressDialog dialog;
    String AppId  = "42dab6855427c532053895c9fc3930a7";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_pay);
        ButterKnife.inject(this);
        BP.init(this,AppId);
    }


    @OnClick({ R.id.gopay_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gopay_pay:
                //立即支付
                pay(false);
                BP.pay("商品名称", "商品描述", 0.02, false, new PListener() {
                    @Override
                    public void orderId(String s) {

                    }

                    @Override
                    public void succeed() {

                    }

                    @Override
                    public void fail(int i, String s) {

                    }

                    @Override
                    public void unknow() {

                    }
                });
                break;
        }
    }

    /**
     * 调用支付
     *
     * @param alipayOrWechatPay
     *            支付类型，true为支付宝支付,false为微信支付
     */
    void pay(final boolean alipayOrWechatPay) {
        showDialog("正在获取订单...");
        final String name = getName();

        BP.pay(name, getBody(), getPrice(), alipayOrWechatPay, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(GoPayActivity.this, "支付结果未知,请稍后手动查询"+name+"'s pay status is unknow", Toast.LENGTH_SHORT)
                        .show();

                hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                Toast.makeText(GoPayActivity.this, "支付成功!"+name + "+'s pay status is success", Toast.LENGTH_SHORT).show();

                hideDialog();
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询

                Toast.makeText(GoPayActivity.this,name + "'s orderid is " + orderId , Toast.LENGTH_SHORT).show();
                //showDialog("获取订单成功!请等待跳转到支付页面~");
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(
                            GoPayActivity.this,
                            "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(GoPayActivity.this, "支付中断!", Toast.LENGTH_SHORT)
                            .show();
                }
                Toast.makeText(GoPayActivity.this, name + "'s pay status is fail, error code is "
                        + code + " ,reason is " + reason , Toast.LENGTH_SHORT).show();

                hideDialog();
            }
        });
    }
    void showDialog(String message) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(this);
                dialog.setCancelable(true);
            }
            dialog.setMessage(message);
            dialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    void hideDialog() {
        if (dialog != null && dialog.isShowing())
            try {
                dialog.dismiss();
            } catch (Exception e) {
            }
    }

    // 默认为0.02
    double getPrice() {
        double price = 0.02;

        return price;
    }
    // 商品详情(可不填)
    String getName() {
        return "dddd";
    }

    // 商品详情(可不填)
    String getBody() {
        return "dddddd";
    }

    // 支付订单号(查询时必填)
    String getOrder() {
        return "dddddd";
    }
}
