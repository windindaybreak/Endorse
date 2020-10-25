package com.example.user.shell.UI.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.Connection;

public class RemainAcount extends AppCompatActivity {

    int cookie;
    Connection con = new Connection();
    String reply = "000";
    TextView yue;
    Button tixian, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_remain_acount );
        cookie = getIntent().getIntExtra( "cookie", 10000 );
        yue = findViewById( R.id.yue );
        tixian = findViewById( R.id.tixian体现 );
        back = findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
        setTixian();
        setYue();
    }

    private void setYue() {
        reply = con.CSQI( "600", cookie, "000" );
        if (reply.substring( 0, 3 ).equals( "200" )) {
            if (reply.lastIndexOf( "." ) + 3 < reply.length())
                yue.setText( reply.substring( 4, reply.lastIndexOf( "." ) + 3 ) );
            else yue.setText( reply.substring( 4 ) );
        } else
            yue.setText( "获取信息错误" );
    }

    private void setTixian() {
        tixian.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog( RemainAcount.this );
                dialog.setContentView( R.layout.layout_tixian );
                final EditText jine = dialog.findViewById( R.id.tixian );
                Button submit = dialog.findViewById( R.id.submit );
                Button cancel = dialog.findViewById( R.id.cancel );
                jine.addTextChangedListener( new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            float num = Float.parseFloat( jine.getText().toString() );
                            float range = Float.parseFloat( yue.getText().toString() );
                            if (num > range) {
                                Toast.makeText( RemainAcount.this, "提现金额不能超过余额",
                                        Toast.LENGTH_SHORT ).show();
                                jine.setText( String.valueOf( range ) );
                            }
                        } catch (Exception ignored) {

                        }


                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                } );
                dialog.show();
                submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (jine.length() == 0) {
                            Toast.makeText( RemainAcount.this, "金额不能为空",
                                    Toast.LENGTH_SHORT ).show();
                        } else {
                            String num = jine.getText().toString();
                            reply = con.CSQI( "700", cookie, num );
                            dialog.dismiss();
                            Toast.makeText( RemainAcount.this, "提现成功",
                                    Toast.LENGTH_SHORT ).show();
                            finish();
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
            AlertDialog.Builder builder = new AlertDialog.Builder( RemainAcount.this );
            builder.setTitle( "重复登陆" );
            builder.setMessage( "该账号在其他地方登陆，请重新登录。" );
            builder.setPositiveButton( "重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent( RemainAcount.this, LoginActivity.class );
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
