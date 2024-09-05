package org.example.mtgtests.client.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.mtgtests.client.models.ImmutableRawCard;
import org.immutables.value.Value.Immutable;

import java.util.Optional;
import java.util.Set;

@Immutable
@JsonDeserialize(as = ImmutableRawCard.class)
public interface RawCard {

    String name();
    String rarity();
    double cmc();

    Optional<String> text();
    Optional<String> manaCost();
    Set<String> colorIdentity();
    Set<String> colors();
    Set<String> types();
}
