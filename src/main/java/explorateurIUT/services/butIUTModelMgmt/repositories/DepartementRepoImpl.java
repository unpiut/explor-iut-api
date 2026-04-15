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

import explorateurIUT.model.Departement;
import explorateurIUT.model.DepartementRepository;
import explorateurIUT.model.projections.DepartementCodesOfIUTId;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModelManager;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Rémi Venant
 */
@Component
public class DepartementRepoImpl extends AbstractMemoryModelRepoImpl implements DepartementRepository {

    @Autowired
    public DepartementRepoImpl(BUTIUTModelManager modelManager) {
        super(modelManager);
    }

    @Override
    public Stream<DepartementCodesOfIUTId> streamIUTIdByIdIn(Collection<String> ids) {
        // From Mongo agg request:
        //    @Aggregation(pipeline = {
        //        "{$match: {_id: {$in: ?0}}}",
        //        "{$group: {_id: '$iut', codes: {$addToSet:  '$code'}}}",
        //        "{$set: {iut: '$_id', _id:'$$REMOVE'}}"
        //    })
        // Create a set of departement id lowered to filter from
        // Filter departements where iut id is contained in the set
        // Collect if a map of <iut id, Set of code>
        // Generate of list of DepartementCodesOfIUTId from the map
        final Set<String> deptIdsLowered = new HashSet<>(ids);
        final Map<String, Departement> deptsById = this.getActiveModel().getDepartementsById();
        return deptIdsLowered.stream()
                .map(deptsById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        dept -> dept.getIut().getId(),
                        dept -> Set.of(dept.getCode()),
                        DepartementRepoImpl::mergeSets))
                .entrySet()
                .stream()
                .map(e -> new DepartementCodesOfIUTId(e.getKey(), List.copyOf(e.getValue())));
    }

    private static Set<String> mergeSets(Set<String> s1, Set<String> s2) {
        HashSet<String> sMerged = new HashSet<>(s1);
        sMerged.addAll(s2);
        return sMerged;
    }
}
