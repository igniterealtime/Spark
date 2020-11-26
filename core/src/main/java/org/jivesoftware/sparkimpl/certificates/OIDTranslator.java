package org.jivesoftware.sparkimpl.certificates;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.util.log.Log;

public final class OIDTranslator {

	private static final Map<String, String> OIDtoDescriptionMap = new HashMap<>();
	static {
		OIDtoDescriptionMap.put("2.5.29.1", Res.getString("2.5.29.1"));
		OIDtoDescriptionMap.put("2.5.29.2", Res.getString("2.5.29.2"));
		OIDtoDescriptionMap.put("2.5.29.3", Res.getString("2.5.29.3"));
		OIDtoDescriptionMap.put("2.5.29.4", Res.getString("2.5.29.4"));
		OIDtoDescriptionMap.put("2.5.29.5", Res.getString("2.5.29.5"));
		OIDtoDescriptionMap.put("2.5.29.6", Res.getString("2.5.29.6"));
		OIDtoDescriptionMap.put("2.5.29.7", Res.getString("2.5.29.7"));
		OIDtoDescriptionMap.put("2.5.29.8", Res.getString("2.5.29.8"));
		OIDtoDescriptionMap.put("2.5.29.9", Res.getString("2.5.29.9"));
		OIDtoDescriptionMap.put("2.5.29.10", Res.getString("2.5.29.10"));
		OIDtoDescriptionMap.put("2.5.29.11", Res.getString("2.5.29.11"));
		OIDtoDescriptionMap.put("2.5.29.12", Res.getString("2.5.29.12"));
		OIDtoDescriptionMap.put("2.5.29.13", Res.getString("2.5.29.13"));
		OIDtoDescriptionMap.put("2.5.29.14", Res.getString("2.5.29.14"));
		OIDtoDescriptionMap.put("2.5.29.15", Res.getString("2.5.29.15"));
		OIDtoDescriptionMap.put("2.5.29.16", Res.getString("2.5.29.16"));
		OIDtoDescriptionMap.put("2.5.29.17", Res.getString("2.5.29.17"));
		OIDtoDescriptionMap.put("2.5.29.18", Res.getString("2.5.29.18"));
		OIDtoDescriptionMap.put("2.5.29.19", Res.getString("2.5.29.19"));
		OIDtoDescriptionMap.put("2.5.29.20", Res.getString("2.5.29.20"));
		OIDtoDescriptionMap.put("2.5.29.21", Res.getString("2.5.29.21"));
		OIDtoDescriptionMap.put("2.5.29.22", Res.getString("2.5.29.22"));
		OIDtoDescriptionMap.put("2.5.29.23", Res.getString("2.5.29.23"));
		OIDtoDescriptionMap.put("2.5.29.24", Res.getString("2.5.29.24"));
		OIDtoDescriptionMap.put("2.5.29.25", Res.getString("2.5.29.25"));
		OIDtoDescriptionMap.put("2.5.29.26", Res.getString("2.5.29.26"));
		OIDtoDescriptionMap.put("2.5.29.27", Res.getString("2.5.29.27"));
		OIDtoDescriptionMap.put("2.5.29.28", Res.getString("2.5.29.28"));
		OIDtoDescriptionMap.put("2.5.29.29", Res.getString("2.5.29.29"));
		OIDtoDescriptionMap.put("2.5.29.30", Res.getString("2.5.29.30"));
		OIDtoDescriptionMap.put("2.5.29.31", Res.getString("2.5.29.31"));
		OIDtoDescriptionMap.put("2.5.29.32", Res.getString("2.5.29.32"));
		OIDtoDescriptionMap.put("2.5.29.33", Res.getString("2.5.29.33"));
		OIDtoDescriptionMap.put("2.5.29.34", Res.getString("2.5.29.34"));
		OIDtoDescriptionMap.put("2.5.29.35", Res.getString("2.5.29.35"));
		OIDtoDescriptionMap.put("2.5.29.36", Res.getString("2.5.29.36"));
		OIDtoDescriptionMap.put("2.5.29.37", Res.getString("2.5.29.37"));
		OIDtoDescriptionMap.put("2.5.29.38", Res.getString("2.5.29.38"));
		OIDtoDescriptionMap.put("2.5.29.39", Res.getString("2.5.29.39"));
		OIDtoDescriptionMap.put("2.5.29.40", Res.getString("2.5.29.40"));
		OIDtoDescriptionMap.put("2.5.29.41", Res.getString("2.5.29.41"));
		OIDtoDescriptionMap.put("2.5.29.42", Res.getString("2.5.29.42"));
		OIDtoDescriptionMap.put("2.5.29.43", Res.getString("2.5.29.43"));
		OIDtoDescriptionMap.put("2.5.29.44", Res.getString("2.5.29.44"));
		OIDtoDescriptionMap.put("2.5.29.45", Res.getString("2.5.29.45"));
		OIDtoDescriptionMap.put("2.5.29.46", Res.getString("2.5.29.46"));
		OIDtoDescriptionMap.put("2.5.29.47", Res.getString("2.5.29.47"));
		OIDtoDescriptionMap.put("2.5.29.48", Res.getString("2.5.29.48"));
		OIDtoDescriptionMap.put("2.5.29.49", Res.getString("2.5.29.49"));
		OIDtoDescriptionMap.put("2.5.29.50", Res.getString("2.5.29.50"));
		OIDtoDescriptionMap.put("2.5.29.51", Res.getString("2.5.29.51"));
		OIDtoDescriptionMap.put("2.5.29.52", Res.getString("2.5.29.52"));
		OIDtoDescriptionMap.put("2.5.29.53", Res.getString("2.5.29.53"));
		OIDtoDescriptionMap.put("2.5.29.54", Res.getString("2.5.29.54"));
		OIDtoDescriptionMap.put("2.5.29.55", Res.getString("2.5.29.55"));
		OIDtoDescriptionMap.put("2.5.29.56", Res.getString("2.5.29.56"));
		OIDtoDescriptionMap.put("2.5.29.57", Res.getString("2.5.29.57"));
		OIDtoDescriptionMap.put("2.5.29.58", Res.getString("2.5.29.58"));
		OIDtoDescriptionMap.put("2.5.29.59", Res.getString("2.5.29.59"));
		OIDtoDescriptionMap.put("2.5.29.60", Res.getString("2.5.29.60"));
		OIDtoDescriptionMap.put("2.5.29.61", Res.getString("2.5.29.61"));
		OIDtoDescriptionMap.put("2.5.29.62", Res.getString("2.5.29.62"));
		OIDtoDescriptionMap.put("2.5.29.63", Res.getString("2.5.29.63"));
		OIDtoDescriptionMap.put("2.5.29.64", Res.getString("2.5.29.64"));
		OIDtoDescriptionMap.put("2.5.29.65", Res.getString("2.5.29.65"));
		OIDtoDescriptionMap.put("2.5.29.66", Res.getString("2.5.29.66"));
		OIDtoDescriptionMap.put("2.5.29.67", Res.getString("2.5.29.67"));
		OIDtoDescriptionMap.put("2.5.29.68", Res.getString("2.5.29.68"));
		OIDtoDescriptionMap.put("2.5.29.69", Res.getString("2.5.29.69"));
		
		OIDtoDescriptionMap.put("2.16.840.1.113730.1.1", Res.getString("2.16.840.1.113730.1.1"));
		OIDtoDescriptionMap.put("1.3.6.1.4.1.311.20.2", Res.getString("1.3.6.1.4.1.311.20.2"));
		OIDtoDescriptionMap.put("1.3.6.1.4.1.311.21.1", Res.getString("1.3.6.1.4.1.311.21.1"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.2", Res.getString("1.3.6.1.5.5.7.3.2"));
		OIDtoDescriptionMap.put("1.2.840.113533.7.65.0", Res.getString("1.2.840.113533.7.65.0"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.1.1", Res.getString("1.3.6.1.5.5.7.1.1"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.1.12", Res.getString("1.3.6.1.5.5.7.1.12"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.1", Res.getString("1.3.6.1.5.5.7.3.1"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.3", Res.getString("1.3.6.1.5.5.7.3.3"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.4", Res.getString("1.3.6.1.5.5.7.3.4"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.5", Res.getString("1.3.6.1.5.5.7.3.5"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.6", Res.getString("1.3.6.1.5.5.7.3.6"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.7", Res.getString("1.3.6.1.5.5.7.3.7"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.8", Res.getString("1.3.6.1.5.5.7.3.8"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.9", Res.getString("1.3.6.1.5.5.7.3.9"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.10", Res.getString("1.3.6.1.5.5.7.3.10"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.11", Res.getString("1.3.6.1.5.5.7.3.11"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.12", Res.getString("1.3.6.1.5.5.7.3.12"));
		OIDtoDescriptionMap.put("1.3.6.1.5.5.7.3.13", Res.getString("1.3.6.1.5.5.7.3.13"));
		OIDtoDescriptionMap.put("1.3.6.1.4.1.311.10.3.4", Res.getString("1.3.6.1.4.1.311.10.3.4"));
		OIDtoDescriptionMap.put("2.23.42.7.0", Res.getString("2.23.42.7.0"));
	}

	public static String getDescription(String oid) {
		if (OIDtoDescriptionMap.containsKey(oid)) {
			return OIDtoDescriptionMap.get(oid);
		} else {
			Log.warning("Unknown description for  Object ID (OID: " + oid + ")");
			return Res.getString("cert.unknown.oid");
		}
	}
}
