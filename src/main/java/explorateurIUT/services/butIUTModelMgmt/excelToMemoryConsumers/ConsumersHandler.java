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
package explorateurIUT.services.butIUTModelMgmt.excelToMemoryConsumers;

import explorateurIUT.services.butIUTModelMgmt.BUTIUTModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Rémi Venant
 */
public class ConsumersHandler {

    private static final Log LOG = LogFactory.getLog(ConsumersHandler.class);

    private final BUTIUTModel model;

    private BUTConsumer butConsumer;
    private IUTConsumer iutconsumer;
    private AppTextConsumer appTextconsumer;
    private MailTextConsumer mailTextConsumer;

    public ConsumersHandler(BUTIUTModel model) {
        this.model = model;
        this.butConsumer = new BUTConsumer(model);
        this.iutconsumer = new IUTConsumer(model, this.butConsumer.getParcoursButByCodeButParc());
        this.appTextconsumer = new AppTextConsumer(model);
        this.mailTextConsumer = new MailTextConsumer(model);
    }

    public void reset() {
        this.butConsumer = new BUTConsumer(model);
        this.iutconsumer = new IUTConsumer(model, this.butConsumer.getParcoursButByCodeButParc());
        this.appTextconsumer = new AppTextConsumer(model);
        this.mailTextConsumer = new MailTextConsumer(model);
    }

    public BUTConsumer getExcelBUTConsumer() {
        return this.butConsumer;
    }

    public IUTConsumer getExcelIUTConsumer() {
        return this.iutconsumer;
    }

    public AppTextConsumer getExcelAppTextConsumer() {
        return this.appTextconsumer;
    }

    public MailTextConsumer getMailTextConsumer() {
        return mailTextConsumer;
    }
}
