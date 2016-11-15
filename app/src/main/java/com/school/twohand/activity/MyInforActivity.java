package com.school.twohand.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.school.twohand.activity.wheelview.DateUtils;
import com.school.twohand.activity.wheelview.JudgeDate;
import com.school.twohand.activity.wheelview.ScreenInfo;
import com.school.twohand.activity.wheelview.WheelMain;
import com.school.twohand.entity.User;
import com.school.twohand.myApplication.MyApplication;
import com.school.twohand.query.entity.InsertUserBean;
import com.school.twohand.schooltwohandapp.MainActivity;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.*;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * 修改个人资料的页面
 */
public class MyInforActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.goback)
    ImageView goback;
    @InjectView(R.id.checksex)
    TextView checksex;
    @InjectView(R.id.rl_sex)
    RelativeLayout rlSex;
    @InjectView(R.id.checkbirth)
    TextView checkbirth;
    @InjectView(R.id.rl_birth)
    RelativeLayout rlBirth;
    @InjectView(R.id.rl_noaddress)
    RelativeLayout rlNoaddress;
    @InjectView(R.id.rl_getaddress)
    RelativeLayout rlGetaddress;
    @InjectView(R.id.rl_introduce)
    RelativeLayout rlIntroduce;
    @InjectView(R.id.checknoaddress)
    TextView checknoaddress;
    @InjectView(R.id.tv_person)
    TextView tvPerson;

    com.school.twohand.utils.CircleImageView ivHeadimg2;

    private WheelMain wheelMainDate;
    private String beginTime;

    public static final int SELECT_PIC = 12;
    public static final int TAKE_PHOTO = 13;
    public static final int CROP_PHOTO = 14;

    private Uri imageUri;

    private File file;
    String items[] = {"相册选择", "拍照"};
    String items1[] = {"男", "女"};
    MyApplication myApplication;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_infor);

        ButterKnife.inject(this);
        ivHeadimg2 = (com.school.twohand.utils.CircleImageView) findViewById(R.id.iv_headimg2);
        myApplication = (MyApplication) getApplication();

        user = myApplication.getUser();

        getData();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //目录，文件名Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            file = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
            imageUri = Uri.fromFile(file);
        }

    }


    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void showBottoPopupWindow() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        View menuView = LayoutInflater.from(this).inflate(R.layout.show_popup_window, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int) (width * 0.8),
                ActionBar.LayoutParams.WRAP_CONTENT);
        ScreenInfo screenInfoDate = new ScreenInfo(this);
        wheelMainDate = new WheelMain(menuView, true);
        wheelMainDate.screenheight = screenInfoDate.getHeight();
        String time = DateUtils.currentMonth().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-DD")) {
            try {
                calendar.setTime(new Date(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelMainDate.initDateTimePicker(year, month, day);
        final String currentTime = wheelMainDate.getTime().toString();
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(rlBirth, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        backgroundAlpha(0.6f);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText("选择生日");
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                beginTime = wheelMainDate.getTime().toString();
                try {
                    Date begin = dateFormat.parse(currentTime);
                    Date end = dateFormat.parse(beginTime);
                    checkbirth.setText(DateUtils.formateStringH(beginTime, DateUtils.yyyyMMddHHmm));
                    Timestamp timestamp = new Timestamp(end.getTime());
                    InsertUserBean insertUserBean = new InsertUserBean();
                    insertUserBean.setUserId(user.getUserId());
                    insertUserBean.setUserBirthday(timestamp);
                    setInformation(insertUserBean);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }


    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }


    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }


    @OnClick({R.id.goback, R.id.rl_headImg, R.id.rl_sex, R.id.rl_birth, R.id.rl_noaddress, R.id.rl_getaddress, R.id.rl_introduce})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goback:
                finish();
                break;
            case R.id.rl_headImg:
                new AlertDialog.Builder(this).setTitle("选择").setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                //相册选择
                                Intent intent = new Intent(Intent.ACTION_PICK, null);
                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        "image/*");
                                startActivityForResult(intent, SELECT_PIC);

                                break;


                            case 1:

                                //拍照:
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                startActivityForResult(intent2, TAKE_PHOTO);

                                break;
                        }
                    }
                }).show();
                break;

            case R.id.rl_sex:
                new AlertDialog.Builder(this).setItems(items1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checksex.setText(items1[which]);
                        switch (which) {
                            case 0:
                                String sex = "男";
                                InsertUserBean insertuserBean = new InsertUserBean();
                                insertuserBean.setUserId(user.getUserId());
                                insertuserBean.setUserSex(sex);
                                setInformation(insertuserBean);
                                break;
                            case 1:
                                String sex1 = "女";
                                InsertUserBean insertuserBean1 = new InsertUserBean();
                                insertuserBean1.setUserId(user.getUserId());
                                insertuserBean1.setUserSex(sex1);
                                setInformation(insertuserBean1);
                                break;
                        }
                    }
                }).show();
                break;
            case R.id.rl_birth:
                showBottoPopupWindow();
                break;
            case R.id.rl_noaddress:
                LayoutInflater factory = LayoutInflater.from(MyInforActivity.this);//提示框
                final View view1 = factory.inflate(R.layout.noaddress, null);//这里必须是final的
                final EditText edit = (EditText) view1.findViewById(R.id.editText1);//获得输入框对象

                new AlertDialog.Builder(MyInforActivity.this)
                        .setTitle("亲，请输入自己常住地")//提示框标题
                        .setView(view1)
                        .setPositiveButton("确定",//提示框的两个按钮
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        checknoaddress.setText(edit.getText());
                                        InsertUserBean insertuserBean3 = new InsertUserBean();
                                        insertuserBean3.setUserId(user.getUserId());
                                        insertuserBean3.setUserAddress(edit.getText().toString());
                                        setInformation(insertuserBean3);


                                    }
                                }).setNegativeButton("取消", null).create().show();


                break;
            case R.id.rl_getaddress:
                Intent intent4 = new Intent(MyInforActivity.this, GetAddressActivity.class);
                startActivity(intent4);

                break;
            case R.id.rl_introduce:

                Intent intent3 = new Intent(MyInforActivity.this, IntroduceActivity.class);
                intent3.putExtra("flag", "name");
                startActivityForResult(intent3, 1);


                break;
        }
    }

    public void crop(Uri uri) {
        //  intent.setType("image/*");
        //裁剪
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PIC:
                if (data != null) {
                    crop(data.getData());
                }
                break;
            case TAKE_PHOTO:
                crop(Uri.fromFile(file));

                break;
            case CROP_PHOTO:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap bitmap = extras.getParcelable("data");
                        showImage(bitmap);
                    }
                }
            case 1:
                if (data!=null){
                    String profile = data.getStringExtra("Profile");
                    tvPerson.setText(profile);
                InsertUserBean insertuserBean4 = new InsertUserBean();
                insertuserBean4.setUserId(user.getUserId());
                insertuserBean4.setUserPersonalProfile(profile);
                setInformation(insertuserBean4);



                }
                break;

        }
    }


    //显示图片，上传服务器


    public void showImage(Bitmap bitmap) {
        ImageView imageView1 = (ImageView) findViewById(R.id.iv_headimg1);
        imageView1.setVisibility(View.GONE);
        ivHeadimg2.setVisibility(View.VISIBLE);
        ivHeadimg2.setImageBitmap(bitmap);//iv显示图片
        saveImage(bitmap);//保存文件
        uploadImage();//上传服务器
    }

    //将bitmap保存在文件中
    public void saveImage(Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fos);
    }

    //上传图片
    public void uploadImage() {
        RequestParams requestParams = new RequestParams(NetUtil.url + "UploadHeadServlet");
        requestParams.setMultipart(true);
        requestParams.addBodyParameter("userId",user.getUserId()+"");
        requestParams.addBodyParameter("file", file);
        requestParams.setUseCookie(true);
       x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("MyInforActivity", "onSuccess: onSuccess");
                JMessageClient.updateUserAvatar(file, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0){
                            Toast.makeText(MyInforActivity.this, "头像更新成功", Toast.LENGTH_SHORT).show();
                        }
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


    public void setInformation(InsertUserBean insertUserBean) {
        String url = NetUtil.url + "UserServlet";
        RequestParams requestParams = new RequestParams(url);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String insertUserBeanJson = gson.toJson(insertUserBean);
        requestParams.addQueryStringParameter("insertUserBeanJson", insertUserBeanJson);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(MyInforActivity.this, "修改成功~", Toast.LENGTH_SHORT).show();
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

    public void getData(){
        String url= NetUtil.url+"QueryInfoServlet";
        RequestParams requestParams=new RequestParams(url);
        requestParams.addQueryStringParameter("userId",user.getUserId()+"");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson=new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                User user1= gson.fromJson(result,User.class);
                Log.i("MyInforActivity", "onSuccess: "+user1);

                ImageView imageView1 = (ImageView) findViewById(R.id.iv_headimg1);
                if(imageView1!=null){
                    imageView1.setVisibility(View.VISIBLE);
                    ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true).build();
                    x.image().bind(imageView1,NetUtil.imageUrl+user1.getUserHead(),imageOptions);
                }

                TextView tv1= (TextView) findViewById(R.id.checksex);
                if(tv1!=null){
                    tv1.setText(user1.getUserSex());
                }

                TextView tv2= (TextView) findViewById(R.id.checkbirth);
                Date date=new Date(user1.getUserBirthday().getTime());
                SimpleDateFormat aa=new SimpleDateFormat("yyyy-MM-dd");
                String a=aa.format(date);
                tv2.setText(a);

                TextView tv3= (TextView) findViewById(R.id.checknoaddress);
                if(tv3!=null){
                    tv3.setText(user1.getUserAddress());
                }

                TextView tv4= (TextView) findViewById(R.id.tv_person);
                tv4.setText(user1.getUserPersonalProfile());

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.getStackTrace();
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
