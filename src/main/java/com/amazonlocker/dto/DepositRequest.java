package com.amazonlocker.dto;

import com.amazonlocker.entity.PackageSize;
import jakarta.validation.constraints.NotNull;

public record DepositRequest(@NotNull PackageSize packageSize) {
}
