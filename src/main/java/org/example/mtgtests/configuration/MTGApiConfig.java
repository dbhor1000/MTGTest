package org.example.mtgtests.configuration;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import org.example.mtgtests.catalog.models.Card;
import org.example.mtgtests.client.CardsClient;
import org.example.mtgtests.client.models.RawCard;
import org.example.mtgtests.client.mtg.MTGCardsClient;
import org.example.mtgtests.client.utils.BodyParser;
import org.example.mtgtests.catalog.CardCatalog;
import org.example.mtgtests.catalog.mappers.RawCardToCardMapper;
import org.example.mtgtests.catalog.mtg.MTGCardCatalog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableCaching
@RestController
public class MTGApiConfig {

  @Bean
  public WebClient webClient() {
    return WebClient.builder().build();
  }

  @Bean
  @Scope("prototype")
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .findAndRegisterModules();
  }

  @Bean
  public BodyParser jsonDeserializer(ObjectMapper objectMapper) {
    return new BodyParser(objectMapper);
  }

  @Bean(name = "cardPagesCacheManager")
  public CacheManager cardPagesCacheManager() {
    return new ConcurrentMapCacheManager("cardPages");
  }

  @Bean
  public Converter<RawCard, Card> rawCardToCardMapper() {
    return new RawCardToCardMapper();
  }

  @Bean
  public CardsClient cardsClient(@Value("${api.mtgio.cards.uri}") String baseUri, BodyParser bodyParser, WebClient webClient) {
    return new MTGCardsClient(baseUri, webClient, bodyParser);
  }

  @Bean
  public CardCatalog cardCatalog(CardsClient cardsClient, Converter<RawCard, Card> mapper) {
    return new MTGCardCatalog(cardsClient, (RawCardToCardMapper) mapper);
  }
}
