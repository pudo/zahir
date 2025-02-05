package org.opensanctions.zahir;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.opensanctions.zahir.db.Store;
import org.opensanctions.zahir.db.StoreView;
import org.opensanctions.zahir.ftm.entity.StatementEntity;
import org.opensanctions.zahir.ftm.model.Model;
import org.opensanctions.zahir.ftm.resolver.Linker;
import org.rocksdb.RocksDBException;

public class App {

    public static void main(String[] args) {
        try {
            Model model = Model.loadDefault();
            Store store = new Store(model, "/Users/pudo/Code/zahir/data/exp1");
            Linker linker = Linker.fromJsonPath("/Users/pudo/Code/operations/etl/data/resolve.ijson");

            System.out.println("Linker loaded: " + linker.size());
            // StatementHelper.loadStatementsFromCSVPath(model, store, "/Users/pudo/Data/statements.csv");
            StoreView view = store.getView(linker, List.of("test"));
            Optional<StatementEntity> entity = view.getEntity("Q7747");
            if (entity.isPresent()) {
                System.out.println(entity.get().getCaption());
            }
        } catch (RocksDBException | IOException re) {
            re.printStackTrace();
        }
    }
}
