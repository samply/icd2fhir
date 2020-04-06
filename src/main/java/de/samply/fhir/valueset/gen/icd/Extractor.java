package de.samply.fhir.valueset.gen.icd;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.ValueSet;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Extractor {

    private CodeSystem codeSystem;

    public Extractor(Reader reader) {
        IParser parser = FhirContext.forR4().newJsonParser();
        codeSystem = parser.parseResource(CodeSystem.class, reader);
    }

    public List<ValueSet> generate() {
        List<ValueSet> generatedValueSets = new ArrayList<>();
        for (CodeSystem.ConceptDefinitionComponent subCategory : getSubCategories()) {

            CodeSystem.ConceptDefinitionComponent category = getCategoryBySubCategory(subCategory);
            ValueSet valueSet = findValueSet(generatedValueSets, category.getCode());
            if (valueSet == null) {
                valueSet = createValueSet(category.getCode(), category.getDisplay());
                generatedValueSets.add(valueSet);
            }

            valueSet.getExpansion().addContains(createContains(subCategory.getCode(), subCategory.getDisplay()));
        }
        return generatedValueSets;
    }


    private ValueSet findValueSet(List<ValueSet> valueSetList, String code) {
        for (ValueSet valueSet : valueSetList) {
            if (valueSet.getId().equals(code)) {
                return valueSet;
            }
        }
        return null;
    }


    private CodeSystem.ConceptDefinitionComponent getCategoryBySubCategory(CodeSystem.ConceptDefinitionComponent subCategory) {
        String categoryName = subCategory.getCode().substring(0, 3);
        for (CodeSystem.ConceptDefinitionComponent concept : codeSystem.getConcept()) {
            if (concept.getCode().equals(categoryName)) {
                return concept;
            }
        }
        throw new IllegalStateException("category: " + categoryName + " not found for subcategory: " + subCategory.getCode());
    }

    private List<CodeSystem.ConceptDefinitionComponent> getSubCategories() {
        return codeSystem.getConcept().stream().filter(c -> Pattern.matches("[A-Z][0-9][0-9].[0-9]", c.getCode())).collect(Collectors.toList());
    }


    private ValueSet createValueSet(String code, String title) {
        ValueSet valueSet = new ValueSet()
                .setTitle(title)
                .setName(code)
                .setUrl(codeSystem.getUrl() + "/" + code)
                .setStatus(Enumerations.PublicationStatus.DRAFT);
        valueSet.setId(code);

        // compose
        ValueSet.ConceptSetFilterComponent conceptSetFilterComponent = new ValueSet.ConceptSetFilterComponent()
                .setOp(ValueSet.FilterOperator.ISA)
                .setProperty("parent")
                .setValue(code);
        ValueSet.ConceptSetComponent conceptSetComponent = new ValueSet.ConceptSetComponent()
                .setSystem(codeSystem.getUrl())
                .setVersion(codeSystem.getVersion())
                .addFilter(conceptSetFilterComponent);
        ValueSet.ValueSetComposeComponent valueSetComposeComponent = new ValueSet.ValueSetComposeComponent()
                .addInclude(conceptSetComponent);
        valueSet.setCompose(valueSetComposeComponent);

        return valueSet;
    }

    private ValueSet.ValueSetExpansionContainsComponent createContains(String code, String display) {
        ValueSet.ValueSetExpansionContainsComponent valueSetExpansionContainsComponent = new ValueSet.ValueSetExpansionContainsComponent()
                .setSystem(codeSystem.getUrl())
                .setVersion(codeSystem.getVersion())
                .setCode(code)
                .setDisplay(display);
        return valueSetExpansionContainsComponent;
    }
}
