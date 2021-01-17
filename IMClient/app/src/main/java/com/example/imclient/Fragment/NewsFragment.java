package com.example.imclient.Fragment;


import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.imclient.Activity.DetailActivity;
import com.example.imclient.Activity.MainActivity;
import com.example.imclient.Adapter.NewsAdapter;
import com.example.imclient.Model.News;
import com.example.imclient.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 新闻界面
 */
public class NewsFragment extends Fragment {

    private View view;
    Handler handler = new Handler();
    Handler handler1 = new Handler();
    ArrayList<News> news_list;
    ArrayList<Bitmap> bitmaps=new ArrayList<>();
    int index = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { Manifest.permission.INTERNET }, 123);
//            return ;
        }

        final ImageView imageView=(ImageView)view.findViewById(R.id.imageView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String httpUrl= " http://www.wust.edu.cn ";    //不支持http
                final String html= crawler( httpUrl );
                news_list=getNews(html);
                for(News n:news_list){
                    Log.d("test","1."+n.getPhotoUrl());
                    bitmaps.add(getHttpBitmap(n.getPhotoUrl()));
                }
                //一个线程处理listView
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        NewsAdapter adapter = new NewsAdapter(getActivity(), R.layout.news_item, news_list);
                        final ListView listView =(ListView)view.findViewById(R.id.news_listview);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                TextView news_href=(TextView)view.findViewById(R.id.href);
                                String href="http://www.wust.edu.cn"+news_href.getText().toString();
                                Intent intent = new Intent(getActivity(), DetailActivity.class);
                                Bundle bundle=new Bundle();
                                bundle.putString("href",href);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }).start();

        //一个线程处理imageView
        handler1 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==123)
                    if(bitmaps.size()>0){
                        //Log.d("test",news_list.get(msg.arg1).photoUrl+" "+msg.arg1);
                        imageView.setImageBitmap(bitmaps.get(msg.arg1));
                    }
            }
        };

        //并行实现图片轮播
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 123;
                msg.arg1 = index;
                handler1.sendMessage(msg);
                index++;
                if (index >= bitmaps.size()) {
                    index = 0;
                }
            }
        };
        timer.schedule(timerTask, 500, 2000);

        return view;
    }

    //将图片转化为bitmap格式
    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    //爬虫函数：爬取指定网址的网页
    public String crawler(String httpUrl) {
        String msg = ""; //服务器返回结果
        try {
            //创建URL对象
            URL url = new URL(httpUrl);
            //创建HttpURLConnection对象
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置连接相关属性
            conn.setConnectTimeout(5000); //设置连接超时为5秒
            conn.setRequestMethod("GET"); //设置请求方式(默认为get)
            conn.setRequestProperty("Charset", "UTF-8"); //设置uft-8字符集
            // 建立到连接
            conn.connect();
            //接收服务器返回的信息（输入流）
            // 200：表示成功完成(HTTP_OK)， 404：请求资源不存在(HTTP_NOT_FOUND)
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    msg+=line+"\n";
                }
                //关闭缓冲区和连接
                bufferedReader.close();
                conn.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    } //end scawler

    //根据html切出来图片的地址获取各条新闻
    public ArrayList<News> getNews(String html) { //参数html为爬取的页面内容
        Document doc = Jsoup.parse(html); //将字符串转为文档对象
        Elements elements = doc.select(".post.post1.post-14 .news");//使用select方法获取元素集合
        Elements elements_photo = doc.selectFirst("div.post-11").getElementsByTag("script");
        String cash[] = elements_photo.toString().split("\\[");
        String cash_2[] = cash[1].split("]");
        String cash_3[] = cash_2[0].split("\\}"); //每个图片右括号前面保留
        int x = 0;
        int index=0;
        Log.d("msg",""+elements.size());
        ArrayList<News> lst = new ArrayList<News>();
        for (Element element : elements) {
            String content[] = cash_3[x].split(",");
            String PhotoUrl[] ;//遍历元素集合
            if(x==0)
                PhotoUrl = content[1].split("\""); //第一张图片src在第二个位置，然后以引号继续分割取第一个
            else
                PhotoUrl = content[2].split("\""); //后面的图片第一个符号就是逗号，src在第三个位置
            //Log.d("msg",""+PhotoUrl[1]);
            News n = new News(
                    element.getElementsByTag("a").get(0).attr("title"),
                    element.getElementsByTag("a").get(0).attr("href"),
                    element.getElementsByClass("news_meta").get(0).text(),
                    "http://www.wust.edu.cn"+PhotoUrl[1]
            );
            x++;
            lst.add(n);
            Log.d("test"," 新闻标题： "+n.getTitle()+", 地址： "+n.getHref()+", 图片地址： "+n.getPhotoUrl()+", 时间： "+n.getTime());
        }
        return lst ;
    }
}
