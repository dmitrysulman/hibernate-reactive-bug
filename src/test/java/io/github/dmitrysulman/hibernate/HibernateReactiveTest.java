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
class HibernateReactiveTest {
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
        PlainObject object = new PlainObject(
                "prop1",
                123L,
                List.of("s1", "s2"),
                Map.of("k1", "v2", "k2", Map.of("k3", "v3"), "k4", List.of("v5", "v6"))
        );
        JsonObjectEntity entity = new JsonObjectEntity(null, object);
        sessionFactory.withTransaction(transaction -> transaction.persist(entity))
                .await().indefinitely();

        JsonObjectEntity fetched = sessionFactory
                .withSession(session -> session.find(JsonObjectEntity.class, entity.id))
                .await().indefinitely();

        assertEquals(object.stringProperty, fetched.jsonObject.stringProperty);
        assertEquals(object.longProperty, fetched.jsonObject.longProperty);
        assertEquals(object.listProperty, fetched.jsonObject.listProperty);
        assertEquals(object.mapProperty, fetched.jsonObject.mapProperty);
    }

    @Test
    // Fails
    void testJsonObjectListEntity() {
        List<PlainObject> objects = List.of(
                new PlainObject(
                        "prop1",
                        123L,
                        List.of("s1", "s2"),
                        Map.of("k1", "v2", "k2", Map.of("k3", "v3"), "k4", List.of("v5", "v6"))
                ),
                new PlainObject(
                        "prop2",
                        456L,
                        List.of("s3", "s4"),
                        Map.of("k", "v")
                )
        );
        JsonObjectListEntity entity = new JsonObjectListEntity(null, objects);
        sessionFactory.withTransaction(transaction -> transaction.persist(entity))
                .await().indefinitely();

        JsonObjectListEntity fetched = sessionFactory
                .withSession(session -> session.find(JsonObjectListEntity.class, entity.id))
                .await().indefinitely();

        assertEquals(objects, fetched.jsonList);
    }
}
