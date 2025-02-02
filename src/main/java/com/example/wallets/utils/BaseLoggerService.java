package com.example.wallets.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseLoggerService {
    protected final Logger logger;

    protected BaseLoggerService() {
        this.logger = LoggerFactory.getLogger(getClass());
    }
}
