package com.example.user.shell.UI.Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import com.example.user.shell.FtpUtils;
import com.example.user.shell.UI.Activity.AfterEntering;
import com.example.user.shell.UI.Activity.GetDataFromMainActivity;
import com.example.user.shell.UI.Activity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.user.shell.UI.Fragment.ChaiLv.imagePath;

public class YunShu extends Fragment {

    private int cookie = 10000;
    private int budget_status = 0;// 0  未申请 1 待审核   2已审核 3 过期
    private static final int CHOOSE_PHOTO = 7;

    private String reply = "000";
    private Connection con = new Connection();

    private EditText Et_item, Et_num, Et_from, Et_go, Et_type, Et_company, Et_account, Et_addtip;
    private Button Btn_left, Btn_submit, Btn_quick, Btn_addaccount, Btn_end;
    private LinearLayout btngroup1, btngroup2;
    private TextView datefrom, dateto;
    private ImageView back;

    private SharedPreferences.Editor editor;

    private View view;

    private GetDataFromMainActivity getDataFromMainActivity;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = Objects.requireNonNull ( getActivity ( ) ).getLayoutInflater ().inflate ( R.layout.fragment_yun_shu,container,false );

        cookie = getDataFromMainActivity.GetCookie ();
        budget_status = getDataFromMainActivity.GetBudgetStatus ();

        setWidget();
        setTime_choose();
        setLeft();
        setSubmit();
        setAddaccount();
        setEnd();
        setQuickReimbursement ();
        loadsave();
        switch (budget_status){
            case 1:
                Btn_left.setText( "撤销" );
                loaddata( "262" );
                break;
            case 2:
                loaddata( "263" );
                btngroup1.setVisibility( View.GONE );
                btngroup2.setVisibility( View.VISIBLE );
                Et_item.setEnabled( false );
                Et_num.setEnabled( false );
                Et_from.setEnabled( false );
                Et_go.setEnabled( false );
                Et_type.setEnabled( false );
                datefrom.setEnabled( false );
                dateto.setEnabled( false );
                Et_company.setEnabled( false );
                Et_account.setEnabled( false );
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

    public void setTime_choose() {
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
                        datefrom.setText( year + "" + (++month) + "" + (dayOfMonth < 10 ? ("0" + dayOfMonth) : dayOfMonth) );
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog( getContext (),
                        DatePickerDialog.THEME_HOLO_LIGHT, listener, year, month, day );
                dialog.show();
            }
        } );
        dateto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateto.setText( year + "" + (++month) + "" +
                                (dayOfMonth < 10 ? ("0" + dayOfMonth) : dayOfMonth) );
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog( getContext (), DatePickerDialog.THEME_HOLO_LIGHT, listener, year, month, day );
                dialog.show();
            }
        } );
    }

    private void loadsave(){
        final SharedPreferences sharedPreferences = getActivity ().getSharedPreferences( "Yunshu_save_data", MODE_PRIVATE );
        boolean load = sharedPreferences.getBoolean( "load", false );
        if (load) {
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext () );
            builder.setTitle( "检测到上次的保存的记录" );
            builder.setMessage( "是否载入?" );
            builder.setPositiveButton( "是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Et_item.setText( sharedPreferences.getString( "item"," " ) );
                    Et_num.setText( sharedPreferences.getString( "num"," " ) );
                    Et_from.setText( sharedPreferences.getString( "from"," " ) );
                    Et_go.setText( sharedPreferences.getString( "go"," " ) );
                    Et_type.setText( sharedPreferences.getString( "type"," " ) );
                    datefrom.setText( sharedPreferences.getString( "timefrom", " " ) );
                    dateto.setText( sharedPreferences.getString( "timeto", " " ) );
                    Et_company.setText( sharedPreferences.getString( "company", " " ) );
                    Et_account.setText( sharedPreferences.getString( "account", " " ) );
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

    private void loaddata(String code){
        reply=con.CSQI(code, cookie, "000");
        List<String> data = new ArrayList<>();
        int p = reply.length() - 1, q = p;
        String temp;
        while (reply.charAt(p) == '#')
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
        Et_item.setText( data.get(0) );
        Et_num.setText( data.get(1) );
        Et_from.setText( data.get(2) );
        Et_go.setText( data.get(3) );
        datefrom.setText( data.get( 4 ).substring( 0, 8 ) );
        dateto.setText( data.get( 4 ).substring( 9 ) );
        Et_type.setText( data.get(5) );
        Et_company.setText( data.get(6) );
        Et_account.setText( data.get(8) );
        Et_addtip.setText( data.get(7) );
    }

    public String analyseReply(String reply) {
        if (reply.charAt( 0 ) == '0') {
            return "400#unknown error";
        } else if (reply.charAt( 0 ) == '1') {
            return "200#";
        } else {
            return reply;
        }
    }

    private void setWidget() {
        back=view.findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        } );

        datefrom = view.findViewById( R.id.Datefrom );
        dateto = view.findViewById( R.id.Dateto );

        Et_item = view.findViewById( R.id.Et运输物品 );
        Et_num = view.findViewById( R.id.Et物品数量 );
        Et_from = view.findViewById( R.id.Etyunshu始发地 );
        Et_go = view.findViewById( R.id.Etyunshu目的地 );
        Et_company = view.findViewById( R.id.Et运输公司 );
        Et_type = view.findViewById( R.id.Et运输方式 );
        Et_account = view.findViewById( R.id.Et运输预算 );
        Et_addtip = view.findViewById( R.id.Et运输备注 );

        Btn_left = view.findViewById( R.id.Btn运输撤销 );
        Btn_submit = view.findViewById( R.id.Btn运输提交 );
        Btn_quick = view.findViewById( R.id.Btnyunshu快速报销 );
        Btn_addaccount = view.findViewById( R.id.Btnyunshu追加预算 );
        Btn_end = view.findViewById( R.id.Btnyunshu结束行程 );

        btngroup1 = view.findViewById( R.id.Btngroup1_yunshu );
        btngroup2 = view.findViewById( R.id.Btngroup2_yunshu );
        editor = getActivity ().getSharedPreferences( "Yunshu_save_data", MODE_PRIVATE ).edit();
    }

    private void setLeft() {
        Btn_left.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (budget_status) {
                    case 0:
                        editor.putBoolean( "load", true );
                        editor.putString( "item", Et_item.getText().toString() );
                        editor.putString( "num", Et_num.getText().toString() );
                        editor.putString( "from", Et_from.getText().toString() );
                        editor.putString( "go", Et_go.getText().toString() );
                        editor.putString( "type", Et_type.getText().toString() );
                        editor.putString( "timefrom", datefrom.getText().toString() );
                        editor.putString( "timeto", dateto.getText().toString() );
                        editor.putString( "company", Et_company.getText().toString() );
                        editor.putString( "account", Et_account.getText().toString() );
                        editor.putString( "addtip", Et_addtip.getText().toString() );
                        editor.apply();
                        Toast.makeText( getContext (), "保存成功", Toast.LENGTH_SHORT ).show();
                        break;
                    case 1:
                        reply = con.CSQI( "261", cookie, "000" );
                        if (analyseReply( reply ).substring( 0, 3 ).equals( "200" )) {
                            Toast.makeText( getContext (), "撤销成功", Toast.LENGTH_SHORT ).show();
                            break;
                        } else {
                            Toast.makeText( getContext (), "撤销失败", Toast.LENGTH_SHORT ).show();
                            return;
                        }
                }
                Intent Ate = new Intent( getContext (), AfterEntering.class );
                Ate.putExtra( "BudgetStatus", budget_status );
                Ate.putExtra( "cookie", cookie );
                startActivity( Ate );
            }
        } );
    }

    private void setSubmit() {
        Btn_submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean( "load", false );
                editor.apply();
                String item = Et_item.getText().toString();
                String num=Et_num.getText().toString();
                String from=Et_from.getText().toString();
                String go=Et_go.getText().toString();
                String type=Et_type.getText().toString();
                String datef=datefrom.getText().toString();
                String datet=dateto.getText().toString();
                if(Integer.parseInt( datef )>Integer.parseInt( datet )){
                    Toast.makeText( getContext (),"请选择正确的时间区间",
                            Toast.LENGTH_SHORT ).show();
                    return;
                }
                String time = datef + "-" +datet ;
                String company = Et_company.getText().toString();
                String account;
                if (Et_account.length() == 0) {
                    Toast.makeText( getContext (), "预算不能为空", Toast.LENGTH_SHORT ).show();
                    return;
                }
                account = Et_account.getText().toString();
                String addtip = Et_addtip.getText().toString();
                String ask="#";
                //运输物品
                ask+=item+"#";
                //物品数量
                ask+=num+"#";
                //始发地
                ask+=from+"#";
                //目的地
                ask+=go+"#";
                //运输时间
                ask+=time+"#";
                //运输方式
                ask+=type+"#";
                //运输公司
                ask+=company+"#";
                //运输费预算
                ask+=account+"#";
                //备注
                ask+=addtip+"#";
                if (budget_status == 1) {
                    reply = con.CSQI( "261", cookie, "000" );
                    if (!analyseReply( reply ).substring( 0, 3 ).equals( "200" )) {
                        Toast.makeText( getContext (), "修改失败", Toast.LENGTH_SHORT ).show();
                        return;
                    }
                }
                reply=con.CSQI("260", cookie, ask);
                if (analyseReply( reply ).substring( 0, 3 ).equals( "200" )) {
                    Toast.makeText( getContext (), "提交成功", Toast.LENGTH_SHORT ).show();
                    budget_status = 1;
                    Intent Ate = new Intent( getContext (), AfterEntering.class );
                    Ate.putExtra( "BudgetStatus", budget_status );
                    Ate.putExtra( "cookie", cookie );
                    startActivity( Ate );
                } else {
                    System.out.println( "提交失败" );
                    Toast.makeText( getContext (), "提交失败", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

    private void setAddaccount(){
        Btn_addaccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout dialoglayout;
                final Dialog dialog = new Dialog( getContext () );
                dialog.setContentView( R.layout.dialog );
                dialoglayout = dialog.findViewById( R.id.差旅追加预算 );
                dialoglayout.setVisibility( View.GONE );
                dialoglayout = dialog.findViewById( R.id.layout_zixun_追加预算 );
                dialoglayout.setVisibility( View.VISIBLE );
                final EditText addaccount = dialog.findViewById( R.id.Et_zixun_追加预算 );
                final Button submit = dialog.findViewById( R.id.Btn_zixun_submit_追加预算 );
                Button cancel = dialog.findViewById( R.id.Btn_zixun_cancel_追加预算 );
                addaccount.setHint( "运输费预算追加" );
                dialog.show();
                submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (addaccount.length() == 0) {
                            Toast.makeText( getContext (), "金额不能为空", Toast.LENGTH_SHORT ).show();
                            return;
                        }
                        String account = addaccount.getText().toString();
                        String tmp = "#" + account + "#";//budget_add:追加金额
                        reply = con.CSQI( "265", cookie, tmp );
                        if (analyseReply( reply ).substring( 0, 3 ).equals( "200" )) {
                            Toast.makeText( getContext (), "提交成功", Toast.LENGTH_SHORT ).show();
                        } else Toast.makeText( getContext (), "提交失败", Toast.LENGTH_SHORT ).show();
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

    private void setEnd(){
        Btn_end.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply = con.CSQI( "264", cookie, "000" );
                if (analyseReply( reply ).substring( 0, 3 ).equals( "200" )) {
                    Toast.makeText( getContext (), "已成功结束本次申请！", Toast.LENGTH_SHORT ).show();
                    getDataFromMainActivity.GetInstance ().finish ();
                } else {
                    Toast.makeText( getContext (), "未能结束本次申请！", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

    private void setQuickReimbursement() {
        Btn_quick.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection con = new Connection();
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
                    Intent intent = new Intent( "android.intent.action.GET_CONTENT" );
                    intent.setType( "image/*" );
                    startActivityForResult( intent, CHOOSE_PHOTO );
                } else {
                    Toast.makeText( getContext (), "未开启权限", Toast.LENGTH_LONG ).show();
                }
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4以上
                        imagePath = handleImageOnKitKat( data );
                    } else {
                        //4.4以下
                        imagePath = handleImageBeforeKitKat( data );
                    }
                }
                break;
            default:
                break;
        }
        if (imagePath != null) {
            System.out.println( "imagePath:" + imagePath );
            FtpUtils ftpUtils = new FtpUtils();
            String kzm = imagePath.substring( imagePath.lastIndexOf( "." ) );
            String new_name = con.CSQI( "400", cookie, "000" ) + kzm;
            ftpUtils.uploadFile( "/home/test/images", new_name, imagePath );  //上传图片
            //con.CSQI( "407", cookie, new_name ); // 差旅快速报销
            if(con.CSQI("407", cookie, new_name).equals("200#")) // 差旅快速报销
            {
                Toast.makeText(getContext (),"快速报销成功",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext (),"快速报销失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @TargetApi(19)
    private String handleImageOnKitKat(Intent data) {
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri( getContext (), uri )) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId( uri );
            if ("com.android.providers.media.documents".equals( uri.getAuthority() )) {
                String id = docId.split( ":" )[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection );
            } else if ("com.android.providers.downloads.documents".equals( uri.getAuthority() )) {
                Uri contentUri = ContentUris.withAppendedId( Uri.
                        parse( "content://downloads/public_downloads" ), Long.valueOf( docId ) );
                imagePath = getImagePath( contentUri, null );
            }
        } else if ("content".equalsIgnoreCase( uri.getScheme() )) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath( uri, null );
        } else if ("file".equalsIgnoreCase( uri.getScheme() )) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath( uri, null );
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity ().getContentResolver().query( uri, null, selection,
                null, null );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA ) );
            }
            cursor.close();
        }
        return path;
    }
}
