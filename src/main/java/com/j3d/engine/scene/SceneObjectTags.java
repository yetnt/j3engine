package com.j3d.engine.scene;

import com.yetnt.utils.builders.SimpleJSON;
import com.yetnt.utils.builders.ToJSON;
import com.yetnt.utils.collection.HashMultiMap;
import com.yetnt.utils.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class SceneObjectTags extends HashMap<Pair<String, Class<?>>, Object> implements ToJSON<SimpleJSON, Exception> {
    @Nullable
    public <T> T get(String key, @NotNull Class<?> tagProvider, @NotNull Class<T> ofClass) {
        Object t = get(new Pair<>(key, tagProvider));
        if (ofClass.isAssignableFrom(t.getClass())) return ofClass.cast(t);
        return null;
    }

    public void put(String key, @NotNull Class<?> tagProvider, @NotNull Object value) {
        put(new Pair<>(key, tagProvider), value);
    }

    public boolean remove(String key, @NotNull Class<?> tagProvider, @NotNull Object value) {
        return remove(new Pair<>(key, tagProvider), value);
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    @Override
    public SimpleJSON toJson() {
        return new SimpleJSON(
                this
                        .entrySet()
                        .stream()
                        .map(p ->
                            new SimpleEntry<>(p.getKey().getFirst()+"-"+p.getKey().getSecond().getName(), p.getValue())
                        )
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue))
                );
    }

    @Override
    public String toString() {
        return "SOTags-" + toJson().toString();
    }

    public Map<String, Object> getTagsProvidedByClass(@NotNull Class<?> tagProvider) {
        return this
                .entrySet()
                .stream()
                .filter(p -> p.getKey().getSecond().equals(tagProvider))
                .map(p ->
                        new SimpleEntry<>(p.getKey().getFirst(), p.getValue())
                )
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public Map<Class<?>, Object> getTagsOfKey(String key) {
        return this
                .entrySet()
                .stream()
                .filter(p -> p.getKey().getFirst().equals(key))
                .map(p ->
                        new SimpleEntry<>(p.getKey().getSecond(), p.getValue())
                )
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    @NotNull
    public HashMultiMap<String, Object> toKeyMultiMap() {
        HashMultiMap<String, Object> map = new HashMultiMap<>();
        this.forEach((p, o) -> map.putValue(p.getFirst(), o));
        return map;
    }

    @NotNull
    public HashMultiMap<Class<?>, Object> toProviderMultiMap() {
        HashMultiMap<Class<?>, Object> map = new HashMultiMap<>();
        this.forEach((p, o) -> map.putValue(p.getSecond(), o));
        return map;
    }
}
