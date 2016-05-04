import nl.tue.spa.executor.r.RProcessor;

/**
 *	To use R, the environment variables must be set correctly:
 *	R_HOME must be set (e.g. C:/Program Files/R/R-3.1.2)
 *	PATH must be set to include:
 *  - the folder that contains the appropriate binary file for running R (e.g. C:/Program Files/R/R-3.1.2/bin/x64)
 *  - the folder that contains the binary library for JRI (e.g. G:\R\win-library\3.1\rJava\jri\x64)
 *  This obviously requires that R is installed. 
 *  JRI must also be installed. This can be done by running the following command inside of R:
 *  install.packages("rJava")  
 *
 *	Also requires jsonlite:
 *	install.packages("jsonlite")
 *
 */
public class RTest {

	public static void main(String[] args) {
		System.out.println(RProcessor.getRProcessor().evaluate("1+1"));
	}

}
