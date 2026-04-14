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
package explorateurIUT.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Rémi Venant
 */
public class PendingMailRepositoryImpl implements PendingMailRepositoryCustom {

    private static final Log LOG = LogFactory.getLog(PendingMailRepositoryImpl.class);

    private final EntityManager entityManager;

    @Autowired
    public PendingMailRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public int clearMailsByCreationDateTimeBefore(LocalDateTime creationDateTime) {
        // 3 delete request to do: PendingMailAttachement, PendingMailIUTRecipient, PendingMail
        // Common filter: creationTime is before
        final CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        int totalRowRemoved = 0;

        // DELETION REQUEST FOR PendingMailAttachement
        {
            // 1.1. Create the Delete query for pendingMailAttachement
            CriteriaDelete<PendingMailAttachement> pmaDelete = cb.createCriteriaDelete(PendingMailAttachement.class);
            Root<PendingMailAttachement> rootPendingMailAttachement = pmaDelete.from(PendingMailAttachement.class);
            // 1.2. Create the Subquery to find A IDs based on the date
            Subquery<Long> subquery = pmaDelete.subquery(Long.class);
            Root<PendingMail> rootPendingMail = subquery.from(PendingMail.class);
            // Define the subquery selection: SELECT a.id FROM A a WHERE a.creationDate < :dateLimit
            subquery.select(rootPendingMail.get("id"))
                    .where(cb.lessThan(rootPendingMail.get("creationTime"), creationDateTime));
            // 1.3. Set the WHERE clause for the delete: WHERE b.a.id IN (subquery)
            pmaDelete.where(rootPendingMailAttachement.get("mail").get("id").in(subquery));
            // 1.4. Execute
            totalRowRemoved += this.entityManager.createQuery(pmaDelete).executeUpdate();
        }
        // DELETION REQUEST FOR PendingMailIUTRecipient
        {
            // 2.1. Create the Delete query for pendingMailIUTRecipient
            CriteriaDelete<PendingMailIUTRecipient> pmrDelete = cb.createCriteriaDelete(PendingMailIUTRecipient.class);
            Root<PendingMailIUTRecipient> rootPendingMailIUTRecipient = pmrDelete.from(PendingMailIUTRecipient.class);
            // 2.2. Create the Subquery to find A IDs based on the date
            Subquery<Long> subquery = pmrDelete.subquery(Long.class);
            Root<PendingMail> rootPendingMail = subquery.from(PendingMail.class);
            // Define the subquery selection: SELECT a.id FROM A a WHERE a.creationDate < :dateLimit
            subquery.select(rootPendingMail.get("id"))
                    .where(cb.lessThan(rootPendingMail.get("creationTime"), creationDateTime));
            // 2.3. Set the WHERE clause for the delete: WHERE b.a.id IN (subquery)
            pmrDelete.where(rootPendingMailIUTRecipient.get("mail").get("id").in(subquery));
            // 2.4. Execute
            totalRowRemoved += this.entityManager.createQuery(pmrDelete).executeUpdate();
        }
        // DELETION REQUEST FOR PendingMail
        {
            // 3.1. Create the Delete query for PendingMail
            CriteriaDelete<PendingMail> pmDelete = cb.createCriteriaDelete(PendingMail.class);
            Root<PendingMail> rootPendingMail = pmDelete.from(PendingMail.class);
            // 3.2. Set the WHERE clause for the delete: WHERE a.creationDate < :dateLimit
            pmDelete.where(cb.lessThan(rootPendingMail.get("creationTime"), creationDateTime));
            // 3.3. Execute
            totalRowRemoved += this.entityManager.createQuery(pmDelete).executeUpdate();
        }
        return totalRowRemoved;
    }

}
