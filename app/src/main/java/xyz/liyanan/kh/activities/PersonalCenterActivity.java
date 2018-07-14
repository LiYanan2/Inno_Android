package xyz.liyanan.kh.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.liyanan.kh.R;

public class PersonalCenterActivity extends AppCompatActivity {

    //定义每个控件,用户名和两次密码
    private TextView username_textview, myorder_textview;
    private EditText password_etx_1, password_etx_2;
    private Button update_button;
    //获取两个密码并转化为string类型保存
    String string_password_etx_1, string_password_etx_2;
    String result, is;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        //分别获取每个控件
        username_textview = findViewById(R.id.activity_personalcenter_username_editText);
        myorder_textview = findViewById(R.id.activity_personalcenter_myOrder_textview);
        update_button = findViewById(R.id.activity_personalcenter_submit_button);
        password_etx_1 = findViewById(R.id.activity_personalcenter_password_editText1);
        password_etx_2 = findViewById(R.id.activity_personalcenter_password_editText2);

        //获取sharedPreferences本地文件里的username值,并且赋值给username_textview
        SharedPreferences sharedPreferences = getSharedPreferences("li.yanan", MODE_PRIVATE);
        final String username = sharedPreferences.getString("username", "");
        username_textview.setText(username);

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取两个password进行页面中非空以及是否相等的判断
                string_password_etx_1 = password_etx_1.getText().toString().trim();
                string_password_etx_2 = password_etx_2.getText().toString().trim();
                if (string_password_etx_1.length() == 0 || string_password_etx_2.length() == 0) {
                    Toast.makeText(PersonalCenterActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    if (string_password_etx_1.equals(string_password_etx_2)) {
                        //TODO 进行网络操作
                        update_personalcenter();
                    } else {
                        Toast.makeText(PersonalCenterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        myorder_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalCenterActivity.this, OrderListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void update_personalcenter() {
        //网络请求
        SharedPreferences sharedPreferences = getSharedPreferences("li.yanan", MODE_PRIVATE);
        final String username = sharedPreferences.getString("username", "");
        final String password = string_password_etx_1;
        new Thread(new Runnable() {//开启线程
            @Override
            public void run() {
                FormBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.81:5000/KH/v1/update")
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

    //解析
    private void JX(String date) {
        try {
            JSONObject jsonObject = new JSONObject(date);
            String flag = jsonObject.getString("flag");//获取返回值flag的内容
            if (flag.equals("1")) {
                is = jsonObject.getString("description");
            } else {
                is = jsonObject.getString("description");
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
                    Toast.makeText(PersonalCenterActivity.this, is, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


}

