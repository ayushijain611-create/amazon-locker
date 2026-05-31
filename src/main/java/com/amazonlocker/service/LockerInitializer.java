package com.amazonlocker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LockerInitializer implements ApplicationRunner {

    private final LockerService lockerService;

    @Override
    public void run(ApplicationArguments args) {
        lockerService.seedCompartments();
        lockerService.initQueues();
    }
}
