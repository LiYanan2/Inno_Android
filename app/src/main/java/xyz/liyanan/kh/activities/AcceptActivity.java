package xyz.liyanan.kh.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.liyanan.kh.R;

public class AcceptActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);
        linearLayout=findViewById(R.id.liner_1);
        connect();
        //设置监听器
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AcceptActivity.this,ListChooseActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void connect(){
        SharedPreferences sharedPreferences = getSharedPreferences("li.yanan", MODE_PRIVATE);
        final String username = sharedPreferences.getString("username", "");
        //网络请求
        new Thread(new Runnable() {//开启线程
            @Override
            public void run() {
                FormBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.81:5000/KH/v1/connect")
                        .post(formBody)
                        .build();
                OkHttpClient okHttpClient = new OkHttpClient();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    result = response.body().string();
                    JX_um(result);//解析
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void JX_um(String date) {
        try {
            JSONObject jsonObject = new JSONObject(date);
            String flag = jsonObject.getString("flag");//获取返回值flag的内容
            if (flag.equals("1")) {
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
