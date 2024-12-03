import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.SdtElement;

import java.io.File;
import java.util.List;

public class Docx4jExample {

    public static void main(String[] args) throws Docx4JException {
        // Load the Word document
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File("example.docx"));

        // Tag to search for
        String targetTag = "MyTag";

        // Find content control by tag
        SdtElement sdtElement = findContentControlByTag(wordMLPackage, targetTag);

        if (sdtElement != null) {
            System.out.println("Content Control found with tag: " + targetTag);
            System.out.println("Content: " + sdtElement.getSdtContent().getContent().toString());
        } else {
            System.out.println("No content control found with tag: " + targetTag);
        }
    }

    private static SdtElement findContentControlByTag(WordprocessingMLPackage wordMLPackage, String tag) {
        List<Object> elements = wordMLPackage.getMainDocumentPart().getContent();

        for (Object element : elements) {
            Object unwrapped = org.docx4j.XmlUtils.unwrap(element);

            if (unwrapped instanceof ContentAccessor) {
                SdtElement sdt = findSdtByTag((ContentAccessor) unwrapped, tag);
                if (sdt != null) {
                    return sdt;
                }
            }
        }

        return null;
    }

    private static SdtElement findSdtByTag(ContentAccessor contentAccessor, String tag) {
        for (Object child : contentAccessor.getContent()) {
            Object unwrapped = org.docx4j.XmlUtils.unwrap(child);

            if (unwrapped instanceof SdtElement) {
                SdtElement sdtElement = (SdtElement) unwrapped;
                String sdtTag = sdtElement.getSdtPr().getTag().getVal();

                if (tag.equals(sdtTag)) {
                    return sdtElement;
                }
            }

            if (unwrapped instanceof ContentAccessor) {
                SdtElement sdt = findSdtByTag((ContentAccessor) unwrapped, tag);
                if (sdt != null) {
                    return sdt;
                }
            }
        }

        return null;
    }
}
