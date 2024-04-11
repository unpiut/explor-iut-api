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

import explorateurIUT.model.BUT;
import explorateurIUT.model.BUTRepository;
import explorateurIUT.model.IUT;
import explorateurIUT.model.IUTFormationFilter;
import explorateurIUT.model.IUTRepository;
import explorateurIUT.model.cacheUtils.SerializableBUTSummary;
import explorateurIUT.model.cacheUtils.SerializableIUTSummary;
import explorateurIUT.model.projections.BUTSummary;
import explorateurIUT.model.projections.IUTSummary;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author Remi Venant
 */
@Service
@Validated
public class BUTIUTServiceImpl implements BUTService, IUTService {

    private static final Log LOG = LogFactory.getLog(BUTIUTServiceImpl.class);

    private final BUTRepository butRepo;

    private final IUTRepository iutRepo;

    @Autowired
    public BUTIUTServiceImpl(BUTRepository butRepo, IUTRepository iutRepo) {
        this.butRepo = butRepo;
        this.iutRepo = iutRepo;
    }

    @Cacheable(cacheNames = "butSummaries", unless = "#result == null || #result.isEmpty()")
    @Override
    public List<BUTSummary> findBUTSummaries() {
        return this.butRepo.streamSummariesBy().map(SerializableBUTSummary::fromBUTSummary).toList();
    }

    @Override
    public BUT findBUT(String butId) throws ConstraintViolationException, NoSuchElementException {
        return this.butRepo.findById(butId)
                .orElseThrow(() -> new NoSuchElementException("BUT introuvable"));
    }

    @Override
    public BUT findBUTByCode(String butCode) throws ConstraintViolationException, NoSuchElementException {
        return this.butRepo.findByCodeIgnoreCase(butCode)
                .orElseThrow(() -> new NoSuchElementException("BUT introuvable"));
    }

    @Cacheable(cacheNames = "iutSummaries", unless = "#result == null || #result.isEmpty()")
    @Override
    public List<IUTSummary> findIUTSummaries() {
        return this.iutRepo.streamSummariesBy().map(SerializableIUTSummary::fromIUTSummary).toList();
    }

    @Override
    public IUT findIUT(String iutId) throws ConstraintViolationException, NoSuchElementException {
        return this.iutRepo.findById(iutId)
                .orElseThrow(() -> new NoSuchElementException("IUT introuvable"));
    }

    @Override
    public Stream<IUTSummary> streamFilteredIUTSummaries(IUTFormationFilter filter) throws ConstraintViolationException {
        return this.iutRepo.streamSummariesByFilter(filter).stream();
    }

    private static void checkUniqueParamPresence(MultiValueMap<String, String> params, String param, String exMessage) {
        List<String> paramList = params.get(param);
        if (paramList != null && paramList.size() > 1) {
            throw new IllegalArgumentException(exMessage);
        }
    }

    private static Double checkAndConvertRawParamToDoubleOrNull(String rawParam, String exMessage) {
        if (rawParam == null) {
            return null;
        }
        try {
            return Double.valueOf(rawParam);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(exMessage);
        }
    }

    private static boolean convertBoolParams(String rawParam) {
        return !(rawParam == null || rawParam.equalsIgnoreCase("no") || rawParam.equalsIgnoreCase("false") || rawParam.equals("0"));
    }

    private static final Set<String> ALLOWED_FILTER_Q_PARAMS = new HashSet<>(Arrays.asList("region", "job", "but", "q", "lat", "lon", "rad", "all-depts", "block-only"));

    private void checkParamsAccepted(MultiValueMap<String, String> params, String exMessage) {
        for (String pName : params.keySet()) {
            if (!ALLOWED_FILTER_Q_PARAMS.contains(pName)) {
                throw new IllegalArgumentException(exMessage + " (" + pName + ")");
            }
        }
    }

    @Override
    public IUTFormationFilter generateFilterFromQueryParams(MultiValueMap<String, String> params) throws IllegalArgumentException {
        /*
        q: unique, str, trimed, not blanck
        lat: unique, double,
        lon: unique, double,
        rad: unique, double, stricly positive,
        region: multiple, each trimed, not blanck
        but: multiple, each trimed, not blanck
        block-but: multipled, each trimed, not blanck
        all-depts: unique, true if not equals to 0, false, no (case ignored) false otherwise
         */
        // Check all params are allowed
        checkParamsAccepted(params, "Paramètre de recherche invalide");
        // Start checking params unicity
        checkUniqueParamPresence(params, "q", "Une seule recherche textuelle doit être fournie au maximum");
        checkUniqueParamPresence(params, "lat", "Une seule latitude de zone géographique de filtrage doit être fournie au maximum");
        checkUniqueParamPresence(params, "lon", "Une seule longitude de zone géographique de filtrage textuelle doit être fournie au maximum");
        checkUniqueParamPresence(params, "rad", "Un seule rayon de zone géographique de filtrage textuelle doit être fournie au maximum");
        checkUniqueParamPresence(params, "all-depts", "Un seul indicateur d'inclusion de tous les départements doit être fourni au maximum");

        final IUTFormationFilter.Builder builder = new IUTFormationFilter.Builder();

        builder.withFreeTextQuery(params.getFirst("q"));
        builder.withGPSFilter(
                checkAndConvertRawParamToDoubleOrNull(params.getFirst("lat"), "Valeur numérique de latitude invalide."),
                checkAndConvertRawParamToDoubleOrNull(params.getFirst("lon"), "Valeur numérique de longitude invalide."),
                checkAndConvertRawParamToDoubleOrNull(params.getFirst("rad"), "Valeur numérique de rayon invalide."));
        builder.withRegions(params.get("region"));
        builder.withButs(params.get("but"));
        builder.withIncludeAllDepts(convertBoolParams(params.getFirst("all-depts")));
        return builder.build();
    }

}
