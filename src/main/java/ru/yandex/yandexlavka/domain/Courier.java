package ru.yandex.yandexlavka.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    public enum Type {
        FOOT(2, 3),
        BIKE(3, 2),
        AUTO(4, 1);

        private final int earningsQuotient;
        private final int ratingQuotient;

        Type(int earningsQuotient, int ratingQuotient) {
            this.earningsQuotient = earningsQuotient;
            this.ratingQuotient = ratingQuotient;
        }

        @JsonCreator
        public static Type getValue(String name) {
            return Type.valueOf(name);
        }
    }

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "courier_region",
        joinColumns = @JoinColumn(name = "courier_id"),
        inverseJoinColumns = @JoinColumn(name = "region_id"))
    private List<Region> regions = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "courier_shift",
            joinColumns = @JoinColumn(name = "courier_id"),
            inverseJoinColumns = @JoinColumn(name = "shift_id"))
    private List<Shift> workingHours = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "courier")
    private List<Order> orders = new ArrayList<>();

    public void addWorkingHours(Shift shift) {
        shift.getCouriers().add(this);
        this.workingHours.add(shift);
    }

    public void addRegion(Region region) {
        region.getCouriers().add(this);
        this.regions.add(region);
    }

    public List<Integer> getRegionsId() {
        List<Integer> ids = new ArrayList<>();
        this.regions.forEach(r -> ids.add(r.getId()));
        return ids;
    }

    public List<String> getWorkingHours() {
        List<String> workingHours = new ArrayList<>();
        this.workingHours.forEach(wh -> workingHours.add(wh.getWorkingHours()));
        return workingHours;
    }
}
