package org.example.mtgtests.resources;
import org.example.mtgtests.catalog.models.Card;
import org.example.mtgtests.client.models.ImmutableRawCard;
import org.example.mtgtests.client.models.RawCard;
import org.example.mtgtests.catalog.mappers.RawCardToCardMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.ResolvableType;


public final class TestResources {

  static final String ONE_THOUSAND_CARDS_RESOURCE_PATH = "one-thousand-cards.json";
  private static final JacksonTester<List<RawCard>> CARD_JSON = new JacksonTester<>(
      TestResources.class, ResolvableType.forClassWithGenerics(List.class, RawCard.class), new ObjectMapper()
  );

  public static List<Card> oneThousandCards() {
    return oneThousandRawCards().stream().map(MAPPER::convert).collect(Collectors.toList());
  }

  public static List<RawCard> oneThousandRawCards() {
    return loadRawCardsFromJsonResource(ONE_THOUSAND_CARDS_RESOURCE_PATH);
  }


  private static List<RawCard> loadRawCardsFromJsonResource(final String resourceName) {
    try {
      return CARD_JSON.parse(Files.readString(Paths.get(ClassLoader.getSystemResource(resourceName).toURI()))).getObject();
    } catch (Throwable thrown) {
      throw new TestResourceConfigurationException(resourceName, thrown);
    }
  }

  public static RawCard RAW_FLAMEWAVE_INVOKER =
      ImmutableRawCard.builder()
          .name("Flamewave Invoker")
          .rarity("Uncommon")
          .cmc(3.0)
          .text("{7}{R}: Flamewave Invoker deals 5 damage to target player or planeswalker.")
          .manaCost("{2}{R}")
          .colorIdentity(ImmutableSet.of("R"))
          .colors(ImmutableSet.of("Red"))
          .types(ImmutableSet.of("Creature"))
          .build();

  public static RawCard RAW_CANOPY_SPIDER =
      ImmutableRawCard.builder()
          .name("Canopy Spider")
          .rarity("Common")
          .cmc(2.0)
          .text("Reach (This creature can block creatures with flying.)")
          .manaCost("{1}{G}")
          .colorIdentity(ImmutableSet.of("G"))
          .colors(ImmutableSet.of("Green"))
          .types(ImmutableSet.of("Creature"))
          .build();

  public static RawCardToCardMapper MAPPER = new RawCardToCardMapper();

  public static Card MAPPED_FLAMEWAVE_INVOKER = MAPPER.convert(RAW_FLAMEWAVE_INVOKER);
  public static Card MAPPED_CANOPY_SPIDER = MAPPER.convert(RAW_CANOPY_SPIDER);
}
