package me.hutcwp.runner.other.Impl;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import me.hutcwp.runner.other.interfaces.IPlayMusic;

/**
 * Created by Adminis on 2017/3/2.
 */

public class PlayMusicBySystem implements IPlayMusic {


    @Override
    public void playMusic(Activity activity) {
        Intent intent_music = new Intent(Intent.ACTION_PICK);
        intent_music.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/playlist");
        intent_music.putExtra("withtabs", true); // 显示tab选项卡
        intent_music.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent j = Intent.createChooser(intent_music, "Choose an application getHistory open with:");
        if (j == intent_music) {
            activity.startActivity(j);
        } else {
            Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
            activity.startActivity(intent);
        }
    }
}
