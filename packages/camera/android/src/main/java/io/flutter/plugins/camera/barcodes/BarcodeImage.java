package io.flutter.plugins.camera.barcodes;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;

import android.util.Log;

import java.nio.ByteBuffer;

class BarcodeImage {
  int xSize;
  int ySize;

  int frameId;
  int frameRotation;
  long timestamp;

  byte[] bytes = new byte[0];
  int bytesCount = 0;

  public boolean capture(Image image) {
    if(image.getFormat() != ImageFormat.YUV_420_888)
      return false; // TODO: Handle this better.

    xSize = image.getWidth();
    ySize = image.getHeight();

    ByteBuffer lumaBuffer = image.getPlanes()[0].getBuffer();
    ByteBuffer chromaBlueBuffer = image.getPlanes()[2].getBuffer();

    // Resize our local buffer
    int lumaByteCount = lumaBuffer.remaining();
    int chromaBlueByteCount = chromaBlueBuffer.remaining();
    if(bytes.length < lumaByteCount + chromaBlueByteCount)
      bytes = new byte[lumaByteCount + chromaBlueByteCount];

    // Copy over the data
    lumaBuffer.get(bytes, 0, lumaByteCount);
    chromaBlueBuffer.get(bytes, lumaByteCount, chromaBlueByteCount);
    bytesCount = lumaByteCount + chromaBlueByteCount;

    return true;
  }

  public BarcodeImage scaledHalf() {
    final int FACTOR = 2;

    BarcodeImage result = new BarcodeImage();

    result.xSize = xSize / FACTOR;
    result.ySize = ySize / FACTOR;
    result.frameId = frameId;
    result.frameRotation = frameRotation;
    result.timestamp = timestamp;

    result.bytesCount = xSize / FACTOR * ySize / FACTOR * 3 / 2;
    result.bytes = new byte[result.bytesCount];
    for(int y = 0, index = 0; y < ySize; y += FACTOR) {
      for(int x = 0; x < xSize; x += FACTOR, index++) {
        result.bytes[index] = bytes[y * xSize + x];
      }
    }

    return result;
  }
}