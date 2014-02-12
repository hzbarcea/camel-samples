package org.example.camel.six.one;

public class Individual {
    public String fullName;
    public int weight;

    public Individual(String fullName, int weight) {
    	this.fullName = fullName;
    	this.weight = weight;
    }
    
    public String toString() {
    	return fullName + " [" + weight + "]";
    }
}