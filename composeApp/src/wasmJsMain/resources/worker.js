import sqlite3InitModule from '@sqlite.org/sqlite-wasm';

const log = (...args) => console.log('[SQLite Worker]', ...args);
const error = (...args) => console.error('[SQLite Worker]', ...args);

let db = null;
const queue = [];

log('Initializing SQLite WASM module...');

sqlite3InitModule({ print: log, printErr: error }).then((sqlite3) => {
    try {
        log('SQLite WASM module loaded. Checking OPFS...');
        const oo = sqlite3.oo1;
        
        if (sqlite3.opfs) {
            log('OPFS is available. Opening persistent database: /tally.db');
            db = new oo.OpfsDb('/tally.db');
        } else {
            console.warn('[SQLite Worker] OPFS is NOT available. Falling back to in-memory database.');
            db = new oo.DB();
        }
        
        log('Database initialized successfully.');
        
        while (queue.length > 0) {
            const msg = queue.shift();
            log('Processing queued message:', msg.data.action);
            handleMessage(msg);
        }
    } catch (err) {
        error('Initialization failed:', err);
    }
}).catch(err => {
    error('Failed to initialize sqlite3 module:', err);
});

self.onmessage = (event) => {
    if (!db) {
        log('Worker not ready, queuing message:', event.data.action);
        queue.push(event);
        return;
    }
    handleMessage(event);
};

function handleMessage(event) {
    const { id, action, sql, params } = event.data;
    
    try {
        let results = [];
        switch (action) {
            case 'exec':
                const rows = [];
                db.exec({
                    sql: sql,
                    bind: params,
                    rowMode: 'array',
                    callback: (row) => rows.push(row)
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
        }

        self.postMessage({
            id: id,
            results: {
                values: results
            }
        });
        
    } catch (err) {
        if (sql && sql.includes('CREATE TABLE') && err.message.includes('already exists')) {
            self.postMessage({ 
                id: id, 
                results: { 
                    values: [[0]] 
                } 
            });
        } else {
            self.postMessage({ id: id, error: err.message });
        }
    }
}
