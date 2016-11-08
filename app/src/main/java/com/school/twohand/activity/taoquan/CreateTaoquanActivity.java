package com.school.twohand.activity.taoquan;

import android.content.ContentResolver;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.NetUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;

/**
 * 创建淘圈的页面
 */
public class CreateTaoquanActivity extends AppCompatActivity {

    MyApplication myApplication;
    User user;

    Bitmap bitmap;
    private final int SELECT_FROM_ALBUM = 0;    // 从相册中选
    private final int SELECT_FROM_CAMERA = 1;  //从相机中选
    private final int PHOTO_REQUEST_CROP = 2;   //裁剪图片

    String circleImageUrl = "true";//若用户修改了头像，则上传该信息，服务端会将图片地址写好，这里只需要传个信息，令其不为空即可
    File circleImageFile;  //保存用户从相机或相册选取的文件
    Uri uri; // 图片的Uri地址

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    double myLatitude; //当前位置的纬度
    double myLongitude; //当前位置的经度
    BDLocation myLocation; //当前位置的BDLocation对象
    List<Poi> poiList;// POI数据
    int poiFlag; //需要显示的poi的flag
    boolean isFirstClickLocation = true; //是否是第一次点击定位按钮

    @InjectView(R.id.et_circleName)
    EditText etCircleName;
    @InjectView(R.id.et_circleLabel)
    EditText etCircleLabel;
    @InjectView(R.id.et_circleAddress)
    TextView etCircleAddress;
    @InjectView(R.id.iv_upload_circle_image)
    ImageView ivUploadCircleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_taoquan);
        ButterKnife.inject(this);
        myApplication = (MyApplication) getApplication();
        user = myApplication.getUser();
        poiFlag = 0;

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        //int span=1000;
        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @OnClick({R.id.button, R.id.iv_upload_circle_image, R.id.iv_create_taoquan_return, R.id.iv_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:  //点击提交按钮
                final String circleName = etCircleName.getText().toString();
                final String circleLabel = etCircleLabel.getText().toString();
                final String circleAddress = etCircleAddress.getText().toString();


                if (bitmap == null) {
                    Toast.makeText(CreateTaoquanActivity.this, "你还没有选择头像哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (circleName.equals("")) {
                    Toast.makeText(CreateTaoquanActivity.this, "你还没有输入淘圈名哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (myLatitude == 0 || myLongitude == 0) {
                    Toast.makeText(CreateTaoquanActivity.this, "请定位哦", Toast.LENGTH_SHORT).show();
                    return;
                }

                /**
                 * 创建群的方法
                 * groupName - 群组名称
                 groupDesc - 群组描述
                 callback - 回调接口
                 */
                JMessageClient.createGroup(circleName, circleLabel, new CreateGroupCallback() {
                    @Override
                    public void gotResult(int i, String s, long l) {
                        if (i == 0) {
                            //存入数据库群聊表 存入的字段有：创建者id，和群号
                            Log.i("gotResult", "gotResult: " + l);//群号
                            RequestParams requ = new RequestParams(NetUtil.url + "AddGroupServlet");
                            //将创建者id和群号包装好
                            requ.addQueryStringParameter("groupMainUserId", user.getUserId() + "");
                            requ.addQueryStringParameter("groupNumber", l + "");
                            //访问服务器，添加新建的群
                            x.http().get(requ, new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    String url = NetUtil.url + "InsertCircleServlet";
                                    RequestParams requestParams = new RequestParams(url);
                                    requestParams.addBodyParameter("circleUserId", user.getUserId() + "");
                                    requestParams.addBodyParameter("circleName", circleName);
                                    requestParams.addBodyParameter("circleLabel", circleLabel);
                                    requestParams.addBodyParameter("circleAddress", circleAddress);

                                    if (bitmap != null) {
                                        //如果用户设置了图片，就上传信息给服务器，若没有设置图片，则不发送图片地址的信息，服务器端会进行判断
                                        requestParams.addBodyParameter("circleImageUrl", circleImageUrl);
                                    }
                                    requestParams.addBodyParameter("latitude", myLatitude + "");
                                    requestParams.addBodyParameter("longitude", myLongitude + "");

                                    x.http().post(requestParams, new Callback.CommonCallback<String>() {
                                        @Override
                                        public void onSuccess(final String result) {
                                            if (result.equals("default.png")) {//如果图片地址为默认地址，即用户没有选择淘圈图片，则不上传图片
                                                return;
                                            }
                                            //如果添加到服务器数据库成功，则把头像上传到服务器,服务器返回的result就是图片的服务器url地址
                                            //开子线程进行耗时操作
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    String url = NetUtil.url + "CircleImageServlet";
                                                    RequestParams requestParams = new RequestParams(url);
                                                    //
                                                    // 1、获取sd卡目录
                                                    //  File sdFile = Environment.getExternalStorageDirectory();
//                                                   //2、获取文件完整目录
//                                                  File imageFile = new File(sdFile+"/xiaoyuanershou/image/circleImage.png");
//
                                                    //将文件上传到服务器
                                                    requestParams.setMultipart(true);  //指定上传文件格式
                                                    requestParams.addBodyParameter("circleImage", circleImageFile);
                                                    requestParams.addBodyParameter("circleImageUrl", result);
                                                    x.http().post(requestParams, new CommonCallback<String>() {
                                                        @Override
                                                        public void onSuccess(String result) {
                                                            if (circleImageFile.exists()) {
                                                                circleImageFile.delete();//删除文件，不占用用户存储空间
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

                                            Toast.makeText(CreateTaoquanActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {
                                            Toast.makeText(CreateTaoquanActivity.this, "创建失败,请检查你的网络设置", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(CancelledException cex) {
                                        }

                                        @Override
                                        public void onFinished() {
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
                    }
                });
                break;
            case R.id.iv_upload_circle_image: //点击选择拍照或者从相册选择照片作为淘圈头像
                final CharSequence[] items = {"相册", "拍照"};//CharSequence是接口，String实现
                new AlertDialog.Builder(this).setTitle("选择图片来源").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == SELECT_FROM_ALBUM) { //点击从相册选取
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");  //设置图片类型
                            startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_FROM_ALBUM);
                        } else { //点击拍照
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                                //获取sd卡目录
//                                File sdFile = Environment.getExternalStorageDirectory();
//                                circleImageFile = new File(Environment.getExternalStorageDirectory(),
//                                        sdFile + "/xiaoyuanershou/image"+"/circleImage.png");
//                                //Uri uri = Uri.fromFile(circleImageFile); //将Uri提为全局变量
//                                uri = Uri.fromFile(circleImageFile);
//                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent, SELECT_FROM_CAMERA);
//                            }else{
//                                Toast.makeText(CreateTaoquanActivity.this, "未找到储存卡，无法存储照片", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    }
                }).create().show();
                break;
            case R.id.iv_create_taoquan_return:
                finish();
                break;
            case R.id.iv_location:
                mLocationClient.start();  //开始定位
                if (isFirstClickLocation) { //如果是第一次点击定位，则后面不执行
                    isFirstClickLocation = false;
                    poiFlag++;
                    return;
                }
                String poi = "";
                if (poiList != null) {
                    int poiSize = poiList.size();
                    if (poiFlag != poiSize) {  //不是poiList里的最后一个poi的后一个
                        poi = poiList.get(poiFlag).getName();
                        poiFlag++;
                    } else { //是poiList里的最后一个poi的后一个
                        poi = "";
                        poiFlag = 0;
                    }
                }
                etCircleAddress.setText(myLocation.getAddrStr().replace("中国", "") + poi);
                break;
        }
    }

    //获取图片后的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_FROM_ALBUM) {//从相册选择
            //Uri uri = data.getData();
            uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            //Log.i("CreateTaoquanActivity", "onActivityResult: "+uri);
            try {
                if (bitmap != null) {
                    bitmap.recycle();//如果不释放的话，不断取图片，将会内存不够
                }
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                //暂时写入SD卡
                writeImageToSdCard();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //设置从相册选取的图片显示在ImageView上面
            ivUploadCircleImage.setImageBitmap(bitmap);
        } else if (resultCode == RESULT_OK && requestCode == SELECT_FROM_CAMERA) {//拍照选择
            if (bitmap != null) {
                bitmap.recycle();
            }
            //设置拍照后的图片显示在ImageView上面
            bitmap = data.getParcelableExtra("data");
            //crop(Uri.fromFile(circleImageFile));//将拍照后的图片裁剪并保存

            //暂时写入SD卡
            writeImageToSdCard();

            ivUploadCircleImage.setImageBitmap(bitmap);
        }
//        else if(resultCode == RESULT_OK&&requestCode==PHOTO_REQUEST_CUT){//裁剪图片并保存到临时文件
//            if(data!=null){
//                // 保存图片到internal storage
//                FileOutputStream outputStream;
//                try {
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                    out.flush();
//                    // out.close();
//                    // final byte[] buffer = out.toByteArray();
//                    // outputStream.write(buffer);
//                    outputStream = CreateTaoquanActivity.this.openFileOutput("_head_icon.jpg",
//                            Context.MODE_PRIVATE);
//                    out.writeTo(outputStream);
//                    out.close();
//                    outputStream.flush();
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
////            try {
////                if (circleImageFile != null && circleImageFile.exists())
////                    circleImageFile.delete();  //删除掉临时文件
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//        }
        else {
            Toast.makeText(CreateTaoquanActivity.this, "请重新选择图片", Toast.LENGTH_SHORT).show();
        }

        //处理图片，方法二，获得图片的地址再处理：
//        if(resultCode == RESULT_OK){
//            Uri uri = data.getData();
//            String [] proj={MediaStore.Images.Media.DATA};
//            Cursor cursor = managedQuery( uri,
//                    proj,                 // Which columns to btn_return
//                    null,                 // WHERE clause; which rows to btn_return (all rows)
//                    null,                 // WHERE clause selection arguments (none)
//                    null);                // Order-by clause (ascending by name)
//
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//
//            String path = cursor.getString(column_index);
//            bitmap = BitmapFactory.decodeFile(path);
//            System.out.println("the path is :" + path);
//        }else{
//            Toast.makeText(CreateTaoquanActivity.this, "请重新选择图片", Toast.LENGTH_SHORT).show();
//        }

    }

    //将从相册或者相机中选取的照片暂时写入SD卡,相应成功后会删掉
    public void writeImageToSdCard() {
        //开子子线程，将图片暂时写入SD卡
        new Thread() {
            @Override
            public void run() {
                super.run();
                //将图片写到SD卡，判断是否有SD卡
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //1、获取sd卡目录
                    File sdFile = Environment.getExternalStorageDirectory();
                    //2、获取想要存储的文件夹的路径
                    File imageFile = new File(sdFile + "/xiaoyuanershou/image");
                    if (!imageFile.exists()) {//如果文件夹不存在，则创建该目录
                        imageFile.mkdirs();
                    }
                    //3、获取文件完整目录
                    circleImageFile = new File(imageFile, "/circleImage.png");
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(circleImageFile); //获得文件输出流
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);//将bitmap写入输出流
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (fos != null) {
                            try {
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Toast.makeText(CreateTaoquanActivity.this, "未找到SD卡", Toast.LENGTH_SHORT).show();
                }

            }
        }.start();
    }

    //裁剪图片,以及进行一些设置
    public void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", true);
        //aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

//        intent.putExtra("outputFormat", "JPEG");//设置格式
//        intent.putExtra("noFaceDetection", true);//取消人脸识别功能
        intent.putExtra("return-data", true); //是否要返回值

        startActivityForResult(intent, PHOTO_REQUEST_CROP);
    }

    //定位监听
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            myLocation = location;
            myLatitude = location.getLatitude(); //纬度
            myLongitude = location.getLongitude(); //经度
            poiList = location.getPoiList();// POI数据

            etCircleAddress.setText(myLocation.getAddrStr().replace("中国", "") + poiList.get(0).getName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        poiFlag = 0;
    }


}
