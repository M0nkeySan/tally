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

# Define the production CSP
export PRODUCTION_CSP="default-src 'self'; script-src 'self' 'unsafe-inline' 'wasm-unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: blob:; font-src 'self' data:; connect-src 'self' blob:; worker-src 'self' blob:; base-uri 'self'; form-action 'self';"

# Use Perl to handle multi-line replacement
# -0777: Slurps the whole file into memory
# -i: Edit in-place
# -pe: Execute the regex and print
# The regex finds the meta tag regardless of newlines (\s+) and replaces it.
perl -0777 -i -pe 's/<meta\s+http-equiv="Content-Security-Policy"\s+content="[^"]*">/<meta http-equiv="Content-Security-Policy" content="$ENV{PRODUCTION_CSP}">/gs' "$INDEX_HTML"

# Verify the change
if grep -q "unsafe-eval" "$INDEX_HTML"; then
    echo -e "${YELLOW}[Tally CSP]${NC} ❌ Error: 'unsafe-eval' is still present in $INDEX_HTML"
    echo "Current Content-Security-Policy tag found:"
    # This grep helps debug by showing the line with its context
    grep -A 2 "Content-Security-Policy" "$INDEX_HTML"
    exit 1
else
    echo -e "${GREEN}[Tally CSP]${NC} ✅ Success: 'unsafe-eval' removed from production CSP."
fi