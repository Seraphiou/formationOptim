<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
    xmlns:added="http://projets.admisource.gouv.fr/xemelios/namspaces#added"
    xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namspaces#anomally"
    exclude-result-prefixes="xsl"
    version="2.0">
    
    <xsl:output encoding="##output-encoding##" standalone="yes" method="xml" indent="yes"/>

    <xsl:template match="/*[position()=1]">
        <xsl:if test="local-name(.)='DocumentPaye'">
            <DocumentPaye
                xmlns="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
                xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
                xmlns:added="http://projets.admisource.gouv.fr/xemelios/namspaces#added"
                xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namspaces#anomally">
                <xsl:for-each select="*">
                    <xsl:call-template name="writeElement"><xsl:with-param name="el" select="."/></xsl:call-template>
                </xsl:for-each>
            </DocumentPaye>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="writeElement">
        <xsl:param name="el"/>
        <xsl:element name="{local-name($el)}" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
            <xsl:for-each select="$el/@*">
                <xsl:attribute name="{local-name(.)}"><xsl:value-of select="."/></xsl:attribute>
            </xsl:for-each>
            <xsl:for-each select="$el/*">
                <xsl:call-template name="writeElement"><xsl:with-param name="el" select="."/></xsl:call-template>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>