package io.github.dmitrysulman.hibernate;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.Configuration;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.hibernate.reactive.provider.Settings;
import org.hibernate.tool.schema.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class Tests {
    @Container
    private static final MySQLContainer mysql = new MySQLContainer("mysql:8.4.7");

    private Mutiny.SessionFactory sessionFactory;

    @BeforeEach
    void init() {
        Configuration configuration = new Configuration();
        configuration.setProperty(Settings.HBM2DDL_AUTO, Action.ACTION_CREATE);
        configuration.setProperty(Settings.JAKARTA_JDBC_URL, mysql.getJdbcUrl());
        configuration.setProperty(Settings.JAKARTA_JDBC_USER, mysql.getUsername());
        configuration.setProperty(Settings.JAKARTA_JDBC_PASSWORD, mysql.getPassword());
        configuration.addAnnotatedClasses(JsonObjectEntity.class, JsonObjectListEntity.class);

        StandardServiceRegistry registry = new ReactiveServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        sessionFactory = configuration.buildSessionFactory(registry).unwrap(Mutiny.SessionFactory.class);
    }

    @Test
    void testJsonObjectEntity() {
        JsonObject jsonObject = new JsonObject(
                "prop1",
                123L,
                List.of("s1", "s2"),
                Map.of("k1", "v2", "k2", Map.of("k3", "v3"), "k4", List.of("v5", "v6"))
        );
        JsonObjectEntity entity = new JsonObjectEntity(null, jsonObject);
        sessionFactory.withTransaction(transaction ->
                transaction
                        .persist(entity)
                        .replaceWith(entity)
        ).await().indefinitely();

        JsonObjectEntity fetched = sessionFactory.withSession(session -> session.find(JsonObjectEntity.class, entity.id))
                .await().indefinitely();

        assertEquals(fetched.jsonObject.stringProperty, entity.jsonObject.stringProperty);
        assertEquals(fetched.jsonObject.longProperty, entity.jsonObject.longProperty);
        assertEquals(fetched.jsonObject.listProperty, entity.jsonObject.listProperty);
        assertEquals(fetched.jsonObject.mapProperty, entity.jsonObject.mapProperty);
    }

    @Test
    void testJsonObjectListEntity() {
        List<JsonObject> jsonObjects = List.of(
                new JsonObject(
                        "prop1",
                        123L,
                        List.of("s1", "s2"),
                        Map.of("k1", "v2", "k2", Map.of("k3", "v3"), "k4", List.of("v5", "v6"))
                )
        );
        JsonObjectListEntity entity = new JsonObjectListEntity(null, jsonObjects);
        sessionFactory.withTransaction(transaction ->
                transaction
                        .persist(entity)
                        .replaceWith(entity)
        ).await().indefinitely();

        JsonObjectListEntity fetched = sessionFactory.withSession(session -> session.find(JsonObjectListEntity.class, entity.id))
                .await().indefinitely();

        assertEquals(fetched.jsonObjects, entity.jsonObjects);
    }
}
