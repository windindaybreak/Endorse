package com.example.user.shell.UI.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.Connection;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoview;
    Connection con = new Connection();
    String reply = "000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_splash );

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //检测用户登陆状态
        SharedPreferences sPreferences=this.getSharedPreferences("UserLoginState", this.MODE_PRIVATE);
        String account=sPreferences.getString("account", "0");
        final int cookie=sPreferences.getInt ("cookie", 10000);
        reply=con.CSQI("502",cookie,account);
        //Toast.makeText ( this,"reply"+reply,Toast.LENGTH_LONG ).show ();

        videoview = findViewById(R.id.video_view);
        videoview.setVideoURI( Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.open));
        videoview.start();

        final Handler handler = new Handler (  );
        final Runnable runnable = new Runnable ( ) {
            @Override
            public void run() {
                if(reply.equals ( "400#" )){
                    Intent login_intent=new Intent (SplashActivity.this, LoginActivity.class); //使用intent方法，在活动间跳转
                    startActivity(login_intent);
                }
                else if(reply.equals ( "200#" )){
                    Intent main_intent=new Intent (SplashActivity.this, AfterEntering.class); //使用intent方法，在活动间跳转
                    main_intent.putExtra("cookie",cookie);
                    startActivity(main_intent);
                }
                finish();
            }
        };
        handler.postDelayed ( runnable,3000 );//延时启动runnable中的run


        videoview.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if(reply.equals ( "400#" )){
                    Intent login_intent=new Intent (SplashActivity.this, LoginActivity.class); //使用intent方法，在活动间跳转
                    startActivity(login_intent);
                }
                else if(reply.equals ( "200#" )){
                    Intent main_intent=new Intent (SplashActivity.this, AfterEntering.class); //使用intent方法，在活动间跳转
                    main_intent.putExtra("cookie",cookie);
                    startActivity(main_intent);
                }
                handler.removeCallbacks(runnable);
                finish();
            }
        } );
    }
}
