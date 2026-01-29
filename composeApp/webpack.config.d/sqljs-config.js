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
//                {
//                    from: '../../node_modules/@sqlite.org/sqlite-wasm/sqlite-wasm/jswasm/sqlite3.js',
//                    to: 'sqlite3.js'
//                },
//                {
//                    from: '../../node_modules/@sqlite.org/sqlite-wasm/sqlite-wasm/jswasm/sqlite3.wasm',
//                    to: 'sqlite3.wasm'
//                }
            {
                from: '../../node_modules/sql.js/dist/sql-wasm.wasm',
                to: 'sql-wasm.wasm'
            },
            {
                from: '../../node_modules/@cashapp/sqldelight-sqljs-worker/sqljs.worker.js',
                to: 'sqljs.worker.js'
            }
        ]
    })
);