package de.samply.fhir.valueset.gen.icd;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.ValueSet;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class icdExtractorTest {

    @Test
    void test() {
        Reader codeSystemReader = null, valueSetA00Reader = null, valueSetA01Reader = null;
        try {
            codeSystemReader = new FileReader("src/test/java/de/samply/fhir/valueset/gen/icd/icdCodeSystem.json");
            valueSetA00Reader = new FileReader("src/test/java/de/samply/fhir/valueset/gen/icd/icdValueSetA00.json");
            valueSetA01Reader = new FileReader("src/test/java/de/samply/fhir/valueset/gen/icd/icdValueSetA01.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Extractor extractor = new Extractor(codeSystemReader);
        List<ValueSet> extractedValueSetList = extractor.generate();

        IParser parser = FhirContext.forR4().newJsonParser();
        ValueSet testValueSetA00 = parser.parseResource(ValueSet.class, valueSetA00Reader);
        ValueSet testValueSetA01 = parser.parseResource(ValueSet.class, valueSetA01Reader);

        assertEquals(extractedValueSetList.size(), 2);
        assertEquals(parser.encodeResourceToString(testValueSetA00), parser.encodeResourceToString(extractedValueSetList.get(0)));
        assertEquals(parser.encodeResourceToString(testValueSetA01), parser.encodeResourceToString(extractedValueSetList.get(1)));
    }

}
