package com.valuego.effort.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record EffortItemReqDto(
        @NotNull
        EffortItemCategory itemCategory,

        String title
) {
        public enum EffortItemCategory {
                DRIVING,
                RESERVATION,
                ETC
        }

        public static EffortItemReqDto of(EffortItemCategory itemCategory, String title) {
                return new EffortItemReqDto(itemCategory, title);
        }
}
