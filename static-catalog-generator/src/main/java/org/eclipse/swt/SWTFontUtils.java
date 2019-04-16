/** https://bugs.eclipse.org/bugs/attachment.cgi?id=238603 */
package org.eclipse.swt;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/** {@link SWT} font related utility methods. */
public class SWTFontUtils {
	/** Cache: mapping from SWT devices to their monospaced fonts. */
	private static final Map<Device, Font> MONOSPACED_FONTS = new HashMap<>();

	/** Constructor for the {@link SWTFontUtils} class. */
	private SWTFontUtils() {
		// Static class.
	}

	/**
	 * Returns the monospaced font for the current display. The font will
	 * automatically be disposed once the display is disposed.
	 *
	 * <p>
	 * This method is thread safe.
	 * </p>
	 *
	 * @return The monospaced font for the current display.
	 * @throws IllegalStateException If the method is not invoked from a SWT UI
	 *                               thread.
	 */
	public static Font getMonospacedFont() {
		synchronized (MONOSPACED_FONTS) {
			// Get current display.
			Display display = Display.getCurrent();
			if (display == null) {
				String msg = "Must be invoked for a SWT UI thread.";
				throw new IllegalStateException(msg);
			}

			// Forward request.
			return getMonospacedFont(display);
		}
	}

	/**
	 * Creates a monospaced font for the given display. The font will automatically
	 * be disposed once the display is disposed.
	 *
	 * <p>
	 * This method is thread safe.
	 * </p>
	 *
	 * @param display The display for which to create a monospaced font.
	 * @return A monospaced font for the given display.
	 */
	public static Font getMonospacedFont(final Display display) {
		synchronized (MONOSPACED_FONTS) {
			// Based on class 'org.eclipse.jface.resource.FontRegistry' and
			// resources 'org.eclipse.jface.resource/jfacefonts*.properties'
			// from 'org.eclipse.jface' plug-in (version 3.9.1).

			// Use cache if possible.
			Font cachedFont = MONOSPACED_FONTS.get(display);
			if (cachedFont != null)
				return cachedFont;

			// Get operating system and windowing system names.
			String os = System.getProperty("os.name");
			String ws = SWT.getPlatform();
			os = StringUtils.deleteWhitespace(os).toLowerCase(Locale.US);
			ws = StringUtils.deleteWhitespace(ws).toLowerCase(Locale.US);

			// Get names to check, in order from specific to generic.
			String[] names = { os + "_" + ws, os, "" };

			// Get font data texts for platform.
			String[] fontDataTxts = null;
			for (String name : names) {
				if (name.equals("aix")) {
					fontDataTxts = new String[] { "adobe-courier|normal|12" };
					break;

				} else if (name.equals("hp-ux")) {
					fontDataTxts = new String[] { "adobe-courier|normal|14" };
					break;

				} else if (name.equals("linux_gtk")) {
					fontDataTxts = new String[] { "Monospace|normal|10" };
					break;

				} else if (name.equals("linux")) {
					fontDataTxts = new String[] { "adobe-courier|normal|12" };
					break;

				} else if (name.equals("macosx")) {
					fontDataTxts = new String[] { "Monaco|normal|11", "Courier|normal|12", "Courier New|normal|12" };
					break;

				} else if (name.equals("sunos") || name.equals("solaris")) {
					fontDataTxts = new String[] { "adobe-courier|normal|12" };
					break;

				} else if (name.equals("windows98")) {
					fontDataTxts = new String[] { "Courier New|normal|10", "Courier|normal|10",
							"Lucida Console|normal|9" };
					break;

				} else if (name.equals("windowsnt")) {
					fontDataTxts = new String[] { "Courier New|normal|10", "Courier|normal|10",
							"Lucida Console|normal|9" };
					break;

				} else if (name.equals("windows2000")) {
					fontDataTxts = new String[] { "Courier New|normal|10", "Courier|normal|10",
							"Lucida Console|normal|9" };
					break;

				} else if (name.equals("windowsxp")) {
					fontDataTxts = new String[] { "Courier New|normal|10", "Courier|normal|10",
							"Lucida Console|normal|9" };
					break;

				} else if (name.startsWith("windows")) {
					fontDataTxts = new String[] { "Consolas|normal|10", "Courier New|normal|10" };
					break;

				} else if (name.equals("")) {
					fontDataTxts = new String[] { "Courier New|normal|10", "Courier|normal|10",
							"b&h-lucidabright|normal|9" };
					break;
				}
			}
			if (fontDataTxts == null) {
				// Can't happen, but silences a warning.
				throw new AssertionError();
			}

			// Convert texts to font data.
			FontData[] fontDatas = new FontData[fontDataTxts.length];
			for (int i = 0; i < fontDatas.length; i++) {
				// Find splitters.
				String txt = fontDataTxts[i];
				int bar2 = txt.lastIndexOf('|');
				int bar1 = txt.lastIndexOf('|', bar2 - 1);

				// Get font name.
				String name = txt.substring(0, bar1);

				// Get font style.
				String[] styles = txt.substring(bar1 + 1, bar2).split(",");
				int style = 0;
				for (String s : styles) {
					if (s.equals("normal")) {
						style |= SWT.NORMAL;
					} else if (s.equals("bold")) {
						style |= SWT.BOLD;
					} else if (s.equals("italic")) {
						style |= SWT.ITALIC;
					} else {
						throw new RuntimeException("Invalid style: " + s);
					}
				}

				// Get font height.
				int height = Integer.parseInt(txt.substring(bar2 + 1));

				// Create and add font date.
				fontDatas[i] = new FontData(name, height, style);
			}

			// Create font.
			final Font font = new Font(display, fontDatas);

			// Register dispose callback, to dispose of the font once the
			// display is disposed.
			display.disposeExec(new Runnable() {
				@Override
				public void run() {
					synchronized (MONOSPACED_FONTS) {
						MONOSPACED_FONTS.remove(display);
						font.dispose();
					}
				}
			});

			// Add to cache.
			MONOSPACED_FONTS.put(display, font);

			// Return the new font.
			return font;
		}
	}
}
