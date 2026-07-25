package ru.otus.java.spring.project.promotion.dtos.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProviderDto {

    private Long id;

    private String title;

    private String description;

    private Boolean isActive;

}
