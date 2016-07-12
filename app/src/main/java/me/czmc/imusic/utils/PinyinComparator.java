package me.czmc.imusic.utils;


import java.util.Comparator;

import me.czmc.imusic.domain.MusicData;

public class PinyinComparator implements Comparator<MusicData> {

	public int compare(MusicData o1, MusicData o2) {
		if (o1.equals("@")
				|| o2.firstLetter.equals("#")) {
			return -1;
		} else if (o1.firstLetter.equals("#") || o2.firstLetter.equals("@")) {
			return 1;
		} else {
			return o1.firstLetter.compareTo(o2.firstLetter);
		}
	}

}
