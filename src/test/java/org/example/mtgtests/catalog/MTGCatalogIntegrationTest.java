package org.example.mtgtests.catalog;

import static java.lang.String.format;
import static java.lang.System.out;

import org.example.mtgtests.catalog.ImmutableCardCriteria;
import org.example.mtgtests.catalog.models.Color;
import org.example.mtgtests.configuration.MTGApiConfig;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MTGApiConfig.class)
class MTGCatalogIntegrationTest {

  private final CardCatalog cardCatalog;
  private final int n;

  @Autowired
  MTGCatalogIntegrationTest(CardCatalog cardCatalog, @Value("${test.integration.take-n}") int n) {
    this.cardCatalog = cardCatalog;
    this.n = n;
  }

  @Test
  void printCards() {
    cardCatalog.getAllCards().take(n).doOnEach(out::println).blockLast();
  }

  @Test
  void countCards() {
    out.println(
        format("Counted %d Magic: The Gathering cards (taking n: %d)", cardCatalog.getAllCards().take(n).count().block(), n)
    );
  }

  @Test
  void printCardsWithGreenColorIdentity() {
    cardCatalog.matchCards(
        ImmutableCardCriteria.builder()
            .colorIdentity(Set.of(Color.GREEN))
            .build()
    ).take(n / 4).doOnEach(out::println).blockLast();
  }

  @Test
  void printUniqueCard() {
    cardCatalog.matchCards(
        ImmutableCardCriteria.builder()
            .nameContains("Nekrataal")
            .build()).take(1).doOnEach(System.out::println).blockLast();
  }

  @Test
  void printCardsWithTheInName() {
    cardCatalog.matchCards(
        ImmutableCardCriteria.builder()
            .nameContains("the")
            .build()).take(n / 4).doOnEach(System.out::println).blockLast();
  }
}
