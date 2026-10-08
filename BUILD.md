# Build and Release Strategy

## Absolute Remote Build Architecture

In compliance with the project specifications, **no compilation, packaging, or signing operations are executed on personal hardware**. All build operations are handled remotely via GitHub Actions.

### Remote Pipeline Workflow (`.github/workflows/build-release.yml`)

1. **Virtual Runner Environment**:
   - OS: `ubuntu-latest`
   - Java: JDK 17 (Temurin distribution)
   - Android SDK Tools, Platform 34, Build Tools 34.0.0
   - Android NDK 26.x for native architecture targets

2. **Automated Release Signing**:
   - An ephemeral cryptographic keystore is generated in the CI environment using `keytool` with RSA 2048-bit keys.
   - Gradle builds the release APK via `assembleRelease`.
   - The generated APK is validated using `apksigner verify` and aligned using `zipalign`.

3. **Checksum & Publication**:
   - Computes SHA-256 checksum: `sha256sum lot-release.apk > checksums.txt`
   - Uploads build artifacts to GitHub Actions workflow artifacts.
   - Tags and publishes a GitHub Release with direct APK download links.
