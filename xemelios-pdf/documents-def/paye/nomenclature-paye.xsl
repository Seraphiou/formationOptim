<?xml version="1.1" encoding="iso-8859-1"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&#160;">
]>
<xsl:transform 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 xmlns:n="http://www.minefi.gouv.fr/cp/helios/pes_v2/paye_1_1" 
 xmlns:added="http://projets.admisource.gouv.fr/xemelios/namspaces#added" 
 xmlns:ano="http://projets.admisource.gouv.fr/xemelios/namspaces#anomally" 
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

 <xsl:output method="xhtml" encoding="iso-8859-1" indent="yes" use-character-maps="accents"/>

 <!-- pour eviter les sorties parasites de tags non matches -->
 <xsl:template match="*"> </xsl:template>

 <!-- parametre implicite pour tous les format-number utilises dans la feuille-->
 <xsl:decimal-format decimal-separator="." grouping-separator=" "/>


 <xsl:param name="language" select="'fr'"/>


 <xsl:template match="/n:DocumentPaye">
  <html>
   <head>
    <title>Nomenclature de Paye : <xsl:value-of select="/n:DocumentPaye/n:Employeur/n:Nom/@V"/></title>
    <style type="text/css" media="all">
     @page { size: 210mm 297mm; @bottom-center { content: "Page " counter(page) " / " counter(pages); } }
     body { font-size: 10px; }
     table { -fs-table-paginate: paginate; }
     h2 { font-size: 16px; }
     tr { page-break-inside: avoid }
     td { font-size: 11px; border-width: 1px; border-color: black; border-spacing: 0px border-collapse: collapse; padding: 2px; }
     td.gauche { border-style: none none solid solid; }
     td.droite { border-style: none solid solid solid; }
     .titreGauche { font-weight: bold; text-align: center; background-color: #555555; color: white; border-style: solid none solid solid; }
     .titreDroite { font-weight: bold; text-align: center; background-color: #555555; color: white; border-style: solid solid solid solid; }
    </style>
   </head>
   <body>
    <xsl:apply-templates/>
   </body>
  </html>
 </xsl:template>

 <xsl:template match="n:Nomenclatures">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="/n:DocumentPaye/n:Nomenclatures/n:NomenclatureEvtPaye">
  <h2>
   <xsl:value-of select="n:Description/@V"/>
  </h2>
  <table cellspacing="0">
   <thead>
    <tr>
     <td class="titreGauche">Libellé</td>
     <td class="titreDroite">Code</td>
    </tr>
   </thead>
   <tbody>
    <xsl:apply-templates/>
   </tbody>
  </table>
  <br/>
 </xsl:template>

 <xsl:template match="/n:DocumentPaye/n:Nomenclatures/n:NomenclatureCaisse">
  <h2>
   <xsl:value-of select="n:Description/@V"/>
  </h2>
  <table cellspacing="0">
   <thead>
   <tr>
    <td class="titreGauche">Libellé</td>
    <td class="titreDroite">Code</td>
   </tr>
   </thead>
   <tbody>
    <xsl:apply-templates/>
   </tbody>
  </table>
  <br/>
 </xsl:template>

 <xsl:template match="/n:DocumentPaye/n:Nomenclatures/n:NomenclatureBudget">
  <h2>
   <xsl:value-of select="n:Description/@V"/>
  </h2>
  <table cellspacing="0">
   <thead>
   <tr>
    <td class="titreGauche">Libellé</td>
    <td class="titreDroite">Code</td>
   </tr>
   </thead>
   <tbody>
    <xsl:apply-templates/>
   </tbody>
  </table>
  <br/>
 </xsl:template>

 <xsl:template match="/n:DocumentPaye/n:Nomenclatures/n:NomenclatureNature">
  <h2>
   <xsl:value-of select="n:Description/@V"/>
  </h2>
  <table cellspacing="0">
   <thead>
   <tr>
    <td class="titreGauche">Libellé</td>
    <td class="titreDroite">Code</td>
   </tr>
   </thead>
   <tbody>
    <xsl:apply-templates/>
   </tbody>
  </table>
  <br/>
 </xsl:template>

 <xsl:template match="/n:DocumentPaye/n:Nomenclatures/n:NomenclatureRubriquePaye">
  <!-- celui-ci est particulier -->
  <h2>
   <xsl:value-of select="n:Description/@V"/>
  </h2>
  <table cellspacing="0">
   <thead>
   <tr>
    <td class="titreGauche">Libellé</td>
    <td class="titreGauche">Code</td>
    <td class="titreGauche">Code Caisse</td>
    <td class="titreGauche">Code Nature <i>(part employeur)</i></td>
    <td class="titreDroite">Code Nature <i>(part salariale)</i></td>
   </tr>
   </thead>
   <tbody>
    <xsl:apply-templates/>
   </tbody>
  </table>
  <br/>
 </xsl:template>

 <xsl:template match="/n:DocumentPaye/n:Nomenclatures/n:NomenclatureStatut">
  <h2>
   <xsl:value-of select="n:Description/@V"/>
  </h2>
  <table cellspacing="0">
   <thead>
   <tr>
    <td class="titreGauche">Libellé</td>
    <td class="titreDroite">Code</td>
   </tr>
   </thead>
   <tbody>
    <xsl:apply-templates/>
   </tbody>
  </table>
  <br/>
 </xsl:template>

 <xsl:template match="/n:DocumentPaye/n:Nomenclatures/n:NomenclatureTrain">
  <h2>
   <xsl:value-of select="n:Description/@V"/>
  </h2>
  <table cellspacing="0">
   <thead>
    <tr>
     <td class="titreGauche">Libellé</td>
     <td class="titreDroite">Code</td>
    </tr>
   </thead>
   <tbody>
    <xsl:apply-templates/>
   </tbody>
  </table>
  <br/>
 </xsl:template>
 <!-- fin structure globale-->


 <xsl:template match="n:NomenclatureEvtPaye/n:Correspondance|n:NomenclatureTrain/n:Correspondance">
  <tr>
   <td class="gauche"><xsl:value-of select="n:Libelle/@V"/>&#8201;</td>
   <td class="droite"><xsl:value-of select="n:Code/@V"/>&#8201;</td>
  </tr>
 </xsl:template>

 <xsl:template match="n:NomenclatureCaisse/n:Correspondance">
  <tr>
   <td class="gauche"><xsl:value-of select="n:Libelle/@V"/>&#8201;</td>
   <td class="droite"><xsl:value-of select="n:Code/@V"/>&#8201;</td>
  </tr>
 </xsl:template>

 <xsl:template match="n:NomenclatureNature/n:Correspondance">
  <tr>
   <td class="gauche"><xsl:value-of select="n:Libelle/@V"/>&#8201;</td>
   <td class="droite"><xsl:value-of select="n:Code/@V"/>&#8201;</td>
  </tr>
 </xsl:template>

 <xsl:template match="n:NomenclatureBudget/n:Correspondance">
  <tr>
   <td class="gauche"><xsl:value-of select="n:Libelle/@V"/>&#8201;</td>
   <td class="droite"><xsl:value-of select="n:Code/@V"/>&#8201;</td>
  </tr>
 </xsl:template>

 <xsl:template match="n:NomenclatureRubriquePaye/n:Correspondance">
  <!-- le cas particulier -->
  <tr>
   <td class="gauche"><xsl:value-of select="n:Libelle/@V"/>&#8201;</td>
   <td class="gauche"><xsl:value-of select="n:Code/@V"/>&#8201;</td>
   <td class="gauche"><xsl:value-of select="n:CodeCaisse/@V"/>&#8201;</td>
   <td class="gauche"><xsl:value-of select="n:CodeNatureEmp/@V"/>&#8201;</td>
   <td class="droite"><xsl:value-of select="n:CodeNatureSal/@V"/>&#8201;</td>
  </tr>
 </xsl:template>

 <xsl:template match="n:NomenclatureStatut/n:Correspondance">
  <tr>
   <td class="gauche"><xsl:value-of select="n:Libelle/@V"/>&#8201;</td>
   <td class="droite"><xsl:value-of select="n:Code/@V"/>&#8201;</td>
  </tr>
 </xsl:template>

</xsl:transform>
