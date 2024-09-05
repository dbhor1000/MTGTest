package org.example.mtgtests.service.suggest;

import org.example.mtgtests.catalog.models.Card;

import java.util.Collection;

public interface CardSuggester {
  Collection<Card> suggestCardsByName(String name);
}
