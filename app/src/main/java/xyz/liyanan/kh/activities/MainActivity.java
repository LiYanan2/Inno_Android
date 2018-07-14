package xyz.liyanan.kh.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.liyanan.kh.R;

public class MainActivity extends AppCompatActivity {
    //我要打伞按钮,我有伞，个人信息页以及目的地
    private Button btn_submit_um;
    private EditText edt_destination;
    private TextView textView_haveUm;
    private ImageView account_image_toSelf;

    //与地图相关的信息注册
    //设置位置
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListener();
    private Button bt;
    private Button button;
    private Button buttons;
    private LatLng latLng;
    private boolean isFirstLoc = true; // 是否首次定位

    private String result, toast;

    private ImageView imageView;


    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //默认显示普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        //mBaiduMap.setTrafficEnabled(true);
        //开启热力图
        //mBaiduMap.setBaiduHeatMapEnabled(true);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        //配置定位SDK参数
        initLocation();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开启定位
        mLocationClient.start();
        //图片点击事件，回到定位点
        mLocationClient.requestLocation();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            // 当不需要定位图层时关闭定位图层
            //mBaiduMap.setMyLocationEnabled(false);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            }
        }
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.activity_main_mapview);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initView();
        initMap();
        imageView = findViewById(R.id.activity_main_locate_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
            }
        });

        //获取我要打伞按钮以及监听事
        btn_submit_um = findViewById(R.id.activity_main_button_forUmbrella);
        btn_submit_um.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //我要打伞的监听事件
                submit_um();
            }
        });

        //获取地点的输入以及监听事件
        edt_destination = findViewById(R.id.activity_main_destination);
        edt_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 输入地址的监听事件
            }
        });

        //获取我有伞以及监听事件
        textView_haveUm = findViewById(R.id.activity_main_textView_haveUm);
        textView_haveUm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_haveUm();
            }
        });

        account_image_toSelf = findViewById(R.id.activity_main_account_image);
        account_image_toSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击监听事件，跳转至个人中心页面
                Intent intent = new Intent(MainActivity.this, PersonalCenterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void submit_um() {
        final String mylocation = "浙江工业大学";
        final String destination = "印象城";
        final int type = 1;
        //获取sharedPreferences本地文件里的username值
        SharedPreferences sharedPreferences = getSharedPreferences("li.yanan", MODE_PRIVATE);
        final String username = sharedPreferences.getString("username", "");
        //网络请求
        new Thread(new Runnable() {//开启线程
            @Override
            public void run() {
                FormBody formBody = new FormBody.Builder()
                        .add("mylocation", mylocation)
                        .add("destination", destination)
                        .add("type", "1")
                        .add("username", username)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.81:5000/KH/v1/submit_um")
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

    private void submit_haveUm() {
        final String mylocation = "浙江工业大学";
        final String destination = "印象城";
        final int type = 1;
        //获取sharedPreferences本地文件里的username值
        SharedPreferences sharedPreferences = getSharedPreferences("li.yanan", MODE_PRIVATE);
        final String username = sharedPreferences.getString("username", "");
        //网络请求
        new Thread(new Runnable() {//开启线程
            @Override
            public void run() {
                FormBody formBody = new FormBody.Builder()
                        .add("mylocation", mylocation)
                        .add("destination", destination)
                        .add("type", "1")
                        .add("username", username)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.81:5000/KH/v1/submit_haveum")
                        .post(formBody)
                        .build();
                OkHttpClient okHttpClient = new OkHttpClient();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    result = response.body().string();
                    JX_haveum(result);//解析
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
            toast = jsonObject.getString("description");
            if (flag.equals("1")) {
                toast = jsonObject.getString("description");
                Intent intent = new Intent(MainActivity.this, UmWaitActivity.class);
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

    private void JX_haveum(String date) {
        try {
            JSONObject jsonObject = new JSONObject(date);
            String flag = jsonObject.getString("flag");//获取返回值flag的内容
            toast = jsonObject.getString("description");
            if (flag.equals("1")) {
                toast = jsonObject.getString("description");
                Intent intent = new Intent(MainActivity.this, HaveUmWaitActivity.class);
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
                    Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


}
