/**
 * Copyright (C) 2011-2013 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.base.book.enums;

public enum UniBookResult {

	/** update processed on top */
	TOP, //

	/** update processed in range */
	NORMAL, //

	/** update dropped due to out of range */
	DISCARD, //

	/** invalid update request */
	ERROR, //

}
