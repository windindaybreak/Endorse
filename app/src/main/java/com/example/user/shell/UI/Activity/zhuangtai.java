package com.example.user.shell.UI.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.Connection;

import java.util.ArrayList;
import java.util.List;

public class zhuangtai extends AppCompatActivity {

    int cookie;
    Connection con = new Connection();
    String reply = "000";
    Button tai1, tai2, tai3,tem;
    ListView listView;
    ImageView background,back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_zhuangtai );
        cookie = getIntent().getIntExtra( "cookie", 10000 );
        back=findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
        tai1 = findViewById( R.id.WeiRefer );
        tai2 = findViewById( R.id.YiRefer );
        tai3 = findViewById( R.id.YiShenHe );
        listView = findViewById( R.id.余额 );
        background=findViewById( R.id.background );
        tem=tai1;
        setView();
    }

    private void setView() {
        final List<String>[] lists = new ArrayList[3];
        lists[0] = new ArrayList<>();
        lists[1] = new ArrayList<>();
        lists[2] = new ArrayList<>();
        reply = con.CSQI( "411", cookie, "000" );
        lists[Integer.parseInt( reply.charAt( 1 ) + "" )].add( "差旅费申请" );
        lists[Integer.parseInt( reply.charAt( 3 ) + "" )].add( "招待费申请" );
        lists[Integer.parseInt( reply.charAt( 5 ) + "" )].add( "交通费申请" );
        lists[Integer.parseInt( reply.charAt( 7 ) + "" )].add( "车辆费申请" );
        lists[Integer.parseInt( reply.charAt( 9 ) + "" )].add( "培训费申请" );
        lists[Integer.parseInt( reply.charAt( 11 ) + "" )].add( "采购费申请" );
        lists[Integer.parseInt( reply.charAt( 13 ) + "" )].add( "咨询费申请" );
        lists[Integer.parseInt( reply.charAt( 15 ) + "" )].add( "租赁费申请" );
        lists[Integer.parseInt( reply.charAt( 17 ) + "" )].add( "运输费申请" );
        if(!lists[0].isEmpty())
            background.setVisibility( View.GONE );
        ArrayAdapter<String> adapter = new ArrayAdapter<>( zhuangtai.this,
                android.R.layout.simple_list_item_1, lists[0] );
        listView.setAdapter( adapter );
        tai1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tem.setTextColor( Color.BLACK );
                tem=tai1;
                tai1.setTextColor( Color.parseColor( "#8F03A9F4" ) );
                ArrayAdapter<String> adapter = new ArrayAdapter<>( zhuangtai.this,
                        android.R.layout.simple_list_item_1, lists[0] );
                listView.setAdapter( adapter );
                if(!lists[0].isEmpty())
                    background.setVisibility( View.GONE );
                else
                    background.setVisibility( View.VISIBLE );
            }
        } );
        tai2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tem.setTextColor( Color.BLACK );
                tem=tai2;
                tai2.setTextColor( Color.parseColor( "#8F03A9F4" ) );
                ArrayAdapter<String> adapter = new ArrayAdapter<>( zhuangtai.this,
                        android.R.layout.simple_list_item_1, lists[1] );
                listView.setAdapter( adapter );
                if(!lists[1].isEmpty())
                    background.setVisibility( View.GONE );
                else
                    background.setVisibility( View.VISIBLE );
            }
        } );
        tai3.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tem.setTextColor( Color.BLACK );
                tem=tai3;
                tai3.setTextColor( Color.parseColor( "#8F03A9F4" ) );
                ArrayAdapter<String> adapter = new ArrayAdapter<>( zhuangtai.this,
                        android.R.layout.simple_list_item_1, lists[2] );
                listView.setAdapter( adapter );
                if(!lists[2].isEmpty())
                    background.setVisibility( View.GONE );
                else
                    background.setVisibility( View.VISIBLE );
            }
        } );
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged( hasFocus );
        reply = con.CSQI( "501", cookie, "000" );
        if (reply.substring( 0, 3 ).equals( "400" )) {
            AlertDialog.Builder builder = new AlertDialog.Builder( zhuangtai.this );
            builder.setTitle( "重复登陆" );
            builder.setMessage( "该账号在其他地方登陆，请重新登录。" );
            builder.setPositiveButton( "重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent( zhuangtai.this, LoginActivity.class );
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
