package cn.edu.pku.zhanghuiru.miniweather;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.edu.pku.zhanghuiru.app.MyApplication;
import cn.edu.pku.zhanghuiru.bean.City;

/**
 * Created by Nichole on 2016/10/18.
 */
public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView mlistView;
    private TextView mTitleName;


    private MyApplication myApplication;
    private List<City> cities;
    private City citySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        myApplication=(MyApplication) getApplication();
        cities=myApplication.getCityList();

        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mTitleName=(TextView)findViewById(R.id.title_name);

        mlistView = (ListView)findViewById(R.id.cityListView);
        ArrayAdapter<City> adapter=new ArrayAdapter<City>(
                SelectCity.this,android.R.layout.simple_list_item_1,cities);
        mlistView.setAdapter(adapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                citySelected=cities.get(i);
                mTitleName.setText("当前城市："+citySelected.getCity());
                Toast.makeText(SelectCity.this, "你选择了:"+cities.get(i).getCity(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.title_back:
                Intent intent=new Intent();
                intent.putExtra("cityCode",citySelected.getNumber());
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
    }

}
