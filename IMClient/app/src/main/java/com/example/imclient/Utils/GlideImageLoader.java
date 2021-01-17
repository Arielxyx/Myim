//package com.example.imclient.Utils;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.widget.ImageView;
//
//import com.assionhonty.lib.assninegridview.AssNineGridView;
//import com.bumptech.glide.Glide;
//
//public class GlideImageLoader implements AssNineGridView.ImageLoader{
//
//    @Override
//    public void onDisplayImage(Context context, ImageView imageView, String url) {
//        Glide.with(context).load(url).into(imageView);
//    }
//
//    @Override
//    public Bitmap getCacheImage(String url) {
//        return null;
//    }
//}