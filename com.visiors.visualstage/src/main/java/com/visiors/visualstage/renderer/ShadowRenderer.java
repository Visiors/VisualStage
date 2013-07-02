package com.visiors.visualstage.renderer;



import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;


public class ShadowRenderer {


    // size of the shadow in pixels 
    private int size = 5;
    
    // opacity of the shadow
    private float opacity = 0.5f;
    
    // color of the shadow
    private Color color = Color.BLACK;
    

    public static int shadowSize = 3;
    
    public static BufferedImage makeShadow(BufferedImage source, int size, Color color) {

    	if(size == 0)
    		return source;
    	
    	ShadowRenderer sr = new ShadowRenderer(size , 0.3f, color);
        BufferedImage imgShadow = sr.createShadow(source);
        imgShadow.getGraphics().drawImage(source, 0, 0, null);
        return imgShadow;
    }
    public static BufferedImage makeShadow(BufferedImage source) {
    	
    	if(shadowSize == 0)
    		return source;
    	
    	ShadowRenderer sr = new ShadowRenderer(shadowSize , 0.2f, Color.darkGray);
    	BufferedImage imgShadow = sr.createShadow(source);
    	imgShadow.getGraphics().drawImage(source, 0, 0, null);
    	return imgShadow;
    }
    
    /**
     * <p>Creates a default good looking shadow generator.
     * The default shadow renderer provides the following default values:
     * <ul>
     *   <li><i>size</i>: 5 pixels</li>
     *   <li><i>opacity</i>: 50%</li>
     *   <li><i>color</i>: Black</li>
     * </ul></p>
     * <p>These properties provide a regular, good looking shadow.</p>
     */
    public ShadowRenderer() {
        this(5, 0.5f, Color.BLACK);
    }
    
    public ShadowRenderer(final int size, final float opacity, final Color color) {
        setSize(size);
        setOpacity(opacity);
        setColor(color);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(final Color shadowColor) {
        if (shadowColor != null) {
            this.color = shadowColor;
        }
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(final float shadowOpacity) {
        float oldOpacity = this.opacity;
        
        if (shadowOpacity < 0.0) {
            this.opacity = 0.0f;
        } else if (shadowOpacity > 1.0f) {
            this.opacity = 1.0f;
        } else {
            this.opacity = shadowOpacity;
        }
    }

    public int getSize() {
        return size;
    }


    public void setSize(final int shadowSize) {
        int oldSize = this.size;
        
        if (shadowSize < 0) {
            this.size = 0;
        } else {
            this.size = shadowSize;
        }
  
    }

    /**
     * <p>Generates the shadow for a given picture and the current properties
     * of the renderer.</p>
     * <p>The generated image dimensions are computed as following:</p>
     * <pre>
     * width  = imageWidth  + 2 * shadowSize
     * height = imageHeight + 2 * shadowSize
     * </pre>
     * @param image the picture from which the shadow must be cast
     * @return the picture containing the shadow of <code>image</code> 
     */
    public BufferedImage createShadow(final BufferedImage image) {
    
        int shadowSize = size * 2;

        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        int dstWidth = srcWidth + shadowSize;
        int dstHeight = srcHeight + shadowSize;

        int left = size;
        int right = shadowSize - left;

        int yStop = dstHeight - right;

        int shadowRgb = color.getRGB() & 0x00FFFFFF;
        int[] aHistory = new int[shadowSize];
        int historyIdx;

        int aSum;

        BufferedImage dst = new BufferedImage(dstWidth, dstHeight,
                                              BufferedImage.TYPE_INT_ARGB);

        int[] dstBuffer = new int[dstWidth * dstHeight];
        int[] srcBuffer = new int[srcWidth * srcHeight];

        getPixels(image, 0, 0, srcWidth, srcHeight, srcBuffer);

        int lastPixelOffset = right * dstWidth;
        float hSumDivider = 1.0f / shadowSize;
        float vSumDivider = opacity / shadowSize;

        int[] hSumLookup = new int[256 * shadowSize];
        for (int i = 0; i < hSumLookup.length; i++) {
            hSumLookup[i] = (int) (i * hSumDivider);
        }

        int[] vSumLookup = new int[256 * shadowSize];
        for (int i = 0; i < vSumLookup.length; i++) {
            vSumLookup[i] = (int) (i * vSumDivider);
        }

        int srcOffset;

        // horizontal pass : extract the alpha mask from the source picture and
        // blur it into the destination picture
        for (int srcY = 0, dstOffset = left * dstWidth; srcY < srcHeight; srcY++) {

            // first pixels are empty
            for (historyIdx = 0; historyIdx < shadowSize; ) {
                aHistory[historyIdx++] = 0;
            }

            aSum = 0;
            historyIdx = 0;
            srcOffset = srcY * srcWidth;

            // compute the blur average with pixels from the source image
            for (int srcX = 0; srcX < srcWidth; srcX++) {

                int a = hSumLookup[aSum];
                dstBuffer[dstOffset++] = a << 24;   // store the alpha value only
                                                    // the shadow color will be added in the next pass

                aSum -= aHistory[historyIdx]; // substract the oldest pixel from the sum

                // extract the new pixel ...
                a = srcBuffer[srcOffset + srcX] >>> 24;
                aHistory[historyIdx] = a;   // ... and store its value into history
                aSum += a;                  // ... and add its value to the sum

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }

            // blur the end of the row - no new pixels to grab
            for (int i = 0; i < shadowSize; i++) {

                int a = hSumLookup[aSum];
                dstBuffer[dstOffset++] = a << 24;

                // substract the oldest pixel from the sum ... and nothing new to add !
                aSum -= aHistory[historyIdx];

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }
        }

        // vertical pass
        for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {

            aSum = 0;

            // first pixels are empty
            for (historyIdx = 0; historyIdx < left;) {
                aHistory[historyIdx++] = 0;
            }

            // and then they come from the dstBuffer
            for (int y = 0; y < right; y++, bufferOffset += dstWidth) {
                int a = dstBuffer[bufferOffset] >>> 24;         // extract alpha
                aHistory[historyIdx++] = a;                     // store into history
                aSum += a;                                      // and add to sum
            }

            bufferOffset = x;
            historyIdx = 0;

            // compute the blur avera`ge with pixels from the previous pass
            for (int y = 0; y < yStop; y++, bufferOffset += dstWidth) {

                int a = vSumLookup[aSum];
                dstBuffer[bufferOffset] = a << 24 | shadowRgb;  // store alpha value + shadow color

                aSum -= aHistory[historyIdx];   // substract the oldest pixel from the sum

                a = dstBuffer[bufferOffset + lastPixelOffset] >>> 24;   // extract the new pixel ...
                aHistory[historyIdx] = a;                               // ... and store its value into history
                aSum += a;                                              // ... and add its value to the sum

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }

            // blur the end of the column - no pixels to grab anymore
            for (int y = yStop; y < dstHeight; y++, bufferOffset += dstWidth) {

                int a = vSumLookup[aSum];
                dstBuffer[bufferOffset] = a << 24 | shadowRgb;

                aSum -= aHistory[historyIdx];   // substract the oldest pixel from the sum

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }
        }

        setPixels(dst, 0, 0, dstWidth, dstHeight, dstBuffer);
        return dst;
    }
    
    public static int[] getPixels(BufferedImage img, int x, int y, int w,
			int h, int[] pixels) {
		if (w == 0 || h == 0) {
			return new int[0];
		}

		if (pixels == null) {
			pixels = new int[w * h];
		} else if (pixels.length < w * h) {
			throw new IllegalArgumentException(
					"pixels array must have a length" + " >= w*h");
		}

		int imageType = img.getType();
		if (imageType == BufferedImage.TYPE_INT_ARGB
				|| imageType == BufferedImage.TYPE_INT_RGB) {
			Raster raster = img.getRaster();
			return (int[]) raster.getDataElements(x, y, w, h, pixels);
		}

		// Unmanages the image
		return img.getRGB(x, y, w, h, pixels, 0, w);
	}
    
    public static void setPixels(BufferedImage img, int x, int y, int w, int h,
			int[] pixels) {
		if (pixels == null || w == 0 || h == 0) {
			return;
		} else if (pixels.length < w * h) {
			throw new IllegalArgumentException(
					"pixels array must have a length" + " >= w*h");
		}

		int imageType = img.getType();
		if (imageType == BufferedImage.TYPE_INT_ARGB
				|| imageType == BufferedImage.TYPE_INT_RGB) {
			WritableRaster raster = img.getRaster();
			raster.setDataElements(x, y, w, h, pixels);
		} else {
			// Unmanages the image
			img.setRGB(x, y, w, h, pixels, 0, w);
		}
	}


}
