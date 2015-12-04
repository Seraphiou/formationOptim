package fr.gouv.finances.dgfip.xemelios.auth;

import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class SimpleXemeliosUserImpl implements XemeliosUser {

    private String id;
    private String displayName;
    private boolean hasAllRoles;
    private Set roles;
    private boolean hasAllDocuments;
    private Set documents;
    private boolean hasAllCollectivites;
    private Hashtable<String, HashSet<String>> collectivites;

    public SimpleXemeliosUserImpl(String id, String displayName, boolean hasAllRoles, Collection<String> roles, boolean hasAllDocuments, Collection<String> documents, boolean hasAllCollectivites, Collection<Collectivite> collectivites) {
        this.id = id;
        this.displayName = displayName;
        this.hasAllRoles = hasAllRoles;
        this.roles = new HashSet<String>(roles != null ? roles : Collections.EMPTY_LIST);
        this.hasAllDocuments = hasAllDocuments;
        this.documents = new HashSet<String>(documents != null ? documents : Collections.EMPTY_LIST);
        this.hasAllCollectivites = hasAllCollectivites;
        this.collectivites = new Hashtable<String, HashSet<String>>();
        if (!hasAllCollectivites && collectivites != null) {
            for (Collectivite coll : collectivites) {
                HashSet<String> colls = this.collectivites.get(coll.getNatureIdentifiant());
                if (colls == null) {
                    colls = new HashSet<String>();
                    this.collectivites.put(coll.getNatureIdentifiant(), colls);
                }
                colls.add(coll.getCollectivite());
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean hasRole(String role) {
        return hasAllRoles || roles.contains(role);
    }

    @Override
    public boolean hasDocument(String document) {
        return hasAllDocuments || documents.contains(document);
    }

    @Override
    public boolean hasCollectivite(String collectivite, DocumentModel dm) {
        if (hasAllCollectivites) {
            return true;
        } else {
            HashSet<String> colls = collectivites.get(dm.getNatureIdentifiantCollectivite());
            if (colls == null) {
                return false;
            } else {
                if (colls.contains(collectivite)) {
                    return true;
                } else {
                    return colls.contains("*");
                }
            }
        }
    }
}
