<?xml version="1.1" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&#160;">
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:java="xalan://xml.apache.org/xalan/java"
 xmlns="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
 xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
 xmlns:added="http://projets.admisource.gouv.fr/xemelios/namespaces#added"
 xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namespaces#anomally"
 xmlns:xs="http://www.w3.org/2001/XMLSchema"
 exclude-result-prefixes="java" version="2.0">

 <xsl:output standalone="yes" method="xml" indent="yes"/>

 <xsl:output encoding="ISO-8859-1"/>
 <xsl:output version="1.0"/>
 
 <xsl:param name="language" select="'fr'"/>
 <xsl:param name="file.name" as="xs:string"/>

 <xsl:template match="/*[position()=1]">
  <xsl:choose>
   <xsl:when test="local-name(.) = 'DocumentPaye'">
    <DocumentPaye xmlns="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
     xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
     xmlns:added="http://projets.admisource.gouv.fr/xemelios/namespaces#added"
     xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namespaces#anomally">
     <xsl:element name="FileName" namespace="http://projets.admisource.gouv.fr/xemelios/namespaces#added">
      <xsl:attribute name="V"><xsl:value-of select="$file.name"/></xsl:attribute>
     </xsl:element>
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
   xmlns:added="http://projets.admisource.gouv.fr/xemelios/namespaces#added"
   xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namespaces#anomally">
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
    <xsl:attribute name="{name(.)}">
     <xsl:value-of select="."/>
    </xsl:attribute>
   </xsl:for-each>
   <xsl:choose>
    <xsl:when
     test="contains('|SupFam|TraitBrut|IndemResid|Indemnite|RemDivers|AvantageNature|Acompte|Cotisation|Rappel|Retenue|Deduction|RepartitionBudget|Evenement|PJRef|Commentaire|',concat('|',name($el),'|'))">
     <xsl:attribute name="generated-id">
      <xsl:value-of select="generate-id()"/>
     </xsl:attribute>
     <xsl:attribute name="tag-name">
      <xsl:value-of select="name($el)"/>
     </xsl:attribute>
    </xsl:when>
   </xsl:choose>
   <xsl:choose>
    <xsl:when test="contains('|PayeIndivMensuel|',concat('|',name($el),'|'))">
     <xsl:attribute name="primary-key"
      namespace="http://projets.admisource.gouv.fr/xemelios/namespaces#added">
      <xsl:value-of select="../../*[local-name()='Annee'][1]/@V"/>-<xsl:value-of
       select="../../*[local-name()='Mois'][1]/@V"/>-<xsl:value-of select="./*[local-name()='Agent']/*[local-name()='Matricule']/@V"
      />
     </xsl:attribute>
    </xsl:when>
   </xsl:choose>
   <!--xsl:choose>
    <xsl:when
     test="contains('|Nom|Prenom|EmploiMetier|Service|Matricule|CodeFonction|',concat('|',name($el),'|'))">
     <xsl:attribute name="V2">
      <xsl:value-of select="upper-case(./@V)"/>
     </xsl:attribute>
    </xsl:when>
   </xsl:choose-->
   <!--xsl:choose>
    <xsl:when test="contains('|CptBancaire',concat('|',name($el)))">
     <xsl:attribute name="rib">
      <xsl:value-of select="$el/*[local-name()='CodeEtab'][1]/@V"/>
      <xsl:value-of select="$el/*[local-name()='CodeGuic'][1]/@V"/>
      <xsl:value-of select="$el/*[local-name()='IdCpte'][1]/@V"/>
      <xsl:value-of select="$el/*[local-name()='CleRib'][1]/@V"/>
     </xsl:attribute>
    </xsl:when>
   </xsl:choose-->
   <xsl:choose>
    <xsl:when test="name($el)='RefNomenStatutaire'">
     <xsl:attribute name="desc">
      <xsl:value-of select="$el/*[local-name()='Libelle'][1]/@V"/>
      <xsl:text> (</xsl:text>
      <xsl:value-of select="$el/*[local-name()='Code'][1]/@V"/>
      <xsl:text>)</xsl:text>
     </xsl:attribute>
    </xsl:when>
   </xsl:choose>
   <xsl:choose>
    <xsl:when test="contains('|PayeIndivMensuel|Repartition',concat('|',name($el)))">
     <xsl:element name="Annee" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
      <xsl:attribute name="V">
       <xsl:value-of select="../../*[local-name()='Annee'][1]/@V"/>
      </xsl:attribute>
     </xsl:element>
     <xsl:element name="Mois" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
      <xsl:attribute name="V">
       <xsl:value-of select="../../*[local-name()='Mois'][1]/@V"/>
      </xsl:attribute>
     </xsl:element>
     <xsl:element name="AnneeMois" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
      <xsl:attribute name="V">
       <xsl:value-of select="$annee"/>
       <xsl:if test="string-length($mois)=1">0</xsl:if>
       <xsl:value-of select="$mois"/>
      </xsl:attribute>
     </xsl:element>
     <!--xsl:element name="AnneeMois" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
      <xsl:attribute name="V">
       <xsl:value-of select="$annee"/>
       <xsl:if test="string-length($mois)=1">0</xsl:if>
       <xsl:value-of select="$mois"/>
      </xsl:attribute>
     </xsl:element-->
    </xsl:when>
   </xsl:choose>
   <xsl:choose>
    <xsl:when
     test="contains('|Nom|Prenom|EmploiMetier|Service|Matricule|CodeFonction|',concat('|',name($el),'|'))">
     <xsl:attribute name="V2">
      <xsl:value-of select="upper-case(./@V)"/>
     </xsl:attribute>
    </xsl:when>
    <xsl:when test="contains('|Code|',concat('|',name($el),'|'))">
     <xsl:attribute name="V2">
      <xsl:value-of select="normalize-space(upper-case(./@V))"/>
     </xsl:attribute>
    </xsl:when>
   </xsl:choose>
   <xsl:choose>
    <xsl:when test="contains('|CptBancaire|',concat('|',name($el),'|'))">
     <xsl:attribute name="rib">
      <xsl:choose>
       <xsl:when test="exists($el/*[local-name()='BIC'])">
        <xsl:value-of select="$el/*[local-name()='BIC'][1]/@V"/>
        <xsl:value-of select="$el/*[local-name()='IBAN'][1]/@V"/>
       </xsl:when>
       <xsl:otherwise>
        <xsl:value-of select="$el/*[local-name()='CodeEtab'][1]/@V"/>
        <xsl:value-of select="$el/*[local-name()='CodeGuic'][1]/@V"/>
        <xsl:value-of select="$el/*[local-name()='IdCpte'][1]/@V"/>
        <xsl:value-of select="$el/*[local-name()='CleRib'][1]/@V"/>
       </xsl:otherwise>
      </xsl:choose>
     </xsl:attribute>
    </xsl:when>
   </xsl:choose>
   <xsl:choose>
    <xsl:when test="name($el)='RefNomenStatutaire'">
     <xsl:attribute name="desc">
      <xsl:value-of select="$el/*[local-name()='Libelle'][1]/@V"/>
      <xsl:text> (</xsl:text>
      <xsl:value-of select="$el/*[local-name()='Code'][1]/@V"/>
      <xsl:text>)</xsl:text>
     </xsl:attribute>
    </xsl:when>
   </xsl:choose>
   <xsl:if test="name($el)='PJRef'">
    <xsl:if test="not($el/*[local-name() eq 'NomPJ'])">
     <xsl:element name="NomPJ" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
      <xsl:attribute name="V" select="$el/*[local-name() eq 'IdUnique']/@V"/>
     </xsl:element>
    </xsl:if>
   </xsl:if>
   <xsl:for-each select="$el/*">
    <xsl:call-template name="writeElement">
     <xsl:with-param name="el" select="."/>
     <xsl:with-param name="annee" select="$annee"/>
     <xsl:with-param name="mois" select="$mois"/>
    </xsl:call-template>
   </xsl:for-each>
   <xsl:value-of select="$el/text()"/>
  </xsl:element>
 </xsl:template>


</xsl:stylesheet>
