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

import explorateurIUT.model.BUT;
import explorateurIUT.model.BUTRepository;
import explorateurIUT.model.projections.BUTSummary;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModelManager;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Rémi Venant
 */
@Component
public class BUTRepoImpl extends AbstractMemoryModelRepoImpl implements BUTRepository {

    @Autowired
    public BUTRepoImpl(BUTIUTModelManager modelManager) {
        super(modelManager);
    }

    @Override
    public Stream<BUTSummary> streamSummariesBy() {
        return this.getActiveModel().getButsById().values().stream()
                .map(ProjectionsTools::createBUTSummary);
    }

    @Override
    public Optional<BUT> findById(String id) {
        System.out.println(this.getActiveModel().getButsById().toString());
        return Optional.ofNullable(this.getActiveModel().getButsById().get(id));
    }

    @Override
    public Optional<BUT> findByCodeIgnoreCase(String code) {
        return this.getActiveModel().getButsById().values().stream()
                .filter(but -> but.getCode().equalsIgnoreCase(code))
                .findAny();
    }

}
