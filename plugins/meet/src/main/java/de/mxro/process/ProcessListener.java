package de.mxro.process;

/**
 * A listener to intercept outputs from the process.
 * 
 * @author Max
 * 
 */
public interface ProcessListener {

	/**
	 * When the process wrote a line to its standard output stream.
	 * 
	 * @param line
	 */
    void onOutputLine(String line);

	/**
	 * When the process wrote a line to its error output stream.
	 * 
	 * @param line
	 */
    void onErrorLine(String line);

	/**
	 * When the output stream is closed.
	 */
    void onProcessQuit(int returnValue);

	/**
	 * When an unexpected error is thrown while interacting with the process.
	 * 
	 * @param t
	 */
    void onError(Throwable t);

}
