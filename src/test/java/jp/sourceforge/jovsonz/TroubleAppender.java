/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.Flushable;
import java.io.IOException;

/**
 *
 */
public class TroubleAppender implements Appendable, Flushable {

    private final StringBuilder content = new StringBuilder();
    private final long limit;

    public TroubleAppender(long limit){
        super();
        this.limit = limit;
        return;
    }

    private void checkLimit() throws IOException{
        if(this.limit < this.content.length()) throw new IOException();
        return;
    }

    @Override
    public Appendable append(CharSequence csq)
            throws IOException{
        this.content.append(csq);
        checkLimit();
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end)
            throws IOException{
        this.content.append(csq, start, end);
        checkLimit();
        return this;
    }

    @Override
    public Appendable append(char c)
            throws IOException{
        this.content.append(c);
        checkLimit();
        return this;
    }

    @Override
    public void flush() throws IOException{
        throw new IOException();
    }

    @Override
    public String toString(){
        return this.content.toString();
    }

}
