package com.github.snail.common;

import java.io.InputStream;

class ClosedInputStream extends InputStream {
    
    public static final ClosedInputStream CLOSED_INPUT_STREAM = new ClosedInputStream();

    @Override
    public int read() {
        return -1;
    }
}