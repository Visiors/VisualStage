package com.visiors.visualstage.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseGraphException extends RuntimeException {

	private final boolean DEBUG = true;
	private final boolean LOG = true;

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseGraphException.class);

	public BaseGraphException(String msg) {

		super(msg);

		if (LOG) {

			LOGGER.error(msg);
		}
	}

	public BaseGraphException(String msg, Throwable cause) {

		super(msg, cause);

		if (DEBUG) {
			cause.printStackTrace();
		}
		if (LOG) {
			LOGGER.error(msg, cause);
		}
	}

}
