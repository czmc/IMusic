package me.czmc.imusic.utils;

/**
 * Created by MZone on 2/22/2016.
 */
public class StringUtils {
    /**
     *
     *判断String是否合法
     * @param str
     * @return
     */
    public static boolean isStringInvalid(String str)
    {
        if (str == null || str.length() < 1)
        {
            return true;
        }
        return false;
    }

    /**
     * 输入10000@192.168.1.104/Smack
     *
     * @param user
     * @return 10000@192.168.1.104
     */
    public static String escapeUserResource(String user) {
        return user.replaceAll("/.+$", "");
    }

    /**
     * 输入10000@192.168.1.104/Smack
     *
     * @param user
     * @return 10000
     */
    public static String escapeUserHost(String user) {
        return user.replaceAll("@.+$", "");
    }

}
