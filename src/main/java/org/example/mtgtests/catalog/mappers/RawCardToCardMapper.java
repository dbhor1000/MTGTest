package org.example.mtgtests.catalog.mappers;
import org.example.mtgtests.catalog.models.Color;
import org.example.mtgtests.catalog.models.Card;
import org.example.mtgtests.catalog.models.ImmutableCard;
import org.example.mtgtests.client.models.RawCard;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public final class RawCardToCardMapper implements Converter<RawCard, Card> {

    @Override
    public Card convert(RawCard rawCard) {
        return ImmutableCard.builder()
                .name(rawCard.name())
                .colorIdentity(rawCard.colorIdentity().stream().map(Color::parse).collect(Collectors.toSet()))
                .convertedManaCost(Double.valueOf(rawCard.cmc()).intValue())
                .build();
    }

    private static final Converter<Set<String>, Map<Color, Integer>> CONVERTED_MANA_COST_MAPPER = (cmc) -> {
        return null;
    };
}

