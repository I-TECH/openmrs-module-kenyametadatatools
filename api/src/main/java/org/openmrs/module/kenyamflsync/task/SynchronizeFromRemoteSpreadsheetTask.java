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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyamflsync.KenyaMflSyncConstants;
import org.openmrs.util.OpenmrsUtil;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Task to synchronize locations from a remote spreadsheet
 */
public class SynchronizeFromRemoteSpreadsheetTask extends BaseSynchronizeTask {

	protected URL spreadsheetUrl = null;

	protected Map<String, Integer> mflCodesToIds = new HashMap<String, Integer>();

	protected LocationAttributeType mfcAttrType;

	/**
	 * Creates task from spreadsheet URL
	 * @param spreadsheetUrl the spreadsheet URL
	 */
	public SynchronizeFromRemoteSpreadsheetTask(URL spreadsheetUrl) {
		this.spreadsheetUrl = spreadsheetUrl;
	}

	/**
	 * @see org.openmrs.module.kenyamflsync.task.BaseSynchronizeTask#doImport()
	 */
	@Override
	public void doImport() throws Exception {

		File tmpZip = null;

		mfcAttrType = Context.getLocationService().getLocationAttributeTypeByUuid(KenyaMflSyncConstants.MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE_UUID);

		try {
			log("Loading MFC to ID mappings...");

			for (Location loc : Context.getLocationService().getAllLocations()) {
				for (LocationAttribute attr : loc.getActiveAttributes(mfcAttrType)) {
					if (attr.getValue() != null) {
						mflCodesToIds.put((String) attr.getValue(), loc.getLocationId());
					}
				}
			}

			log("Downloading MFL archive. This may take a few minutes...");

			tmpZip = File.createTempFile("mfl", ".zip");

			OutputStream out = new BufferedOutputStream(new FileOutputStream(tmpZip));
			OpenmrsUtil.copyFile(spreadsheetUrl.openStream(), out);

			log("Downloaded archive " + tmpZip.getName());
			log("Looking for XLS files in archive...");

			ZipFile zipFile = new ZipFile(tmpZip);
			for (ZipEntry entry : Collections.list(zipFile.entries())) {
				if (entry.getName().toLowerCase().endsWith(".xls")) {
					log("Importing " + entry.getName() + "...");
					importXls(zipFile.getInputStream(entry));
				}
			}
		}
		finally {
			if (tmpZip != null) {
				tmpZip.delete();
			}
		}
	}

	/**
	 * Imports a MFL spreadsheet from a stream
	 * @param stream the input stream
	 * @throws IOException if an error occurred
	 */
	protected void importXls(InputStream stream) throws IOException {
		POIFSFileSystem poifs = new POIFSFileSystem(stream);
		HSSFWorkbook wbook = new HSSFWorkbook(poifs);
		HSSFSheet sheet = wbook.getSheetAt(0);

		for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); ++r) {
			HSSFRow row = sheet.getRow(r);
			String code = String.valueOf(cellValue(row.getCell(0)));
			String name = (String) cellValue(row.getCell(1));
			String province = (String) cellValue(row.getCell(2));
			String type = (String) cellValue(row.getCell(6));

			if (code != null && name != null) {
				importLocation(code, name, province, type);
			}
		}
	}

	/**
	 * Imports a location
	 * @param code the MFL code
	 * @param name the location name
	 * @param province the province
	 * @param type location type
	 */
	protected void importLocation(String code, String name, String province, String type) {
		Location location = null;

		// Look for existing location with this code
		Integer existingLocationId = mflCodesToIds.get(code);
		if (existingLocationId != null) {
			location = Context.getLocationService().getLocation(existingLocationId);
		}

		// Create new location if it doesn't exist
		if (location == null) {
			location = new Location();

			LocationAttribute mfcAttr = new LocationAttribute();
			mfcAttr.setAttributeType(mfcAttrType);
			mfcAttr.setValue(code);
			mfcAttr.setOwner(location);

			location.addAttribute(mfcAttr);

			createdCount++;
		}
		else {
			updatedCount++;
		}

		location.setName(name);
		location.setDescription(type);
		location.setCountry("Kenya");
		location.setStateProvince(province);

		location = Context.getLocationService().saveLocation(location);
		mflCodesToIds.put(code, location.getLocationId());

		Context.flushSession();
		Context.clearSession();
	}

	/**
	 * Extracts the value of the given cell
	 * @param cell the cell
	 * @return the cell value
	 */
	private static Object cellValue(HSSFCell cell) {
		return cell.getCellType() == 0 ? cell.getNumericCellValue() : cell.getStringCellValue();
	}
}