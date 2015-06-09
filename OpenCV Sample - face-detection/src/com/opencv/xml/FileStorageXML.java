package com.opencv.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opencv.core.Mat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class FileStorageXML {

	private static final String    TAG                 = "PCA::FileStorageXML";
	
	// READ Write flag
	public static final int READ = 0 ;
	public static final int WRITE = 1 ;
	
	private Node root ;
	private Document document ;
	
	public FileStorageXML () {
		
		this.root = null ;
		this.document = null ;
	}
	
	// Write Only
	public void create (String filePath, Mat eigenFace, Mat omega) {
		
		//create a new file called "new.xml" in the SD card
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument() ;
			root = document.createElement("opencv_storage") ;
			document.appendChild(root) ;
			{
				writeMatTag ("eigenFace", root, eigenFace) ;
				writeMatTag ("omega", root, omega) ;
			}
			
			// Document 저장
			DOMSource xmlDOM = new DOMSource(document);
			StreamResult xmlFile = new StreamResult(new File(filePath));
			try {
				TransformerFactory.newInstance().newTransformer().transform(xmlDOM, xmlFile);
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void writeMatTag (String tagName, Node root, Mat mat) {
		
		Element matElem = document.createElement("tagName") ;
		matElem.setAttribute("type_id", "opencv-matrix") ;
		root.appendChild(matElem) ;
		{
			Element rows = document.createElement("rows") ;
			rows.appendChild(document.createTextNode(mat.rows() +"")) ;
			matElem.appendChild(rows) ;
		}
		{
			Element cols = document.createElement("cols") ;
			cols.appendChild(document.createTextNode(mat.cols() +"")) ;
			matElem.appendChild(cols) ;
		}
		{
			Element dt = document.createElement("dt") ;
			dt.appendChild(document.createTextNode("d")) ;
			matElem.appendChild(dt) ;
		}
		{
			String dumpData = mat.dump() ;
			dumpData = dumpData.substring(1, dumpData.length() - 1).replace(",", "") ;
			
			Element data = document.createElement("data") ;
			data.appendChild(document.createTextNode(dumpData)) ;
			matElem.appendChild(data) ;
		}
	}
}
