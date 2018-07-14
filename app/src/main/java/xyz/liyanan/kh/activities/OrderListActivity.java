package xyz.liyanan.kh.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
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
import xyz.liyanan.kh.adapter.ListDetailAdapter;
import xyz.liyanan.kh.bean.ListDetail;

public class OrderListActivity extends AppCompatActivity {
    private List<ListDetail> listDetails = new ArrayList<>();
    private String is, result;
    private String username = "sj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        get_data();
        ListDetailAdapter adapter = new ListDetailAdapter(OrderListActivity.this, R.layout.list_detail, listDetails);
        ListView listView = findViewById(R.id.ordelist_listview);
        listView.setAdapter(adapter);
    }

    public void get_data() {
        //获取listitem的值
        new Thread(new Runnable() {//开启线程
            //获取sharedPreferences本地文件里的username值
            @Override
            public void run() {
                FormBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.81:5000/KH/v1/order_list")
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
            JSONArray jsonArray = jsonObject.getJSONArray("orders");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                if (jsonObject1.getString("type").equals("1")) {
                    ListDetail listDetail = new ListDetail(username, jsonObject1.getString("username2"), jsonObject1.getString("datetime"));
                    listDetails.add(listDetail);
                } else {
                    ListDetail listDetail = new ListDetail(jsonObject1.getString("username2"), username, jsonObject1.getString("datetime"));
                    listDetails.add(listDetail);
                }
            }

        } catch (
                JSONException e)

        {
            e.printStackTrace();
        }
    }

}
