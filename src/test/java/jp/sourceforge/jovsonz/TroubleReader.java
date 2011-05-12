/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;

/**
 * limit文字まで正常に読み込め、
 * 以降の読み出しで自動的にIOExceptionを投げるStringReader。
 */
public class TroubleReader extends StringReader{

    private final int limit;
    private int ct = 0;

    public TroubleReader(String text, int limit){
        super(text);
        this.limit = limit;
        return;
    }

    @Override
    public int read() throws IOException{
        if(this.ct >= this.limit) throw new IOException();
        this.ct++;
        return super.read();
    }

    @Override
    public int read(char[] cbuf){
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(char[] cbuf, int off, int len){
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(CharBuffer target){
        throw new UnsupportedOperationException();
    }

    @Override
    public long skip(long ns){
        throw new UnsupportedOperationException();
    }

}
