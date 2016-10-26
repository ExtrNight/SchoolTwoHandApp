package com.school.twohand.activity;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.school.twohand.entity.MusicDataInfro;
import com.school.twohand.schooltwohandapp.R;
import com.school.twohand.utils.CommonAdapter;
import com.school.twohand.utils.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ShowMusicActivity extends AppCompatActivity {

    private List<MusicDataInfro> musicDataInfros;
    private ListView musicList;
    private CommonAdapter<MusicDataInfro> commonAdapter;//通用适配器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_music);
        musicDataInfros = new ArrayList<>();
        musicList = (ListView) findViewById(R.id.musicLists);

        //读取外部多媒体文件
        getData();
        Log.i("getData", "onCreate: "+musicDataInfros);
        //展示音乐条目
        if (commonAdapter == null) {
            commonAdapter = new CommonAdapter<MusicDataInfro>(this,musicDataInfros,R.layout.music_item) {
                @Override
                public void convert(ViewHolder viewHolder, MusicDataInfro musicDataInfro, int position) {
                    TextView musicNameView = viewHolder.getViewById(R.id.musicName);
                    TextView musicSingerView = viewHolder.getViewById(R.id.musicSinger);
                    TextView musicDurationView = viewHolder.getViewById(R.id.musicTime);
                    int duration =  musicDataInfro.getDuration();//时间的但是是int类型
                    String durationString = formatDate(duration);//转化mm:ss的字符串
                    //将每条音乐信息逐个给每个item中的控件赋值
                    musicNameView.setText(musicDataInfro.getMusicName());
                    musicSingerView.setText(musicDataInfro.getSinger());
                    musicDurationView.setText(durationString);
                    Log.i("getData", "convert: "+musicDataInfro.getMusicName()+musicDataInfro.getSinger()+musicDataInfro.getDuration());
                }
            };
        }else {
            commonAdapter.notifyDataSetChanged();
        }
        //list配上适配器
        musicList.setAdapter(commonAdapter);
    }

    /**
     * 将int类型转化成时间
     * @param duration
     * @return
     */
    public String formatDate(int duration ){

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(duration);

    }

    /**
     * 读取外部多媒体文件,获取系统音乐文件集合
     */
    public void getData(){

        //获取系统音乐文件集合赋给List -->MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        String projection[] = {
                MediaStore.Audio.Media._ID,//唯一id
                //MediaStore.Audio.Media.ALBUM,//专辑名
                MediaStore.Audio.Media.ARTIST,//演唱者
                MediaStore.Audio.Media.TITLE,//音乐名称
                //MediaStore.Audio.Media.DATA,//音乐文件路径
                MediaStore.Audio.Media.DURATION,//播放时长
        };
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
        //将系统音乐详细信息遍历出来赋值给list集合
        while(cursor.moveToNext()){
            int _id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String musicName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            MusicDataInfro musicDataInfro = new MusicDataInfro(_id,musicName,singer,duration);
            musicDataInfros.add(musicDataInfro);
        }
    }
}
