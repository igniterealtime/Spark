package de.mxro.process.internal;

public interface StreamListener {
	void onOutputLine(String line);
	void onClosed();
	void onError(Throwable t);
}
