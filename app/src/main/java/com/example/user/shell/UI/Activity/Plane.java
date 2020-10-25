package com.example.user.shell.UI.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.example.user.shell.UI.Activity.R;

import java.util.ArrayList;

public class Plane extends AppCompatActivity {


    int cookie=10000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_plane);

        Banner ();//轮播图

        cookie = getIntent().getIntExtra("cookie",10000);
    }

    private void Banner() {
        ConvenientBanner banner = findViewById ( R.id.Plane广告 );
        ArrayList<Integer> data = new ArrayList<> ( );
        data.add ( R.drawable.advertisement1 );
        data.add ( R.drawable.advertisement2 );
        data.add ( R.drawable.advertisement3 );
        data.add ( R.drawable.advertisement4 );

        banner.setPages(
                new CBViewHolderCreator () {
                    @Override
                    public LocalImageHolderView createHolder(View itemView) {
                        return new LocalImageHolderView (itemView);
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
}
