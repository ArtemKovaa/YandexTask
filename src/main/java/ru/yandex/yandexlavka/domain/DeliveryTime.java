package ru.yandex.yandexlavka.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class DeliveryTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String deliveryHours;

    @ManyToMany(mappedBy = "deliveryTimes")
    private List<Order> orders = new ArrayList<>();
}
