http://camel.apache.org/camel-maven-archetypes.html

mvn archetype:generate \
  -DgroupId=org.example.camel.four \
  -DartifactId=four-one \
  -Dversion=1.0.0 \
  -DarchetypeGroupId=org.apache.camel.archetypes \
  -DarchetypeArtifactId=camel-archetype-component \
  -DarchetypeVersion=2.12.2


Provide the following values when prompted:

Define value for property 'name': : Hello
Define value for property 'scheme': : hello

