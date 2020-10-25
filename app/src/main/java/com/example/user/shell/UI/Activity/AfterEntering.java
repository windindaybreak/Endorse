package com.example.user.shell.UI.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.ViewDragHelper;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.example.user.shell.Bean.CakeValue;
import com.example.user.shell.Connection;
import com.example.user.shell.MyAdapter;
import com.example.user.shell.View.CakeSurfaceView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*登录之后的界面*/
public class AfterEntering extends AppCompatActivity implements View.OnClickListener{

    private CakeSurfaceView cakeSurfaceView ;

    private LinearLayout layout_now,dialog_layout_now;

    public static Activity mActivity = null;

    private DrawerLayout mdrawerLayout;

    private ArrayList<Fragment> mFragmentList = new ArrayList <Fragment>();

    String history = "000";
    Connection con = new Connection();
    String reply = "000";
    List<Map<String, Object>> list = new ArrayList<>();
    LinearLayout huochepiao;
    LinearLayout feijipiao;
    LinearLayout jiudian;

    Dialog dialog;

    MyAdapter myAdapter;

    private ListView listview;
    public int budget_status = 0; // 0  未申请 1 待审核   2已审核 3 过期
    public int cookie = 10000;
    TextView authorized;//待还款
    TextView unauthorized;//未报销
    TextView company_name;
    //TextView balance;//余额
    Spinner bill_choice;//历史单据的下拉栏
    LinearLayout btn_ChaiLvFei, btn_ZhaoDaiFei, btnJiaoTongFei, btn_CheLiangFei, btn_PeiXunFei,
            btn_CaiGouFei, btn_ZiXunFei, btn_ZuLinFei, btn_YunFei;//费用申请按钮(现在是用一个个小布局代替原来的按钮）
    TextView unsubmit_num, unauthorized_num, authorized_num;
    LinearLayout my_company, my_card, my_bill, my_switch, my_addrebook,
            my_zhuangtai, my_yue, my_setting,my_help;
    ImageView my_head;
    TextView person_name, person_position, person_point;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.主页:
                    if (!Objects.equals(layout_now, findViewById(R.id.layout_主页 ))) {
                        layout_now.setVisibility(View.GONE);
                        layout_now = findViewById(R.id.layout_主页 );
                        layout_now.setVisibility(View.VISIBLE);
                    }
                    return true;
                case R.id.合作企业:
                    if (!Objects.equals(layout_now, findViewById(R.id.layout_合作企业))) {
                        layout_now.setVisibility(View.GONE);
                        layout_now = findViewById(R.id.layout_合作企业);
                        layout_now.setVisibility(View.VISIBLE);
                    }
                    return true;
                case R.id.账单:
                    if (!Objects.equals(layout_now, findViewById(R.id.layout_账单 ))) {
                        layout_now.setVisibility(View.GONE);
                        layout_now = findViewById(R.id.layout_账单 );
                        layout_now.setVisibility(View.VISIBLE);
                    }
                    intiData();
                    return true;
                case R.id.我的:
                    if (!Objects.equals(layout_now, findViewById(R.id.layout_我的))) {
                        layout_now.setVisibility(View.GONE);
                        layout_now = findViewById(R.id.layout_我的);
                        layout_now.setVisibility(View.VISIBLE);
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data.getIntExtra("cookie", 10000) != cookie) {
                cookie = data.getIntExtra("cookie", 10000);
                setBill_choice();
                setnum_status();
                setPersonInfo();
                String CompanyName = "404NotFound";
                CompanyName = con.CSQI("202", cookie, "000");
                company_name.setText(CompanyName);
                reply = con.CSQI("412", cookie, "000");
                System.out.println("额度：" + reply.substring(0, reply.length() - 1));
                unauthorized.setText(reply.substring(0, reply.length() - 1));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_entering);
        cookie = getIntent().getIntExtra("cookie", 10000);
        budget_status = getIntent().getIntExtra("BudgetStatus", 0);
        mActivity = this;

        cakeSurfaceView = findViewById(R.id.assets_pie_chart);

        mdrawerLayout= findViewById(R.id.drawer);
        setDrawerLeftEdgeSize(this, mdrawerLayout, 0.6f);





        dialog = new Dialog ( AfterEntering.this );//账单详情弹窗
        dialog.setContentView ( R.layout.dialog_detailed_bill );
        dialog_layout_now = dialog.findViewById ( R.id.差旅申请详情 );

        {
            layout_now = findViewById ( R.id.layout_主页 );
            company_name = findViewById ( R.id.company );
            bill_choice = findViewById ( R.id.Sp历史账单种类 );
            authorized = findViewById ( R.id.待还款金额 );
            unauthorized = findViewById ( R.id.未报销数字 );
            unsubmit_num = findViewById ( R.id.AE未提交 );
            unauthorized_num = findViewById ( R.id.AE未审核 );
            authorized_num = findViewById ( R.id.AE已审核 );

            btn_ChaiLvFei = findViewById ( R.id.btn_差旅费申请 );
            btn_ZhaoDaiFei = findViewById ( R.id.btn_招待费申请 );
            btnJiaoTongFei = findViewById ( R.id.btn_交通费申请 );
            btn_CheLiangFei = findViewById ( R.id.btn_车辆费申请 );
            btn_PeiXunFei = findViewById ( R.id.btn_培训费申请 );
            btn_CaiGouFei = findViewById ( R.id.btn_采购费申请 );
            btn_ZiXunFei = findViewById ( R.id.btn_咨询费申请 );
            btn_ZuLinFei = findViewById ( R.id.btn_租赁费申请 );
            btn_YunFei = findViewById ( R.id.btn_运费申请 );

            huochepiao = findViewById ( R.id.layout火车票 );
            setHuoChepiao ( );
            feijipiao = findViewById ( R.id.layout机票 );
            setFeiJiPiao ( );
            jiudian = findViewById ( R.id.layout酒店 );
            setJiuDian ( );
        }

        {
            btn_ChaiLvFei.setOnClickListener ( this );
            btn_ZhaoDaiFei.setOnClickListener ( this );
            btnJiaoTongFei.setOnClickListener ( this );
            btn_CheLiangFei.setOnClickListener ( this );
            btn_PeiXunFei.setOnClickListener ( this );
            btn_CaiGouFei.setOnClickListener ( this );
            btn_ZiXunFei.setOnClickListener ( this );
            btn_ZuLinFei.setOnClickListener ( this );
            btn_YunFei.setOnClickListener ( this );
        }

        {
            my_company = findViewById(R.id.我的企业intent);
            my_card = findViewById(R.id.我的银行卡intent);
            my_bill = findViewById(R.id.开票信息intent);
            my_head = findViewById(R.id.我的头像);
            my_switch = findViewById(R.id.切换公司intent);
            my_addrebook = findViewById(R.id.企业通讯簿intent);
            my_zhuangtai = findViewById(R.id.申请状态intent);
            my_yue = findViewById(R.id.我的零钱intent);
            my_help=findViewById( R.id.帮助和反馈intent );
        }
        setnum_status();
        setMy_company();
        setMy_card();
        setMy_bill();
        setMy_head();
        setMy_switch();
        setMy_addrebook();
        setMy_zhuangtai();
        setMy_yue();
        setMy_help();

        person_name = findViewById(R.id.我的姓名);
        person_position = findViewById(R.id.我的职位);
        person_point = findViewById(R.id.我的信誉权重分);
        setPersonInfo();

        my_setting = findViewById(R.id.设置intent);
        setMy_setting();

        setBill_choice();//设置账单历史种类下拉栏

        Banner ();//轮播图

        //显示公司信息
        String CompanyName = "404NotFound";
        CompanyName = con.CSQI("202", cookie, "000");
        company_name.setText(CompanyName);

        reply = con.CSQI("412", cookie, "000");
        System.out.println("额度：" + reply.substring(0, reply.length() - 1));
        unauthorized.setText(reply.substring(0, reply.length() - 1) + "￥");

        //显示额度
        String Quota = "0￥";
        //Quota = con.CSQI( "203", cookie, "000" );
        //authorized.setText( Quota.substring( 1 ) + "￥" );
        authorized.setText(Quota);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                             Object mHelperUtils;
                             Toast.makeText(this, "再按一次退出APP", Toast.LENGTH_SHORT).show();
                             //System.currentTimeMillis()系统当前时间
                            mExitTime = System.currentTimeMillis();
                         } else {
                             finish();
                         }
                     return true;
                 }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("SetTextI18n")
    private void setPersonInfo() {
        reply = con.CSQI("609", cookie, "000");
        if (reply.substring(0, 3).equals("400")) {
            Toast.makeText(AfterEntering.this, "获取信息错误", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("姓名：" + reply.substring(0, reply.lastIndexOf('#')));
            System.out.println("职务：" + reply.substring(reply.lastIndexOf('#') + 1, reply.length()));
            person_name.setText("姓名：" + reply.substring(0, reply.lastIndexOf('#')));
            person_position.setText("职务：" + reply.substring(reply.lastIndexOf('#') + 1, reply.length()));
        }
        reply = con.CSQI("610", cookie, "000");
        person_point.setText("信誉积分：" + reply.substring(0, reply.length() - 1));
        System.out.println("信誉积分：" + reply.substring(0, reply.length() - 1));
    }

    private void setMy_help(){
        my_help.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent( AfterEntering.this, MyHelp.class );
                intent.putExtra( "cookie",cookie );
                startActivity( intent );
            }
        } );
    }

    //设置历史单据下拉栏
    private void setBill_choice() {
        List<String> list = new ArrayList<>();
        list.add("差旅费申请");
        list.add("招待费申请");
        list.add("交通费申请");
        list.add("车辆费申请");
        list.add("培训费申请");
        list.add("采购费申请");
        list.add("咨询费申请");
        list.add("租赁费申请");
        list.add("运费申请");//为List填充数据
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AfterEntering.this,
                android.R.layout.simple_list_item_1, list);
        final ListView listView = new ListView(AfterEntering.this);
        listView.setAdapter(adapter);
        bill_choice.setAdapter((SpinnerAdapter) listView.getAdapter());
        bill_choice.setOnItemSelectedListener( detailed_onItemSelectedListener );
    }

    private void setMy_company() {
        my_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply = con.CSQI("603", cookie, "000");
                if (reply.substring(0, 3).equals("400")) {
                    Toast.makeText(AfterEntering.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AfterEntering.this, CompanyInfo.class);
                    intent.putExtra("cookie", cookie);
                    intent.putExtra("BudgetStatus", budget_status);
                    startActivity(intent);
                }

            }
        });
    }

    private void setJiuDian() {
        jiudian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AfterEntering.this, Hotel.class);
                i.putExtra("cookie", cookie);
                startActivity(i);
            }
        });
    }

    private void setHuoChepiao() {
        huochepiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AfterEntering.this, Train.class);
                i.putExtra("cookie", cookie);
                startActivity(i);
            }
        });
    }

    private void setFeiJiPiao() {
        feijipiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AfterEntering.this, Plane.class);
                i.putExtra("cookie", cookie);
                startActivity(i);
            }
        });
    }

    private void setMy_card() {
        my_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterEntering.this, CardInfo.class);
                intent.putExtra("cookie", cookie);
                startActivity(intent);
            }
        });
    }

    private void setMy_bill() {
        my_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply = con.CSQI("604", cookie, "000");
                if (reply.substring(0, 3).equals("400"))
                    Toast.makeText(AfterEntering.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(AfterEntering.this, BillInfo.class);
                    intent.putExtra("cookie", cookie);
                    startActivity(intent);
                }
            }
        });
    }

    private void setMy_head() {
        my_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply = con.CSQI("603", cookie, "000");
                if (reply.substring(0, 3).equals("400")) {
                    Toast.makeText(AfterEntering.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AfterEntering.this, PersonInfo.class);
                    intent.putExtra("cookie", cookie);
                    startActivity(intent);
                }
            }
        });
    }

    private void setMy_switch() {
        my_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterEntering.this, ChangeCompany.class);
                intent.putExtra("cookie", cookie);
                startActivityForResult(intent, 5);
            }
        });
    }

    private void setMy_addrebook() {
        my_addrebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterEntering.this, AddrebookInfo.class);
                intent.putExtra("cookie", cookie);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setnum_status() {
        reply = con.CSQI("411", cookie, "000");
        int[] num = new int[3];
        for (int i = 0; i < reply.length(); i++)
            if (reply.charAt(i) != '#') num[reply.charAt(i) - '0']++;
        unsubmit_num.setText("未提交:" + num[0]);
        unauthorized_num.setText("未审核:" + num[1]);
        authorized_num.setText("已审核:" + num[2]);
    }

    private void setMy_zhuangtai() {
        my_zhuangtai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply = con.CSQI("411", cookie, "000");
                if (reply.substring(0, 3).equals("400")) {
                    Toast.makeText(AfterEntering.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AfterEntering.this, zhuangtai.class);
                    intent.putExtra("cookie", cookie);
                    startActivity(intent);
                }
            }
        });
    }

    private void setMy_yue() {
        my_yue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterEntering.this, RemainAcount.class);
                intent.putExtra("cookie", cookie);
                startActivity(intent);
            }
        });
    }

    private void setMy_setting() {
        my_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterEntering.this, Setting.class);
                intent.putExtra("cookie", cookie);
                startActivity(intent);
            }
        });
    }

    private void Banner() {
        ConvenientBanner banner1 = findViewById ( R.id.合作企业广告 );
        ConvenientBanner banner2 = findViewById ( R.id.预算申请广告 );
        ArrayList<Integer> data = new ArrayList<> ( );
        data.add ( R.drawable.advertisement1 );
        data.add ( R.drawable.advertisement2 );
        data.add ( R.drawable.advertisement3 );
        data.add ( R.drawable.advertisement4 );

        banner1.setPages(
                new CBViewHolderCreator () {
                    @Override
                    public LocalImageHolderView createHolder(View itemView) {
                        return new LocalImageHolderView(itemView);
                    }

                    @Override
                    public int getLayoutId() {
                        return R.layout.item_localimage;
                    }
                }, data)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{ R.drawable.ic_page_indicator_focused,R.drawable.ic_page_indicator,R.drawable.ic_page_indicator,R.drawable.ic_page_indicator})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                //开始轮播,4s一换
                .startTurning (4000);
        banner2.setPages(
                new CBViewHolderCreator () {
                    @Override
                    public LocalImageHolderView createHolder(View itemView) {
                        return new LocalImageHolderView(itemView);
                    }

                    @Override
                    public int getLayoutId() {
                        return R.layout.item_localimage;
                    }
                }, data)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{ R.drawable.ic_page_indicator_focused,R.drawable.ic_page_indicator,R.drawable.ic_page_indicator,R.drawable.ic_page_indicator})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                //开始轮播,4s一换
                .startTurning (4000);
    }

    public class LocalImageHolderView extends Holder<Integer> {
        private ImageView imageView;

        public LocalImageHolderView(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView(View itemView) {
            imageView =itemView.findViewById(R.id.iv_banner);
            imageView.setScaleType ( ImageView.ScaleType.FIT_XY );
        }

        @Override
        public void updateUI(Integer data) {
            imageView.setImageResource(data);
        }
    }

    @Override
    public void onClick(View v) {
        Connection con = new Connection();

        switch (v.getId()) {
            case R.id.btn_差旅费申请:
                budget_status = Integer.parseInt(con.CSQI("211", cookie, "000"));
                Intent intent = new Intent(AfterEntering.this, FragmentRequest.class);
                intent.putExtra ( "type", 0);
                intent.putExtra("cookie", cookie);
                intent.putExtra("BudgetStatus", budget_status);
                startActivity(intent);
                break;
            case R.id.btn_招待费申请:
                budget_status = Integer.parseInt(con.CSQI("212", cookie, "000"));
                Intent intent1 = new Intent(AfterEntering.this, FragmentRequest.class);
                intent1.putExtra ( "type",1 );
                intent1.putExtra("cookie", cookie);
                intent1.putExtra("BudgetStatus", budget_status);
                startActivity(intent1);
                break;
            case R.id.btn_交通费申请:
                budget_status = Integer.parseInt(con.CSQI("220", cookie, "000"));
                Intent intent2 = new Intent(AfterEntering.this, FragmentRequest.class);
                intent2.putExtra ( "type",2 );
                intent2.putExtra("cookie", cookie);
                intent2.putExtra("BudgetStatus", budget_status);
                startActivity(intent2);
                break;
            case R.id.btn_车辆费申请:
                budget_status = Integer.parseInt(con.CSQI("228", cookie, "000"));
                Intent intent3 = new Intent(AfterEntering.this, FragmentRequest.class);
                intent3.putExtra ( "type", 3);
                intent3.putExtra("cookie", cookie);
                intent3.putExtra("BudgetStatus", budget_status);
                startActivity(intent3);
                break;
            case R.id.btn_培训费申请:
                budget_status = Integer.parseInt(con.CSQI("235", cookie, "000"));
                Intent intent4 = new Intent(AfterEntering.this, FragmentRequest.class);
                intent4.putExtra ( "type", 4);
                intent4.putExtra("cookie", cookie);
                intent4.putExtra("BudgetStatus", budget_status);
                startActivity(intent4);
                break;
            case R.id.btn_租赁费申请:
                budget_status = Integer.parseInt(con.CSQI("251", cookie, "000"));
                Intent intent5 = new Intent(AfterEntering.this, FragmentRequest.class);
                intent5.putExtra ( "type",5 );
                intent5.putExtra("cookie", cookie);
                intent5.putExtra("BudgetStatus", budget_status);
                startActivity(intent5);
                break;
            case R.id.btn_咨询费申请:
                budget_status = Integer.parseInt(con.CSQI("243", cookie, "000"));
                Intent intent6 = new Intent(AfterEntering.this, FragmentRequest.class);
                intent6.putExtra ( "type", 6);
                intent6.putExtra("cookie", cookie);
                intent6.putExtra("BudgetStatus", budget_status);
                startActivity(intent6);
                break;
            case R.id.btn_运费申请:
                budget_status = Integer.parseInt(con.CSQI("259", cookie, "000"));
                Intent intent7 = new Intent(AfterEntering.this, FragmentRequest.class);
                intent7.putExtra ( "type", 7);
                intent7.putExtra("cookie", cookie);
                intent7.putExtra("BudgetStatus", budget_status);
                startActivity(intent7);
                break;
            case R.id.btn_采购费申请:
                budget_status = Integer.parseInt(con.CSQI("267", cookie, "000"));
                Intent intent8 = new Intent(AfterEntering.this, FragmentRequest.class);
                intent8.putExtra ( "type", 8);
                intent8.putExtra("cookie", cookie);
                intent8.putExtra("BudgetStatus", budget_status);
                startActivity(intent8);
                break;
        }
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        System.out.println ( "After_cookie:"+cookie );
        super.onWindowFocusChanged(hasFocus);
        reply = con.CSQI("501", cookie, "000");
        if (reply.substring(0, 3).equals("400")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AfterEntering.this);
            builder.setTitle("重复登陆");
            builder.setMessage("该账号在其他地方登陆，请重新登录。");
            builder.setPositiveButton("重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(AfterEntering.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            builder.show();
        }
    }

    AdapterView.OnItemSelectedListener detailed_onItemSelectedListener = new AdapterView.OnItemSelectedListener ( ) {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            list.clear ();
            //添加数据源
            switch (position) {
                case 0:
                    history = con.CSQI("204", cookie, "000");
                    int cnt = 0, pos = 0;
                    for (int i = history.length() - 1; i >= 0; i--)
                        if (history.charAt(i) == '#') {
                            pos = i;
                            break;
                        }
                    for (int i = pos + 1; i < history.length(); i++)
                        cnt = cnt * 10 + history.charAt(i) - '0';
                    pos = 1;
                    int end = 1;
                    for (int j = 1; j <= cnt; j++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        for (int i = pos; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                end = i;
                                break;
                            }
                        map.put("开始日期", "出行日期:" + history.substring(pos, end));
                        pos = end + 1;

                        for (int i = pos; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                end = i;
                                break;
                            }
                        map.put("始发地", "始发地:" + history.substring(pos, end));
                        pos = end + 1;

                        for (int i = pos; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                end = i;
                                break;
                            }
                        map.put("目的地", "目的地:" + history.substring(pos, end));
                        pos = end + 1;

                        for (int i = pos; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                end = i;
                                break;
                            }
                        map.put("路费开销", "路费开销:" + history.substring(pos, end));
                        pos = end + 1;

                        for (int i = pos; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                end = i;
                                break;
                            }
                        map.put("住宿开销", "住宿开销:" + history.substring(pos, end));
                        pos = end + 1;

                        for (int i = pos; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                end = i;
                                break;
                            }
                        map.put("餐饮开销", "餐饮开销:" + history.substring(pos, end));
                        pos = end + 1;

                        for (int i = pos; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                end = i;
                                break;
                            }
                        map.put("交通开销", "交通开销:" + history.substring(pos, end));
                        pos = end + 1;

                        list.add(map);
                    }
                    System.out.println ( "history0"+history );
                    break;
                case 1:
                    history = con.CSQI("208", cookie, "000");
                    boolean query208 = true;
                    if (query208) {
                        ArrayList[] data = new ArrayList[50005];
                        int num, dataID = 0, p = history.length() - 1, q = p;
                        String temp;
                        while (p >= 0 && history.charAt(p) != '#') p--;
                        temp = history.substring(p + 1, q + 1);
                        p--;
                        num = Integer.parseInt(temp);
                        dataID = num;

                        while (dataID >= 0 && p >= 0) {
                            data[dataID] = new ArrayList<>();
                            for (int i = 0; i < 10; i++) {
                                q = p;
                                while (p >= 0 && history.charAt(p) != '#') p--;
                                temp = history.substring(p + 1, q + 1);
                                data[dataID].add(temp);
                                p--;
                            }
                            dataID--;
                        }
                        for (int i = 1; i <= num; i++) {
                            List<String> arr = data[i];
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("接待时间", "接待时间:" + arr.get(9));
                            map.put("接待地点", "接待地点:" + arr.get(8));
                            map.put("接待内容", "接待内容:" + arr.get(7));
                            map.put("级别", "来访人员待遇级别:" + arr.get(6));
                            map.put("人数", "人数:" + arr.get(5));
                            map.put("租车费", "租车费:" + arr.get(4));
                            map.put("住宿费", "住宿费:" + arr.get(3));
                            map.put("餐饮费", "餐饮费:" + arr.get(2));
                            map.put("总共开销", "总共开销:" + arr.get(1));
                            map.put("备注", "备注:" + arr.get(0));

                            list.add(map);
                        }
                    }
                    System.out.println ( "history1"+history );
                    break;
                case 2:
                    history = con.CSQI("218", cookie, "000");
                    int num2 = Integer.parseInt(history.substring(history.lastIndexOf('#') + 1));
                    int l = 1, r = 0;
                    for (int i = 0; i < num2; i++) {
                        Map<String, Object> map = new HashMap<>();

                        for (int j = l; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r = j;
                                break;
                            }
                        map.put("出行日期", "出行日期:" + history.substring(l, r));
                        l = r + 1;

                        for (int j = l; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r = j;
                                break;
                            }
                        map.put("出行理由", "出行理由:" + history.substring(l, r));
                        l = r + 1;

                        for (int j = l; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r = j;
                                break;
                            }
                        map.put("实际花销", "实际花销:" + history.substring(l, r));
                        l = r + 1;

                        for (int j = l; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r = j;
                                break;
                            }
                        map.put("出行备注", "出行备注:" + history.substring(l, r));
                        l = r + 1;

                        list.add(map);
                    }
                    System.out.println ( "history2"+history );
                    break;
                case 3:
                    history = con.CSQI("227", cookie, "000");
                    int num3 = Integer.parseInt(history.substring(history.lastIndexOf('#') + 1, history.length()));
                    int l3 = 1, r3 = 1;
                    for (int k = 0; k < num3; k++) {
                        Map<String, Object> map = new HashMap<>();
                        for (int i = l3; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                r3 = i;
                                break;
                            }
                        map.put("始发地", "始发地:" + history.substring(l3, r3));
                        l3 = r3 + 1;
                        for (int i = l3; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                r3 = i;
                                break;
                            }
                        map.put("目的地", "目的地:" + history.substring(l3, r3));
                        l3 = r3 + 1;
                        for (int i = l3; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                r3 = i;
                                break;
                            }
                        map.put("理由", "理由:" + history.substring(l3, r3));
                        l3 = r3 + 1;
                        for (int i = l3; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                r3 = i;
                                break;
                            }
                        map.put("实际花销", "实际花销:" + history.substring(l3, r3));
                        l3 = r3 + 1;
                        for (int i = l3; i < history.length(); i++)
                            if (history.charAt(i) == '#') {
                                r3 = i;
                                break;
                            }
                        map.put("补充说明", "补充说明:" + history.substring(l3, r3));
                        l3 = r3 + 1;

                        list.add(map);
                    }
                    System.out.println ( "history3"+history );
                    break;
                case 4:
                    history = con.CSQI("234", cookie, "000");
                    int num = Integer.parseInt(history.substring(history.lastIndexOf('#') + 1));
                    int l4 = 1, r4 = 1;
                    for (int i = 0; i < num; i++) {
                        Map<String, Object> map = new HashMap<>();
                        for (int j = l4; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r4 = j;
                                break;
                            }
                        map.put("培训机构名称", "培训机构名称:" + history.substring(l4, r4));
                        l4 = r4 + 1;
                        for (int j = l4; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r4 = j;
                                break;
                            }
                        map.put("培训日期", "培训日期:" + history.substring(l4, r4));
                        l4 = r4 + 1;
                        for (int j = l4; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r4 = j;
                                break;
                            }
                        map.put("授课方式", "授课方式:" + history.substring(l4, r4));
                        l4 = r4 + 1;
                        for (int j = l4; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r4 = j;
                                break;
                            }
                        map.put("授课地点", "授课地点:" + history.substring(l4, r4));
                        l4 = r4 + 1;
                        for (int j = l4; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r4 = j;
                                break;
                            }
                        map.put("培训预算", "培训预算:" + history.substring(l4, r4));
                        l4 = r4 + 1;
                        for (int j = l4; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r4 = j;
                                break;
                            }
                        map.put("培训内容", "培训内容:" + history.substring(l4, r4));
                        l4 = r4 + 1;
                        for (int j = l4; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r4 = j;
                                break;
                            }
                        map.put("考核方式", "考核方式:" + history.substring(l4, r4));
                        l4 = r4 + 1;
                        for (int j = l4; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r4 = j;
                                break;
                            }
                        map.put("备注", "备注:" + history.substring(l4, r4));
                        l4 = r4 + 1;

                        list.add(map);
                    }
                    System.out.println ( "history4"+history );
                    break;
                case 5:
                    history = con.CSQI("266", cookie, "000");
                    int num5 = Integer.parseInt(history.substring(history.lastIndexOf('#') + 1));
                    int l5 = 1, r5 = 1;
                    for (int i = 0; i < num5; i++) {
                        Map<String, Object> map = new HashMap<>();
                        for (int j = l5; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r5 = j;
                                break;
                            }
                        map.put("采购事由", "采购事由:" + history.substring(l5, r5));
                        l5 = r5 + 1;
                        for (int j = l5; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r5 = j;
                                break;
                            }
                        map.put("采购日期", "采购日期:" + history.substring(l5, r5));
                        l5 = r5 + 1;
                        for (int j = l5; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r5 = j;
                                break;
                            }
                        map.put("采购公司", "采购公司:" + history.substring(l5, r5));
                        l5 = r5 + 1;
                        for (int j = l5; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r5 = j;
                                break;
                            }
                        map.put("商品名称", "商品名称:" + history.substring(l5, r5));
                        l5 = r5 + 1;
                        for (int j = l5; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r5 = j;
                                break;
                            }
                        map.put("商品数量", "商品数量:" + history.substring(l5, r5));
                        l5 = r5 + 1;
                        for (int j = l5; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r5 = j;
                                break;
                            }
                        map.put("商品单价", "商品单价:" + history.substring(l5, r5));
                        l5 = r5 + 1;
                        for (int j = l5; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r5 = j;
                                break;
                            }
                        map.put("商品总花销", "商品总花销:" + history.substring(l5, r5));
                        l5 = r5 + 1;
                        for (int j = l5; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r5 = j;
                                break;
                            }
                        map.put("备注", "备注:" + history.substring(l5, r5));
                        l5 = r5 + 1;

                        list.add(map);
                    }
                    System.out.println ( "history5"+history );
                    break;
                case 6:
                    history = con.CSQI("242", cookie, "000");
                    int num6 = Integer.parseInt(history.substring(history.lastIndexOf('#') + 1));
                    int l6 = 1, r6 = 1;
                    for (int i = 0; i < num6; i++) {
                        Map<String, Object> map = new HashMap<>();
                        for (int j = l6; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r6 = j;
                                break;
                            }
                        map.put("咨询日期", "咨询日期:" + history.substring(l6, r6));
                        l6 = r6 + 1;
                        for (int j = l6; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r6 = j;
                                break;
                            }
                        map.put("咨询原因", "咨询原因:" + history.substring(l6, r6));
                        l6 = r6 + 1;
                        for (int j = l6; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r6 = j;
                                break;
                            }
                        map.put("咨询公司", "咨询公司:" + history.substring(l6, r6));
                        l6 = r6 + 1;
                        for (int j = l6; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r6 = j;
                                break;
                            }
                        map.put("咨询费用", "咨询费用:" + history.substring(l6, r6));
                        l6 = r6 + 1;
                        for (int j = l6; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r6 = j;
                                break;
                            }
                        map.put("备注", "备注:" + history.substring(l6, r6));
                        l6 = r6 + 1;

                        list.add(map);
                    }
                    System.out.println ( "history6"+history );
                    break;
                case 7:
                    history = con.CSQI("250", cookie, "000");
                    int num7 = Integer.parseInt(history.substring(history.lastIndexOf('#') + 1));
                    int l7 = 1, r7 = 1;
                    for (int i = 0; i < num7; i++) {
                        Map<String, Object> map = new HashMap<>();
                        for (int j = l7; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r7 = j;
                                break;
                            }
                        map.put("租赁物品", "租赁物品:" + history.substring(l7, r7));
                        l7 = r7 + 1;
                        for (int j = l7; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r7 = j;
                                break;
                            }
                        map.put("租赁原因", "租赁原因:" + history.substring(l7, r7));
                        l7 = r7 + 1;
                        for (int j = l7; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r7 = j;
                                break;
                            }
                        map.put("租赁日期", "租赁日期:" + history.substring(l7, r7));
                        l7 = r7 + 1;
                        for (int j = l7; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r7 = j;
                                break;
                            }
                        map.put("租赁公司", "租赁公司:" + history.substring(l7, r7));
                        l7 = r7 + 1;
                        for (int j = l7; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r7 = j;
                                break;
                            }
                        map.put("租赁花销", "租赁花销:" + history.substring(l7, r7));
                        l7 = r7 + 1;
                        for (int j = l7; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r7 = j;
                                break;
                            }
                        map.put("备注", "备注:" + history.substring(l7, r7));
                        l7 = r7 + 1;

                        list.add(map);
                    }
                    System.out.println ( "history7"+history );
                    break;
                case 8:
                    history = con.CSQI("258", cookie, "000");
                    int num8 = Integer.parseInt(history.substring(history.lastIndexOf('#') + 1));
                    int l8 = 1, r8 = 1;
                    for (int i = 0; i < num8; i++) {
                        Map<String, Object> map = new HashMap<>();
                        for (int j = l8; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r8 = j;
                                break;
                            }
                        map.put("运输物品", "运输物品:" + history.substring(l8, r8));
                        l8 = r8 + 1;
                        for (int j = l8; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r8 = j;
                                break;
                            }
                        map.put("物品数量", "物品数量:" + history.substring(l8, r8));
                        l8 = r8 + 1;
                        for (int j = l8; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r8 = j;
                                break;
                            }
                        map.put("始发地", "始发地:" + history.substring(l8, r8));
                        l8 = r8 + 1;
                        for (int j = l8; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r8 = j;
                                break;
                            }
                        map.put("目的地", "目的地:" + history.substring(l8, r8));
                        l8 = r8 + 1;
                        for (int j = l8; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r8 = j;
                                break;
                            }
                        map.put("预计日期", "预计日期:" + history.substring(l8, r8));
                        l8 = r8 + 1;
                        for (int j = l8; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r8 = j;
                                break;
                            }
                        map.put("运输方式", "运输方式:" + history.substring(l8, r8));
                        l8 = r8 + 1;
                        for (int j = l8; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r8 = j;
                                break;
                            }
                        map.put("运输公司", "运输公司:" + history.substring(l8, r8));
                        l8 = r8 + 1;
                        for (int j = l8; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r8 = j;
                                break;
                            }
                        map.put("实际花销", "实际花销:" + history.substring(l8, r8));
                        l8 = r8 + 1;
                        for (int j = l8; j < history.length(); j++)
                            if (history.charAt(j) == '#') {
                                r8 = j;
                                break;
                            }
                        map.put("备注", "备注:" + history.substring(l8, r8));
                        l8 = r8 + 1;

                        list.add(map);
                    }
                    System.out.println ( "history8"+history );
                    break;
            }

            //拿到listview对象
            listview = findViewById(R.id.list_view);
            listview.setOnItemClickListener ( brief_onItemClickListener );

            //设置适配器
            myAdapter = new MyAdapter (AfterEntering.this, position);
            myAdapter.setList(list);
            //绑定适配器
            listview.setAdapter( myAdapter );
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //添加下拉栏被取消使得点击事件，一般为无
        }
    };
    AdapterView.OnItemClickListener brief_onItemClickListener = new AdapterView.OnItemClickListener ( ) {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            dialog_layout_now.setVisibility ( View.GONE );
            switch (myAdapter.flag){
                case 0:
                    dialog_layout_now = dialog.findViewById ( R.id.差旅申请详情 );
                    dialog_layout_now.setVisibility ( View.VISIBLE );
                    TextView BeginDate = dialog.findViewById( R.id.差旅账单详情出行日期 );
                    TextView BeginPlace = dialog.findViewById( R.id.差旅账单详情始发地 );
                    TextView EndPlace = dialog.findViewById( R.id.差旅账单详情目的地 );
                    TextView Travel = dialog.findViewById( R.id.差旅账单详情路费 );
                    TextView Accommodation = dialog.findViewById( R.id.差旅账单详情住宿 );
                    TextView Diet = dialog.findViewById( R.id.差旅账单详情餐饮 );
                    TextView Transportation = dialog.findViewById( R.id.差旅账单详情交通 );
                    BeginDate.setText ( (CharSequence) myAdapter.getItem ( position ).get ( "开始日期" ) );
                    BeginPlace.setText ( (CharSequence) myAdapter.getItem ( position ).get ( "始发地" ) );
                    EndPlace.setText ( (CharSequence) myAdapter.getItem ( position ).get ( "目的地" ) );
                    Travel.setText ( (CharSequence) myAdapter.getItem ( position ).get ( "路费开销" ) );
                    Accommodation.setText ( (CharSequence) myAdapter.getItem ( position ).get ( "住宿开销" ) );
                    Diet.setText ( (CharSequence) myAdapter.getItem ( position ).get ( "餐饮开销" ) );
                    Transportation.setText ( (CharSequence) myAdapter.getItem ( position ).get ( "交通开销" ) );
                    break;
                case 1:
                    dialog_layout_now = dialog.findViewById ( R.id.招待申请详情 );
                    dialog_layout_now.setVisibility ( View.VISIBLE );
                    TextView Level = dialog.findViewById( R.id.招待账单详情级别 );
                    TextView Num = dialog.findViewById( R.id.招待账单详情人数 );
                    TextView Time = dialog.findViewById( R.id.招待账单详情接待时间 );
                    TextView Place = dialog.findViewById( R.id.招待账单详情接待地点 );
                    TextView Reason = dialog.findViewById( R.id.招待账单详情招待内容 );
                    TextView Rent = dialog.findViewById( R.id.招待账单详情租车费 );
                    TextView Diet1 = dialog.findViewById( R.id.招待账单详情餐饮费 );
                    TextView Accommodation1 = dialog.findViewById( R.id.招待账单详情住宿费 );
                    TextView Total = dialog.findViewById( R.id.招待账单详情总共开销 );
                    TextView More = dialog.findViewById( R.id.招待账单详情备注 );
                    Time.setText( (CharSequence) myAdapter.getItem ( position ).get( "接待时间" ) );
                    Place.setText( (CharSequence) myAdapter.getItem ( position ).get( "接待地点" ) );
                    Reason.setText( (CharSequence) myAdapter.getItem ( position ).get( "接待内容" ) );
                    Level.setText( (CharSequence) myAdapter.getItem ( position ).get( "级别" ) );
                    Num.setText( (CharSequence) myAdapter.getItem ( position ).get( "人数" ) );
                    Rent.setText( (CharSequence) myAdapter.getItem ( position ).get( "租车费" ) );
                    Accommodation1.setText( (CharSequence) myAdapter.getItem ( position ).get( "住宿费" ) );
                    Diet1.setText( (CharSequence) myAdapter.getItem ( position ).get( "餐饮费" ) );
                    Total.setText( (CharSequence) myAdapter.getItem ( position ).get( "总共开销" ) );
                    More.setText( (CharSequence) myAdapter.getItem ( position ).get( "备注" ) );
                    break;
                case 2:
                    dialog_layout_now = dialog.findViewById ( R.id.交通申请详情 );
                    dialog_layout_now.setVisibility ( View.VISIBLE );
                    TextView JT_Reason = dialog.findViewById( R.id.交通账单详情出行理由 );
                    TextView JT_Date = dialog.findViewById( R.id.交通账单详情出行日期 );
                    TextView JT_Money = dialog.findViewById( R.id.交通账单详情实际花销 );
                    TextView JT_Ps = dialog.findViewById( R.id.交通账单详情出行备注 );
                    JT_Reason.setText( (CharSequence) myAdapter.getItem ( position ).get( "出行理由" ) );
                    JT_Date.setText( (CharSequence) myAdapter.getItem ( position ).get( "出行日期" ) );
                    JT_Money.setText( (CharSequence) myAdapter.getItem ( position ).get( "实际花销" ) );
                    JT_Ps.setText( (CharSequence) myAdapter.getItem ( position ).get( "出行备注" ) );
                    break;
                case 3:
                    dialog_layout_now = dialog.findViewById ( R.id.车辆申请详情 );
                    dialog_layout_now.setVisibility ( View.VISIBLE );
                    TextView CLStartPlace = dialog.findViewById( R.id.车辆账单详情始发地 );
                    TextView CLEndPlace = dialog.findViewById( R.id.车辆账单详情目的地 );
                    TextView CLReason = dialog.findViewById( R.id.车辆账单详情理由 );
                    TextView CLPay = dialog.findViewById( R.id.车辆账单详情实际花销 );
                    TextView CLMore = dialog.findViewById( R.id.车辆账单详情补充说明 );
                    CLStartPlace.setText( (CharSequence) myAdapter.getItem ( position ).get( "始发地" ) );
                    CLEndPlace.setText( (CharSequence) myAdapter.getItem ( position ).get( "目的地" ) );
                    CLPay.setText( (CharSequence) myAdapter.getItem ( position ).get( "实际花销" ) );
                    CLReason.setText( (CharSequence) myAdapter.getItem ( position ).get( "理由" ) );
                    CLMore.setText( (CharSequence) myAdapter.getItem ( position ).get( "补充说明" ) );
                    break;
                case 4:
                    dialog_layout_now = dialog.findViewById ( R.id.培训申请详情 );
                    dialog_layout_now.setVisibility ( View.VISIBLE );
                    TextView PXTrainingInstitutionName = dialog.findViewById( R.id.培训账单详情培训机构名称 );
                    TextView PXDate = dialog.findViewById( R.id.培训账单详情培训日期 );
                    TextView PXTrainingMethod = dialog.findViewById( R.id.培训账单详情授课方式 );
                    TextView PXTrainingPlace = dialog.findViewById( R.id.培训账单详情授课地点 );
                    TextView PXTrainingBudget = dialog.findViewById( R.id.培训账单详情培训预算 );
                    TextView PXTrainingContent = dialog.findViewById( R.id.培训账单详情培训内容 );
                    TextView PXExaminationMethod = dialog.findViewById( R.id.培训账单详情考核方式 );
                    TextView PXMore = dialog.findViewById( R.id.培训账单详情备注 );
                    PXTrainingInstitutionName.setText( (CharSequence) myAdapter.getItem ( position ).get( "培训机构名称" ) );
                    PXDate.setText( (CharSequence) myAdapter.getItem ( position ).get( "培训日期" ) );
                    PXTrainingMethod.setText( (CharSequence) myAdapter.getItem ( position ).get( "授课方式" ) );
                    PXTrainingPlace.setText( (CharSequence) myAdapter.getItem ( position ).get( "授课地点" ) );
                    PXTrainingBudget.setText( (CharSequence) myAdapter.getItem ( position ).get( "培训预算" ) );
                    PXTrainingContent.setText( (CharSequence) myAdapter.getItem ( position ).get( "培训内容" ) );
                    PXExaminationMethod.setText( (CharSequence) myAdapter.getItem ( position ).get( "考核方式" ) );
                    PXMore.setText( (CharSequence) myAdapter.getItem ( position ).get( "备注" ) );
                    break;
                case 5:
                    dialog_layout_now = dialog.findViewById ( R.id.采购申请详情 );
                    dialog_layout_now.setVisibility ( View.VISIBLE );
                    TextView CGProcurementMatters = dialog.findViewById( R.id.采购账单详情采购事由 );
                    TextView CGProcurementDate = dialog.findViewById( R.id.采购账单详情采购日期 );
                    TextView CGProcurementCompany = dialog.findViewById( R.id.采购账单详情采购公司 );
                    TextView CGGoodsName = dialog.findViewById( R.id.采购账单详情商品名称 );
                    TextView CGGoodsNumber = dialog.findViewById( R.id.采购账单详情商品数量 );
                    TextView CGGoodsUnitPrice = dialog.findViewById( R.id.采购账单详情商品单价 );
                    TextView CGGoodsTotalPrice = dialog.findViewById( R.id.采购账单详情商品总花销 );
                    TextView CGMore = dialog.findViewById( R.id.采购账单详情备注 );
                    CGProcurementMatters.setText( (CharSequence) myAdapter.getItem ( position ).get( "采购事由" ) );
                    CGProcurementDate.setText( (CharSequence) myAdapter.getItem ( position ).get( "采购日期" ) );
                    CGProcurementCompany.setText( (CharSequence) myAdapter.getItem ( position ).get( "采购公司" ) );
                    CGGoodsName.setText( (CharSequence) myAdapter.getItem ( position ).get( "商品名称" ) );
                    CGGoodsNumber.setText( (CharSequence) myAdapter.getItem ( position ).get( "商品数量" ) );
                    CGGoodsUnitPrice.setText( (CharSequence) myAdapter.getItem ( position ).get( "商品单价" ) );
                    CGGoodsTotalPrice.setText( (CharSequence) myAdapter.getItem ( position ).get( "商品总花销" ) );
                    CGMore.setText( (CharSequence) myAdapter.getItem ( position ).get( "备注" ) );
                    break;
                case 6:
                    dialog_layout_now = dialog.findViewById ( R.id.咨询申请详情 );
                    dialog_layout_now.setVisibility ( View.VISIBLE );
                    TextView ZXDate = dialog.findViewById( R.id.咨询账单详情咨询日期 );
                    TextView ZXReason = dialog.findViewById( R.id.咨询账单详情咨询原因 );
                    TextView ZXCompany = dialog.findViewById( R.id.咨询账单详情咨询公司 );
                    TextView ZXCost = dialog.findViewById( R.id.咨询账单详情咨询费用 );
                    TextView ZXMore = dialog.findViewById( R.id.咨询账单详情备注 );
                    ZXDate.setText( (CharSequence) myAdapter.getItem ( position ).get( "咨询日期" ) );
                    ZXReason.setText( (CharSequence) myAdapter.getItem ( position ).get( "咨询原因" ) );
                    ZXCompany.setText( (CharSequence) myAdapter.getItem ( position ).get( "咨询公司" ) );
                    ZXCost.setText( (CharSequence) myAdapter.getItem ( position ).get( "咨询费用" ) );
                    ZXMore.setText( (CharSequence) myAdapter.getItem ( position ).get( "备注" ) );
                    break;
                case 7:
                    dialog_layout_now = dialog.findViewById ( R.id.租赁申请详情 );
                    dialog_layout_now.setVisibility ( View.VISIBLE );
                    TextView ZLItem = dialog.findViewById( R.id.租赁账单详情租赁物品 );
                    TextView ZLReason = dialog.findViewById( R.id.租赁账单详情租赁原因 );
                    TextView ZLDate = dialog.findViewById( R.id.租赁账单详情租赁日期 );
                    TextView ZLCompany = dialog.findViewById( R.id.租赁账单详情租赁公司 );
                    TextView ZLCost = dialog.findViewById( R.id.租赁账单详情租赁花销 );
                    TextView ZLMore = dialog.findViewById( R.id.租赁账单详情备注 );
                    ZLItem.setText( (CharSequence) myAdapter.getItem ( position ).get( "租赁物品" ) );
                    ZLReason.setText( (CharSequence) myAdapter.getItem ( position ).get( "租赁原因" ) );
                    ZLDate.setText( (CharSequence) myAdapter.getItem ( position ).get( "租赁日期" ) );
                    ZLCompany.setText( (CharSequence) myAdapter.getItem ( position ).get( "租赁公司" ) );
                    ZLCost.setText( (CharSequence) myAdapter.getItem ( position ).get( "租赁花销" ) );
                    ZLMore.setText( (CharSequence) myAdapter.getItem ( position ).get( "备注" ) );
                    break;
                case 8:
                    dialog_layout_now = dialog.findViewById ( R.id.运费申请详情 );
                    dialog_layout_now.setVisibility ( View.VISIBLE );
                    TextView YSItem = dialog.findViewById( R.id.运费账单详情运输物品 );
                    TextView YSItemNumber = dialog.findViewById( R.id.运费账单详情物品数量 );
                    TextView YSStartPlace = dialog.findViewById( R.id.运费账单详情始发地 );
                    TextView YSEndPlace = dialog.findViewById( R.id.运费账单详情目的地 );
                    TextView YSEstimatedDate = dialog.findViewById( R.id.运费账单详情预计日期 );
                    TextView YSTransportMethod = dialog.findViewById( R.id.运费账单详情运输方式 );
                    TextView YSTransportCompany = dialog.findViewById( R.id.运费账单详情运输公司 );
                    TextView YSCost = dialog.findViewById( R.id.运费账单详情实际花销 );
                    TextView YSMore = dialog.findViewById( R.id.运费账单详情备注 );
                    YSItem.setText( (CharSequence) myAdapter.getItem ( position ).get( "运输物品" ) );
                    YSItemNumber.setText( (CharSequence) myAdapter.getItem ( position ).get( "物品数量" ) );
                    YSStartPlace.setText( (CharSequence) myAdapter.getItem ( position ).get( "始发地" ) );
                    YSEndPlace.setText( (CharSequence) myAdapter.getItem ( position ).get( "目的地" ) );
                    YSEstimatedDate.setText( (CharSequence) myAdapter.getItem ( position ).get( "预计日期" ) );
                    YSTransportMethod.setText( (CharSequence) myAdapter.getItem ( position ).get( "运输方式" ) );
                    YSTransportCompany.setText( (CharSequence) myAdapter.getItem ( position ).get( "运输公司" ) );
                    YSCost.setText( (CharSequence) myAdapter.getItem ( position ).get( "实际花销" ) );
                    YSMore.setText( (CharSequence) myAdapter.getItem ( position ).get( "备注" ) );
                    break;
            }
            dialog.show ();
        }
    };

    public void intiData(){
        List<CakeValue> itemBeanLists = new ArrayList<>();
        itemBeanLists.add(new CakeValue("可用余额",100,"#FABD3B"));
        itemBeanLists.add(new CakeValue("待收总额",500,"#F9943C"));
        itemBeanLists.add(new CakeValue("投资冻结",200,"#FFD822"));
        itemBeanLists.add(new CakeValue("提现冻结",300,"#F7602B"));
        cakeSurfaceView.setData(itemBeanLists);
    }

    //抽屉布局的滑动范围扩大
    private void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
           if (activity == null || drawerLayout == null)
                     return;
             try {
                    // find ViewDragHelper and set it accessible
                     Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
                     leftDraggerField.setAccessible(true);
                     ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);
                    // find edgesize and set is accessible
                     Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
                    edgeSizeField.setAccessible(true);
                     int edgeSize = edgeSizeField.getInt(leftDragger);
                     // set new edgesize
                    // Point displaySize = new Point();
                    DisplayMetrics dm = new DisplayMetrics();
                     activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                     edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (dm.widthPixels * displayWidthPercentage)));
                 } catch (NoSuchFieldException e) {
                     Log.e("NoSuchFieldException", e.getMessage().toString ());
                 } catch (IllegalArgumentException e) {
                     Log.e("IlegalArgumentException", e.getMessage().toString());
                 } catch (IllegalAccessException e) {
                     Log.e("IllegalAccessException", e.getMessage().toString());
                 }
         }

}