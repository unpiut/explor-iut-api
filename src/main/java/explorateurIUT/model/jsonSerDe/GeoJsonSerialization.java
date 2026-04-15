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
package explorateurIUT.model.jsonSerDe;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import explorateurIUT.model.GeoJsonPoint;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.jackson.JsonComponent;

/**
 *
 * @author Remi Venant
 */
@JsonComponent
public class GeoJsonSerialization {

    private static final Log LOG = LogFactory.getLog(GeoJsonSerialization.class);

    public static class Serializer extends JsonSerializer<GeoJsonPoint> {

        @Override
        public void serialize(GeoJsonPoint value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeStartArray();
                gen.writeNumber(value.getX());
                gen.writeNumber(value.getY());
                gen.writeEndArray();
            }
        }

    }

    public static class Deserializer extends JsonDeserializer<GeoJsonPoint> {

        @Override
        public GeoJsonPoint deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
            final JsonNode mainNode = jp.getCodec().readTree(jp);
            if (mainNode.isNull()) {
                return null;
            } else if (mainNode.isArray()) {
                Double x = this.extractCoordinate(mainNode.get(0));
                Double y = this.extractCoordinate(mainNode.get(1));
                if (x == null || y == null) {
                    throw new JsonParseException("Unable to parse GeoJsonPoint: invalid coordinates structure");
                }
                return new GeoJsonPoint(x, y);
            } else {
                throw new JsonParseException(jp, "Unable to parse GeoJsonPoint: wrong structure");
            }
        }

        private Double extractCoordinate(JsonNode node) {
            return node.isNumber() ? node.asDouble() : null;
        }

    }
}
