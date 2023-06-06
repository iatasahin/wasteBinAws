package com.example.waste.service;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.MultiFormatOneDReader;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class BarcodeScannerService {
    public String scan(InputStream inputStream) throws IOException, NotFoundException, FormatException {
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        BinaryBitmap binaryBitmap = convertImageToBinaryBitmap(bufferedImage);
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        Result result = new MultiFormatOneDReader(hints).decode(binaryBitmap);
        return result.getText();
    }

    protected static BinaryBitmap convertImageToBinaryBitmap(BufferedImage image) {
        int[] pixels = image.getRGB(0, 0,
                image.getWidth(), image.getHeight(),
                null, 0, image.getWidth());
        RGBLuminanceSource source = new RGBLuminanceSource(
                image.getWidth(), image.getHeight(), pixels
        );
        return new BinaryBitmap(new HybridBinarizer(source));
    }
}
