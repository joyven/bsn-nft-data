package org.libra.bsn.service.controllerTest;

import org.libra.bsn.AppMainTest;
import org.libra.bsn.util.HttpClientUtil;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author xianhu.wang
 * @date 2022年10月20日 12:47 下午
 */
public class TestDemo extends AppMainTest {

    public void test() throws IOException {
        String url = "https://www.heqi.cn/zixun";

        System.out.println(HttpClientUtil.sendPost(url, new HashMap<>()));

//        String url2 = "http://www.baidu.com";
//
//        System.out.println(HttpClientUtil.sendPost(url2, new HashMap<>()));
//        //解析网页(Jsoup返回浏览器Document对象，可以使用Js的方法)
//        Document document = Jsoup.parse(new URL(url), 60000);
//
//        Elements elements = document.getElementsByClass("news-item clearfix");
//        for (Element el : elements) {
//            Elements elementsByClass = el.getElementsByClass("news-text mb10");
//            for (Element e : elementsByClass) {
//                System.out.println("title=>" + e.getElementsByTag("a").attr("title"));
//                System.out.println("text=>" + e.text());
//                System.out.println("img=>" + e.getElementsByTag("img").attr("src"));
//            }
//            Elements elementsByClass2 = el.getElementsByClass("ask-a lh-30 f-12");
//            Element element = elementsByClass2.get(0);
//            Elements span = element.getElementsByTag("span");
//
//            System.out.println("时间=>" + span.get(0).text());
//            System.out.println("关注=>" + span.get(1).text());
//        }
    }
//    public static void main(String[] args) throws IOException {
//
//        String url = "https://www.heqi.cn/zixun/";
//
//        HttpClientUtil.sendPost(url,new HashMap<>());
//
//        //解析网页(Jsoup返回浏览器Document对象，可以使用Js的方法)
//        Document document = Jsoup.parse(new URL(url), 60000);
//
//        Elements elements = document.getElementsByClass("news-item clearfix");
//        for (Element el : elements) {
//            Elements elementsByClass = el.getElementsByClass("news-text mb10");
//            for (Element e : elementsByClass) {
//                System.out.println("title=>" + e.getElementsByTag("a").attr("title"));
//                System.out.println("text=>" + e.text());
//                System.out.println("img=>" + e.getElementsByTag("img").attr("src"));
//            }
//            Elements elementsByClass2 = el.getElementsByClass("ask-a lh-30 f-12");
//            Element element = elementsByClass2.get(0);
//            Elements span = element.getElementsByTag("span");
//
//            System.out.println("时间=>" + span.get(0).text());
//            System.out.println("关注=>" + span.get(1).text());
//        }
//    }
}
