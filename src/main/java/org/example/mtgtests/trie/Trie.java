package org.example.mtgtests.trie;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public interface Trie<V> {

  void add(V value);

  List<V> search(String key, int maximumDistance);

  static Trie<String> create(String... values) {
    return create(List.of(values));
  }

  static Trie<String> create(Iterable<String> values) {
    return withKeyMapping(Function.identity(), values);
  }

  static <V> Trie<V> withKeyMapping(Function<V, String> keyMapping, V... values) {
    return withKeyMapping(keyMapping, List.of(values));
  }

  static <V> Trie<V> withKeyMapping(Function<V, String> keyMapping, Iterable<V> values) {
    final var trie = new RedundantTrie<>(keyMapping, (key) -> Arrays.spliterator(key.split("")));
    values.forEach(trie::add);
    return trie;
  }
}
