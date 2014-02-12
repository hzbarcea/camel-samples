package org.example.camel.six.one;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;

@Converter
public class PersonConverter {
    @Converter
    public Individual fromPerson(Person one, Exchange exchange) {
        return new Individual(one.firstName + " " + one.lastName, one.age + 100);
    }
}