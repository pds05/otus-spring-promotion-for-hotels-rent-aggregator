package ru.otus.java.spring.project.promotion.dtos.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class HotelRoomsDto {

    private Long id;

    private String title;

    private String address;

    private String type;

    private Integer grade;

    private BigDecimal rating;

    private Map<String, List<String>> amenities = new TreeMap<>();

    private List<HotelRoomDto> rooms;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class HotelRoomDto{

        private Long id;

        private String title;

        private Integer size;

        private Integer roomsAmount;

        private Integer maxGuests;

        private List<String> beds;

        private List<String> amenities;

        private List<HotelRoomRateDto> rates;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class HotelRoomRateDto {

        private Long id;

        private String title;

        private String food;

        private Boolean refund;

        private BigDecimal price;
    }

}
