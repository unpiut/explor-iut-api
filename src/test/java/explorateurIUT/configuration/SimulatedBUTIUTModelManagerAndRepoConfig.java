/*
 * Copyright (C) 2024 IUT Laval - Le Mans Université.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package explorateurIUT.configuration;

import explorateurIUT.model.AppTextRepository;
import explorateurIUT.model.BUTRepository;
import explorateurIUT.model.DepartementRepository;
import explorateurIUT.model.IUTRepository;
import explorateurIUT.model.TestDatasetGenerator;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModelManager;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModelManagerImpl;
import explorateurIUT.services.butIUTModelMgmt.repositories.AppTextRepoImpl;
import explorateurIUT.services.butIUTModelMgmt.repositories.BUTRepoImpl;
import explorateurIUT.services.butIUTModelMgmt.repositories.DepartementRepoImpl;
import explorateurIUT.services.butIUTModelMgmt.repositories.IUTRepoImpl;
import jakarta.validation.Validator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 *
 * @author Remi Venant
 */
@TestConfiguration
public class SimulatedBUTIUTModelManagerAndRepoConfig {

    @ConditionalOnMissingBean
    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        //a common bean to enforce javax validation constraints
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public BUTIUTModelManager butIUTModelManager(Validator validator) {
        return new BUTIUTModelManagerImpl(validator);
    }

    @Bean
    public TestDatasetGenerator testDatasetGenerator(BUTIUTModelManager butIUTModelManager) {
        return new TestDatasetGenerator(butIUTModelManager);
    }

    @Bean
    public AppTextRepository appTextRepository(BUTIUTModelManager butIUTModelManager) {
        return new AppTextRepoImpl(butIUTModelManager);
    }

    @Bean
    public BUTRepository butRepository(BUTIUTModelManager butIUTModelManager) {
        return new BUTRepoImpl(butIUTModelManager);
    }

    @Bean
    public DepartementRepository departementRepository(BUTIUTModelManager butIUTModelManager) {
        return new DepartementRepoImpl(butIUTModelManager);
    }

    @Bean
    public IUTRepository iutRepository(BUTIUTModelManager butIUTModelManager) {
        return new IUTRepoImpl(butIUTModelManager);
    }
}
