package com.amazonlocker.dto;

import jakarta.validation.constraints.NotNull;

public record ClearRequest(@NotNull Long compartmentId) {
}
