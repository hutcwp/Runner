package com.hut.cwp.runner.activitys.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hut.cwp.runner.R;

import es.dmoral.toasty.Toasty;

public class About extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_about);


    }

    /**
     * 返回按钮
     * @param view
     */
    public void onBack(View view){

        this.finish();
    }

    /**
     * 打开github
     * @param view
     */
    public void gotoGithub(View view){

        String url = getString(R.string.github_url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    /**
     * 打开博客地址
     * @param view
     */
    public void gotoBlog(View view){

        String url = getString(R.string.blog_url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }



    /**
     * 发邮件
     * @param view
     */
    public void mailTo(View view){

        try{
            Uri uri = Uri.parse(getString(R.string.mail_to_me));
            Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_topic));
            intent.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.device_model) + Build.MODEL + "\n"
                            + getString(R.string.sdk_version) + Build.VERSION.RELEASE + "\n"
                            );
            startActivity(intent);
        }catch (android.content.ActivityNotFoundException ex){

            Toasty.error(this,getString(R.string.no_mail),Toast.LENGTH_LONG).show();
        }
    }
}
