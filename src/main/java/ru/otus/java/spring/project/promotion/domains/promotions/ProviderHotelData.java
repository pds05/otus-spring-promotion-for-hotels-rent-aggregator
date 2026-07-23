package ru.otus.java.spring.project.promotion.domains.promotions;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString(exclude = "promoCampaign")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "provider_hotel_data")
public class ProviderHotelData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "campaign_id")
    private PromoCampaign promoCampaign;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "city_name", nullable = false)
    private String cityName;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "hotel_name", length = 50)
    private String hotelName;

    @Column(name = "hotel_room_id", nullable = false)
    private Long hotelRoomId;

    @Column(name = "hotel_room_name", length = 50)
    private String hotelRoomName;

    @Column(name = "hotel_room_rate_id", nullable = false)
    private Long hotelRoomRateId;

    @Column(name = "hotel_room_rate_name", length = 50)
    private String hotelRoomRateName;

    @Column(name = "max_guests")
    private Integer maxGuests;

    @PositiveOrZero
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "food", length = 50)
    private String food;

    @Column(name = "date_in")
    private LocalDate dateIn;

    @Column(name = "date_out")
    private LocalDate dateOut;

    @CreationTimestamp
    @Column(name = "date_create")
    private LocalDateTime dateCreate;

}
