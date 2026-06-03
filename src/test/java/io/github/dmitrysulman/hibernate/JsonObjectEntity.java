package io.github.dmitrysulman.hibernate;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
public class JsonObjectEntity {
    public JsonObjectEntity() {
    }

    public JsonObjectEntity(Long id, PlainObject jsonObject) {
        this.id = id;
        this.jsonObject = jsonObject;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    public PlainObject jsonObject;
}
