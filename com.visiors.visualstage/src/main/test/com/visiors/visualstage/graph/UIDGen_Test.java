package com.visiors.visualstage.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.visiors.visualstage.exception.DuplicateIdentifierException;

public class UIDGen_Test {

	@Test
	public void test() {

		// generate a number of ids
		long firstID = UIDGen.getInstance().getNextId();
		int n = 100;
		for (int i = 0; i < n; i++) {
			UIDGen.getInstance().getNextId();
		}
		long currentID = UIDGen.getInstance().getNextId();
		assertEquals(currentID, firstID + n + 1);

		// expect a DuplicateIdentifierException exception for using and
		// external id that has already been used
		long exteranlId = currentID;
		Throwable caught = null;
		try {
			UIDGen.getInstance().considerExternalId(exteranlId);
		} catch (Throwable t) {
			caught = t;
		}
		assertNotNull(caught);
		assertSame(DuplicateIdentifierException.class, caught.getClass());

		// push an external id that is not used and expect no exception
		exteranlId = currentID + 2;
		try {
			UIDGen.getInstance().considerExternalId(exteranlId);
		} catch (Throwable t) {
			fail();
		}

		// expect an exception as we put the same id again
		exteranlId = currentID;
		caught = null;
		try {
			UIDGen.getInstance().considerExternalId(exteranlId);
		} catch (Throwable t) {
			caught = t;
		}
		assertNotNull(caught);
		assertSame(DuplicateIdentifierException.class, caught.getClass());

		// must skip currentID + 2 as it was defined as an external id
		assertEquals(UIDGen.getInstance().getNextId(), currentID + 1);
		assertEquals(UIDGen.getInstance().getNextId(), currentID + 3);
	}

}
