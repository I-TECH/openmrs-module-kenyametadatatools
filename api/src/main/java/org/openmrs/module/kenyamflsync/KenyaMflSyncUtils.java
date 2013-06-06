/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyamflsync;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility methods
 */
public class KenyaMflSyncUtils {

	private static final Log log = LogFactory.getLog(KenyaMflSyncUtils.class);

	private static MessageDigest md5Digest;

	static {
		try {
			// This is only for diffing values so MD5 is fine
			md5Digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException ex) {
			log.error(ex);
		}
	}

	/**
	 * Computes a hash of a set of values
	 * @param values the input values
	 * @return the hash value
	 */
	public static String hash(String... values) {
		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			sb.append(value != null ? value : "xxxxxxxx");
		}

		md5Digest.update(sb.toString().getBytes());
		return Hex.encodeHexString(md5Digest.digest());
	}
}