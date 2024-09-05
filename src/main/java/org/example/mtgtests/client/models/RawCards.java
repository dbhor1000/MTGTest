package org.example.mtgtests.client.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.mtgtests.client.models.ImmutableRawCards;
import org.immutables.value.Value.Parameter;
import org.immutables.value.Value.Immutable;

import java.util.List;

@Immutable(builder = false)
@JsonDeserialize(as = ImmutableRawCards.class)
public interface RawCards {

    @Parameter
    List<RawCard> cards();
}
