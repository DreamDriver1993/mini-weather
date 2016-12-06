package cn.edu.pku.zhanghuiru.miniweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhanghuiru.bean.TodayWeather;
import cn.edu.pku.zhanghuiru.util.NetUtil;
import cn.edu.pku.zhanghuiru.util.ViewPagerAdapter;

/**
 * Created by Nichole on 2016/9/20.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private static final int UPDATE_TODAY_WEATHER=1;
    private ImageView mUpdateBtn,mCitySelect;

    private TextView cityTv,timeTv,humidityTv,pmQualityTv,pmDataTv,weekTv,temperatureTv,
            climateTv,windTv,city_name_Tv,degreeTv;
    private ImageView weatherImg,pmImg;
    private ProgressBar updateprogressbar;

    private ViewPager viewPager;
    private List<View> views;
    private ViewPagerAdapter viewPagerAdapter;

    private RelativeLayout pagerLayout;

    private Handler mhandler=new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }

    };

    //进行序列化所需要的
    private TodayWeather weather=null;
    private long startTime=01,endTime=01;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn=(ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        mCitySelect=(ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        updateprogressbar=(ProgressBar)findViewById(R.id.title_update_progress);

        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
            Log.d("myWeather","网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        }else{
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了!", Toast.LENGTH_LONG).show();
        }

        initView();
        initPagerView();
    }

    //一周天气情况
    public void initPagerView(){
        pagerLayout=(RelativeLayout)findViewById(R.id.sevenDayWeather_content);
        LayoutInflater layoutInflater=LayoutInflater.from(this);
        views=new ArrayList<View>();
        views.add(layoutInflater.inflate(R.layout.three_days_weather,null));
        views.add(layoutInflater.inflate(R.layout.six_days_weather,null));
        viewPagerAdapter=new ViewPagerAdapter(views,this);
        /*viewPager=(ViewPager)findViewById(R.id.sevenDayWeather);*/
        viewPager=new ViewPager(this);
        viewPager.setAdapter(viewPagerAdapter);
        //添加到父容器中
        pagerLayout.addView(viewPager);
    }
    /**
     *
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode){
        final String address="http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("myWeather",address);
        new Thread(new Runnable(){
            @Override
            public void run(){
                HttpURLConnection con=null;
                TodayWeather todayWeather=null;
                try{
                    URL url=new URL(address);
                    con=(HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in=con.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String responseStr=response.toString();
                    Log.d("myWeather",responseStr);

                    todayWeather=parseXML(responseStr);
                    if(todayWeather!=null){
                        //对于没有pm信息的城市默认为0
                        if(todayWeather.getPm25()==null){
                            todayWeather.setPm25("0");
                        }
                        Log.d("todayWeather",todayWeather.toString());

                        //将weather信息保存到文件中
                        saveObject(serialize(todayWeather));

                        Message msg=new Message();
                        msg.what=UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mhandler.sendMessage(msg);

                    }

                }catch(Exception e){
                    e.printStackTrace();;
                }finally{
                    if(con!=null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.title_city_manager){
            Intent intent=new Intent(this,SelectCity.class);
//            startActivity(intent);
            startActivityForResult(intent,1);
        }

        if(view.getId()==R.id.title_update_btn){

            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
            String cityCode=sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
                Log.d("myWeather","网络OK");
                mUpdateBtn.setVisibility(View.INVISIBLE);
                updateprogressbar.setVisibility(View.VISIBLE);
                queryWeatherCode(cityCode);
                mUpdateBtn.setVisibility(View.VISIBLE);
                updateprogressbar.setVisibility(View.INVISIBLE);
            }else{
                Log.d("myWeather","网络挂了");
                mUpdateBtn.setVisibility(View.INVISIBLE);
                updateprogressbar.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    void initView(){
        cityTv=(TextView)findViewById(R.id.city);
        timeTv=(TextView)findViewById(R.id.time);
        humidityTv=(TextView)findViewById(R.id.humidity);
        pmQualityTv=(TextView)findViewById(R.id.pm2_5_quality);
        pmDataTv=(TextView)findViewById(R.id.pm_data);
        weekTv=(TextView)findViewById(R.id.week_today);
        temperatureTv=(TextView)findViewById(R.id.temperatrue);
        climateTv=(TextView)findViewById(R.id.climate);
        windTv=(TextView)findViewById(R.id.wind);
        city_name_Tv=(TextView)findViewById(R.id.title_city_name);
        weatherImg=(ImageView)findViewById(R.id.weather_img);
        pmImg=(ImageView)findViewById(R.id.pm2_5_img);
        //自己添加的温度部分
        degreeTv=(TextView)findViewById(R.id.degree);

        //从文件中读取天气信息
        try{
            weather=(TodayWeather)deSerialize(getObject());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(weather==null){
            cityTv.setText("N/A");
            timeTv.setText("N/A");
            humidityTv.setText("N/A");
            pmQualityTv.setText("N/A");
            pmDataTv.setText("N/A");
            weekTv.setText("N/A");
            temperatureTv.setText("N/A");
            climateTv.setText("N/A");
            windTv.setText("N/A");
            city_name_Tv.setText("N/A");
            degreeTv.setText("N/A");
        }else{
            city_name_Tv.setText(weather.getCity()+"天气");
            cityTv.setText(weather.getCity());
            timeTv.setText("今天"+weather.getUpdatetime()+"发布");
            humidityTv.setText("湿度："+weather.getShidu());
            pmQualityTv.setText(weather.getQuality());
            pmDataTv.setText(weather.getPm25());
            weekTv.setText(weather.getDate());
            if(weather.getHigh()!=null&&weather.getLow()!=null) {
                temperatureTv.setText(weather.getHigh().substring(2) + "~" + weather.getLow().substring(2));
            }climateTv.setText(weather.getType());
            windTv.setText(weather.getFengxiang()+weather.getFengli());
            degreeTv.setText(weather.getWendu()+"℃");
        }


    }

    //接收从另一个布局返回的数据
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent i){
        if(requestCode==1&&resultCode==RESULT_OK){
            String newCityCode=i.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+newCityCode);
            //可以把城市代码和天气信息保存到本地文件中，下次打开之后直接显示

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
                Log.d("myWeather","网络OK");
                queryWeatherCode(newCityCode);
            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }

    }


    /*private void parseXML(String xmldata){
        int fengxiangCount=0;
        int fengliCount=0;
        int dateCount=0;
        int highCount=0;
        int lowCount=0;
        int typeCount=0;
        try{
            XmlPullParserFactory fac=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType=xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while(eventType!=XmlPullParser.END_DOCUMENT){
                switch(eventType){
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("city")){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","city: "+xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("updatetime")){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","updatetime: "+xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("wendu")){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","wendu: "+xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("shidu")){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","shidu: "+xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("fengli")&&fengliCount==0){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","fengli: "+xmlPullParser.getText());
                            fengliCount++;
                        }else if(xmlPullParser.getName().equals("fengxiang")&&fengxiangCount==0){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","fengxiang: "+xmlPullParser.getText());
                            fengxiangCount++;
                        }else if(xmlPullParser.getName().equals("pm25")){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","pm25: "+xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("quality")){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","quality: "+xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("date")&&dateCount==0){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","date: "+xmlPullParser.getText());
                            dateCount++;
                        }else if(xmlPullParser.getName().equals("high")&&highCount==0){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","high: "+xmlPullParser.getText());
                            highCount++;
                        }else if(xmlPullParser.getName().equals("low")&&lowCount==0){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","low: "+xmlPullParser.getText());
                            lowCount++;
                        }else if(xmlPullParser.getName().equals("type")&&typeCount==0){
                            eventType=xmlPullParser.next();
                            Log.d("myWeather","type: "+xmlPullParser.getText());
                            typeCount++;
                        }
                        break;
                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType=xmlPullParser.next();
            }
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
*/

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather=null;
        int fengxiangCount=0;
        int fengliCount=0;
        int dateCount=0;
        int highCount=0;
        int lowCount=0;
        int typeCount=0;
        try{
            XmlPullParserFactory fac=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType=xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while(eventType!=XmlPullParser.END_DOCUMENT){
                switch(eventType){
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather=new TodayWeather();
                        }
                        if(todayWeather!=null){
                            if(xmlPullParser.getName().equals("city")){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","city: "+xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("updatetime")){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","updatetime: "+xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("wendu")){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","wendu: "+xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("shidu")){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","shidu: "+xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("fengli")&&fengliCount==0){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","fengli: "+xmlPullParser.getText());
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            }else if(xmlPullParser.getName().equals("fengxiang")&&fengxiangCount==0){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","fengxiang: "+xmlPullParser.getText());
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            }else if(xmlPullParser.getName().equals("pm25")){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","pm25: "+xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("quality")){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","quality: "+xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            }else if(xmlPullParser.getName().equals("date")&&dateCount==0){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","date: "+xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            }else if(xmlPullParser.getName().equals("high")&&highCount==0){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","high: "+xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            }else if(xmlPullParser.getName().equals("low")&&lowCount==0){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","low: "+xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            }else if(xmlPullParser.getName().equals("type")&&typeCount==0){
                                eventType=xmlPullParser.next();
//                                Log.d("myWeather","type: "+xmlPullParser.getText());
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                            break;
                        }
                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType=xmlPullParser.next();
            }
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
      return todayWeather;
    }

    /**
     * 更新UI控件
     */
    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText("今天"+todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmQualityTv.setText(todayWeather.getQuality());
        pmDataTv.setText(todayWeather.getPm25());
        weekTv.setText(todayWeather.getDate());
        if(todayWeather.getHigh()!=null&&todayWeather.getLow()!=null){
            temperatureTv.setText(todayWeather.getHigh().substring(2)+"~"+todayWeather.getLow().substring(2));
        }
        climateTv.setText(todayWeather.getType());
        windTv.setText(todayWeather.getFengxiang()+todayWeather.getFengli());
        degreeTv.setText(todayWeather.getWendu()+"℃");
        int pm25=Integer.parseInt(todayWeather.getPm25());
        if(pm25<=50){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        }else {
            if(pm25<=100){
             pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            }else{
                if(pm25<=150){
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
                }else{
                    if(pm25<=200){
                        pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
                    }else{
                        if(pm25<=300){
                            pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
                        }else{
                            pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
                        }
                    }
                }
            }
        }

        String weather=todayWeather.getType();
        switch (weather){
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
        }
        Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_LONG).show();
    }

    //对天气预报信息进行序列化，使用sharedPreference存储到文件中
    private String serialize(TodayWeather todayWeather)throws IOException{
        startTime=System.currentTimeMillis();
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(todayWeather);
        String str=byteArrayOutputStream.toString("ISO-8859-1");
        str=java.net.URLEncoder.encode(str,"UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        Log.d("Serial TodayWeather","Serial Content:"+str);
        endTime=System.currentTimeMillis();
        Log.d("Serial TodayWeather","序列化时间为："+(endTime-startTime));
        return str;
    }

    private TodayWeather deSerialize(String str)throws IOException,ClassNotFoundException{
        startTime=System.currentTimeMillis();
        String redStr=java.net.URLDecoder.decode(str,"UTF-8");
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream);
        TodayWeather todayWeather=(TodayWeather) objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        endTime=System.currentTimeMillis();
        Log.d("Deserial TodayWeather","反序列化时间为:"+(endTime-startTime));
        return todayWeather;
    }

    void saveObject(String  strObject){
        SharedPreferences sp=getSharedPreferences("weatherinfo",0) ;
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("todayWeather",strObject);
        editor.commit();
    }

    String getObject(){
        SharedPreferences sp=getSharedPreferences("weatherinfo",0);
        return sp.getString("todayWeather",null);
    }

}

