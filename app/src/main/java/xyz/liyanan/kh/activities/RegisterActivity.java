package xyz.liyanan.kh.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.liyanan.kh.R;

public class RegisterActivity extends AppCompatActivity {

    private Button register_button;
    private EditText editText_username,editText_password1,editText_password2;
    private String string_username,string_password1,string_password2,result,is;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register_button = (Button) findViewById(R.id.activity_register_submit_button);
        editText_username = (EditText) findViewById(R.id.activity_register_username_editText);
        editText_password1 = (EditText) findViewById(R.id.activity_register_password_editText1);
        editText_password2 = (EditText) findViewById(R.id.activity_register_password_editText2);
        //TODO 注册的时候保证用户名的唯一性。
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                string_username = editText_username.getText().toString().trim();
                string_password1 = editText_password1.getText().toString().trim();
                string_password2 = editText_password2.getText().toString().trim();
                register(string_username,string_password1,string_password2);
            }
        });
    }

    private void register(final String username,final String password1,final String password2){

        if (!password1.equals(password2)){
            Toast.makeText(RegisterActivity.this, "两次密码不一致,请重新输入", Toast.LENGTH_SHORT).show();
        }
        else if (username.trim().length()==0){
            Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        }else {
            new Thread(new Runnable() {//开启线程
                @Override
                public void run() {
                    FormBody formBody = new FormBody.Builder()
                            .add("username", username)
                            .add("password", password1)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://192.168.43.81:5000/KH/v1/register")
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
    }

    //解析
    private void JX(String date) {
        try {
            JSONObject jsonObject = new JSONObject(date);
            String flag = jsonObject.getString("flag");//获取返回值flag的内容
            if (flag.equals("success")) {
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
                    Toast.makeText(RegisterActivity.this, is, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity
                            .this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };


}
