package com.groupeisi.company.dto;

import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentesDto {
    private Long id;
    private Date dateP;
    private Double quantity;
    private String productRef;
    private String productName;
    private Long userId;
    private String userEmail;
}