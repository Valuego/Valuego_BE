package com.valuego.styles.api.dto.response;

import java.util.List;

public record StyleAiResDto(
        String dnaTitle,
        String dnaDescription,
        List<String> tags
) {
}
