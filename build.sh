cd WSDL_XSD_in_Dita
mvn -DskipTests=true -Dmaven.javadoc.skip=true -B clean org.jacoco:jacoco-maven-plugin:prepare-agent install sonar:sonar
