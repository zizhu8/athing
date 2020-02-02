package com.github.ompc.athing.thing.impl.aliyun.util;

import java.util.HashMap;

/**
 * Map对象
 */
public class MapObject extends HashMap<String, Object> {

    private final MapObject parent;

    public MapObject() {
        this(null);
    }

    private MapObject(MapObject parent) {
        this.parent = parent;
    }

    public MapObject putProperty(String name, Object value) {
        put(name, value);
        return this;
    }

    public MapObject enterProperty(String name) {
        final MapObject mapObject = new MapObject(this);
        put(name, mapObject);
        return mapObject;
    }

    public MapObject exitProperty() {
        if (null == parent) {
            throw new IllegalStateException("root");
        }
        return parent;
    }

}
