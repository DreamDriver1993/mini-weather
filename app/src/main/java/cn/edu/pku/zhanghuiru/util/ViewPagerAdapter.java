package cn.edu.pku.zhanghuiru.util;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import java.util.List;

/**
 * Created by Nichole on 2016/11/29.
 */
public class ViewPagerAdapter extends PagerAdapter{
    private List<View> views;
    private Context context;
    public ViewPagerAdapter(List<View> views,Context context){
        this.views=views;
        this.context=context;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==object);
    }
}
