package JarPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.testng.TestNG;



public class PartnerValidation {

    public static TestNG test ;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	
		String[] array = args;
	
			
			List<String> file = new ArrayList<String>();
			file.add(array[0]);
			
			 test = new TestNG();

	         test.setTestSuites(file);
			
			 test.run();


	}

}
