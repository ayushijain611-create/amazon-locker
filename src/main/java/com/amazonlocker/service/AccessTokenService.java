package com.amazonlocker.service;

import com.amazonlocker.entity.AccessToken;
import com.amazonlocker.entity.Compartment;
import com.amazonlocker.repository.AccessTokenRepository;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final AccessTokenRepository tokenRepository;

    public AccessToken createToken(Compartment compartment) {
        if (hasLiveToken(compartment.getCompartmentId())) {
            throw new IllegalStateException("Compartment already has an active token");
        }

        AccessToken token = new AccessToken();
        token.initForCompartment(compartment, pinInUseCheck());
        return tokenRepository.save(token);
    }

    public AccessToken regenerateToken(Long tokenId, Compartment compartment) {
        AccessToken expiredToken = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenId));

        expiredToken.deactivateIfExpired();
        if (expiredToken.isTokenActive()) {
            throw new IllegalStateException("Token is still active");
        }

        return createToken(compartment);
    }

    private boolean hasLiveToken(Long compartmentId) {
        return tokenRepository.hasLiveToken(compartmentId);
    }

    private Predicate<String> pinInUseCheck() {
        return tokenRepository::isPinInUse;
    }
}
