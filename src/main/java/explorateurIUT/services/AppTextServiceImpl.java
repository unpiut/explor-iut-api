/*
 * Copyright (C) 2023 IUT Laval - Le Mans Université.
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
package explorateurIUT.services;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import explorateurIUT.model.AppText;
import explorateurIUT.model.AppTextRepository;
import jakarta.validation.constraints.NotBlank;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author Julien Fourdan
 */
@Service
@Validated
public class AppTextServiceImpl implements AppTextService {

    private final static String DEFAULT_LANGUAGE = "fr";
    private final AppTextRepository textRepo;

    @Autowired
    public AppTextServiceImpl(AppTextRepository appTextRepository) {
        textRepo = appTextRepository;
    }

    @Override
    public Map<String, String> getAppTextsByCode(@NotBlank String language) {
        return this.textRepo.streamByLanguage(language)
                .collect(Collectors.toMap(AppText::getCode, AppText::getContent));
    }

    @Override
    public Map<String, String> getDefaultAppTextsByCode() {
        return getAppTextsByCode(DEFAULT_LANGUAGE);
    }

}
