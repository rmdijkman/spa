import nl.tue.spa.executor.r.RProcessor;

/**
 *	To use R, the environment variables must be set correctly:
 *	R_HOME must be set (e.g. C:/Program Files/R/R-3.1.2)
 *  R_LIBS_USER may need to be set, if it does not point to the location where the R libraries are installed (e.g. G:/R/win-library/3.1)
 *	PATH must be set to include:
 *  - the folder that contains the appropriate binary file for running R, e.g. 
 *  	C:/Program Files/R/R-3.1.2/bin/x64)
 *  	/Library/Frameworks/R.framework/Resources
 *  - the folder that contains the binary library for JRI, e.g.: 
 *  	G:\R\win-library\3.1\rJava\jri\x64
 *  	/Library/Frameworks/R.framework/Resources/library/rJava/jri
 *    You can set this as an environment variable, e.g.:
 *    -Djava.library.path=/Library/Frameworks/R.framework/Resources/library/rJava/jri
 *    
 *  This requires that R is installed.
 *   
 *  JRI must also be installed. This can be done by running the following command inside of R:
 *  install.packages("rJava")  
 *
 *	jsonlite must also be installed:
 *	install.packages("jsonlite")
 *
 */
public class RTest {

	public static void main(String[] args) {
		//System.load("/Library/Frameworks/R.framework/Resources/library/rJava/jri/libjri.jnilib");
		System.out.println(System.getProperty("java.library.path"));
		System.out.println(RProcessor.getRProcessor().evaluate("1+1"));
	}

}
