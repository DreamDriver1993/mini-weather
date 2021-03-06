package cn.edu.pku.zhanghuiru.miniweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhanghuiru.util.ViewPagerAdapter;

/**
 * Created by Nichole on 2016/12/25.
 */
public class Guide extends Activity implements ViewPager.OnPageChangeListener{

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids={R.id.dot1,R.id.dot2,R.id.dot3};

    private Button button;

    private static final String SHAREDPREFERENCES_NAME = "first_pref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initView();
        initDots();

        button=(Button)views.get(2).findViewById(R.id.exerciseBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(
                        SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                // 存入数据
                editor.putBoolean("isFirstIn", false);
                // 提交修改
                editor.commit();
                Intent i=new Intent(Guide.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void initView(){
        LayoutInflater layoutInflater=LayoutInflater.from(this);
        views=new ArrayList<View>();
        views.add(layoutInflater.inflate(R.layout.pager1,null));
        views.add(layoutInflater.inflate(R.layout.pager2,null));
        views.add(layoutInflater.inflate(R.layout.pager3,null));
        viewPagerAdapter=new ViewPagerAdapter(views,this);
        viewPager=(ViewPager)findViewById(R.id.guideviewpager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    void initDots(){
        dots=new ImageView[views.size()];
        for(int i=0;i<views.size();i++){
            dots[i]=(ImageView)findViewById(ids[i]);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int i=0;i<views.size();i++){
            if(position==i){
                dots[i].setImageResource(R.drawable.page_indicator_focused);
            }else{
                dots[i].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
