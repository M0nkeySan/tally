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

echo -e "${GREEN}[Tally CSP]${NC} Injecting strict production CSP..."

# Define the production CSP
export PRODUCTION_CSP="default-src 'self'; script-src 'self' 'unsafe-inline' 'wasm-unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: blob:; font-src 'self' data:; connect-src 'self' blob:; worker-src 'self' blob:; base-uri 'self'; form-action 'self';"

# Use Perl with a more relaxed regex for your specific HTML structure
# This matches: <meta http-equiv="Content-Security-Policy" (any whitespace) content="...">
perl -0777 -i -pe 's/<meta\s+http-equiv="Content-Security-Policy"[\s\n]+content="[^"]*">/<meta http-equiv="Content-Security-Policy" content="$ENV{PRODUCTION_CSP}">/gs' "$INDEX_HTML"

# VERIFICATION
# We check for 'unsafe-eval' (with quotes) to ensure the OLD one is gone
if grep -q "'unsafe-eval'" "$INDEX_HTML"; then
    echo -e "${YELLOW}[Tally CSP]${NC} ❌ Error: Original 'unsafe-eval' is still present."
    exit 1
else
    echo -e "${GREEN}[Tally CSP]${NC} ✅ Success: Production CSP injected and verified."
fi