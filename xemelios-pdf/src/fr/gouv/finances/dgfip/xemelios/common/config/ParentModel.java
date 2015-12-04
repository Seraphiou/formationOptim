/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author chm
 */
public class ParentModel implements NoeudModifiable, Comparable {
    public static final transient String TAG = "parent-coll";
    public static final transient QName QN = new QName(TAG);
    private String id;
    private String libelle;
    private int level = -1;
    private CollectiviteInfosModel parent;
    private XPathModel codePath, libellePath;

    public ParentModel(QName tagName) {
        super();

    }

    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return parent;
    }

    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        parent = (CollectiviteInfosModel)p;
    }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        if(DocumentsMapping.CODE_PATH.getLocalPart().equals(tagName)) return codePath;
        else return libellePath;
    }

    @Override
    public void modifyAttr(String attrName, String value) {
        if("id".equals(attrName)) id=value;
        else if("libelle".equals(attrName)) libelle=value;
    }

    @Override
    public void modifyAttrs(Attributes attrs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getChildIdAttrName(String childTagName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetCharData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getIdValue() {
        return "id";
    }

    @Override
    public String getConfigXPath() {
        return parent.getConfigXPath()+"/"+TAG+"[@level="+level+"]";
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
        // nothing to do
    }

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        if(DocumentsMapping.CODE_PATH.equals(tag)) {
            codePath = (XPathModel)child;
            codePath.setParent(this);
        } else if(DocumentsMapping.LIBELLE_PATH.equals(tag)) {
            libellePath = (XPathModel)child;
            libellePath.setParent(this);
        } else {
            throw new SAXException("Unexpected child for "+getConfigXPath()+": "+tag.toString());
        }
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = attributes.getValue("id");
        libelle = attributes.getValue("libelle");
        if(attributes.getValue("level")!=null)
            level = attributes.getIntValue("level");
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return (XmlMarshallable)getChildAsNoeudModifiable(qName, null);
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        output.addAttribute("id", id);
        output.addAttribute("libelle",libelle);
        output.addAttribute("level",level);
        codePath.marshall(output);
        libellePath.marshall(output);
        output.endTag(TAG);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        if(level==-1) throw new InvalidXmlDefinition(getConfigXPath()+"/@level must be >= 1");
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public ParentModel clone() {
        ParentModel other = new ParentModel(QN);
        other.setId(getId());
        other.setLibelle(getLibelle());
        other.setLevel(getLevel());
        try { other.addChild(codePath.clone(), DocumentsMapping.CODE_PATH); } catch(Exception ex) {}
        try { other.addChild(libellePath.clone(), DocumentsMapping.LIBELLE_PATH); } catch(Exception ex) {}
        return other;
    }

    public XPathModel getCodePath() {
        return codePath;
    }

    public void setCodePath(XPathModel codePath) {
        this.codePath = codePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public XPathModel getLibellePath() {
        return libellePath;
    }

    public void setLibellePath(XPathModel libellePath) {
        this.libellePath = libellePath;
    }

    public CollectiviteInfosModel getParent() {
        return parent;
    }

    public void setParent(CollectiviteInfosModel parent) {
        this.parent = parent;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof ParentModel) {
            ParentModel other = (ParentModel)o;
            return getLevel()-other.getLevel();
        }
        return -1;
    }
    @Override
    public String toString() { return getLibelle(); }

    @Override
    public void prepareForUnload() {
        parent = null;
        if(codePath!=null) codePath.prepareForUnload();
        if(libellePath!=null) libellePath.prepareForUnload();
    }

}
