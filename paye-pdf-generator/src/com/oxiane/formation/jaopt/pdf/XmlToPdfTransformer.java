package com.oxiane.formation.jaopt.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;
import com.oxiane.formation.jaopt.pdf.utils.CustomUserAgentCallback;
import com.oxiane.formation.jaopt.pdf.utils.FactoryProvider;

public class XmlToPdfTransformer implements Runnable {

	private InputStream inputFile;
	private File outputFile;

	@Override
	public void run() {

		StreamSource source = new StreamSource(inputFile);
        File xhtmlFile = new File(outputFile.getParentFile(), "temp.xhtml");
        StreamResult result = new StreamResult(xhtmlFile);
        templates.newTransformer();
        if(repository!=null) {
        	templates.newTransformer().setParameter("repository", repository);
        }
        transformer.transform(source, result);
        try (OutputStream os = new FileOutputStream(outputFile)) {
            ITextRenderer renderer = new ITextRenderer();
            CustomUserAgentCallback callback = new CustomUserAgentCallback(renderer.getOutputDevice());
            callback.setSharedContext(renderer.getSharedContext());
            renderer.getSharedContext().setUserAgentCallback(callback);
            StyleReference css = new StyleReference(callback);
            renderer.setDocument(xhtmlFile);
            renderer.layout();
            renderer.createPDF(os, true);
            os.flush();
        } finally {
            if(xhtmlFile!=null && xhtmlFile.exists()) {
                xhtmlFile.delete();
            }
        }
	}

}
