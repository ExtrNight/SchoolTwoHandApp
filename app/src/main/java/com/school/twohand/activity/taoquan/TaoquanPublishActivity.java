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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.king.photo_library.ImagesSelectorActivity;
import com.king.photo_library.SelectorSettings;
import com.school.twohand.activity.GoodsClassActivity;
import com.school.twohand.customview.loadingview.ShapeLoadingDialog;
import com.school.twohand.entity.AmoyCircle;
import com.school.twohand.entity.ClassTbl;
import com.school.twohand.entity.Goods;
import com.school.twohand.entity.GoodsImage;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 从淘圈发布商品
 * Created by yang on 2016/10/29 0029.
 */
public class TaoquanPublishActivity extends AppCompatActivity {

    @InjectView(R.id.public_title)
    EditText publicTitle;
    @InjectView(R.id.public_content)
    EditText publicContent;
    @InjectView(R.id.public_photo)
    ImageView publicPhoto;
    @InjectView(R.id.public_photo1)
    ImageView publicPhoto1;
    @InjectView(R.id.public_photo2)
    ImageView publicPhoto2;
    @InjectView(R.id.public_photo3)
    ImageView publicPhoto3;
    @InjectView(R.id.public_photo4)
    ImageView publicPhoto4;
    @InjectView(R.id.public_photo5)
    ImageView publicPhoto5;
    @InjectView(R.id.publicAuction1)
    RadioButton publicAuction1;
    @InjectView(R.id.publicAuction2)
    RadioButton publicAuction2;
    @InjectView(R.id.publicSure)
    Button publicSure;
    @InjectView(R.id.publicPrice)
    EditText publicPrice;
    @InjectView(R.id.publicClass)
    TextView publicClass;
    @InjectView(R.id.publicCircle)
    Spinner publicCircle;
    @InjectView(R.id.tv_publish_taoquan_name)
    TextView tvPublishTaoquanName;
    @InjectView(R.id.publish_finish)
    ImageView publishFinish;

    public static final int ResultCode = 19;
    private ArrayList<String> mResults = new ArrayList<>();
    List<File> files = new ArrayList<>();
    private static final int REQUEST_CODE = 732;
    private static final int REQUEST_CODE_CLASS = 1;
    Integer classid = 0;
    Integer amoyId = 0;//选中淘圈的id
    File imageFileDir;  //存放多张图片的本地临时文件夹，在最后删除掉，不占用用户内存空间

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.inject(this);
        publicCircle.setVisibility(View.GONE);
        tvPublishTaoquanName.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if (intent != null) {
            amoyId = intent.getIntExtra("circleId", 0);
            String circleName = intent.getStringExtra("circleName");
            if (circleName != null) {
                tvPublishTaoquanName.setText(circleName);
            }
        }

        initEvent();

    }

    private void initEvent(){
        //设置长按删除选择的图片
        longClickToRemovePhoto(publicPhoto1,0);
        longClickToRemovePhoto(publicPhoto2,1);
        longClickToRemovePhoto(publicPhoto3,2);
        longClickToRemovePhoto(publicPhoto4,3);
        longClickToRemovePhoto(publicPhoto5,4);
    }

    //设置长按删除选择的图片
    private void longClickToRemovePhoto(View view, final int position){
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String[] items = {"删除选中图片"};
                new AlertDialog.Builder(TaoquanPublishActivity.this).setItems(items, new DialogInterface.OnClickListener() {
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

    //回调请求码是REQUEST_CODE就请求图库，请求码是REQUEST_CODE_CLASS就请求分类界面返回分类结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //返回选择图片的地址集合
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //files集合清空
                files.clear();
                mResults.clear();
                mResults.addAll(data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS));
                //初始化选择图片后的图片控件
                initPhotoView();
                //选择图片的保存
                final ShapeLoadingDialog shapeLoadingDialog = new ShapeLoadingDialog(this);//shapeLoadingDialog对象
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
        //请求分类界面
        if (requestCode == REQUEST_CODE_CLASS) {
            switch (data.getStringExtra("result")) {
                case "1":
                    publicClass.setText("校园代步");
                    classid = 1;
                    break;
                case "2":
                    publicClass.setText("手机");
                    classid = 2;
                    break;
                case "3":
                    publicClass.setText("电脑");
                    classid = 3;
                    break;
                case "4":
                    publicClass.setText("数码配件");
                    classid = 4;
                    break;
                case "5":
                    publicClass.setText("数码");
                    classid = 5;
                    break;
                case "6":
                    publicClass.setText("电器");
                    classid = 6;
                    break;
                case "7":
                    publicClass.setText("运动健身");
                    classid = 7;
                    break;
                case "8":
                    publicClass.setText("衣物伞帽");
                    classid = 8;
                    break;
                case "9":
                    publicClass.setText("图书教材");
                    classid = 9;
                    break;
                case "10":
                    publicClass.setText("租赁");
                    classid = 10;
                    break;
                case "11":
                    publicClass.setText("生活娱乐");
                    classid = 11;
                    break;
                case "12":
                    publicClass.setText("其他");
                    classid = 12;
                    break;

            }
        }
    }

    //初始化图片控件
    public void initPhotoView() {
        if (mResults != null) {
            //预览图片控件初始化
            final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) publicPhoto.getLayoutParams();
            publicPhoto1.setVisibility(View.GONE);
            publicPhoto2.setVisibility(View.GONE);
            publicPhoto3.setVisibility(View.GONE);
            publicPhoto4.setVisibility(View.GONE);
            publicPhoto5.setVisibility(View.GONE);
            //图片位置规则重置
            layoutParams.addRule(RelativeLayout.ALIGN_LEFT, 0);
            layoutParams.addRule(RelativeLayout.BELOW, 0);
            //图片控件的消失与隐藏
            for (int i = 0; i < mResults.size(); i++) {
                //图片缩略显示
                Bitmap bitmap = decodeSampledBitmapFromFd(mResults.get(i), 80, 100);

                if (i == 0) {
                    publicPhoto1.setVisibility(View.VISIBLE);
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.public_photo1);
                    publicPhoto1.setImageBitmap(bitmap);
                }
                if (i == 1) {
                    publicPhoto2.setVisibility(View.VISIBLE);
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.public_photo2);
                    publicPhoto2.setImageBitmap(bitmap);
                }
                if (i == 2) {
                    publicPhoto3.setVisibility(View.VISIBLE);
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.public_photo3);
                    publicPhoto3.setImageBitmap(bitmap);
                }
                if (i == 3) {
                    publicPhoto4.setVisibility(View.VISIBLE);
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.public_photo1);
                    layoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.public_photo1);
                    publicPhoto4.setImageBitmap(bitmap);
                }
                if (i == 4) {
                    publicPhoto5.setVisibility(View.VISIBLE);
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.public_photo5);
                    layoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.public_photo2);
                    publicPhoto5.setImageBitmap(bitmap);
                }
            }
        }
    }

    //下面三个方法都是图片缩略处理
    // 从sd卡上加载图片
    public static Bitmap decodeSampledBitmapFromFd(String pathName,
                                                   int reqWidth, int reqHeight) {
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

    //保存图片压缩值
    public void initSaveImage() {
        for (int i = 0; i < mResults.size(); i++) {
            Bitmap bm = null;
            File file = new File(mResults.get(i));
            Uri imageUri = Uri.fromFile(file);
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(imageUri);
                bm = BitmapFactory.decodeStream(is);
                if (is != null) {
                    is.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            files.add(saveImage(bm));
        }
    }


    //获取相片名字
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
            options -= 10;//每次都减少10
            if (options == 10) {
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
        File file = new File(imageFileDir, "/" + getPhotoFileName());

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

    //图片点击事件以及发布按钮点击事件
    @OnClick({R.id.public_photo, R.id.publicSure, R.id.publicClass,R.id.publish_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.publish_finish:
                finish();
                break;
            case R.id.public_photo:
                if (mResults.size() < 5) {
                    // start multiple photos selector
                    Intent intent = new Intent(TaoquanPublishActivity.this, ImagesSelectorActivity.class);
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
            case R.id.publicSure:
                if (publicTitle.getText().toString().trim().length() == 0 || publicContent.getText().toString().trim().length() == 0
                        || files.size() < 1 || publicPrice.getText().toString().trim().length() == 0 || publicClass.getText().toString().trim().length() == 0) {
                    Toast.makeText(TaoquanPublishActivity.this, "信息不全", Toast.LENGTH_SHORT).show();
                } else {
                    uploadAll();
                }

                break;
            case R.id.publicClass:
                Intent intent = new Intent(this, GoodsClassActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CLASS);
        }
    }

    //上传发布信息
    public void uploadAll() {
        //Goods对象：
        //获取当前用户对象
        ClassTbl classTbl = new ClassTbl(classid, null);
        User user = ((MyApplication) getApplication()).getUser();

        AmoyCircle amoyCircle = new AmoyCircle();
        amoyCircle.setCircleId(amoyId);
        String title = publicTitle.getText().toString();
        String describe = publicContent.getText().toString();
        Float goodsPrice = Float.parseFloat(publicPrice.getText().toString());
        Byte auction = -1;
        if (publicAuction1.isChecked()) {
            auction = 0;//一口价
        } else if (publicAuction2.isChecked()) {
            auction = 1;//拍卖
        }
        List<GoodsImage> goodsImages = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String address = files.get(i).toString().substring(files.get(i).toString().lastIndexOf("/") + 1, files.get(i).toString().length());
            GoodsImage goodsImage = new GoodsImage(null, null, address);
            goodsImages.add(goodsImage);
        }

        Goods goods = new Goods(null, classTbl, user, amoyCircle, title, describe, goodsPrice, null, 1, auction, goodsImages, null, null, 0,user.getUserSchoolName());
        final ProgressDialog dia = new ProgressDialog(this);
        dia.setMessage("加载中....");
        dia.show();

        RequestParams params = new RequestParams(NetUtil.url + "UploadImages");
        params.setMultipart(true);

        for (int i = 0; i < files.size(); i++) {
            params.addBodyParameter("file" + i, files.get(i));
        }
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String goodsString = gson.toJson(goods);
        params.addBodyParameter("goods", goodsString);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //加载成功回调，返回获取到的数据
                Log.i("LAG", "onSuccess: " + result);
                Toast.makeText(TaoquanPublishActivity.this, "发布成功~", Toast.LENGTH_SHORT).show();
                setResult(ResultCode);
                finish();
            }

            @Override
            public void onFinished() {
                dia.dismiss();//加载完成
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除文件夹里所有内容，直接对文件夹调用delete，若文件夹里内容为空，则可以成功删除，否则要将文件夹里所有文件全部删除才可以删除该文件夹
        if (imageFileDir != null) { //要先判断文件夹不为null，否则若用户没选择图片会出现空指针异常
            if (imageFileDir.exists()) {
                //删除保存在本地的图片,不占用用户的内存
                Log.i("CreateTaoquanDynamic", "onDestroy: 1111");
                File[] tempImageFiles = imageFileDir.listFiles();
                for (int i = 0; i < tempImageFiles.length; i++) {
                    if (tempImageFiles[i] != null) {
                        if (tempImageFiles[i].isFile() && tempImageFiles[i].exists()) {
                            tempImageFiles[i].delete();
                        }
                    }
                }
            }
        }
    }


}
