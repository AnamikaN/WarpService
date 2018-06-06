package com.anamika.app.service;

import javax.validation.constraints.NotNull;

/**
 *
 */
public interface WarpService {
    void scheduleFleet(@NotNull final String fleetId, @NotNull final Integer duration, @NotNull final Double warpSpeed);
}
