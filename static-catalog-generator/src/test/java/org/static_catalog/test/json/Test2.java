package org.static_catalog.test.json;

import liqp.Template;

public class Test2 {

	public static class Foo {
        public String a = "A";
    }

	public class Foo2 {
        public String a = "A";
    }

	public void run() {
		
//		Map<String, Object> data = new HashMap<String, Object>();
        //data.put("foo", "{ \"foo\": { \"a\": \"aa\" }}");
        //data.put("{ \"foo\": { \"a\": \"aa\" }}");
        Template fooAt = Template.parse("{{foo.a}}");

        String fooA = fooAt.render("{ \"foo\": { \"a\": \"aa\" }}");
        
        System.out.println(fooA);
		
	}

	
	public static void main(String[] args) {

		
//		Map<String, Object> data = new HashMap<String, Object>();
//        data.put("foo", new Test2.Foo());
//        String fooA = Template.parse("s {{foo.a}}").render(data);
//
//        System.out.println(fooA);

        Test2 test2 = new Test2();
        test2.run();
        
	}
	

}
