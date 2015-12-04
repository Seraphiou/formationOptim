<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&#160;">
]>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1" version="2.0">
 <xsl:output method="xml" indent="yes"/>
 <xsl:output encoding="ISO-8859-1"/>
 <xsl:output version="1.0"/>

 <xsl:param name="language" select="'fr'"/>


 <xsl:template match="/*[position()=1]">
  <xsl:choose>
   <xsl:when test="local-name(.) = 'DocumentPaye'">
    <n:DocumentPaye xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1"
     xmlns:added="http://projets.admisource.gouv.fr/xemelios/namspaces#added"
     xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namspaces#anomally">
     <xsl:comment>on est ici !</xsl:comment>
     <xsl:apply-templates/>
    </n:DocumentPaye>    
   </xsl:when>
   <xsl:otherwise>
    <xsl:comment>local-name()=<xsl:value-of select="local-name(.)"/></xsl:comment>
    <xsl:copy-of select="."/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template match="*">
  <xsl:element name="{local-name(.)}"
   namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
   <xsl:choose>
    <xsl:when test="local-name(.) = 'Libelle'">
     <xsl:attribute name="V">
      <xsl:value-of select="concat(./@V,' (',../*[local-name()='Code']/@V,')')"/>
     </xsl:attribute>
    </xsl:when>
    <xsl:otherwise>
     <xsl:for-each select="./@*">
      <xsl:attribute name="{local-name(.)}">
       <xsl:value-of select="."/>
      </xsl:attribute>
     </xsl:for-each>
    </xsl:otherwise>
   </xsl:choose>
   <xsl:apply-templates/>
   <xsl:if test="local-name(.) = 'Nomenclatures'">
    <xsl:if test="count(./*[local-name()='NomenclatureStatut']) = 0">
     <n:NomenclatureStatut>
      <n:Description V="Nomenclature des statuts des agents"/>
      <n:Correspondance>
       <n:Libelle V="Titulaire"/>
       <n:Code V="TITULAIRE"/>
      </n:Correspondance>
      <n:Correspondance>
       <n:Libelle V="Non Titulaire"/>
       <n:Code V="NON_TITULAIRE"/>
      </n:Correspondance>
      <n:Correspondance>
       <n:Libelle V="Stagiaire"/>
       <n:Code V="STAGIAIRE"/>
      </n:Correspondance>
      <n:Correspondance>
       <n:Libelle V="Emploi Fonctionnel"/>
       <n:Code V="EMPLOI_FONC"/>
      </n:Correspondance>
      <n:Correspondance>
       <n:Libelle V="Emploi aidé"/>
       <n:Code V="EMPLOI_AIDE"/>
      </n:Correspondance>
      <n:Correspondance>
       <n:Libelle V="Autre statut"/>
       <n:Code V="AUTRE_STATUT"/>
      </n:Correspondance>
     </n:NomenclatureStatut>
     <!--xsl:element name="NomenclatureStatut" namespace="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1">
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
    </xsl:element-->
    </xsl:if>
   </xsl:if>
  </xsl:element>
 </xsl:template>



</xsl:transform>
