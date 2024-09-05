package org.example.mtgtests.resources;
import org.example.mtgtests.catalog.models.Card;
import org.example.mtgtests.client.models.RawCard;
import org.example.mtgtests.catalog.CardCatalog;
import org.example.mtgtests.configuration.MTGApiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@Disabled(value =
    "Only run this to generate data for tests. Exercise extreme caution when replacing test data, as it may affect tests written around a previous data set."
)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MTGApiConfig.class)
final class TestResourceGenerator {

  private final CardCatalog cardCatalog;
  private final JacksonTester<List<RawCard>> cardSerializer;

  @Autowired
  TestResourceGenerator(CardCatalog cardCatalog, ObjectMapper objectMapper) {
    this.cardCatalog = cardCatalog;
    cardSerializer = new JacksonTester<>(getClass(), ResolvableType.forClass(Card.class), objectMapper);
  }

  @Test
  void saveOneThousandCardsAsJsonResource() {
  }
}
