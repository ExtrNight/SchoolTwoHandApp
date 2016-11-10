package com.school.twohand.customview.myrecyclerview;

import android.view.View;

/**
 * Created by young on 2016/11/7.
 */
public interface MyRecyclerViewItemClickListener {
    //每个子的view,以及当前点击的item的位置
    void onItemClick(View v, int position);
}
