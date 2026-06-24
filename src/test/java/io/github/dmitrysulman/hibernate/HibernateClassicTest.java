package io.github.dmitrysulman.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
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
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@Testcontainers
class HibernateClassicTest {
    @Container
    private static final MySQLContainer mysql = new MySQLContainer("mysql:8.4.7");

    private SessionFactory sessionFactory;

    @BeforeEach
    void init() {
        Configuration configuration = new Configuration();
        configuration.setProperty(Settings.HBM2DDL_AUTO, Action.ACTION_CREATE);
        configuration.setProperty(Settings.JAKARTA_JDBC_URL, mysql.getJdbcUrl());
        configuration.setProperty(Settings.JAKARTA_JDBC_USER, mysql.getUsername());
        configuration.setProperty(Settings.JAKARTA_JDBC_PASSWORD, mysql.getPassword());
        configuration.addAnnotatedClasses(JsonObjectEntity.class, JsonObjectListEntity.class);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        sessionFactory = configuration.buildSessionFactory(registry);
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
        try (Session session = sessionFactory.createEntityManager()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        }

        try (Session Session = sessionFactory.createEntityManager()) {
            JsonObjectEntity fetched = Session.find(JsonObjectEntity.class, entity.id);
            assertEquals(object, fetched.jsonObject);
        }
    }

    @Test
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
        try (Session session = sessionFactory.createEntityManager()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        }

        try (Session session = sessionFactory.createEntityManager()) {
            JsonObjectListEntity fetched = session.find(JsonObjectListEntity.class, entity.id);
            assertIterableEquals(objects, fetched.jsonList);
        }
    }
}
