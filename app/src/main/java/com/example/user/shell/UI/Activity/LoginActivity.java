package com.example.user.shell.UI.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.Connection;

/*登录界面*/
public class LoginActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button BtnLogin = findViewById( R.id.BtnLogin);  //登陆按钮
        final ImageView IVeye = findViewById(R.id.eye);
        final EditText EtNumber = findViewById(R.id.EtNumber);//账号输入栏
        final EditText EtPassword = findViewById(R.id.EtPassword);//密码输入栏

        BtnLogin.setOnClickListener(new View.OnClickListener() {//登录按钮的点击事件

            String password = new String("000");
            String account = new String("000");
            //密码和账号的初始化

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(EtNumber.getText()) && TextUtils.isEmpty(EtPassword.getText())) {
                    Toast.makeText(LoginActivity.this, "账号密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    account = EtNumber.getText().toString();
                    password = EtPassword.getText().toString();
                    String reply = "000";
                    Connection con = new Connection();
                    reply = con.CSQI("200", 10000, account + "#" + password);
                    int status = 0;
                    int cookie = 0; // 连接状态 cookie 信息
                    for (int i = 0; i < 3; i++) {
                        status = status * 10 + reply.charAt(i) - '0';
                    }
                    if (status == 200) {       //如果正确
                        for (int i = 4; i < reply.length(); i++) {
                            cookie = cookie * 10 + reply.charAt(i) - '0';
                        }
                        Intent i = new Intent(LoginActivity.this, AfterEntering.class);
                        i.putExtra("cookie", cookie);
                        SaveUserLoginState(account, cookie);//本地储存用户登陆状态
                        startActivity(i);
                        finish();
                    } else if (status == 404) {//账号正确密码错误
                        Toast.makeText(LoginActivity.this, "密码或账号错误", Toast.LENGTH_SHORT).show();
                    } else {//账号错误
                        Toast.makeText(LoginActivity.this, "账号未注册", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        IVeye.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    EtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else EtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                Selection.setSelection(EtPassword.getText(), EtPassword.length());
                return false;
            }
        });

    }

    private void SaveUserLoginState(String account, int cookie) {
        SharedPreferences sPreferences = getSharedPreferences("UserLoginState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString("account", account);
        editor.putInt("cookie", cookie);
        editor.apply();
    }
}
