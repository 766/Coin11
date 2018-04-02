package com.bitcast.app;

import com.bitcast.app.bean.News;
import com.bitcast.app.bean.Setting;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KangWei on 2018-03-29.
 * 2018-03-29 00:17
 * Coin
 * com.baidu.coin
 */
public class NewsProvider {
    public static List<News> getPersonList(int page) {
        ArrayList<News> arr = new ArrayList<>();
        if (page >= 4) return arr;

        String json =
                "{'time':'23:13','title':'天宫一号预计周末坠落地球 残骸不会构成危险','content':'参考消息网3月29日报道外媒称，据预测，中国天宫一号空间站将在复活节长周末坠落地球。\n" +
                        "据英国《每日邮报》网站3月28日报道，该空间站于2011年发射升空，于2016年正式停止使用。\n" +
                        "报道称，自停止使用以来，该空间站的高度一直在稳步下降，现在看来它将脱离轨道，在复活节长周末撞击地球。\n" +
                        "报道称，目前该航天器每一个半小时环绕地球飞行一圈，速度为每小时2.8万公里，其轨迹在南北纬43度之间。\n" +
                        "报道指出，这使澳大利亚、非洲、南美洲、美国大部分地区以及东南亚都处在可能的撞击区内。\n" +
                        "另据路透社3月27日报道，欧洲航天局说，中国天宫一号太空实验室可能在3月30日和4月2日之间坠落地球，残骸碎片撒落范围可达数千平方公里，不过对人构成的危险非常小。\n" +
                        "欧洲航天局说，这个长10.4米的空间站预计将在南北纬43度之间的某个点再入地球大气层。\n" +
                        "欧洲航天局说，对于天宫一号将坠落何处的更准确预测要等到它再入大气层前一天才可能做出。但即使到那时做出的预测，也可能误差数千公里。\n" +
                        "不过该航天局说，人被天宫一号残骸碎片击中的可能性是被闪电击中的可能性的千万分之一，因为地球的大部分被水覆盖或是荒无人烟。\n" +
                        "欧洲航天局在其网站上说：“在航天历史上，尚没有证实发生过太空残骸碎片坠落造成伤亡的情况。”\n" +
                        "此外，美国趣味科学网站3月26日刊登文章称，由于高空大气层摩擦力的拖累，天宫一号的高度将逐渐下降，接触的大气密度越来越高，造成力度更大的拖拽，结果使之进一步下坠，轨道飞行速度继续放慢，这一过程被称为轨道衰变。\n" +
                        "文章称，以这一速度飞行，大气摩擦力会产生大量的热。不过天宫一号缺乏隔热手段。美国航空航天公司说，除了高热，空间站接触的大气越来越厚，速度开始迅速下降。减速会产生高达重力加速度10倍的载荷，造成该航天器开始解体。\n" +
                        "该空间站上脱落的大部分小部件会被摩擦产生的热焚毁，不过专家估计有些部分会在烈火中幸存下来，坠落地面。靠近地面时，大气密度大，空间站的残余部件会大大减速和冷却下来。\n" +
                        "美国趣味科学网站3月26日还报道称，中国废弃的空间站天宫一号失去控制，不久将撞向地球。不过不要惊慌。\n" +
                        "报道称，一个重8.5吨的金属体脱离轨道冲向地球广大中部的某个未知地点，这让有些人感到担心，不过这对民众并不构成什么危险。\n" +
                        "不过，值得一提的是，天宫一号不过是已经（受控或不受控）坠落地球的其他物体的一个零头。\n" +
                        "报道称，鉴于它是一个空间站原型，天宫一号作为一个航天器来说都不算大，更别说作为空间站来说了。它的重量不过是俄罗斯空间站“和平”号120吨重量的7%。据欧洲航天局说，2001年坠落的“和平”号是从太空坠落的最大人造物体。'}";
        Gson gson = new Gson();
        News news = gson.fromJson(json, News.class);
        for (int i = 0; i < 5; i++) {
            arr.add(news);
        }
        return arr;
    }

    public static List<Setting> getSettingList() {
        ArrayList<Setting> arr = new ArrayList<>();
        arr.add(new Setting("去评分"));
        arr.add(new Setting("订阅新闻", "true"));
        arr.add(new Setting("版本", App.getInstance().getAppVersionName()));
        arr.add(new Setting("关于我们"));
        return arr;
    }

    public static List fetchData() {
        return null;
    }
}
