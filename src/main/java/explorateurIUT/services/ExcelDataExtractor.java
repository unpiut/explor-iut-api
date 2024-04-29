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
package explorateurIUT.services;

import java.io.IOException;
import java.io.InputStream;
import explorateurIUT.excelImport.consumers.AppTextConsumer;
import explorateurIUT.excelImport.consumers.BUTConsumer;
import explorateurIUT.excelImport.consumers.IUTConsumer;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Julien Fourdan
 */
public interface ExcelDataExtractor {

    public void extractFromAppProperties(@NotNull AppTextConsumer appTextConsumer, @NotNull IUTConsumer iutConsumer, @NotNull BUTConsumer butConsumer) throws IOException;

    public void extractFromInputStream(@NotNull AppTextConsumer appTextConsumer, @NotNull IUTConsumer iutConsumer, @NotNull BUTConsumer butConsumer, @NotNull InputStream inputStream) throws IOException;
}
