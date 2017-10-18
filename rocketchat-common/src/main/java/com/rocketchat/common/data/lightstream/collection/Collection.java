package com.rocketchat.common.data.lightstream.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 11/8/17.
 */
public class Collection<T, K> extends Observable {

    ConcurrentHashMap<T, K> documents;

    //List of observers
    ConcurrentHashMap<T, Observer<K>> observers;

    public Collection() {
        documents = new ConcurrentHashMap<>();
        observers = new ConcurrentHashMap<>();
    }

    public void add(T key, K value) {
        documents.put(key, value);
        publish(Type.ADDED, key, value);
        setChanged();
        notifyObservers(value);
    }

    public K get(T key) {
        return documents.get(key);
    }

    public void update(T key, K newValue) {
        documents.put(key, newValue);
        publish(Type.CHANGED, key, newValue);
        setChanged();
        notifyObservers(newValue);
    }

    public K remove(T key) {
        K value = documents.remove(key);
        publish(Type.REMOVED, key, value);
        setChanged();
        notifyObservers();
        return value;
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

    public enum Type {
        ADDED,
        CHANGED,
        REMOVED
    }

    public void register(T key, Observer<K> o) {
        observers.put(key, o);
    }


    public void unregister(T key) {
        observers.remove(key);
    }

    private void publish(Type type, T key, K document) {
        if (observers.containsKey(key)) {
            observers.get(key).onUpdate(type, document);
        }
    }

    public int size() {
        return documents.size();
    }

    public void unregisterAll() {
        observers.clear();
    }

    public interface Observer<K> {
        void onUpdate(Type type, K document);
    }
}
