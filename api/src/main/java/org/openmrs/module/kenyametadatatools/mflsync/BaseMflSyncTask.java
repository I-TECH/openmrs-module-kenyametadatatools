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

package org.openmrs.module.kenyametadatatools.mflsync;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyametadatatools.task.BaseTask;
import org.openmrs.module.kenyametadatatools.task.TaskEngine;

import java.util.*;

/**
 * Base class for synchronize tasks
 */
public abstract class BaseMflSyncTask extends BaseTask {

	protected int mflCodeAttrTypeId;

	protected Map<String, Integer> mflCodeCache = new HashMap<String, Integer>();

	protected Set<Integer> notSyncedLocations = new HashSet<Integer>();

	protected int createdCount = 0;

	protected int updatedCount = 0;

	protected int retiredCount = 0;

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

			// Examine existing locations
			for (Location loc : Context.getLocationService().getAllLocations(true)) {
				List<LocationAttribute> mfcAttrs = loc.getActiveAttributes(mfcAttrType);

				if (mfcAttrs.size() == 0) {
					TaskEngine.log("Ignoring location '" + loc.getName() + "' with no MFL code");
				}
				else if (mfcAttrs.size() > 1) {
					TaskEngine.log("Ignoring location '" + loc.getName() + "' with multiple MFL codes");
				}
				else {
					String mflCode = (String) mfcAttrs.get(0).getValue();

					// Check there isn't another location with this code
					if (lookupMflCodeCache(mflCode) != null) {
						TaskEngine.log("Ignoring location '" + loc.getName() + "' with duplicate MFL code " + mflCode);
					}
					else {
						updateMflCodeCache(mflCode, loc);
						notSyncedLocations.add(loc.getLocationId());
					}
				}
			}

			TaskEngine.log("Loaded " + mflCodeCache.size() + " existing locations with MFL codes");

			// Delegate to the sub-class to do the actual import
			doImport();

			// Retire locations that weren't in the MFL
			for (Integer notSyncedLocId : notSyncedLocations) {
				Location notSyncedLoc = Context.getLocationService().getLocation(notSyncedLocId);
				if (!notSyncedLoc.getRetired()) {
					notSyncedLoc.setRetired(true);
					notSyncedLoc.setRetiredBy(Context.getAuthenticatedUser());
					notSyncedLoc.setRetireReason("No longer in MFL");
					Context.getLocationService().saveLocation(notSyncedLoc);

					TaskEngine.log("Retired existing location '" + notSyncedLoc.getName() + "'");
					retiredCount++;
				}
			}

			// Log sync statistics
			TaskEngine.log("Synchronization complete:");
			TaskEngine.log(" * Created " + getCreatedCount() + " new locations");
			TaskEngine.log(" * Updated " + getUpdatedCount() + " existing locations");
			TaskEngine.log(" * Retired " + getRetiredCount() + " locations no longer in MFL");
			TaskEngine.log(" * Database contains " + Context.getLocationService().getAllLocations(false).size() + " non-retired locations");
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
	 * Marks a location as synced
	 * @param location the location
	 */
	protected void markLocationSynced(Location location) {
		notSyncedLocations.remove(location.getLocationId());
	}

	/**
	 * Gets the location attribute type for MFL codes
	 * @return the attribute type
	 */
	protected LocationAttributeType getMflCodeAttributeType() {
		return Context.getLocationService().getLocationAttributeType(mflCodeAttrTypeId);
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

	/**
	 * Gets the count of retired locations
	 * @return the count
	 */
	public int getRetiredCount() {
		return retiredCount;
	}
}