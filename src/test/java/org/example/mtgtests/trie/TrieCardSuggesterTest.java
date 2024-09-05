package org.example.mtgtests.trie;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.example.mtgtests.catalog.CardCatalog;
import org.example.mtgtests.catalog.models.Card;
import org.example.mtgtests.catalog.models.Color;
import org.example.mtgtests.catalog.models.ImmutableCard;
import org.example.mtgtests.configuration.MTGApiConfig;
import org.example.mtgtests.service.trie.TrieCardSuggester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;

import java.util.*;

//Как показали тесты, метод public Collection<Card> suggestCardsByName(String name) классы TrieCardSuggester в package org.example.mtgtests.service.trie
//нуждался в изменениях, так как выявлял fuzzyMatches некорректно. Старый код под комментариями в том же классе, тесты вызывают доработанный метод.

@SpringBootTest(classes = MTGApiConfig.class)
@ExtendWith(SpringExtension.class)
class TrieCardSuggesterTest {

    @Mock
    private CardCatalog cardCatalog;

    @InjectMocks
    private TrieCardSuggester trieCardSuggester;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testPopulateTrie() {
        // Mock data
        Card card1 = ImmutableCard.builder().name("Card1").colorIdentity(Set.of(Color.RED)).convertedManaCost(1).build();
        Card card2 = ImmutableCard.builder().name("Card2").colorIdentity(Set.of(Color.BLUE)).convertedManaCost(2).build();
        when(cardCatalog.getAllCards()).thenReturn(Flux.just(card1, card2));

        // Test
        trieCardSuggester.populateTrie(cardCatalog);

        // Assertions
        assertTrue(trieCardSuggester.getTrie().containsKey("Card1"));
        assertTrue(trieCardSuggester.getTrie().containsKey("Card2"));
        assertEquals(card1, trieCardSuggester.getTrie().get("Card1"));
        assertEquals(card2, trieCardSuggester.getTrie().get("Card2"));
    }

    @Test
    void testSuggestCardsByName_exactMatch() {
        // Mock data
        Card card1 = ImmutableCard.builder().name("Card1").colorIdentity(Set.of(Color.RED)).convertedManaCost(1).build();
        Card card2 = ImmutableCard.builder().name("Card2").colorIdentity(Set.of(Color.BLUE)).convertedManaCost(2).build();

        when(cardCatalog.getAllCards()).thenReturn(Flux.just(card1, card2));
        trieCardSuggester.populateTrie(cardCatalog);

        // Test
        //Метод public Collection<Card> suggestCardsByName(String name) в package org.example.mtgtests.service.trie требует refactoring?
        Collection<Card> suggestions = trieCardSuggester.suggestCardsByName("Card");
        System.out.println(suggestions);

        // Assertions
        assertEquals(2, suggestions.size());
        assertTrue(suggestions.contains(card1));
        assertTrue(suggestions.contains(card2));
    }

    @Test
    void testSuggestCardsByName_fuzzyMatch() {
        // Mock data
        Card card1 = ImmutableCard.builder().name("Card1").colorIdentity(Set.of(Color.RED)).convertedManaCost(1).build();
        Card card2 = ImmutableCard.builder().name("Card2").colorIdentity(Set.of(Color.BLUE)).convertedManaCost(2).build();
        Card card3 = ImmutableCard.builder().name("AnotherCard").colorIdentity(Set.of(Color.GREEN)).convertedManaCost(3).build();
        when(cardCatalog.getAllCards()).thenReturn(Flux.just(card1, card2, card3));
        trieCardSuggester.populateTrie(cardCatalog);

        // Test
        //Метод public Collection<Card> suggestCardsByName(String name) в package org.example.mtgtests.service.trie требует refactoring?
        Collection<Card> suggestions = trieCardSuggester.suggestCardsByName("Card");

        // Assertions
        assertEquals(3, suggestions.size());
        assertTrue(suggestions.contains(card1));
        assertTrue(suggestions.contains(card2));
        assertTrue(suggestions.contains(card3));
    }

    @Test
    void testSuggestCardsByName_max10Suggestions() {
        // Mock data
        List<Card> cards = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            cards.add(ImmutableCard.builder().name("Card" + i).colorIdentity(Set.of(Color.RED)).convertedManaCost(i).build());
        }
        when(cardCatalog.getAllCards()).thenReturn(Flux.fromIterable(cards));
        trieCardSuggester.populateTrie(cardCatalog);

        // Test
        Collection<Card> suggestions = trieCardSuggester.suggestCardsByName("Card");

        // Assertions
        assertEquals(10, suggestions.size());
    }

    @Test
    void testSuggestCardsByName_noMatch() {
        // Mock data
        Card card1 = ImmutableCard.builder().name("Card1").colorIdentity(Set.of(Color.RED)).convertedManaCost(1).build();
        Card card2 = ImmutableCard.builder().name("Card2").colorIdentity(Set.of(Color.BLUE)).convertedManaCost(2).build();
        when(cardCatalog.getAllCards()).thenReturn(Flux.just(card1, card2));
        trieCardSuggester.populateTrie(cardCatalog);
        System.out.println(trieCardSuggester.getTrie());

        // Test
        //Метод public Collection<Card> suggestCardsByName(String name) в package org.example.mtgtests.service.trie требует refactoring?
        Collection<Card> suggestions = trieCardSuggester.suggestCardsByName("Nonexistent");
        System.out.println(suggestions);

        // Assertions
        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testSuggestCardsByName_emptyTrie() {
        // Test
        Collection<Card> suggestions = trieCardSuggester.suggestCardsByName("Card");

        // Assertions
        assertTrue(suggestions.isEmpty());
    }
}