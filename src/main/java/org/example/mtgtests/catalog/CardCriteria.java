package org.example.mtgtests.catalog;

import org.example.mtgtests.catalog.models.Color;
import org.immutables.value.Value.Immutable;
import java.util.Optional;
import java.util.Set;


@Immutable
public interface CardCriteria {

    default boolean exclusiveMatch() {
        return true;
    }

    Optional<String> nameContains();
    Optional<Set<Color>> colorIdentity();
}
