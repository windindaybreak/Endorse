package com.example.user.shell.UI.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.Connection;
import com.example.user.shell.IntroductionItem;
import com.example.user.shell.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class CompanyInfo extends AppCompatActivity {

    int cookie, budget_status;
    Connection con = new Connection();
    String reply = "000";
    ListView listView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_company_info );
        cookie = getIntent().getIntExtra( "cookie", 10000 );
        back=findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
        listView=findViewById( R.id.company );
        List<IntroductionItem> list = new ArrayList<>();
        IntroductionItem item;
        reply = con.CSQI( "603", cookie, "000" ); // 400# company not found
        int l = 1, r = 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        item=new IntroductionItem( "公司名称:",reply.substring( l, r )  );
        list.add( item );
        l = r + 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        item=new IntroductionItem( "公司地址:",reply.substring( l, r )  );
        list.add( item );
        l = r + 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        item=new IntroductionItem( "公司邮箱:",reply.substring( l, r )  );
        list.add( item );
        l = r + 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        item=new IntroductionItem( "公司类型:",reply.substring( l, r )  );
        list.add( item );
        l = r + 1;;
        ItemAdapter adapter=new ItemAdapter( CompanyInfo.this,R.layout.info_item,list );
        listView.setAdapter( adapter );
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged( hasFocus );
        reply = con.CSQI( "501", cookie, "000" );
        if (reply.substring( 0, 3 ).equals( "400" )) {
            AlertDialog.Builder builder = new AlertDialog.Builder( CompanyInfo.this );
            builder.setTitle( "重复登陆" );
            builder.setMessage( "该账号在其他地方登陆，请重新登录。" );
            builder.setPositiveButton( "重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent( CompanyInfo.this, LoginActivity.class );
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
