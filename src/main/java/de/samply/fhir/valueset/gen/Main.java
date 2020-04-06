package de.samply.fhir.valueset.gen;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.samply.fhir.valueset.gen.icd.Extractor;
import org.hl7.fhir.r4.model.ValueSet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    /**
     * https://hapifhir.io/hapi-fhir/docs/model/parsers.html
     *
     * @param args source output
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 2) throw new IllegalArgumentException("source file or output path not provided");
        String inputPath = args[0];
        String outputPath = args[1];

        for (Reader inputFile : getInputFiles(inputPath)) {
            Extractor extractor = new Extractor(inputFile);
            writeValueSets(outputPath, extractor.generate());
        }
    }

    private static List<Reader> getInputFiles(String inputPath) throws IOException {
        System.out.println("Reading files: ");
        List<Reader> listOfReaders = new ArrayList<>();
        for (File file : new File(inputPath).listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                System.out.println(file.getName());
                listOfReaders.add(new FileReader(file));
            }
        }
        return listOfReaders;
    }

    private static void writeValueSets(String outputPath, List<ValueSet> valueSets) throws FileNotFoundException {
        if (outputPath.charAt(outputPath.length() - 1) != File.separatorChar) {
            outputPath += File.separator;
        }
        if (!new File(outputPath).isDirectory() || new File(outputPath).list().length != 0) {
            System.out.println(outputPath + " is not empty or not exists");
            return;
        }
        System.out.println("Writing " + valueSets.size() + " ValueSets to " + outputPath);
        IParser parser = FhirContext.forR4().newJsonParser().setPrettyPrint(true);
        for (ValueSet valueSet : valueSets) {
            try (PrintWriter out = new PrintWriter(outputPath + "ValueSet-" + valueSet.getName())) {
                out.println(parser.encodeResourceToString(valueSet));
            }
        }
    }
}
