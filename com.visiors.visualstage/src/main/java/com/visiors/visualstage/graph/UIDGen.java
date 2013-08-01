package com.visiors.visualstage.graph;

import com.visiors.visualstage.exception.IDInvalidException;


/**
 * This class generates unique ids that can used throughout the graph framework.<br>
 * It is possible to use a different id for graph objects ; however, the method
 * {@link UIDGen} must be called in order to inform this class about the used
 * identifiers so that duplicates can be avoided.
 * 
 * 
 */
public class UIDGen {

	private final static UIDGen instance = new UIDGen();
	/*
	 * counter containing the next identifier proposal
	 */
	private long nextId = -1;
	/*
	 * buffer block size for each allocation
	 */
	private final int bufferAllocation = 100;
	/*
	 * array to keep track of external ids
	 */
	private long[] externalIDs = new long[bufferAllocation];
	/*
	 * pointer to next available field in the bufferAllocation
	 */
	private int exIdPointer;

	public static UIDGen getInstance() {
		return instance;
	}

	/**
	 * @return the next unique id.
	 */
	public synchronized long getNextId() {

		long id = nextId + 1;
		while (exists(id)) {
			id++;
		}
		nextId = id;
		return nextId;
	}

	/**
	 * Considers the given id to make sure that this id is not proposed in
	 * future.
	 * 
	 * @throws IDInvalidException
	 *             if the given id is already in use.
	 */
	public void considerExternalId(long id) {

		if (exists(id)) {
			throw new IDInvalidException("The given id '" + id + "' exsits already!");
		}
		trackExternalId(id);
	}

	/**
	 * Keeps track of external ids.
	 */
	private void trackExternalId(long id) {
		// check if the buffer needs to be extended
		if (exIdPointer >= externalIDs.length) {
			final long tmp[] = new long[externalIDs.length + bufferAllocation];
			System.arraycopy(externalIDs, 0, tmp, 0, externalIDs.length);
			externalIDs = tmp;
		}
		externalIDs[exIdPointer++] = id;
	}

	/**
	 * Checks if the given id is used.
	 */
	private boolean exists(long id) {

		if (id <= nextId) {
			return true;
		}
		for (int i = 0; i < exIdPointer; i++) {
			if (externalIDs[i] == id) {
				return true;
			}
		}
		return false;
	}
}