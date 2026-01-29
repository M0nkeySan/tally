# WASM Build Guide

This guide explains how to build the Tally application for WebAssembly (WASM) in both development
and production modes.

## Development Build

Development builds include source maps and are optimized for debugging:

```bash
./gradlew wasmJsBrowserDevelopmentRun
```

This will:

- Build in development mode
- Start a dev server at http://localhost:8080
- Enable hot reload
- Include verbose source maps (`eval-source-map`)
- Open browser automatically

## Production Build

Production builds are optimized for size and performance:

```bash
# Build production bundle
NODE_ENV=production ./gradlew wasmJsBrowserProductionWebpack

# Inject strict CSP (removes unsafe-eval)
./composeApp/scripts/inject-production-csp.sh
```

This will:

- Build in production mode
- Enable minification and tree-shaking
- Generate optimized WASM binaries (30-40% smaller)
- Create production source maps
- Replace development CSP with strict production CSP
- Output to `build/dist/wasmJs/productionExecutable/`

### Build Output

After a production build, you'll find:

- `composeApp.js` - Main JavaScript bundle
- `Tally-composeApp.wasm` - WebAssembly binary
- `*.map` - Source map files
- `index.html` - Entry point

### Serving Production Build

To test the production build locally:

```bash
cd build/dist/wasmJs/productionExecutable
python3 -m http.server 8000
```

Then open http://localhost:8000 in your browser.

## Build Differences

| Feature       | Development       | Production        |
|---------------|-------------------|-------------------|
| Mode          | `development`     | `production`      |
| Minification  | ❌ No              | ✅ Yes             |
| Tree-shaking  | ❌ No              | ✅ Yes             |
| Source maps   | `eval-source-map` | `source-map`      |
| Binary size   | ~32 MB            | ~22-25 MB         |
| Build time    | Fast (~1-2 min)   | Slower (~3-5 min) |
| Debug logging | Removed           | Removed           |
| CSP headers   | ✅ Enabled         | ✅ Enabled         |

## Security Features

### Content Security Policy (CSP)

The application uses **conditional CSP** for optimal security and developer experience:

#### Development Builds
- **Policy**: Permissive (allows `unsafe-eval` for webpack dev server)
- **Location**: `index.html`
- **Directives**:
  - `script-src 'self' 'unsafe-eval' 'wasm-unsafe-eval'`
  - Allows webpack's eval-based source maps for fast rebuilds
  - Required for development server to function

#### Production Builds
- **Policy**: Strict (NO `unsafe-eval`)
- **Injection**: Via post-build script (`scripts/inject-production-csp.sh`)
- **Directives**:
  - `script-src 'self' 'unsafe-inline' 'wasm-unsafe-eval'`
  - Maximum security - blocks eval
  - Only allows WASM execution and necessary inline glue code

**Why Two Policies?**
- Development needs `unsafe-eval` for webpack's `eval-source-map`
- Production doesn't need it - uses regular source maps
- This gives you **fast dev builds** + **secure production**

### Other Security Features

- **No debug logging**: Console logs removed for cleaner output
- **Error tracking**: Critical errors still logged via `console.error`
- **Same-origin policy**: All resources must come from same domain
- **XSS protection**: Blocks external scripts and inline event handlers

## Troubleshooting

### Build fails with memory error

Increase Node.js heap size:

```bash
export NODE_OPTIONS="--max-old-space-size=4096"
./gradlew wasmJsBrowserProductionWebpack
```

### CSP blocks resources

If you see CSP violations in the browser console:

**Problem**: `call to eval() blocked by CSP`
- **Solution**: You're running a development build. This is expected.
- **Verify**: Check that `index.html` includes `'unsafe-eval'` in CSP
- **If missing**: The CSP should be:
  ```
  script-src 'self' 'unsafe-eval' 'wasm-unsafe-eval'
  ```

**Problem**: Production build not loading
- **Solution**: Make sure you built with `NODE_ENV=production`
- **Verify**: Check that production CSP was injected (no `unsafe-eval`)
- **Check**: Open DevTools → Network → index.html → Preview CSP meta tag

**Problem**: Resources blocked from external domains
- **This is intentional**: CSP blocks external resources for security
- **Solution**: Host all resources on the same domain as your app

### Large bundle size

The WASM binary is large due to:

- Compose UI framework
- Skiko graphics library
- Kotlin stdlib

This is expected for Compose Multiplatform apps.

### Data Persistence (SQLDelight & OPFS)

The application uses **SQLDelight** for data persistence. On the web platform, it leverages the **Origin Private File System (OPFS)** for robust and performant storage.

- **Storage Type**: SQLite (via official SQLite WASM)
- **VFS**: OPFS (Persistent across sessions)
- **Worker**: Background thread for database operations
- **Location**: `/tally.db` in OPFS

**Important**: OPFS requires specific security headers in some environments (though modern browsers are becoming more permissive). If you encounter storage issues, ensure your server sends:
- `Cross-Origin-Opener-Policy: same-origin`
- `Cross-Origin-Embedder-Policy: require-corp`

---

## CI/CD Integration

The project includes a GitHub Actions workflow to automatically build and deploy the WASM application to GitHub Pages.

### Automatic Deployment

Every push to the `main` branch triggers the `.github/workflows/deploy-wasm.yml` workflow.

### Manual Setup for GitHub Pages

To enable deployment, follow these steps in your GitHub repository:

1. Go to **Settings > Pages**.
2. Under **Build and deployment > Source**, select **GitHub Actions**.
3. Once the workflow finishes, your site will be live at `https://<username>.github.io/<repository-name>/`.

### Manual CI/CD Steps

If you use a different CI/CD system, the standard steps are:

```bash
# 1. Build with production optimizations
NODE_ENV=production ./gradlew wasmJsBrowserProductionWebpack

# 2. Inject strict CSP
./composeApp/scripts/inject-production-csp.sh ./composeApp/build/dist/wasmJs/productionExecutable/index.html

# 3. Deploy the contents of this directory:
# composeApp/build/dist/wasmJs/productionExecutable/
```

## Additional Resources

- [Kotlin/Wasm Documentation](https://kotlinlang.org/docs/wasm-overview.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Webpack Configuration](https://webpack.js.org/configuration/)
