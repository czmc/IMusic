package me.czmc.imusic.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import me.czmc.imusic.R;
import me.czmc.imusic.domain.MusicData;
import me.czmc.imusic.utils.Constans;
import me.czmc.imusic.utils.ImageUtils;
import me.czmc.imusic.utils.MediaUtils;
import me.czmc.imusic.view.CircleImageView;


public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        View view = findViewById(R.id.content);
        CircleImageView music_icon = (CircleImageView)findViewById(R.id.music_icon);
        TextView music_name = (TextView)findViewById(R.id.music_name);
        TextView music_author = (TextView)findViewById(R.id.music_author);
        TextView album = (TextView)findViewById(R.id.album);
        TextView music_duration = (TextView)findViewById(R.id.music_duration);
        TextView music_path = (TextView)findViewById(R.id.music_path);
        TextView lrc_path = (TextView)findViewById(R.id.lrc_path);
        TextView album_path = (TextView)findViewById(R.id.album_path);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        MusicData music = getIntent().getParcelableExtra(Constans.PASS＿MUSIC);
        if(music==null) return;
        music_name.setText(music.name);
        music_author.setText(music.artist);
        album.setText(music.album);
        music_duration.setText(MediaUtils.second2Time(music.duration));
        music_path.setText(music.path);
        lrc_path.setText(music.lrcPath);
        album_path.setText(music.coverPath);
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(music.coverPath);
        ImageLoader.getInstance().displayImage(music.coverPath == null ? "" : "file://" + music.coverPath, music_icon, ImageUtils.getOptions(R.drawable.cd));
    }
}
