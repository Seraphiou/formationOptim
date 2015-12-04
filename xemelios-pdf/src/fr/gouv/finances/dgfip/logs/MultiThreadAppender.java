/*
 * Copyright
 *  2012 axYus - http://www.axyus.com
 *  2012 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS_NB.
 *
 * XEMELIOS_NB is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS_NB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS_NB; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.logs;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Cette classe propose un fichier de sortie par thread, si le thread
 * se base sur un MDC et propose la clef {@link #MDC_THREAD_KEY}. Si
 * le thread n'a pas cette clef, se base sur l'appender par défaut.
 *
 * Les paramètres attendus sont :
 * <ul>
 * <li>targetDirectory: l'emplacement dans lequel déposer les fichiers créés par threads</li>
 * </ul>
 * Il faut un appender-ref pour définir l'appender par défaut
 * @author cmarchand
 */
public class MultiThreadAppender extends AppenderSkeleton implements AppenderAttachable {
    public static final transient String MDC_THREAD_KEY = "thread.key";
    public static final transient String START_SPLIT_MSG = "start.split.output";
    public static final transient String END_SPLIT_MSG = "end.split.output";
    
    private static MultiThreadAppender INSTANCE = null;

//    private String defaultAppenderRef;
    private String targetDirectory;

    private Appender defaultAppender;
    private Hashtable<Object, FileAppender> perThreadAppenders;
    private Hashtable<Object, File> closedFiles;

    public MultiThreadAppender() {
        super();
        perThreadAppenders = new Hashtable<Object, FileAppender>();
        closedFiles = new Hashtable<Object, File>();
        if(INSTANCE==null) INSTANCE = this;
    }
    /**
     * Renvoie le fichier créé pour ce Thread. Une fois la réponse renvoyée,
     * le mapping est supprimé ; donc dans deux appels successifs, le second appel
     * renverra toujours <tt>null</tt>.
     * Si le fichier n'a pas été fermé, renvoie <tt>null</tt>.
     * @param mdcKeyName
     * @return
     */
    public static File getFile(Object mdcKeyName) {
        return INSTANCE.closedFiles.remove(mdcKeyName);
    }

    @Override
    public void activateOptions() {
        super.activateOptions();
        if(targetDirectory==null)
            LogLog.error("Le paramètre targetDirectory est requis pour les appender "+MultiThreadAppender.class.getName());
    }

    @Override
    protected void append(LoggingEvent le) {
        Object mdcKey = le.getMDC(MDC_THREAD_KEY);
        if(mdcKey==null) {
            defaultAppender.doAppend(le);
        } else {
            Object msg = le.getMessage();
            if(le.getLevel().equals(Level.FATAL)) {
                if(START_SPLIT_MSG.equals(msg)) {
                    if(perThreadAppenders.get(mdcKey)!=null) {
                        closeThreadAppender(mdcKey);
                    }
                    createThreadAppender(mdcKey);
                } else if(END_SPLIT_MSG.equals(msg)) {
                    closeThreadAppender(mdcKey);
                }
            } else {
                FileAppender appender = perThreadAppenders.get(mdcKey);
                if(appender==null) {
                    LogLog.error("Trying to log into "+mdcKey.toString()+" but it is not created");
                } else {
                    appender.doAppend(le);
                }
            }
        }
    }

    private void closeThreadAppender(Object mdcKey) {
        FileAppender fa = perThreadAppenders.get(mdcKey);
        String fileName = fa.getFile();
        fa.close();
        perThreadAppenders.remove(mdcKey);
        closedFiles.put(mdcKey, new File(fileName));
    }

    private void createThreadAppender(Object mdcKey) {
        File targetFile = new File(targetDirectory, mdcKey.toString()+".log");
        if(targetFile.exists()) targetFile.delete();
        try {
            FileAppender fa = new FileAppender(getLayout(), targetFile.getAbsolutePath(), false);
            perThreadAppenders.put(mdcKey, fa);
        } catch(Exception ex) {
            LogLog.error("creating FileAppender for "+mdcKey.toString(), ex);
        }
    }
    @Override
    public void close() {
        for(Object key:perThreadAppenders.keySet()) {
            closeThreadAppender(key);
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    public void addAppender(Appender apndr) {
        defaultAppender = apndr;
    }

    @Override
    public Enumeration getAllAppenders() {
        return new Enumeration() {
            boolean asked = false;
            @Override
            public boolean hasMoreElements() {
                return !asked;
            }
            @Override
            public Object nextElement() {
                asked = true;
                return defaultAppender;
            }
        };
    }

    @Override
    public Appender getAppender(String string) {
        return defaultAppender;
    }

    @Override
    public boolean isAttached(Appender apndr) {
        return apndr.equals(defaultAppender);
    }

    @Override
    public void removeAllAppenders() {
        defaultAppender = null;
    }

    @Override
    public void removeAppender(Appender apndr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeAppender(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

}
