#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

INDEX_HTML="${1:-build/dist/wasmJs/productionExecutable/index.html}"

if [ ! -f "$INDEX_HTML" ]; then
    echo -e "${YELLOW}Warning: index.html not found at $INDEX_HTML${NC}"
    exit 1
fi

echo -e "${GREEN}[Tally CSP]${NC} Injecting strict production CSP into $INDEX_HTML..."

# Strict production CSP
PRODUCTION_CSP="default-src 'self'; script-src 'self' 'unsafe-inline' 'wasm-unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: blob:; font-src 'self' data:; connect-src 'self' blob:; worker-src 'self' blob:; base-uri 'self'; form-action 'self';"

# ROBUST REPLACEMENT:
# This matches any meta tag with http-equiv="Content-Security-Policy"
# and replaces its entire content attribute.
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    sed -i '' "s|<meta http-equiv=\"Content-Security-Policy\" content=\"[^\"]*\">|<meta http-equiv=\"Content-Security-Policy\" content=\"$PRODUCTION_CSP\">|g" "$INDEX_HTML"
else
    # Linux
    sed -i "s|<meta http-equiv=\"Content-Security-Policy\" content=\"[^\"]*\">|<meta http-equiv=\"Content-Security-Policy\" content=\"$PRODUCTION_CSP\">|g" "$INDEX_HTML"
fi

# Verify the change
if grep -q "unsafe-eval" "$INDEX_HTML"; then
    echo -e "${YELLOW}[Tally CSP]${NC} ❌ Error: 'unsafe-eval' is still present in $INDEX_HTML"
    # Show what's actually in there to help debugging
    grep "Content-Security-Policy" "$INDEX_HTML"
    exit 1
else
    echo -e "${GREEN}[Tally CSP]${NC} ✅ Success: 'unsafe-eval' removed and production CSP injected."
fi