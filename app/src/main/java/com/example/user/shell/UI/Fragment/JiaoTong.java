package com.example.user.shell.UI.Fragment;

import android.Manifest;
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

import java.util.Calendar;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.user.shell.UI.Fragment.ChaiLv.imagePath;

public class JiaoTong extends Fragment implements View.OnClickListener {


    int cookie = 10000;
    int budget_status = 0;// 0  未申请 1 待审核   2已审核 3 过期
    private static final int CHOOSE_PHOTO = 3;

    String reply = "000";
    Connection con = new Connection();

    private Button BtnJiaoTongCancel,BtnJiaoTongRefer,BtnQuickReimbursement,BtnOverJourney,BtnAddbudget;
    private LinearLayout BtnGroup1,BtnGroup2;
    private EditText EtJiaoTongReason,EtJiaoTongPs,EtJiaoTongMoney;
    private String reason;//="去乡下飙车"; //出行理由
    private String date;//="20190822-20190825"; //出行日期
    private float budget;//=23333;  //交通预算
    private String ps;//="去警察局交罚款"; // 备注
    private ImageView back;

    private TextView datefrom, dateto;
    private SharedPreferences.Editor editor;

    private View view;

    private GetDataFromMainActivity getDataFromMainActivity;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = Objects.requireNonNull ( getActivity ( ) ).getLayoutInflater ().inflate ( R.layout.fragment_jiao_tong,container,false );

        cookie = getDataFromMainActivity.GetCookie ();
        budget_status = getDataFromMainActivity.GetBudgetStatus ();

        LoadSave ();

        SetWidget();

        BtnJiaoTongCancel.setOnClickListener( this );
        BtnJiaoTongRefer.setOnClickListener( this );
        BtnQuickReimbursement.setOnClickListener( this );
        BtnAddbudget.setOnClickListener( this );
        BtnOverJourney.setOnClickListener( this );

        SetTimeChoose ();

        switch (budget_status) {
            case 1:
                Uploading ();
                break;
            case 2:
                Uploaded ();
                break;
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach ( context );
        getDataFromMainActivity = (GetDataFromMainActivity) context;
    }

    private void SetWidget() {
        back=view.findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        } );

        BtnJiaoTongRefer = view.findViewById( R.id.Btn交通提交 );
        BtnJiaoTongCancel = view.findViewById( R.id.Btn交通撤销 );
        BtnQuickReimbursement = view.findViewById( R.id.Btn快速报销 );
        BtnAddbudget = view.findViewById( R.id.Btn追加预算 );
        BtnOverJourney = view.findViewById( R.id.Btn结束行程 );

        BtnGroup1 = view.findViewById( R.id.btnGroup_jiaotong_1 );
        BtnGroup2 = view.findViewById( R.id.btnGroup_jiaotong_2 );

        //EtJiaoTongDate = findViewById( R.id.Et交通出行日期 );
        EtJiaoTongMoney = view.findViewById( R.id.Et交通预算 );
        EtJiaoTongPs = view.findViewById( R.id.Et交通备注 );
        EtJiaoTongReason = view.findViewById( R.id.Et交通出行理由 );
        editor= getActivity ().getSharedPreferences( "Jiaotong_save_data", MODE_PRIVATE ).edit();
    }

    private void Uploading() {
        BtnJiaoTongCancel.setText( "撤销" );
        BtnGroup1.setVisibility( View.VISIBLE );
        BtnGroup2.setVisibility( View.GONE );

        //EtJiaoTongDate.setEnabled ( true );
        EtJiaoTongMoney.setEnabled( true );
        EtJiaoTongReason.setEnabled( true );
        EtJiaoTongPs.setEnabled( true );

        reply = con.CSQI( "221", cookie, "000" );
        int l = 1, r = 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        //System.out.println("出行日期："+reply.substring(l, r));

        datefrom.setText( reply.substring( l, r ).substring( 0, 8 ) );
        dateto.setText( reply.substring( l, r ).substring( 9 ) );
        //EtJiaoTongDate.setText(reply.substring(l, r));
        l = r + 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        //System.out.println("出行理由："+reply.substring(l, r));
        EtJiaoTongReason.setText( reply.substring( l, r ) );

        l = r + 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        //System.out.println("交通预算："+reply.substring(l, r));
        EtJiaoTongMoney.setText( reply.substring( l, r ) );

        l = r + 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        //System.out.println("备注："+reply.substring(l, r));
        EtJiaoTongPs.setText( reply.substring( l, r ) );
    }

    private void Uploaded() {
        BtnGroup1.setVisibility( View.GONE );
        BtnGroup2.setVisibility( View.VISIBLE );

        datefrom.setEnabled( false );
        dateto.setEnabled( false );
        //EtJiaoTongDate.setEnabled ( false );
        EtJiaoTongMoney.setEnabled( false );
        EtJiaoTongReason.setEnabled( false );
        EtJiaoTongPs.setEnabled( false );

        reply = con.CSQI( "226", cookie, "000" );

        int l = 1, r = 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        datefrom.setText( reply.substring( l, r ).substring( 0, 8 ) );
        dateto.setText( reply.substring( l, r ).substring( 9 ) );
        //EtJiaoTongDate.setText ( reply.substring(l, r) );//交通出行日期
        l = r + 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        EtJiaoTongReason.setText( reply.substring( l, r ) );//交通出行理由
        l = r + 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        EtJiaoTongPs.setText( reply.substring( l, r ) );//交通出行备注
        l = r + 1;
        for (int i = l; i < reply.length(); i++)
            if (reply.charAt( i ) == '#') {
                r = i;
                break;
            }
        EtJiaoTongMoney.setText( reply.substring( l, r ) );
    }

    private void LoadSave() {
        if (budget_status != 0) {
            return;
        }
        final SharedPreferences sharedPreferences = getActivity ().getSharedPreferences( "Jiaotong_save_data", MODE_PRIVATE );
        boolean load = sharedPreferences.getBoolean( "load", false );
        if (load) {
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext () );
            builder.setTitle( "检测到上次的保存的记录" );
            builder.setMessage( "是否载入?" );
            builder.setPositiveButton( "是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    datefrom.setText( sharedPreferences.getString( "datefrom", " " ) );
                    dateto.setText( sharedPreferences.getString( "dateto", " " ) );
                    //Et_time.setText( sharedPreferences.getString( "time", " " ) );
                    EtJiaoTongMoney.setText( sharedPreferences.getString( "money", "" ) );
                    EtJiaoTongPs.setText( sharedPreferences.getString( "tips", "" ) );
                    EtJiaoTongReason.setText( sharedPreferences.getString( "reason", "" ) );

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

    public void SetTimeChoose() {
        datefrom = view.findViewById( R.id.Datefrom );
        dateto = view.findViewById( R.id.Dateto );
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
                        dateto.setText( year + "" + (++month) + "" +
                                (dayOfMonth < 10 ? ("0" + dayOfMonth) : dayOfMonth) );
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog( getContext (), DatePickerDialog.THEME_HOLO_LIGHT, listener, year, month, day );
                dialog.show();
            }
        } );
    }

    private void ReferClicked() {//提交按钮点击事件
        reason = EtJiaoTongReason.getText().toString();
        String datef = datefrom.getText().toString();
        String datet = dateto.getText().toString();
        if (Integer.parseInt( datef ) > Integer.parseInt( datet )) {
            Toast.makeText( getContext (), "请选择正确的时间区间", Toast.LENGTH_SHORT ).show();
            return;
        }
        String date = datef + "-" + datet;
        //date = EtJiaoTongDate.getText().toString();
        String tmp;
        tmp = EtJiaoTongMoney.getText().toString();
        if (!tmp.isEmpty()) budget = Float.parseFloat( EtJiaoTongMoney.getText().toString() );
        ps = EtJiaoTongPs.getText().toString();
        if (budget_status == 1) {
            reply = con.CSQI( "224", cookie, "000" );
            if (reply.equals( "0" )) {
                Toast.makeText( getContext (), "修改失败", Toast.LENGTH_SHORT ).show();
                return;
            }
        }

        String ask = "#" + reason + "#" + date + "#" + budget + "#" + ps + "#";
        reply = con.CSQI( "219", cookie, ask );
        //System.out.println(reply);
        if (reply.charAt( 0 ) == '0') {
            Toast.makeText( getContext (), "提交失败", Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText( getContext (), "提交成功", Toast.LENGTH_SHORT ).show();
            budget_status = 1;
            editor.putBoolean( "load",false );
            editor.apply();
            Intent Ate = new Intent( getContext (), AfterEntering.class );
            Ate.putExtra( "BudgetStatus", budget_status );
            Ate.putExtra( "cookie", cookie );
            startActivity( Ate );
        }
    }

    private void CancelClicked() {//撤销按钮点击事件

        switch (budget_status) {
            case 0:
                editor.putBoolean( "load", true );
                editor.putString( "reason", EtJiaoTongReason.getText().toString() );
                editor.putString( "datefrom", datefrom.getText().toString() );
                editor.putString( "dateto", dateto.getText().toString() );
                editor.putString( "money", EtJiaoTongMoney.getText().toString() );
                editor.putString( "tips", EtJiaoTongPs.getText().toString() );
                editor.apply();
                Toast.makeText( getContext (), "保存成功", Toast.LENGTH_SHORT ).show();
                getDataFromMainActivity.GetInstance ().finish();
                break;

            case 1:
                if (con.CSQI( "224", cookie, "000" ).equals( "1" )) {
                    Toast.makeText( getContext (), "撤销成功！", Toast.LENGTH_SHORT ).show();
                    getDataFromMainActivity.GetInstance ().finish();
                } else Toast.makeText( getContext (), "撤销失败！", Toast.LENGTH_SHORT ).show();
                break;
        }

    }

    private void OverJourneyClicked() {
        Connection con = new Connection();
        if (con.CSQI( "225", cookie, "000" ).substring( 0, 3 ).equals( "200" )) {
            Toast.makeText( getContext (), "已成功结束本次申请！", Toast.LENGTH_SHORT ).show();
            Intent intent = new Intent( getContext (), AfterEntering.class );
            startActivity( intent );
        } else
            Toast.makeText( getContext (), "未能结束本次申请！", Toast.LENGTH_SHORT ).show();
    }

    private void BtnAddBudget_clicked() {
        final Dialog dialog = new Dialog( getContext () );
        dialog.setContentView( R.layout.dialog );
        LinearLayout dialoglayout;

        dialoglayout = dialog.findViewById( R.id.差旅追加预算 );
        dialoglayout.setVisibility( View.GONE );
        dialoglayout = dialog.findViewById( R.id.layout_zixun_追加预算 );
        dialoglayout.setVisibility( View.VISIBLE );
        final EditText addaccount = dialog.findViewById( R.id.Et_zixun_追加预算 );
        final Button BtnJTSubmit = dialog.findViewById( R.id.Btn_zixun_submit_追加预算 );
        Button BtnJTCancel = dialog.findViewById( R.id.Btn_zixun_cancel_追加预算 );
        addaccount.setHint( "交通费预算追加" );

        dialog.show();

        BtnJTSubmit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addaccount.getText().toString().equals( "" ))
                    Toast.makeText( getContext (), "提交失败,追加费用不能为空", Toast.LENGTH_LONG ).show();
                else {
                    String command = "#" + addaccount.getText().toString() + "#";
                    reply = con.CSQI( "223", cookie, command );
                    Toast.makeText( getContext (), "提交成功", Toast.LENGTH_SHORT ).show();
                    dialog.dismiss();
                }
            }
        } );
        BtnJTCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );
    }


    private void BtnQuickReimbursement_clicked() {
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
            FtpUtils ftpUtils = new FtpUtils();
            String kzm = imagePath.substring( imagePath.lastIndexOf( "." ) );
            String new_name = con.CSQI( "400", cookie, "000" ) + kzm;
            ftpUtils.uploadFile( "/home/test/images", new_name, imagePath );  //上传图片
            //con.CSQI("403", cookie, new_name); // 差旅快速报销
            if (con.CSQI( "403", cookie, new_name ).equals( "200#" )) // 差旅快速报销
            {
                Toast.makeText( getContext (), "快速报销成功", Toast.LENGTH_SHORT ).show();
            } else {
                Toast.makeText( getContext (), "快速报销失败", Toast.LENGTH_SHORT ).show();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Btn交通撤销:
                CancelClicked();
                break;
            case R.id.Btn交通提交:
                ReferClicked();
                break;
            case R.id.Btn快速报销:
                BtnQuickReimbursement_clicked();
                break;
            case R.id.Btn追加预算:
                BtnAddBudget_clicked();
                break;
            case R.id.Btn结束行程:
                OverJourneyClicked();
                break;
        }
    }
}

