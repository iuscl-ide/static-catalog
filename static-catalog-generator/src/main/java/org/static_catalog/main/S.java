/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** XML serialization */
public class S {

	/** Jackson */
	private static final ObjectMapper jsonEngine = new ObjectMapper();

	/** File into string */
	public static String loadFileInString(String fileName) {
		
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
		}
		catch (IOException ioException) {
			L.e("Error loading file", ioException);
		}

		return null;
	}

	/** String into file */
	public static void saveStringToFile(String source, String fileName) {

		try {
			Files.write(Paths.get(fileName), source.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException ioException) {
			L.e("Error writing file", ioException);
		}
	}

	/** JSON string to object */
	public static <T> T loadObjectFromJsonString(String jsonString, Class<T> clazz) {
		
		try {
			return (T) jsonEngine.readValue(jsonString, clazz);	
		}
		catch (JsonMappingException jsonMappingException) {
			L.e("Error mapping json", jsonMappingException);
		}
		catch (JsonProcessingException jsonProcessingException) {
			L.e("Error processing json", jsonProcessingException);
		}
		catch (IOException ioException) {
			L.e("Error loading json", ioException);
		}

		return null;
	}

	/** Object to JSON string */
	public static <T> String saveObjectToJsonString(T t) {
		
		try {
			return jsonEngine.writerWithDefaultPrettyPrinter().writeValueAsString(t);
		}
		catch (JsonProcessingException jsonProcessingException) {
			L.e("Error processing json", jsonProcessingException);
		}
		
		return null;
	}

	/** JSON file to object */
	public static <T> T loadObjectFromJsonFileName(String jsonFileName, Class<T> clazz) {
		
		return loadObjectFromJsonString(loadFileInString(jsonFileName), clazz);
	}

	/** Object to JSON file */
	public static <T> void saveObjectToJsonFileName(T t, String jsonFileName) {
		
		saveStringToFile(saveObjectToJsonString(t), jsonFileName);
	}
	
	/** Load resource */
	public static InputStream getResourceAsInputStream(String resourceName) {
		
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
	}

	/** Load text resource */
	public static String getResourceAsText(String textResourceName) {
		
		//return loadFileInString(new File(Thread.currentThread().getContextClassLoader().getResource(textResourceName).getFile()));
		return loadInputStreamInString(getResourceAsInputStream(textResourceName));
	}


	/** Loads an entire input stream in one string */
	public static String loadInputStreamInString(InputStream inputStream) {

		Reader reader = new InputStreamReader(inputStream);
		StringBuilder sb = new StringBuilder();
		char buffer[] = new char[16384];  /* read 16k blocks */
		int len; /* how much content was read? */

		try {

			while ((len = reader.read(buffer)) > 0) {

				sb.append(buffer, 0, len);
			}
		}
		catch (IOException ioException) {

			L.e("IOException", ioException);
		}
		finally {
			
			try {

				reader.close();
			}
			catch (IOException ioException) {

				L.e("IOException", ioException);
			}
		}

		return sb.toString();
	}

	/** Saves an entire input stream in a file */
    public static void saveInputStreamInFile(InputStream inputStream, File file) {

        byte buffer[] = new byte[16384];  /* read 16k blocks */
        int len; /* how much content was read? */

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            while ((len = inputStream.read(buffer)) > 0) {

                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.flush();
            fileOutputStream.close();

        }
        catch (IOException ioException) {

            L.e("IOException = " + file, ioException);
        }
    }

    /** Delete folder and contents */
    public static void deleteFolder(File file) {

        if (file.isDirectory()) {

            for (String child : file.list()) {

                deleteFolder(new File(file, child));
            }
        }

        boolean result = file.delete();  /* Delete child file or empty directory */

        //noinspection PointlessBooleanExpression
        if (result == false) {

            L.e("deleteFolder false for " + file, new Exception("File delete false"));
        }
    }
    
}
