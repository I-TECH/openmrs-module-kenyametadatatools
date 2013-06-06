package org.openmrs.module.kenyamflsync;

import org.openmrs.LocationAttributeType;

public class SynchronizationOptions {

	private LocationAttributeType attributeType;

	private String spreadsheetUrl;

	public SynchronizationOptions() {
		this.spreadsheetUrl = KenyaMflSyncConstants.DEFAULT_SPREADSHEET_URL;
	}

	public LocationAttributeType getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(LocationAttributeType attributeType) {
		this.attributeType = attributeType;
	}

	public String getSpreadsheetUrl() {
		return spreadsheetUrl;
	}

	public void setSpreadsheetUrl(String spreadsheetUrl) {
		this.spreadsheetUrl = spreadsheetUrl;
	}
}