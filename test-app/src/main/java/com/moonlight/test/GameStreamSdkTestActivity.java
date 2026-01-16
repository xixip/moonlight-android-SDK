package com.moonlight.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wslight.WsLightSDK;
import com.wslight.nvstream.http.ComputerDetails;
import com.wslight.nvstream.http.NvApp;

import java.util.LinkedList;

/**
 * 演示如何使用 WsLightSDK 连接到 Sunshine 服务器
 */
public class GameStreamSdkTestActivity extends AppCompatActivity {

    private WsLightSDK wsLightSDK;
    private EditText serverAddressEditText;
    private TextView statusTextView;
    private Button checkServerButton;
    private Button pairServerButton;
    private Button getAppListButton;
    private Button startStreamingButton;

    private ComputerDetails pairedComputer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_stream_sdk_test);

        // 初始化视图组件
        serverAddressEditText = findViewById(R.id.server_address);
        statusTextView = findViewById(R.id.status_text);
        checkServerButton = findViewById(R.id.check_server_button);
        pairServerButton = findViewById(R.id.pair_server_button);
        getAppListButton = findViewById(R.id.get_app_list_button);
        startStreamingButton = findViewById(R.id.start_streaming_button);

        serverAddressEditText.setText("192.168.100.140");
        // 初始化 SDK
        wsLightSDK = new WsLightSDK(this, "test-client-id");

        // 设置按钮点击监听器
        checkServerButton.setOnClickListener(v -> checkServerStatus());
        pairServerButton.setOnClickListener(v -> pairServer());
        getAppListButton.setOnClickListener(v -> getAppList());
        startStreamingButton.setOnClickListener(v -> startStreaming());

        updateStatus("SDK 初始化成功！");
    }

    /**
     * 检查服务器状态
     */
    private void checkServerStatus() {
        String address = serverAddressEditText.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "请输入服务器地址", Toast.LENGTH_SHORT).show();
            return;
        }

        updateStatus("正在检查服务器状态...");
        checkServerButton.setEnabled(false);

        new Thread(() -> {
            boolean isOnline = wsLightSDK.checkServerOnline(address);
            runOnUiThread(() -> {
                if (isOnline) {
                    updateStatus("服务器在线！");
                    Toast.makeText(this, "服务器在线", Toast.LENGTH_SHORT).show();
                } else {
                    updateStatus("无法连接到服务器！");
                    Toast.makeText(this, "无法连接到服务器", Toast.LENGTH_SHORT).show();
                }
                checkServerButton.setEnabled(true);
            });
        }).start();
    }

    /**
     * 配对服务器
     */
    private void pairServer() {
        String address = serverAddressEditText.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "请输入服务器地址", Toast.LENGTH_SHORT).show();
            return;
        }

        updateStatus("正在配对服务器...");
        pairServerButton.setEnabled(false);

        new Thread(() -> {
            // 执行配对
            WsLightSDK.PairingResult result = wsLightSDK.pairServer(address);
            runOnUiThread(() -> {
                if (result.success) {
                    pairedComputer = result.computer;
                    updateStatus("配对成功！\n计算机名称: " + result.computer.name + "\nPIN码: " + result.pin);
                    Toast.makeText(this, "配对成功", Toast.LENGTH_SHORT).show();
                    getAppListButton.setEnabled(true);
                } else {
                    updateStatus("配对失败: " + result.message);
                    Toast.makeText(this, "配对失败: " + result.message, Toast.LENGTH_SHORT).show();
                }
                pairServerButton.setEnabled(true);
            });
        }).start();
    }

    /**
     * 获取应用列表
     */
    private void getAppList() {
        if (pairedComputer == null) {
            Toast.makeText(this, "请先配对服务器", Toast.LENGTH_SHORT).show();
            return;
        }

        updateStatus("正在获取应用列表...");
        getAppListButton.setEnabled(false);

        new Thread(() -> {
            WsLightSDK.AppListResult result = wsLightSDK.getAppList(pairedComputer);
            runOnUiThread(() -> {
                if (result.success) {
                    LinkedList<NvApp> apps = result.apps;
                    StringBuilder appList = new StringBuilder("应用列表:\n");
                    for (NvApp app : apps) {
                        appList.append(app.getAppName()).append(" (ID: " + app.getAppId() + ")\n");
                    }
                    updateStatus(appList.toString());
                    Toast.makeText(this, "获取到 " + apps.size() + " 个应用", Toast.LENGTH_SHORT).show();
                    startStreamingButton.setEnabled(true);
                } else {
                    updateStatus("获取应用列表失败: " + result.message);
                    Toast.makeText(this, "获取应用列表失败: " + result.message, Toast.LENGTH_SHORT).show();
                }
                getAppListButton.setEnabled(true);
            });
        }).start();
    }

    /**
     * 启动流式传输
     */
    private void startStreaming() {
        if (pairedComputer == null) {
            Toast.makeText(this, "请先配对服务器", Toast.LENGTH_SHORT).show();
            return;
        }

        updateStatus("正在获取应用列表...");
        startStreamingButton.setEnabled(false);

        new Thread(() -> {
            WsLightSDK.AppListResult appResult = wsLightSDK.getAppList(pairedComputer);
            runOnUiThread(() -> {
                if (appResult.success && !appResult.apps.isEmpty()) {
                    // 启动第一个应用
                    NvApp firstApp = appResult.apps.getFirst();
                    boolean started = wsLightSDK.startGameStream(pairedComputer, firstApp, false);
                    if (started) {
                        updateStatus("正在启动: " + firstApp.getAppName());
                        Toast.makeText(this, "正在启动: " + firstApp.getAppName(), Toast.LENGTH_SHORT).show();
                    } else {
                        updateStatus("启动失败");
                        Toast.makeText(this, "启动失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    updateStatus("获取应用列表失败: " + appResult.message);
                    Toast.makeText(this, "获取应用列表失败: " + appResult.message, Toast.LENGTH_SHORT).show();
                }
                startStreamingButton.setEnabled(true);
            });
        }).start();
    }

    /**
     * 更新状态显示
     */
    private void updateStatus(String message) {
        statusTextView.setText(message);
    }
}
