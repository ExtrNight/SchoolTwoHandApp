package com.school.twohand.activity.taoquan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.king.photo_library.ImagesSelectorActivity;
import com.king.photo_library.SelectorSettings;
import com.school.twohand.customview.loadingview.ShapeLoadingDialog;
import com.school.twohand.entity.AmoyCircleDynamic;
import com.school.twohand.entity.AmoyCircleDynamicImage;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 创建动态的页面
 */
public class CreateTaoquanDynamicActivity extends AppCompatActivity {

    @InjectView(R.id.finish)
    ImageView finish;
    @InjectView(R.id.tv_publish_dynamic)
    TextView tvPublishDynamic;
    @InjectView(R.id.et_circle_dynamic_title)
    EditText etCircleDynamicTitle;
    @InjectView(R.id.et_circle_dynamic_content)
    EditText etCircleDynamicContent;
    @InjectView(R.id.iv_photo1)
    ImageView ivPhoto1;
    @InjectView(R.id.iv_photo2)
    ImageView ivPhoto2;
    @InjectView(R.id.iv_photo3)
    ImageView ivPhoto3;
    @InjectView(R.id.iv_photo4)
    ImageView ivPhoto4;
    @InjectView(R.id.iv_photo5)
    ImageView ivPhoto5;
    @InjectView(R.id.iv_add_photo)
    ImageView ivAddPhoto;
    private ShapeLoadingDialog shapeLoadingDialog; //带有动画效果的加载

    private User user;
    private int circleId;
    public static final int ResultCode = 11;
    ProgressDialog pd;   //进度条，圆形
    private static final int REQUEST_CODE = 732;    //请求图库
    private ArrayList<String> mResults = new ArrayList<>();
    private List<File> files = new ArrayList<>();    //存放多张图片的文件集合
    File imageFileDir;  //存放多张图片的本地临时文件夹，在最后删除掉，不占用用户内存空间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_taoquan_dynamic);
        ButterKnife.inject(this);

        init();
        initEvent();

    }

    private void init() {
        MyApplication myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();
        Intent intent = getIntent();
        if (intent != null) {
            circleId = intent.getIntExtra("circleId", 0);
        }

    }

    private void initEvent(){
        shapeLoadingDialog = new ShapeLoadingDialog(this);//shapeLoadingDialog对象
        //设置长按删除选择的图片
        longClickToRemovePhoto(ivPhoto1,0);
        longClickToRemovePhoto(ivPhoto2,1);
        longClickToRemovePhoto(ivPhoto3,2);
        longClickToRemovePhoto(ivPhoto4,3);
        longClickToRemovePhoto(ivPhoto5,4);
    }

    //设置长按删除选择的图片
    private void longClickToRemovePhoto(View view, final int position){
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String[] items = {"删除选中图片"};
                new AlertDialog.Builder(CreateTaoquanDynamicActivity.this).setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mResults.remove(position);
                        files.clear();
                        initPhotoView();
                    }
                }).show();
                return false;
            }
        });
    }

    //初始化图片控件
    public void initPhotoView() {
        if (mResults != null) {
            //预览图片控件初始化
            final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivAddPhoto.getLayoutParams();
            ivPhoto1.setVisibility(View.GONE);
            ivPhoto2.setVisibility(View.GONE);
            ivPhoto3.setVisibility(View.GONE);
            ivPhoto4.setVisibility(View.GONE);
            ivPhoto5.setVisibility(View.GONE);
            //图片位置规则重置
            layoutParams.addRule(RelativeLayout.ALIGN_LEFT, 0);
            layoutParams.addRule(RelativeLayout.BELOW, 0);
            //图片控件的消失与隐藏
            for (int i = 0; i < mResults.size(); i++) {
                //图片缩略显示
                Bitmap bitmap = decodeSampledBitmapFromFd(mResults.get(i),80,100);

                if (i == 0) {
                    ivPhoto1.setVisibility(View.VISIBLE);
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.iv_photo1);
                    ivPhoto1.setImageBitmap(bitmap);
                }
                if (i == 1) {
                    ivPhoto2.setVisibility(View.VISIBLE);
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.iv_photo2);
                    ivPhoto2.setImageBitmap(bitmap);
                }
                if (i == 2) {
                    ivPhoto3.setVisibility(View.VISIBLE);
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.iv_photo3);
                    ivPhoto3.setImageBitmap(bitmap);
                }
                if (i == 3) {
                    ivPhoto4.setVisibility(View.VISIBLE);
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.iv_photo1);
                    layoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.iv_photo1);
                    ivPhoto4.setImageBitmap(bitmap);
                }
                if (i == 4) {
                    ivPhoto5.setVisibility(View.VISIBLE);
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.iv_photo5);
                    layoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.iv_photo2);
                    ivPhoto5.setImageBitmap(bitmap);
                }
            }
        }
    }

    //下面三个方法都是图片缩略处理
    // 从sd卡上加载图片
    public static Bitmap decodeSampledBitmapFromFd(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight);
    }
    //计算图片大小
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth,
                                            int dstHeight) {
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    @OnClick({R.id.finish, R.id.tv_publish_dynamic,R.id.iv_add_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.finish:
                finish();
                break;
            case R.id.iv_add_photo:  //点击弹出图库，选择图片
                if (mResults.size() < 5) {
                    // start multiple photos selector
                    Intent intent = new Intent(CreateTaoquanDynamicActivity.this, ImagesSelectorActivity.class);
                    // max number of images to be selected
                    intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 5);
                    // min size of image which will be shown; to filter tiny images (mainly icons)
                    intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
                    // show camera or not
                    intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
                    // pass current selected images as the initial value
                    intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mResults);
                    // start the selector
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
            case R.id.tv_publish_dynamic:
                String title = etCircleDynamicTitle.getText().toString();
                String content = etCircleDynamicContent.getText().toString();
                if (title.trim().length()==0) {
                    Toast.makeText(CreateTaoquanDynamicActivity.this, "请输入标题哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (content.trim().length()==0) {
                    Toast.makeText(CreateTaoquanDynamicActivity.this, "请输入内容哦", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestParams requestParams = new RequestParams(NetUtil.url + "InsertCircleDynamicServlet");
                requestParams.setMultipart(true);
                //获取imageList,注：这里只有circleDynamicImageUrl属性，其他为null，因为服务端只需要这个属性
                List<AmoyCircleDynamicImage> imageList = new ArrayList<>();
                if(files!=null){
                    if(files.size()>0){
                        for(int i = 0;i<files.size();i++){
                            imageList.add(new AmoyCircleDynamicImage(0,0,files.get(i).getName()));
                            requestParams.addBodyParameter("file"+i,files.get(i));
                        }
                    }
                }
                AmoyCircleDynamic amoyCircleDynamic = new AmoyCircleDynamic(user, circleId, title, content, 0, new Timestamp(System.currentTimeMillis()), imageList);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();//设置日期格式（24小时）
                String amoyCircleDynamicJson = gson.toJson(amoyCircleDynamic);
                requestParams.addBodyParameter("amoyCircleDynamicJson", amoyCircleDynamicJson);
                pd = new ProgressDialog(CreateTaoquanDynamicActivity.this);
                pd.setMessage("发布中..");
                pd.show();
                x.http().post(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        setResult(ResultCode); //设置结果码，在activity里面判断，更新页面数据
                        finish();
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }
                    @Override
                    public void onFinished() {
                        //pd.dismiss();   //两种方式
                        pd.cancel();
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==RESULT_OK){
            //files集合清空
            files.clear();
            mResults.clear();
            mResults.addAll(data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS));
            //初始化选择图片后的图片控件
            initPhotoView();
            //选择图片的保存
            shapeLoadingDialog.setLoadingText("正在进行图片处理，请稍等...");
            shapeLoadingDialog.show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //压缩保存
                    initSaveImage();
                    shapeLoadingDialog.dismiss();
                }
            });
            thread.start();
        }
    }

    //保存图片压缩值
    public void initSaveImage(){
        for (int i = 0; i < mResults.size(); i++) {
            Bitmap bm = null;
            File file = new File(mResults.get(i));
            Uri imageUri = Uri.fromFile(file);
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(imageUri);
                bm = BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            files.add(saveImage(bm));
        }
    }

    //获取相片名字：20161027_103823.png
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        return sdf.format(date) + ".png";
    }

    //压缩图片，转化成输出流
    private ByteArrayOutputStream compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        int i = 1;
        Log.i("LAG", "压缩前的长度: " + baos.toByteArray().length);
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 45;//每次都减少10
            if(options == 10){
                break;
            }
            Log.i("LAG", "看看质量减少没，并且执行: " + (i++) + "次---->" + options);
            baos.reset();//重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        Log.i("LAG", "压缩后的长度: " + baos.toByteArray().length);
        return baos;
    }

    //将压缩好的bitmap保存在文件中
    public File saveImage(Bitmap bitmap) {
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" +
//                getPhotoFileName());

        //1、获取sd卡目录,Environment.getExternalStorageDirectory()
        //2、获取想要存储的文件夹的路径
        imageFileDir = new File(Environment.getExternalStorageDirectory() + "/xiaoyuanershou/tempImage");
        if (!imageFileDir.exists()) {//如果文件夹不存在，则创建该目录
            imageFileDir.mkdirs();
        }
        //3、获取文件完整目录
        File file = new File(imageFileDir,"/"+getPhotoFileName());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = compressImage(bitmap);

        try {
            baos.writeTo(fos);
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除文件夹里所有内容，直接对文件夹调用delete，若文件夹里内容为空，则可以成功删除，否则要将文件夹里所有文件全部删除才可以删除该文件夹
        if(imageFileDir!=null){ //要先判断文件夹不为null，否则若用户没选择图片会出现空指针异常
            if(imageFileDir.exists()){
                //这里保留了文件夹,由于该文件夹里只存放了图片文件并没有子文件夹,所以可以直接删除掉文件
                //若有文件夹则需要写一个递归方法去删除文件夹下所有内容{判断是否是文件夹：file.isDirectory()}
                Log.i("CreateTaoquanDynamic", "onDestroy: 111");
                File[] tempImageFiles = imageFileDir.listFiles();
                for(int i = 0;i<tempImageFiles.length;i++){
                    if(tempImageFiles[i]!=null){
                        if(tempImageFiles[i].isFile()&&tempImageFiles[i].exists()){
                            tempImageFiles[i].delete();
                        }
                    }
                }
            }
        }

    }



}
