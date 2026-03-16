package com.groupeisi.company.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitsDto {
    private String ref;
    private String name;
    private Double stock;
    private Long userId;
    private String userEmail;
}