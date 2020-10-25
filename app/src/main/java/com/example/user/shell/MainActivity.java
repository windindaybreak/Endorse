package com.example.user.shell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.UI.Activity.AfterEntering;
import com.example.user.shell.UI.Activity.R;

/*登录界面*/
public class MainActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button BtnLogin = findViewById( R.id.BtnLogin);  //登陆按钮
        final ImageView IVeye=findViewById ( R.id.eye );
        final EditText EtNumber = findViewById(R.id.EtNumber);//账号输入栏
        final EditText EtPassword = findViewById(R.id.EtPassword);//密码输入栏

        BtnLogin.setOnClickListener(new View.OnClickListener() {//登录按钮的点击事件

            String password = new String("000");
            String account = new String("000");
            //密码和账号的初始化

            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog ( MainActivity.this );
                dialog.setContentView ( R.layout.loading );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable ( Color.TRANSPARENT));
                dialog.show ();
                if (EtNumber.getText() == null || EtPassword.getText() == null) {
                    Toast.makeText(MainActivity.this, "请将账号密码填写完整", Toast.LENGTH_SHORT).show();
                } else {
                    //密码和账号都已经输入，判断是否匹配
                    account = EtNumber.getText().toString();
                    password = EtPassword.getText().toString();
                    String reply="000";

                    Connection con = new Connection();
                    
                    reply=con.CSQI("200",10000,account+"#"+password);

                    int status = 0;
                    int cookie = 0; // 连接状态 cookie 信息

                    for (int i = 0; i < 3; i++) {status = status * 10 + reply.charAt(i) - '0';}
                    if (status == 200) {       //如果正确
                        for(int i=4;i<reply.length();i++) {cookie=cookie*10+reply.charAt(i)-'0';}
                        Intent i = new Intent(MainActivity.this, AfterEntering.class);
                        i.putExtra ( "Account",account );
                        i.putExtra ( "Password",password );
                        i.putExtra("cookie",cookie);
                        startActivity(i);
                        dialog.dismiss ();
                        finish();
                    } else if (status == 404) {//账号正确密码错误
                        dialog.dismiss ();
                        Toast.makeText(MainActivity.this, "账号未注册", Toast.LENGTH_SHORT).show();
                    } else {//账号错误
                        dialog.dismiss ();
                        Toast.makeText(MainActivity.this, "密码或账号错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        IVeye.setOnTouchListener ( new View.OnTouchListener ( ) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) EtPassword.setTransformationMethod( HideReturnsTransformationMethod.getInstance());
                else EtPassword.setTransformationMethod( PasswordTransformationMethod.getInstance());
                Selection.setSelection(EtPassword.getText (), EtPassword.length());
                return false;
            }
        } );

    }
}
