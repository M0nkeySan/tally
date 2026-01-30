config.resolve = {
    fallback: {
        fs: false,
        path: false,
        crypto: false,
    }
};

const CopyWebpackPlugin = require('copy-webpack-plugin');
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            {
                from: '../../node_modules/@sqlite.org/sqlite-wasm/dist/sqlite3.wasm',
                to: 'sqlite3.wasm'
            },
            {
                from: '../../node_modules/@sqlite.org/sqlite-wasm/dist/index.mjs',
                to: 'sqlite3.mjs'
            }
        ]
    })
);
