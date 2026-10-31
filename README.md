# NativePlatformEngine

Hardware, battery, network, clipboard, and OS platform capability engine for Android.

[![JitPack](https://jitpack.io/v/infoadmstudio2020-cyber/native-platform-engine.svg)](https://jitpack.io/#infoadmstudio2020-cyber/native-platform-engine)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-Apache-2.0-blue.svg)](LICENSE)

## Architecture Overview

`NativePlatformEngine` operates at **Layer 2: Domain Engine Subsystems (Hardware & Platform Bridge)** within the NativeRuntime ecosystem.
It provides:
- **Device Hardware & Display Telemetry**: Brand, manufacturer, SoC architecture, screen resolution, density, and memory limits (`DevicePlatformService`, `DeviceInfoProvider`).
- **Battery & Thermal Monitoring**: State of charge, battery level, power source (AC/USB/Wireless), temperature, and health status with observer callbacks (`BatteryPlatformService`, `BatteryMonitor`).
- **Network Connectivity Bridge**: Real-time connection detection across WiFi, Cellular, Ethernet, VPN, and Bluetooth transports with metered state tracking (`NetworkPlatformService`, `NetworkStateMonitor`).
- **Storage Metrics & Cache Management**: Internal and external storage statistics, total/free/used bytes, and cache cleanup operations (`StoragePlatformService`).
- **System Clipboard Integration**: Safe, synchronized text copying, extraction, and clearance (`ClipboardPlatformService`).
- **Toast Notifications & Haptics**: Main-thread toast dispatch and vibration feedback with telemetry history (`ToastNotificationService`).
- **Universal RPC Command Handler**: Full implementation of Layer 0 `IApiHandler` responding to `/platform` commands (`PlatformCommandHandler`).
- **Ecosystem Service Registration**: Automatic registration with Layer 1 `ServiceRegistry` through `NativePlatformEngineService` (extending `BaseService`).

## Installation

### Step 1 — Add JitPack repository

In your root `settings.gradle`:

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2 — Add dependency

In your module's `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.infoadmstudio2020-cyber:native-platform-engine:1.0.0'

    // Ecosystem dependencies
    api 'com.github.infoadmstudio2020-cyber:NativeRuntimeContracts:v1.0.1'
    implementation 'com.github.infoadmstudio2020-cyber:NativeServiceSDK:1.0.1'
    compileOnly 'com.github.infoadmstudio2020-cyber:NativeLoggerSDK:1.0.1'
}
```

## Quick Start

### 1. Initialize Engine

```java
PlatformEngineConfig config = new PlatformEngineConfig.Builder()
    .debugEnabled(BuildConfig.DEBUG)
    .tag("PlatformEngine")
    .autoMonitorBattery(true)
    .autoMonitorNetwork(true)
    .build();

PlatformEngine.getInstance().init(context, config);
```

### 2. Query Device & Battery Telemetry

```java
PlatformEngine engine = PlatformEngine.getInstance();

// Device specifications
DeviceInfoDTO device = engine.getDeviceService().getDeviceInfo();
Log.d("Device", device.getManufacturer() + " " + device.getModel());

// Battery level & state
BatteryStatusDTO battery = engine.getBatteryService().getBatteryStatus();
Log.d("Battery", battery.getLevel() + "%, Charging: " + battery.isCharging());

// Network connectivity
NetworkType netType = engine.getNetworkService().getNetworkType();
boolean isOnline = engine.getNetworkService().isConnected();
```

### 3. Handle RPC Commands

```java
NativeApiRequest request = new NativeApiRequest(
    UUID.randomUUID().toString(),
    "/platform",
    "battery.status",
    Collections.emptyMap(),
    System.currentTimeMillis()
);

engine.getCommandHandler().handle(request, new IApiCallback() {
    @Override
    public void onResponse(NativeApiResponse response) {
        // Handle response payload
    }

    @Override
    public void onError(NativeApiError error) {
        // Handle error
    }
});
```

## Requirements

- Android minSdk 21
- compileSdk 34+
- Java 17

## Companion Demo App

The repository includes a companion demonstration application in the `app` module demonstrating:
- Real-time device hardware specifications and display metrics
- Live battery level and charging observer feedback
- Network connectivity changes across WiFi, Cellular, and offline modes
- Storage stats and cache clearance
- Toast notifications and vibration haptics
- RPC dispatching to `/platform`

## License

Apache License 2.0. See [LICENSE](LICENSE) for details.

