# Changelog

All notable changes to NativePlatformEngine are documented here.
Format based on [Keep a Changelog](https://keepachangelog.com).

## [1.0.0] — 2026-10-31

### Added
- Complete Layer 2 Domain Engine implementation for hardware and platform bridging.
- `PlatformEngine` coordinator with thread-safe singleton state machine and `IDisposable` lifecycle.
- `DevicePlatformService` & `DeviceInfoProvider` for hardware specifications, SoC architecture, and screen metrics.
- `BatteryPlatformService` & `BatteryMonitor` with real-time level, charging state, plug type, and temperature monitoring.
- `NetworkPlatformService` & `NetworkStateMonitor` for connectivity detection across WiFi, Cellular, VPN, and Ethernet.
- `StoragePlatformService` for total, free, and used storage metrics, plus cache cleanup operations.
- `ClipboardPlatformService` for clipboard read, write, and clear operations.
- `ToastNotificationService` for UI toast dispatch, vibration haptic feedback, and notification telemetry history.
- `PlatformCommandHandler` implementing Layer 0 `IApiHandler` for `/platform` command dispatching.
- `NativePlatformEngineService` integrating `PlatformEngine` with Layer 1 `ServiceRegistry` (extending `BaseService`).
- Convenience provider and monitor classes: `DeviceInfoProvider`, `BatteryMonitor`, `NetworkStateMonitor`, `SensorHub`.
- Standard exceptions `PlatformEngineException` and `PlatformServiceUnavailableException`.
- Models & DTOs: `BatteryStatusDTO`, `DeviceInfoDTO`, `NetworkStatusDTO`, `NetworkType`, `StorageStatsDTO`, `ToastRecord`.
- Companion demonstration app in `app` module with live UI controls and visual telemetry feedback.
- Comprehensive automated JVM unit test suites (`PlatformEngineTest`, `PlatformServicesTest`, `PlatformCommandHandlerTest`, `MonitorsAndHubTest`).
- Updated GitHub Actions CI/CD workflows with write permissions and `softprops/action-gh-release@v2`.
- JitPack publication configuration.

