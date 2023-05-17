package ru.yandex.yandexlavka.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float weight;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "order_delivery_time",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "delivery_time_id"))
    private List<DeliveryTime> deliveryTimes = new ArrayList<>();

    private Integer cost;

    private Date completedTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "courier_id")
    private Courier courier;

    public void addRegion(Region region) {
        region.getOrders().add(this);
        this.region = region;
    }

    public void addDeliveryTime(DeliveryTime deliveryTime) {
        deliveryTime.getOrders().add(this);
        this.getDeliveryTimes().add(deliveryTime);
    }

    public Integer getRegionId() {
        return this.region.getId();
    }

    public List<String> getDeliveryHours() {
        List<String> deliveryHours = new ArrayList<>();
        this.deliveryTimes.forEach(dt -> deliveryHours.add(dt.getDeliveryHours()));
        return deliveryHours;
    }
}
