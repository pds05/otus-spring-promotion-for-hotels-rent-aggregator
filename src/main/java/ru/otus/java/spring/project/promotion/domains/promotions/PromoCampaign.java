package ru.otus.java.spring.project.promotion.domains.promotions;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignResult;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignStatus;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignType;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "promo_campaigns")
public class PromoCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_type", length = 50)
    private PromoCampaignType campaignType;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @CreationTimestamp
    @Column(name = "create_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updatedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PromoCampaignStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private PromoCampaignResult result;

    @Column(name = "details")
    private String details;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "campaign_id")
    private Set<CampaignHotelParameter> hotelParameters;

    public void addHotelParameter(CampaignHotelParameter hotelParameter) {
        if (hotelParameters == null) {
            hotelParameters = new HashSet<>();
            hotelParameters.add(hotelParameter);
        } else {
            hotelParameters.add(hotelParameter);
        }
    }

    public void removeHotelParameter(CampaignHotelParameter hotelParameter) {
        if (hotelParameters != null) {
            hotelParameters.remove(hotelParameter);
        }
    }

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "campaign_id")
    private Set<CampaignProvider> campaignProviders;

    public void addCampaignProvider(CampaignProvider campaignProvider) {
        if (campaignProviders == null) {
            campaignProviders = new HashSet<>();
            campaignProviders.add(campaignProvider);
        } else {
            campaignProviders.add(campaignProvider);
        }
    }

    public void addProviderId(long providerId) {
        CampaignProvider campaignProvider = new CampaignProvider();
        campaignProvider.setProviderId(providerId);
        if (campaignProviders == null) {
            campaignProviders = new HashSet<>();
            campaignProviders.add(campaignProvider);
        } else {
            campaignProviders.add(campaignProvider);
        }
    }

    public void removeCampaignProvider(CampaignProvider campaignProvider) {
        if (campaignProviders != null) {
            campaignProviders.remove(campaignProvider);
        }
    }

    public void removeProviderId(long providerId) {
        if (campaignProviders != null) {
            CampaignProvider campaignProvider = campaignProviders.stream().filter(cp -> cp.getProviderId() == providerId).findFirst().orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
            campaignProviders.remove(campaignProvider);
        }
    }

    public List<Long> getProviderIds() {
        if (campaignProviders != null) {
            return campaignProviders.stream().map(CampaignProvider::getProviderId).toList();
        } else {
            return Collections.emptyList();
        }
    }

}
