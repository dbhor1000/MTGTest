package org.example.mtgtests.client.models;

import org.immutables.value.Value.Immutable;

import java.util.List;
import java.util.Optional;

@Immutable
public interface Page {

    List<RawCard> cards();
    Optional<Integer> nextPageNumber();
    Integer lastPageNumber();
}
