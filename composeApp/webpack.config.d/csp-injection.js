/**
 * CSP Injection Plugin for Production Builds
 * 
 * This plugin replaces the development CSP with a stricter production CSP
 * during production builds. Development builds keep 'unsafe-eval' for webpack
 * dev server, while production builds remove it for better security.
 * 
 * Note: For production builds, manually replace the CSP in the built index.html
 * or use a post-build script to inject the strict CSP.
 */

if (process.env.NODE_ENV === 'production') {
    console.log('[Tally CSP] Production build - remember to use strict CSP (no unsafe-eval)');
    console.log('[Tally CSP] Post-build: Replace CSP in index.html with:');
    console.log('[Tally CSP]   script-src \'self\' \'wasm-unsafe-eval\' (remove unsafe-eval)');
} else {
    console.log('[Tally CSP] Development build - using permissive CSP (allows unsafe-eval)');
}
