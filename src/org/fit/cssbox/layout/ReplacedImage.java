/*
 * ImgBox.java
 * Copyright (c) 2005-2007 Radek Burget
 *
 * CSSBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * CSSBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with CSSBox. If not, see <http://www.gnu.org/licenses/>.
 *
 * Created on 4. prosinec 2005, 21:01
 */

package org.fit.cssbox.layout;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;

import com.androdome.iadventure.utils.IconManager;

/**
 * This class represents an image as the contents of a replaced element
 * 
 * @author  radek
 */
public class ReplacedImage extends ReplacedContent implements ImageObserver {
	private static boolean LOAD_IMAGES = true;

	private URL base; //document base url
	private URL url; //image url
	private VisualContext ctx; //visual context
	private BufferedImage img; //the loaded image
	Graphics2D g = null;
	int width = 0;
	int height = 0;

	private boolean error = false;

	/** 
	 * Creates a new instance of ImgBox 
	 */
	public ReplacedImage(final ElementBox owner, VisualContext ctx, URL baseurl) {
		super(owner);
		this.ctx = ctx;
		this.base = baseurl;
		new Thread() {
			public void run() {
				try
				{
					String src = getOwner().getElement().getAttribute("src");
					System.out.println(base);
					url = new URL(base, src);
					if (LOAD_IMAGES)
					{
						System.err.println("Loading image: " + url);
						img = javax.imageio.ImageIO.read(url);
						if(g != null)
							draw(g, width, height);
					}
				}
				catch (MalformedURLException e)
				{
					System.err.println("ImgBox: URL: " + e.getMessage());
					img = null;
					url = null;
					error = true;
					if(g != null)
						draw(g, width, height);
				}
				catch (IOException e)
				{
					System.err.println("ImgBox: I/O: " + e.getMessage());
					img = null;
					error = true;
					if(g != null)
						draw(g, width, height);
				}
				catch (IllegalArgumentException e)
				{
					System.err.println("ImgBox: Format error: " + e.getMessage());
					img = null;
					error = true;
					if(g != null)
						draw(g, width, height);
				}
				
			}
		}.start();
	}

	/**
	 * Switches automatic image data downloading on or off.
	 * @param b when set to <code>true</code>, the images are automatically loaded
	 * from the server. When set to <code>false</code>, the images are not loaded
	 * and the corresponding box is displayed empty. When the image loading is switched
	 * off, the box size can be only determined from the element attributes or style.
	 * The default value is on.
	 */
	public static void setLoadImages(boolean b) {
		LOAD_IMAGES = b;
	}

	public static boolean getLoadImages() {
		return LOAD_IMAGES;
	}

	/**
	 * @return the url of the image
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * @return the image
	 */
	public BufferedImage getImage() {
		return img;
	}

	public void draw(Graphics2D g, int width, int height) {
		this.g = g;
		this.width = width;
		this.height = height;
		ctx.updateGraphics(g);
		if (img != null)
			g.drawImage(img, getOwner().getAbsoluteContentX(), getOwner().getAbsoluteContentY(), width, height, this);
		else if(error  == true)
		{
			g.drawImage(IconManager.getImage("/stopscale.png"),getOwner().getAbsoluteContentX()+2, getOwner().getAbsoluteContentY()+2,32,32, this);
			g.setStroke(new BasicStroke(1));
			g.drawRect(getOwner().getAbsoluteContentX(), getOwner().getAbsoluteContentY(), getOwner().getContentWidth() - 1, getOwner().getContentHeight() - 1);
			/*g.drawLine(getOwner().getAbsoluteContentX(), getOwner().getAbsoluteContentY(), getOwner().getAbsoluteContentX() + getOwner().getContentWidth() - 1, getOwner().getAbsoluteContentY() + getOwner().getContentHeight() - 1);
			g.drawLine(getOwner().getAbsoluteContentX() + getOwner().getContentWidth() - 1, getOwner().getAbsoluteContentY(), getOwner().getAbsoluteContentX(), getOwner().getAbsoluteContentY() + getOwner().getContentHeight() - 1);
			*/
		}
	}

	@Override
	public int getIntrinsicHeight() {
		if (img != null)
			return img.getHeight();
		else return 20;
	}

	@Override
	public int getIntrinsicWidth() {
		if (img != null)
			return img.getWidth();
		else return 20;
	}

	@Override
	public float getIntrinsicRatio() {
		return (float) getIntrinsicWidth() / (float) getIntrinsicHeight();
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		return false;
	}

}
