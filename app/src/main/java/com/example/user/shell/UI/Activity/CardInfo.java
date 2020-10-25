package com.example.user.shell.UI.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.CardAdapter;
import com.example.user.shell.Connection;

import java.util.ArrayList;
import java.util.List;

public class CardInfo extends AppCompatActivity {

    int cookie;
    ListView listView;
    Button add_card;
    ImageView back;
    Connection con = new Connection();
    String reply = "000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_card_info );
        add_card=findViewById( R.id.add_card );
        back=findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
        cookie = getIntent().getIntExtra( "cookie", 10000 );
        listView = findViewById( R.id.card_last4 );
        reply=con.CSQI("601", cookie,"000");
        List<String> list = new ArrayList<>();
        if (!reply.substring( 0, 3 ).equals( "400" )) {
            int num = 0,l=1,r=1;
            num = Integer.parseInt(reply.substring(reply.lastIndexOf('#')+1,reply.length())); // 一共有num张银行卡
            for(int k = 0; k<num; k++){
                for(int i=l;i<reply.length();i++) if(reply.charAt(i)=='#'){
                    r=i;
                    break;
                }
                list.add( reply.substring(l,r) );
                l=r+1;
            }
        }
        if(list.isEmpty()){
            listView.setVisibility( View.GONE );
        }else {
            CardAdapter adapter=new CardAdapter( CardInfo.this,R.layout.card_item,list );
            listView.setAdapter( adapter );
        }
        setAdd_card();
    }

    private void setAdd_card(){
        add_card.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog( CardInfo.this );
                dialog.setContentView( R.layout.add_card_view );
                final EditText account=dialog.findViewById( R.id.account );
                final EditText password=dialog.findViewById( R.id.password );
                dialog.setTitle( "添加银行卡" );
                Button submit=dialog.findViewById( R.id.submit );
                Button cancel=dialog.findViewById( R.id.cancel );
                dialog.show();
                submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ask="000";
                        // 我的银行卡号是
                        String acc = account.getText().toString();
                        // 我的密码是
                        String pass=password.getText().toString();
                        ask=acc+"#"+pass;
                        reply=con.CSQI("602", cookie,ask);
                        Log.i( reply, "reply" );
                        if(reply.substring( 0,3 ).equals( "200" )){
                            Toast.makeText( CardInfo.this,"提交成功",Toast.LENGTH_SHORT ).show();
                            dialog.dismiss();
                        }
                    }
                } );
                cancel.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                } );
            }
        } );
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged( hasFocus );
        reply = con.CSQI( "501", cookie, "000" );
        if (reply.substring( 0, 3 ).equals( "400" )) {
            AlertDialog.Builder builder = new AlertDialog.Builder( CardInfo.this );
            builder.setTitle( "重复登陆" );
            builder.setMessage( "该账号在其他地方登陆，请重新登录。" );
            builder.setPositiveButton( "重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent( CardInfo.this, LoginActivity.class );
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
