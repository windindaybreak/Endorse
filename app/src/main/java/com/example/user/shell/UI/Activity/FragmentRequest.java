package com.example.user.shell.UI.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.user.shell.Connection;
import com.example.user.shell.UI.Fragment.CaiGou;
import com.example.user.shell.UI.Fragment.ChaiLv;
import com.example.user.shell.UI.Fragment.CheLiang;
import com.example.user.shell.UI.Fragment.JiaoTong;
import com.example.user.shell.UI.Fragment.PeiXun;
import com.example.user.shell.UI.Fragment.YunShu;
import com.example.user.shell.UI.Fragment.ZhaoDai;
import com.example.user.shell.UI.Fragment.ZiXun;
import com.example.user.shell.UI.Fragment.ZuLin;

import java.util.ArrayList;

public class FragmentRequest extends AppCompatActivity implements GetDataFromMainActivity{
    ViewPager viewPager;

    Fragment fragment;

    Connection con = new Connection ();

    private ArrayList<Fragment> mFragmentList = new ArrayList <Fragment>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_fragment_request );

        StrictMode. ThreadPolicy
                policy=new StrictMode. ThreadPolicy. Builder().permitAll().build();
        StrictMode. setThreadPolicy(policy);

        viewPager = findViewById(R.id.ViewPager);

        switch (getIntent ().getIntExtra ( "type",-1 )){
            case 0:
                fragment = new ChaiLv();
                break;
            case 1:
                fragment = new ZhaoDai ();
                break;
            case 2:
                fragment = new JiaoTong ();
                break;
            case 3:
                fragment = new CheLiang ();
                break;
            case 4:
                fragment = new PeiXun ();
                break;
            case 5:
                fragment = new ZuLin ();
                break;
            case 6:
                fragment = new ZiXun ();
                break;
            case 7:
                fragment = new YunShu ();
                break;
            case 8:
                fragment = new CaiGou ();
                break;
            default:
                fragment = new Fragment ();
        }
        mFragmentList.add(fragment);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter ( getSupportFragmentManager ( )) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragmentList != null ? mFragmentList.get ( position ) : null;
            }

            @Override
            public int getCount() {
                return mFragmentList != null ? mFragmentList.size ( ) : 0;
            }
        };
        viewPager.setAdapter( fragmentPagerAdapter );
        viewPager.setCurrentItem ( 1 );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged( hasFocus );
        String reply = "000";
        reply = con.CSQI( "501", getIntent ().getIntExtra ( "cookie", 10000 ), "000" );
        if (reply.substring( 0, 3 ).equals( "400" )) {
            AlertDialog.Builder builder = new AlertDialog.Builder ( this );
            builder.setTitle ( "重复登陆" );
            builder.setMessage ( "该账号在其他地方登陆，请重新登录。" );
            builder.setPositiveButton ( "重新登陆", new DialogInterface.OnClickListener ( ) {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent ( FragmentRequest.this, LoginActivity.class );
                    startActivity ( intent );
                }
            } );
            builder.setNegativeButton ( "退出", new DialogInterface.OnClickListener ( ) {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.os.Process.killProcess ( android.os.Process.myPid ( ) );
                }
            } );
            builder.show ( );
        }
    }

    @Override
    public int GetCookie() {
        return getIntent ().getIntExtra ( "cookie", 10000 );
    }

    @Override
    public int GetBudgetStatus() {
        return getIntent ().getIntExtra ( "BudgetStatus", 0 );
    }

    @Override
    public FragmentRequest GetInstance() {
        return this;
    }
}
