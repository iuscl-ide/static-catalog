/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Serialization, files */
public class S {

	/** Jackson */
	private static final ObjectMapper jsonEngine = new ObjectMapper();

	/** File into string */
	public static String loadFileInString(String fileName) {
		
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
		}
		catch (IOException ioException) {
			L.e("saveStringToFile => fileName: " + fileName, ioException);
			throw new E(ioException);
		}
	}

	/** String into file */
	public static void saveStringToFile(String source, String fileName) {

		try {
			Files.write(Paths.get(fileName), source.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException ioException) {
			L.e("saveStringToFile => source: " + source + ", fileName: " + fileName, ioException);
			throw new E(ioException);
		}
	}

	/** JSON string to object */
	public static <T> T loadObjectFromJsonString(String jsonString, Class<T> clazz) {
		
		try {
			return (T) jsonEngine.readValue(jsonString, clazz);	
		}
		catch (JsonMappingException jsonMappingException) {
			L.e("loadObjectFromJsonString => jsonString: " + jsonString, jsonMappingException);
			throw new E(jsonMappingException);
		}
		catch (JsonProcessingException jsonProcessingException) {
			L.e("loadObjectFromJsonString => jsonString: " + jsonString, jsonProcessingException);
			throw new E(jsonProcessingException);
		}
		catch (IOException ioException) {
			L.e("loadObjectFromJsonString => jsonString: " + jsonString, ioException);
			throw new E(ioException);
		}
	}

	/** Object to JSON string */
	public static <T> String saveObjectToJsonString(T t) {
		
		try {
			return jsonEngine.writerWithDefaultPrettyPrinter().writeValueAsString(t);
		}
		catch (JsonProcessingException jsonProcessingException) {
			L.e("saveObjectToJsonString => t: " + t, jsonProcessingException);
			throw new E(jsonProcessingException);
		}
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
		char buffer[] = new char[16384]; /* read 16k blocks */
		int len; /* how much content was read? */

		try {
			while ((len = reader.read(buffer)) > 0) {
				sb.append(buffer, 0, len);
			}
		}
		catch (IOException ioException) {
			L.e("loadInputStreamInString", ioException);
			throw new E(ioException);
		}
		finally {
			try {
				reader.close();
			}
			catch (IOException ioException) {
				L.e("loadInputStreamInString => close reader", ioException);
				throw new E(ioException);
			}
		}

		return sb.toString();
	}

	/** Saves an entire input stream in a file */
	public static void saveInputStreamInFile(InputStream inputStream, File file) {

		byte buffer[] = new byte[16384]; /* read 16k blocks */
		int len; /* how much content was read? */

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			while ((len = inputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, len);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException ioException) {
			L.e("createFoldersIfNotExists => file: " + file.getAbsolutePath(), ioException);
			throw new E(ioException);
		}
	}

	/** Delete folder contents */
	public static void deleteFolderContentsOnly(String folderName) {

		deleteFolder(folderName, true);
	}

	/** Delete folder and contents */
	public static void deleteFolder(String folderName) {

		deleteFolder(folderName, false);
	}

	/** Delete folder and/only contents */
	private static void deleteFolder(String folderName, boolean deleteContentsOnly) {

		try {
			Path rootPath = Paths.get(folderName);
			if (Files.notExists(rootPath)) {
				return;
			}
			List<Path> pathsToDelete = Files.walk(rootPath).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
			if (deleteContentsOnly) {
				pathsToDelete.remove(rootPath);
			}
			for(Path path : pathsToDelete) {
			    Files.deleteIfExists(path);
			}
		}
		catch (IOException ioException) {
			L.e("deleteFolder => folderName: " + folderName + ", deleteContentsOnly: " + deleteContentsOnly, ioException);
			throw new E(ioException);
		}
	}

	/** Create all folders */
	public static void createFoldersIfNotExists(String folderName) {

		try {
			Path catalogBlocksFolderPath = Paths.get(folderName); 
			if (Files.notExists(catalogBlocksFolderPath)) {
				Files.createDirectories(catalogBlocksFolderPath);
			}
		}
		catch (IOException ioException) {
			L.e("createFoldersIfNotExists => folderName: " + folderName, ioException);
			throw new E(ioException);
		}
	}

	/** Create all folders */
	public static long findFileSizeInBytes(String fileName) {

		long fileSize = -1; 
		try {
			fileSize = Files.size(Paths.get(fileName)); 
		}
		catch (IOException ioException) {
			L.e("findFileSizeInBytes => fileName: " + fileName, ioException);
			throw new E(ioException);
		}
		return fileSize;
	}

    /**
     * https://stackoverflow.com/questions/6214703/copy-entire-directory-contents-to-another-directory/10068306#10068306
     */
	private static class CopyFileVisitor extends SimpleFileVisitor<Path> {
		
		private final Path targetPath;
		private Path sourcePath = null;
		
		private final ArrayList<String> ignoredExtensions = new ArrayList<String>();

		public CopyFileVisitor(Path targetPath, String ignoreExtensions) {
			this.targetPath = targetPath;
			
			for (String ignoredExtension : ignoreExtensions.split(";")) {
				ignoredExtensions.add(ignoredExtension.trim());
			}
		}

		@Override
		public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
			
			if (sourcePath == null) {
				sourcePath = dir;
			}
			else {
				Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
			
			String fileExtension = getExtension(file.toString());
			if (ignoredExtensions.contains(fileExtension)) {
				return FileVisitResult.CONTINUE;
			}
			Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
			return FileVisitResult.CONTINUE;
		}
	}

	/** Copy folders */
	public static void copyFolders(Path sourcePath, Path targetPath) throws IOException {
	
		Files.walkFileTree(sourcePath, new CopyFileVisitor(targetPath, ""));
	}

	/** Copy folders */
	public static void copyFolders(String sourceFolder, String targetFolder, String ignoreForExtensions) {
		
		try {
			Files.walkFileTree(Paths.get(sourceFolder), new CopyFileVisitor(Paths.get(targetFolder), ignoreForExtensions));	
		}
		catch (IOException ioException) {
			L.e("copyFolders => sourceFolder: " + sourceFolder + ", targetFolder: " + targetFolder + ", ignoreForExtensions: " + ignoreForExtensions, ioException);
			throw new E(ioException);
		}
	}

	/** File path */
	public static String getFileFolderName(String fileName) {
		
		return (Paths.get(fileName)).getParent().toString(); 
	}
	
	/**
	 * https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java/21974043
	 */
	public static String getExtension(String fileName) {

		char ch;
		int len;
		if (fileName == null || (len = fileName.length()) == 0 || (ch = fileName.charAt(len - 1)) == '/' || ch == '\\'
				|| // in the case of a directory
				ch == '.') // in the case of . or ..
			return "";
		int dotInd = fileName.lastIndexOf('.'),
				sepInd = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if (dotInd <= sepInd)
			return "";
		else
			return fileName.substring(dotInd + 1).toLowerCase();
	}
	
	/** Size */
	public static long getFileSize(String fileName) {

		File javaFile = new File(fileName);
		return Long.valueOf(javaFile.length());
	}
	
	/** From IusCL */
	public static String formatSize(long size) {

		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		
		double b = size / (1024 * 1024 * 1024);
		String suffix = "B";
		if (b > 1) {
			suffix = "GB";
		}
		else {
			b = size / (1024 * 1024);
			if (b > 1) {
				suffix = "MB";
			}
			else {
				b = size / 1024;
				if (b > 1) {
					suffix = "KB";
				}
				else {
					return size + " B";
				}
			}
		}
		
		return decimalFormat.format(b) + " " + suffix;
	};

}
