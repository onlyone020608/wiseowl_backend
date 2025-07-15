package com.hyewon.wiseowl_backend.domain.facility.entity;

import com.hyewon.wiseowl_backend.domain.course.entity.Building;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    private FacilityCategory facilityCategory;

    private Integer floor;

    private String description;





}
