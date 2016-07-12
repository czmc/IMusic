package me.czmc.imusic.domain;

/**
 * Created by MZone on 3/18/2016.
 */

import android.content.Context;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDataDB extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String NAME = "IMUSIC";

    private static final String TABLE_MUSIC_NAME = "Video";
    private static final String TABLE_LIST_NAME = "Fav";

    private static final String DATABASE_REMOTE_MUSIC_CREATE = "create table RemoteMusic(_id integer primary key autoincrement, "
            + "id text not null UNIQUE,"
            + "name text not null,"
            + "musicurl text not null,"
            + "artist text not null,"
            + "album text not null,"
            + "duration text not null,"
            + "lrcoath text not null,"
            + "coverpath text not null,"
            + "time text not null,"
            + ");";

    private static final String DATABASE_LIST_CREATE = "create table Fav("
            + "_id integer primary key autoincrement," + "mid integer UNIQUE,"
            + "uri text not null,"
            + "addtime text not null" + ")";

    public MusicDataDB(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_REMOTE_MUSIC_CREATE);
        db.execSQL(DATABASE_LIST_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
