package mobi.app.redis.transcoders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * User: thor
 * Date: 12-12-21
 * Time: 下午3:41
 */
public class SerializingTranscoder implements Transcoder<Object>{
    static final int SERIALIZED = 1;
    static final int COMPRESSED = 2;

    // Special flags for specially handled types.
    private static final int SPECIAL_MASK = 0xff00;
    static final int SPECIAL_BOOLEAN = (1 << 8);
    static final int SPECIAL_INT = (2 << 8);
    static final int SPECIAL_LONG = (3 << 8);
    static final int SPECIAL_DATE = (4 << 8);
    static final int SPECIAL_BYTE = (5 << 8);
    static final int SPECIAL_FLOAT = (6 << 8);
    static final int SPECIAL_DOUBLE = (7 << 8);
    static final int SPECIAL_BYTEARRAY = (8 << 8);
//    private final TranscoderUtils tu = new TranscoderUtils(false);
    @Override
    public byte[] encode(Object o) {
        byte[] b;
        int flags = 0;
        if (o instanceof String) {
            b = encodeString((String) o);
        } else if (o instanceof Long) {
            b = TranscoderUtils.encodeLong((Long) o);
            flags |= SPECIAL_LONG;
        } else if (o instanceof Integer) {
            b = TranscoderUtils.encodeInt((Integer) o);
            flags |= SPECIAL_INT;
        } else if (o instanceof Boolean) {
            b = TranscoderUtils.encodeBoolean((Boolean) o);
            flags |= SPECIAL_BOOLEAN;
        } else if (o instanceof Date) {
            b = TranscoderUtils.encodeLong(((Date) o).getTime());
            flags |= SPECIAL_DATE;
        } else if (o instanceof Byte) {
            b = TranscoderUtils.encodeByte((Byte) o);
            flags |= SPECIAL_BYTE;
        } else if (o instanceof Float) {
            b = TranscoderUtils.encodeInt(Float.floatToRawIntBits((Float) o));
            flags |= SPECIAL_FLOAT;
        } else if (o instanceof Double) {
            b = TranscoderUtils.encodeLong(Double.doubleToRawLongBits((Double) o));
            flags |= SPECIAL_DOUBLE;
        } else if (o instanceof byte[]) {
            b = (byte[]) o;
            flags |= SPECIAL_BYTEARRAY;
        } else {
            b = serialize(o);
            flags |= SERIALIZED;
        }
        assert b != null;

        if (b.length > compressionThreshold) {
            byte[] compressed = compress(b);
            if (compressed.length < b.length) {
                logger.debug(String.format("Compressed %s from %d to %d",
                        o.getClass().getName(), b.length, compressed.length));
                b = compressed;
                flags |= COMPRESSED;
            } else {
                logger.info(String.format("Compression increased the size of %s from %d to %d",
                        o.getClass().getName(), b.length, compressed.length));
            }
        }
        byte[] fullData = new byte[4 + b.length];
        byte[] flagBytes = TranscoderUtils.encodeInt(flags);
        System.arraycopy(flagBytes, 0, fullData, 0, flagBytes.length);
        System.arraycopy(b, 0, fullData, flagBytes.length, b.length);
        return fullData;
    }

    @Override
    public Object decode(byte[] v) {

        int dataFlags = TranscoderUtils.decodeInt(Arrays.copyOfRange(v, 0, 4));
        byte[] data = Arrays.copyOfRange(v, 4, v.length);

        Object rv = null;
        if ((dataFlags & COMPRESSED) != 0) {
            data = decompress(data);
        }
        int flags = dataFlags & SPECIAL_MASK;
        if ((dataFlags & SERIALIZED) != 0 && data != null) {
            rv = deserialize(data);
        } else if (flags != 0 && data != null) {
            switch (flags) {
                case SPECIAL_BOOLEAN:
                    rv = TranscoderUtils.decodeBoolean(data);
                    break;
                case SPECIAL_INT:
                    rv = TranscoderUtils.decodeInt(data);
                    break;
                case SPECIAL_LONG:
                    rv = TranscoderUtils.decodeLong(data);
                    break;
                case SPECIAL_DATE:
                    rv = new Date(TranscoderUtils.decodeLong(data));
                    break;
                case SPECIAL_BYTE:
                    rv = TranscoderUtils.decodeByte(data);
                    break;
                case SPECIAL_FLOAT:
                    rv = Float.intBitsToFloat(TranscoderUtils.decodeInt(data));
                    break;
                case SPECIAL_DOUBLE:
                    rv = Double.longBitsToDouble(TranscoderUtils.decodeLong(data));
                    break;
                case SPECIAL_BYTEARRAY:
                    rv = data;
                    break;
                default:
                    logger.warn("Undecodeable with flags %x", flags);
            }
        } else {
            rv = decodeString(data);
        }
        return rv;
    }


    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Default compression threshold value.
     */
    public static final int DEFAULT_COMPRESSION_THRESHOLD = 16384;

    private static final String DEFAULT_CHARSET = "UTF-8";

    protected int compressionThreshold = DEFAULT_COMPRESSION_THRESHOLD;
    protected String charset = DEFAULT_CHARSET;


    /**
     * Initialize a serializing transcoder with the given maximum data size.
     */


    /**
     * Set the compression threshold to the given number of bytes. This transcoder
     * will attempt to compress any data being stored that's larger than this.
     *
     * @param to the number of bytes
     */
    public void setCompressionThreshold(int to) {
        compressionThreshold = to;
    }

    /**
     * Set the character set for string value transcoding (defaults to UTF-8).
     */
    public void setCharset(String to) {
        // Validate the character set.
        try {
            new String(new byte[97], to);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        charset = to;
    }

    /**
     * Get the bytes representing the given serialized object.
     */
    protected byte[] serialize(Object o) {
        if (o == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv=null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(o);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Non-serializable object", e);
        } finally {
            CloseUtil.close(os);
            CloseUtil.close(bos);
        }
        return rv;
    }

    /**
     * Get the object represented by the given serialized bytes.
     */
    protected Object deserialize(byte[] in) {
        Object rv=null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if(in != null) {
                bis=new ByteArrayInputStream(in);
                is=new ObjectInputStream(bis);
                rv=is.readObject();
                is.close();
                bis.close();
            }
        } catch (IOException e) {
            logger.warn("Caught IOException decoding %d bytes of data",
                    in.length, e);
        } catch (ClassNotFoundException e) {
            logger.warn("Caught CNFE decoding %d bytes of data",
                    in.length, e);
        } finally {
            CloseUtil.close(is);
            CloseUtil.close(bis);
        }
        return rv;
    }

    /**
     * Compress the given array of bytes.
     */
    protected byte[] compress(byte[] in) {
        if (in == null) {
            throw new NullPointerException("Can't compress null");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gz = null;
        try {
            gz = new GZIPOutputStream(bos);
            gz.write(in);
        } catch (IOException e) {
            throw new RuntimeException("IO exception compressing data", e);
        } finally {
            CloseUtil.close(gz);
            CloseUtil.close(bos);
        }
        byte[] rv = bos.toByteArray();
        logger.debug("Compressed %d bytes to %d", in.length, rv.length);
        return rv;
    }

    /**
     * Decompress the given array of bytes.
     *
     * @return null if the bytes cannot be decompressed
     */
    protected byte[] decompress(byte[] in) {
        ByteArrayOutputStream bos = null;
        if(in != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(in);
            bos = new ByteArrayOutputStream();
            GZIPInputStream gis = null;
            try {
                gis = new GZIPInputStream(bis);

                byte[] buf = new byte[8192];
                int r = -1;
                while ((r = gis.read(buf)) > 0) {
                    bos.write(buf, 0, r);
                }
            } catch (IOException e) {
                logger.warn("Failed to decompress data", e);
                bos = null;
            } finally {
                CloseUtil.close(gis);
                CloseUtil.close(bis);
                CloseUtil.close(bos);
            }
        }
        return bos == null ? null : bos.toByteArray();
    }

    /**
     * Decode the string with the current character set.
     */
    protected String decodeString(byte[] data) {
        String rv = null;
        try {
            if (data != null) {
                rv = new String(data, charset);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return rv;
    }

    /**
     * Encode a string into the current character set.
     */
    protected byte[] encodeString(String in) {
        byte[] rv;
        try {
            rv = in.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return rv;
    }


}
