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

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

/**
 * Tests for {@link KenyaMflSyncUtils}
 */
public class KenyaMflSyncUtilsTest {

	/**
	 * @see KenyaMflSyncUtils#hash(String...)
	 */
	@Test
	public void hash_shouldComputeSuitableHash() {
		Assert.assertThat(KenyaMflSyncUtils.hash("qwerty", "dvorak"), is(KenyaMflSyncUtils.hash("qwerty", "dvorak")));
		Assert.assertThat(KenyaMflSyncUtils.hash("qwerty", "dvorak"), is(not(KenyaMflSyncUtils.hash("dvorak", "qwerty"))));
		Assert.assertThat(KenyaMflSyncUtils.hash("qwerty", null), is(not(KenyaMflSyncUtils.hash(null, "qwerty"))));
	}
}
