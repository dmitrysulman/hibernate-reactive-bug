package io.github.dmitrysulman.hibernate;

import java.util.List;
import java.util.Map;

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
}
