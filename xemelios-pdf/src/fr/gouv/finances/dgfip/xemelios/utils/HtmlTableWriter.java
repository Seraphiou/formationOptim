/*
 * Copyright 
 *   2008 axYus - www.axyus.com
 *   2008 L. Meckert - laurent.meckert@axyus.com
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

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.Writer;


import java.io.BufferedReader;

import fr.gouv.finances.dgfip.xemelios.common.Constants;
import org.apache.log4j.Logger;

/** HTML TABLE writer 
 * 
 *  (le but est d'alimenter un fichier Excel)
 *  
 *
 *
 */
public class HtmlTableWriter {

    private static final Logger logger = Logger.getLogger(HtmlTableWriter.class);
    private Writer rawWriter;
    private PrintWriter pw;
    private static String modele;  // cette chaine est lue dans un fichier de ressources
    public static final Integer TEXT_CELLFORMAT = 0;
    public static final Integer INTEGER_CELLFORMAT = 1;
    public static final Integer FLOAT_CELLFORMAT = 2;
    public static final Integer DATE_CELLFORMAT = 3;

    private String fileName;
    private PropertiesExpansion applicationProperties;
    /**
     * Constructs HtmlTableWriter
     *
     * @param writer
     *            the writer to an underlying HTML source.

     */
    public HtmlTableWriter(Writer writer,String fileName, PropertiesExpansion applicationProperties) {
        this.rawWriter = writer;
        this.pw = new PrintWriter(writer);
        this.fileName = fileName;
        this.applicationProperties = applicationProperties;

logger.debug("HtmlTableWriter<"+fileName+">.open()");



        StringBuffer sb = new StringBuffer();
        if (modele == null) {
            // Lire le fichier de ressources (modele contenant des styles Microsoft)
            String dirRessources = this.applicationProperties.getProperty(Constants.SYS_PROP_RESOURCES_LOCATION, "C:\\");
            String filePath = dirRessources + "/modeleExcelHtml.xls";

            try {
                StringBuffer fileData = new StringBuffer(1000);
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                char[] buf = new char[1024];
                int numRead = 0;
                while ((numRead = reader.read(buf)) != -1) {
                    fileData.append(buf, 0, numRead);
                }
                reader.close();
                modele = fileData.toString();
            } catch (java.io.IOException e) {
                logger.error(e);
            }
        }


        if (modele != null) {
            sb.append(modele);
        }
        sb.append("<BODY>");
        sb.append("<TABLE  border=\"1\">");
        pw.write(sb.toString());
    }

    /**
     * Prepare une entree de type <TD class="axyusXXX">donnees</TD>
     *  en focntion du type de donnees
     */
    public static String buildCell(String data, Integer cellFormat, Integer level) {
        StringBuffer sb = new StringBuffer();

        if (cellFormat == TEXT_CELLFORMAT && level > 2) {
            sb.append("<TD class=\"axyusText\">");
        } else if (cellFormat == INTEGER_CELLFORMAT) {
            sb.append("<TD class=\"axyusInteger\">");
        } else if (cellFormat == FLOAT_CELLFORMAT) {
            sb.append("<TD class=\"axyusFloat\">");
        } else if (cellFormat == DATE_CELLFORMAT) {
            sb.append("<TD class=\"axyusDate\">");
        } else {
            if (level == 1) {
                sb.append("<TD class=\"axyusTitre\">");
            } else if (level == 2) {
                sb.append("<TD class=\"axyusNomsChamps\">");
            } else {
                sb.append("<TD>");
            }
        }

        if (data != null) {
            sb.append(data);
        }
        sb.append("</TD>");
        return sb.toString();

    }

    /**
     * Writes the next line to the file.
     *
     * @param nextLine
     *            a string array
     *
     */
    public void writeNext(String[] nextLine, Integer maxInd) {
//logger.debug("HtmlTableWriter<"+fileName+">.writeNext()");

        if (nextLine == null) {
            return;
        }

        StringBuffer sb = new StringBuffer();

        sb.append("<TR>");
        for (int i = 0; i <= maxInd; i++) {
            String nextElement = nextLine[i];
            if (nextElement != null) {
                sb.append(nextElement);
            } else {
                sb.append("<TD></TD>");
            }
        }

        sb.append("</TR>\n");
        pw.write(sb.toString());

    }

    /**
     * Flush underlying stream to writer.
     *
     * @throws IOException if bad things happen
     */
    public void flush() throws IOException {

        pw.flush();

    }

    /**
     * Close the underlying stream writer flushing any buffered content.
     *
     * @throws IOException if bad things happen
     *
     */
    public void close() throws IOException {
logger.debug("HtmlTableWriter<"+fileName+">.close()");

        StringBuffer sb = new StringBuffer();

        sb.append("</TABLE>");
        sb.append("</BODY>");
        sb.append("</HTML>");
        pw.write(sb.toString());


        pw.flush();
        pw.close();
        rawWriter.close();
    }
}
