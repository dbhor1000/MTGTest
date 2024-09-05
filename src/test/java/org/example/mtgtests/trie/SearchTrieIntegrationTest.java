package org.example.mtgtests.trie;

import org.example.mtgtests.catalog.CardCatalog;
import org.example.mtgtests.catalog.models.Card;
import org.example.mtgtests.configuration.MTGApiConfig;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MTGApiConfig.class)
final class SearchTrieIntegrationTest {

  private final CardCatalog cardCatalog;

  @Autowired
  SearchTrieIntegrationTest(CardCatalog cardCatalog) {
    this.cardCatalog = cardCatalog;
  }

  @Test
  void buildAndSearch() {
    final Trie<Card> trie = Trie.withKeyMapping(Card::name);
    cardCatalog.getAllCards().take(400)
        .doOnNext(System.out::println)
        .doOnNext(trie::add).blockLast();
  }

  @Test
  void getMeNames() {
    System.out.println(
        cardCatalog.getAllCards().take(400).map(Card::name).collect(Collectors.toList()).block().stream().collect(Collectors.joining("\\\",\\\""))
    );
  }
}
