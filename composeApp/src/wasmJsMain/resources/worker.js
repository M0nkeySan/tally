import sqlite3InitModule from '@sqlite.org/sqlite-wasm';

const DEBUG = false;

const log = (...args) => {
    if (DEBUG) console.log('[SQLite Worker]', ...args);
};
const error = (...args) => console.error('[SQLite Worker]', ...args);

let db = null;
let promiseChain = Promise.resolve();

log('Initializing SQLite WASM module...');

const initPromise = sqlite3InitModule({ print: log, printErr: error }).then(async (sqlite3) => {
    try {
        const oo = sqlite3.oo1;
        if (sqlite3.opfs) {
            log('OPFS is available. Opening persistent database: /tally.db');
            db = new oo.OpfsDb('/tally.db');
        } else {
            console.warn('[SQLite Worker] OPFS is NOT available. Falling back to in-memory database.');
            db = new oo.DB();
        }
        log('Database initialized successfully.');
    } catch (err) {
        error('Initialization failed:', err);
        throw err;
    }
}).catch(err => {
    error('Failed to initialize sqlite3 module:', err);
    throw err;
});

self.onmessage = (event) => {
    promiseChain = promiseChain.then(async () => {
        try {
            await initPromise;
            return handleMessage(event);
        } catch (err) {
            self.postMessage({ id: event.data.id, error: 'Database not initialized' });
        }
    });
};

async function handleMessage(event) {
    const { id, action, sql, params } = event.data;
    const start = performance.now();
    
    try {
        let results = [];
        switch (action) {
            case 'exec':
                const rows = [];
                db.exec({
                    sql: sql,
                    bind: params,
                    rowMode: 'array',

                    callback: (row) => rows.push(Array.from(row))
                });

                const isQuery = /^\s*(SELECT|PRAGMA)\b/i.test(sql);
                if (isQuery) {
                    results = rows;
                } else {
                    results = [[db.changes()]];
                }
                break;
                
            case 'begin_transaction':
                db.exec("BEGIN TRANSACTION;");
                results = [[0]];
                break;
                
            case 'end_transaction':
                db.exec("COMMIT;");
                results = [[0]];
                break;
                
            case 'rollback_transaction':
                db.exec("ROLLBACK;");
                results = [[0]];
                break;
                
            default:
                throw new Error(`Unknown action: ${action}`);
        }

        if (DEBUG) {
            const duration = (performance.now() - start).toFixed(2);
            log(`Executed ${action} in ${duration}ms: ${sql?.substring(0, 60)}${sql?.length > 60 ? '...' : ''}`);
        }

        self.postMessage({
            id: id,
            results: {
                values: results
            }
        });
        
    } catch (err) {
        if (sql && sql.includes('CREATE TABLE') && err.message.includes('already exists')) {
            log('Suppressed "table already exists" error for initialization SQL');
            self.postMessage({ 
                id: id, 
                results: { 
                    values: [[0]] 
                } 
            });
        } else {
            error(`Execution failed for action ${action}:`, err.message);
            self.postMessage({ id: id, error: err.message });
        }
    }
}
