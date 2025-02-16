package org.opensanctions.zahir.server;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.opensanctions.zahir.Config;
import org.opensanctions.zahir.db.Store;
import org.opensanctions.zahir.resolver.Linker;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.followthemoney.exc.ViewException;
import tech.followthemoney.model.Model;

public class ZahirManager {
    private final static Logger log = LoggerFactory.getLogger(ZahirManager.class);
    private final Model model;
    private final Store store;

    private Map<String, ViewSession> sessions = new HashMap<>();

    // TODO: linker instances should be view session-scoped and managed by the server
    protected final Linker linker;

    public ZahirManager() throws IOException{
        this.model = Model.loadDefault();
        Path dataPath = Paths.get("").toAbsolutePath().resolve(Config.DATA_PATH);
        this.store = new Store(model, dataPath.toString());
        log.info("Store initialized at: {}", store.getPath());
        this.linker = Linker.fromJsonPath("/Users/pudo/Code/operations/etl/data/resolve.ijson");
        this.sessions = new HashMap<>();
    }

    protected Store getStore() {
        return store;
    }

    public Model getModel() {
        return model;
    }

    public ViewSession createSession(Map<String, String> scope, boolean unResolved, boolean withExternal) throws RocksDBException{
        ViewSession session = new ViewSession(this, scope, unResolved, withExternal);
        sessions.put(session.getId(), session);
        return session;
    }

    public ViewSession getSession(String id) {
        return sessions.get(id);
    }

    public Optional<ViewSession> closeSession(String id) throws ViewException {
        ViewSession session = sessions.remove(id);
        if (session != null) {
            session.close();
        }
        return Optional.ofNullable(session);
    }

    public void shutdown() {
        for (ViewSession session : sessions.values()) {
            try {
                session.close();
            } catch (ViewException e) {
                log.error("Failed to close session: {}", e.getMessage());
            }
        }
        store.close();
        log.info("ZahirManager was shut down.");
    }
}
