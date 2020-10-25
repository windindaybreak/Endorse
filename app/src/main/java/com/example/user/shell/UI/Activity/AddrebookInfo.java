package com.example.user.shell.UI.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.Connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddrebookInfo extends AppCompatActivity {

    int cookie;
    Connection con = new Connection();
    String reply = "000";

    EditText Et_search;
    Button Btn_search;
    ListView listView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_addrebook_info );
        cookie = getIntent().getIntExtra( "cookie", 10000 );
        Btn_search=findViewById( R.id.Btn_search );
        Et_search=findViewById( R.id.Et_search );
        listView=findViewById( R.id.addrebook );
        back=findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
        Btn_search.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Et_search.length()==0){
                    Toast.makeText( AddrebookInfo.this,
                            "搜索内容不能为空",Toast.LENGTH_SHORT ).show();
                }else{
                    reply= con.CSQI("605", cookie, Et_search.getText().toString());
                    List<String> list = new ArrayList<>();
                    List<String> data = new ArrayList<>();
                    int p = reply.length() - 1, q = p;
                    String temp;
                    while (p>=0&&reply.charAt(p) == '#')
                        p--;
                    while (p >= 0) {
                        q = p;
                        while (p >= 0 && reply.charAt(p) != '#')
                            p--;
                        temp = reply.substring(p + 1, q + 1);
                        data.add(temp);
                        p--;
                    }
                    Collections.reverse(data);
                    for (int i=0;i<data.size();i+=2) {
                        list.add( data.get(i)+" : "+data.get(i+1) );
                    }
                    if(list.isEmpty()){
                        Toast.makeText( AddrebookInfo.this,
                                "未找到相关联系人",Toast.LENGTH_SHORT ).show();
                        return;
                    }
                        ArrayAdapter<String> adapter=new ArrayAdapter<>( AddrebookInfo.this,
                                android.R.layout.simple_list_item_1,list );
                    listView.setAdapter( adapter );
                }
            }
        } );
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged( hasFocus );
        reply = con.CSQI( "501", cookie, "000" );
        if (reply.substring( 0, 3 ).equals( "400" )) {
            AlertDialog.Builder builder = new AlertDialog.Builder( AddrebookInfo.this );
            builder.setTitle( "重复登陆" );
            builder.setMessage( "该账号在其他地方登陆，请重新登录。" );
            builder.setPositiveButton( "重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent( AddrebookInfo.this, LoginActivity.class );
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
