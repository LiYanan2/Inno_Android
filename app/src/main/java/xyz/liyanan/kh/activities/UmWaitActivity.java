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
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.liyanan.kh.R;

public class UmWaitActivity extends AppCompatActivity {

    //定义一个Button类umWait_delete_button
    private Button umWait_delete_button;

    //定义一个String类型的result存放服务器返回的结果,toast用来存放toast信息
    private String result, toast;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
                get_um();
                //计时器要干的事情
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    //服务器请求接口，获取是否有有伞队列
    public void get_um(){
        SharedPreferences sharedPreferences = getSharedPreferences("li.yanan", MODE_PRIVATE);
        final String username = sharedPreferences.getString("username", "");

        new Thread(new Runnable() {//开启线程
            @Override
            public void run() {
                FormBody formBody = new FormBody.Builder()
                        .add("username",username)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.81:5000/KH/v1/get_um")
                        .post(formBody)
                        .build();
                OkHttpClient okHttpClient = new OkHttpClient();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    result = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String flag = jsonObject.getString("flag");//获取返回值flag的内容
                        if (flag.equals("1")) {

                            Intent intent = new Intent(UmWaitActivity.this, AcceptActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }//解析
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onStart() {
        super.onStart();
        timer.schedule(task, 3000, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_um_wait);
        //获取取消按钮
        umWait_delete_button = (Button) findViewById(R.id.umwait_button_delete);
        //设置取消我要打伞请求按钮的监听事件
        umWait_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_um();
            }
        });
    }

    private void delete_um() {
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
                        .url("http://192.168.43.81:5000/KH/v1/delete_um")
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
                Intent intent = new Intent(UmWaitActivity.this, MainActivity.class);
                startActivity(intent);
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
                    Toast.makeText(UmWaitActivity.this, toast, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
