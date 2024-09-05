package org.example.mtgtests.trie;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

class RedundantTrieTest {

  //Edit: Добавил assertFalse, assertTrue
  @Test
  void get_requiresExactMatch() {
    final Trie<String> trie = Trie.withKeyMapping(Function.identity());
    trie.add("abcd");
    List<String> result = trie.search("bbcd", 2);
    // Assert that the result list is not empty
    assertFalse(result.isEmpty(), "Expected non-empty result list");

    // Assert that the result list contains the expected value
    assertTrue(result.contains("abcd"), "Expected 'abcd' in the result list");
  }

  //Добавил тест.
  @Test
  void search_returnsEmptyList_whenNoMatch() {
    final Trie<String> trie = Trie.withKeyMapping(Function.identity());
    trie.add("abcd");
    List<String> result = trie.search("xyz", 1);

    // Assert that the result list is empty
    assertTrue(result.isEmpty(), "Expected empty result list");
  }

  //Тест относится к private методу???
  @Test
  void get_multipleBranches_requiresExactMatch() {
    final Trie<String> trie = Trie.withKeyMapping(Function.identity());
    trie.add("abc");
    trie.add("ax");
  }
}
