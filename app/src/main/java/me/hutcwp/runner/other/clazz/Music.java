package me.hutcwp.runner.other.clazz;


import android.app.Activity;

import me.hutcwp.runner.other.Impl.PlayMusicBySystem;
import me.hutcwp.runner.other.interfaces.IPlayMusic;

/**
 * Created by Adminis on 2017/3/2.
 */

public class Music {

    private Activity activity;
    private IPlayMusic iPlayMusic = new PlayMusicBySystem();

    public Music(Activity activity) {
        this.activity = activity;
    }

    public void setiPlayMusic(IPlayMusic iPlayMusic) {
        this.iPlayMusic = iPlayMusic;
    }

    public void playMusic() {

        iPlayMusic.playMusic(activity);
    }
}
