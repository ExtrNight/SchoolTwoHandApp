package com.school.twohand.activity.taoquan;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ModifyTaoquanInfoActivity extends AppCompatActivity {

    @InjectView(R.id.tv_taoquan_name)
    TextView tvTaoquanName;
    @InjectView(R.id.iv_modify_circle_image)
    ImageView ivModifyCircleImage;
    @InjectView(R.id.iv_modify_circle_bg)
    ImageView ivModifyCircleBg;

    static final int ResultCode = 2;
    private int modifyFlag;      //修改的标记,1表示修改淘圈头像，2表示修改淘圈背景
    private int selectBgFlag;   //选择淘圈背景的标记，1表示系统默认图片(s1~s15)，2表示相机或相册选择的图片
    int defaultBgId = 0;        //系统默认图片的Id，1~15

    private User user;
    private int circleId;
    private String circleName;
    private String circleBackgroundUrl; //淘圈背景图片的地址，首字母为s表示为系统的皮肤
    private String circleImageUrl;  //形如：(/2/1475660662247circle.png)，在作为本地照片名的时候要把“/2/”去掉

    private File imageFile;       //临时存放相机拍摄照片或相册照片的文件,淘圈头像
    private File backgroundFile;    //淘圈背景
    Uri uri;
    Bitmap imageBitmap;                 //存放从相册或相机选取的bitmap对象,淘圈头像
    Bitmap backgroundBitmap;            //淘圈背景

    private static final int SELECT_FROM_ALBUM = 1;     //从相册选取图片
    private static final int SELECT_FROM_CAMERA = 2;    //从相机选取图片
    private static final int CROP = 3;                   //裁剪
    private static final int SelectTaoquanBg = 4;       //选择淘圈背景

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_taoquan_info);
        ButterKnife.inject(this);

        init();
        initData();
    }

    private void init() {
        user = ((MyApplication)getApplication()).getUser();
        Intent intent = getIntent();
        if (intent != null) {
            circleId = intent.getIntExtra("circleId", 0);
            circleName = intent.getStringExtra("circleName");
            circleBackgroundUrl = intent.getStringExtra("circleBackgroundUrl");
            circleImageUrl = intent.getStringExtra("circleImageUrl");
        }

    }

    private void initData() {
        if(circleName!=null){
            tvTaoquanName.setText(circleName);
        }
        if (circleBackgroundUrl != null) {
            if (circleBackgroundUrl.substring(0, 1).equals("s")) {
                int systemCircleBackgroundId = Integer.parseInt(circleBackgroundUrl.substring(1));
                setHeadBackground(systemCircleBackgroundId, ivModifyCircleBg);
            }else{
                String imageUrl = NetUtil.imageUrl + circleBackgroundUrl;
                ImageOptions imageOptions = new ImageOptions.Builder().build();
                x.image().bind(ivModifyCircleBg, imageUrl, imageOptions);
            }
        }
        if (circleImageUrl != null) {
            String imageUrl = NetUtil.imageUrl + circleImageUrl;
            ImageOptions imageOptions = new ImageOptions.Builder().setCrop(true).build();
            x.image().bind(ivModifyCircleImage, imageUrl, imageOptions);
        }

        //判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //“/2/1475660662247circle.png”-->1475660662247-->替换为当前时间
            String circleImageUrlTimeStr = circleImageUrl.substring(circleImageUrl.lastIndexOf("/") + 1, circleImageUrl.lastIndexOf("circle"));
            //修改图片地址（将里面的时间毫秒数改为当前时间毫秒数）
            circleImageUrl = circleImageUrl.replaceFirst(circleImageUrlTimeStr, System.currentTimeMillis() + "");

            String imageFileName = circleImageUrl.substring(circleImageUrl.lastIndexOf("/") + 1);
            File fileDir = new File(Environment.getExternalStorageDirectory() + "/xiaoyuanershou/image");
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            imageFile = new File(fileDir, imageFileName);  //淘圈头像文件的全路径
//            imageUri = Uri.fromFile(imageFile);

            String backgroundFileUrl = System.currentTimeMillis()+"bg.png"; //文件名：3234324bg.png
            circleBackgroundUrl = user.getUserId()+"/"+backgroundFileUrl; //背景图片：1/2133432bg.png
            backgroundFile = new File(fileDir,backgroundFileUrl); //淘圈背景图片的文件
//            backgroundImageUri = Uri.fromFile(backgroundFile);
        }

    }

//    //获取文件路径
//    private String getPhotoFileName() {        //文件名：当前时间.png
////        Date date = new Date(System.currentTimeMillis());
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
////        return sdf.format(date) + ".png";
//        return System.currentTimeMillis()+".png";
//    }

    @OnClick({R.id.iv_return, R.id.tv_modify_circle_image, R.id.btn_confirm_modify, R.id.tv_modify_circle_bg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                if(backgroundFile.exists()){
                    backgroundFile.delete();
                }
                finish();
                break;
            case R.id.tv_modify_circle_image:
                modifyFlag = 1;  //1表示修改淘圈头像
                String[] items = {"    从相册选取", "    相机"};
                //点击出现弹框
                new AlertDialog.Builder(this).setTitle("修改淘圈头像").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //从相册选取
                                Intent intent = new Intent(Intent.ACTION_PICK, null);
                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intent, SELECT_FROM_ALBUM);
                                break;
                            case 1: //从相机选取
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                //putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))  什么意思???????,
                                //(个人理解)把相机获取的照片uri指向file，在回调里data.getData()获得的是null，因为uri已指向文件，而不在data里面可根据Uri.fromFile()获取uri
                                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                                startActivityForResult(intent2, SELECT_FROM_CAMERA);
                                break;
                        }
                    }
                }).show();
                break;
            case R.id.tv_modify_circle_bg:
                Intent intent = new Intent(this,SelectTaoquanBgActivity.class);
                startActivityForResult(intent,SelectTaoquanBg);
                break;
            case R.id.btn_confirm_modify: //确认修改
                String modifyCircleImageUrl = null;   //修改后的淘圈头像地址
                String modifyCircleBackgroundUrl = null;    //修改后的淘圈背景图片地址
                Intent intent1 = new Intent();
                if(imageFile.exists()){
                    uploadImage(imageFile,circleImageUrl);
                    modifyCircleImageUrl = circleImageUrl;
                    intent1.putExtra("modifyCircleImageUrl",modifyCircleImageUrl);
                }
                if(backgroundFile.exists()&&selectBgFlag == 2){//选择的是相机或相册图片
                    uploadImage(backgroundFile,circleBackgroundUrl);
                    modifyCircleBackgroundUrl = circleBackgroundUrl;
                    intent1.putExtra("modifyCircleBackgroundUrl",modifyCircleBackgroundUrl);
                }else if(selectBgFlag == 1){ //选择的是系统图片
                    modifyCircleBackgroundUrl = "s"+defaultBgId;
                    intent1.putExtra("modifyCircleBackgroundUrl",modifyCircleBackgroundUrl);
                }
                if(modifyCircleImageUrl!=null||modifyCircleBackgroundUrl!=null){
                    updateCircleImageUrl(modifyCircleImageUrl,modifyCircleBackgroundUrl); //修改数据库字段
//                    intent1.putExtra()
                    setResult(ResultCode,intent1);  //设置结果码，返回的activity可以通过这个结果码选择执行操作
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1000);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_FROM_ALBUM: //从相册选取
//                if (data != null) {
//                    crop(data.getData());
//                }
                //不进行裁剪
                if(data!=null){
                    ContentResolver cr = this.getContentResolver();
                    try {
                        if(modifyFlag==1){  //淘圈头像
                            if(imageBitmap!=null){
                                imageBitmap.recycle();//如果不释放的话，不断取图片，将会内存不够
                            }
                            uri = data.getData();
                            imageBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            saveImage(imageBitmap);
                            ivModifyCircleImage.setImageBitmap(imageBitmap);
                        }else if(modifyFlag==2){  //淘圈背景图片
                            if(backgroundBitmap!=null){
                                backgroundBitmap.recycle();//如果不释放的话，不断取图片，将会内存不够
                            }
                            uri = data.getData();
                            backgroundBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            saveImage(backgroundBitmap);
                            ivModifyCircleBg.setImageBitmap(backgroundBitmap);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_FROM_CAMERA: //相机拍照选取
//                if(modifyFlag==1){          //修改淘圈头像
//                    crop(Uri.fromFile(imageFile));
//                }else if(modifyFlag==2){    //修改淘圈背景图片
//                    crop(Uri.fromFile(backgroundFile));
//                }
                //不进行裁剪
                try {
//                    if(bitmap!=null){
//                        ivModifyCircleBg.setImageBitmap(null);
//                        Log.i("ModifyTaoquanInfo", "onActivityResult: 11111");
//                        bitmap.recycle();//如果不释放的话，不断取图片，将会内存不够
//                    }
                    ContentResolver cr = this.getContentResolver();
                    if(modifyFlag==1){      //淘圈头像
                        if(imageBitmap!=null){
                            imageBitmap.recycle();
                        }
                        uri = Uri.fromFile(imageFile);
                        //bitmap = data.getParcelableExtra("data");获取不到，因为Uri已指向文件里面,用下面的方式
                        imageBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        saveImage(imageBitmap);
                        ivModifyCircleImage.setImageBitmap(imageBitmap);
                    }else if(modifyFlag==2){   //淘圈背景图片
                        if(backgroundBitmap!=null){
                            backgroundBitmap.recycle();
                        }
                        uri = Uri.fromFile(backgroundFile);
                        backgroundBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        saveImage(backgroundBitmap);
                        ivModifyCircleBg.setImageBitmap(backgroundBitmap);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
//            case CROP: //裁剪
//                if (data != null) {
//                    Bundle bundle = data.getExtras();
//                    if (bundle != null) {
//                        Bitmap bitmap = bundle.getParcelable("data");
//                        saveImage(bitmap);
//                        if (modifyFlag == 1) {
//                            ivModifyCircleImage.setImageBitmap(bitmap);
//                        }else{
//                            ivModifyCircleBg.setImageBitmap(bitmap);
//                        }
//                    }
//                }
//                break;
            case SelectTaoquanBg: //从选取淘圈背景跳回的
                if(resultCode==SelectTaoquanBgActivity.ResultCode){
                    modifyFlag = 2;  //2表示修改淘圈头部背景
                    if(data.getStringExtra("isFromAlbumOrCamera")!=null){
                        selectBgFlag = 2;   //2表示从相册或相机选取图片
                        String[] items2 = {"    从相册选取", "    相机"};
                        new AlertDialog.Builder(this).setTitle("修改淘圈背景").setItems(items2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: //从相册选取
                                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                        startActivityForResult(intent, SELECT_FROM_ALBUM);
                                        break;
                                    case 1: //从相机选取
                                        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(backgroundFile));  //???
                                        startActivityForResult(intent2, SELECT_FROM_CAMERA);
                                        break;
                                }
                            }
                        }).show();
                    }else{
                        selectBgFlag = 1;  //1表示选择的是系统图片
                        defaultBgId = data.getIntExtra("defaultBgId",1);
                        setHeadBackground(defaultBgId,ivModifyCircleBg);
                    }
                }
                break;
        }
    }

    //裁剪图片
//    public void crop(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//
//        intent.putExtra("crop", "true");
//        //宽高比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        //定义宽和高
//        intent.putExtra("outputX", 200);
//        intent.putExtra("outputY", 200);
//        //是否要返回值
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, CROP);
//    }

    //将bitmap保存在文件中，开子线程
    public void saveImage(final Bitmap bitmap) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                FileOutputStream fos = null;
                try {
                    if(modifyFlag==1){
                        fos = new FileOutputStream(imageFile);
                    }else if(modifyFlag==2){
                        fos = new FileOutputStream(backgroundFile);
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); //将bitmap写入输出流
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try { //刷新关闭流
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    //将图片上传到服务器
    private void uploadImage(final File file, final String imageUrl) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                RequestParams requestParams = new RequestParams(NetUtil.url + "CircleImageServlet");
                requestParams.setMultipart(true);  //指定上传文件格式
                requestParams.addBodyParameter("circleImage", file);
                requestParams.addBodyParameter("circleImageUrl", imageUrl);
                x.http().post(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        //上传成功后，删除文件
                        if (file.exists()) {
                            file.delete();
                        }
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
        }.start();
    }

    //修改数据库里淘圈头像地址
    private void updateCircleImageUrl(String modifyCircleImageUrl,String modifyCircleBackgroundUrl) {
        RequestParams requestParams = new RequestParams(NetUtil.url + "UpdateCircleInfoServlet");
        requestParams.addQueryStringParameter("circleId", circleId + "");
        requestParams.addQueryStringParameter("circleImageUrl", modifyCircleImageUrl);
        requestParams.addQueryStringParameter("circleBackgroundUrl",modifyCircleBackgroundUrl);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
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

    //设置头布局背景图片
    private void setHeadBackground(int systemCircleBackgroundId, ImageView imageView) {
        switch (systemCircleBackgroundId) {
            case 1:
                imageView.setImageResource(R.drawable.taoquan_bg_1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.taoquan_bg_2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.taoquan_bg_3);
                break;
            case 4:
                imageView.setImageResource(R.drawable.taoquan_bg_4);
                break;
            case 5:
                imageView.setImageResource(R.drawable.taoquan_bg_5);
                break;
            case 6:
                imageView.setImageResource(R.drawable.taoquan_bg_6);
                break;
            case 7:
                imageView.setImageResource(R.drawable.taoquan_bg_7);
                break;
            case 8:
                imageView.setImageResource(R.drawable.taoquan_bg_8);
                break;
            case 9:
                imageView.setImageResource(R.drawable.taoquan_bg_9);
                break;
            case 10:
                imageView.setImageResource(R.drawable.taoquan_bg_10);
                break;
            case 11:
                imageView.setImageResource(R.drawable.taoquan_bg_11);
                break;
            case 12:
                imageView.setImageResource(R.drawable.taoquan_bg_12);
                break;
            case 13:
                imageView.setImageResource(R.drawable.taoquan_bg_13);
                break;
            case 14:
                imageView.setImageResource(R.drawable.taoquan_bg_14);
                break;
            case 15:
                imageView.setImageResource(R.drawable.taoquan_bg_15);
                break;
        }
    }


}
