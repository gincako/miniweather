package com.simple.miniweather.gson;

/**
 * Created by Administrator on 2017/2/26 0026.
 */

public class DailyWeather {

    /**
     * astro : {"mr":"06:05","ms":"17:35","sr":"06:27","ss":"17:53"}
     * cond : {"code_d":"100","code_n":"100","txt_d":"晴","txt_n":"晴"}
     * date : 2017-02-26
     * hum : 57
     * pcpn : 0.0
     * pop : 1
     * pres : 1026
     * tmp : {"max":"15","min":"5"}
     * uv : 6
     * vis : 10
     * wind : {"deg":"299","dir":"西南风","sc":"微风","spd":"3"}
     */

    private AstroBean astro;
    private CondBean cond;
    private String date;
    private String hum;
    private String pcpn;
    private String pop;
    private String pres;
    private TmpBean tmp;
    private String uv;
    private String vis;
    private WindBean wind;

    public AstroBean getAstro() {
        return astro;
    }

    public void setAstro(AstroBean astro) {
        this.astro = astro;
    }

    public CondBean getCond() {
        return cond;
    }

    public void setCond(CondBean cond) {
        this.cond = cond;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHum() {
        return hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public String getPcpn() {
        return pcpn;
    }

    public void setPcpn(String pcpn) {
        this.pcpn = pcpn;
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }

    public String getPres() {
        return pres;
    }

    public void setPres(String pres) {
        this.pres = pres;
    }

    public TmpBean getTmp() {
        return tmp;
    }

    public void setTmp(TmpBean tmp) {
        this.tmp = tmp;
    }

    public String getUv() {
        return uv;
    }

    public void setUv(String uv) {
        this.uv = uv;
    }

    public String getVis() {
        return vis;
    }

    public void setVis(String vis) {
        this.vis = vis;
    }

    public WindBean getWind() {
        return wind;
    }

    public void setWind(WindBean wind) {
        this.wind = wind;
    }

    public static class AstroBean {
        /**
         * mr : 06:05
         * ms : 17:35
         * sr : 06:27
         * ss : 17:53
         */

        private String mr;
        private String ms;
        private String sr;
        private String ss;

        public String getMr() {
            return mr;
        }

        public void setMr(String mr) {
            this.mr = mr;
        }

        public String getMs() {
            return ms;
        }

        public void setMs(String ms) {
            this.ms = ms;
        }

        public String getSr() {
            return sr;
        }

        public void setSr(String sr) {
            this.sr = sr;
        }

        public String getSs() {
            return ss;
        }

        public void setSs(String ss) {
            this.ss = ss;
        }
    }

    public static class CondBean {
        /**
         * code_d : 100
         * code_n : 100
         * txt_d : 晴
         * txt_n : 晴
         */

        private String code_d;
        private String code_n;
        private String txt_d;
        private String txt_n;

        public String getCode_d() {
            return code_d;
        }

        public void setCode_d(String code_d) {
            this.code_d = code_d;
        }

        public String getCode_n() {
            return code_n;
        }

        public void setCode_n(String code_n) {
            this.code_n = code_n;
        }

        public String getTxt_d() {
            return txt_d;
        }

        public void setTxt_d(String txt_d) {
            this.txt_d = txt_d;
        }

        public String getTxt_n() {
            return txt_n;
        }

        public void setTxt_n(String txt_n) {
            this.txt_n = txt_n;
        }
    }

    public static class TmpBean {
        /**
         * max : 15
         * min : 5
         */

        private String max;
        private String min;

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }
    }

    public static class WindBean {
        /**
         * deg : 299
         * dir : 西南风
         * sc : 微风
         * spd : 3
         */

        private String deg;
        private String dir;
        private String sc;
        private String spd;

        public String getDeg() {
            return deg;
        }

        public void setDeg(String deg) {
            this.deg = deg;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public String getSc() {
            return sc;
        }

        public void setSc(String sc) {
            this.sc = sc;
        }

        public String getSpd() {
            return spd;
        }

        public void setSpd(String spd) {
            this.spd = spd;
        }
    }
}
