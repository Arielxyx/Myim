package com.example.imclient.Activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imclient.R;
import com.example.imclient.Utils.Constant;
import com.example.imclient.Utils.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.imclient.Activity.MainActivity.user;
import static com.example.imclient.Fragment.PostListFragment.imageString;

public class PublishAboutActivity extends AppCompatActivity {

    TextView publishContentTextView;
    ImageView publishImageView;
    Button publishBtn;

    private String publishContent;
    private String publishImage;
    private Uri imageUri;

    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_about);

        publishContentTextView = (TextView)findViewById(R.id.publish_content);
        publishImageView = (ImageView)findViewById(R.id.publish_image);
        publishBtn = (Button)findViewById(R.id.publish_btn);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //更新说说
        if(bundle!=null && bundle.getString("publishOrUpdate").equals("update")){
            System.out.println("publishOrUpdate == update");

            //填充更新编辑前的数据
            String content = bundle.getString("content");
            publishContentTextView.setText(content);
            //获取bundle数据
            int postId = bundle.getInt("postId");
            publishImage = imageString;
            byte[] bytes = Base64.decode(publishImage, Base64.DEFAULT);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            publishImageView.setImageBitmap(imageBitmap);
            //发表按钮的监听事件
            publishBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取更新编辑后的数据
                    publishContent = publishContentTextView.getText().toString();
//                    if(imageUri != null)
//                        publishImage = imageUri.toString();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();//将Bitmap转成Byte[]
                    Bitmap bitmap = ((BitmapDrawable)publishImageView.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);//压缩
                    publishImage = Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT);//加密转换成String
                    update(postId, publishContent, publishImage);
                }
            });
        }else{
            //发表说说
            publishBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    publishContent = publishContentTextView.getText().toString();
//                    //image
//                    if(imageUri != null)
//                        publishImage = imageUri.toString();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();//将Bitmap转成Byte[]
                    Bitmap bitmap = ((BitmapDrawable)publishImageView.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);//压缩
                    publishImage = Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT);//加密转换成String

                    publish(user.getId(), publishContent, publishImage);
                }
            });
        }
        //添加说说图片的监听事件
        publishImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击图片的话会打开相册、如果相册没有图片会自动打开相机
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, 0x1);
            }
        });
    }

    /**
     * 对选择的图片进行处理，根据图片的url值加载图片
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果选择照片成功就把照片数据的url设置过来
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                publishImageView.setImageURI(imageUri);
                Cursor cursor = getContentResolver().query(imageUri,null,null,null,null);
                if(cursor != null){
                    cursor.moveToFirst();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void publish(int userId, String content, String image) {
        HttpRequest.publish(userId, content, image, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            //回调函数 获得服务器的返回信息
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(response.body().string().equals(Constant.SUCCESS)){
                        Intent intent = new Intent(PublishAboutActivity.this, FragmentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("fragmentActivity","tab4");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        publishImageView.post(()->Toast.makeText(getApplicationContext(),"说说已成功发表...",Toast.LENGTH_SHORT).show());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void update(int postId, String content, String image) {
        HttpRequest.update(postId, content, image, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            //回调函数 获得服务器的返回信息
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(response.body().string().equals(Constant.SUCCESS)){
                        Intent intent = new Intent(PublishAboutActivity.this, FragmentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("fragmentActivity","tab4");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        publishImageView.post(()->Toast.makeText(getApplicationContext(),"说说已成功重新编辑...",Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
