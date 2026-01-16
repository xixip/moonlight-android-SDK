package com.wslight;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.wslight.WsLightSDK.ConnectionResult;
import com.wslight.WsLightSDK.PairingResult;
import com.wslight.binding.crypto.AndroidCryptoProvider;
import com.wslight.nvstream.http.ComputerDetails;
import com.wslight.nvstream.http.LimelightCryptoProvider;
import com.wslight.nvstream.http.NvApp;
import com.wslight.nvstream.http.NvHTTP;
import com.wslight.nvstream.http.PairingManager;
import com.wslight.nvstream.http.PairingManager.PairState;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * WsLight Android SDK - 游戏流式传输 SDK 专门支持 Sunshine 服务器
 */
public class WsLightSDK {

    private static final String TAG = "WsLightSDK";
    private Context context;
    private String clientId;
    private LimelightCryptoProvider cryptoProvider;
    private PairingResult pairingResult;

    /**
     * 初始化 SDK
     *
     * @param context  Android 上下文
     * @param clientId 客户端唯一标识符
     */
    public WsLightSDK(Context context, String clientId) {
        this.context = context.getApplicationContext();
        this.clientId = clientId;
        this.cryptoProvider = new AndroidCryptoProvider(this.context);
    }

    /**
     * 检查服务器是否在线
     *
     * @param address 服务器地址
     * @return true 表示服务器在线
     */
    public boolean checkServerOnline(String address) {
        try {
            NvHTTP http = new NvHTTP(address, clientId, null, cryptoProvider);
            http.getServerInfo();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "检查服务器状态失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 自动连接并配对Sunshine服务器 该方法会自动完成发现、配对和连接的整个过程
     *
     * @param address 服务器地址
     * @return 连接结果
     */
    public PairingResult pairServer(String address) {
        PairingResult result = new PairingResult();
        try {
            // 检查服务器是否在线
            if (!checkServerOnline(address)) {
                result.success = false;
                result.message = "服务器未在线";
                return result;
            }

            // 生成PIN码
            String pin = generatePin();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<PairingResult> future = executor.submit(() -> startPairServer(address, pin));
            do {
                Thread.sleep(200);
                if (!submitPin(address, pin)) {
                    result.success = false;
                    result.message = "提交PIN码失败";
                    break;
                }

                try {
                    result = future.get(1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    continue;
                }
                break;
            } while (true);

            // 执行配对
            pairingResult = result;
        } catch (Exception e) {
            result.success = false;
            result.message = "连接过程中发生错误: " + e.getMessage();
            Log.e(TAG, "自动连接失败", e);
        }
        return result;
    }

    class SSLTrustAllUtils {
        public javax.net.ssl.X509TrustManager getTrustAllX509TrustManager() {
            return new javax.net.ssl.X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            };
        }

        public javax.net.ssl.SSLSocketFactory getTrustAllSSLSocketFactory() {
            try {
                javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
                sslContext.init(null, new javax.net.ssl.TrustManager[] { getTrustAllX509TrustManager() },
                        new java.security.SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException("构建 SSLSocketFactory 失败", e);
            }
        }

        public javax.net.ssl.HostnameVerifier getTrustAllHostnameVerifier() {
            return (hostname, session) -> true;
        }
    }

    /**
     * 提交 PIN 码到 Sunshine 服务器
     *
     * @param address 服务器地址
     * @param pin     PIN 码
     * @return true 表示提交成功
     */
    public boolean submitPin(String address, String pin) {
        try {
            // Sunshine 服务器的 PIN 码提交 API
            String url = "https://" + address + ":47990/api/pin";

            // 创建 HTTP 客户端，设置超时
            SSLTrustAllUtils trustAllUtils = new SSLTrustAllUtils();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .sslSocketFactory(trustAllUtils.getTrustAllSSLSocketFactory(),
                            trustAllUtils.getTrustAllX509TrustManager())
                    .hostnameVerifier(trustAllUtils.getTrustAllHostnameVerifier())
                    .build();

            // 根据 Sunshine API 文档创建请求体
            // JSON 格式: {"pin": "1234", "name": "Friendly Client Name"}
            // 或表单格式: pin=1234
            // 这里使用表单格式，因为它更简单
            String jsonBody = "{\"pin\": \"" + pin + "\", \"name\": \"WsLightSDK\"}";
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    jsonBody);

            // 创建请求
            String authStr = "admin" + ":" + "123456";
            byte[] authBytes = authStr.getBytes(StandardCharsets.UTF_8);
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Authorization", "Basic " + Base64.encodeToString(authBytes, Base64.NO_WRAP))
                    .build();

            // 执行请求
            Response response = client.newCall(request).execute();

            // 检查响应状态
            boolean success = response.isSuccessful();
            int responseCode = response.code();

            Log.i(TAG, "提交 PIN 码到 Sunshine 服务器: " + pin
                    + "，状态码: " + responseCode
                    + "，成功: " + success);

            // 如果需要，可以读取响应体
            if (!success && response.body() != null) {
                String responseBody = response.body().string();
                Log.w(TAG, "PIN 提交失败响应: " + responseBody);
            }

            response.close();
            return success;

        } catch (Exception e) {
            Log.e(TAG, "提交 PIN 码失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 配对服务器
     *
     * @param address 服务器地址
     * @param pin     PIN 码
     * @return 配对结果
     */
    public PairingResult startPairServer(String address, String pin) {
        PairingResult result = new PairingResult();

        try {
            NvHTTP http = new NvHTTP(address, clientId, null, cryptoProvider);
            String serverInfo = http.getServerInfo();
            ComputerDetails computer = http.getComputerDetails();

            PairingManager pairingManager = http.getPairingManager();
            PairState pairState = pairingManager.pair(serverInfo, pin);

            if (pairState == PairState.PAIRED) {
                result.success = true;
                result.computer = computer;
                result.serverCert = pairingManager.getPairedCert();
                result.pin = pin;
                result.message = "配对成功";
            } else {
                result.success = false;
                result.message = "配对失败: " + pairState;
            }

        } catch (Exception e) {
            result.success = false;
            result.message = "配对失败: " + e.getMessage();
            Log.e(TAG, "配对服务器失败", e);
        }

        return result;
    }

    /**
     * 生成 PIN 码
     *
     * @return 4位 PIN 码
     */
    public String generatePin() {
        return PairingManager.generatePinString();
    }

    /**
     * 获取应用列表
     *
     * @param computer 计算机信息
     * @return 应用列表结果
     */
    public AppListResult getAppList(ComputerDetails computer) {
        AppListResult result = new AppListResult();

        try {
            NvHTTP http = new NvHTTP(computer.localAddress, clientId, pairingResult.serverCert, cryptoProvider);
            LinkedList<NvApp> apps = http.getAppList();
            result.success = true;
            result.apps = apps;
        } catch (Exception e) {
            result.success = false;
            result.message = "获取应用列表失败: " + e.getMessage();
            Log.e(TAG, "获取应用列表失败", e);
        }

        return result;
    }

    /**
     * 启动游戏流式传输
     *
     * @param computer  计算机信息
     * @param app       应用信息
     * @param enableHdr 是否启用 HDR
     * @return true 表示启动成功
     */
    public boolean startGameStream(ComputerDetails computer, NvApp app, boolean enableHdr) {
        try {
            Log.i(TAG, "启动游戏流式传输: " + app.getAppName());
            
            // 检查配对结果
            if (pairingResult == null || !pairingResult.success) {
                Log.e(TAG, "请先配对服务器");
                return false;
            }
            
            // 创建启动 Game Activity 的 Intent
            Intent intent = new Intent(context, Game.class);
            
            // 设置必要的参数
            intent.putExtra(Game.EXTRA_HOST, computer.localAddress);
            intent.putExtra(Game.EXTRA_APP_NAME, app.getAppName());
            intent.putExtra(Game.EXTRA_APP_ID, app.getAppId());
            intent.putExtra(Game.EXTRA_APP_HDR, enableHdr);
            intent.putExtra(Game.EXTRA_UNIQUEID, clientId);
            intent.putExtra(Game.EXTRA_PC_UUID, computer.uuid);
            intent.putExtra(Game.EXTRA_PC_NAME, computer.name);
            
            // 如果有服务器证书，也传过去
            if (pairingResult.serverCert != null) {
                try {
                    intent.putExtra(Game.EXTRA_SERVER_CERT, pairingResult.serverCert.getEncoded());
                } catch (Exception e) {
                    Log.w(TAG, "无法编码服务器证书: " + e.getMessage());
                }
            }
            
            // 设置启动标志，确保在新的任务中启动
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            // 启动 Game Activity
            context.startActivity(intent);
            
            Log.i(TAG, "游戏流式传输已启动: " + app.getAppName());
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "启动游戏流式传输失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 连接结果类
     */
    public static class ConnectionResult {

        public boolean success;
        public ComputerDetails computer;
        public java.security.cert.X509Certificate serverCert;
        public String pin;
        public String message;
    }

    /**
     * 配对结果类
     */
    public static class PairingResult {

        public boolean success;
        public ComputerDetails computer;
        public java.security.cert.X509Certificate serverCert;
        public String pin;
        public String message;
    }

    /**
     * 应用列表结果类
     */
    public static class AppListResult {

        public boolean success;
        public LinkedList<NvApp> apps;
        public String message;
    }
}
