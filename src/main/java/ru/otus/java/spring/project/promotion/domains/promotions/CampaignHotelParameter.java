package ru.otus.java.spring.project.promotion.domains.promotions;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "campaign_hotel_parameters")
public class CampaignHotelParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private CtCity city;

    @Column(name = "campaign_id")
    private Long campaignId;

    @Column(name = "date_in")
    private LocalDate dateIn;

    @Column(name = "date_out")
    private LocalDate dateOut;

    @Column(name = "guests")
    private Integer guests;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "campaign_hotel_parameters_ct_hotel_type_rel",
            joinColumns = @JoinColumn(name = "campaign_hotel_parameter_id"),
    inverseJoinColumns = @JoinColumn(name = "ct_hotel_type_id"))
    private Set<CtHotelType> ctHotelTypes;

    public void addCtHotelType(CtHotelType ctHotelType) {
        if (this.ctHotelTypes == null) {
            this.ctHotelTypes = new HashSet<>();
            this.ctHotelTypes.add(ctHotelType);
        } else {
            this.ctHotelTypes.add(ctHotelType);
        }
    }

    public void removeCtHotelType(CtHotelType ctHotelType) {
        if (this.ctHotelTypes != null) {
            this.ctHotelTypes.remove(ctHotelType);
        }
    }

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "campaign_hotel_parameters_ct_food_type_rel",
            joinColumns = @JoinColumn(name = "campaign_hotel_parameter_id"),
            inverseJoinColumns = @JoinColumn(name = "ct_food_type_id"))
    private Set<CtFoodType> ctFoodTypes;

    public void addCtFoodType(CtFoodType ctFoodType) {
        if (this.ctFoodTypes == null) {
            this.ctFoodTypes = new HashSet<>();
            this.ctFoodTypes.add(ctFoodType);
        } else {
            this.ctFoodTypes.add(ctFoodType);
        }
    }

    public void removeCtFoodType(CtFoodType ctFoodType) {
        if (this.ctFoodTypes != null) {
            this.ctFoodTypes.remove(ctFoodType);
        }
    }
}
