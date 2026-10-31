package com.roni.library.platform.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.roni.library.contracts.api.IApiCallback;
import com.roni.library.contracts.api.NativeApiError;
import com.roni.library.contracts.api.NativeApiRequest;
import com.roni.library.contracts.api.NativeApiResponse;
import com.roni.library.platform.PlatformEngine;
import com.roni.library.platform.PlatformEngineConfig;
import com.roni.library.platform.model.BatteryStatusDTO;
import com.roni.library.platform.model.DeviceInfoDTO;
import com.roni.library.platform.model.NetworkStatusDTO;
import com.roni.library.platform.model.StorageStatsDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Interactive Companion Demonstration Activity for NativePlatformEngine.
 * Provides controls for testing Device Info, Battery, Network, Storage,
 * Clipboard, Toast, Haptics, and RPC command execution.
 *
 * @author infoadmstudio2020-cyber
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private TextView tvConsole;
    private ScrollView scrollConsole;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    private PlatformEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupPlatformEngine();
        setupListeners();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvConsole = findViewById(R.id.tv_console_output);
        scrollConsole = findViewById(R.id.scroll_console);
    }

    private void setupPlatformEngine() {
        appendLog("[INIT] Initializing NativePlatformEngine...");

        PlatformEngineConfig config = new PlatformEngineConfig.Builder()
                .debugEnabled(true)
                .tag("DemoApp")
                .autoMonitorBattery(true)
                .autoMonitorNetwork(true)
                .build();

        engine = PlatformEngine.getInstance();
        engine.init(this, config);

        // Register live listeners
        engine.getBatteryService().addListener(status -> mainHandler.post(() -> {
            appendLog("[BATTERY EVENT] Level=" + status.getLevel() + "%, Charging=" + status.isCharging());
            updateStatusBanner();
        }));

        engine.getNetworkService().addListener(status -> mainHandler.post(() -> {
            appendLog("[NETWORK EVENT] Type=" + status.getNetworkType() + ", Connected=" + status.isConnected());
            updateStatusBanner();
        }));

        updateStatusBanner();
        appendLog("[READY] NativePlatformEngine ready with all services active.");
    }

    private void updateStatusBanner() {
        if (engine != null) {
            BatteryStatusDTO battery = engine.getBatteryService().getBatteryStatus();
            String netType = engine.getNetworkService().getNetworkType().name();
            boolean netConnected = engine.getNetworkService().isConnected();

            String status = "State: " + engine.getState() +
                    " | Batt: " + (battery != null ? battery.getLevel() + "%" : "--") +
                    " | Net: " + netType + (netConnected ? " (Online)" : " (Offline)");
            tvStatus.setText(status);
        }
    }

    private void setupListeners() {
        Button btnDevice = findViewById(R.id.btn_device);
        Button btnBattery = findViewById(R.id.btn_battery);
        Button btnNetwork = findViewById(R.id.btn_network);
        Button btnStorage = findViewById(R.id.btn_storage);
        Button btnClipboard = findViewById(R.id.btn_clipboard);
        Button btnToast = findViewById(R.id.btn_toast);
        Button btnVibrate = findViewById(R.id.btn_vibrate);
        Button btnRpc = findViewById(R.id.btn_rpc);
        Button btnClear = findViewById(R.id.btn_clear);

        btnDevice.setOnClickListener(v -> showDeviceSpecs());
        btnBattery.setOnClickListener(v -> showBatteryInfo());
        btnNetwork.setOnClickListener(v -> showNetworkInfo());
        btnStorage.setOnClickListener(v -> showStorageStats());
        btnClipboard.setOnClickListener(v -> testClipboard());
        btnToast.setOnClickListener(v -> testToast());
        btnVibrate.setOnClickListener(v -> testVibrate());
        btnRpc.setOnClickListener(v -> testRpcCommand());
        btnClear.setOnClickListener(v -> tvConsole.setText("Console cleared.\n"));
    }

    private void showDeviceSpecs() {
        DeviceInfoDTO info = engine.getDeviceService().getDeviceInfo();
        appendLog("[DEVICE SPECS]\n" +
                "  Manufacturer: " + info.getManufacturer() + "\n" +
                "  Model: " + info.getModel() + "\n" +
                "  Brand: " + info.getBrand() + "\n" +
                "  Android OS: " + info.getOsVersion() + " (SDK " + info.getSdkInt() + ")\n" +
                "  Display: " + info.getScreenWidth() + "x" + info.getScreenHeight() + " @" + info.getScreenDensity() + "x\n" +
                "  Architecture: " + info.getArchitecture() + "\n" +
                "  RAM: " + (info.getFreeMemoryBytes() / 1024 / 1024) + "MB free / " + (info.getTotalMemoryBytes() / 1024 / 1024) + "MB total");
    }

    private void showBatteryInfo() {
        BatteryStatusDTO battery = engine.getBatteryService().getBatteryStatus();
        appendLog("[BATTERY STATUS]\n" +
                "  Level: " + battery.getLevel() + "%\n" +
                "  Charging: " + battery.isCharging() + "\n" +
                "  Plug Type: " + battery.getPlugType() + "\n" +
                "  Temperature: " + battery.getTemperature() + "°C\n" +
                "  Health: " + battery.getHealthStatus());
    }

    private void showNetworkInfo() {
        NetworkStatusDTO net = new NetworkStatusDTO(
                engine.getNetworkService().getNetworkType(),
                engine.getNetworkService().isConnected(),
                engine.getNetworkService().isMetered(),
                -60,
                System.currentTimeMillis()
        );
        appendLog("[NETWORK STATUS]\n" +
                "  Type: " + net.getNetworkType() + "\n" +
                "  Connected: " + net.isConnected() + "\n" +
                "  Metered: " + net.isMetered() + "\n" +
                "  Signal: " + net.getSignalDbm() + " dBm");
    }

    private void showStorageStats() {
        StorageStatsDTO storage = engine.getStorageService().getStorageStatsDTO();
        appendLog("[STORAGE STATS]\n" +
                "  Internal Total: " + (storage.getInternalTotalBytes() / 1024 / 1024) + " MB\n" +
                "  Internal Free: " + (storage.getInternalFreeBytes() / 1024 / 1024) + " MB\n" +
                "  Internal Used: " + (storage.getInternalUsedBytes() / 1024 / 1024) + " MB\n" +
                "  App Cache Size: " + (storage.getCacheSizeBytes() / 1024) + " KB");
    }

    private void testClipboard() {
        String testText = "NativePlatformEngine_CopyTest_" + System.currentTimeMillis();
        boolean set = engine.getClipboardService().setText(testText);
        String readBack = engine.getClipboardService().getText();
        appendLog("[CLIPBOARD TEST] SetSuccess=" + set + ", ReadBack='" + readBack + "'");
    }

    private void testToast() {
        engine.getToastService().showToast("Platform notification at " + System.currentTimeMillis(), false);
        appendLog("[TOAST] Dispatched notification message to screen.");
    }

    private void testVibrate() {
        engine.getToastService().vibrate(200);
        appendLog("[HAPTIC] Dispatched 200ms vibration effect.");
    }

    private void testRpcCommand() {
        String reqId = "rpc_" + UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> params = new HashMap<>();
        params.put("client", "DemoApp");

        NativeApiRequest request = new NativeApiRequest(reqId, "/platform", "diagnostic", params, System.currentTimeMillis());
        appendLog("--> RPC DISPATCH: [/platform] Action=diagnostic");

        engine.getCommandHandler().handle(request, new IApiCallback() {
            @Override
            public void onResponse(NativeApiResponse response) {
                mainHandler.post(() -> appendLog("<-- RPC SUCCESS: [" + response.getRequestId() + "] Data=" + response.getData()));
            }

            @Override
            public void onError(NativeApiError error) {
                mainHandler.post(() -> appendLog("<-- RPC ERROR: [" + reqId + "] Msg=" + error.getMessage()));
            }
        });
    }

    private void appendLog(String message) {
        String timestamp = dateFormat.format(new Date());
        String entry = "[" + timestamp + "] " + message + "\n";
        tvConsole.append(entry);
        scrollConsole.post(() -> scrollConsole.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.release();
        }
    }
}
