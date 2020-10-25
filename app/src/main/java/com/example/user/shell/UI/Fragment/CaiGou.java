package com.example.user.shell.UI.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.user.shell.Connection;
import com.example.user.shell.UI.Activity.R;
import com.example.user.shell.UI.Activity.AfterEntering;
import com.example.user.shell.UI.Activity.GetDataFromMainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class CaiGou extends Fragment {

    private int budget_status = 0;// 0  未申请 1 待审核   2已审核 3 过期
    private int cookie = 10000;
    private int CHOOSE_PHOTO = 8;

    private String reply = "000";
    private Connection con = new Connection ( );

    private EditText Et_item, Et_reason, Et_company, Et_num, Et_danjia, Et_yusuan, Et_addtip;
    private Button Btn_submit, Btn_left, Btn_quick, Btn_addacount, Btn_end;
    private LinearLayout btngroup1, btngroup2;
    private TextView datefrom, dateto;
    private ImageView back;

    private SharedPreferences.Editor editor;
    private GetDataFromMainActivity getDataFromMainActivity;

    private View view;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = Objects.requireNonNull ( getActivity ( ) ).getLayoutInflater ( ).inflate ( R.layout.fragment_cai_gou, container, false );

        cookie = getDataFromMainActivity.GetCookie ( );
        budget_status = getDataFromMainActivity.GetBudgetStatus ();

        SetWidget();
        SetTimeChoose();
        SetLeft();
        SetSubmit ();
        SetAddaccount ();
        SetQuickReimbursement ();
        Loadsave ();
        SetEnd ();

        switch (budget_status) {
            case 1:
                Btn_left.setText( "撤销" );
                LoadData ( "270" );
                break;
            case 2:
                LoadData ( "271" );
                btngroup1.setVisibility( View.GONE );
                btngroup2.setVisibility( View.VISIBLE );
                Et_item.setEnabled( false );
                Et_reason.setEnabled( false );
                datefrom.setEnabled( false );
                dateto.setEnabled( false );
                Et_company.setEnabled( false );
                Et_num.setEnabled( false );
                Et_danjia.setEnabled( false );
                Et_yusuan.setEnabled( false );
                Et_addtip.setEnabled( false );
                break;
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach ( context );
        getDataFromMainActivity = (GetDataFromMainActivity) context;
    }

    private void LoadData(String code) {
        reply = con.CSQI( code, cookie, "000" );
        List<String> data = new ArrayList<> ();
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
        Collections.reverse( data );
        Et_reason.setText( data.get( 0 ) );
        datefrom.setText( data.get( 1 ).substring( 0, 8 ) );
        dateto.setText( data.get( 1 ).substring( 9 ) );
        //Et_time.setText( data.get( 1 ) );
        Et_company.setText( data.get( 2 ) );
        Et_item.setText( data.get( 3 ) );
        Et_num.setText( data.get( 4 ) );
        Et_danjia.setText( data.get( 5 ) );
        Et_addtip.setText( data.get( 6 ) );
        Et_yusuan.setText( data.get( 7 ) );
    }

    private void SetWidget() {
        datefrom = view.findViewById( R.id.Datefrom );
        dateto = view.findViewById( R.id.Dateto );

        back=view.findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        } );

        Et_item = view.findViewById( R.id.Et采购物品 );
        Et_reason = view.findViewById( R.id.Et采购理由 );
        //Et_time = findViewById( R.id.Et采购时间 );
        Et_num = view.findViewById( R.id.Et采购数量 );
        Et_company = view.findViewById( R.id.Et采购公司 );
        Et_danjia = view.findViewById( R.id.Et采购单价 );
        Et_yusuan = view.findViewById( R.id.Et采购预算 );
        Et_addtip = view.findViewById( R.id.Et采购备注 );

        Btn_submit = view.findViewById( R.id.Btn采购提交 );
        Btn_left = view.findViewById( R.id.Btn采购撤销 );
        Btn_quick = view.findViewById( R.id.Btn采购快速报销 );
        Btn_addacount = view.findViewById( R.id.Btn采购追加预算 );
        Btn_end = view.findViewById( R.id.Btn采购结束行程 );

        btngroup1 = view.findViewById( R.id.Btngroup1_caigou );
        btngroup2 = view.findViewById( R.id.Btngroup2_caigou );

        editor = getActivity ().getSharedPreferences( "Caigou_save_data", MODE_PRIVATE ).edit();
    }

    private void SetTimeChoose() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get( Calendar.YEAR );
        final int month = calendar.get( Calendar.MONTH );
        final int day = calendar.get( Calendar.DAY_OF_MONTH );
        String time = year + "" + (month + 1) + "" + (day < 10 ? ("0" + day) : day);
        datefrom.setText( time );
        dateto.setText( time );
        datefrom.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        datefrom.setText( year + "" + (++month) + "" +(dayOfMonth < 10 ? ("0" + dayOfMonth) : dayOfMonth) );
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog( getContext (), DatePickerDialog.THEME_HOLO_LIGHT, listener, year, month, day );
                dialog.show();
            }
        } );
        dateto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateto.setText( year + "" + (++month) + "" + (dayOfMonth < 10 ? ("0" + dayOfMonth) : dayOfMonth) );
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog( getContext (), DatePickerDialog.THEME_HOLO_LIGHT, listener, year, month, day );
                dialog.show();
            }
        } );
    }

    private String AnalyseReply(String reply) {
        if (reply.charAt( 0 ) == '0') {
            return "400#unknown error";
        } else if (reply.charAt( 0 ) == '1') {
            return "200#";
        } else {
            return reply;
        }
    }

    private void SetLeft() {
        Btn_left.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (budget_status) {
                    case 0:
                        editor.putBoolean( "load", true );
                        editor.putString( "item", Et_item.getText().toString() );
                        editor.putString( "reason", Et_reason.getText().toString() );
                        editor.putString( "timefrom", datefrom.getText().toString() );
                        editor.putString( "timeto", dateto.getText().toString() );
                        editor.putString( "company", Et_company.getText().toString() );
                        editor.putString( "num", Et_num.getText().toString() );
                        editor.putString( "danjia", Et_danjia.getText().toString() );
                        editor.putString( "yusuan", Et_yusuan.getText().toString() );
                        editor.putString( "addtip", Et_addtip.getText().toString() );
                        editor.apply();
                        Toast.makeText( getContext (), "保存成功", Toast.LENGTH_SHORT ).show();
                        break;
                    case 1:
                        reply = con.CSQI( "269", cookie, "000" );
                        if (AnalyseReply ( reply ).substring( 0, 3 ).equals( "200" )) {
                            Toast.makeText( getContext (), "撤销成功", Toast.LENGTH_SHORT ).show();
                            break;
                        } else {
                            Toast.makeText( getContext (), "撤销失败", Toast.LENGTH_SHORT ).show();
                        }
                }
            }
        } );
    }

    private void SetSubmit() {
        Btn_submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean( "load", false );
                editor.apply();
                String item = Et_item.getText().toString();
                String reason = Et_reason.getText().toString();
                String datef=datefrom.getText().toString();
                String datet=dateto.getText().toString();
                if(Integer.parseInt( datef )>Integer.parseInt( datet )){
                    Toast.makeText( getContext (),"请选择正确的时间区间", Toast.LENGTH_SHORT ).show();
                    return;
                }
                String time = datef + "-" +datet ;
                String company = Et_company.getText().toString();
                String num = Et_num.getText().toString();
                String danjia = Et_danjia.getText().toString();
                String yusuan;
                if (Et_yusuan.length() == 0) {
                    Toast.makeText( getContext (), "预算不能为空", Toast.LENGTH_SHORT ).show();
                    return;
                }
                yusuan = Et_yusuan.getText().toString();
                String addtip = Et_addtip.getText().toString();
                String ask = "#";
                ask += reason + "#";//采购原由
                ask += time + "#";//采购日期
                ask += company + "#";//采购公司
                ask += item + "#";//采购物品
                ask += num + "#";//物品数量
                ask += danjia + "#";//物品单价
                ask += yusuan + "#";//采购总预算
                ask += addtip + "#";//备注
                if (budget_status == 1) {
                    reply = con.CSQI( "269", cookie, "000" );
                    if (!AnalyseReply( reply ).substring( 0, 3 ).equals( "200" )) {
                        Toast.makeText( getContext (), "修改失败", Toast.LENGTH_SHORT ).show();
                        return;
                    }
                }
                reply = con.CSQI( "268", cookie, ask );
                if (AnalyseReply( reply ).substring( 0, 3 ).equals( "200" )) {
                    Toast.makeText( getContext (), "提交成功", Toast.LENGTH_SHORT ).show();
                    editor.putBoolean( "load",false );
                    budget_status = 1;
                }
                else {
                    System.out.println( "提交失败" );
                    Toast.makeText( getContext (), "提交失败", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

    private void SetAddaccount() {
        Btn_addacount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout dialoglayout;
                final Dialog dialog = new Dialog( getContext () );
                dialog.setContentView( R.layout.dialog );
                dialoglayout = dialog.findViewById( R.id.差旅追加预算 );
                dialoglayout.setVisibility( View.GONE );
                dialoglayout = dialog.findViewById( R.id.layout_caigou_追加预算 );
                dialoglayout.setVisibility( View.VISIBLE );
                dialog.show();
                final EditText addaccount = dialog.findViewById( R.id.Et_caigou_追加预算 );
                final Button submit = dialog.findViewById( R.id.Btn_caigou_submit_追加预算 );
                Button cancel = dialog.findViewById( R.id.Btn_caigou_cancel_追加预算 );
                submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (addaccount.length() == 0) {
                            Toast.makeText( getContext (), "金额不能为空", Toast.LENGTH_SHORT ).show();
                            return;
                        }
                        String account = addaccount.getText().toString();
                        String tmp = "#" + account + "#";//budget_add:追加金额
                        reply = con.CSQI( "273", cookie, tmp );
                        if (AnalyseReply( reply ).substring( 0, 3 ).equals( "200" )) {
                            Toast.makeText( getContext (), "提交成功", Toast.LENGTH_SHORT ).show();
                        }
                        else {
                            Toast.makeText( getContext (), "提交失败", Toast.LENGTH_SHORT ).show();
                        }
                        dialog.dismiss();
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

    private void SetQuickReimbursement() {
        Btn_quick.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission( getContext (),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions( getActivity (), new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );
                } else {
                    Intent intent = new Intent( "android.intent.action.GET_CONTENT" );
                    intent.setType( "image/*" );
                    startActivityForResult( intent, CHOOSE_PHOTO );
                }
            }
        } );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent ( "android.intent.action.GET_CONTENT" );
                    intent.setType( "image/*" );
                    startActivityForResult( intent, CHOOSE_PHOTO );
                } else {
                    Toast.makeText( getContext (), "未开启权限", Toast.LENGTH_LONG ).show();
                }
                break;
            default:
        }
    }

    private void Loadsave() {
        final SharedPreferences sharedPreferences = getActivity ().getSharedPreferences( "Caigou_save_data", MODE_PRIVATE );
        boolean load = sharedPreferences.getBoolean( "load", false );
        if (load) {
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext () );
            builder.setTitle( "检测到上次的保存的记录" );
            builder.setMessage( "是否载入?" );
            builder.setPositiveButton( "是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Et_item.setText( sharedPreferences.getString( "item", " " ) );
                    Et_reason.setText( sharedPreferences.getString( "reason", " " ) );
                    datefrom.setText( sharedPreferences.getString( "timefrom", " " ) );
                    dateto.setText( sharedPreferences.getString( "timeto", " " ) );
                    //Et_time.setText( sharedPreferences.getString( "time", " " ) );
                    Et_company.setText( sharedPreferences.getString( "company", " " ) );
                    Et_num.setText( sharedPreferences.getString( "num", " " ) );
                    Et_danjia.setText( sharedPreferences.getString( "danjia", " " ) );
                    Et_yusuan.setText( sharedPreferences.getString( "yusuan", " " ) );
                    Et_addtip.setText( sharedPreferences.getString( "addtip", " " ) );
                }
            } ).

                    setNegativeButton( "否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putBoolean( "load", false );
                            editor.apply();
                        }
                    } );
            builder.show();
        }
    }

    private void SetEnd() {
        Btn_end.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply = con.CSQI( "272", cookie, "000" );
                if (AnalyseReply( reply ).substring( 0, 3 ).equals( "200" )) {
                    Toast.makeText( getContext (), "已成功结束本次申请！", Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent( getContext (), AfterEntering.class );
                    budget_status = 0;
                    intent.putExtra( "BudgetStatus", budget_status );
                    intent.putExtra( "cookie", cookie );
                    startActivity( intent );
                } else {
                    Toast.makeText( getContext (), "未能结束本次申请！", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

}