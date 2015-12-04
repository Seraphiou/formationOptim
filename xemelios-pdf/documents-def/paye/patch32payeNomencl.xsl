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
            <xsl:if test="local-name(.) = 'Nomenclatures'">
                <xsl:if test="count(./*[local-name()='NomenclatureStatut']) = 0">
                    <xsl:element name="NomenclatureStatut" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                        <xsl:element name="Description" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                            <xsl:attribute name="V">Nomenclature des statuts des agents</xsl:attribute>
                        </xsl:element>
                        <xsl:element name="Correspondance" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                            <xsl:element name="Libelle" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">Titulaire</xsl:attribute>
                            </xsl:element>
                            <xsl:element name="Code" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">TITULAIRE</xsl:attribute>
                            </xsl:element>
                        </xsl:element>
                        <xsl:element name="Correspondance" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                            <xsl:element name="Libelle" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">Non Titulaire</xsl:attribute>
                            </xsl:element>
                            <xsl:element name="Code" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">NON_TITULAIRE</xsl:attribute>
                            </xsl:element>
                        </xsl:element>
                        <xsl:element name="Correspondance" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                            <xsl:element name="Libelle" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">Stagiaire</xsl:attribute>
                            </xsl:element>
                            <xsl:element name="Code" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">STAGIAIRE</xsl:attribute>
                            </xsl:element>
                        </xsl:element>
                        <xsl:element name="Correspondance" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                            <xsl:element name="Libelle" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">Emploi Fonctionnel</xsl:attribute>
                            </xsl:element>
                            <xsl:element name="Code" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">EMPLOI_FONC</xsl:attribute>
                            </xsl:element>
                        </xsl:element>
                        <xsl:element name="Correspondance" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                            <xsl:element name="Libelle" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">Emploi aidé</xsl:attribute>
                            </xsl:element>
                            <xsl:element name="Code" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">EMPLOI_AIDE</xsl:attribute>
                            </xsl:element>
                        </xsl:element>
                        <xsl:element name="Correspondance" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                            <xsl:element name="Libelle" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">Autre statut</xsl:attribute>
                            </xsl:element>
                            <xsl:element name="Code" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
                                <xsl:attribute name="V">AUTRE_STATUT</xsl:attribute>
                            </xsl:element>
                        </xsl:element>
                        
                    </xsl:element>
                </xsl:if>
            </xsl:if>
            <xsl:for-each select="$el/@*">
                <xsl:attribute name="{local-name(.)}"><xsl:value-of select="."/></xsl:attribute>
            </xsl:for-each>
            <xsl:for-each select="$el/*">
                <xsl:call-template name="writeElement"><xsl:with-param name="el" select="."/></xsl:call-template>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>