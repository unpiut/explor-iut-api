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
package explorateurIUT.services.butIUTModelMgmt;

import java.io.IOException;
import java.io.InputStream;
import explorateurIUT.services.butIUTModelMgmt.excelImport.model.ExcelAppText;
import explorateurIUT.services.butIUTModelMgmt.excelImport.model.ExcelBUT;
import explorateurIUT.services.butIUTModelMgmt.excelImport.model.ExcelIUT;
import jakarta.validation.constraints.NotNull;
import java.util.function.Consumer;

/**
 *
 * @author Rémi Venant
 */
public interface ExcelDataFileLoader {

    public void extractFromInputStream(@NotNull Consumer<ExcelAppText> appTextConsumer,
            @NotNull Consumer<ExcelIUT> iutConsumer,
            @NotNull Consumer<ExcelBUT> butConsumer,
            @NotNull Consumer<ExcelAppText> mailTextConsumer,
            @NotNull InputStream inputStream) throws IOException;
}
