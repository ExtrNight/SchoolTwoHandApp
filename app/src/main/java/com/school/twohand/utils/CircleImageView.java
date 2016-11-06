package com.school.twohand.utils;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.school.twohand.schooltwohandapp.R;


public class CircleImageView extends ImageView {
    //缩放类型
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;//中心点缩放
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;//4个8位组成32位的ARGB位图
    private static final int COLORDRAWABLE_DIMENSION = 2;//未知？？

    //边界属性
    private static final int DEFAULT_BORDER_WIDTH = 0;//边界宽度,默认边界为0
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;//边界颜色，默认边界颜色为黑色
    private static final boolean DEFAULT_BORDER_OVERLAY = false;//未知？？

    //什么是rectf：用于表示坐标系中的一块矩形区域，并可以对其做一些简单操作。这块矩形区域，需要用左上和右下两个坐标点表示。
    //与rect区别：精度不一样。Rect是使用int类型作为数值，RectF是使用float类型作为数值。
    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();
    /*
     *Android中的Matrix是一个3 x 3的矩阵，
     * 可以 控制缩放，平移 ， 旋转等
     * 放大两倍： matrix.setScale(2f, 2f);
     *
     */
    private final Matrix mShaderMatrix = new Matrix();

    //初始化画笔工具
    private final Paint mBitmapPaint = new Paint();//这个画笔最重要的是关联了mBitmapShader 使canvas在执行的时候可以切割原图片(mBitmapShader是关联了原图的bitmap的)
    private final Paint mBorderPaint = new Paint();//这个描边，则与本身的原图bitmap没有任何关联
    //边界颜色和宽度
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private Bitmap mBitmap;
    /*
     * BitmapShader mBitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        参数1：bitmap
        参数2，参数3：TileMode；

        TileMode的取值有三种：
        CLAMP 拉伸：这个和电脑屏保的模式应该有些不同，这个拉伸的是图片最后的那一个像素；横向的最后一个横行像素，不断的重复，纵项的那一列像素，不断的重复；
        REPEAT 重复：就是横向、纵向不断重复这个bitmap
        MIRROR 镜像：横向不断翻转重复，纵向不断翻转重复；
     */
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;//位图宽度
    private int mBitmapHeight;//位图高度

    private float mDrawableRadius;// 图片半径
    private float mBorderRadius;// 带边框的的图片半径
    //色彩过滤器
    private ColorFilter mColorFilter;
    //初始false
    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverlay;
    //三个构造方法
    public CircleImageView(Context context) {
        this(context,null);
    }
    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        /*
            通过obtainStyledAttributes 获得一组值赋给 TypedArray（数组） ,
            这一组值来自于res/values/attrs.xml中的name="CircleImageView"的declare-styleable中。
        */
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);
        //通过TypedArray提供的一系列方法getXXXX取得我们在xml里定义的参数值；
        // 获取边界的宽度
        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
        // 获取边界的颜色
        mBorderColor = a.getColor(R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR);
        mBorderOverlay = a.getBoolean(R.styleable.CircleImageView_border_overlay, DEFAULT_BORDER_OVERLAY);
        //调用 recycle() 回收TypedArray,以便后面重用
        a.recycle();

        init();
    }
    /**
     * 作用就是保证第一次执行setup函数里下面代码要在构造函数执行完毕时调用
     */
    private void init() {
        super.setScaleType(SCALE_TYPE);
        mReady = true;
        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }
    /**
     * 这里明确指出 此种imageview 只支持CENTER_CROP 这一种属性
     *
     * @param scaleType
     */
    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }


    /*
    设置View的最大高度,单独使用无效,需要与setAdjustViewBounds一起使用.如果想设置图片固定大小,又想保持图片宽高比,需要如下设置:
    1.设置setAdjustViewBounds为true;
    2.设置setMaxHeight,setMaxWidth;
    3.设置设置对应layout_width或layout_height为wrap_content.
    */
    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //如果图片不存在就不画
        if (getDrawable() == null) {
            return;
        }
        //绘制内圆形 图片 画笔为mBitmapPaint
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
        //如果边界长度不为0则我们还要绘制带边界的外圆形 边界画笔为mBorderPaint
        if (mBorderWidth != 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public void setBorderColorResource(@ColorRes int borderColorRes) {
        setBorderColor(getContext().getResources().getColor(borderColorRes));
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay == mBorderOverlay) {
            return;
        }

        mBorderOverlay = borderOverlay;
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        mBitmapPaint.setColorFilter(mColorFilter);
        invalidate();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        //因为mReady默认值为false,所以第一次进这个函数的时候if语句为真进入括号体内
        //设置mSetupPending为true然后直接返回，后面的代码并没有执行。
        if (!mReady) {
            mSetupPending = true;
            return;
        }
        //防止空指针异常
        if (mBitmap == null) {
            return;
        }
        // 构建渲染器，用mBitmap位图来填充绘制区域 ，参数值代表如果图片太小的话 就直接拉伸
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        // 设置图片画笔反锯齿
        mBitmapPaint.setAntiAlias(true);
        // 设置图片画笔渲染器
        mBitmapPaint.setShader(mBitmapShader);
        // 设置边界画笔样式，让画出的图形是空心的
        mBorderPaint.setStyle(Paint.Style.STROKE);
        // 设置图片画笔反锯齿
        mBorderPaint.setAntiAlias(true);
        // 设置边界画笔样式
        mBorderPaint.setColor(mBorderColor);
        //画笔边界宽度
        mBorderPaint.setStrokeWidth(mBorderWidth);
        //这个地方是取的原图片的宽高
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();
        //设置含边界显示区域，取的是CircleImageView的布局实际大小，为方形，查看xml也就是160dp(240px)  getWidth得到是某个view的实际尺寸
        mBorderRect.set(0, 0, getWidth(), getHeight());
        //计算 圆形带边界部分（外圆）的最小半径，取mBorderRect的宽高减去一个边缘大小的一半的较小值（这个地方我比较纳闷为什么求外圆半径需要先减去一个边缘大小）
        //猜想：setStrokeWidth设置边界宽度是，当前半径加上边界宽度，所以要减去边界宽度才可以让外圆和内圆贴合
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);
        // 初始图片显示区域为mBorderRect（CircleImageView的布局实际大小）
        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay) {
            //demo里始终执行
            //通过inset方法  使得图片显示的区域从mBorderRect大小上下左右内移边界的宽度形成区域
            /*
            public void inset(float dx, float dy) {
                 left    += dx;
                 top     += dy;
                 right   -= dx;
                 bottom  -= dy;
            }
             */
            mDrawableRect.inset(mBorderWidth, mBorderWidth);
        }
        //这里计算的是内圆的最小半径，也即去除边界宽度的半径
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);
        //设置渲染器的变换矩阵也即是mBitmap用何种缩放形式填充
        updateShaderMatrix();
        invalidate();
    }
    /**
     * 这个函数为设置BitmapShader的Matrix参数，设置最小缩放比例，平移参数。
     * 作用：保证图片损失度最小和始终绘制图片正中央的那部分
     */
    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);
        //这里不好理解 这个不等式也就是(mBitmapWidth / mDrawableRect.width()) > (mBitmapHeight / mDrawableRect.height())
        //取最小的缩放比例这样图片不会太小
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx) + mDrawableRect.left, (int) (dy ) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}