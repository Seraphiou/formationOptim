/*
 * Copyright
 *   2005 axYus - www.axyus.com
 *   2005 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS.
 *
 * XEMELIOS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package fr.gouv.finances.dgfip.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A writer that writes nowhere.
 * @author chm
 */
public class NullWriter extends OutputStream {
    
    /**
     * Construct a NullWriter
     */
    public NullWriter() {
        super();
    }
    
    /* (non-Javadoc)
     * @see java.io.Flushable#flush()
     */
    @Override
    public void flush() throws IOException {}
    
    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {}
    @Override
    public void write(int i) {}
    
    private static class NullOutputStream extends OutputStream {
        public NullOutputStream() {
            super();
        }
        public void write(int code) {}
        public void write(byte[] b) {}
        public void write(byte[] b,int offset,int length) {}
        public void flush() {}
        public void close() {}
    }
}
