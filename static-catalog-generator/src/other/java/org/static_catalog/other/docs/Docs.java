package org.static_catalog.other.docs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.static_catalog.main.S;

public class Docs {

	/** */
	//private final static String loc = "../../../../../../../../";
	// about:config
	// privacy.file_unique_origin

	/** */
	public static void main(String[] args) {

		Docs docs = new Docs();
		docs.run();
	}

	/** */
	private void pr(String message) {
		
		System.out.println(message);
	}
	
	/** */
	private void run() {

		String s = File.separator;
		Path currentRelativePath = Paths.get("");
		String srcLoc = currentRelativePath.toAbsolutePath().toString() + s + "src-docs" + s;
		String tLoc = srcLoc + "templates" + s;
		String siteloc = srcLoc + "static-catalog.org" + s;
		String docsLoc = siteloc + "docs" + s;
		
				
				

		pr(siteloc);
		
		String headerFooterT = S.loadFileInString(tLoc + "hf-t.html"); 
		
		String header = headerFooterT.substring(0, headerFooterT.indexOf("<!-- 8< 1 -->"));
		String footer = headerFooterT.substring(headerFooterT.indexOf("<!-- 8< 2 -->"));
		//pr(footer);
		
		String indexT = S.loadFileInString(tLoc + "index-t.html");
		indexT = indexT.substring(indexT.indexOf("<!-- 8< 1 -->"), indexT.indexOf("<!-- 8< 2 -->"));
		String index = header + indexT + footer;
		S.saveStringToFile(index, siteloc + "index.html");
		
		String faqT = S.loadFileInString(tLoc + "faq-t.html");
		faqT = faqT.substring(faqT.indexOf("<!-- 8< 1 -->"), faqT.indexOf("<!-- 8< 2 -->"));
		String faq = header + faqT + footer;
		faq = faq.replace("<a class=\"item\" href=\"faq.html\">", "<a class=\"active item\" href=\"faq.html\">");
		S.saveStringToFile(faq, siteloc + "faq.html");

		String screenshotsT = S.loadFileInString(tLoc + "screenshots-t.html");
		screenshotsT = screenshotsT.substring(screenshotsT.indexOf("<!-- 8< 1 -->"), screenshotsT.indexOf("<!-- 8< 2 -->"));
		String screenshots = header + screenshotsT + footer;
		screenshots = screenshots.replace("<a class=\"item\" href=\"screenshots.html\">", "<a class=\"active item\" href=\"screenshots.html\">");
		S.saveStringToFile(screenshots, siteloc + "screenshots.html");

		
		String headerFooterDocsT = S.loadFileInString(tLoc + "static-catalog-docs--hf-t.html"); 
		
		String headerDocs = headerFooterDocsT.substring(0, headerFooterDocsT.indexOf("<!-- 8< 1 -->"));
		String footerDocs = headerFooterDocsT.substring(headerFooterDocsT.indexOf("<!-- 8< 2 -->"));
		
		String introductionDocsT = S.loadFileInString(tLoc + "static-catalog-docs--introduction-t.html");
		introductionDocsT = introductionDocsT.substring(introductionDocsT.indexOf("<!-- 8< 1 -->"), introductionDocsT.indexOf("<!-- 8< 2 -->"));
		String introductionDocs = headerDocs + introductionDocsT + footerDocs;
		introductionDocs = introductionDocs.replace("<a class=\"item\" href=\"static-catalog-docs--introduction.html\">", "<a class=\"active item\" href=\"static-catalog-docs--introduction.html\">");
		S.saveStringToFile(introductionDocs, docsLoc + "static-catalog-docs--introduction.html");

		String sourceDocsT = S.loadFileInString(tLoc + "static-catalog-docs--source-t.html");
		sourceDocsT = sourceDocsT.substring(sourceDocsT.indexOf("<!-- 8< 1 -->"), sourceDocsT.indexOf("<!-- 8< 2 -->"));
		String sourceDocs = headerDocs + sourceDocsT + footerDocs;
		sourceDocs = sourceDocs.replace("<a class=\"item\" href=\"static-catalog-docs--source.html\">", "<a class=\"active item\" href=\"static-catalog-docs--source.html\">");
		S.saveStringToFile(sourceDocs, docsLoc + "static-catalog-docs--source.html");

		String examineDocsT = S.loadFileInString(tLoc + "static-catalog-docs--examine-t.html");
		examineDocsT = examineDocsT.substring(examineDocsT.indexOf("<!-- 8< 1 -->"), examineDocsT.indexOf("<!-- 8< 2 -->"));
		String examineDocs = headerDocs + examineDocsT + footerDocs;
		examineDocs = examineDocs.replace("<a class=\"item\" href=\"static-catalog-docs--examine.html\">", "<a class=\"active item\" href=\"static-catalog-docs--examine.html\">");
		S.saveStringToFile(examineDocs, docsLoc + "static-catalog-docs--examine.html");

		String fieldsDocsT = S.loadFileInString(tLoc + "static-catalog-docs--fields-filters-t.html");
		fieldsDocsT = fieldsDocsT.substring(fieldsDocsT.indexOf("<!-- 8< 1 -->"), fieldsDocsT.indexOf("<!-- 8< 2 -->"));
		String fieldsDocs = headerDocs + fieldsDocsT + footerDocs;
		fieldsDocs = fieldsDocs.replace("<a class=\"item\" href=\"static-catalog-docs--fields-filters.html\">", "<a class=\"active item\" href=\"static-catalog-docs--fields-filters.html\">");
		S.saveStringToFile(fieldsDocs, docsLoc + "static-catalog-docs--fields-filters.html");

		String generateDocsT = S.loadFileInString(tLoc + "static-catalog-docs--generate-t.html");
		generateDocsT = generateDocsT.substring(generateDocsT.indexOf("<!-- 8< 1 -->"), generateDocsT.indexOf("<!-- 8< 2 -->"));
		String generateDocs = headerDocs + generateDocsT + footerDocs;
		generateDocs = generateDocs.replace("<a class=\"item\" href=\"static-catalog-docs--generate.html\">", "<a class=\"active item\" href=\"static-catalog-docs--generate.html\">");
		S.saveStringToFile(generateDocs, docsLoc + "static-catalog-docs--generate.html");

		String conclusionDocsT = S.loadFileInString(tLoc + "static-catalog-docs--conclusion-t.html");
		conclusionDocsT = conclusionDocsT.substring(conclusionDocsT.indexOf("<!-- 8< 1 -->"), conclusionDocsT.indexOf("<!-- 8< 2 -->"));
		String conclusionDocs = headerDocs + conclusionDocsT + footerDocs;
		conclusionDocs = conclusionDocs.replace("<a class=\"item\" href=\"static-catalog-docs--conclusion.html\">", "<a class=\"active item\" href=\"static-catalog-docs--conclusion.html\">");
		S.saveStringToFile(conclusionDocs, docsLoc + "static-catalog-docs--conclusion.html");
	}
}
