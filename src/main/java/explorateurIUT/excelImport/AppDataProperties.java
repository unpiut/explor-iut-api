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
package explorateurIUT.excelImport;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Remi Venant
 */
@Configuration
@ConfigurationProperties(prefix = "app.data")
public class AppDataProperties {

    private String dataDir = "./";
    private String dataFilePrefix = "data";
    private String butSheetName = "but";
    private String iutSheetName = "iut";
    private String appTextSheetName = "textes";

    public AppDataProperties() {
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getDataFilePrefix() {
        return dataFilePrefix;
    }

    public void setDataFilePrefix(String dataFilePrefix) {
        this.dataFilePrefix = dataFilePrefix;
    }

    public String getButSheetName() {
        return butSheetName;
    }

    public void setButSheetName(String butSheetName) {
        this.butSheetName = butSheetName;
    }

    public String getIutSheetName() {
        return iutSheetName;
    }

    public void setIutSheetName(String iutSheetName) {
        this.iutSheetName = iutSheetName;
    }

    public String getAppTextSheetName() {
        return appTextSheetName;
    }

    public void setAppTextSheetName(String appTextSheetName) {
        this.appTextSheetName = appTextSheetName;
    }

}
