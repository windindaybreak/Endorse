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
import android.text.TextUtils;
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

public class ZhaoDai extends Fragment {

    public int cookie = 0;
    public int budget_status = 0;
    public static final int CHOOSE_PHOTO = 2;
    private Connection con = new Connection();
    private String reply = "000",date = "000",place = "000",reason = "000",ps = " ";
    private int fLevel = 0,num = 0, isCar = 0;
    private float zCar = 0,acco_budget = 0,food_budget = 0,tot_budget = 0;

    private Button submit,save,quickreimbursement,Btn_addaccount,Btn_end;
    private EditText Et_level,Et_people_num,Et_place,Et_people,Et_car,Et_cost_car,Et_cost_food,Et_cost_hotel,Et_cost_total,Et_addtip;
    private SharedPreferences.Editor editor;

    private TextView datefrom,dateto;
    private ImageView back;

    private View view;

    private GetDataFromMainActivity getDataFromMainActivity;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = Objects.requireNonNull ( getActivity ( ) ).getLayoutInflater ().inflate ( R.layout.fragment_zhao_dai,container,false );

        cookie = getDataFromMainActivity.GetCookie ();
        budget_status = getDataFromMainActivity.GetBudgetStatus ();

        SetWidget ();
        SetTimeChoose ();
        SetBtn_AddAccount ();
        SetSubmit ();
        SetSave ();
        SetBtnEnd ();
        SetQuickReimbursement ();
        Loadsave ();
        switch (budget_status) {
            case 1://若当前还未审核通过
                save.setText( "撤销" );
                loaddata( "213" );
                break;
            case 2:
                loaddata( "214" );
                LinearLayout btngroup_1 = view.findViewById ( R.id.btngroup_zhaodai_1 );
                btngroup_1.setVisibility( View.GONE );
                btngroup_1 = view.findViewById( R.id.btngroup_zhaodai_2 );
                btngroup_1.setVisibility( View.VISIBLE );
                setChangeable();
                break;
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach ( context );
        getDataFromMainActivity = (GetDataFromMainActivity) context;
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

    private void SetBtnEnd(){
        Btn_end.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(con.CSQI ( "217",cookie,"000" ).substring ( 0,3 ).equals ( "200" )){
                    Toast.makeText ( getContext (),"已成功结束本次申请！",Toast.LENGTH_SHORT ).show ();
                    getDataFromMainActivity.GetInstance ().finish ();
                }
                else
                    Toast.makeText ( getContext (),"未能结束本次申请！",Toast.LENGTH_SHORT ).show ();
            }
        } );

    }

    private void SetBtn_AddAccount() {
        Btn_addaccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout dialoglayout;
                final Dialog dialog = new Dialog( getContext () );
                dialog.setContentView( R.layout.dialog );
                dialoglayout = dialog.findViewById( R.id.差旅追加预算 );
                dialoglayout.setVisibility( View.GONE );
                dialoglayout = dialog.findViewById( R.id.layout_zhaodai_追加预算 );
                dialoglayout.setVisibility( View.VISIBLE );
                dialog.show();

                EditText Et_zcar = dialog.findViewById( R.id.Et_zhaodai_追加预算_租车费追加 );
                EditText Et_acco = dialog.findViewById( R.id.Et_zhaodai_追加预算_住宿费追加 );
                EditText Et_food = dialog.findViewById( R.id.Et_zhaodai_追加预算_餐饮费追加 );
                EditText Et_tot = dialog.findViewById( R.id.Et_zhaodai_追加预算_总金额追加 );
                Button Btn_submit = dialog.findViewById( R.id.Btn_zhaodai_追加预算_提交 );
                Button Btn_cancel = dialog.findViewById( R.id.Btn_zhaodai_追加预算_取消 );
                final String zcar_add = Et_zcar.getText().toString();
                final String acco_add = Et_acco.getText().toString();
                final String food_add = Et_food.getText().toString();
                final String tot_add = Et_tot.getText().toString();
                Btn_submit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty( zcar_add ) ||
                                TextUtils.isEmpty( acco_add ) ||
                                TextUtils.isEmpty( food_add ) ||
                                TextUtils.isEmpty( tot_add )) {
                            Toast.makeText( getContext (), "提交失败,追加费用不能为空",
                                    Toast.LENGTH_SHORT ).show();
                        } else {
                            String command = "#" + zcar_add + "#" + acco_add + "#" + food_add + "#" + tot_add + "#";
                            reply = con.CSQI( "223", cookie, command );
                            Toast.makeText( getContext (), "提交成功", Toast.LENGTH_SHORT ).show();
                            dialog.dismiss();
                        }
                    }
                } );
                Btn_cancel.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                } );
            }
        } );
    }

    private void setChangeable() {
        Et_people.setEnabled( false );
        Et_level.setEnabled( false );
        Et_people_num.setEnabled( false );
        datefrom.setEnabled( false );
        dateto.setEnabled( false );
        //Et_time.setEnabled( false );
        Et_place.setEnabled( false );
        Et_car.setEnabled( false );
        Et_cost_car.setEnabled( false );
        Et_cost_hotel.setEnabled( false );
        Et_cost_food.setEnabled( false );
        Et_cost_total.setEnabled( false );
        Et_addtip.setEnabled( false );
    }

    private void Loadsave() {
        final SharedPreferences sharedPreferences = getActivity ().getSharedPreferences( "ZhaoDai_save_data", MODE_PRIVATE );
        boolean load = sharedPreferences.getBoolean( "load", false );
        if (load) {
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext () );
            builder.setTitle( "检测到上次的保存的记录" );
            builder.setMessage( "是否载入?" );
            builder.setPositiveButton( "是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    datefrom.setText( sharedPreferences.getString( "timefrom", " " ) );
                    dateto.setText( sharedPreferences.getString( "timeto", " " ) );
                    Et_place.setText( sharedPreferences.getString( "place", " " ) );
                    Et_people.setText( sharedPreferences.getString( "reason", " " ) );
                    float tmp_float;
                    int tmp_int;
                    tmp_int = sharedPreferences.getInt( "fLevel", -1 );
                    if (tmp_int == -1) {
                        Et_level.setText( " " );
                    } else
                        Et_level.setText( String.valueOf( tmp_int ) );
                    tmp_int = sharedPreferences.getInt( "num", -1 );
                    if (tmp_int == -1) {
                        Et_people_num.setText( " " );
                    } else
                        Et_people_num.setText( String.valueOf( tmp_int ) );
                    Et_car.setText( sharedPreferences.getString( "car", " " ) );
                    tmp_float = sharedPreferences.getFloat( "zCar", 0 );
                    if (tmp_float != 0)
                        Et_cost_car.setText( String.valueOf( tmp_float ) );
                    tmp_float = sharedPreferences.getFloat( "acco_budget", 0 );
                    if (tmp_float != 0)
                        Et_cost_hotel.setText( String.valueOf( tmp_float ) );
                    tmp_float = sharedPreferences.getFloat( "food_budget", 0 );
                    if (tmp_float != 0)
                        Et_cost_food.setText( String.valueOf( tmp_float ) );
                    tmp_float = sharedPreferences.getFloat( "tot_budget", 0 );
                    if (tmp_float != 0)
                        Et_cost_total.setText( String.valueOf( tmp_float ) );
                    Et_addtip.setText( sharedPreferences.getString( "ps", " " ) );
                }
            } ).setNegativeButton( "否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor = getActivity ().getSharedPreferences( "ZhaoDai_save_data", MODE_PRIVATE ).edit();
                    editor.putBoolean( "load", false );
                    editor.apply();
                }
            } );
            builder.show();
        }
    }

    private void loaddata(String code) {
        reply = con.CSQI( code, cookie, "000" );
        boolean query213 = true;
        if (query213) {
            List<String> data = new ArrayList<>();
            int p = reply.length() - 1, q = p;
            String temp;
            while (reply.charAt( p ) == '#') p--;

            for (int i = 0; i < 11; i++) {
                q = p;
                while (p >= 0 && reply.charAt( p ) != '#')
                    p--;
                temp = reply.substring( p + 1, q + 1 );
                data.add( temp );
                p--;
            }
            Et_people.setText( data.get( 10 ) );
            Et_level.setText( data.get( 9 ) );
            Et_people_num.setText( data.get( 8 ) );

            datefrom.setText( data.get( 7 ).substring( 0, 8 ) );
            dateto.setText( data.get( 7 ).substring( 9 ) );
            Et_place.setText( data.get( 6 ) );
            switch (data.get( 5 )) {
                case "0":
                    Et_car.setText( "否" );
                    break;
                case "1":
                    Et_car.setText( "是" );
                    break;
            }
            Et_cost_car.setText( data.get( 4 ) );
            Et_cost_hotel.setText( data.get( 3 ) );
            Et_cost_food.setText( data.get( 2 ) );
            Et_cost_total.setText( data.get( 1 ) );
            Et_addtip.setText( data.get( 0 ) );
        }
    }

    private void SetSave() {
        save.setOnClickListener( new View.OnClickListener() {
            @SuppressLint({"CommitPrefEdits", "NewApi"})
            @Override
            public void onClick(View v) {
                if (budget_status == 1) {
                    reply = "0";
                    reply = con.CSQI( "210", cookie, "000" );
                    if (Objects.equals( reply, "0" )) {
                        Toast.makeText( getContext (), "撤销失败", Toast.LENGTH_SHORT ).show();
                    }
                    else{
                        Toast.makeText( getContext (), "撤销成功", Toast.LENGTH_SHORT ).show();
                        budget_status = 0;
                        Intent Ate = new Intent( getContext (), AfterEntering.class );
                        Ate.putExtra( "BudgetStatus", budget_status );
                        Ate.putExtra( "cookie", cookie );
                        startActivity( Ate );
                    }
                }else if(budget_status==0){
                    editor = getActivity ().getSharedPreferences( "ZhaoDai_save_data", MODE_PRIVATE ).edit();
                    editor.putBoolean( "load", true );
                    editor.putString( "timefrom", datefrom.getText().toString() );
                    editor.putString( "timeto", dateto.getText().toString() );
                    editor.putString( "place", Et_place.getText().toString() );
                    editor.putString( "reason", Et_people.getText().toString() );
                    String tmp;
                    tmp = Et_level.getText().toString();
                    if (!tmp.isEmpty()) {
                        editor.putInt( "fLevel", Integer.parseInt( tmp ) );
                    } else {
                        editor.putInt( "fLevel", 1 );
                    }
                    tmp = Et_people_num.getText().toString();
                    if (!tmp.isEmpty()) {
                        editor.putInt( "num", Integer.parseInt( tmp ) );
                    } else {
                        editor.putInt( "num", 1 );
                    }
                    editor.putString( "car", Et_car.getText().toString() );
                    tmp = Et_cost_car.getText().toString();
                    if (!tmp.isEmpty())
                        editor.putFloat( "zCar", Float.parseFloat( tmp ) );
                    else {
                        editor.putFloat( "zCar", 0 );
                    }
                    tmp = Et_cost_hotel.getText().toString();
                    if (!tmp.isEmpty())
                        editor.putFloat( "acco_budget", Float.parseFloat( tmp ) );
                    else
                        editor.putFloat( "acco_budget", 0 );
                    tmp = Et_cost_food.getText().toString();
                    if (!tmp.isEmpty())
                        editor.putFloat( "food_budget", Float.parseFloat( tmp ) );
                    else {
                        editor.putFloat( "food_budget", 0 );
                    }
                    tmp = Et_cost_total.getText().toString();
                    if (!tmp.isEmpty())
                        editor.putFloat( "tot_budget", Float.parseFloat( tmp ) );
                    else {
                        editor.putFloat( "tot_budget", 0 );
                    }
                    editor.putString( "ps", Et_addtip.getText().toString() );
                    editor.apply();
                    Toast.makeText( getContext (), "保存成功", Toast.LENGTH_SHORT ).show();
                    getDataFromMainActivity.GetInstance ().finish ();
                }

            }
        } );
    }

    private void SetWidget() {
        back=view.findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        } );

        Et_level = view.findViewById( R.id.Et_zhaodai_级别 );
        Et_people_num = view.findViewById( R.id.Et_zhaodai_人数 );
        //Et_time = findViewById( R.id.Et_zhaodai_接待时间 );
        Et_place = view.findViewById( R.id.Et_zhaodai_接待地点 );
        Et_people = view.findViewById( R.id.Et_zhaodai_接待内容 );
        Et_car = view.findViewById( R.id.Et_zhaodai_用车 );
        Et_cost_car = view.findViewById( R.id.Et_zhaodai_租车费 );
        Et_cost_food = view.findViewById( R.id.Et_zhaodai_餐饮费 );
        Et_cost_hotel = view.findViewById( R.id.Et_zhaodai_住宿费 );
        Et_cost_total = view.findViewById( R.id.Et_zhaodai_总金额 );
        Et_addtip = view.findViewById( R.id.Et_zhaodai_补充说明 );

        submit = view.findViewById( R.id.Btn_zhaodai_submit );
        save = view.findViewById( R.id.Btn_zhaodai_save );

        quickreimbursement = view.findViewById( R.id.Btn快速报销 );
        Btn_addaccount = view.findViewById( R.id.Btn追加预算 );
        Btn_end = view.findViewById( R.id.Btn结束行程 );

        datefrom = view.findViewById( R.id.Datefrom );
        dateto = view.findViewById( R.id.Dateto );
    }

    private void SetSubmit() {
        submit.setOnClickListener( new View.OnClickListener() {
            @SuppressLint({"CommitPrefEdits", "NewApi"})
            @Override
            public void onClick(View v) {
                editor = getActivity ().getSharedPreferences( "ZhaoDai_save_data", MODE_PRIVATE ).edit();
                editor.putBoolean( "load", false );
                editor.apply();
                String datef=datefrom.getText().toString();
                String datet=dateto.getText().toString();
                if(Integer.parseInt( datef )>Integer.parseInt( datet )){
                    Toast.makeText( getContext (),"请选择正确的时间区间",
                            Toast.LENGTH_SHORT ).show();
                    return;
                }
                date = datef + "-" +datet ;

                place = Et_place.getText().toString();

                reason = Et_people.getText().toString();

                String tmp;

                tmp = Et_level.getText().toString();
                if (!tmp.isEmpty()) {
                    fLevel = Integer.parseInt( tmp );
                }

                tmp = Et_people_num.getText().toString();
                if (!tmp.isEmpty()) {
                    num = Integer.parseInt( tmp );
                }

                if (Et_car.getText().toString().equals( "是" )) {
                    isCar = 1;
                    tmp = Et_cost_car.getText().toString();
                    if (!tmp.isEmpty())
                        zCar = Float.parseFloat( tmp );
                } else {
                    isCar = 0;
                    zCar = 0;
                }
                tmp = Et_cost_hotel.getText().toString();
                if (!tmp.isEmpty())
                    acco_budget = Float.parseFloat( tmp );

                tmp = Et_cost_food.getText().toString();
                if (!tmp.isEmpty())
                    food_budget = Float.parseFloat( tmp );

                tmp = Et_cost_total.getText().toString();
                if (!tmp.isEmpty())
                    tot_budget = Float.parseFloat( tmp );

                ps = Et_addtip.getText().toString();

                String apply = "#" + date + "#" + place + "#" + reason + "#" + fLevel + "#" + num + "#" +
                        zCar + "#" + acco_budget + "#" + food_budget + "#" + tot_budget + "#" + ps + "#" + isCar + "#";
                //to do: submit the query
                if (budget_status == 1) {
                    reply = "0";
                    reply = con.CSQI( "210", cookie, "000" );
                    if (Objects.equals( reply, "0" )) {
                        Toast.makeText( getContext (), "修改失败", Toast.LENGTH_SHORT ).show();
                        return;
                    }
                }

                reply = con.CSQI( "209", cookie, apply );
                if (reply.equals( "1" )) {
                    System.out.println( "提交成功" );
                    Toast.makeText( getContext (), "提交成功", Toast.LENGTH_SHORT ).show();
                    getDataFromMainActivity.GetInstance ().finish ();
                } else {
                    System.out.println( "提交失败" );
                    Toast.makeText( getContext (), "提交失败", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

    private void SetQuickReimbursement() {
        quickreimbursement.setOnClickListener( new View.OnClickListener() {
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
            System.out.println( "imagePath:" + imagePath );
            FtpUtils ftpUtils = new FtpUtils();
            String kzm = imagePath.substring( imagePath.lastIndexOf( "." ) );
            String new_name = con.CSQI( "400", cookie, "000" ) + kzm;
            ftpUtils.uploadFile( "/home/test/images", new_name, imagePath );  //上传图片
            //con.CSQI( "402", cookie, new_name ); // 差旅快速报销
            if(con.CSQI("402", cookie, new_name).equals("200#")) // 差旅快速报销
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
