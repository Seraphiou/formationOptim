<?xml version="1.1" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&#160;">
]>

<xsl:stylesheet
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:java="xalan://xml.apache.org/xalan/java"
 xmlns="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
 xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
 xmlns:added="http://projets.admisource.gouv.fr/xemelios/namespaces#added"
 xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namespaces#anomally"
 exclude-result-prefixes="java"
 version="2.0"
>
 
 <xsl:output
 standalone="yes"
 method="xml"
 indent="yes"/>
 
 <xsl:output encoding="ISO-8859-1"/>
<xsl:output version="1.0"/>
 
<xsl:param name="language" select="'fr'"/>

 <xsl:template match="/*[position()=1]">
  <xsl:choose>
   <xsl:when test="local-name(.) = 'DocumentPaye'">
  <DocumentPaye
   xmlns="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
   xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
   xmlns:added="http://projets.admisource.gouv.fr/xemelios/namspaces#added"
   xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namspaces#anomally">
   <xsl:variable name="annee_glb" select="Annee/@V"/>
 <xsl:variable name="mois_glb" select="Mois/@V"/>
<xsl:for-each select="./*">
 <xsl:call-template name="writeElement">
  <xsl:with-param name="el" select="."/>
  <xsl:with-param name="annee" select="$annee_glb"/>
  <xsl:with-param name="mois" select="$mois_glb"/>
 </xsl:call-template>
</xsl:for-each>
</DocumentPaye>
   </xsl:when>
  </xsl:choose>
</xsl:template>
 
 <!--xsl:template match="/n:DocumentPaye">
  <DocumentPaye
   xmlns="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
   xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
   xmlns:added="http://projets.admisource.gouv.fr/xemelios/namspaces#added"
   xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namspaces#anomally">
   <xsl:variable name="annee_glb" select="Annee/@V"/>
   <xsl:variable name="mois_glb" select="Mois/@V"/>
   <xsl:for-each select="./*">
    <xsl:call-template name="writeElement">
     <xsl:with-param name="el" select="."/>
     <xsl:with-param name="annee" select="$annee_glb"/>
     <xsl:with-param name="mois" select="$mois_glb"/>
    </xsl:call-template>
   </xsl:for-each>
  </DocumentPaye>
 </xsl:template-->
 
<xsl:template name="writeElement">
<xsl:param name="el"/>
<xsl:param name="annee"/>
<xsl:param name="mois"/>
 <xsl:element name="{name($el)}" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
 <xsl:for-each select="$el/@*">
  <xsl:attribute name="{name(.)}"><xsl:value-of select="."/></xsl:attribute>
 </xsl:for-each>
 <xsl:choose>
  <xsl:when test="contains('|ligne',concat('|',name($el)))"><xsl:element name="Annee" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"><xsl:attribute name="V"><xsl:value-of select="../../n:Annee/@V"/></xsl:attribute></xsl:element><xsl:element name="Mois" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"><xsl:attribute name="V"><xsl:value-of select="../../n:Mois/@V"/></xsl:attribute></xsl:element></xsl:when>
 </xsl:choose>
 <xsl:choose>
  <xsl:when test="contains('|Etablissement|TypeCredit|Code|Libelle',concat('|',name($el)))">
    <xsl:attribute name="V2">
     <xsl:value-of select="upper-case(./@V)"/>
    </xsl:attribute>
  </xsl:when>
 </xsl:choose>
 <xsl:choose>
  <xsl:when test="contains('|Rubrique|Caisse',concat('|',name($el)))"><xsl:attribute name="display" namespace="http://projets.admisource.gouv.fr/xemelios/namespaces#added"><xsl:value-of select="./n:Libelle/@V"/> (<xsl:value-of select="./n:Code/@V"/>)</xsl:attribute></xsl:when>
 </xsl:choose>
 <xsl:for-each select="$el/*">
  <xsl:call-template name="writeElement">
   <xsl:with-param name="el" select="."/>
   <xsl:with-param name="annee" select="$annee"/>
   <xsl:with-param name="mois" select="$mois"/>
  </xsl:call-template>
 </xsl:for-each><xsl:value-of select="$el/text()"/></xsl:element></xsl:template>

</xsl:stylesheet>
