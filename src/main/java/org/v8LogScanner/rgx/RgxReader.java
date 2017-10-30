package org.v8LogScanner.rgx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RgxReader implements AutoCloseable {

    // The main pattern using to find event blocks in the log file.
    private final String EVENT_RGX = ".+?(?=\\d{2}:\\d{2}\\.\\d{6})";

    //The index into the buffer currently held by the Reader
    private int position;

    // The buffer which the reader read in.
    private CharBuffer buf;

    // Channel input reader using to read a text from
    // log files into the buffer.
    private Reader reader = null;

    // Stream supply a channel which reader used
    FileInputStream fs = null;

    private Pattern pattern;

    private Matcher matcher;

    //When is using next() need to control reading state of the buffer
    private boolean needInput = false;

    // The remaining input converted to string from the buffer.
// It needs as the pattern cannot matches last event from a file.
    private String remaining = "";

    private int limit;

    //The filtered logs content returned by RgxReader.
    private ArrayList<String> result;

    public RgxReader(String fileName, Charset charset, int _limit) throws FileNotFoundException {

        this(CharBuffer.allocate(1024));

        fs = new FileInputStream(fileName);
        ReadableByteChannel channel = fs.getChannel();
        CharsetDecoder decoder = charset.newDecoder();
        reader = Channels.newReader(channel, decoder, -1);
        buf.limit(0);
        limit = _limit;
    }

    public RgxReader(CharBuffer source) {

        pattern = Pattern.compile(EVENT_RGX, Pattern.DOTALL);
        buf = source;
        matcher = pattern.matcher(buf);
        buf.position(position);
        matcher.useTransparentBounds(true);
        matcher.useAnchoringBounds(false);

        result = new ArrayList<>(limit);
    }

    public boolean next() throws IOException {

        if (reader == null)
            throw new IncorrectReaderMethod();

        result.clear();

        int n = 0;
        while (true) {

            if (!needInput)
                readFromBuffer(limit);

            if (result.size() >= limit)
                return true;

            if (needInput) {
                // make space
                if (buf.limit() == buf.capacity()) {
                    makeSpace();
                }

                // read input
                int p = buf.position();
                buf.position(buf.limit());
                buf.limit(buf.capacity());

                n = reader.read(buf);

                if (n == -1 && !remaining.isEmpty()) {
                    result.add(remaining.trim());
                    remaining = "";
                    return true;
                } else if (n == -1 && remaining.isEmpty())
                    return false;

                // Restore current position and limit for reading
                buf.limit(buf.position());
                buf.position(p);
                position = buf.position();
                needInput = false;
            }
        }
    }

    public void readFromBuffer(int limit) throws IOException {

        matcher.region(position, buf.limit());
        while (matcher.lookingAt()) {

            String s = matcher.group();
            //!! bad fix *+ matching empty string
            if (s.length() > 1)
                result.add(s.trim());
            position = matcher.end();

            if (limit > 0 && result.size() >= limit) {
                needInput = false;
                return;
            }
            matcher.region(position, buf.limit());
        }

        if (buf.hasRemaining() && buf.limit() > position && position >= 0) {
            buf.position(position);
            remaining = buf.toString();
            buf.position(buf.limit());
        }
        needInput = true;
    }

    private void makeSpace() {

        int offset = position;
        buf.position(offset);
        // Gain space by compacting buffer
        if (offset > 0) {
            buf.compact();
            position -= offset;
            buf.flip();
            return;
        }
        // Gain space by growing buffer
        int newSize = buf.capacity() * 2;
        CharBuffer newBuf = CharBuffer.allocate(newSize);
        newBuf.put(buf);
        newBuf.flip();
        position -= offset;
        buf = newBuf;
        matcher.reset(buf);
    }

    public ArrayList<String> getResult() {
        return result;
    }

    public void close() throws IOException {
        fs.close();
        reader.close();
    }
}

class IncorrectReaderMethod extends IOException {

    private static final long serialVersionUID = -6768497416328793322L;

    public IncorrectReaderMethod() {
        super("Incorrect method has been chosen."
                + " You need to use \"readFromBuffer()\" instead");
    }
}
