package org.example.mtgtests.catalog.mtg;
import static reactor.core.publisher.Flux.concat;
import static reactor.core.publisher.Flux.empty;
import static reactor.core.publisher.Flux.just;
import static reactor.core.publisher.Flux.range;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.example.mtgtests.catalog.CardCatalog;
import org.example.mtgtests.catalog.CardCriteria;
import org.example.mtgtests.catalog.mappers.RawCardToCardMapper;
import org.example.mtgtests.catalog.models.Card;
import org.example.mtgtests.client.CardsClient;
import org.example.mtgtests.client.models.Page;
import org.example.mtgtests.client.models.RawCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public final class MTGCardCatalog implements CardCatalog {

    private final CardsClient client;
    private final Converter<RawCard, Card> mapper;

   @Autowired
    public MTGCardCatalog(CardsClient client, RawCardToCardMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public Flux<Card> getAllCards() {
        return client.getPage(1)
                .flatMapMany(page -> {

                    final var remainingPages = page.nextPageNumber().map(nextPageNumber -> {
                        final var lastPageNumber = page.lastPageNumber();

                        if (nextPageNumber.equals(lastPageNumber)) {
                            return client.getPage(lastPageNumber).flux();
                        }

                        return concat(range(nextPageNumber, lastPageNumber).map(client::getPage));
                    }).orElse(empty());

                    return Flux.merge(just(page), remainingPages)
                            .map(Page::cards)
                            .flatMap(Flux::fromIterable)
                            .map(mapper::convert);
                });
    }

    @Override
    public Flux<Card> matchCards(CardCriteria criteria) {
        final var matcher = new CardMatcher(criteria);
        return getAllCards().filter(matcher::test);
    }

    private static final class CardMatcher implements Predicate<Card> {

        private final CardCriteria criteria;

        private CardMatcher(CardCriteria criteria) {
            this.criteria = criteria;
        }

        @Override
        public boolean test(Card card) {
            final var conditions = Stream.of(
                    checkCondition(criteria::nameContains, (name) -> card.name().contains(name)),
                    checkCondition(criteria::colorIdentity, (color) -> card.colorIdentity().containsAll(color))
            );

            return criteria.exclusiveMatch() ? conditions.allMatch(p -> p.test(card)) : conditions.anyMatch(p -> p.test(card));
        }

        private static <T> Predicate<Card> checkCondition(Supplier<Optional<T>> criteria, Predicate<T> condition) {
            return (c) -> criteria.get().map(condition::test).orElse(true);
        }
    }
}
