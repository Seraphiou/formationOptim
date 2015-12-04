<?xml version="1.0" encoding="UTF-8"?>
<!--
 Copyright
   2009 axYus - www.axyus.com
   2009 C.Marchand - christophe.marchand@axyus.com

 This file is part of XEMELIOS.

 XEMELIOS is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 XEMELIOS is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with XEMELIOS; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs" version="2.0">
    <xsl:output method="xml" encoding="ISO-8859-1" indent="yes"/>
    <xsl:param name="volume">0</xsl:param>

    <xsl:template match="manifeste">
        <xsl:element name="manifeste">
            <xsl:for-each select="@*"><xsl:copy-of select="."/></xsl:for-each>
            <xsl:element name="volumes">
                <xsl:element name="volume">
                    <xsl:for-each select="volumes/volume[@num=$volume]/@*"><xsl:copy-of select="."/></xsl:for-each>
                    <xsl:copy-of select="//document[@volume=$volume]"/>
                    <xsl:copy-of select="action[@volume=$volume or not(exists(@volume))]"/>
                    <xsl:copy-of select="on-delete[@volume=$volume or not(exists(@volume))]"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>