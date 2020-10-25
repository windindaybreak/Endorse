package com.example.user.shell.UI.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import androidx.appcompat.app.AlertDialog;
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
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.user.shell.UI.Fragment.ChaiLv.imagePath;

public class PeiXun extends Fragment {

    public int cookie;
    public int budget_status;
    public static final int CHOOSE_PHOTO = 4;

    Connection con=new Connection();
    String reply = "000";

    Button Btn_quick,Btn_addaccount,Btn_end,Btn_change,Btn_submit;
    EditText Et_peixunjigou,Et_shoukefangshi,Et_shoukedidian,Et_peixunyusuan,Et_peixunneirong,Et_kaohexingshi,Et_addtip;
    LinearLayout btngroup1,btngroup2;
    private ImageView back;

    SharedPreferences.Editor editor;
    TextView datefrom, dateto;

    private View view;

    private GetDataFromMainActivity getDataFromMainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity ().getLayoutInflater ().inflate ( R.layout.fragment_pei_xun,container,false );

        cookie = getDataFromMainActivity.GetCookie ();
        budget_status = getDataFromMainActivity.GetBudgetStatus ();

        SetWidget ();
        setTime_choose();
        SetChange ();
        SetSubmit ();
        SetAddAccount ();
        SetEnd ();
        LoadSave ();
        setQuickReimbursement ();
        switch (budget_status) {
            case 1:
                LoadData1 ( "238" );
                Btn_change.setText( "撤销" );
                break;
            case 2:
                LoadData ( "240" );
                btngroup1.setVisibility( View.GONE );
                btngroup2.setVisibility( View.VISIBLE );
                Et_peixunjigou.setEnabled( false );
                datefrom.setEnabled( false );
                dateto.setEnabled( false );
                Et_shoukefangshi.setEnabled( false );
                Et_shoukedidian.setEnabled( false );
                Et_peixunyusuan.setEnabled( false );
                Et_peixunneirong.setEnabled( false );
                Et_kaohexingshi.setEnabled( false );
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

    private void setTime_choose() {
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
                DatePickerDialog dialog = new DatePickerDialog( getContext (),
                        DatePickerDialog.THEME_HOLO_LIGHT, listener, year, month, day );
                dialog.show();
            }
        } );
    }

    private void LoadSave() {
        final SharedPreferences sharedPreferences = getActivity ().getSharedPreferences( "Peixun_save_data", MODE_PRIVATE );
        boolean load = sharedPreferences.getBoolean( "load", false );
        if (load) {
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext () );
            builder.setTitle( "检测到上次的保存的记录" );
            builder.setMessage( "是否载入?" );
            builder.setPositiveButton( "是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Et_peixunjigou.setText( sharedPreferences.getString( "peixunjigou", " " ) );
                    datefrom.setText( sharedPreferences.getString( "timefrom", " " ) );
                    dateto.setText( sharedPreferences.getString( "timeto", " " ) );
                    Et_shoukefangshi.setText( sharedPreferences.getString( "shoukefangshi", " " ) );
                    Et_shoukedidian.setText( sharedPreferences.getString( "shoukedidian", " " ) );
                    Et_peixunneirong.setText( sharedPreferences.getString( "peixunneirong", " " ) );
                    Et_peixunyusuan.setText( sharedPreferences.getString( "peixunyusuan", " " ) );
                    Et_kaohexingshi.setText( sharedPreferences.getString( "kaohefangshi", " " ) );
                    Et_addtip.setText( sharedPreferences.getString( "addtip", " " ) );
                }
            } ).

                    setNegativeButton( "否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor = getActivity ().getSharedPreferences( "Peixun_save_data", MODE_PRIVATE ).edit();
                            editor.putBoolean( "load", false );
                            editor.apply();
                        }
                    } );
            builder.show();
        }
    }

    private void LoadData(String code) {
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
        Et_peixunjigou.setText(  data.get( 7 ) );
        datefrom.setText( data.get( 6 ).substring( 0, 8 ) );
        dateto.setText( data.get( 6 ).substring( 9 ) );
        Et_shoukefangshi.setText( data.get( 5 )) ;
        Et_shoukedidian.setText( data.get( 4 ) );
        Et_peixunyusuan.setText(data.get( 0 ) );
        Et_peixunneirong.setText( data.get( 3 ) );
        Et_kaohexingshi.setText( data.get( 2 ) );
        Et_addtip.setText( data.get( 1 ) );
    }

    private void LoadData1(String code) {
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
        Et_peixunjigou.setText(  data.get( 7 ) );
        datefrom.setText( data.get( 6 ).substring( 0, 8 ) );
        dateto.setText( data.get( 6 ).substring( 9 ) );
        Et_shoukefangshi.setText( data.get( 5 )) ;
        Et_shoukedidian.setText( data.get( 4 ) );
        Et_peixunyusuan.setText(data.get( 3 ) );
        Et_peixunneirong.setText( data.get( 2 ) );
        Et_kaohexingshi.setText( data.get( 1 ) );
        Et_addtip.setText( data.get( 0 ) );
    }

    private void SetWidget() {
        back=view.findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        } );

        Btn_quick = view.findViewById( R.id.Btn_peixun_快速报销 );
        Btn_addaccount = view.findViewById( R.id.Btn_peixun_追加预算 );
        Btn_end = view.findViewById( R.id.Btn_peixun_结束行程 );
        Btn_change = view.findViewById( R.id.Btn_peixun_change );
        Btn_submit = view.findViewById( R.id.Btn_peixun_提交 );

        Et_peixunjigou = view.findViewById( R.id.Et_peixun_培训机构 );
        Et_shoukefangshi = view.findViewById( R.id.Et_peixun_授课方式 );
        Et_shoukedidian = view.findViewById( R.id.Et_peixun_授课地点 );
        Et_peixunyusuan = view.findViewById( R.id.Et_peixun_培训预算 );
        Et_peixunneirong = view.findViewById( R.id.Et_peixun_培训内容 );
        Et_kaohexingshi = view.findViewById( R.id.Et_peixun_考核形式 );
        Et_addtip = view.findViewById( R.id.Et_peixun_备注 );

        btngroup1 = view.findViewById( R.id.Btngroup_peixun_1 );
        btngroup2 = view.findViewById( R.id.Btngroup_peixun_2 );

        editor = getActivity ().getSharedPreferences( "Peixun_save_data", MODE_PRIVATE ).edit();

        datefrom = view.findViewById( R.id.Datefrom );
        dateto = view.findViewById( R.id.Dateto );
    }

    private void SetChange() {
        Btn_change.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                switch (budget_status) {
                    case 0:
                        editor = getActivity ().getSharedPreferences( "Peixun_save_data", MODE_PRIVATE ).edit();
                        editor.putBoolean( "load", true );
                        editor.putString( "peixunjigou", Et_peixunjigou.getText().toString() );
                        editor.putString( "timefrom", datefrom.getText().toString() );
                        editor.putString( "timeto", dateto.getText().toString() );
                        editor.putString( "shoukefangshi", Et_shoukefangshi.getText().toString() );
                        editor.putString( "shoukedidian", Et_shoukedidian.getText().toString() );
                        editor.putString( "peixunneirong", Et_peixunneirong.getText().toString() );
                        editor.putString( "peixunyusuan", Et_peixunyusuan.getText().toString() );
                        editor.putString( "kaohefangshi", Et_kaohexingshi.getText().toString() );
                        editor.putString( "addtip", Et_addtip.getText().toString() );
                        editor.apply();
                        Toast.makeText( getContext (), "保存成功", Toast.LENGTH_SHORT ).show();
                        break;
                    case 1:
                        reply = con.CSQI( "237", cookie, "000" );
                        System.out.println( reply );
                        if (Objects.equals( reply, "1" )) {
                            Toast.makeText( getContext (), "撤销成功", Toast.LENGTH_SHORT ).show();
                            budget_status = 0;
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

    private void SetSubmit() {
        Btn_submit.setOnClickListener( new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                editor.putBoolean( "load", false );
                editor.apply();
                String peixunjigou = Et_peixunjigou.getText().toString();
                String datef=datefrom.getText().toString();
                String datet=dateto.getText().toString();
                if(Integer.parseInt( datef )>Integer.parseInt( datet )){
                    Toast.makeText( getContext (),"请选择正确的时间区间",
                            Toast.LENGTH_SHORT ).show();
                    return;
                }
                String peixunriqi = datef + "-" +datet ;
                String shoukefangshi = Et_shoukefangshi.getText().toString();
                String shoukedidian = Et_shoukedidian.getText().toString();
                String peixunneirong = Et_peixunneirong.getText().toString();
                float yusuan = (float) 0;
                if (Et_peixunyusuan.length() != 0)
                    try {
                        yusuan = Float.parseFloat( Et_peixunyusuan.getText().toString() );
                    } catch (Exception e) {
                        Toast.makeText( getContext (), "请输入正确的金额", Toast.LENGTH_SHORT ).show();
                        return;
                    }
                else {
                    Toast.makeText( getContext (), "金额不能为空", Toast.LENGTH_SHORT ).show();
                    return;
                }
                String kaohefangshi = Et_kaohexingshi.getText().toString();
                String addtip = Et_addtip.getText().toString();
                String ask = "#";
                // 培训机构名称
                ask += peixunjigou + "#";
                // 培训日期
                ask += peixunriqi + "#";
                //授课方式
                ask += shoukefangshi + "#";
                //授课地点
                ask += shoukedidian + "#";
                // 培训预算
                ask += yusuan + "#";
                //培训内容
                ask += peixunneirong + "#";
                //考核形式
                ask += kaohefangshi + "#";
                //备注
                ask += addtip + "#";
                if(budget_status==1){
                    reply="0";
                    reply = con.CSQI( "237", cookie, "000" );
                    System.out.println( reply );
                    if (!Objects.equals( reply, "1" )) {
                        Toast.makeText( getContext (), "修改失败", Toast.LENGTH_SHORT ).show();
                        return;
                    }
                }
                reply = con.CSQI( "236", cookie, ask );
                if (reply.substring( 0, 3 ).equals( "200" )) {
                    System.out.println( "提交成功" );
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

    private void SetAddAccount() {
        Btn_addaccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout dialoglayout;
                final Dialog dialog = new Dialog( getContext () );
                dialog.setContentView( R.layout.dialog );
                dialoglayout = dialog.findViewById( R.id.差旅追加预算 );
                dialoglayout.setVisibility( View.GONE );
                dialoglayout = dialog.findViewById( R.id.layout_peixun_追加预算 );
                dialoglayout.setVisibility( View.VISIBLE );
                dialog.show();

                final EditText addaccount = dialog.findViewById( R.id.Et_peixun_追加预算 );
                Button submit = dialog.findViewById( R.id.Btn_peixun_快速报销_submit );
                Button cancel = dialog.findViewById( R.id.Btn_peixun_快速报销_cancel );
                submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float account;
                        if (addaccount.length() != 0) {
                            try {
                                account = Float.parseFloat( addaccount.getText().toString() );
                            } catch (Exception e) {
                                Toast.makeText( getContext (), "请输入正确的金额", Toast.LENGTH_SHORT ).show();
                                return;
                            }
                        } else {
                            Toast.makeText( getContext (), "金额不能为空", Toast.LENGTH_SHORT ).show();
                            return;
                        }
                        String tmp = "#" + account + "#";
                        reply = con.CSQI( "241", cookie, tmp );
                        Toast.makeText( getContext (),"提交成功",Toast.LENGTH_SHORT ).show();
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

    private void SetEnd() {
        Btn_end.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply = con.CSQI( "239", cookie, "000" );
                if (reply.substring( 0, 3 ).equals( "200" )) {
                    Toast.makeText( getContext (), "已成功结束本次申请！", Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent( getContext (), AfterEntering.class );
                    budget_status = 0;
                    intent.putExtra( "BudgetStatus", budget_status );
                    intent.putExtra( "cookie", cookie );
                    startActivity( intent );
                } else
                    Toast.makeText( getContext (), "未能结束本次申请！", Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    private void setQuickReimbursement(){
        Btn_quick.setOnClickListener ( new View.OnClickListener ( ) {
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
                    Intent intent = new Intent( "android.intent.action.GET_CONTENT" );
                    intent.setType( "image/*" );
                    startActivityForResult( intent, CHOOSE_PHOTO );
                } else {
                    Toast.makeText( getContext (), "未开启权限",
                            Toast.LENGTH_LONG ).show();
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
                        imagePath=handleImageOnKitKat( data );
                    } else {
                        //4.4以下
                        imagePath=handleImageBeforeKitKat( data );
                    }
                }
                break;
            default:
                break;
        }
        if(imagePath!=null){
            FtpUtils ftpUtils=new FtpUtils();
            String kzm=imagePath.substring ( imagePath.lastIndexOf ( "." ) );
            String new_name=con.CSQI("400",cookie,"000" )+kzm;
            ftpUtils.uploadFile("/home/test/images", new_name, imagePath);  //上传图片
            //con.CSQI("404", cookie, new_name); // 差旅快速报销
            if(con.CSQI("404", cookie, new_name).equals("200#")) // 差旅快速报销
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
        return  imagePath;
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
