package io.rocketchat.common.data.collection;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 11/8/17.
 */
public class Collection <T, K>{

    ConcurrentHashMap <T , K> collections;

    public Collection (){
        collections=new ConcurrentHashMap<>();
    }

    public void add(T key, K value) {
        collections.put(key, value);
    }

    public K get (T key){
        return collections.get(key);
    }

    public void update(T key, K value) {
        collections.replace(key, value);
    }

    public K remove (T key) {
        return collections.remove(key);
    }


    public ArrayList <K> getData (){
        ArrayList <K> list= new ArrayList();
        Set<Map.Entry <T, K>> set= collections.entrySet();
        for (Map.Entry entry : set){
            list.add((K) entry.getValue());
        }
        return list;
    }

    public void removeAll(){
        collections.clear();
    }

}
