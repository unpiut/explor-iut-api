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
package explorateurIUT.model.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Rémi Venant
 */
public class PendingMailIUTRecipientDeptCodesConverter implements AttributeConverter<List<String>, String> {

    private static final Log LOG = LogFactory.getLog(PendingMailIUTRecipientDeptCodesConverter.class);

    private final ObjectMapper mapper;
    private final CollectionType deptCodesType;

    public PendingMailIUTRecipientDeptCodesConverter() {
        this.mapper = new ObjectMapper();
        this.deptCodesType = this.mapper.getTypeFactory()
                .constructCollectionType(List.class, String.class);
    }

    @Override
    public String convertToDatabaseColumn(List<String> deptCodes) {
        if (deptCodes == null || deptCodes.isEmpty()) {
            return null;
        }
        try {
            return this.mapper.writeValueAsString(deptCodes);
        } catch (JsonProcessingException ex) {
            LOG.warn("Unable to convert depts code to entity.", ex);
            return null;
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String deptCodesRepr) {
        if (deptCodesRepr == null || deptCodesRepr.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return this.mapper.readValue(deptCodesRepr, this.deptCodesType);
        } catch (JsonProcessingException ex) {
            LOG.warn("Unable to convert entity to depts code.", ex);
            return new ArrayList<>();
        }
    }

}
