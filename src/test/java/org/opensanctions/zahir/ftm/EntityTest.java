package org.opensanctions.zahir.ftm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensanctions.zahir.ftm.model.Model;
import org.opensanctions.zahir.ftm.model.Schema;

public class EntityTest {
    private static Model model;
    private static final String ENTITY_ID = "entity1";
    private static final String CANONICAL_ID = "canon1";
    private static final String DATASET = "test";
    private static final String LANG = "eng";
    

    @BeforeAll
    public static void setUp() throws IOException {
        model = Model.loadDefault();
    }
    
    @Test
    public void testEntityFromStatements() {
        Schema schema = model.getSchema("Person");
        Statement stmt1 = new Statement("a1", ENTITY_ID, CANONICAL_ID, schema, 
            "name", DATASET, "Harry Smith", "", "", false, 100L, 200L);
        Statement stmt1b = new Statement("aaa", ENTITY_ID, CANONICAL_ID, schema, 
            "name", DATASET, "Harry M. Smith", "", "", false, 100L, 200L);
        Statement stmt2 = new Statement("aab", ENTITY_ID, CANONICAL_ID, schema, 
            "country", DATASET, "gb", null, null, false, 100L, 200L);
        Statement stmt3 = new Statement("deadbeef", ENTITY_ID, CANONICAL_ID, schema, 
            "birthDate", DATASET, "1980-01-15", null, null, false, 100L, 200L);
        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmt1);
        stmts.add(stmt1b);
        stmts.add(stmt2);
        Entity entity = Entity.fromStatements(stmts);
        assertTrue(entity.has(schema.getProperty("name")));
        assertEquals(entity.getStatements(schema.getProperty("name")).size(), 2);
        assertFalse(entity.has(schema.getProperty("birthDate")));
        entity.addStatement(stmt3);
        assertTrue(entity.has(schema.getProperty("birthDate")));
    }
}
