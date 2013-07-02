package com.visiors.visualstage.property;

public interface PropertyList extends Property, Iterable<Property> {

    public boolean add(Property property);

    public Property get(String name);

    public Property get(int index);

    public boolean remove(String name);

    public boolean remove(int index);

    public int size();

    @Override
    public PropertyList deepCopy();

    public void setText(String text);

    public String getText();
}
