package sun.net.www.protocol.about;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.androdome.iadventure.PropertyManager;

public class Handler extends URLStreamHandler {
    /** The classloader to find resources from. */
    private final ClassLoader classLoader;

    public Handler() {
        this.classLoader = getClass().getClassLoader();
    }

    public Handler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
    	if(u.toString().equalsIgnoreCase("about:home"))
    		return new URL(PropertyManager.getProperty("home", "about:welcome")).openConnection();
    	String path = u.getPath();
    	if(!u.getPath().contains("."))
    		path += ".html";
    	System.out.println("/pages/"+path);
        final URL resourceUrl = classLoader.getResource("pages/"+path);
        if(resourceUrl == null)
        	throw new IOException("Failed to load file "+path);
        return resourceUrl.openConnection();
    }
}