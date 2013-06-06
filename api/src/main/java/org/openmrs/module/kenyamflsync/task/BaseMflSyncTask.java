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

package org.openmrs.module.kenyamflsync.task;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for synchronize tasks
 */
public abstract class BaseMflSyncTask extends BaseTask {

	protected int mflCodeAttrTypeId;

	protected Map<String, Integer> mflCodeCache = new HashMap<String, Integer>();

	protected int existingCount = 0;

	protected int createdCount = 0;

	protected int updatedCount = 0;

	/**
	 * Constructs new synchronization task
	 * @param mflCodeAttrType the attribute type for MFL codes
	 */
	public BaseMflSyncTask(LocationAttributeType mflCodeAttrType) {
		this.mflCodeAttrTypeId = mflCodeAttrType.getLocationAttributeTypeId();
	}

	/**
	 * Runs the import process
	 * @throws Exception if an error occurred
	 */
	protected abstract void doImport() throws Exception;

	/**
	 * Execute the synchronization task
	 */
	public final void execute() {
		try {
			TaskEngine.log("Starting location synchronization ...");

			LocationAttributeType mfcAttrType = getMflCodeAttributeType();

			for (Location loc : Context.getLocationService().getAllLocations()) {
				for (LocationAttribute attr : loc.getActiveAttributes(mfcAttrType)) {
					if (attr.getValue() != null) {
						updateMflCodeCache((String) attr.getValue(), loc);
					}
				}
			}

			existingCount = mflCodeCache.size();

			TaskEngine.log("Cached " + getExistingCount() + " existing locations with MFL codes");

			doImport();

			TaskEngine.log("Synchronization complete. Created " + getCreatedCount() + " new locations and updated " + getUpdatedCount() + " of " + getExistingCount() + " existing locations");
		}
		catch (Exception ex) {
			TaskEngine.logError("Synchronization failed");
			TaskEngine.logError(ex);
		}
	}

	/**
	 * Updates the cache of MFL codes
	 * @param code the code
	 * @param location the location
	 */
	protected void updateMflCodeCache(String code, Location location) {
		mflCodeCache.put(code, location.getLocationId());
	}

	/**
	 * Lookup a location in the cache of MFL codes
	 * @param code the code
	 * @return the location
	 */
	protected Location lookupMflCodeCache(String code) {
		Integer locationId = mflCodeCache.get(code);
		return locationId != null ? Context.getLocationService().getLocation(locationId) : null;
	}

	/**
	 * Gets the location attribute type for MFL codes
	 * @return the attribute type
	 */
	protected LocationAttributeType getMflCodeAttributeType() {
		return Context.getLocationService().getLocationAttributeType(mflCodeAttrTypeId);
	}

	/**
	 * Gets the count of existing locations
	 * @return the count
	 */
	public int getExistingCount() {
		return existingCount;
	}

	/**
	 * Gets the count of newly created locations
	 * @return the count
	 */
	public int getCreatedCount() {
		return createdCount;
	}

	/**
	 * Gets the count of updated locations
	 * @return the count
	 */
	public int getUpdatedCount() {
		return updatedCount;
	}
}