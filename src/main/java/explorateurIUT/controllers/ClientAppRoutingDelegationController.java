/*
 * Copyright (C) 2024 A4LL Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package explorateurIUT.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Rémi Venant
 */
@Controller
public class ClientAppRoutingDelegationController {

    @RequestMapping(value = {
        "/",
        "/formation",
        "/map",
        "/result",
        "/mail",
        "/modifyMail",
        "/mailSend",
        "/validate",
        "/mentions",
        "/excelAccess"})
    public String index() {
        return "/index.html";
    }

}
