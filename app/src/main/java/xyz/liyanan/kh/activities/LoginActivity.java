package xyz.liyanan.kh.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.liyanan.kh.R;

public class LoginActivity extends AppCompatActivity {
    private EditText editText_username, editText_password;
    private Button button;
    private TextView toregister;
    private String result, is;
    private String userString, pwdString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText_username = (EditText) findViewById(R.id.activity_login_username_editText);
        editText_password = (EditText) findViewById(R.id.activity_login_password_editText);
        toregister = (TextView) findViewById(R.id.activity_login_register);
        button = (Button) findViewById(R.id.activity_login_submit_button);
        //sharedPreferences判断是否已经登录过
        SharedPreferences sharedPreferences = getSharedPreferences("li.yanan", MODE_PRIVATE);
        if (sharedPreferences.getString("isLogin", "").equals("1")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取文本框中的内容
                userString = editText_username.getText().toString().trim();
                pwdString = editText_password.getText().toString().trim();
                if (userString.trim().length() > 0 && pwdString.trim().length() > 0) {
                    okhttp(userString, pwdString);
                } else {
                    Toast.makeText(LoginActivity.this, "用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
                }


            }
        });

        toregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到register页面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //TODO 如果没有给予权限则出现提示
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LoginActivity.this, permissions, 1);
        }

    }

    //重写onBackPressed实现只启动一次首页
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); 	不要调用父类的方法
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    //请求，多线程
    public void okhttp(final String username, final String pwd) {

        new Thread(new Runnable() {//开启线程
            @Override
            public void run() {
                FormBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", pwd)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.81:5000/KH/v1/login")
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
            userString = jsonObject.getString("username");
            if (flag.equals("success")) {
                is = jsonObject.getString("description");
                SharedPreferences.Editor editor = getSharedPreferences("li.yanan", MODE_PRIVATE).edit();
                editor.putString("username", userString);
                editor.putString("isLogin", "1");
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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
                    Toast.makeText(LoginActivity.this, is, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


}
