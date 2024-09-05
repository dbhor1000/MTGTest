package org.example.mtgtests.service.trie;

import static java.util.stream.Collectors.toList;

import org.example.mtgtests.catalog.CardCatalog;
import org.example.mtgtests.catalog.models.Card;
import org.example.mtgtests.service.suggest.CardSuggester;

import java.util.*;
import javax.annotation.PostConstruct;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class TrieCardSuggester implements CardSuggester {

  private final Trie<String, Card> trie;

  public TrieCardSuggester() {
    trie = new PatriciaTrie<>();
  }

  @PostConstruct
  public void populateTrie(@Autowired CardCatalog cardCatalog) {
    final var allCards = cardCatalog.getAllCards();
    allCards.subscribe((card) -> trie.put(card.name(), card));
    allCards.blockLast();
  }

  //@Override
  //public Collection<Card> suggestCardsByName(String name) {
  //  final var matches = new ArrayList<>(trie.prefixMap(name).values());
  //
  //  final var exactMatchCount = matches.size();
  //  if (exactMatchCount >= 10) {
  //    return matches.stream().limit(10).collect(toList());
  //  }
  //
  //  matches.addAll(trie.headMap(name).values().stream().limit(10 - exactMatchCount).collect(toList()));
  //  final var fuzzyMatchCount = matches.size();
  //  if (fuzzyMatchCount == 10) {
  //    return matches;
  //  }
  //
  //  matches.addAll(trie.tailMap(name).values().stream().limit(10 - fuzzyMatchCount).collect(toList()));
  //
  //  return matches;
  //}

  @Override
  public Collection<Card> suggestCardsByName(String name) {
    // Get exact matches
    List<Card> exactMatches = new ArrayList<>(trie.prefixMap(name).values());

    // If we have 10 or more exact matches, return the first 10
    if (exactMatches.size() >= 10) {
      return exactMatches.stream().limit(10).collect(toList());
    }

    // Get fuzzy matches from headMap and tailMap
    List<Card> fuzzyMatches = new ArrayList<>();
    fuzzyMatches.addAll(trie.headMap(name).values().stream()
            .filter(card -> card.name().toLowerCase().contains(name.toLowerCase()))
            .collect(toList()));
    fuzzyMatches.addAll(trie.tailMap(name).values().stream()
            .filter(card -> card.name().toLowerCase().contains(name.toLowerCase()))
            .collect(toList()));

    // Remove duplicates by converting to a set and back to a list
    Set<Card> uniqueMatches = new LinkedHashSet<>(exactMatches);
    uniqueMatches.addAll(fuzzyMatches);

    // Limit the total number of matches to 10
    List<Card> limitedMatches = uniqueMatches.stream().limit(10).collect(toList());

    // Logging to verify the contents of the suggestions
    System.out.println("Suggestions for '" + name + "': " + limitedMatches);

    return limitedMatches;
  }

  public Trie<String, Card> getTrie() {
    return trie;
  }
}
