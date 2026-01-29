/**
 * Custom webpack configuration for production builds.
 * 
 * This configuration is automatically merged with the default Kotlin/JS webpack config.
 * It enables production optimizations when NODE_ENV=production.
 * 
 * Key features:
 * - Minification and tree-shaking
 * - Production source maps
 * - Code splitting
 * - Strict CSP (no unsafe-eval) via csp-injection.js
 */

// Check if we're in production mode
if (process.env.NODE_ENV === 'production') {
    console.log('[Tally] Building in PRODUCTION mode with optimizations enabled');
    
    // Set webpack mode to production
    config.mode = 'production';
    
    // Use production-quality source maps (slower build, better for debugging production issues)
    config.devtool = 'source-map';
    
    // Enable performance hints
    config.performance = {
        hints: 'warning',
        maxEntrypointSize: 512000,  // 512kb
        maxAssetSize: 512000         // 512kb
    };
    
    // Add optimization settings
    config.optimization = {
        ...config.optimization,
        minimize: true,
        moduleIds: 'deterministic',
        runtimeChunk: 'single',
        splitChunks: {
            cacheGroups: {
                vendor: {
                    test: /[\\/]node_modules[\\/]/,
                    name: 'vendors',
                    chunks: 'all'
                }
            }
        }
    };
    
    console.log('[Tally] Production optimizations enabled');
    console.log('[Tally] - Minification: ON');
    console.log('[Tally] - Tree-shaking: ON');
    console.log('[Tally] - Code splitting: ON');
    console.log('[Tally] - Source maps: production');
    console.log('[Tally] - CSP: strict (no unsafe-eval)');
} else {
    console.log('[Tally] Building in DEVELOPMENT mode');
    console.log('[Tally] - Fast rebuilds with eval source maps');
    console.log('[Tally] - CSP: permissive (allows unsafe-eval)');
}
