package ru.yandex.yandexlavka.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Region {
    @Id
    private Integer id;

    @ManyToMany(mappedBy = "regions")
    private List<Courier> couriers = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "region")
    private List<Order> orders = new ArrayList<>();
}
