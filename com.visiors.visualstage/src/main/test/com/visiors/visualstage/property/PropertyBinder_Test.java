package com.visiors.visualstage.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.util.PropertyUtil;


public class PropertyBinder_Test {


	@Test
	public void testDataIntegtiry() {

		System.err.println("Integration test...");
		DummyPropertyOwner propertyOwner = new DummyPropertyOwner();
		// check if the initial property values are in sync
		PropertyUnit propertyUnit = PropertyUtil.findPropertyUnit(propertyOwner.getProperties(), "root:valueA");
		assertEquals(propertyOwner.getValueA(), propertyUnit.getValue());
		// change the value and the see if property gets updated
		propertyOwner.setValueA("B");
		propertyUnit = PropertyUtil.findPropertyUnit(propertyOwner.getProperties(), "root:valueA");
		assertEquals(propertyOwner.getValueA(), propertyUnit.getValue());
		// change the property value and the see if class member gets synchronized
		propertyUnit = PropertyUtil.findPropertyUnit(propertyOwner.getProperties(), "root:valueA");
		propertyUnit.setValue("C");
		assertEquals("C", propertyOwner.getValueA());
		System.err.println("Done.");
	}

	@Test
	public void testLoadAll() {

		System.err.println("Load test...");
		DummyPropertyOwner propertyOwner = new DummyPropertyOwner();
		PropertyList properties = new DefaultPropertyList("root");
		properties.add(new DefaultPropertyUnit("valueA",  "X"));
		properties.add(new DefaultPropertyUnit("valueB",   717));
		properties.add(new  DefaultPropertyUnit("valueC", true));
		properties.add(new  DefaultPropertyUnit("valueD", 9.0));
		propertyOwner.setProperties(properties);
		propertyOwner.loadAll();
		assertEquals("X", propertyOwner.getValueA());
		assertEquals(717, propertyOwner.getValueB());
		assertEquals(true, propertyOwner.getValueC());
		System.err.println("Done.");
	}


	@Test
	public void testCustomHanlder() {

		System.err.println("Custom handler test...");
		DummyPropertyOwnerDelegator propertyOwner = new DummyPropertyOwnerDelegator();

		// change the value of a member in the main class 
		propertyOwner.setValueA("A2");
		PropertyUnit propertyUnit = PropertyUtil.findPropertyUnit(propertyOwner.getProperties(), "root:valueA");
		assertEquals(propertyOwner.getValueA(), propertyUnit.getValue());
		// change the member in DummyPropertyOwnerB and see if the property gets updated
		propertyOwner.getPropertyOwnerB().setValueB("B2");
		propertyUnit = PropertyUtil.findPropertyUnit(propertyOwner.getProperties(), "root:branchB:valueB");
		assertEquals(propertyOwner.getPropertyOwnerB().getValueB(), propertyUnit.getValue());
		// change the member in DummyPropertyOwnerC and see if the property gets updated
		propertyOwner.getPropertyOwnerB().getPropertyOwnerC().setValueC("C2");
		propertyUnit = PropertyUtil.findPropertyUnit(propertyOwner.getProperties(), "root:branchB:branchC:valueC");
		assertEquals(propertyOwner.getPropertyOwnerB().getPropertyOwnerC().getValueC(), propertyUnit.getValue());

		// change the property value and the see if class member gets synchronized
		propertyUnit = PropertyUtil.findPropertyUnit(propertyOwner.getProperties(), "root:valueA");
		propertyUnit.setValue("A3");
		assertEquals("A3", propertyOwner.getValueA());
		// do the same for a property handled by a different handler
		propertyUnit = PropertyUtil.findPropertyUnit(propertyOwner.getProperties(), "root:branchB:valueB");
		propertyUnit.setValue("B3");
		assertEquals("B3", propertyOwner.getPropertyOwnerB().getValueB());

		System.err.println("Done.");
	}

	@Test
	public void testPerformance() {


		int n = 1000;
		int pn = 1000;

		DummyPropertyOwner propertyOwner = new DummyPropertyOwner();
		// create a large set
		PropertyList properties = new DefaultPropertyList("root");		
		properties.add(new DefaultPropertyUnit("valueA",  "X"));
		properties.add(new DefaultPropertyUnit("valueB",   717));
		properties.add(new  DefaultPropertyUnit("valueC", true));
		for (int p = 0; p < pn / 3; p++) {
			PropertyList branch = new DefaultPropertyList("breanch" + p);			
			branch.add(new DefaultPropertyUnit("valueA",  "X"));
			branch.add(new DefaultPropertyUnit("valueB",   717));
			branch.add(new  DefaultPropertyUnit("valueC", true));
			properties.add(branch);
		}
		System.err.println("Performance test with a property-list containing " + pn + " property-units...");
		long start = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			// bind 
			propertyOwner.setProperties(properties);
		}
		long duration = System.currentTimeMillis() - start;
		System.err.println(" * " +n + " times binding took: " + duration + " ms");
		assertTrue(duration < 5000);
		start = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			// bind 
			propertyOwner.loadAll();
		}
		duration = System.currentTimeMillis() - start;
		System.err.println(" * " +n + " times loading took: " + duration + " ms");
		assertTrue(duration < 200);
		start = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			// bind 
			propertyOwner.saveAll();
		}
		duration = System.currentTimeMillis() - start;
		System.err.println(" * " +n + " times saving took: " + duration + " ms");
		assertTrue(duration < 200);
		assertEquals("X", propertyOwner.getValueA());
		assertEquals(717, propertyOwner.getValueB());
		assertEquals(true, propertyOwner.getValueC());
		System.err.println("Done.");
	}


}
