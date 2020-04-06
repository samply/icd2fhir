# fhir-valueset-gen

Generate ValueSets of ICD CodeSystem

## Build

         mvn clean package
         
## Run

* As **Source** provide a ICD10 Codesystem as json containing subcategories as concept
* As **Output** provide an empty Folder for generated ValueSets          
         
         java -jar target/fhir-valueset-gen-jar-with-dependencies.jar "src/main/java/de/samply/fhir/valueset/gen/icd/in" "src/main/java/de/samply/fhir/valueset/gen/icd/out"


## License

Copyright 2019 The Samply Development Community

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.