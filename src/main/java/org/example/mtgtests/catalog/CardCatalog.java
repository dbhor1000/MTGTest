package org.example.mtgtests.catalog;
import org.example.mtgtests.catalog.models.Card;
import reactor.core.publisher.Flux;

public interface CardCatalog {

    Flux<Card> getAllCards();
    Flux<Card> matchCards(CardCriteria criteria);
}
