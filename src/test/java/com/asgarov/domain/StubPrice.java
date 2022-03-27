package com.asgarov.domain;

import java.math.BigDecimal;

public class StubPrice implements Price {
    @Override
    public BigDecimal getInitialPrice() {
        return BigDecimal.valueOf(10);
    }
}
