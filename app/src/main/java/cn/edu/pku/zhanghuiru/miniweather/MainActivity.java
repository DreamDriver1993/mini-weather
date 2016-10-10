package cn.edu.pku.zhanghuiru.miniweather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import cn.edu.pku.zhanghuiru.util.NetUtil;

/**
 * Created by Nichole on 2016/9/20.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
            Log.d("myWeather","网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        }else{
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了!", Toast.LENGTH_LONG).show();
        }
    }



}
