package com.pszt.housePricingNeuralNetwork.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.pszt.housePricingNeuralNetwork.execute.ExecutionService;
import com.pszt.housePricingNeuralNetwork.execute.NNetExecutionService;
import com.pszt.housePricingNeuralNetwork.logger.MessageProducer;
import com.pszt.housePricingNeuralNetwork.repository.CSVFileRepository;
import com.pszt.housePricingNeuralNetwork.repository.CSVFileRepositoryImpl;
import com.pszt.housePricingNeuralNetwork.service.CSVFileService;
import com.pszt.housePricingNeuralNetwork.service.CSVFileServiceImpl;
import com.pszt.housePricingNeuralNetwork.service.LoggerService;
import com.pszt.housePricingNeuralNetwork.service.LoggerServiceImpl;
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
            bind(ExecutionService.class).to(NNetExecutionService.class).in(Scopes.SINGLETON);
            bind(CSVFileRepository.class).to(CSVFileRepositoryImpl.class).in(Scopes.SINGLETON);
            bind(CSVFileService.class).to(CSVFileServiceImpl.class).in(Scopes.SINGLETON);
        }

    }
}
