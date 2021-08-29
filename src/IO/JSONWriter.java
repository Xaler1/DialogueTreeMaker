package IO;

import Managers.Project;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class JSONWriter {

    public static void writeProject(Project project, File destination) throws ParserConfigurationException, TransformerException, IOException {
        Document xml_doc = XMLWriter.getProjectXML(project, false);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(xml_doc), new StreamResult(writer));
        String xml_str = writer.getBuffer().toString();
        JSONObject json = XML.toJSONObject(xml_str);
        FileWriter file_writer = new FileWriter(destination);
        json.write(file_writer);
        file_writer.close();
    }
}
