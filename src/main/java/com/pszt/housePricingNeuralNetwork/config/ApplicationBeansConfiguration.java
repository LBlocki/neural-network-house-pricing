package com.pszt.housePricingNeuralNetwork.config;

import com.google.inject.*;
import com.pszt.housePricingNeuralNetwork.execute.Execution;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import com.pszt.housePricingNeuralNetwork.repository.PictureRepository;
import com.pszt.housePricingNeuralNetwork.repository.PictureRepositoryImpl;
import com.pszt.housePricingNeuralNetwork.service.LoggerService;
import com.pszt.housePricingNeuralNetwork.service.LoggerServiceImpl;
import com.pszt.housePricingNeuralNetwork.service.PictureService;
import com.pszt.housePricingNeuralNetwork.service.PictureServiceImpl;
import lombok.Value;

/**
 * Sluzy konfiguracji dependency injection aplikacji. Mozna wykorzystac injector aby pobrac
 * dowolna instancje klasy okreslonej w konfiguracji
 */
@Value
public class ApplicationBeansConfiguration {

    private static Injector injector = Guice.createInjector(new BeanModule());

    public static <T> T getInstance(Class<T> value) {
        return injector.getInstance(value);
    }

    private static class BeanModule extends AbstractModule {
        /**
         * Konfiguracja modułu.
         */
        protected void configure() {
            bind(LoggerService.class).to(LoggerServiceImpl.class).in(Scopes.SINGLETON);
            bind(MessageProducer.class).in(Scopes.SINGLETON);
            bind(Execution.class).in(Scopes.SINGLETON);
            bind(PictureRepository.class).to(PictureRepositoryImpl.class).in(Scopes.SINGLETON);
            bind(PictureService.class).to(PictureServiceImpl.class).in(Scopes.SINGLETON);
        }

    }
}
