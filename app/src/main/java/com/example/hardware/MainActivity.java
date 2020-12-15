package com.example.hardware;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.JsonObject;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    @BindView(R.id.tv_success)
    TextView tvSuccess;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.btn_copy)
    Button btnCopy;
    @BindView(R.id.tv_deny)
    TextView tvDeny;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.tv_deviceId)
    TextView tvDeviceId;
    @BindView(R.id.tv_hardware)
    EditText tvHardware;
    private Context context;
    public final static int REQUEST_READ_PHONE_STATE = 1;
    String imeiString = "";
    String uuId = "";
    String hardwareString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = getApplicationContext();
        requestPermission();

    }

    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_FINE_LOCATION,
                        Permission.ACCESS_COARSE_LOCATION,
                        Permission.READ_PHONE_STATE,
                        Permission.READ_PHONE_NUMBERS)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        imeiString = Hardware.getIMEI(context) == null ? "" : Hardware.getIMEI(context);
                        if (imeiString == null || imeiString.length() == 0) {
                            uuId = "UUid_" + SystemUtils.getUniqueIdentificationCode(context);
                            tvInfo.setText("你的任务码为：" + uuId);
                        } else {
                            tvInfo.setText("你的任务码为：" + imeiString);
                        }
                        submitCurHardwareInfo();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        tvDeny.setVisibility(View.VISIBLE);
                        tvSuccess.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }


    private void submitCurHardwareInfo() {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PHONE_STATE);
        } else {
            JsonObject hardwareRoot = Hardware.getObjHardwareInfo(this);
            AndPermission.with(MainActivity.this)
                    .runtime()
                    .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                    .onGranted(permissions -> {
                        boolean isSuccess = FileUtils.WriteStringToFile(hardwareString, Environment.getExternalStorageDirectory() + File.separator  + "hardwareInfo.txt");
                                if (isSuccess) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                            System.exit(0);
                                        }
                                    }, 5000);
                                }
                    })
                    .onDenied(permissions -> {
                        Toast.makeText(getApplicationContext(), "请给予任务存储权限", Toast.LENGTH_SHORT).show();
                    })
                    .start();
            String phoneNum = imeiString.length() == 0 ? uuId : imeiString;
            WebApiCall webApiCall2 = new WebApiCall(WebApiCall.SubmitHardwareInfo, hardwareRoot, requestHandler, phoneNum);
            Thread thread2 = new Thread(webApiCall2);
            thread2.start();

            WebApiCall webApiCall1 = new WebApiCall(WebApiCall.GetAndSetHardwareInfo, hardwareRoot, requestHandler, phoneNum);
            Thread thread1 = new Thread(webApiCall1);
            thread1.start();
            if (hardwareRoot != null) {
                hardwareString = hardwareRoot.toString();
            }
            Log.i("Submit:", hardwareRoot.toString());
//            tvHardware.setText(hardwareRoot.toString());
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler requestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String responseType = "";
            switch (msg.arg1) {
                case WebApiCall.SubmitHardwareInfo:
                    responseType = "提交硬件信息";
                    break;
                case WebApiCall.GetAndSetHardwareInfo:
                    responseType = "获取硬件信息";
                    break;
                case WebApiCall.GetSelfRealIMEI:
                    responseType = "获取IMEI";
                    break;
                default:
                    return;
            }

            switch (msg.what) {
                case WebApiCall.REQUEST_SUCCESS: {
                    String txtMsg = (String) msg.obj;
                    Log.i("handleMessage", "handleMessage: " + txtMsg);
                    tvSuccess.setVisibility(View.VISIBLE);
                    tvDeny.setVisibility(View.GONE);
                    break;
                }
                case WebApiCall.REQUEST_FAIL:
                    Toast.makeText(MainActivity.this, responseType + "任务失败！", Toast.LENGTH_LONG).show();
                    tvSuccess.setVisibility(View.VISIBLE);
                    tvSuccess.setText(("任务失败！"));
                    tvDeny.setVisibility(View.GONE);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("imei", "permission is granted after requested！");
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Log.d("imei", "permission is not granted after requested！");
                //这里表示申请权限后被用户拒绝了
            } else {
                Log.d("imei", "permission is not granted after requested！");
            }
        }
    }

    @OnClick({R.id.btn_copy, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(null, imeiString.length() == 0 ? uuId : imeiString);// 把数据复制到剪贴板
                clipboard.setPrimaryClip(clipData);
                break;
            case R.id.btn_submit:
                requestPermission();
                break;
        }
    }
}