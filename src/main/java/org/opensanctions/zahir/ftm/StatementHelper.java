package org.opensanctions.zahir.ftm;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.opensanctions.zahir.db.Store;
import org.opensanctions.zahir.db.StoreWriter;
import org.opensanctions.zahir.ftm.model.Model;
import org.opensanctions.zahir.ftm.model.Schema;
import org.rocksdb.RocksDBException;

public class StatementHelper {
    private final static Map<String, Instant> dateCache = new HashMap<>();

    protected static Instant parseDateTime(String dateTime) {
        if (dateCache.containsKey(dateTime)) {
            return dateCache.get(dateTime);
        }
        Instant instant = Instant.parse(dateTime + ".00Z");
        dateCache.put(dateTime, instant);
        return instant;
    }
    
    public static void loadStatementsFromCSVPath(Model model, Store store, String path) throws FileNotFoundException, RocksDBException {
        Reader reader = new FileReader(path);
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().build();
        // List<Statement> statements = new ArrayList<>();
        long count = 0;
        StoreWriter writer = store.getWriter("test", "ALPHA");
        System.out.println("Loading statements from " + path);
        try (CSVParser csvParser = new CSVParser(reader, format)) {
            for (CSVRecord record : csvParser) {
                count++;
                Schema schema = model.getSchema(record.get("schema"));
                if (schema == null) {
                    System.err.println("Schema not found: " + record.get("schema"));
                    continue;
                }
                String property = record.get("prop");
                Instant firstSeen = parseDateTime(record.get("first_seen"));
                Instant lastSeen = parseDateTime(record.get("last_seen"));
                boolean external = record.get("external").startsWith("t");

                Statement stmt = new Statement(record.get("id"), record.get("entity_id"), record.get("canonical_id"), schema, property, record.get("dataset"), record.get("value"), record.get("lang"), record.get("original_value"), external, firstSeen.getEpochSecond(), lastSeen.getEpochSecond());
                // writer.writeStatement(stmt);
                // statements.add(stmt);
                if (count > 0 && count % 100000 == 0) {
                    System.err.println(count);
                    // System.out.println();
                }
                
                // String entityId = record.get("entity_id");
                // System.err.println(id);
                // Process each record
            }
            System.err.println("Total: " + count);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
