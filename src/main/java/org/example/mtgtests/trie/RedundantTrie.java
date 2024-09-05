package org.example.mtgtests.trie;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.StreamSupport.stream;

import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of {@linkplain Trie} that doesn't perform any memory optimization, but otherwise supports all configurable functionality of a Trie capable of
 * performing fuzzy prefix/key searches.
 */
final class RedundantTrie<V> implements Trie<V> {

  private final Function<V, String> keyMapping;
  private final Function<String, Spliterator<String>> keyPartitioner;
  private final Map<String, Node<String, V>> roots = new HashMap<>();

  RedundantTrie(Function<V, String> keyMapping, Function<String, Spliterator<String>> keyPartitioner) {
    this.keyPartitioner = keyPartitioner;
    this.keyMapping = keyMapping;
  }

  @Override
  public void add(V value) {
    final var keyParts = tokenizeStringeyForValue(value);
    if (keyParts.isEmpty()) {
      return;
    }

    var currentTreeLevel = roots;
    var depth = 0;
    while (true) {
      final var keyPart = keyParts.get(depth);

      final Node<String, V> currentNode = currentTreeLevel.compute(keyPart, (k, v) -> {
        if (v == null) {
          return new Node<>();
        }
        return v;
      });

      if (++depth == keyParts.size()) {
        currentNode.values.add(value);
        break;
      }
      currentTreeLevel = currentNode.children;
    }
  }

  public List<V> search(String key, int maximumDistance) {
    final var inputStringeys = tokenizeStringey(key);

    if (inputStringeys.isEmpty()) {
      return List.of(); // or throw an error!
    }

    final var valuesByDistance = new HashMap<Integer, Set<V>>();

    searchBranch(inputStringeys, 0, maximumDistance, roots, valuesByDistance);

    return valuesByDistance.values().stream().flatMap(Set::stream).collect(toList());
  }

  private List<String> tokenizeStringeyForValue(V value) {
    return tokenizeStringey(keyMapping.apply(value));
  }

  private List<String> tokenizeStringey(String key) {
    return stream(keyPartitioner.apply(key), false).collect(toUnmodifiableList());
  }

  private static <String, V> void searchBranch(
      List<String> inputs,
      int currentDistance, int maximumDistance,
      Map<String, Node<String, V>> nodes, Map<Integer, Set<V>> matchingValuesByDistance
  ) {
    final var currentInput = inputs.get(0);
    final var remainingInputs = inputs.subList(1, inputs.size());

    nodes.forEach((nodePrefix, node) -> {
      final var newDistance = currentInput.equals(nodePrefix) ? currentDistance : currentDistance + 1;

      if (newDistance > maximumDistance) {
        return;
      }

      if (!node.values.isEmpty()) {
        matchingValuesByDistance.compute(newDistance, unionWith(node.values));
      }

      if (!remainingInputs.isEmpty()) {
        searchBranch(remainingInputs, newDistance, maximumDistance, node.children, matchingValuesByDistance);
      } else {
        matchingValuesByDistance.compute(newDistance, unionWith(allValuesForCurrentPrefix(node)));
      }
    });
  }

  private static <V> BiFunction<Integer, Set<V>, Set<V>> unionWith(Set<V> newValues) {
    return (distance, values) -> values == null ? newValues : Sets.union(newValues, values);
  }

  private static <V> Set<V> allValuesForCurrentPrefix(Node<?, V> node) {
    if (node.children.isEmpty()) {
      return node.values;
    }
    return Sets.union(
        node.values, node.children.values().stream()
            .map(RedundantTrie::allValuesForCurrentPrefix)
            .flatMap(Set::stream)
            .collect(Collectors.toUnmodifiableSet())
    );
  }

  private static final class Node<String, V> {
    private final Set<V> values = new HashSet<>();
    private final Map<String, Node<String, V>> children = new HashMap<>();
  }
}
