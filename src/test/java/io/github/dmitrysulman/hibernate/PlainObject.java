package io.github.dmitrysulman.hibernate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlainObject {
    public String stringProperty;
    public Long longProperty;
    public List<String> listProperty;
    public Map<String, Object> mapProperty;

    public PlainObject() {
    }

    public PlainObject(String stringProperty, Long longProperty, List<String> listProperty, Map<String, Object> mapProperty) {
        this.stringProperty = stringProperty;
        this.longProperty = longProperty;
        this.listProperty = listProperty;
        this.mapProperty = mapProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlainObject that)) return false;
        return Objects.equals(stringProperty, that.stringProperty) && Objects.equals(longProperty, that.longProperty) && Objects.equals(listProperty, that.listProperty) && Objects.equals(mapProperty, that.mapProperty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringProperty, longProperty, listProperty, mapProperty);
    }
}
