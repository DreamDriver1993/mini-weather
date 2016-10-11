package cn.edu.pku.zhanghuiru.bean;

/**
 * Created by Nichole on 2016/10/11.
 */
public class TodayWeather {
    private String city;
    private String updatetime;
    private String shidu;
    private String wendu;
    private String pm25;
    private String fengli;
    private String fengxiang;
    private String quality;
    private String date;
    private String high;
    private String low;
    private String type;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString(){
        return "TodayWeather{" +
                "city='"+city+"\'" +
                ",updatetime='"+updatetime+"\'" +
                ",shidu='"+shidu+"\'" +
                ",wendu='"+wendu+"\'" +
                ",pm2.5='"+pm25+"\'" +
                ",fengli='"+fengli+"\'" +
                ",fengxiang='"+fengxiang+"\'" +
                ",quality='"+quality+"\'" +
                ",date='"+date+"\'" +
                ",high='"+high+"\'" +
                ",low='"+low+"\'" +
                ",type='"+type+"\'" +
                "}";
    }
}
