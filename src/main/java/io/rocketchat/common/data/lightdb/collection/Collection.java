package io.rocketchat.common.data.lightdb.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by sachin on 11/8/17.
 */
public class Collection<T, K> {

    ConcurrentHashMap<T, K> documents;

    //List of observers
    ConcurrentHashMap<T, ConcurrentLinkedQueue<Observer<K>>> observers;

    public Collection() {
        documents = new ConcurrentHashMap<>();
        observers = new ConcurrentHashMap<>();
    }

    public void add(T key, K value) {
        documents.put(key, value);
        publish(Type.ADDED, key, value);
    }

    public K get(T key) {
        return documents.get(key);
    }

    public void update(T key, K value) {
        documents.replace(key, value);
    }

    public void updated(T key, K newValue) {
        publish(Type.CHANGED, key, newValue);
    }

    public K remove(T key) {
        K value = documents.remove(key);
        publish(Type.REMOVED, key, value);
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
        if (observers.containsKey(key)) {
            ConcurrentLinkedQueue<Observer<K>> queue = observers.get(key);
            if (!queue.contains(o)) {
                queue.add(o);
            }
        } else {
            ConcurrentLinkedQueue<Observer<K>> queue = new ConcurrentLinkedQueue<>();
            queue.add(o);
            observers.put(key, queue);
        }
    }

    public void unRegister(T key, Observer<K> o) {
        if (observers.contains(key)) {
            ConcurrentLinkedQueue<Observer<K>> queue = observers.get(key);
            queue.remove(o);
        }
    }

    public void unRegister(T key) {
        observers.remove(key);
    }

    private void publish(Type type, T key, K document) {
        if (observers.contains(key)) {
            ConcurrentLinkedQueue<Observer<K>> queue = observers.get(key);
            for (Observer<K> observer : queue) {
                observer.onUpdate(type, document);
            }
        }
    }

    public void unRegisterAll() {
        observers.clear();
    }

    public interface Observer<K> {
        void onUpdate(Type type, K document);
    }
}
