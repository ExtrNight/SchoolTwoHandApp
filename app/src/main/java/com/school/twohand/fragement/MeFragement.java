package com.school.twohand.fragement;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.school.twohand.activity.MyAuctionActivity;
import com.school.twohand.activity.MyBuyActivity;
import com.school.twohand.activity.MyPriaseActivity;
import com.school.twohand.activity.MyReleaseActivity;
import com.school.twohand.activity.MySellActivity;
import com.school.twohand.activity.NumberAttentionActivity;
import com.school.twohand.activity.NumberFansActivity;
import com.school.twohand.activity.NumberPriaseActivity;
import com.school.twohand.schooltwohandapp.R;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class MeFragement extends Fragment {

RelativeLayout release;
RelativeLayout sell;
RelativeLayout buy;
RelativeLayout priase;
RelativeLayout auction;
FrameLayout numberPriase;
FrameLayout numberAttention;
FrameLayout numberFans;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_mine,container , false);
        release= (RelativeLayout) view.findViewById(R.id.rl_release);
        sell= (RelativeLayout) view.findViewById(R.id.rl_sell);
        buy= (RelativeLayout) view.findViewById(R.id.rl_buy);
        priase= (RelativeLayout) view.findViewById(R.id.rl_praise);
        auction= (RelativeLayout) view.findViewById(R.id.rl_auction);
        numberPriase= (FrameLayout) view.findViewById(R.id.fl_sum1);
        numberAttention= (FrameLayout) view.findViewById(R.id.fl_sum2);
        numberFans= (FrameLayout) view.findViewById(R.id.fl_sum3);

        release.setOnClickListener(new View.OnClickListener() {
                @Override
          public void onClick(View v) {
                    Intent intent=new Intent(getActivity(), MyReleaseActivity.class);
                    startActivity(intent);
          }
          });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(getActivity(), MySellActivity.class);
                startActivity(intent1);
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(getActivity(), MyBuyActivity.class);
                startActivity(intent2);
            }
        });

        priase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3=new Intent(getActivity(), MyPriaseActivity.class);
                startActivity(intent3);
            }
        });

        auction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4=new Intent(getActivity(), MyAuctionActivity.class);
                startActivity(intent4);
            }
        });

        numberPriase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5=new Intent(getActivity(), NumberPriaseActivity.class);
                startActivity(intent5);
            }
        });

        numberAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6=new Intent(getActivity(), NumberAttentionActivity.class);
                startActivity(intent6);
            }
        });

        numberFans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent7=new Intent(getActivity(), NumberFansActivity.class);
                startActivity(intent7);
            }
        });

        return view;
    }


}
