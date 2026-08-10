package com.valuego.groups.entity.Enum;

public enum Destination {
    BUSAN("부산"),
    GANGNEUNG("강릉"),
    GYEONGJU("경주"),
    YEOSU("여수"),
    JEONJU("전주"),
    SOKCHO("속초");

    private final String description;

    Destination(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
