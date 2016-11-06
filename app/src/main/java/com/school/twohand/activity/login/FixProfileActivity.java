package com.school.twohand.activity.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class FixProfileActivity extends AppCompatActivity {

    private Button mFinishBtn;//完成按钮
    private EditText mNickNameEt;//昵称
    private ImageView mAvatarIv;//头像控件
    Uri imageUri = null;
    //请求码，打开相机，打开图库，裁剪
    private static final int CAMERA_CODE = 1;
    private static final int GALLERY_CODE = 2;
    private static final int CROP_CODE = 3;

    File img;

    //android项目中的拍照和本地图片截图
    //对话框显示条目
    String items[]={"相册选择","拍照"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_profile);
        //找控件
        mNickNameEt = (EditText) findViewById(R.id.nick_name_et);//昵称
        mAvatarIv = (CircleImageView) findViewById(R.id.jmui_avatar_iv);//头像
        mFinishBtn = (Button) findViewById(R.id.finish_btn);//完成按钮

        //如果有昵称则赋值给控件
        if (JMessageClient.getMyInfo().getNickname().trim().length()!=0){
            mNickNameEt.setText(JMessageClient.getMyInfo().getNickname());
        }

        //头像点击选择图库or拍照
        mAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FixProfileActivity.this).setTitle("选择").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            //从图库获取相片
                            case 0:
                                chooseFromGallery();
                                break;
                            //从相机获取相片
                            case 1:
                                chooseFromCamera();
                                break;
                        }
                    }
                }).show();
            }
        });
        if (JMessageClient.getMyInfo().getAvatar()!=null) {
            JMessageClient.getMyInfo().getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int i, String s, Bitmap bitmap) {
                    if (i == 0) {//获取头像
                        //有头像
                        mAvatarIv.setImageBitmap(bitmap);
                    }
                }
            });
        }
        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pdilog = new ProgressDialog(FixProfileActivity.this);
                pdilog.setMessage("上传个人资料中。。。");
                pdilog.show();
                if (img!=null&&mNickNameEt.getText().toString()!=null){
                    JMessageClient.updateUserAvatar(img, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0){
                                Toast.makeText(FixProfileActivity.this, "头像传送成功", Toast.LENGTH_SHORT).show();
                                JMessageClient.getMyInfo().setNickname(mNickNameEt.getText().toString());
                                JMessageClient.updateMyInfo(UserInfo.Field.nickname, JMessageClient.getMyInfo(), new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        pdilog.cancel();
                                        if (i == 0){
                                            Toast.makeText(FixProfileActivity.this, "用户名更新", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else{
                                            Toast.makeText(FixProfileActivity.this, "用户名更新失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                pdilog.cancel();
                                Toast.makeText(FixProfileActivity.this, "头像传送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else if(mNickNameEt.getText().toString()!=null&& JMessageClient.getMyInfo().getAvatar()!=null){
                    JMessageClient.getMyInfo().setNickname(mNickNameEt.getText().toString());
                    JMessageClient.updateMyInfo(UserInfo.Field.nickname, JMessageClient.getMyInfo(), new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            pdilog.cancel();
                            if (i == 0){
                                Toast.makeText(FixProfileActivity.this, "用户名更新", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(FixProfileActivity.this, "用户名更新失败", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });
    }

    /**
     * 拍照选择图片
     */
    private void chooseFromCamera() {
        //构建隐式Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //调用系统相机
        startActivityForResult(intent, CAMERA_CODE);
    }
    /**
     * 从相册选择图片
     */
    private void chooseFromGallery() {
        //构建一个内容选择的Intent
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //设置选择类型为图片类型
        intent.setType("image/*");
        //打开图片选择
        startActivityForResult(intent, GALLERY_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CAMERA_CODE:
                //用户点击了取消
                if(data == null){
                    return;
                }else{
                    Bundle extras = data.getExtras();
                    if (extras != null){
                        //获得拍的照片
                        Bitmap bm = extras.getParcelable("data");
                        //将Bitmap转化为uri
                        Uri uri = saveBitmap(bm, "temp");
                        //启动图像裁剪
                        startImageZoom(uri);
                    }
                }

                break;
            case GALLERY_CODE:
                if (data == null){
                    return;
                }else{
                    //用户从图库选择图片后会返回所选图片的Uri
                    Uri uri;
                    //获取到用户所选图片的Uri
                    uri = data.getData();
                    //返回的Uri为content类型的Uri,不能进行复制等操作,需要转换为文件Uri
                    uri = convertUri(uri);
                    startImageZoom(uri);
                }

                break;
            case CROP_CODE:
                if (data == null){
                    return;
                }else{
                    Bundle extras = data.getExtras();
                    if (extras != null){
                        //获取到裁剪后的图像
                        Bitmap bm = extras.getParcelable("data");
                        mAvatarIv.setImageBitmap(bm);
                        Uri uri = saveBitmap(bm,"tp");
                    }
                }
                break;

            default:
                break;
        }
    }


    /**
     * 将content类型的Uri转化为文件类型的Uri
     * @param uri
     * @return
     */
    private Uri convertUri(Uri uri) {
        InputStream is;
        try {
            //Uri ----> InputStream
            is = getContentResolver().openInputStream(uri);
            //InputStream ----> Bitmap
            Bitmap bm = BitmapFactory.decodeStream(is);
            //关闭流
            is.close();
            return saveBitmap(bm, "temp");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 将Bitmap写入SD卡中的一个文件中,并返回写入文件的Uri
     * @param bm
     * @param dirPath
     * @return
     */
    private Uri saveBitmap(Bitmap bm, String dirPath) {
        //新建文件夹用于存放裁剪后的图片
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/" + dirPath);
        if (!tmpDir.exists()){
            tmpDir.mkdir();
        }

        //新建文件存储裁剪后的图片
         img = new File(tmpDir.getAbsolutePath() + "/avator.png");
        try {
            //打开文件输出流
            FileOutputStream fos = new FileOutputStream(img);
            //将bitmap压缩后写入输出流(参数依次为图片格式、图片质量和输出流)
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            //刷新输出流
            fos.flush();
            //关闭输出流
            fos.close();
            //返回File类型的Uri
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }




    /**
     * 通过Uri传递图像信息以供裁剪
     * @param uri
     */
    private void startImageZoom(Uri uri){
        //构建隐式Intent来启动裁剪程序
        Intent intent = new Intent("com.android.camera.action.CROP");
        //设置数据uri和类型为图片类型
        intent.setDataAndType(uri, "image/*");
        //显示View为可裁剪的
        intent.putExtra("crop", true);
        //裁剪的宽高的比例为1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //输出图片的宽高均为150
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        //裁剪之后的数据是通过Intent返回
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_CODE);
    }
}


