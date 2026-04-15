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

import explorateurIUT.model.AppText;
import explorateurIUT.model.BUT;
import explorateurIUT.model.Departement;
import explorateurIUT.model.IUT;
import explorateurIUT.model.ParcoursBUT;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DuplicateKeyException;

/**
 *
 * @author Rémi Venant
 */
public class BUTIUTModel {

    private static final Log LOG = LogFactory.getLog(BUTIUTModel.class);

    private final BUTIUTModelManager manager;
    private final Validator validator;
    private HashMap<String, BUT> butsById;
    private HashMap<String, ParcoursBUT> parcoursById;
    private HashMap<String, IUT> iutsById;
    private HashMap<String, Departement> departementsById;
    private HashMap<String, AppText> appTextsById;
    private boolean readOnly;

    public BUTIUTModel(BUTIUTModelManager manager, Validator validator) {
        this.manager = manager;
        this.validator = validator;
        this.readOnly = false;
        this.butsById = new HashMap<>();
        this.parcoursById = new HashMap<>();
        this.iutsById = new HashMap<>();
        this.departementsById = new HashMap<>();
        this.appTextsById = new HashMap<>();
    }

    public Map<String, BUT> getButsById() {
        return Collections.unmodifiableMap(butsById);
    }

    public Map<String, ParcoursBUT> getParcoursById() {
        return Collections.unmodifiableMap(parcoursById);
    }

    public Map<String, IUT> getIutsById() {
        return Collections.unmodifiableMap(iutsById);
    }

    public Map<String, Departement> getDepartementsById() {
        return Collections.unmodifiableMap(departementsById);
    }

    public Map<String, AppText> getAppTextsById() {
        return Collections.unmodifiableMap(appTextsById);
    }

    public void commit() {
        this.readOnly = true;
        this.manager.replaceActiveModel(this);
    }

    public void rollback() {
        if (this.readOnly) {
            throw new IllegalStateException("Cannot rollback on read only mode");
        }
        this.butsById = new HashMap<>();
        this.parcoursById = new HashMap<>();
        this.iutsById = new HashMap<>();
        this.departementsById = new HashMap<>();
        this.appTextsById = new HashMap<>();
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public BUT saveBUT(BUT but) {
        this.checkIfWritable();
        this.validateEntity(but);
        if (this.butsById.putIfAbsent(but.getId(), but) != null) {
            throw new DuplicateKeyException("BUT already known in model: " + but.getCode() + " (id: " + but.getId() + ")");
        }
        return but;
    }

    public ParcoursBUT saveParcours(ParcoursBUT parcours) {
        this.checkIfWritable();
        this.validateEntity(parcours);
        if (this.parcoursById.putIfAbsent(parcours.getId().toLowerCase(), parcours) != null) {
            throw new DuplicateKeyException("Parcours already known in model: " + parcours.getCode() + " (id: " + parcours.getId() + ")");
        }
        return parcours;
    }

    public IUT saveIUT(IUT iut) {
        this.checkIfWritable();
        this.validateEntity(iut);
        if (this.iutsById.putIfAbsent(iut.getId().toLowerCase(), iut) != null) {
            throw new DuplicateKeyException("IUT already known in model: " + iut.getNom() + " (id: " + iut.getId() + ")");
        }
        return iut;
    }

    public Departement saveDepartement(Departement departement) {
        this.checkIfWritable();
        this.validateEntity(departement);
        if (this.departementsById.putIfAbsent(departement.getId().toLowerCase(), departement) != null) {
            throw new DuplicateKeyException("Departement already known in model: " + departement.getCode() + " (id: " + departement.getId() + ")");
        }
        return departement;
    }

    public AppText saveAppText(AppText appText) {
        this.checkIfWritable();
        this.validateEntity(appText);
        if (this.appTextsById.putIfAbsent(appText.getId().toLowerCase(), appText) != null) {
            throw new DuplicateKeyException("AppText already known in model: " + appText.getCode() + " (id: " + appText.getId() + ")");
        }
        return appText;
    }

    private void checkIfWritable() {
        if (this.readOnly) {
            throw new IllegalStateException("Cannot save an entity in a read-only model");
        }
    }

    private <T> void validateEntity(T entity) throws ValidationException {
        Set<ConstraintViolation<T>> violations = this.validator.validate(entity);
        if (violations != null && !violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
