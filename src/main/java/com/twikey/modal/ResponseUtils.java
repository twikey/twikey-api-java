package com.twikey.modal;

import java.net.http.HttpHeaders;
import java.util.Optional;

public final class ResponseUtils {

    private ResponseUtils() {
        // prevent instantiation
    }

    public static Optional<String> extractFilenameFromContentDisposition(HttpHeaders headers) {
        return headers.firstValue("content-disposition")
                .flatMap(ResponseUtils::parseContentDispositionFilename);
    }

    static Optional<String> parseContentDispositionFilename(String disposition) {
        if (disposition == null || disposition.isBlank()) {
            return Optional.empty();
        }

        for (String part : disposition.split(";")) {
            part = part.trim();

            if (part.startsWith("filename*=")) {
                return Optional.of(decodeExtendedFilename(part.substring(9)));
            }

            if (part.startsWith("filename=")) {
                return Optional.of(stripQuotes(part.substring(9)));
            }
        }

        return Optional.empty();
    }

    private static String stripQuotes(String value) {
        return value.replaceAll("^\"|\"$", "");
    }

    private static String decodeExtendedFilename(String value) {
        int idx = value.indexOf("''");
        if (idx > 0) {
            return java.net.URLDecoder.decode(value.substring(idx + 2), java.nio.charset.StandardCharsets.UTF_8);
        }
        return value;
    }
}
