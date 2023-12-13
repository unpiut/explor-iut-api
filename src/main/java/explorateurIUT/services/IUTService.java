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

import explorateurIUT.model.IUT;
import explorateurIUT.model.IUTFormationFilter;
import explorateurIUT.model.projections.IUTSummary;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Remi Venant
 */
public interface IUTService {

    IUTFormationFilter generateFilterFromQueryParams(
            @NotNull MultiValueMap<String, String> params
    ) throws IllegalArgumentException;

    List<IUTSummary> findIUTSummaries();

    Stream<IUTSummary> streamFilteredIUTSummaries(
            @NotNull IUTFormationFilter filter
    ) throws ConstraintViolationException;

    IUT findIUT(
            @NotNull @Pattern(regexp = "[abcdef0-9]{24}", flags = Pattern.Flag.CASE_INSENSITIVE) String iutId
    ) throws ConstraintViolationException, NoSuchElementException;
}
