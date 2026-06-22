package study.ywork.cook.string;

import java.util.Objects;

/**
 * 键值对的工具类
 *
 * @param <K> 键
 * @param <V> 值
 */
public final class Pair<K, V> {
    final K key;
    final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Pair)) {
            return false;
        }

        Pair<?, ?> obj = (Pair<?, ?>) o;
        return Objects.equals(key, obj.key) && Objects.equals(value, obj.value);
    }
}
