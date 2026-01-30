import sqlite3InitModule from './sqlite3.mjs';

const DB_NAME = 'TallyStorage';
const STORE_NAME = 'data';
const DB_KEY = 'tally_db_blob';
const DEBUG = false;

let db = null;
let sqlite3 = null;
let promiseChain = Promise.resolve();

const log = (...args) => { if (DEBUG) console.log('[SQLite Worker]', ...args); };
const error = (...args) => console.error('[SQLite Worker]', ...args);

/**
 * Helper to interact with IndexedDB (Available in Workers)
 */
const idb = {
    open: () => new Promise((resolve, reject) => {
        const request = indexedDB.open(DB_NAME, 1);
        request.onupgradeneeded = () => request.result.createObjectStore(STORE_NAME);
        request.onsuccess = () => resolve(request.result);
        request.onerror = () => reject(request.error);
    }),
    get: async () => {
        const db = await idb.open();
        return new Promise((resolve) => {
            const transaction = db.transaction(STORE_NAME, 'readonly');
            const request = transaction.objectStore(STORE_NAME).get(DB_KEY);
            request.onsuccess = () => resolve(request.result);
            request.onerror = () => resolve(null);
        });
    },
    set: async (blob) => {
        const db = await idb.open();
        return new Promise((resolve) => {
            const transaction = db.transaction(STORE_NAME, 'readwrite');
            const request = transaction.objectStore(STORE_NAME).put(blob, DB_KEY);
            request.onsuccess = () => resolve();
            request.onerror = () => error('Failed to save DB to IndexedDB');
        });
    }
};

/**
 * Exports the in-memory DB and saves it to IndexedDB
 */
async function persist() {
    if (!db || !sqlite3) return;
    try {
        const byteArray = sqlite3.capi.sqlite3_js_db_export(db.pointer);
        await idb.set(byteArray);
        log('Database persisted to IndexedDB');
    } catch (err) {
        error('Persistence failed:', err);
    }
}

/**
 * Initialize SQLite and load existing data
 */
const initPromise = sqlite3InitModule({ print: log, printErr: error }).then(async (instance) => {
    sqlite3 = instance;
    const oo = sqlite3.oo1;
    const binary = await idb.get();

    if (binary && binary.byteLength > 0) {
        log('Restoring existing database from IndexedDB...');
        const pcv = sqlite3.wasm.allocFromTypedArray(binary);
        db = new oo.DB();
        const rc = sqlite3.capi.sqlite3_deserialize(
            db.pointer, 'main', pcv, binary.byteLength, binary.byteLength,
            sqlite3.capi.SQLITE_DESERIALIZE_FREEONCLOSE | sqlite3.capi.SQLITE_DESERIALIZE_RESIZEABLE
        );
        log('Database restored successfully.');
    } else {
        log('No existing database found. Creating new in-memory instance.');
        db = new oo.DB();
    }
}).catch(err => {
    error('Initialization failed:', err);
});

/**
 * Worker message listener
 */
self.onmessage = (event) => {
    // We use a promise chain to ensure SQL commands execute in order
    promiseChain = promiseChain.then(async () => {
        try {
            await initPromise;
            await handleMessage(event);
        } catch (err) {
            self.postMessage({ id: event.data.id, error: err.message });
        }
    });
};

async function handleMessage(event) {
    const { id, action, sql, params } = event.data;

    try {
        let results = [];
        let shouldPersist = false;

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
                    shouldPersist = true; // Data changed
                }
                break;

            case 'begin_transaction':
                db.exec("BEGIN TRANSACTION;");
                results = [[0]];
                break;

            case 'end_transaction':
                db.exec("COMMIT;");
                results = [[0]];
                shouldPersist = true; // Commit finalized
                break;

            case 'rollback_transaction':
                db.exec("ROLLBACK;");
                results = [[0]];
                break;

            default:
                throw new Error(`Unknown action: ${action}`);
        }

        // Send response back to SQLDelight
        self.postMessage({
            id: id,
            results: { values: results }
        });

        // If data changed, save to IndexedDB asynchronously
        if (shouldPersist) {
            await persist();
        }

    } catch (err) {
        // Suppress "table already exists" errors common during KMP init
        if (sql && sql.includes('CREATE TABLE') && err.message.includes('already exists')) {
            self.postMessage({ id: id, results: { values: [[0]] } });
        } else {
            error(`SQL Error: ${err.message}`);
            self.postMessage({ id: id, error: err.message });
        }
    }
}