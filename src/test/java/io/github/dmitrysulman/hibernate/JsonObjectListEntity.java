package io.github.dmitrysulman.hibernate;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
public class JsonObjectListEntity {
    public JsonObjectListEntity() {
    }

    public JsonObjectListEntity(Long id, List<PlainObject> jsonList) {
        this.id = id;
        this.jsonList = jsonList;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JdbcTypeCode(SqlTypes.JSON) // Fails with io.vertx.core.json.DecodeException: Failed to decode
//    @JdbcTypeCode(SqlTypes.JSON_ARRAY) // Fails with java.lang.NullPointerException: Cannot invoke "org.hibernate.metamodel.mapping.MappingType.getClass()" because "mappedType" is null
    @Column(columnDefinition = "json")
    public List<PlainObject> jsonList;
}
