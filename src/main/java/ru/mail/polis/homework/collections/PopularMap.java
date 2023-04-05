package ru.mail.polis.homework.collections;


import java.util.*;


/**
 * Написать структуру данных, реализующую интерфейс мапы + набор дополнительных методов.
 * 4 дополнительных метода должны возвращать самый популярный ключ и его популярность. (аналогично для самого популярного значения)
 * Популярность - это количество раз, который этот ключ/значение учавствовал/ло в других методах мапы, такие как
 * containsKey, get, put, remove (в качестве параметра и возвращаемого значения).
 * Считаем, что null я вам не передаю ни в качестве ключа, ни в качестве значения
 * <p>
 * Так же надо сделать итератор (подробности ниже).
 * <p>
 * Важный момент, вам не надо реализовывать мапу, вы должны использовать композицию.
 * Вы можете использовать любые коллекции, которые есть в java.
 * <p>
 * Помните, что по мапе тоже можно итерироваться
 * <p>
 * for (Map.Entry<K, V> entry : map.entrySet()) {
 * entry.getKey();
 * entry.getValue();
 * }
 * <p>
 * Всего 10 тугриков (3 тугрика за общие методы, 2 тугрика за итератор, 5 тугриков за логику популярности)
 *
 * @param <K> - тип ключа
 * @param <V> - тип значения
 */
public class PopularMap<K, V> implements Map<K, V> {

    private final Map<K, V> map;
    private final Map<K, Integer> keyWithPopularity;
    private final Map<V, Integer> valueWithPopularity;

    public PopularMap() {
        this.map = new HashMap<>();
        this.keyWithPopularity = new HashMap<K, Integer>();
        this.valueWithPopularity = new HashMap<V, Integer>();
    }

    public PopularMap(Map<K, V> map) {
        this.map = map;
        keyWithPopularity = new HashMap<>();
        valueWithPopularity = new HashMap<>();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        putPopularity((K) key, keyWithPopularity);
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        putPopularity((V) value, valueWithPopularity);
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        putPopularity((K) key, keyWithPopularity);
        V value = map.get(key);
        putPopularity((V) value, valueWithPopularity);
        return value;
    }

    @Override
    public V put(K key, V value) {
        V putVal = map.put(key, value);
        putPopularity((V) value, valueWithPopularity);
        putPopularity((V) putVal, valueWithPopularity);
        putPopularity((K) key, keyWithPopularity);
        return value;
    }

    @Override
    public V remove(Object key) {
        putPopularity((K) key, keyWithPopularity);
        putPopularity((V) map.get(key), valueWithPopularity);
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        //throw new UnsupportedOperationException("putAll");
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    /**
     * Возвращает самый популярный, на данный момент, ключ
     */
    public K getPopularKey() {
        return getMostPopular(keyWithPopularity).orElse(null);
    }


    /**
     * Возвращает количество использование ключа
     */
    public int getKeyPopularity(K key) {
        return keyWithPopularity.getOrDefault(key, 0);
    }

    /**
     * Возвращает самое популярное, на данный момент, значение. Надо учесть что значени может быть более одного
     */
    public V getPopularValue() {
        return getMostPopular(valueWithPopularity).orElse(null);
    }

    /**
     * Возвращает количество использований значений в методах: containsValue, get, put (учитывается 2 раза, если
     * старое значение и новое - одно и тоже), remove (считаем по старому значению).
     */
    public int getValuePopularity(V value) {
        return valueWithPopularity.getOrDefault(value, 0);
    }

    /**
     * Вернуть итератор, который итерируется по значениям (от самых НЕ популярных, к самым популярным)
     * 2 тугрика
     */
    public Iterator<V> popularIterator() {
        return valueWithPopularity
                .entrySet()
                .stream()
                .sorted(Entry.comparingByValue())
                .map(Entry::getKey)
                .iterator();
    }

    private <T> void putPopularity(T key, Map<T, Integer> mapWithPopularity) {
        if (key == null) {
            return;
        }
        if (mapWithPopularity.containsKey(key)) {
            mapWithPopularity.put(key, mapWithPopularity.get(key) + 1);
        } else {
            mapWithPopularity.put(key, 1);
        }
    }

    private <K> Optional<K> getMostPopular(Map<K, Integer> map) {
        if (map.isEmpty()) {
            return Optional.empty();
        }
        Map.Entry<K, Integer> maxEntry = null;
        for (Map.Entry<K, Integer> entry : map.entrySet()) {
            int currentPopularity = entry.getValue();
            if (maxEntry == null || currentPopularity > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        return Optional.of(maxEntry.getKey());
    }
}
