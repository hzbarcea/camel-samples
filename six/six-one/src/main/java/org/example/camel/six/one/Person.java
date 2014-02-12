package org.example.camel.six.one;

public class Person {
    public String firstName;
    public String lastName;
    public int age;

    public Person(String first, String last, int age) {
        firstName = first;
        lastName = last;
        this.age = age;
    }
    
    public String toString() {
        return firstName + " " + lastName + " (" + age + ")";
    }
}
