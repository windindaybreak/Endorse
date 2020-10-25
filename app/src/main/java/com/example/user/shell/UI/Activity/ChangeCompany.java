package com.example.user.shell.UI.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.Connection;

import java.util.ArrayList;
import java.util.List;

public class ChangeCompany extends AppCompatActivity {

    int cookie;
    Connection con = new Connection();
    String reply = "000";
    Button submit;
    RadioGroup radioGroup;
    TextView no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_change_company );
        cookie = getIntent().getIntExtra( "cookie", 10000 );
        radioGroup = findViewById( R.id.company );
        submit = findViewById( R.id.submit );
        no=findViewById( R.id.no_item );
        setView();
    }

    private void setView() {
        reply = con.CSQI( "607", cookie, "000" );
        List<String> list = new ArrayList<>();
        ArrayAdapter<String> adapter = null;
        int num = Integer.parseInt( reply.substring( reply.lastIndexOf( '#' ) + 1, reply.length() ) );
        String[] company_name = new String[num];
        final int[] new_empno = new int[num];
        if (num == 0) {
            no.setText( "no subaccount" );
            no.setVisibility( View.VISIBLE );
            radioGroup.setVisibility( View.GONE );
            submit.setVisibility( View.GONE );
        } else {
            int l = 1, r = 1;
            for (int k = 0; k < num; k++) {
                for (int i = l; i < reply.length(); i++)
                    if (reply.charAt( i ) == '#') {
                        r = i;
                        break;
                    }
                new_empno[k] = Integer.parseInt( reply.substring( l, r ) );
                // 相关账号的empno 这个不需要显示出来 当用户选择哪个子公司登陆的时候再把对应new_empno 传进来
                l = r + 1;
                for (int i = l; i < reply.length(); i++)
                    if (reply.charAt( i ) == '#') {
                        r = i;
                        break;
                    }
                company_name[k] = reply.substring( l, r );  // 这个就是相关账号的 子公司
                RadioButton radioButton=new RadioButton( ChangeCompany.this );
                radioButton.setText( company_name[k] );
                ViewGroup.LayoutParams layoutParams=new RadioGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,100 );
                radioButton.setLayoutParams( layoutParams );
                radioButton.setTextSize( 20 );
                radioButton.setId( k );
                radioGroup.addView( radioButton );
                l = r + 1;
            }
        }
        final int[] choice = {0};
        radioGroup.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                choice[0] =checkedId;
            }
        } );
        submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i( "" + choice[0], "choice " );
                reply = con.CSQI( "608", cookie, String.valueOf( new_empno[choice[0]] ) );
                // 这里第三位就是你选中的对应公司的 empno
                if (reply.substring( 0, 3 ).equals( "200" )) { // 切换成功
                    // 新的cookie码
                    cookie = Integer.parseInt( reply.substring( reply.lastIndexOf( '#' ) + 1, reply.length() ) );
                    Log.i( ""+cookie, "cookie" );
                    Toast.makeText( ChangeCompany.this, "切换成功", Toast.LENGTH_SHORT ).show();
                    Intent intent=new Intent( ChangeCompany.this, AfterEntering.class );
                    intent.putExtra( "cookie",cookie );
                    setResult( RESULT_OK,intent );
                    finish();
                } else {
                    Toast.makeText( ChangeCompany.this, "切换失败", Toast.LENGTH_SHORT ).show();
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
            AlertDialog.Builder builder = new AlertDialog.Builder( ChangeCompany.this );
            builder.setTitle( "重复登陆" );
            builder.setMessage( "该账号在其他地方登陆，请重新登录。" );
            builder.setPositiveButton( "重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent( ChangeCompany.this, LoginActivity.class );
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
