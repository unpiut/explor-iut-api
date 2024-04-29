/*
 * Copyright (C) 2024 IUT Laval - Le Mans Université.
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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import explorateurIUT.services.mailManagement.MailRequestAttachement;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Remi Venant
 */
@Repository
public class PendingMailAttachementRepositoryImpl implements PendingMailAttachementRepository {

    private static final Log LOG = LogFactory.getLog(PendingMailAttachementRepositoryImpl.class);

    private static final String PENDING_MAIL_ID_FIELD = "pendingMailId";
    private static final String CREATION_DATETIME_FIELD = "creationDateTime";

    private final GridFsOperations operations;

    @Autowired
    public PendingMailAttachementRepositoryImpl(GridFsOperations operations) {
        this.operations = operations;
    }

    @Override
    public void save(MailRequestAttachement attachement, PendingMail pendingMail) {
        if (pendingMail.getId() == null || pendingMail.getCreationDateTime() == null) {
            throw new IllegalArgumentException("Cannot save pending mail attachement without any pending mail id or creation datetime");
        }

        // Create the metada object to add the pendingMailId to the file
        final DBObject fileMetaData = new BasicDBObject();
        fileMetaData.put(PENDING_MAIL_ID_FIELD, pendingMail.getId());
        fileMetaData.put(CREATION_DATETIME_FIELD, pendingMail.getCreationDateTime());

        // store the file with its name, type and metadata
        try {
            this.operations.store(attachement.file().getInputStream(),
                    attachement.fileName(), attachement.file().getContentType(), fileMetaData);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to save attachement", ex);
        }
    }

    @Override
    public Stream<GridFSFile> streamByPendingMailId(String pendingMailId) {
        final Query query = new Query(
                Criteria.where(PENDING_MAIL_ID_FIELD).is(pendingMailId)
        );
        GridFSFindIterable gfsIter = this.operations.find(query);
        return StreamSupport.stream(gfsIter.spliterator(), false);
    }

    @Override
    public void deleteByCreationDateTimeBefore(LocalDateTime creationDateTime) {
        final Query query = new Query(
                Criteria.where(CREATION_DATETIME_FIELD).lt(creationDateTime)
        );
        this.operations.delete(query);
    }

    @Override
    public void deleteByPendingMailId(String pendingMailId) {
        final Query query = new Query(
                Criteria.where(PENDING_MAIL_ID_FIELD).is(pendingMailId)
        );
        this.operations.delete(query);
    }

}
