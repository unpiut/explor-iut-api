/*
 * Copyright (C) 2026 IUT Laval - Le Mans Université.
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
package explorateurIUT.services.butIUTModelMgmt.repositories;

import explorateurIUT.model.AppText;
import explorateurIUT.model.AppTextRepository;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModelManager;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Rémi Venant
 */
@Component
public class AppTextRepoImpl extends AbstractMemoryModelRepoImpl implements AppTextRepository {

    @Autowired
    public AppTextRepoImpl(BUTIUTModelManager modelManager) {
        super(modelManager);
    }

    @Override
    public Stream<AppText> streamByLanguageAndBackendMailTextFalse(String language) {
        return this.getActiveModel().getAppTextsById()
                .values().stream()
                .filter(v -> v.getLanguage().equalsIgnoreCase(language) && !v.isBackendMailText());
    }

    @Override
    public Stream<AppText> streamByLanguageAndBackendMailTextTrue(String language) {
        return this.getActiveModel().getAppTextsById()
                .values().stream()
                .filter(v -> v.getLanguage().equalsIgnoreCase(language) && v.isBackendMailText());
    }

}
