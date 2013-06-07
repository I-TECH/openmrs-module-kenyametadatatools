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
 * Tests for {@link MflSyncUtils}
 */
public class MflSyncUtilsTest {

	/**
	 * @see MflSyncUtils#hash(String...)
	 */
	@Test
	public void hash_shouldComputeSuitableHash() {
		Assert.assertThat(MflSyncUtils.hash("qwerty", "dvorak"), is(MflSyncUtils.hash("qwerty", "dvorak")));
		Assert.assertThat(MflSyncUtils.hash("qwerty", "dvorak"), is(not(MflSyncUtils.hash("dvorak", "qwerty"))));
		Assert.assertThat(MflSyncUtils.hash("qwerty", null), is(not(MflSyncUtils.hash(null, "qwerty"))));
	}
}
