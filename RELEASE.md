# Release Procedures and Verification

## 1. Remote Build Trigger
GitHub Actions automatically builds and signs the production APK on every push to `main` or via manual `workflow_dispatch`.

## 2. Artifact Output
- Release APK: `lot-v1.0.0-release.apk`
- Checksums: `checksums.txt` (SHA-256)
- Signed using RSA 2048-bit keystore and verified via `apksigner`.
