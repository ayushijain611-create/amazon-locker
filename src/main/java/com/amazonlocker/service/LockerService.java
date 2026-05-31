package com.amazonlocker.service;

import com.amazonlocker.entity.AccessToken;
import com.amazonlocker.entity.Compartment;
import com.amazonlocker.entity.CompartmentSize;
import com.amazonlocker.entity.PackageSize;
import com.amazonlocker.exception.CompartmentNotAvailableException;
import com.amazonlocker.exception.CompartmentNotFoundException;
import com.amazonlocker.exception.CompartmentNotOccupiedException;
import com.amazonlocker.exception.PinInvalidException;
import com.amazonlocker.repository.AccessTokenRepository;
import com.amazonlocker.repository.CompartmentRepository;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LockerService {

    private static final int COMPARTMENTS_PER_SIZE = 20;

    private final CompartmentRepository compartmentRepository;
    private final AccessTokenService accessTokenService;
    private final AccessTokenRepository tokenRepository;

    private final Map<CompartmentSize, Queue<Long>> availableCompartments =
            new EnumMap<>(CompartmentSize.class);

    public void initQueues() {
        for (CompartmentSize size : CompartmentSize.values()) {
            availableCompartments.put(size, new LinkedList<>());
        }
        for (Compartment compartment : compartmentRepository.findByIsOccupiedFalse()) {
            availableCompartments
                    .get(compartment.getCompartmentSize())
                    .offer(compartment.getCompartmentId());
        }
    }

    public void seedCompartments() {
        for (CompartmentSize size : CompartmentSize.values()) {
            while (compartmentRepository.countByCompartmentSize(size) < COMPARTMENTS_PER_SIZE) {
                compartmentRepository.save(Compartment.builder()
                        .compartmentSize(size)
                        .isOccupied(false)
                        .build());
            }
        }
    }

    public synchronized String depositPackage(PackageSize pkgSize) {
        CompartmentSize compSize = toCompSize(pkgSize);
        Long compartmentId = takeCompartment(compSize);

        Compartment compartment = compartmentRepository.findById(compartmentId)
                .orElseThrow(CompartmentNotFoundException::new);
        compartment.setOccupied(true);
        compartmentRepository.save(compartment);

        AccessToken token = generateToken(compartmentId);
        return token.getPin();
    }

    public synchronized long collectPackage(String pin) {
        AccessToken token = validatePin(pin);
        Compartment compartment = token.getCompartment();
        long compartmentId = compartment.getCompartmentId();

        compartment.setOccupied(false);
        compartmentRepository.save(compartment);
        releaseCompartment(compartment);

        token.deactivate();
        tokenRepository.save(token);

        return compartmentId;
    }

    public synchronized boolean clearCompartment(long compartmentId) {
        Compartment compartment = compartmentRepository.findById(compartmentId)
                .orElseThrow(CompartmentNotFoundException::new);

        if (!compartment.isOccupied()) {
            throw new CompartmentNotOccupiedException();
        }

        compartment.setOccupied(false);
        compartmentRepository.save(compartment);
        deactToken(compartmentId);
        releaseCompartment(compartment);

        return true;
    }

    private CompartmentSize toCompSize(PackageSize pkgSize) {
        return CompartmentSize.valueOf(pkgSize.name());
    }

    private Long takeCompartment(CompartmentSize compSize) {
        Long compartmentId = availableCompartments.get(compSize).poll();
        if (compartmentId == null) {
            throw new CompartmentNotAvailableException();
        }
        return compartmentId;
    }

    private void releaseCompartment(Compartment compartment) {
        availableCompartments
                .get(compartment.getCompartmentSize())
                .offer(compartment.getCompartmentId());
    }

    private AccessToken generateToken(Long compartmentId) {
        Compartment compartment = compartmentRepository.findById(compartmentId)
                .orElseThrow(CompartmentNotFoundException::new);
        return accessTokenService.createToken(compartment);
    }

    private AccessToken validatePin(String pin) {
        AccessToken token = tokenRepository.findActiveByPin(pin)
                .orElseThrow(PinInvalidException::new);

        token.deactivateIfExpired();
        if (!token.isTokenActive()) {
            tokenRepository.save(token);
            throw new PinInvalidException();
        }
        return token;
    }

    private void deactToken(Long compartmentId) {
        tokenRepository.findLiveToken(compartmentId).ifPresent(token -> {
            token.deactivate();
            tokenRepository.save(token);
        });
    }
}
