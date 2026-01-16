package com.moonlight.test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wslight.utils.RootChecker;

/**
 * 简化的 SDK 测试程序 只测试核心功能，避免依赖冲突
 */
public class SimpleTestActivity extends AppCompatActivity {

    private static final String TAG = "SimpleSDKTest";

    private TextView statusText;
    private Button testRootButton;
    private Button testSdkInitButton;
    private Button clearButton;
    private Button startGameStreamTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_test);

        // 初始化视图
        statusText = findViewById(R.id.status_text);
        testRootButton = findViewById(R.id.test_root_button);
        testSdkInitButton = findViewById(R.id.test_sdk_init_button);
        clearButton = findViewById(R.id.clear_button);
        startGameStreamTestButton = findViewById(R.id.start_game_stream_test_button);

        // 设置按钮点击监听器
        testRootButton.setOnClickListener(v -> testRootDetection());
        testSdkInitButton.setOnClickListener(v -> testSdkInitialization());
        clearButton.setOnClickListener(v -> clearStatus());
        startGameStreamTestButton.setOnClickListener(v -> startGameStreamTest());

        log("简化的 SDK 测试程序已启动");
        log("这个测试程序专注于核心功能验证");
    }

    /**
     * 测试 Root 检测功能
     */
    private void testRootDetection() {
        log("正在测试 Root 检测功能...");
        testRootButton.setEnabled(false);

        new Thread(() -> {
            try {
                // 测试 RootChecker 类
                boolean isRooted = RootChecker.isDeviceRooted();
                boolean canUseEvdev = RootChecker.canUseEvdevCapture();

                runOnUiThread(() -> {
                    String message = "Root 检测结果:\n";
                    message += "• 设备是否 Root: " + (isRooted ? "✅ 是" : "❌ 否") + "\n";
                    message += "• Evdev 支持: " + (canUseEvdev ? "✅ 可用" : "❌ 不可用");

                    log(message);
                    testRootButton.setEnabled(true);

                    Toast.makeText(this, "Root 检测完成", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    log("❌ Root 检测失败: " + e.getMessage());
                    Log.e(TAG, "Root 检测失败", e);
                    testRootButton.setEnabled(true);
                    Toast.makeText(this, "Root 检测失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /**
     * 测试 SDK 初始化
     */
    private void testSdkInitialization() {
        log("正在测试 SDK 初始化...");
        testSdkInitButton.setEnabled(false);

        new Thread(() -> {
            try {
                // 测试 WsLightSDK 初始化
                // 注意：这里只是测试类加载，不实际使用
                Class<?> sdkClass = Class.forName("com.wslight.WsLightSDK");

                runOnUiThread(() -> {
                    log("✅ SDK 类加载成功: " + sdkClass.getName());
                    log("✅ SDK 初始化测试通过");
                    testSdkInitButton.setEnabled(true);
                    Toast.makeText(this, "SDK 初始化测试通过", Toast.LENGTH_SHORT).show();
                });

            } catch (ClassNotFoundException e) {
                runOnUiThread(() -> {
                    log("❌ SDK 类未找到，请检查依赖");
                    log("错误: " + e.getMessage());
                    testSdkInitButton.setEnabled(true);
                    Toast.makeText(this, "SDK 类未找到", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    log("❌ SDK 初始化失败: " + e.getMessage());
                    Log.e(TAG, "SDK 初始化失败", e);
                    testSdkInitButton.setEnabled(true);
                    Toast.makeText(this, "SDK 初始化失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /**
     * 清除状态
     */
    private void clearStatus() {
        statusText.setText("");
        log("状态已清除");
    }

    /**
     * 启动 GameStream SDK 测试
     */
    private void startGameStreamTest() {
        log("正在启动 GameStream SDK 测试...");
        try {
            // 启动 GameStreamSdkTestActivity
            android.content.Intent intent = new android.content.Intent(this, GameStreamSdkTestActivity.class);
            startActivity(intent);
            log("✅ GameStream SDK 测试已启动");
        } catch (Exception e) {
            log("❌ 启动 GameStream SDK 测试失败: " + e.getMessage());
            Log.e(TAG, "启动 GameStream SDK 测试失败", e);
            Toast.makeText(this, "启动失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 记录日志
     */
    private void log(String message) {
        runOnUiThread(() -> {
            String currentText = statusText.getText().toString();
            String newLine = "[测试] " + message + "\n\n";
            statusText.setText(newLine + currentText);
            Log.i(TAG, message);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("测试程序已关闭");
    }
}
