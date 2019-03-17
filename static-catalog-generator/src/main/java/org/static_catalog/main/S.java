/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/** XML serialization */
public class S {

//    public static Document loadXmlFromFile(File documentFile) {
//
//        /* jdom */
//        SAXBuilder jdomBuilder = new SAXBuilder();
//
//        try {
//
//        	return jdomBuilder.build(documentFile);
//        }
//        catch (Exception exception) {
//
//            /* */
//        	return null;
//        }
//    }
//
//    public static void saveXmlToFile(Element rootElement, File documentFile) {
//
//        /* jdom */
//		Document jdomDocument = new Document(rootElement);
//		XMLOutputter jdomSerializer = new XMLOutputter(Format.getPrettyFormat());
//
//        try {
//
//    		jdomSerializer.output(jdomDocument, new FileWriter(documentFile));
//        }
//        catch (Exception exception) {
//
//            /* */
//        	return;
//        }
//    }
//
//	
//    public static String getElementChildTextValue(Element element_parent, String childName, Namespace namespace) {
//
//        String textValue = null;
//
//        Element element_child = element_parent.getChild(childName, namespace);
//        if (element_child != null) {
//
//            textValue = element_child.getText();
//        }
//
//        return textValue;
//    }
//
//    public static String getChildString(Element parentElement, String childName) {
//	
//		return parentElement.getChildText(childName);
//	}
//
//    public static Date getChildDate(Element parentElement, String childName) {
//
//		String textValue = getChildString(parentElement, childName);
//		      
//		if (textValue != null) {
//		
//			return new Date(Long.parseLong(textValue));
//		}
//		
//		return null;
//	}
//    
//    public static void putChildString(Element parentElement, String childName, String childValue) {
//
//		if (childValue == null) {
//			
//			return;
//		}
//
//    	Element childElement = new Element(childName);
//    	childElement.setText(childValue);
//    	parentElement.addContent(childElement);
//    }
//
//    public static void putChildDate(Element parentElement, String childName, Date childValue) {
//
//		if (childValue == null) {
//			
//			return;
//		}
//		
//    	Element childElement = new Element(childName);
//    	childElement.setText(Long.toString(childValue.getTime()));
//    	parentElement.addContent(childElement);
//    }
//
//    public static String getAttributeValue(Element element, String attributeName) {
//
//        String attributeValue = null;
//
//        if (element != null) {
//
//            Attribute attribute = element.getAttribute(attributeName);
//            if (attribute != null) {
//
//                attributeValue = attribute.getValue();
//            }
//        }
//
//        return attributeValue;
//    }
//
//    public static boolean hasAttributeValue(Element element, String attributeName, String attributeHasValue) {
//
//        String attributeValue = getAttributeValue(element, attributeName);
//
//        //noinspection RedundantIfStatement
//        if ((attributeValue != null) && (attributeValue.equals(attributeHasValue))) {
//
//            return true;
//        }
//
//        return false;
//    }
//
//    public static void putAttributeString(Element element, String attributeName, String attributeValue) {
//
//    	element.setAttribute(attributeName, attributeValue);
//    }
//
//	/** Loads an entire file in one string */
//	public static String loadFileInString(File file) {
//	
//	    RandomAccessFile randomAccessFile = null;
//	    String content = null;
//	
//	    try {
//	
//	        randomAccessFile = new RandomAccessFile(file, "r");
//	        byte[] buffer = new byte[(int)randomAccessFile.length()];
//	        randomAccessFile.readFully(buffer);
//	        content = new String(buffer, "utf-8");
//	        
//	        /* UTF8_BOM */
//	        if (content.startsWith("\uFEFF")) {
//	        	
//	        	content = content.substring(1);
//	        }
//	    }
//	    catch (FileNotFoundException fileNotFoundException) {
//	
//	        L.e("FileNotFoundException = " + file, fileNotFoundException);
//	    }
//	    catch (IOException ioException) {
//	
//	    	L.e("IOException = " + file, ioException);
//	    }
//	    finally {
//	
//	        try {
//	            if (randomAccessFile != null) {
//	
//	                randomAccessFile.close();
//	            }
//	        }
//	        catch (IOException ioException) {
//	
//	        	L.e("IOException in finally", ioException);
//	        }
//	    }
//	
//	    return content;
//	}

	/** Load resource */
	public static InputStream getResourceAsInputStream(String resourceName) {
		
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
	}

	/** Load text resource */
	public static String getResourceAsText(String textResourceName) {
		
		//return loadFileInString(new File(Thread.currentThread().getContextClassLoader().getResource(textResourceName).getFile()));
		return loadInputStreamInString(getResourceAsInputStream(textResourceName));
	}

	/** Load properties file */
	public static Properties loadPropertiesFile(File file) {
		
		Properties properties = new Properties();

		try {
			
			FileInputStream fileInputStream = new FileInputStream(file);
			
			try {
				
				properties.load(fileInputStream);
			}
			catch (IOException ioException) {
				
				L.e("Properties file exception", ioException);
			}
			finally {
				
				try {

					if (fileInputStream != null) {
					
						fileInputStream.close();
					}
				}
				catch (IOException ioException) {

					L.e("IOException", ioException);
				}
			}
		}
		catch (FileNotFoundException fileNotFoundException) {

			/* Temporary... */
			//L.e("Properties file exception", fileNotFoundException);
		}
		
		return properties;
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
