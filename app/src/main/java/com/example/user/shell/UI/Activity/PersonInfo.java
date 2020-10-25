package com.example.user.shell.UI.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.Connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonInfo extends AppCompatActivity {

    int cookie;
    Connection con = new Connection();
    String reply = "000";
    TextView name,sex,company,mail,phone,num,position,level;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_person_info );
        cookie = getIntent().getIntExtra( "cookie", 10000 );
        name=findViewById( R.id.name );
        sex=findViewById( R.id.sex );
        company=findViewById( R.id.company );
        mail=findViewById( R.id.mail );
        phone=findViewById( R.id.phone );
        num=findViewById( R.id.comp_num );
        position=findViewById( R.id.department );
        level=findViewById( R.id.level );
        back=findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
        List<String> data = new ArrayList<>();
        reply = con.CSQI( "606", cookie, "000" ); // 400# company not found
        int p = reply.length() - 1, q = p;
        String temp;
        while (p >= 0 && reply.charAt( p ) == '#')
            p--;
        while (p >= 0) {
            q = p;
            while (p >= 0 && reply.charAt( p ) != '#')
                p--;
            temp = reply.substring( p + 1, q + 1 );
            data.add( temp );
            p--;
        }
        Collections.reverse( data );
        name.setText( data.get( 0 ) );
        sex.setText( data.get( 1 ) );
        company.setText( data.get( 2 ) );
        mail.setText( data.get( 3 ) );
        phone.setText( data.get( 4 ) );
        num.setText( data.get( 5 ) );
        position.setText( data.get( 6 ) );
        level.setText( data.get( 7 ) );
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged( hasFocus );
        reply = con.CSQI( "501", cookie, "000" );
        if (reply.substring( 0, 3 ).equals( "400" )) {
            AlertDialog.Builder builder = new AlertDialog.Builder( PersonInfo.this );
            builder.setTitle( "重复登陆" );
            builder.setMessage( "该账号在其他地方登陆，请重新登录。" );
            builder.setPositiveButton( "重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent( PersonInfo.this, LoginActivity.class );
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
