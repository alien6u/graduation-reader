package com.jayqqaa12.abase.kit.media;

import android.media.MediaPlayer;

import com.jayqqaa12.abase.core.Abase;

public class SoundKit  
{
	
	public static void playSound(int resId, float volume)
	{
		MediaPlayer player = MediaPlayer.create(Abase.getContext(), resId);
		player.setVolume(volume, volume);
		player.start();

	}

}
