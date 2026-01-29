#!/bin/bash
#
# Production CSP Injection Script
# 
# This script replaces the development CSP with a strict production CSP
# in the built index.html file after webpack completes.
#
# Usage: ./scripts/inject-production-csp.sh <path-to-index.html>
#

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

INDEX_HTML="${1:-build/dist/wasmJs/productionExecutable/index.html}"

if [ ! -f "$INDEX_HTML" ]; then
    echo -e "${YELLOW}Warning: index.html not found at $INDEX_HTML${NC}"
    echo "Usage: $0 <path-to-index.html>"
    exit 1
fi

echo -e "${GREEN}[Tally CSP]${NC} Injecting strict production CSP..."

# Strict production CSP (no unsafe-eval)
# Added blob: to connect-src and worker-src for WASM worker support
# Added unsafe-inline to script-src for production (some JS/Wasm glue needs it)
PRODUCTION_CSP="default-src 'self'; script-src 'self' 'unsafe-inline' 'wasm-unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: blob:; font-src 'self' data:; connect-src 'self' blob:; worker-src 'self' blob:; base-uri 'self'; form-action 'self';"

# Use sed to replace the entire CSP content in-place
# We match the entire meta tag and replace the content attribute
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    sed -i '' "s|content=\"default-src 'self'; script-src 'self' 'unsafe-eval' 'wasm-unsafe-eval'; [^\"]*\"|content=\"$PRODUCTION_CSP\"|g" "$INDEX_HTML"
else
    # Linux
    sed -i "s|content=\"default-src 'self'; script-src 'self' 'unsafe-eval' 'wasm-unsafe-eval'; [^\"]*\"|content=\"$PRODUCTION_CSP\"|g" "$INDEX_HTML"
fi

echo -e "${GREEN}[Tally CSP]${NC} ✅ Production CSP injected successfully"
echo -e "${GREEN}[Tally CSP]${NC} New policy: $PRODUCTION_CSP"

# Verify the change
if grep -q "unsafe-eval" "$INDEX_HTML"; then
    echo -e "${YELLOW}[Tally CSP]${NC} ⚠️  Warning: 'unsafe-eval' still present in CSP"
    exit 1
else
    echo -e "${GREEN}[Tally CSP]${NC} ✅ Verified: 'unsafe-eval' removed from production CSP"
fi
