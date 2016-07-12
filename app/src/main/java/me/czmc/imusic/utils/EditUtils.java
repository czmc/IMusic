package me.czmc.imusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by czmz on 15/12/4.
 */
public class EditUtils{
    public static SharedPreferences.Editor putValue(Context context,String key,Object value){

        SharedPreferences mSharedPreferences = context.getSharedPreferences(Constans.pre_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit =  mSharedPreferences.edit();
       if(value instanceof Boolean) {
           edit.putBoolean(key, (Boolean) value);
       }
       else if(value instanceof String){
           edit.putString(key, (String) value);
       }
       else if(value instanceof Float){
           edit.putFloat(key,(Float)value);
       }
       else if(value instanceof Integer){
           edit.putInt(key,(Integer)value);
       }
        edit.commit();
        return edit;
    }
    public static <T> T getValue(Context context,String key,Class<T> clazz) throws ClassNotFoundException{
        SharedPreferences mSharedPreferences = context.getSharedPreferences(Constans.pre_name, Context.MODE_PRIVATE);
        Object t = null;
        if(clazz.getName().equals(Boolean.class.getName())) {
            t=mSharedPreferences.getBoolean(key, false);
        }
        else if(clazz.getName().equals(String.class.getName())){
            t = mSharedPreferences.getString(key, "");
        }
        else if(clazz.getName().equals(Float.class.getName())){
            t =mSharedPreferences.getFloat(key, 0);
        }
        else if(clazz.getName().equals(Integer.class.getName())){
            t = mSharedPreferences.getInt(key, 0);
        }
        else{
            throw new ClassNotFoundException();
        }
        return (T)t;
    }
}
