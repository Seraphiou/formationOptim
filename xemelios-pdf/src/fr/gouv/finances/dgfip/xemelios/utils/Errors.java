/*
 * Copyright
 *   2009 axYus - www.axyus.com
 *   2009 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS.
 *
 * XEMELIOS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package fr.gouv.finances.dgfip.xemelios.utils;

import java.util.ArrayList;
import java.util.List;

public class Errors {

    public static final int SEVERITY_NONE = 0;
    public static final int SEVERITY_INFO = 1;
    public static final int SEVERITY_WARNING = 2;
    public static final int SEVERITY_ERROR = 3;
    private List<Error> errors;
    private int maxSeverity = SEVERITY_NONE;

    public Errors() {
        super();
        errors = new ArrayList<Error>();
    }

    public void addError(int severity, String message) {
        if (severity > maxSeverity) {
            maxSeverity = severity;
        }
        errors.add(new Error(severity, message));
    }
    public void addError(Error error) {
        if (error.severity > maxSeverity) {
            maxSeverity = error.severity;
        }
        errors.add(error);
    }

    public boolean containsError() {
        return maxSeverity == SEVERITY_ERROR;
    }

    public boolean containsWarning() {
        return maxSeverity >= SEVERITY_WARNING;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public static class Error {

        private int severity = Errors.SEVERITY_NONE;
        private String message;

        public Error(int severity, String message) {
            this.severity = severity;
            this.message = message;
        }

        public String getSeverityMessage() {
            switch (severity) {
                case Errors.SEVERITY_NONE:
                    return "";
                case Errors.SEVERITY_INFO:
                    return "Information";
                case Errors.SEVERITY_WARNING:
                    return "Avertissement";
                case Errors.SEVERITY_ERROR:
                    return "Erreur";
            }
            return null;
        }

        public int getSeverity() {
            return severity;
        }

        public String getMessage() {
            return message;
        }
    }
}
