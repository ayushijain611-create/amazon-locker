package com.amazonlocker.repository;

import com.amazonlocker.entity.AccessToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
            FROM AccessToken a
            WHERE a.pin = :pin
              AND a.active = true
              AND a.expiryDateTime > CURRENT_TIMESTAMP
            """)
    boolean isPinInUse(@Param("pin") String pin);

    @Query("""
            SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
            FROM AccessToken a
            WHERE a.compartment.compartmentId = :compartmentId
              AND a.active = true
              AND a.expiryDateTime > CURRENT_TIMESTAMP
            """)
    boolean hasLiveToken(@Param("compartmentId") Long compartmentId);

    @Query("""
            SELECT a FROM AccessToken a
            WHERE a.compartment.compartmentId = :compartmentId
              AND a.active = true
              AND a.expiryDateTime > CURRENT_TIMESTAMP
            """)
    Optional<AccessToken> findLiveToken(@Param("compartmentId") Long compartmentId);

    @Query("""
            SELECT a.expiryDateTime AS expiryDateTime,
                   a.compartment.compartmentId AS compartmentId,
                   a.pin AS pin
            FROM AccessToken a
            WHERE a.tokenId = :tokenId
            """)
    Optional<TokenDetailsView> getTokenDetails(@Param("tokenId") Long tokenId);

    List<AccessToken> findByCompartment_CompartmentIdOrderByIssuedDateTimeDesc(Long compartmentId);

    @Query("""
            SELECT a FROM AccessToken a
            WHERE a.pin = :pin AND a.active = true
            """)
    Optional<AccessToken> findActiveByPin(@Param("pin") String pin);

    interface TokenDetailsView {
        LocalDateTime getExpiryDateTime();

        Long getCompartmentId();

        String getPin();
    }
}
