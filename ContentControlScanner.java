import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.SdtBlock;
import org.docx4j.wml.SdtRun;

import java.util.ArrayList;
import java.util.List;

public class ContentControlScanner {

    public static void main(String[] args) throws Exception {
        // Load the Word document
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File("your-document.docx"));

        // List to store all content controls
        List<SdtElement> contentControls = new ArrayList<>();

        // Scan main document part
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
        scanContentControls(mainDocumentPart, contentControls);

        // Scan headers and footers
        wordMLPackage.getParts().getParts().values().forEach(part -> {
            if (part instanceof HeaderPart || part instanceof FooterPart) {
                scanContentControls(part, contentControls);
            }
        });

        // Print the count of content controls found
        System.out.println("Total content controls found: " + contentControls.size());
    }

    private static void scanContentControls(Part part, List<SdtElement> contentControls) {
        if (part == null || !(part instanceof MainDocumentPart || part instanceof HeaderPart || part instanceof FooterPart)) {
            return;
        }

        // Use the traversal utility to traverse and collect content controls
        List<Object> elements = part.getContent();
        for (Object obj : elements) {
            Object unwrapped = org.docx4j.XmlUtils.unwrap(obj);
            if (unwrapped instanceof SdtElement) {
                contentControls.add((SdtElement) unwrapped);
            } else if (unwrapped instanceof javax.xml.bind.JAXBElement) {
                // Recursively process the element's children
                scanContentControls(unwrapped, contentControls);
            } else if (unwrapped instanceof List) {
                // Recursively process the list's children
                for (Object child : (List<?>) unwrapped) {
                    scanContentControls(child, contentControls);
                }
            }
        }
    }
}
