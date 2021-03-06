package xyz.liyanan.kh.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.liyanan.kh.R;

public class HaveUmWaitActivity extends AppCompatActivity {

    private Button haveum_button_delete;
    //定义一个String类型的result存放服务器返回的结果,toast用来存放toast信息
    private String result, toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_um_wait);

        haveum_button_delete = findViewById(R.id.haveumwait_button_delete);
        haveum_button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haveum_delete();
            }
        });
    }

    private void haveum_delete(){
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
                        .url("http://192.168.43.81:5000/KH/v1/delete_haveum")
                        .post(formBody)
                        .build();
                OkHttpClient okHttpClient = new OkHttpClient();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    result = response.body().string();
                    JX(result);//解析
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void JX(String date) {
        try {
            JSONObject jsonObject = new JSONObject(date);
            String flag = jsonObject.getString("flag");//获取返回值flag的内容
            toast = jsonObject.getString("description");
            if (flag.equals("1")) {
                toast = jsonObject.getString("description");
                Intent intent = new Intent(HaveUmWaitActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                toast = jsonObject.getString("description");
            }
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Toast.makeText(HaveUmWaitActivity.this, toast, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
