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

import explorateurIUT.model.IUT;
import explorateurIUT.model.IUTRepository;
import explorateurIUT.model.projections.IUTMailOnly;
import explorateurIUT.model.projections.IUTSummary;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModelManager;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Rémi Venant
 */
@Component
public class IUTRepoImpl extends AbstractMemoryModelRepoImpl implements IUTRepository {

    @Autowired
    public IUTRepoImpl(BUTIUTModelManager modelManager) {
        super(modelManager);
    }

    @Override
    public Optional<IUT> findById(String id) {
        return Optional.ofNullable(this.getActiveModel().getIutsById().get(id));
    }

    @Override
    public Stream<IUTSummary> streamSummariesBy() {
        return this.getActiveModel().getIutsById().values().stream()
                .map(ProjectionsTools::createIUTSummary);
    }

    @Override
    public Stream<IUTMailOnly> streamMailOnlyByIdInAndMelIsNotNull(Collection<String> ids) {
        final Map<String, IUT> iutsByIdLowered = this.getActiveModel().getIutsById();
        // Create a set of ids to ensure unicity
        final HashSet<String> idsSet = new HashSet<>(ids);
        // Create a set of lowered ids to iterate from
        return idsSet.stream()
                .map(id -> iutsByIdLowered.get(id))
                .filter(Objects::nonNull)
                .filter(iut -> iut.getMel() != null)
                .map(ProjectionsTools::createIUTMailOnly);
    }

}
