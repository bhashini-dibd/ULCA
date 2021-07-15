package com.ulca.dataset.kakfa;

import java.io.File;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.ulca.dataset.model.Error;

@Service
public interface DatasetValidateIngest {

	default Error validateFileExistence(Map<String, String> fileMap) {

		Error error = null;
		
		String baseLocation = fileMap.get("baseLocation");
		
		String paramsFileLocation = baseLocation + File.separator + "params.json";
		

		if (!isFileAvailable(paramsFileLocation)) {

			error = new Error();
			error.setCause("params.json file not available");
			error.setMessage("params.json file not available");
			error.setCode("1000_PARAMS_JSON_FILE_NOT_AVAILABLE");
			return error;

		}

		String dataFileLocation = baseLocation + File.separator + "data.json";
		if (!isFileAvailable(dataFileLocation)) {
			error = new Error();
			error.setCause("data.json file not available");
			error.setMessage("data.json file not available");
			error.setCode("1000_DATA_JSON_FILE_NOT_AVAILABLE");

			return error;

		}

		return error;
	}

	public default JSONObject deepMerge(JSONObject source, JSONObject target) throws JSONException {
		for (String key : JSONObject.getNames(source)) {
			Object value = source.get(key);
			if (!target.has(key)) {
				// new value for "key":
				target.put(key, value);
			} else {
				// existing value for "key" - recursively deep merge:
				if (value instanceof JSONObject) {
					JSONObject valueJson = (JSONObject) value;
					deepMerge(valueJson, target.getJSONObject(key));
				} else {
					target.put(key, value);
				}
			}
		}
		return target;
	}

	public default boolean isFileAvailable(String filePath) {
		File f = new File(filePath);
		
        // Check if the specified file
        // Exists or not
        if (f.exists()) {
        	return true;
        }
        
        return false;
	}
}
