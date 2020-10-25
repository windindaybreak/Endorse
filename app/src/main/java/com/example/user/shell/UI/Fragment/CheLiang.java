package com.example.user.shell.UI.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.user.shell.Connection;
import com.example.user.shell.UI.Activity.AfterEntering;
import com.example.user.shell.UI.Activity.GetDataFromMainActivity;
import com.example.user.shell.UI.Activity.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class CheLiang extends Fragment {

    public int cookie = 10000;
    public int budget_status = 0;
    private Connection con = new Connection();
    private String reply = "000";

    private EditText Et_from,Et_to,Et_reason,Et_account,Et_addtip;
    private Button change,submit;
    private ImageView back;

    private SharedPreferences.Editor editor;

    private View view;

    private GetDataFromMainActivity getDataFromMainActivity;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = Objects.requireNonNull ( getActivity ( ) ).getLayoutInflater ().inflate ( R.layout.fragment_che_liang,container,false );

        cookie = getDataFromMainActivity.GetCookie ();
        budget_status = getDataFromMainActivity.GetBudgetStatus ();

        SetWidget();
        SetSubmit ();
        SetChange ();
        Soadsave ();

        if(budget_status==1){
            loaddata( "232" );
            change.setText( "撤销" );
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach ( context );
        getDataFromMainActivity = (GetDataFromMainActivity) context;
    }

    private void loaddata(String code) {
        reply = con.CSQI( code, cookie, "000" );
        List<String> data = new ArrayList<>();
        int p = reply.length() - 1, q = p;
        String temp;
        while (reply.charAt( p ) == '#')
            p--;
        while (p >= 0) {
            q = p;
            while (p >= 0 && reply.charAt( p ) != '#')
                p--;
            temp = reply.substring( p + 1, q + 1 );
            data.add( temp );
            p--;
        }
        Et_from.setText( data.get( 4 ) );
        Et_to.setText( data.get( 3 ) );
        Et_reason.setText( data.get( 2 ) );
        Et_account.setText( data.get( 1 ) );
        Et_addtip.setText( data.get( 0 ) );
    }

    private void Soadsave() {
        final SharedPreferences sharedPreferences = getActivity ().getSharedPreferences( "Cheliang_save_data", MODE_PRIVATE );
        boolean load = sharedPreferences.getBoolean( "load", false );
        if (load) {
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext () );
            builder.setTitle( "检测到上次的保存的记录" );
            builder.setMessage( "是否载入?" );
            builder.setPositiveButton( "是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Et_from.setText( sharedPreferences.getString( "from", " " ) );
                    Et_to.setText( sharedPreferences.getString( "to", " " ) );
                    Et_reason.setText( sharedPreferences.getString( "reason", " " ) );
                    Et_account.setText( sharedPreferences.getString( "account", " " ) );
                    Et_addtip.setText( sharedPreferences.getString( "addtip", " " ) );
                }
            } ).

                    setNegativeButton( "否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor = getActivity ().getSharedPreferences( "Cheliang_save_data", MODE_PRIVATE ).edit();
                            editor.putBoolean( "load", false );
                            editor.apply();
                        }
                    } );
            builder.show();
        }
    }

    private void SetWidget() {
        back=view.findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        } );

        Et_from = view.findViewById( R.id.Et_cheliang_始发地 );
        Et_to = view.findViewById( R.id.Et_cheliang_目的地 );
        Et_reason = view.findViewById( R.id.Et_cheliang_理由 );
        Et_account = view.findViewById( R.id.Et_cheliang_报销费用 );
        Et_addtip = view.findViewById( R.id.Et_cheliang_补充说明 );

        LinearLayout btngroup_1 = view.findViewById ( R.id.Btngroup_cheliang );
        submit = view.findViewById( R.id.Btn_cheliang_提交 );
        change = view.findViewById( R.id.Btn_cheliang_change );

        editor = getActivity ().getSharedPreferences( "Cheliang_save_data", MODE_PRIVATE ).edit();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("CommitPrefEdits")
    private void SetSubmit() {
        submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean( "load",false );
                editor.apply();
                String from = Et_from.getText().toString();
                String to = Et_to.getText().toString();
                String reason = Et_reason.getText().toString();
                float account = (float) 0;
                if (Et_account.length() != 0)
                    try {
                        account = Float.parseFloat( Et_account.getText().toString() );
                    } catch (Exception e) {
                        Toast.makeText( getContext (), "请输入正确的金额", Toast.LENGTH_SHORT ).show();
                        return;
                    }
                String addtip = Et_addtip.getText().toString();
                String tmp = "#" + from + "#" + to + "#" + reason + "#" + account + "#" + addtip + "#";
                if (budget_status == 1) {
                    reply = "0";
                    reply = con.CSQI( "231", cookie, "000" );
                    if (Objects.equals( reply, "0" )) {
                        Toast.makeText( getContext (), "修改失败", Toast.LENGTH_SHORT ).show();
                        return;
                    }
                }
                reply = con.CSQI( "230", cookie, tmp );
                if (reply.substring( 0, 3 ).equals( "200" )) {
                    System.out.println( "提交成功" );
                    Toast.makeText( getContext (), "提交成功", Toast.LENGTH_SHORT ).show();
                    budget_status = 1;
                    getDataFromMainActivity.GetInstance ().finish ();
                } else {
                    System.out.println( "提交失败" );
                    Toast.makeText( getContext (), "提交失败", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

    }

    private void SetChange() {
        change.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                switch (budget_status) {
                    case 0://保存的功能
                        editor.putBoolean( "load", true );
                        editor.putString( "from", Et_from.getText().toString() );
                        editor.putString( "to", Et_to.getText().toString() );
                        editor.putString( "reason", Et_reason.getText().toString() );
                        editor.putString( "account", Et_account.getText().toString() );
                        editor.putString( "addtip", Et_addtip.getText().toString() );
                        editor.apply();
                        Toast.makeText( getContext (), "保存成功", Toast.LENGTH_SHORT ).show();
                        break;
                    case 1://撤销的功能
                        reply = "0";
                        reply = con.CSQI( "231", cookie, "000" );
                        if (Objects.equals( reply, "0" )) {
                            Toast.makeText( getContext (), "撤销失败", Toast.LENGTH_SHORT ).show();
                            return;
                        } else {
                            Toast.makeText( getContext (), "撤销成功", Toast.LENGTH_SHORT ).show();
                            budget_status = 0;
                            break;
                        }
                }
                Intent Ate = new Intent( getContext (), AfterEntering.class );
                Ate.putExtra( "BudgetStatus", budget_status );
                Ate.putExtra( "cookie", cookie );
                startActivity( Ate );
            }
        } );

    }
}
