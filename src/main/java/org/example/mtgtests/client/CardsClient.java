package org.example.mtgtests.client;

import org.example.mtgtests.client.models.Page;
import org.springframework.cache.annotation.Cacheable;
import reactor.core.publisher.Mono;

public interface CardsClient {

    @Cacheable(value = "cardPages", cacheManager = "cardPagesCacheManager")
    Mono<Page> getPage(int index);
    Mono<Integer> getLastPageNumber();
}
