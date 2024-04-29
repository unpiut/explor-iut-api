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
package explorateurIUT.model;

import explorateurIUT.model.projections.IUTMailOnly;
import explorateurIUT.model.projections.IUTSummary;
import java.util.Collection;
import java.util.stream.Stream;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Remi Venant
 */
public interface IUTRepository extends MongoRepository<IUT, String>, IUTRepositoryCustom {

    Stream<IUTSummary> streamSummariesBy();

    Stream<IUTMailOnly> streamMailOnlyByIdInAndMelIsNotNull(Collection<String> ids);
}
