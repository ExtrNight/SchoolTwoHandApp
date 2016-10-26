package com.school.twohand.activity.taoquan;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ModifyTaoquanInfoActivity extends AppCompatActivity {

    @InjectView(R.id.iv_return)
    ImageView ivReturn;
    @InjectView(R.id.iv_modify_circle_image)
    ImageView ivModifyCircleImage;
    @InjectView(R.id.btn_modity_circle_image)
    Button btnModityCircleImage;
    @InjectView(R.id.tv_confirm)
    TextView tvConfirm;

    static final int ResultCode = 2;

    int circleId;
    String circleImageUrl;  //形如：(/2/1475660662247circle.png)，在作为本地照片名的时候要把“/2/”去掉
    private File file;       //临时存放相机拍摄照片或相册照片的文件
    private Uri imageUri;   //照片的uri

    private static final int SELECT_FROM_ALBUM = 1;
    private static final int SELECT_FROM_CAMERA = 2;
    private static final int CROP = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_taoquan_info);
        ButterKnife.inject(this);

        init();
        initData();
    }

    private void init() {
        Intent intent = getIntent();
        if (intent != null) {
            circleId = intent.getIntExtra("circleId",0);
            circleImageUrl = intent.getStringExtra("circleImageUrl");

        }

    }

    private void initData() {
        if (circleImageUrl != null) {
            String imageUrl = NetUtil.imageUrl + circleImageUrl;
            ImageOptions imageOptions = new ImageOptions.Builder().setCrop(true).build();
            x.image().bind(ivModifyCircleImage, imageUrl, imageOptions);
        }

        //判断sd卡是否存在
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //“/2/1475660662247circle.png”-->1475660662247-->替换为当前时间
            String circleImageUrlTimeStr = circleImageUrl.substring(circleImageUrl.lastIndexOf("/")+1,circleImageUrl.lastIndexOf("circle"));
            //修改图片地址（将里面的时间毫秒数改为当前时间毫秒数）
            circleImageUrl = circleImageUrl.replaceFirst(circleImageUrlTimeStr,System.currentTimeMillis()+"");

            String fileName = circleImageUrl.substring(circleImageUrl.lastIndexOf("/")+1);
            File fileDir = new File(Environment.getExternalStorageDirectory() + "/xiaoyuanershou/image");
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }
            file = new File(fileDir,fileName);  //文件的全路径
            imageUri = Uri.fromFile(file);
        }

    }

    @OnClick({R.id.iv_return, R.id.btn_modity_circle_image,R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                if(file.exists()){
                    file.delete();
                }
                finish();
                break;
            case R.id.btn_modity_circle_image:
                String[] items = {"从相册选取", "相机"};
                //点击出现弹框
                new AlertDialog.Builder(this).setTitle("选择图片来源").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0: //从相册选取
                                Intent intent = new Intent(Intent.ACTION_PICK, null);
                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intent,SELECT_FROM_ALBUM);
                                break;
                            case 1: //从相机选取
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));  //???
                                startActivityForResult(intent2, SELECT_FROM_CAMERA);
                                break;
                        }
                    }
                }).show();
                break;
            case R.id.tv_confirm: //点击确认
                uploadImage();
                setResult(ResultCode);  //设置结果码，返回的activity可以通过这个结果码选择执行操作
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        switch (requestCode) {
            case SELECT_FROM_ALBUM: //从相册选取
                if (data != null) {
                    crop(data.getData());
                }
                break;
            case SELECT_FROM_CAMERA: //相机拍照选取
                crop(Uri.fromFile(file));
                break;
            case CROP: //裁剪
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        Bitmap bitmap = bundle.getParcelable("data");
                        saveImage(bitmap);
                        ivModifyCircleImage.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }

    //裁剪图片
    public void crop(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
        //宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //定义宽和高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        //是否要返回值
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP);
    }

    //将bitmap保存在文件中，开子线程
    public void saveImage(final Bitmap bitmap){
        new Thread(){
            @Override
            public void run() {
                super.run();
                FileOutputStream fos=null;
                try {
                    fos=new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos); //将bitmap写入输出流
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if(fos!=null){
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
    private void uploadImage(){
        RequestParams requestParams = new RequestParams(NetUtil.url + "/CircleImageServlet");
        requestParams.setMultipart(true);  //指定上传文件格式
        requestParams.addBodyParameter("circleImage",file);
        Log.i("Modify", "uploadImage: "+file+"--"+circleImageUrl);
        requestParams.addBodyParameter("circleImageUrl",circleImageUrl);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //上传成功后，删除文件
                if(file.exists()){
                    file.delete();
                }
                updateCircleImageUrl();
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

    //修改数据库里淘圈头像地址
    private void updateCircleImageUrl(){
        RequestParams requestParams = new RequestParams(NetUtil.url+"/UpdateCircleInfoServlet");
        requestParams.addQueryStringParameter("circleId",circleId+"");
        requestParams.addQueryStringParameter("circleImageUrl",circleImageUrl);
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


}
