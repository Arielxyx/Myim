package com.example.imclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.imclient.Model.News;
import com.example.imclient.R;

import java.util.List;

/**
 * 新闻动态列表的适配器
 */
public class NewsAdapter extends ArrayAdapter<News> {

    private int resourceId;

    public NewsAdapter(Context context, int resource, List<News> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        News n = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView news_title = (TextView)view.findViewById(R.id.title);
        TextView news_meta = (TextView)view.findViewById(R.id.news_meta);
        TextView news_href = (TextView)view.findViewById(R.id.href);
        news_title.setText(""+n.getTitle());

        news_meta.setText(""+n.getTime()); //原方法是getNews_meta()
        news_href.setText(""+n.getHref());
        return view;
    }
}
