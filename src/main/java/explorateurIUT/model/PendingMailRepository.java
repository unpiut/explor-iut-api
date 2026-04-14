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

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Julien Fourdan
 */
public interface PendingMailRepository extends CrudRepository<PendingMail, Long>, PendingMailRepositoryCustom {

    Optional<PendingMail> findByCreationDateTimeAndReplyTo(LocalDateTime creationDateTime, String replyTo);

    @Modifying
    @Query("update PendingMail pm set pm.lastConfirmationMail = ?2 where pm.id = ?1")
    int findAndSetLastConfirmationMailById(Long id, LocalDateTime lastConfirmationMail);
    
    @Override
    int clearMailsByCreationDateTimeBefore(LocalDateTime creationDateTime);
}
