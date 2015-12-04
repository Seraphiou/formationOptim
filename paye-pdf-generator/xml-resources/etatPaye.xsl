<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&#160;">
]>
<xsl:transform 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1" 
 xmlns:added="http://projets.admisource.gouv.fr/xemelios/namespaces#added" 
 xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namespaces#anomally" 
 xmlns:data="data.uri"
 xmlns="http://www.w3.org/1999/xhtml"
 exclude-result-prefixes="#all"
 version="2.0">
 <xsl:character-map name="accents">
  <xsl:output-character character="à" string="&amp;#224;"/>
  <xsl:output-character character="é" string="&amp;#233;"/>
  <xsl:output-character character="è" string="&amp;#232;"/>
  <xsl:output-character character="ê" string="&amp;#234;"/>
  <xsl:output-character character="ë" string="&amp;#235;"/>
  <xsl:output-character character="î" string="&amp;#238;"/>
  <xsl:output-character character="ï" string="&amp;#239;"/>
  <xsl:output-character character="ô" string="&amp;#244;"/>
  <xsl:output-character character="ù" string="&amp;#249;"/>
 </xsl:character-map>

 <xsl:param name="encoding" select="iso-8859-1"/>
 <xsl:param name="IdLignePaye" select="concat('','')"/>
 <xsl:param name="repository" select="document(.)"/>
 <xsl:param name="elementId"/>
 <!-- Paye dans tous les cas -->
 <xsl:param name="anoId"/>
 <!-- id de l'anomalie a mettre en surbrillance -->

 <xsl:output method="xhtml" indent="yes" use-character-maps="accents" encoding="ISO-8859-1" exclude-result-prefixes="xsl n added ano data"/>


 <xsl:template match="*"> </xsl:template>

 <xsl:decimal-format name="decformat" decimal-separator="," grouping-separator=" " digit="#" pattern-separator=";" NaN="NaN" minus-sign="-"> </xsl:decimal-format>
 <xsl:variable name="tags-ano">
  <xsl:for-each select="//ano:Anomalie[@ano:anoId = $anoId]/ano:node">
   <xsl:element name="node">
    <xsl:attribute name="id" select="./@ano:id"/>
   </xsl:element>
  </xsl:for-each>
 </xsl:variable>
<xsl:variable name="data">
 <data xmlns="data.uri">
  <mois xml:lang="fr">
   <m>Janvier</m>
   <m>F&#233;vrier</m>
   <m>Mars</m>
   <m>Avril</m>
   <m>Mai</m>
   <m>Juin</m>
   <m>Juillet</m>
   <m>Ao&#251;t</m>
   <m>Septembre</m>
   <m>Octobre</m>
   <m>Novembre</m>
   <m>D&#233;cembre</m>
  </mois>
 </data>
</xsl:variable>

 <xsl:param name="language" select="'fr'"/>

 <xsl:template name="getLibelleStatut">
  <xsl:param name="codeStatut"/>
  <xsl:value-of select="$repository/n:DocumentPaye/n:Nomenclatures/n:NomenclatureStatut/n:Correspondance[n:Code/@V=$codeStatut]/n:Libelle/@V"/>
 </xsl:template>

 <xsl:template name="slash-date">
  <xsl:param name="datebrute"/>
  <!--date xmlns:data="data.uri" xsl:exclude-result-prefixes="data"-->
   <xsl:value-of select="substring($datebrute, 9, 2)"/>
   <xsl:text>/</xsl:text>
   <xsl:value-of select="substring($datebrute, 6, 2)"/>
   <xsl:text>/</xsl:text>
   <xsl:value-of select="substring($datebrute, 1, 4)"/>
  <!--/date-->
 </xsl:template>

 <xsl:template name="iso-date">
  <xsl:param name="datebrute"/>
  <!--date xmlns:data="data.uri" xsl:exclude-result-prefixes="data"-->
   <xsl:value-of select="substring($datebrute, 9, 2)"/>
   <xsl:text> </xsl:text>
   <xsl:variable name="mois" select="number(substring($datebrute, 6, 2))"/>
   <!-- dans la ligne suivante, le moteur de transformation remplace @@XSL_LOCATION@@ par le nom et l'emplacement de ce fichier -->
   <xsl:value-of select="$data/data:data/data:mois[lang($language)]/data:m[$mois]"/>
   <xsl:text> </xsl:text>
   <xsl:value-of select="substring($datebrute, 1, 4)"/>
  <!--/date-->
 </xsl:template>

 <xsl:template name="iso-mois">
  <xsl:param name="datebrute"/>
  <date xmlns:data="data.uri" xsl:exclude-result-prefixes="data">
   <xsl:variable name="mois" select="number($datebrute)"/>
   <xsl:value-of select="$data/data:data/data:mois[lang($language)]/data:m[$mois]"/>
  </date>
 </xsl:template>

 <xsl:template name="compteBancaire">
  <xsl:param name="compte"/>
  <span>
   <xsl:if test="count($tags-ano/node[@id = $compte/n:CodeEtab/@ano:node-id]) &gt; 0">
    <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
   </xsl:if>
   <xsl:if test="count($tags-ano/node[@id = $compte/n:CodeGuic/@ano:node-id]) &gt; 0">
    <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
   </xsl:if>
   <xsl:if test="count($tags-ano/node[@id = $compte/n:IdCpte/@ano:node-id]) &gt; 0">
    <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
   </xsl:if>
   <xsl:if test="count($tags-ano/node[@id = $compte/n:CleRib/@ano:node-id]) &gt; 0">
    <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
   </xsl:if>
   <xsl:choose>
    <xsl:when test="exists($compte/n:IdCpte)">
     <xsl:value-of select="$compte/n:CodeEtab/@V"/>&nbsp;<xsl:value-of select="$compte/n:CodeGuic/@V"/>&nbsp;<xsl:value-of select="$compte/n:IdCpte/@V"/>&nbsp;<xsl:value-of select="$compte/n:CleRib/@V"/>     
    </xsl:when>
    <xsl:otherwise>
     <xsl:value-of select="$compte/n:BIC/@V"/>&nbsp;<xsl:value-of select="$compte/n:IBAN/@V"/>
    </xsl:otherwise>
   </xsl:choose>&nbsp;<xsl:value-of select="$compte/n:TitCpte/@V"/>&nbsp;<xsl:value-of select="$compte/n:DteBanc/@V"/>&nbsp;
  </span>
 </xsl:template>

 <xsl:template match="/n:DocumentPaye">
  <html>
   <head>
    <style media="all">
     body { font-family: verdana, sans-serif; font-size: 10px; }
     h1 { font-size: 18px; font-weight: bold; }
     table thead th { text-align: center; font-size: 11px; }
     table tbody tr td { font-size: 10px; border: 0px }
     table.categorie { font-size: 10px; border: 1px solid rgb(0, 0, 0); width: 100%; }
     table.categorie thead th { text-align: center; font-size: 11px; border: 1px solid rgb(0, 0, 0); }
     table.categorie tbody tr td { font-size: 10px; border: 1px solid rgb(0, 0, 0); }
     table.detail thead th { background: #555555; color: #FFFFFF; border-style: none; font-size: 10px; }
     table.detail tbody tr td { font-size: 10px; }
     table.table4,table.table5 { border-style: none; }
     table.table4 tbody tr td,table.table5 tbody tr td { border-style: none; }
     table.reglement thead th,table.netcumul thead th,table.evenements thead th,table.repartitionbudget thead th { background: #555555; color: #FFFFFF; border-style: none; font-size: 12px; }
     table.evenements thead th { background: #555555; color: #FFFFFF; border-style: none; font-size: 12px; }
     table.reglement,table.netcumul { border-style: none; }
     table.reglement tbody tr td,table.netcumul tbody tr td,table.evenements tbody tr td,table.repartitionbudget tbody tr td { border-style: none; font-size: 12px; }
     table tbody tr td.numeric,table.detail tbody tr td.numeric { text-align: right; }
     table.detail tbody tr td.numeric { text-align: right; }
     td.cadreEmployeur { border: 1px solid rgb(0, 0, 0); padding: 5px; font-size: 12px; text-align: left; }
     /* td.cadreBulletin { padding: 5px; } */
     td.cadreEmploye2 { background-color: rgb(204, 204, 204); border: 1px solid rgb(0, 0, 0); padding: 5px; }
     span.periodeperiode { font-size: 10px; font-weight:bold; }
     span.bulletinbulletin { font-size: 16px; font-weight:bold; }
     span.bulletinmoisannee { font-size: 12px; font-weight:bold; }
     span.employe2 { text-align: justify; font-size: 12px; }
     span.titresapesiret { font-size: 10px; font-weight:bold; }
     span.titresemploye1 { font-size: 10px; font-weight:bold; }
     span.nomemployeur { font-size: 12px; font-weight:bold; }
     tr.highlighted { background-color: #FFFF66; }
     anomalie { background-color: #FFFF66; } </style>
    <style type="text/css" media="screen">
     table#enteteGauche { width: 100%; }
     table#enteteDoite { width: 100%; }
     td#enteteGauche { width: 65%; }
     td#enteteDroite { width: 35%; }
     table.maxWidth { width: 100%; }
    </style>
    <style type="text/css" media="print">
     @page { size: 210mm 297mm; @bottom-center { content: "Page " counter(page) " / " counter(pages); } }
     body { font-family: verdana, sans-serif; font-size: 10px; width: 210mm; margin: 0px; }
     table { page-break-inside: avoid }
     tr { page-break-inside: avoid; }
     table#enteteGauche { width: 110mm; }
     table#enteteDoite { width: 70mm; }
     table.maxWidth { width: 180mm; }
    </style>
    <title>Bulletin de paye</title>
   </head>
   <body>
    <!--div id="mainDiv"-->
     <xsl:apply-templates/>

     <xsl:if test="./ano:Anomalie">
      <xsl:call-template name="display-ano">
       <xsl:with-param name="ano" select="./ano:Anomalie"/>
       <xsl:with-param name="display-link" select="0"/>
      </xsl:call-template>
     </xsl:if>

     <xsl:if test="./n:DonneesIndiv/ano:Anomalie">
      <xsl:call-template name="display-ano">
       <xsl:with-param name="ano" select="./n:DonneesIndiv/ano:Anomalie"/>
       <xsl:with-param name="display-link" select="1"/>
       <xsl:with-param name="coll">
        <xsl:value-of select="./n:Employeur/n:Siret/@V"/>
       </xsl:with-param>
       <xsl:with-param name="budg">
        <xsl:value-of select="./n:Budget/n:Code/@V"/>
       </xsl:with-param>
       <xsl:with-param name="pk">
        <xsl:value-of select="./@added:primary-key"/>
       </xsl:with-param>
      </xsl:call-template>
     </xsl:if>

     <xsl:if test="./n:DonneesIndiv/n:Etablissement/ano:Anomalie">
      <xsl:call-template name="display-ano">
       <xsl:with-param name="ano" select="./n:DonneesIndiv/n:Etablissement/ano:Anomalie"/>
       <xsl:with-param name="display-link" select="0"/>
      </xsl:call-template>
     </xsl:if>

     <xsl:if test="./n:DonneesIndiv/n:PayeIndivMensuel/ano:Anomalie">
      <xsl:call-template name="display-ano">
       <xsl:with-param name="ano" select="./n:DonneesIndiv/n:PayeIndivMensuel/ano:Anomalie"/>
       <xsl:with-param name="display-link" select="1"/>
       <xsl:with-param name="coll">
        <xsl:value-of select="./n:Employeur/n:Siret/@V"/>
       </xsl:with-param>
       <xsl:with-param name="budg">
        <xsl:value-of select="./n:Budget/n:Code/@V"/>
       </xsl:with-param>
       <xsl:with-param name="pk">
        <xsl:value-of select="./n:DonneesIndiv/n:PayeIndivMensuel/@added:primary-key"/>
       </xsl:with-param>
      </xsl:call-template>
     </xsl:if>

    <!-- /div-->
   </body>
  </html>
 </xsl:template>

 <xsl:template name="display-ano">
  <xsl:param name="ano"/>
  <xsl:param name="display-link"/>
  <xsl:param name="coll"/>
  <xsl:param name="budg"/>
  <xsl:param name="pk"/>
  <hr/>
  <xsl:for-each select="$ano">
   <xsl:element name="div">
    <xsl:if test="$anoId = ./@ano:anoId">
     <xsl:attribute name="style">background-color: #FFFF66</xsl:attribute>
    </xsl:if>
    <h3>
     <xsl:choose>
      <xsl:when test="$display-link = 1">
       <xsl:element name="a">
        <xsl:attribute name="name">AnoId_<xsl:value-of select="./@ano:anoId"/></xsl:attribute>
        <xsl:attribute name="href">xemelios:/query?docId=documentPaye&amp;etatId=etatPaye&amp;elementId=payeIndivMensuel&amp;collectivite=<xsl:value-of select="$coll"/>&amp;budget=<xsl:value-of select="$budg"/>&amp;path=[@added:primary-key='<xsl:value-of select="$pk"/>']&amp;xsl:param=(elementId,Paye)&amp;xsl:param=(anoId,<xsl:value-of select="./@ano:anoId"/>)</xsl:attribute> Anomalie </xsl:element>
      </xsl:when>
      <xsl:otherwise>
       <xsl:element name="p">Anomalie</xsl:element>
      </xsl:otherwise>
     </xsl:choose>
    </h3>
    <p>
     <span class="gras">Contr&#244;le : </span>
     <xsl:value-of select="./@ano:ctrlId"/>
    </p>
    <p>
     <span class="gras">R&#232;gle fonctionnelle : </span>
     <xsl:value-of select="./ano:ctrlRegleFonct/text()" disable-output-escaping="yes"/>
    </p>
    <p>
     <span class="gras">Message : </span>
     <xsl:value-of select="./ano:message/text()" disable-output-escaping="yes"/>
    </p>
   </xsl:element>
  </xsl:for-each>
 </xsl:template>

 <xsl:template match="n:DonneesIndiv">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="n:PayeIndivMensuel">
  <table style="text-align: left; border-color: red;" class="maxWidth" border="0">
   <tr>
    <td id="enteteGauche">
     <xsl:call-template name="enteteGauche"/> 
    </td>
    <td id="enteteDroite">
     <xsl:call-template name="enteteDroite"/> 
    </td>
   </tr>
  </table>
  <br/>
  <table style="text-align: left;" class="maxWidth">
   <tr>
    <td>
     <xsl:call-template name="categorie"/>
    </td>
    <td>
     <xsl:call-template name="service"/>
    </td>
   </tr>
  </table>

  <br/>
  <xsl:call-template name="tabledetails"/>
  <br/>
  <xsl:call-template name="table4"/>
  <br/>
  <xsl:call-template name="table5"/>
  <br/>
  <xsl:call-template name="evenements"/>
  <br/>
  <xsl:call-template name="multi-budget"/>
 </xsl:template>

 <xsl:template name="enteteGauche">
  <table id="enteteGauche" style="text-align: left; width: 100%; height: 100%;  vertical-align: top; " border="0">
   <tr>
    <td colspan="2">
     <br/>&nbsp; <br/>
    </td>
   </tr>
   <tr>
    <td width="60%" class="cadreEmployeur">
     <xsl:choose>
      <xsl:when test="../n:Etablissement">
       <xsl:call-template name="cadreEmployeur">
        <xsl:with-param name="etabOuEmployeur" select="../n:Etablissement"/>
       </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
       <xsl:call-template name="cadreEmployeur">
        <xsl:with-param name="etabOuEmployeur" select="//n:Employeur"/>
       </xsl:call-template>
      </xsl:otherwise>
     </xsl:choose>
    </td>
    <td>&nbsp; <br/>
    </td>
   </tr>

   <tr>
    <td colspan="2">
     <xsl:choose>
      <xsl:when test="../n:Etablissement">
       <xsl:call-template name="cadreApeSiret">
        <xsl:with-param name="etabOuEmployeur" select="../n:Etablissement"/>
       </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
       <xsl:call-template name="cadreApeSiret">
        <xsl:with-param name="etabOuEmployeur" select="//n:Employeur"/>
       </xsl:call-template>
      </xsl:otherwise>
     </xsl:choose>
    </td>
   </tr>
   <tr>
    <td colspan="2"> &nbsp; <br/>
    </td>
   </tr>
   <tr>
    <td colspan="2">
     <xsl:call-template name="cadreEmploye1"/>
    </td>
   </tr>
  </table>
 </xsl:template>



 <xsl:template name="enteteDroite">
  <table id="enteteDroite" style="text-align: left; width: 100%; height: 100%;  vertical-align: top; " border="0">
   <tr>
    <!--td>&nbsp; <br/>
    </td-->
    <td colspan="3">
     <xsl:call-template name="cadrePeriode"/>
    </td>
   </tr>
   <tr>
    <td colspan="3">&nbsp; <br/>
    </td>
   </tr>

   <tr>
    <td>&nbsp;</td>
    <td class="cadreBulletin">
     <xsl:call-template name="cadreBulletin"/>
    </td>
    <td>&nbsp;</td>
   </tr>
   <tr>
    <!--1 rangee vide pour alignement-->
    <td colspan="3">&nbsp; <br/>
    </td>
   </tr>
   <tr>
    <td colspan="3" class="cadreEmploye2">
     <xsl:call-template name="cadreEmploye2"/>
    </td>
    <!--td>
     <br/>
    </td-->
   </tr>
  </table>
 </xsl:template>


 <xsl:template name="cadreEmployeur">
  <xsl:param name="etabOuEmployeur"/>
  <span class="nomemployeur">
   <xsl:value-of select="$etabOuEmployeur/n:Nom/@V"/>
  </span><br/><br/>
  <xsl:if test="$etabOuEmployeur/n:Adresse/n:Adr1">
   <span>
   <xsl:if test="count($tags-ano/node[@id = $etabOuEmployeur/n:Adresse/n:Adr1/@ano:node-id]) &gt; 0">
    <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
   </xsl:if>
   <xsl:value-of select="$etabOuEmployeur/n:Adresse/n:Adr1/@V"/>
    <br/>
   </span></xsl:if>
  <xsl:if test="$etabOuEmployeur/n:Adresse/n:Adr2">
  <span>
   <xsl:if test="count($tags-ano/node[@id = $etabOuEmployeur/n:Adresse/n:Adr2/@ano:node-id]) &gt; 0">
    <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
   </xsl:if>
   <xsl:value-of select="$etabOuEmployeur/n:Adresse/n:Adr2/@V"/>
   <br/>
  </span></xsl:if>
  <xsl:if test="$etabOuEmployeur/n:Adresse/n:Adr3">
  <span>
   <xsl:if test="count($tags-ano/node[@id = $etabOuEmployeur/n:Adresse/n:Adr3/@ano:node-id]) &gt; 0">
    <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
   </xsl:if>
   <xsl:value-of select="$etabOuEmployeur/n:Adresse/n:Adr3/@V"/>
   <br/>
  </span></xsl:if>
  <span>
   <xsl:if test="count($tags-ano/node[@id = $etabOuEmployeur/n:Adresse/n:CP/@ano:node-id]) &gt; 0">
    <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
   </xsl:if>
   <xsl:value-of select="$etabOuEmployeur/n:Adresse/n:CP/@V"/></span>&nbsp;<span><xsl:if test="count($tags-ano/node[@id = $etabOuEmployeur/n:Adresse/n:Ville/@ano:node-id]) &gt; 0">
    <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
   </xsl:if>
   <xsl:value-of select="$etabOuEmployeur/n:Adresse/n:Ville/@V"/>
   <br/>
  </span>
 </xsl:template>

 <xsl:template name="cadreApeSiret">
  <xsl:param name="etabOuEmployeur"/>
  <br/>
  <span class="titresapesiret"> No APE: </span><xsl:value-of select="//n:Employeur/n:APE/@V"/>&nbsp; <span class="titresapesiret"> SIRET: </span>
  <xsl:if test="string-length($etabOuEmployeur/n:Siret/@V) = 0">
   <span><xsl:if test="count($tags-ano/node[@id = $etabOuEmployeur/n:Siret/@ano:node-id]) &gt; 0">
     <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
    </xsl:if>Non renseign&#233;.</span>
   <br/>
  </xsl:if>
  <xsl:if test="string-length($etabOuEmployeur/n:Siret/@V) &gt; 0">
   <xsl:value-of select="$etabOuEmployeur/n:Siret/@V"/>
   <br/>
  </xsl:if>
  <span class="titresapesiret">URSSAF : </span>
  <xsl:value-of select="$etabOuEmployeur/n:NumUrssaf/@V"/>
 </xsl:template>

 <xsl:template name="cadreEmploye1">
  <xsl:for-each select=".">
   <span class="titresemploye1">MATRICULE : </span>
   <xsl:value-of select="n:Agent/n:Matricule/@V"/>
   <br/>
   <span class="titresemploye1">Nbre Heures : </span>
   <xsl:value-of select="n:NbHeureTotal/@V"/>
   <br/>
   <span class="titresemploye1">Nbre Heures Sup : </span>
   <span>
    <xsl:if test="count($tags-ano/node[@id = n:NbHeureSup/@ano:node-id]) &gt; 0">
     <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
    </xsl:if>
    <xsl:value-of select="n:NbHeureSup/@V"/>
   </span>
   <br/>
   <span class="titresemploye1">Qualification : </span>
   <xsl:if test="string-length(n:Agent/n:EmploiMetier/@V) = 0">
    <span><xsl:if test="count($tags-ano/node[@id = n:Agent/n:EmploiMetier/@ano:node-id]) &gt; 0">
      <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
     </xsl:if>Non renseign&#233;.</span>
    <br/>
   </xsl:if>
   <xsl:if test="string-length(n:Agent/n:EmploiMetier/@V) &gt; 0">
    <xsl:value-of select="n:Agent/n:EmploiMetier/@V"/>
    <br/>
   </xsl:if>
   <xsl:choose>
    <xsl:when test="n:Agent/n:RefNomenStatutaire/Code">
     <span class="titresemploye1">Nomenclature statutaire : </span>
     <xsl:value-of select="n:Agent/n:RefNomenStatutaire/Code/@V"/>
     <br/>
    </xsl:when>
    <xsl:otherwise/>
   </xsl:choose>
   <span class="titresemploye1">NIR : </span>
   <xsl:value-of select="n:Agent/n:NIR/@V"/>
  </xsl:for-each>
 </xsl:template>

 <xsl:template name="cadrePeriode">
  <!-- Bidouille d'alignement  -->
   <table style="width: 100%; ">
    <tbody>
    <xsl:for-each select="n:Periode">
     <tr>
     <td align="right">
      <span class="periodeperiode">PERIODE DU</span>
     </td>
     <td align="center">
      <xsl:call-template name="slash-date">
       <xsl:with-param name="datebrute" select="n:DateDebut/@V"/>
      </xsl:call-template>
      <!--<xsl:value-of select="DateDebut/@V"/> -->
     </td>
     <td align="left">
      <span class="periodeperiode">AU</span>
     </td>
     <td align="center">
      <span>
       <xsl:call-template name="slash-date">
        <xsl:with-param name="datebrute" select="n:DateFin/@V"/>
       </xsl:call-template>
      </span>
     </td>
    </tr>
    </xsl:for-each>
    </tbody>
   </table>
 </xsl:template>


 <xsl:template name="cadreBulletin">
  <center>
   <H1>BULLETIN DE PAYE</H1>
  </center>
  <br/>
  <center>
   <span class="bulletinmoisannee">
    <xsl:call-template name="iso-mois">
     <xsl:with-param name="datebrute" select="/n:DocumentPaye/n:Mois/@V"/>
    </xsl:call-template>&nbsp;<xsl:value-of select="/n:DocumentPaye/n:Annee/@V"/></span>
  </center>
 </xsl:template>

 <xsl:template name="cadreEmploye2">
  <xsl:for-each select="n:Agent">
   <span class="employe2">
    <xsl:value-of select="n:Civilite/@V"/>&nbsp; <span><xsl:if test="count($tags-ano/node[@id = n:Nom/@ano:node-id]) &gt; 0">
      <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
     </xsl:if>
     <xsl:value-of select="n:Nom/@V"/>&nbsp; </span>
    <span>
     <xsl:if test="count($tags-ano/node[@id = n:ComplNom/@ano:node-id]) &gt; 0">
      <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
     </xsl:if>
     <xsl:if test="string-length(n:ComplNom/@V) &gt; 0">
      <xsl:value-of select="n:ComplNom/@V"/>&nbsp; </xsl:if>
    </span>
    <xsl:value-of select="n:Prenom/@V"/><br/><br/>
    <span>
     <xsl:if test="count($tags-ano/node[@id = n:Adresse/n:Adr1/@ano:node-id]) &gt; 0">
      <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
     </xsl:if>
     <xsl:if test="string-length(n:Adresse/n:Adr1/@V) &gt; 0">
      <xsl:value-of select="n:Adresse/n:Adr1/@V"/>
      <br/>
     </xsl:if>
    </span>
    <span>
     <xsl:if test="count($tags-ano/node[@id = n:Adresse/n:Adr2/@ano:node-id]) &gt; 0">
      <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
     </xsl:if>
     <xsl:if test="string-length(n:Adresse/n:Adr2/@V) &gt; 0">
      <xsl:value-of select="n:Adresse/n:Adr2/@V"/>
      <br/>
     </xsl:if>
    </span>
    <span>
     <xsl:if test="count($tags-ano/node[@id = n:Adresse/n:Adr3/@ano:node-id]) &gt; 0">
      <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
     </xsl:if>
     <xsl:if test="string-length(n:Adresse/n:Adr3/@V) &gt; 0">
      <xsl:value-of select="n:Adresse/n:Adr3/@V"/>
      <br/>
      <br/>
     </xsl:if>
    </span>
    <span>
     <xsl:if test="count($tags-ano/node[@id = n:Adresse/n:CP/@ano:node-id]) &gt; 0">
      <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
     </xsl:if>
     <xsl:value-of select="n:Adresse/n:CP/@V"/>
    </span>&nbsp;<span>
     <xsl:if test="count($tags-ano/node[@id = n:Adresse/n:Ville/@ano:node-id]) &gt; 0">
      <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
     </xsl:if>
     <xsl:value-of select="n:Adresse/n:Ville/@V"/>
    </span>
   </span>
  </xsl:for-each>
 </xsl:template>



 <xsl:template name="categorie">
  <table class="categorie">
   <thead>
    <tr>
     <th>CAT</th>
     <th>G.R.</th>
     <th>ECH.</th>
     <th>INDICE</th>
     <th>NBI</th>
     <th>SF</th>
     <th>TX</th>
    </tr>
   </thead>
   <tbody>
    <xsl:for-each select=".">
     <tr>
      <td>
       <xsl:call-template name="getLibelleStatut">
        <xsl:with-param name="codeStatut" select="n:Agent/n:Statut/@V"/>
       </xsl:call-template>
      </td>
      <xsl:if test="string-length(n:Agent/n:Grade/@V) = 0">
       <td><xsl:if test="count($tags-ano/node[@id = n:Agent/n:Grade/@ano:node-id]) &gt; 0">
         <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
        </xsl:if>Non renseign&#233;.</td>
      </xsl:if>
      <xsl:if test="string-length(n:Agent/n:Grade/@V) &gt; 0">
       <td>
        <xsl:value-of select="n:Agent/n:Grade/@V"/>
       </td>
      </xsl:if>
      <xsl:if test="string-length(n:Agent/n:Echelon/@V) = 0">
       <td><xsl:if test="count($tags-ano/node[@id = n:Agent/n:Echelon/@ano:node-id]) &gt; 0">
         <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
        </xsl:if>Non renseign&#233;.</td>
      </xsl:if>
      <xsl:if test="string-length(n:Agent/n:Echelon/@V) &gt; 0">
       <td>
        <xsl:value-of select="n:Agent/n:Echelon/@V"/>
       </td>
      </xsl:if>
      <td>
       <xsl:value-of select="n:Agent/n:Indice/@V"/>
      </td>
      <td class="numeric">
       <xsl:value-of select="n:NBI/@V"/>
      </td>
      <td class="numeric">
       <xsl:value-of select="n:Agent/n:NbEnfants/@V"/>
      </td>
      <td class="numeric">
       <xsl:value-of select="n:QuotiteTrav/@V"/>
      </td>
     </tr>
    </xsl:for-each>
   </tbody>
  </table>
 </xsl:template>

 <xsl:template name="service">
  <table class="categorie">
   <thead>
    <tr>
     <th>Service</th>
    </tr>
   </thead>
   <tbody>
    <tr>
     <td>
      <xsl:value-of select="n:Service/@V"/>
     </td>
    </tr>
   </tbody>
  </table>
 </xsl:template>


 <xsl:template name="tabledetails">
  <table class="detail maxWidth" style="text-align: left;">
   <thead>
    <tr>
     <th colspan="2">RUBRIQUE DE PAIE<br/>LIBELLE</th>
     <th>NOMBRE<br/>OU BASE</th>
     <th>TAUX</th>
     <th>GAINS</th>
     <th>RETENUES</th>
     <th colspan="2">CHARGES PATRONALES</th>
    </tr>
    <tr>
     <th colspan="2"/>
     <th/>
     <th/>
     <th/>
     <th/>
     <th>TAUX</th>
     <th>MONTANT</th>
    </tr>
   </thead>
   <tbody>
    <xsl:call-template name="details"/>
   </tbody>
  </table>
 </xsl:template>

 <xsl:template name="details">
  <xsl:variable name="dataOrder">
   <xsl:choose>
    <xsl:when test="NaN = number(n:Remuneration/*[1]/n:Ordre/@V)">text</xsl:when>
    <xsl:otherwise>number</xsl:otherwise>
   </xsl:choose>
  </xsl:variable>
  <xsl:choose>
   <xsl:when test="$dataOrder = 'number'">
    <xsl:for-each select="n:Remuneration/*">
     <!-- sort by number order -->
     <xsl:sort select="n:Ordre/@V" data-type="number" order="ascending"/>
     <xsl:choose>
      <xsl:when test="name()='Commentaire'">
       <tr>
        <td>&nbsp;</td>
        <td colspan="7">
         <xsl:value-of select="n:Libelle/@V"/>
        </td>
       </tr>
      </xsl:when>
      <xsl:otherwise>
       <xsl:element name="tr">
        <xsl:if test="./@generated-id = $IdLignePaye">
         <xsl:attribute name="class">highlighted</xsl:attribute>
        </xsl:if>
        <td>
         <xsl:if test="name(.)='Rappel'">
          <xsl:if test="count($tags-ano/node[@id = n:Code/@ano:node-id]) &gt; 0">
           <xsl:attribute name="style">background-color: #FFFF66;</xsl:attribute>
          </xsl:if>
         </xsl:if>
         <xsl:value-of select="n:Code/@V"/>
        </td>
        <td>
         <xsl:value-of select="n:Libelle/@V"/>
         <xsl:if test="n:PeriodeRef">
          <xsl:text>&nbsp;(</xsl:text>
          <xsl:call-template name="slash-date">
           <xsl:with-param name="datebrute">
            <xsl:value-of select="n:PeriodeRef/n:DateDebut/@V"/>
           </xsl:with-param>
          </xsl:call-template>
          <xsl:text>&nbsp;-&nbsp;</xsl:text>
          <xsl:call-template name="slash-date">
           <xsl:with-param name="datebrute">
            <xsl:value-of select="n:PeriodeRef/n:DateFin/@V"/>
           </xsl:with-param>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
         </xsl:if>
         <xsl:if test="name(.)='Rappel'">&nbsp;(R)</xsl:if>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="n:Base/@V">
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Base/@V"/>
           </xsl:call-template>
          </xsl:when>
          <xsl:when test="n:NbUnite">
           <xsl:value-of select="n:NbUnite/@V"/>
          </xsl:when>
         </xsl:choose>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="name()='Cotisation'"> </xsl:when>
          <xsl:otherwise>
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Taux/@V"/>
           </xsl:call-template>
           <!--xsl:value-of select="Taux/@V"/-->
          </xsl:otherwise>
         </xsl:choose>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="name()='Deduction'"/>
          <xsl:when test="name()='Retenue'"/>
          <xsl:when test="name()='Cotisation'"/>
          <xsl:otherwise>
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Mt/@V"/>
           </xsl:call-template>
          </xsl:otherwise>
         </xsl:choose>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="name()='Deduction'">
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Mt/@V"/>
           </xsl:call-template>
          </xsl:when>
          <xsl:when test="name()='Retenue'">
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Mt/@V"/>
           </xsl:call-template>
          </xsl:when>
          <xsl:otherwise/>
         </xsl:choose>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="name()='Cotisation'">
           <xsl:if test="./n:Taux">
            <xsl:call-template name="number">
             <xsl:with-param name="num" select="n:Taux/@V"/>
            </xsl:call-template>
           </xsl:if>
          </xsl:when>
          <xsl:otherwise> </xsl:otherwise>
         </xsl:choose>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="name()='Cotisation'">
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Mt/@V"/>
           </xsl:call-template>
          </xsl:when>
          <xsl:otherwise/>
         </xsl:choose>
        </td>
       </xsl:element>
      </xsl:otherwise>
     </xsl:choose>
    </xsl:for-each>
   </xsl:when>
   <xsl:otherwise>
    <!-- sort by alphabetical order -->
    <xsl:for-each select="n:Remuneration/*">
     <xsl:sort select="n:Ordre/@V" data-type="text" order="ascending"/>
     <xsl:choose>
      <xsl:when test="name()='Commentaire'">
       <tr>
        <td>&nbsp;</td>
        <td colspan="7">
         <xsl:value-of select="n:Libelle/@V"/>
        </td>
       </tr>
      </xsl:when>
      <xsl:otherwise>
       <xsl:element name="tr">
        <xsl:if test="./@generated-id = $IdLignePaye">
         <xsl:attribute name="class">highlighted</xsl:attribute>
        </xsl:if>
        <td>
         <xsl:value-of select="n:Code/@V"/>
        </td>
        <td>
         <xsl:value-of select="n:Libelle/@V"/>
         <xsl:if test="n:PeriodeRef">
          <xsl:text>&nbsp;(</xsl:text>
          <xsl:call-template name="slash-date">
           <xsl:with-param name="datebrute">
            <xsl:value-of select="n:PeriodeRef/n:DateDebut/@V"/>
           </xsl:with-param>
          </xsl:call-template>
          <xsl:text>&nbsp;-&nbsp;</xsl:text>
          <xsl:call-template name="slash-date">
           <xsl:with-param name="datebrute">
            <xsl:value-of select="n:PeriodeRef/n:DateFin/@V"/>
           </xsl:with-param>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
         </xsl:if>
         <xsl:if test="name(.)='Rappel'">&nbsp;(R)</xsl:if>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="n:Base/@V">
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Base/@V"/>
           </xsl:call-template>
          </xsl:when>
          <xsl:when test="n:NbUnite">
           <xsl:value-of select="n:NbUnite/@V"/>
          </xsl:when>
         </xsl:choose>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="name()='Cotisation'"> </xsl:when>
          <xsl:otherwise>
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Taux/@V"/>
           </xsl:call-template>
           <!--xsl:value-of select="Taux/@V"/-->
          </xsl:otherwise>
         </xsl:choose>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="name()='Deduction'"/>
          <xsl:when test="name()='Retenue'"/>
          <xsl:when test="name()='Cotisation'"/>
          <xsl:otherwise>
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Mt/@V"/>
           </xsl:call-template>
          </xsl:otherwise>
         </xsl:choose>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="name()='Deduction'">
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Mt/@V"/>
           </xsl:call-template>
          </xsl:when>
          <xsl:when test="name()='Retenue'">
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Mt/@V"/>
           </xsl:call-template>
          </xsl:when>
          <xsl:otherwise/>
         </xsl:choose>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="name()='Cotisation'">
           <xsl:if test="./n:Taux">
            <xsl:call-template name="number">
             <xsl:with-param name="num" select="n:Taux/@V"/>
            </xsl:call-template>
           </xsl:if>
          </xsl:when>
          <xsl:otherwise> </xsl:otherwise>
         </xsl:choose>
        </td>
        <td class="numeric">
         <xsl:choose>
          <xsl:when test="name()='Cotisation'">
           <xsl:call-template name="number">
            <xsl:with-param name="num" select="n:Mt/@V"/>
           </xsl:call-template>
          </xsl:when>
          <xsl:otherwise/>
         </xsl:choose>
        </td>
       </xsl:element>
      </xsl:otherwise>
     </xsl:choose>
    </xsl:for-each>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template name="table4">
  <table class="table4" width="100%">
   <tr>
    <td>
     <xsl:call-template name="reglement"/>
    </td>
    <td width="20%"> </td>
   </tr>
  </table>
 </xsl:template>

 <xsl:template name="reglement">
  <table class="reglement" width="100%">
   <thead>
    <tr>
     <th> MODE DE REGLEMENT </th>
     <th> NET A PAYER </th>
    </tr>
   </thead>
   <tbody>
    <tr>
     <td>
      <table>
       <tr>
        <td> Mise en paiement le <xsl:call-template name="slash-date">
          <xsl:with-param name="datebrute" select="n:DatePaiement/@V"/>
         </xsl:call-template>
        </td>
       </tr>
       <tr>
        <td>
         <xsl:call-template name="compteBancaire">
          <xsl:with-param name="compte" select="n:Agent/n:CptBancaire"/>
         </xsl:call-template>
        </td>
       </tr>
      </table>
     </td>
     <td>
      <table width="100%">
       <tr>
        <td> &nbsp; </td>
       </tr>
       <tr>
        <td class="numeric">
         <xsl:call-template name="number">
          <xsl:with-param name="num" select="n:MtNetAPayer/@V"/>
         </xsl:call-template>
        </td>
       </tr>
      </table>
     </td>
    </tr>
   </tbody>
  </table>
 </xsl:template>


 <xsl:template name="table5">
  <table class="table5" width="100%">
   <tr>
    <td>
     <xsl:call-template name="netcumul"/>
    </td>
    <td width="20%"> </td>
   </tr>
  </table>
 </xsl:template>



 <xsl:template name="netcumul">
  <table width="100%" class="netcumul">
   <thead>
    <tr>
     <th> Net </th>
     <th> NET IMPOSABLE </th>
     <th>
      <xsl:choose>
       <xsl:when test="n:MtBrut"> BRUT </xsl:when>
       <xsl:otherwise> &nbsp; </xsl:otherwise>
      </xsl:choose>
     </th>
    </tr>
   </thead>
   <tbody>
    <tr>
     <td class="numeric">
      <xsl:call-template name="number">
       <xsl:with-param name="num" select="n:MtNet/@V"/>
      </xsl:call-template>
     </td>
     <td class="numeric">
      <xsl:call-template name="number">
       <xsl:with-param name="num" select="n:MtImposable/@V"/>
      </xsl:call-template>
     </td>
     <td class="numeric">
      <xsl:choose>
       <xsl:when test="n:MtBrut">
        <xsl:call-template name="number">
         <xsl:with-param name="num" select="n:MtBrut/@V"/>
        </xsl:call-template>
       </xsl:when>
       <xsl:otherwise> &nbsp; </xsl:otherwise>
      </xsl:choose>
     </td>
    </tr>
   </tbody>
   <thead>
    <tr>
     <th> CUMUL BRUT </th>
     <th> CUMUL NET IMPOSABLE </th>
     <th> CUMUL BASE S.S. </th>
    </tr>
   </thead>
   <tbody>
    <tr>
     <td class="numeric">
      <xsl:call-template name="number">
       <xsl:with-param name="num" select="n:CumulMtBrut/@V"/>
      </xsl:call-template>
     </td>
     <td class="numeric">
      <xsl:call-template name="number">
       <xsl:with-param name="num" select="n:CumulMtImposable/@V"/>
      </xsl:call-template>
     </td>
     <td class="numeric">
      <xsl:call-template name="number">
       <xsl:with-param name="num" select="n:CumulBaseSS/@V"/>
      </xsl:call-template>
     </td>
    </tr>
   </tbody>
  </table>
 </xsl:template>

 <xsl:template name="evenements">
  <xsl:if test="exists(n:Evenement)">
   <h4>Ev&#232;nements</h4>
   <table class="evenements">
    <thead>
     <tr>
      <th>Libell&#233;</th>
      <th>Description</th>
     </tr>
    </thead>
    <tbody>
     <xsl:for-each select="n:Evenement">
      <xsl:variable name="codeEvt" select="n:Code/@V"/>
      <tr>
       <td>
        <xsl:value-of select="$repository/n:DocumentPaye/n:Nomenclatures/n:NomenclatureEvtPaye/n:Correspondance[n:Code/@V=$codeEvt]/n:Libelle/@V"/>&nbsp; </td>
       <td>
        <xsl:value-of select="n:Description/@V"/> &nbsp; </td>
      </tr>
     </xsl:for-each>
    </tbody>
   </table>
  </xsl:if>
 </xsl:template>

 <xsl:template name="multi-budget">
  <xsl:if test="n:RepartitionBudget">
   <h4>Répartition budgétaire</h4>
   <table class="repartitionbudget">
    <thead>
     <tr>
      <th>Code Budget</th>
      <th>Libell&#233;</th>
      <th>Taux</th>
      <th>Montant</th>
     </tr>
    </thead>
    <tbody>
     <xsl:for-each select="n:RepartitionBudget">
      <xsl:variable name="codeBudget" select="./n:CodeBudget/@V"/>
      <tr>
       <td>
        <xsl:value-of select="./n:CodeBudget/@V"/>
       </td>
       <td>
        <xsl:value-of select="$repository/n:DocumentPaye/n:Nomenclatures/n:NomenclatureBudget/n:Correspondance[n:Code/@V=$codeBudget]/n:Libelle/@V"/>
       </td>
       <td>
        <xsl:if test="./n:Taux">
         <xsl:value-of select="./n:Taux/@V"/>
        </xsl:if>
       </td>
       <td>
        <xsl:value-of select="./n:Mt/@V"/>
       </td>
      </tr>
     </xsl:for-each>
    </tbody>
   </table>
  </xsl:if>
 </xsl:template>

 <xsl:template name="number">
  <xsl:param name="num"/>
  <xsl:choose>
   <xsl:when test="string-length($num) = 0"/>
   <xsl:when test="number($num) = 0"/>
   <xsl:when test="string(number($num)) = 'NaN'"/>
   <xsl:otherwise>
    <xsl:value-of select="format-number($num,'# ###,00;-# ###,00','decformat')"/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

</xsl:transform>
