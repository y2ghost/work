package study.ywork.doc.util;

import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import study.ywork.doc.exception.CryptException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文档工具
 */
public class DocUtils {
    private static final Logger log = LoggerFactory.getLogger(DocUtils.class);

    // 加密WORD文档(DOC格式)
    public static void encryptDOC(InputStream in, OutputStream out, String password) {
        try (POIFSFileSystem fs = new POIFSFileSystem(in)) {
            HWPFDocument doc = new HWPFDocument(fs);
            Biff8EncryptionKey.setCurrentUserPassword(password);
            doc.write(out);
            doc.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new CryptException("加密DOC文档失败");
        }
    }

    // 加密WORD文档(XLS格式)
    public static void encryptXLS(InputStream in, OutputStream out, String password) {
        try (POIFSFileSystem fs = new POIFSFileSystem(in)) {
            HSSFWorkbook xls = new HSSFWorkbook(fs);
            Biff8EncryptionKey.setCurrentUserPassword(password);
            xls.write(out);
            xls.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new CryptException("加密XLS文档失败");
        }
    }

    // 加密WORD文档(PPT格式)
    public static void encryptPPT(InputStream in, OutputStream out, String password) {
        try (POIFSFileSystem fs = new POIFSFileSystem(in)) {
            HSLFSlideShow ppt = new HSLFSlideShow(fs);
            Biff8EncryptionKey.setCurrentUserPassword(password);
            ppt.write(out);
            ppt.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new CryptException("加密PPT文档失败");
        }
    }

    // 加密PDF文档
    public static void encryptPDF(InputStream in, OutputStream out, String password) {
        try {
            PdfReader reader = new PdfReader(in);
            WriterProperties writerProperties = new WriterProperties().setStandardEncryption(password.getBytes(),
                    password.getBytes(), EncryptionConstants.EMBEDDED_FILES_ONLY,
                    EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA);
            PdfWriter writer = new PdfWriter(out, writerProperties);
            PdfDocument pdf = new PdfDocument(reader, writer);
            pdf.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new CryptException("加密PDF文档失败");
        }
    }

    // 加密XML文档(DOCX, XLSX, PPTX)
    public static void encryptXML(InputStream in, OutputStream out, String password) {
        try (POIFSFileSystem fs = new POIFSFileSystem()) {
            EncryptionInfo encryptionInfo = new EncryptionInfo(EncryptionMode.agile);
            Encryptor encryptor = encryptionInfo.getEncryptor();
            encryptor.confirmPassword(password);

            try (OPCPackage opc = OPCPackage.open(in); OutputStream xmlOut = encryptor.getDataStream(fs)) {
                opc.save(xmlOut);
            }

            fs.writeFilesystem(out);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new CryptException("加密XML文档失败");
        }
    }

    // 打包为加密的ZIP压缩包
    public static void encryptZip(String fileName, InputStream in, OutputStream out, String password) {
        try (ZipOutputStream zipOut = new ZipOutputStream(out, password.toCharArray())) {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
            zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);

            // 如果多个文件，需要循环执行下面的逻辑
            zipParameters.setFileNameInZip(fileName);
            zipOut.putNextEntry(zipParameters);
            StreamUtils.copy(in, zipOut);
            zipOut.closeEntry();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new CryptException("加密ZIP压缩包失败");
        }
    }

    // 通用加密文档接口(根据扩展名识别文档类型)
    public static void encryptFile(String extname, InputStream in, OutputStream out, String password) {
        switch (extname) {
            case "doc" -> encryptDOC(in, out, password);
            case "xls" -> encryptXLS(in, out, password);
            case "ppt" -> encryptPPT(in, out, password);
            case "pdf" -> encryptPDF(in, out, password);
            case "docx", "xlsx", "pptx" -> encryptXML(in, out, password);
            default -> {
                String err = String.format("文件类型: %s 不支持加密", extname);
                log.error(err);
                throw new CryptException(err);
            }
        }
    }

    public static InputStream decryptXls(InputStream xlsIn, String password) {
        Biff8EncryptionKey.setCurrentUserPassword(password);
        InputStream result;

        try (POIFSFileSystem fs = new POIFSFileSystem(xlsIn); Workbook book = new HSSFWorkbook(fs)) {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            book.write(byteOut);
            result = new ByteArrayInputStream(byteOut.toByteArray());
        } catch (Exception ex) {
            log.error("解密XLS文档失败: {}", ex.getMessage());
            throw new CryptException("解密XLS文档失败");
        }

        return result;
    }

    // 解密XML文档(DOCX, XLSX, PPTX)
    public static InputStream decryptXML(InputStream in, String password) {
        InputStream result;
        try (POIFSFileSystem fs = new POIFSFileSystem(in)) {
            EncryptionInfo encryptionInfo = new EncryptionInfo(fs);
            Decryptor decryptor = encryptionInfo.getDecryptor();

            if (!decryptor.verifyPassword(password)) {
                throw new CryptException("解密密码出错");
            }

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            StreamUtils.copy(decryptor.getDataStream(fs), byteOut);
            result = new ByteArrayInputStream(byteOut.toByteArray());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new CryptException("加密XML文档失败");
        }

        return result;
    }

    private DocUtils() {
    }
}
