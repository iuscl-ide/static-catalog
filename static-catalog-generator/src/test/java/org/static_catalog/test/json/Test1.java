package org.static_catalog.test.json;

import java.util.ArrayList;

import org.static_catalog.main.S;

public class Test1 {
	
	private ArrayList<String> sers;
	
//	private Test1() {
//
//		sers = new ArrayList<>();
//	}

	public ArrayList<String> getSers() {
		return sers;
	}



	public static void main(String[] args) {
		
//		Test1 test1 = new Test1();
//		test1.getSers().add("st1");
//		test1.getSers().add("st2");
		
//		StaticCatalogExamine staticCatalogExamine = S.loadObjectFromJsonFileName("C:\\Iustin\\Programming\\_static-catalog\\repositories\\static-catalog\\static-catalog-generator\\examine.json", StaticCatalogExamine.class);
//		S.saveObjectToJsonFileName(staticCatalogExamine, "C:\\Iustin\\Programming\\_static-catalog\\repositories\\static-catalog\\static-catalog-generator\\examine2.json");
		
		//S.saveObjectToJsonFileName(test1, "C:\\Iustin\\Programming\\_static-catalog\\repositories\\static-catalog\\static-catalog-generator\\test1.json");

		Test1 test1l = S.loadObjectFromJsonFileName("C:\\Iustin\\Programming\\_static-catalog\\repositories\\static-catalog\\static-catalog-generator\\test1.json", Test1.class);

		System.out.println(test1l.getSers().size() + "");
	}

}
