package com.orbitalix.util;

import java.time.Instant;

public interface Clock {
    Instant now();

    static Clock systemUtc() { return Instant::now; }
}
