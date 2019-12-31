/*
 * Copyright 2019 Key Bridge. All rights reserved. Use is subject to license
 * terms.
 *
 * This software code is protected by Copyrights and remains the property of
 * Key Bridge and its suppliers, if any. Key Bridge reserves all rights in and to
 * Copyrights and no license is granted under Copyrights in this Software
 * License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request by sending an email to info@keybridgewireless.com.
 *
 * All information contained herein is the property of Key Bridge and its
 * suppliers, if any. The intellectual and technical concepts contained herein
 * are proprietary.
 */
package ch.keybridge.faces;

import java.awt.Color;

/**
 * Simple text color calculator. Determines whether a black or white foreground
 * color should be used based on a background color.
 * <p>
 * For maximum legibility, you want maximum brightness contrast without getting
 * into hues which don't work together. The most consistent way to do this is to
 * stick with black or white for the text color. You might be able to come up
 * with more aesthetically pleasing schemes, but none of them will be more
 * legible.
 * <p>
 * To pick between black or white, you need to know the brightness of the
 * background. This gets a little more complicated, due to two factors:
 * <p>
 * The perceived brightness of the individual primaries red, green, and blue are
 * not identical. The quickest advice I can give is to use the traditional
 * formula to convert RGB to gray - R*0.299 + G*0.587 + B*0.114. There are lots
 * of other formulas.
 * <p>
 * The gamma curve applied to displays makes the middle gray value higher than
 * you'd expect. This is easily solved by using 186 as the middle value rather
 * than 128. Anything less than 186 should use white text, anything greater than
 * 186 should use black text.
 *
 * @see
 * <a href="https://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color">Html
 * color selection</a>
 * @see <a href="https://www.w3.org/TR/WCAG20/#relativeluminancedef">relative
 * luminance</a>
 * @see
 * <a href="https://en.wikipedia.org/wiki/Luma_(video)#Rec._601_luma_versus_Rec._709_luma_coefficients">ITU-R
 * recommendation BT.709</a>
 * @author Key Bridge
 * @since v5.0.0 created 12/30/19
 */
public class TextColor {

  /**
   * Determine if the font color should be dark (normal) or light (inverted)
   * depending upon the provided background color.
   *
   * @param bgColor the background color
   * @return the best font color; black or white
   */
  public static boolean isDarkText(String bgColor) {
    return relativeLuminance(Color.decode(bgColor)) > 0.179;
  }

  /**
   * Determine which font color depending on background color. This is a quick
   * approximation and does not follow W3C Recommendations. The threshold of 186
   * is based on theory, but can be adjusted to taste. Based on the comments
   * below a threshold of 150 may work better for you.
   *
   * @param bgColor the background color
   * @return the best font color; black or white
   */
  public static String byIntensity(String bgColor) {
    Color color = Color.decode(bgColor);
    double relativeIntensity = color.getRed() * 0.299 + color.getGreen() * 0.0587 + color.getBlue() * 0.114;
//    return relativeIntensity > 186
    return relativeIntensity > 150
           ? "#000000"
           : "#ffffff";
  }

  /**
   * Determine which font color depending on background color. This strategy
   * follows W3C Recommendations.
   *
   * @param bgColor the background color
   * @return the best font color; black or white
   */
  public static String byLuminance(String bgColor) {
    return relativeLuminance(Color.decode(bgColor)) > 0.179
           ? "#000000"
           : "#ffffff";
  }

  /**
   * Calculate relative luminance.
   * <p>
   * The relative brightness of any point in a colorspace, normalized to 0 for
   * darkest black and 1 for lightest white.
   * <p>
   * The change in the luma coefficients is to provide the "theoretically
   * correct" coefficients that reflect the corresponding standard
   * chromaticities ('colors') of the primaries red, green, and blue.
   * <p>
   * The formula given for contrast in the W3C Recommendations is (L1 + 0.05) /
   * (L2 + 0.05), where L1 is the luminance of the lightest color and L2 is the
   * luminance of the darkest on a scale of 0.0-1.0. The luminance of black is
   * 0.0 and white is 1.0, so substituting those values lets you determine the
   * one with the highest contrast. If the contrast for black is greater than
   * the contrast for white, use black, otherwise use white. Given the luminance
   * of the color you're testing as L the test becomes:
   * {@code  if (L + 0.05) / (0.0 + 0.05) > (1.0 + 0.05) / (L + 0.05) use #000000 else use #ffffff}
   * This simplifies down algebraically to:
   * {@code if L > sqrt(1.05 * 0.05) - 0.05} Or approximately:
   * {@code if L > 0.179 use #000000 else use #ffffff}
   *
   * @param color A java AWT color
   * @return the relative luminance coefficient
   * @see <a href="https://www.w3.org/TR/WCAG20/#relativeluminancedef">relative
   * luminance</a>
   * @see
   * <a href="https://en.wikipedia.org/wiki/Luma_(video)#Rec._601_luma_versus_Rec._709_luma_coefficients">ITU-R
   * recommendation BT.709</a>
   *
   */
  private static double relativeLuminance(Color color) {
    double L = 0.2126 * lComponent(color.getRed()) + 0.7152 * lComponent(color.getGreen()) + 0.0722 * lComponent(color.getBlue());
    return L;
  }

  /**
   * For the sRGB colorspace, the relative luminance of a color is calculate
   * where the RGB color component is defined as
   * {@code if RsRGB &lte 0.03928 then R = RsRGB/12.92 else R = ((RsRGB+0.055)/1.055) ^ 2.4}
   * where RsRGB = R8bit/255.
   *
   * @param r8bit the 8-bit color value (0 to 255)
   * @return the luminance color component
   */
  private static double lComponent(int r8bit) {
    double cRgb = r8bit / 255.0;
    if (cRgb <= 0.03928) {
      return cRgb / 12.92;
    } else {
      return Math.pow((cRgb + 0.055) / 1.055, 2.4);
    }
  }
}
