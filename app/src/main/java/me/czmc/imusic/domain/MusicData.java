package me.czmc.imusic.domain;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by czmz on 15/12/6.
 */
@Table(name="musics")
public class MusicData extends Model implements Parcelable{

    public final static String BUNDLE_KEY_MUSICDATA="BUNDLE_KEY_MUSICDATA";

    private final static String BUNDLE_KEY_ALBUMID="BUNDLE_KEY_ALBUMID";
    private final static String BUNDLE_KEY_ALBUM="BUNDLE_KEY_ALBUM";
    private final static String BUNDLE_KEY_SIZE="BUNDLE_KEY_SIZE";
    private final static String BUNDLE_KEY_NAME="BUNDLE_KEY_NAME";
    private final static String BUNDLE_KEY_ARTIST="BUNDLE_KEY_ARTIST";
    private final static String BUNDLE_KEY_PATH="BUNDLE_KEY_PATH";
    private final static String BUNDLE_KEY_TIME="BUNDLE_KEY_TIME";
    private final static String BUNDLE_KEY_ID = "BUNDLE_KEY_ID";
    private final static String BUNDLE_KEY_LRCPATH = "BUNDLE_KEY_LRCPATH";
    private final static String BUNDLE_KEY_COVERPATH = "BUNDLE_KEY_COVERPATH";
    private final static String BUNDLE_KEY_FIERSTLETTER = "BUNDLE_KEY_FIERSTLETTER";

    @Column(name = "musicid" ,unique = true, onUniqueConflict = Column.ConflictAction.IGNORE, onUniqueConflicts = Column.ConflictAction.IGNORE)
    public long musicid;
    @Column(name = "albumId") public long albumId;
    @Column(name = "size")public long size;
    @Column(name = "album")public String album;
    @Column(name = "name") public String name;
    @Column(name = "artist") public String artist;
    @Column(name = "path")public String path;
    @Column(name = "duration")public long duration;
    @Column(name = "lrcPath") public String lrcPath;
    @Column(name = "coverPath") public String coverPath;
    @Column(name = "firstLetter") public String firstLetter;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putLong(BUNDLE_KEY_ALBUMID, albumId);
        bundle.putLong(BUNDLE_KEY_ID, musicid);
        bundle.putLong(BUNDLE_KEY_SIZE, size);
        bundle.putString(BUNDLE_KEY_ALBUM, album);
        bundle.putString(BUNDLE_KEY_NAME, name);
        bundle.putString(BUNDLE_KEY_ARTIST, artist);
        bundle.putString(BUNDLE_KEY_PATH, path);
        bundle.putLong(BUNDLE_KEY_TIME, duration);
        bundle.putString(BUNDLE_KEY_LRCPATH, lrcPath);
        bundle.putString(BUNDLE_KEY_COVERPATH, coverPath);
        bundle.putString(BUNDLE_KEY_FIERSTLETTER, firstLetter);
        dest.writeBundle(bundle);
    }
    public void readFromParcel(Parcel in){
        albumId =in.readLong();
        musicid = in.readLong();
        size = in.readLong();
        album = in.readString();
        name =in.readString();
        artist =in.readString();
        path =in.readString();
        duration =in.readLong();
        lrcPath = in.readString();
        coverPath = in.readString();
        firstLetter = in.readString();
    }
    public static final Creator<MusicData> CREATOR = new Creator<MusicData>() {

        @Override
        public MusicData createFromParcel(Parcel source) {
            MusicData Data = new MusicData();

            Bundle mBundle = new Bundle();
            mBundle = source.readBundle();

            Data.albumId = mBundle.getLong(BUNDLE_KEY_ALBUMID);
            Data.musicid = mBundle.getLong(BUNDLE_KEY_ID);
            Data.size = mBundle.getLong(BUNDLE_KEY_SIZE);
            Data.album = mBundle.getString(BUNDLE_KEY_ALBUM);
            Data.name = mBundle.getString(BUNDLE_KEY_NAME);
            Data.artist = mBundle.getString(BUNDLE_KEY_ARTIST);
            Data.path = mBundle.getString(BUNDLE_KEY_PATH);
            Data.duration = mBundle.getInt(BUNDLE_KEY_TIME);
            Data.lrcPath = mBundle.getString(BUNDLE_KEY_LRCPATH);
            Data.coverPath = mBundle.getString(BUNDLE_KEY_COVERPATH);
            Data.firstLetter = mBundle.getString(BUNDLE_KEY_FIERSTLETTER);

            return Data;
        }

        @Override
        public MusicData[] newArray(int size) {
            return new MusicData[size];
        }
    };

}
