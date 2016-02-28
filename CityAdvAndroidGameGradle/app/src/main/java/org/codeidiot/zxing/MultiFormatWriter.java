/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codeidiot.zxing;


import java.util.Hashtable;

import org.codeidiot.zxing.common.BitMatrix;
import org.codeidiot.zxing.qrcode.QRCodeWriter;

/**
 * This is a factory class which finds the appropriate Writer subclass for the BarcodeFormat
 * requested and encodes the barcode with the supplied contents.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class MultiFormatWriter implements Writer {

  public BitMatrix encode(String contents, BarcodeFormat format, int width,
      int height) throws WriterException {

    return encode(contents, format, width, height, null);
  }

  public BitMatrix encode(String contents, BarcodeFormat format, int width, int height,
      Hashtable hints) throws WriterException {

    Writer writer = new QRCodeWriter();
    if (format == BarcodeFormat.EAN_8) {
    } else if (format == BarcodeFormat.EAN_13) {
    } else if (format == BarcodeFormat.QR_CODE) {
    } else if (format == BarcodeFormat.CODE_39) {
    } else if (format == BarcodeFormat.CODE_128) {
    } else if (format == BarcodeFormat.ITF) {
    } else {
      throw new IllegalArgumentException("No encoder available for format " + format);
    }
    return writer.encode(contents, format, width, height, hints);
  }

}
