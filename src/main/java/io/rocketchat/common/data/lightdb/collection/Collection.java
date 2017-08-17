package io.rocketchat.common.data.lightdb.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 11/8/17.
 */
public class Collection<T, K> {

    ConcurrentHashMap<T, K> documents;

    public Collection() {
        documents = new ConcurrentHashMap<>();
    }

    public void add(T key, K value) {
        documents.put(key, value);
    }

    public K get(T key) {
        return documents.get(key);
    }

    public void update(T key, K value) {
        documents.replace(key, value);
    }

    public K remove(T key) {
        return documents.remove(key);
    }


    public List<K> getData() {
        ArrayList<K> list = new ArrayList();
        Set<Map.Entry<T, K>> set = documents.entrySet();
        for (Map.Entry entry : set) {
            list.add((K) entry.getValue());
        }
        return list;
    }

    public void removeAll() {
        documents.clear();
    }

}
