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
package explorateurIUT.controllers;

import explorateurIUT.services.MailManagementService;
import explorateurIUT.services.RequestServerUrlBuilder;
import explorateurIUT.services.mailManagement.MailRequestAttachement;
import explorateurIUT.services.mailManagement.MailSendingRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Remi Venant
 */
@Controller
@RequestMapping("/api/v1/mail")
public class MailController {

    private static final Log LOG = LogFactory.getLog(MailController.class);

    private final RequestServerUrlBuilder serverUrlBuilder;

    private final MailManagementService mailMgmtSvc;

    @Autowired
    public MailController(RequestServerUrlBuilder serverUrlBuilder, MailManagementService mailMgmtSvc) {
        this.serverUrlBuilder = serverUrlBuilder;
        this.mailMgmtSvc = mailMgmtSvc;
    }

    @PostMapping("/request")
    public @ResponseBody
    ResponseEntity<?> sendMailRequest(
            @RequestParam("deptIds") List<String> deptIds,
            @RequestParam("contactIdentity") String identity,
            @RequestParam("contactCompany") String company,
            @RequestParam("contactFunction") String function,
            @RequestParam("contactMail") String mail,
            @RequestParam("mailSubject") String subject,
            @RequestParam("mailBody") String body,
            @RequestParam(name = "files", required = false) MultipartFile[] files,
            HttpServletRequest request) throws IOException {

        ArrayList<MailRequestAttachement> fileList = null;
        if (files != null) {
            fileList = new ArrayList<>(files.length);
            for (int i = 0; i < files.length; i++) {
                final MultipartFile file = files[i];
                final String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : String.format("%d_%s", i + 1, file.getName());
                fileList.add(new MailRequestAttachement(file, fileName));
            }
        }

        final MailSendingRequest msr = new MailSendingRequest(deptIds, identity,
                company, function, mail, subject, body, fileList);

        LocalDateTime requestCreationDT = this.mailMgmtSvc.requestMailSending(msr, this.serverUrlBuilder.buildServerBaseURI());

        return ResponseEntity.ok(Map.of("creationDateTime", requestCreationDT));
    }

    @PostMapping("/resend-confirmation")
    public @ResponseBody
    ResponseEntity<?> sendMailRequest(@RequestParam("c") String contact, @RequestParam("cdt") String rawCreationDateTime) {
        try {
            LocalDateTime creationDateTime = LocalDateTime.parse(rawCreationDateTime);
            this.mailMgmtSvc.resendConfirmationMail(creationDateTime, contact, this.serverUrlBuilder.buildServerBaseURI());
            return ResponseEntity.accepted().build();
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Bad creation datetime");
        }

    }

    @GetMapping("/validate")
    public @ResponseBody
    ResponseEntity<?> validate(@RequestParam("t") String token) throws MessagingException {
        this.mailMgmtSvc.confirmMailSendingRequest(token);
        return ResponseEntity.accepted().build();
    }

}
