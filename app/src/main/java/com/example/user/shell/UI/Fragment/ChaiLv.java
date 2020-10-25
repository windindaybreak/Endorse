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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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


public class ChaiLv extends Fragment implements View.OnClickListener{

    private int choice1 = 0;
    private int choice2 = 0;
    private int budget_status = 0;// 0  未申请  1 待审核   2 已审核   3 过期
    private static final int CHOOSE_PHOTO = 1;
    static String imagePath = null;

    private String reply = "000";
    private Connection con = new Connection ( );

    private LinearLayout BtnGroup1,BtnGroup2;
    private Button BtnTravelChoice,BtnAccommodationChoice;
    private EditText EtFrom,EtTo,EtTravelCost,EtTravelAdd,EtFoodBudget,EtFoodAdd,
            EtAccommodationCost,EtAccommodationAdd,EtTrafficBudget,EtTrafficAdd,EtNote;
    private Button BtnCancel, BtnRefer, BtnQuickReimbursement, BtnOverJourney, BtnAddBudget;
    private int cookie = 10000;

    private TextView dateFrom, dateTo;
    private ImageView back;

    View view;

    GetDataFromMainActivity getDataFromMainActivity;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = Objects.requireNonNull ( getActivity ( ) ).getLayoutInflater ().inflate ( R.layout.fragment_chai_lv,container,false );

        cookie = getDataFromMainActivity.GetCookie ();
        budget_status = getDataFromMainActivity.GetBudgetStatus ();

        SetWidget ();//绑定id

        BtnCancel.setOnClickListener( this );
        BtnRefer.setOnClickListener( this );
        BtnQuickReimbursement.setOnClickListener( this );
        BtnOverJourney.setOnClickListener( this );
        BtnAddBudget.setOnClickListener( this );
        BtnTravelChoice.setOnClickListener( this );
        BtnAccommodationChoice.setOnClickListener( this );


        SetTimeChoose ();//设置时间

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

    private void Travel_clicked() {
        List<String> travel_list = new ArrayList<>();      //用于给AlertDialog填充数据的List
        travel_list.add( "平台购买" );
        travel_list.add( "自行购买" );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getContext (),
                android.R.layout.simple_list_item_single_choice, travel_list );
        ListView listView = new ListView( getContext () );
        listView.setAdapter( adapter );
        AlertDialog.Builder singlechoice = new AlertDialog.Builder( getContext () );
        singlechoice.setTitle( "选择路费购买方式" );
        singlechoice.setSingleChoiceItems( listView.getAdapter(), 0,
                new DialogInterface.OnClickListener() {//通过ListView构建单选对话框
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choice1 = which;                 //获取点击的位置
                    }
                } );
        singlechoice.setPositiveButton( "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//添加确定后的点击事件
                if (choice1 == 0) {
                    Toast.makeText( getContext (), "已选择平台购买", Toast.LENGTH_SHORT ).show();
                } else {
                    Toast.makeText( getContext (), "已选择自行购买", Toast.LENGTH_SHORT ).show();
                    EtTravelCost.setVisibility( View.VISIBLE );
                }
            }
        } );
        singlechoice.show();
    }

    private void Accommodation_clicked() {
        List<String> travel_list = new ArrayList<>();      //用于给AlertDialog填充数据的List
        travel_list.add( "平台购买" );
        travel_list.add( "自行购买" );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getContext(),
                android.R.layout.simple_list_item_single_choice, travel_list );
        ListView listView = new ListView( getContext() );
        listView.setAdapter( adapter );
        AlertDialog.Builder singlechoice = new AlertDialog.Builder( getContext() );
        singlechoice.setTitle( "选住宿购买方式" );
        singlechoice.setSingleChoiceItems( listView.getAdapter(), 0,
                new DialogInterface.OnClickListener() {//通过ListView构建单选对话框
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choice2 = which;                 //获取点击的位置
                    }
                } );
        singlechoice.setPositiveButton( "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//添加确定后的点击事件
                if (choice2 == 0) {
                    Toast.makeText( getContext(), "已选择平台购买", Toast.LENGTH_SHORT ).show();
                } else {
                    Toast.makeText( getContext(), "已选择自行购买", Toast.LENGTH_SHORT ).show();
                    EtAccommodationCost.setVisibility( View.VISIBLE );
                }
            }
        } );
        singlechoice.show();
    }

    private void BtnRefer_clicked() {
        String datef= dateFrom.getText().toString();
        String datet= dateTo.getText().toString();
        if(Integer.parseInt( datef )>Integer.parseInt( datet )){
            Toast.makeText( getContext(),"请选择正确的时间区间",
                    Toast.LENGTH_SHORT ).show();
            return;
        }
        //System.out.println("请输入出差日期:");
        String date = datef + "-" + datet;
        //date = EtDate.getText().toString();

        //System.out.println("请输入始发地:");
        String from = EtFrom.getText ( ).toString ( );

        //System.out.println("请输入目的地:");
        String to = EtTo.getText ( ).toString ( );
        String tmp;
        tmp = EtTrafficBudget.getText().toString();
        float travel_cost = 0;
        if (!tmp.isEmpty()) travel_cost = Float.parseFloat( EtTrafficBudget.getText().toString() );
        float accommodation_cost = 0;
        tmp = EtAccommodationCost.getText().toString();
        if (!tmp.isEmpty())
            accommodation_cost = Float.parseFloat( EtAccommodationCost.getText().toString() );

        String food_budget_str = EtFoodBudget.getText().toString();
        float food_budget = Float.parseFloat ( food_budget_str );

        String traffic_budget_str = EtTrafficBudget.getText().toString();
        float traffic_budget = Float.parseFloat ( traffic_budget_str );
        String note = EtNote.getText ( ).toString ( );

        if (budget_status == 1) {
            reply = con.CSQI( "207", cookie, "000" );
            if (reply.equals( "0" )) {
                Toast.makeText( getContext(), "修改失败", Toast.LENGTH_SHORT ).show();
                return;
            }
        }

        String apply = "#" + date + "#" + from + "#" + to + "#" + choice1 + "#" + travel_cost + "#" + choice2 + "#" + accommodation_cost + "#" + food_budget + "#" + traffic_budget + "#" + note + "#";

        reply = con.CSQI( "205", cookie, apply );

        if (reply.charAt( 0 ) == '0') {
            Toast.makeText( getContext(), "提交失败", Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText( getContext(), "提交成功", Toast.LENGTH_SHORT ).show();
            budget_status = 1;
            getDataFromMainActivity.GetInstance ().finish();
        }
    }

    @SuppressLint("NewApi")
    private void BtnCancel_clicked() {
        reply = "0";

        // 207 为撤销申请
        reply = con.CSQI( "207", cookie, "000" );
        if (Objects.equals( reply, "0" )) //System.out.println("撤销失败");
        {
            Toast.makeText( getContext(), "撤销失败", Toast.LENGTH_SHORT ).show();
        } else //System.out.println("撤销申请成功");
        {
            Toast.makeText( getContext(), "撤销成功", Toast.LENGTH_SHORT ).show();
            budget_status = 0;
            getDataFromMainActivity.GetInstance ().finish();
        }
    }

    private void BtnQuickReimbursement_clicked() {
        if (ContextCompat.checkSelfPermission( getContext(),
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
            FtpUtils ftpUtils = new FtpUtils();
            String kzm = imagePath.substring( imagePath.lastIndexOf( "." ) );
            String new_name = con.CSQI( "400", cookie, "000" ) + kzm;
            ftpUtils.uploadFile( "/home/test/images", new_name, imagePath );  //上传图片
            String reply = con.CSQI( "401", cookie, new_name );
            if (reply.equals( "200#" )) // 差旅快速报销
            {
                Toast.makeText( getContext (), "快速报销成功", Toast.LENGTH_SHORT ).show();
            } else {
                //char[] fault =reply.substring(3,reply.length()).toCharArray();
                Toast.makeText( getContext (), "报销失败", Toast.LENGTH_SHORT ).show();
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

    private void BtnAddBudget_clicked() {
        final Dialog dialog = new Dialog( getContext() );
        dialog.setContentView( R.layout.dialog );
        dialog.show();

        EtTravelAdd = dialog.findViewById( R.id.路费追加 );
        EtAccommodationAdd = dialog.findViewById( R.id.住宿费追加 );
        EtFoodAdd = dialog.findViewById( R.id.餐饮费追加 );
        EtTrafficAdd = dialog.findViewById( R.id.交通费追加 );
        //追加预算提交按钮
        Button btnABSubmit = dialog.findViewById ( R.id.Btn提交追加 );
        //追加预算取消按钮
        Button btnABCancel = dialog.findViewById ( R.id.Btn取消追加 );

        btnABSubmit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EtTravelAdd.getText().toString().equals( "" ) ||
                        EtAccommodationAdd.getText().toString().equals( "" ) ||
                        EtFoodAdd.getText().toString().equals( "" ) ||
                        EtTrafficAdd.getText().toString().equals( "" )) {
                    Toast.makeText( getContext(), "提交失败,追加费用不能为空", Toast.LENGTH_LONG ).show();
                } else {
                    String command = "#" + EtTravelAdd.getText().toString() +
                            "#" + EtAccommodationAdd.getText().toString() +
                            "#" + EtFoodAdd.getText().toString() +
                            "#" + EtTrafficAdd.getText().toString() + "#";
                    reply = con.CSQI( "222", cookie, command );
                    Toast.makeText( getContext(), "提交成功", Toast.LENGTH_SHORT ).show();
                    dialog.dismiss();
                }
            }
        } );
        btnABCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );
    }

    private void BtnOverJourney_clicked() {
        if (con.CSQI( "216", cookie, "000" ).substring( 0, 3 ).equals( "200" )) {
            Toast.makeText( getContext(), "已成功结束本次申请！", Toast.LENGTH_SHORT ).show();
            Intent intent = new Intent( getContext(), AfterEntering.class );
            budget_status = 0;
            intent.putExtra( "BudgetStatus", budget_status );
            intent.putExtra( "cookie", cookie );
            startActivity( intent );
        } else
            Toast.makeText( getContext(), "未能结束本次申请！", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Btn路费支付方式:
                Travel_clicked();
                break;
            case R.id.Btn住宿支付方式:
                Accommodation_clicked();
                break;
            case R.id.Btn提交申请:
                BtnRefer_clicked();
                break;
            case R.id.Btn撤销申请:
                BtnCancel_clicked();
                break;
            case R.id.Btn快速报销:
                BtnQuickReimbursement_clicked();
                break;
            case R.id.Btn追加预算:
                BtnAddBudget_clicked();
                break;
            case R.id.Btn结束行程:
                BtnOverJourney_clicked();
                break;
        }
    }

    private void SetWidget(){
        EtFrom = view.findViewById( R.id.Et始发地 );
        EtTo = view.findViewById( R.id.Et目的地 );
        EtTravelCost = view.findViewById( R.id.Et路费预算 );
        EtAccommodationCost = view.findViewById( R.id.Et住宿预算 );
        EtFoodBudget = view.findViewById( R.id.Et餐饮预算 );
        EtTrafficBudget = view.findViewById( R.id.Et交通预算 );
        EtNote = view.findViewById( R.id.Et出差目的 );

        BtnTravelChoice = view.findViewById( R.id.Btn路费支付方式 );
        BtnAccommodationChoice = view.findViewById( R.id.Btn住宿支付方式 );
        BtnCancel = view.findViewById( R.id.Btn撤销申请 );
        BtnRefer = view.findViewById( R.id.Btn提交申请 );
        BtnQuickReimbursement = view.findViewById( R.id.Btn快速报销 );
        BtnOverJourney = view.findViewById( R.id.Btn结束行程 );
        BtnAddBudget = view.findViewById( R.id.Btn追加预算 );

        BtnGroup1 = view.findViewById( R.id.ButtonGroup1 );
        BtnGroup2 = view.findViewById( R.id.ButtonGroup2 );

        dateFrom = view.findViewById( R.id.Datefrom );
        dateTo = view.findViewById( R.id.Dateto );

        back=view.findViewById( R.id.back );
        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        } );
    }

    private void SetTimeChoose(){
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get( Calendar.YEAR );
        final int month = calendar.get( Calendar.MONTH );
        final int day = calendar.get( Calendar.DAY_OF_MONTH );
        String time = year + "" + (month + 1) + "" + (day < 10 ? ("0" + day) : day);
        dateFrom.setText( time );
        dateTo.setText( time );
        dateFrom.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateFrom.setText( year + "" + (++month) + "" + (dayOfMonth < 10 ? ("0" + dayOfMonth) : dayOfMonth) );
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog( getContext (), DatePickerDialog.THEME_HOLO_LIGHT, listener, year, month, day );
                dialog.show();
            }
        } );
        dateTo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateTo.setText( year + "" + (++month) + "" + (dayOfMonth < 10 ? ("0" + dayOfMonth) : dayOfMonth) );
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog( getContext (), DatePickerDialog.THEME_HOLO_LIGHT, listener, year, month, day );
                dialog.show();
            }
        } );
    }

    private void Uploading(){
        BtnAccommodationChoice.setOnClickListener( this );
        BtnTravelChoice.setOnClickListener( this );
        BtnGroup1.setVisibility( View.VISIBLE );
        BtnGroup2.setVisibility( View.GONE );
        String current_budget = "000";  // 显示当已经提交的前预算信息
        current_budget = con.CSQI( "206", cookie, "000" );

        int pos = 1;
        int end = 1;
        String tmp = "000";
        for (int i = pos; i < current_budget.length(); i++)
            if (current_budget.charAt( i ) == '#') {
                end = i;
                break;
            }

        //EtDate.setEnabled( true );
        EtFrom.setEnabled( true );
        EtTo.setEnabled( true );
        EtTrafficBudget.setEnabled( true );
        EtAccommodationCost.setEnabled( true );
        EtFoodBudget.setEnabled( true );
        EtTravelCost.setEnabled( true );
        EtNote.setEnabled( true );

        tmp = current_budget.substring( pos, end );//System.out.println("出行日期:"+tmp);
        dateFrom.setText( tmp.substring( 0, 8 ) );
        dateTo.setText( tmp.substring( 9 ) );
        //EtDate.setText( tmp );

        pos = end + 1;

        for (int i = pos; i < current_budget.length(); i++)
            if (current_budget.charAt( i ) == '#') {
                end = i;
                break;
            }
        tmp = current_budget.substring( pos, end );//System.out.println("始发地:"+tmp);
        EtFrom.setText( tmp );
        pos = end + 1;

        for (int i = pos; i < current_budget.length(); i++)
            if (current_budget.charAt( i ) == '#') {
                end = i;
                break;
            }
        tmp = current_budget.substring( pos, end );//System.out.println("目的地:"+tmp);
        EtTo.setText( tmp );
        pos = end + 1;

        for (int i = pos; i < current_budget.length(); i++)
            if (current_budget.charAt( i ) == '#') {
                end = i;
                break;
            }
        tmp = current_budget.substring( pos, end );//System.out.println("路费申请选择"+tmp);
        //1自己购买2平台购买
        int i1 = Integer.parseInt( tmp );
        choice1 = i1;
        pos = end + 1;

        for (int i = pos; i < current_budget.length(); i++)
            if (current_budget.charAt( i ) == '#') {
                end = i;
                break;
            }
        tmp = current_budget.substring( pos, end );//System.out.println("路费预算:"+tmp);
        //1自己购买2平台购买
        if (i1 == 1) {
            EtTravelCost.setVisibility( View.VISIBLE );
            EtTravelCost.setText( tmp );
        }
        pos = end + 1;

        for (int i = pos; i < current_budget.length(); i++)
            if (current_budget.charAt( i ) == '#') {
                end = i;
                break;
            }
        tmp = current_budget.substring( pos, end );//System.out.println("住宿申请选择:"+tmp);
        int i2 = Integer.parseInt( tmp );
        choice2 = i2;
        pos = end + 1;

        for (int i = pos; i < current_budget.length(); i++)
            if (current_budget.charAt( i ) == '#') {
                end = i;
                break;
            }
        tmp = current_budget.substring( pos, end );//System.out.println("住宿预算:"+tmp);
        if (i2 == 1) {
            EtAccommodationCost.setVisibility( View.VISIBLE );
            EtAccommodationCost.setText( tmp );
        }
        pos = end + 1;

        for (int i = pos; i < current_budget.length(); i++)
            if (current_budget.charAt( i ) == '#') {
                end = i;
                break;
            }
        tmp = current_budget.substring( pos, end );//System.out.println("餐饮预算:"+tmp);
        EtFoodBudget.setText( tmp );
        pos = end + 1;

        for (int i = pos; i < current_budget.length(); i++)
            if (current_budget.charAt( i ) == '#') {
                end = i;
                break;
            }
        tmp = current_budget.substring( pos, end );//System.out.println("交通预算:"+tmp);
        EtTrafficBudget.setText( tmp );
        pos = end + 1;

        for (int i = pos; i < current_budget.length(); i++)
            if (current_budget.charAt( i ) == '#') {
                end = i;
                break;
            }
        tmp = current_budget.substring( pos, end );//System.out.println("出行理由:"+tmp);
        EtNote.setText( tmp );
        pos = end + 1;
        //System.out.println();  // 到这里是刚刚打开这个修改界面的显示之前已经提交过的内容
    }

    private void Uploaded(){
        BtnGroup1.setVisibility( View.GONE );
        BtnGroup2.setVisibility( View.VISIBLE );

        String current_budget = "000";  // 显示当已经提交的前预算信息
        current_budget = con.CSQI( "215", cookie, "000" );
        boolean query215 = true;
        if (query215) {
            List<String> data = new ArrayList<>();
            int p = current_budget.length() - 1, q = p;
            String temp;
            while (p >= 0 && current_budget.charAt( p ) == '#') p--;

            while (p >= 0) {
                q = p;
                while (p >= 0 && current_budget.charAt( p ) != '#')
                    p--;
                temp = current_budget.substring( p + 1, q + 1 );
                data.add( temp );
                p--;
            }

            dateFrom.setEnabled( false );
            dateTo.setEnabled( false );
            EtTravelCost.setVisibility( View.VISIBLE );
            EtAccommodationCost.setVisibility( View.VISIBLE );
            //EtDate.setEnabled( false );
            EtFrom.setEnabled( false );
            EtTo.setEnabled( false );
            EtTrafficBudget.setEnabled( false );
            EtAccommodationCost.setEnabled( false );
            EtFoodBudget.setEnabled( false );
            EtTravelCost.setEnabled( false );
            EtNote.setEnabled( false );

            dateFrom.setText( data.get( 9 ).substring( 0, 8 ) );
            dateTo.setText( data.get( 9 ).substring( 9 ) );
            EtFrom.setText( data.get( 8 ) );
            EtTo.setText( data.get( 7 ) );
            if (data.get( 6 ).equals( "0" ))
                BtnTravelChoice.setText( "路费(平台购买)" );
            else
                BtnTravelChoice.setText( "路费(自行购买)" );
            EtTrafficBudget.setText( data.get( 5 ) );
            if (data.get( 4 ).equals( "0" ))
                BtnAccommodationChoice.setText( "住宿费(平台购买)" );
            else
                BtnAccommodationChoice.setText( "住宿费(自行购买)" );
            EtAccommodationCost.setText( data.get( 3 ) );
            EtFoodBudget.setText( data.get( 2 ) );
            EtTravelCost.setText( data.get( 1 ) );
            EtNote.setText( data.get( 0 ) );
        }
    }

}
