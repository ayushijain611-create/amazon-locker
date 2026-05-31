package com.amazonlocker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "access_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessToken {

    private static final int MAX_PIN_RETRIES = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(nullable = false)
    private LocalDateTime issuedDateTime;

    @Column(nullable = false)
    private LocalDateTime expiryDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compartment_id", nullable = false)
    private Compartment compartment;

    @Column(nullable = false, length = 6)
    private String pin;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    public void setExpiryDateTime() {
        this.expiryDateTime = LocalDateTime.now().plusDays(7);
    }

    public void assignUniquePin(Predicate<String> isPinInUse) {
        for (int attempt = 0; attempt < MAX_PIN_RETRIES; attempt++) {
            String candidate = String.format(
                    "%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
            if (!isPinInUse.test(candidate)) {
                this.pin = candidate;
                return;
            }
        }
        throw new IllegalStateException("Unable to generate unique pin");
    }

    public void initForCompartment(Compartment compartment, Predicate<String> isPinInUse) {
        this.compartment = compartment;
        this.issuedDateTime = LocalDateTime.now();
        this.active = true;
        assignUniquePin(isPinInUse);
        setExpiryDateTime();
    }

    public boolean isTokenActive() {
        return active
                && expiryDateTime != null
                && expiryDateTime.isAfter(LocalDateTime.now());
    }

    public boolean isExpired() {
        return !isTokenActive();
    }

    public void deactivate() {
        this.active = false;
    }

    public void deactivateIfExpired() {
        if (expiryDateTime != null && !expiryDateTime.isAfter(LocalDateTime.now())) {
            deactivate();
        }
    }
}
