# Copyright (C) 2024 IUT Laval - Le Mans Université.
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
# MA 02110-1301  USA

# Perform compilation in a separate builder container
FROM maven:3.9.9-amazoncorretto-21 AS compiler
WORKDIR /app
# Copy only the pom.xml and download dependencies
COPY ./pom.xml ./
RUN mvn dependency:resolve

# Copy the source code then perform the build
COPY . .
RUN mvn clean package -Dmaven.test.skip

# Perform the extraction in a separate builder container
FROM bellsoft/liberica-openjre-debian:21-cds AS builder
WORKDIR /builder
# Copy the jar file to the working directory and rename it to application.jar
COPY --from=compiler /app/target/ExplorateurIUT.jar ./application.jar
# Extract the jar file using an efficient layout
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# Runtime container
FROM bellsoft/liberica-openjre-debian:21-cds
WORKDIR /application
# Copy the extracted jar contents from the builder container into the working directory in the runtime container
# Every copy step creates a new docker layer
# This allows docker to only pull the changes it really needs
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
# Start the application jar - this is not the uber jar used by the builder
# This jar only contains application code and references to the extracted jar files
# This layout is efficient to start up and CDS friendly
ENTRYPOINT ["java", "-jar", "application.jar"]
