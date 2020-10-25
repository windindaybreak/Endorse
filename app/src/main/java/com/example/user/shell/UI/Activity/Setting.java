package com.example.user.shell.UI.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.Connection;

public class Setting extends AppCompatActivity {

    int cookie;
    Connection con = new Connection();
    String reply = "000";
    Button quit,about;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setting );
        cookie=getIntent().getIntExtra( "cookie",10000 );
        quit = findViewById( R.id.quit_log );
        quit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( Setting.this );
                builder.setTitle( "提示" );
                builder.setMessage( "是否退出登录?" );
                builder.setPositiveButton( "确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent( Setting.this, LoginActivity.class );
                        startActivity( intent );
                        if (AfterEntering.mActivity != null)
                            AfterEntering.mActivity.finish();
                        SharedPreferences sPreferences = getSharedPreferences("UserLoginState", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sPreferences.edit();
                        editor.clear ().apply ();
                        finish();
                        dialog.dismiss();
                    }
                } );
                builder.setNegativeButton( "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );
                builder.show();

            }
        } );
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged( hasFocus );
        reply = con.CSQI( "501", cookie, "000" );
        if (reply.substring( 0, 3 ).equals( "400" )) {
            AlertDialog.Builder builder = new AlertDialog.Builder( Setting.this );
            builder.setTitle( "重复登陆" );
            builder.setMessage( "该账号在其他地方登陆，请重新登录。" );
            builder.setPositiveButton( "重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent( Setting.this, LoginActivity.class );
                    startActivity( intent );
                }
            } );
            builder.setNegativeButton( "退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.os.Process.killProcess( android.os.Process.myPid() );
                }
            } );
            builder.show();
        }
    }
}
