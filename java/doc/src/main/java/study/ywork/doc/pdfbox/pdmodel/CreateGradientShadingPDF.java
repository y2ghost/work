package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.function.PDFunctionType2;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType2;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType3;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType4;

import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CreateGradientShadingPDF {
    public void create(String file) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // type 2 (exponential) function
            COSDictionary fdict = new COSDictionary();
            fdict.setInt(COSName.FUNCTION_TYPE, 2);
            COSArray domain = new COSArray();
            domain.add(COSInteger.ZERO);
            domain.add(COSInteger.ONE);
            COSArray c0 = new COSArray();
            c0.add(COSInteger.ONE);
            c0.add(COSInteger.ZERO);
            c0.add(COSInteger.ZERO);
            COSArray c1 = new COSArray();
            c1.add(COSNumber.get("0.5"));
            c1.add(COSInteger.ONE);
            c1.add(COSNumber.get("0.5"));
            fdict.setItem(COSName.DOMAIN, domain);
            fdict.setItem(COSName.C0, c0);
            fdict.setItem(COSName.C1, c1);
            fdict.setInt(COSName.N, 1);
            PDFunctionType2 func = new PDFunctionType2(fdict);

            // axial shading
            PDShadingType2 axialShading = new PDShadingType2(new COSDictionary());
            axialShading.setColorSpace(PDDeviceRGB.INSTANCE);
            axialShading.setShadingType(PDShading.SHADING_TYPE2);
            COSArray coords1 = new COSArray();
            coords1.add(COSInteger.get(100));
            coords1.add(COSInteger.get(400));
            coords1.add(COSInteger.get(400));
            coords1.add(COSInteger.get(600));
            axialShading.setCoords(coords1);
            axialShading.setFunction(func);

            // radial shading
            PDShadingType3 radialShading = new PDShadingType3(new COSDictionary());
            radialShading.setColorSpace(PDDeviceRGB.INSTANCE);
            radialShading.setShadingType(PDShading.SHADING_TYPE3);
            COSArray coords2 = new COSArray();
            coords2.add(COSInteger.get(100));
            coords2.add(COSInteger.get(400));
            coords2.add(COSInteger.get(50)); // radius1
            coords2.add(COSInteger.get(400));
            coords2.add(COSInteger.get(600));
            coords2.add(COSInteger.get(150)); // radius2
            radialShading.setCoords(coords2);
            radialShading.setFunction(func);

            // Gouraud shading
            PDShadingType4 gouraudShading = new PDShadingType4(document.getDocument().createCOSStream());
            gouraudShading.setShadingType(PDShading.SHADING_TYPE4);
            gouraudShading.setBitsPerFlag(8);
            gouraudShading.setBitsPerCoordinate(16);
            gouraudShading.setBitsPerComponent(8);
            COSArray decodeArray = new COSArray();
            // 坐标 x y 使用 16 bits 十六进制表示更加方便
            decodeArray.add(COSInteger.ZERO);
            decodeArray.add(COSInteger.get(0xFFFF));
            decodeArray.add(COSInteger.ZERO);
            decodeArray.add(COSInteger.get(0xFFFF));
            // RGB颜色 使用 8 bits
            decodeArray.add(COSInteger.ZERO);
            decodeArray.add(COSInteger.ONE);
            decodeArray.add(COSInteger.ZERO);
            decodeArray.add(COSInteger.ONE);
            decodeArray.add(COSInteger.ZERO);
            decodeArray.add(COSInteger.ONE);
            gouraudShading.setDecodeValues(decodeArray);
            gouraudShading.setColorSpace(PDDeviceRGB.INSTANCE);

            try (OutputStream os = ((COSStream) gouraudShading.getCOSObject()).createOutputStream();
                 MemoryCacheImageOutputStream mcos = new MemoryCacheImageOutputStream(os)) {
                mcos.writeByte(0);
                mcos.writeShort(0);
                mcos.writeShort(0);
                mcos.writeByte(0xFF);
                mcos.writeByte(0);
                mcos.writeByte(0);
                mcos.writeByte(0);
                mcos.writeShort(100);
                mcos.writeShort(100);
                mcos.writeByte(0);
                mcos.writeByte(0xFF);
                mcos.writeByte(0);
                mcos.writeByte(0);
                mcos.writeShort(200);
                mcos.writeShort(0);
                mcos.writeByte(0);
                mcos.writeByte(0);
                mcos.writeByte(0xFF);
            }

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, false)) {
                contentStream.shadingFill(axialShading);
                contentStream.shadingFill(radialShading);
                contentStream.shadingFill(gouraudShading);
            }

            document.save(file);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
        } else {
            CreateGradientShadingPDF creator = new CreateGradientShadingPDF();
            creator.create(args[0]);
        }
    }

    private static void usage() {
        System.err.println("usage: java " + CreateGradientShadingPDF.class.getName() + " <outputfile.pdf>");
    }
}
