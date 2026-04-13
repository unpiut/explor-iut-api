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
package explorateurIUT.services.butIUTModelMgmt;

import jakarta.validation.Validator;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Rémi Venant
 */
public class BUTIUTModelManagerImpl implements BUTIUTModelManager {

    private final Validator validator;

    private final AtomicReference<BUTIUTModel> activeModel;

    @Autowired
    public BUTIUTModelManagerImpl(Validator validator) {
        this.validator = validator;
        this.activeModel = new AtomicReference<>();
    }

    @Override
    public boolean isReady() {
        return this.activeModel.get() != null;
    }

    @Override
    public BUTIUTModel getActiveModel() {
        return this.activeModel.get();
    }

    @Override
    public BUTIUTModel startNewModelCreation() {
        return new BUTIUTModel(this, this.validator);
    }

    @Override
    public BUTIUTModel replaceActiveModel(BUTIUTModel newModel) {
        if (!newModel.isReadOnly()) {
            throw new IllegalArgumentException("Active model must be read-only.");
        }
        this.activeModel.set(newModel);
        return newModel;
    }

}
