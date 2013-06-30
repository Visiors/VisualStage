package com.visiors.visualstage.generics.attribute;

/**
 * This interface defines attributes of visual objects that implements
 * interface. while PropertyOwner provide properties that can be set in a higher
 * lever, this interface defines attributes that are aimed to be used in a lower
 * level. This attribute are mostly used intern to control objects behavior. A
 * Subgrup for instance use the attribute ATTRIBUTE_SELECTABLE to disallow
 * selecting its member. The Utility class AttributeUtil contains some useful
 * methods to set or get attributes of Attributable Objects.
 * 
 * @see {@link AttributeUtil}
 */
public interface Attributable {

	public void SetAttributes(Attribute attributes);

	public Attribute getAttributes();
}
