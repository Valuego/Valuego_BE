package com.valuego.tourplace.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TourApiResDto {

    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {

        private Body body;
    }

    @Getter
    @NoArgsConstructor
    public static class Body {

        private Items items;
    }

    @Getter
    @NoArgsConstructor
    public static class Items {

        private List<Item> item;
    }

    @Getter
    @NoArgsConstructor
    public static class Item {

        @JsonProperty("contentid")
        private String contentId;

        @JsonProperty("contenttypeid")
        private String contentTypeId;

        private String title;

        private String addr1;

        private String firstimage;

        private String mapx;

        private String mapy;
    }
}
