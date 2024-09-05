package org.example.mtgtests.client.mtg;

import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.springframework.http.HttpHeaders.LINK;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PageHeaderIntrospector {

    private static final String LINK_RELATIONSHIP_PATTERN = "<([\\w:/\\.\\d\\?=]+)>; rel=\"%s\"";
    private static final Pattern NEXT = compile(format(LINK_RELATIONSHIP_PATTERN, "next"));
    private static final Pattern LAST = compile(format(LINK_RELATIONSHIP_PATTERN, "last"));

    static Optional<UriComponents> getLastPageUri(final HttpHeaders headers) {
        return getLink(headers, LAST);
    }

    static Optional<UriComponents> getNextPageUri(final HttpHeaders headers) {
        return getLink(headers, NEXT);
    }

    private static Optional<UriComponents> getLink(final HttpHeaders headers, final Pattern pattern) {
        return headers.get(LINK).stream().map(content -> {
            final Matcher matcher = pattern.matcher(content);
            if (matcher.find() && matcher.groupCount() >= 1) {
                return matcher.group(1);
            }
            return null;
        }).filter(group -> group != null).findFirst().map(uri -> UriComponentsBuilder.fromUriString(uri).build());
    }

    private PageHeaderIntrospector() {
    }
}
