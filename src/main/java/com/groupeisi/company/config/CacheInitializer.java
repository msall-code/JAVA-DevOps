package com.groupeisi.company.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheInitializer implements ApplicationRunner {

    private final CacheManager cacheManager;

    @Override
    public void run(ApplicationArguments args) {
        cacheManager.getCacheNames()
                .forEach(cacheName -> {
                    cacheManager.getCache(cacheName).clear();
                    log.info("Cache '{}' vidé au démarrage", cacheName);
                });
        log.info("Tous les caches ont été réinitialisés");
    }
}