/*
 * JSON stream source
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * JSONデータ用入力ソース。
 * 先読みした文字のプッシュバック機能と行番号のカウント機能を有する。
 * 行番号は1から始まる。
 * 行と行はLF('\n')で区切られるものとする。(※CRは無視)
 * @see java.io.PushbackReader
 * @see java.io.LineNumberReader
 */
class JsonSource implements Closeable {

    /** プッシュバック可能な文字数。 */
    private static final int PUSHBACK_TOKENS = 10;

    private static final char LINEFEED = '\n';  // LF(0x0a)

    private static final String ERRMSG_OVERFLOW =
            "Pushback buffer overflow";
    private static final String ERRMSG_CLOSED =
            "Stream closed";

    static{
        assert "\\uXXXX".length() < PUSHBACK_TOKENS;
    }

    private final Reader reader;

    // プッシュバック用文字スタック構造。
    private final char[] charStack = new char[PUSHBACK_TOKENS];
    private int stackPt = 0;

    private int lineNumber = 1;

    private boolean closed = false;

    /**
     * コンストラクタ。
     * @param reader 文字入力リーダー
     * @throws NullPointerException 引数がnull
     */
    public JsonSource(Reader reader) throws NullPointerException{
        super();
        if(reader == null) throw new NullPointerException();
        this.reader = reader;
        return;
    }

    /**
     * コンストラクタ。
     * 任意の文字列を入力ソースとする。
     * @param text 文字列
     * @see java.io.StringReader
     */
    public JsonSource(CharSequence text){
        this(new StringReader(text.toString()));
        return;
    }

    /**
     * JSON規格のwhitespace文字を判定する。
     * @param ch 判定対象文字
     * @return whitespaceならtrue
     */
    public static boolean isWhitespace(char ch){
        switch(ch){
        case '\u0020':
        case '\t':
        case '\r':
        case '\n':
            return true;
        default:
            break;
        }
        return false;
    }

    /**
     * JSON規格のwhitespace文字を判定する。
     * @param ch 判定対象文字。
     * 上位16bitがゼロでなければwhitespaceと判定されない。
     * @return whitespaceならtrue。引数が負の場合はfalse。
     */
    public static boolean isWhitespace(int ch){
        if((int)Character.MIN_VALUE > ch) return false;
        if((int)Character.MAX_VALUE < ch) return false;
        return isWhitespace((char)ch);
    }

    /**
     * プッシュバック可能な残り文字数を返す。
     * @return プッシュバック可能な残り文字数
     */
    public int getPushBackSpared(){
        return PUSHBACK_TOKENS - this.stackPt;
    }

    /**
     * 現時点での行番号を返す。
     * @return 1から始まる行番号
     */
    public int getLineNumber(){
        return this.lineNumber;
    }

    /**
     * 1文字読み込む。
     * @return 読み込んだ文字。入力が終わっている場合は負の値。
     * @throws IOException 入力エラー
     * @see java.io.Reader#read()
     */
    public int read() throws IOException{
        if(this.closed) throw new IOException(ERRMSG_CLOSED);

        int chData;
        if(this.stackPt > 0){
            chData = (int) this.charStack[--this.stackPt];
        }else{
            chData = this.reader.read();
        }

        if(chData == (int)LINEFEED) this.lineNumber++;

        return chData;
    }

    /**
     * 入力末端ではないと仮定して1文字読み込む。
     * @return 読み込んだ文字。
     * @throws IOException 入力エラー
     * @throws JsParseException 入力が終わっている
     */
    public char readOrDie() throws IOException, JsParseException{
        int chData = read();
        if(chData < 0){
            throw new JsParseException(JsParseException.ERRMSG_NODATA,
                                       this.lineNumber);
        }
        return (char)chData;
    }

    /**
     * 入力が文字列とマッチするか判定する。
     * 失敗しても読み戻しは行われない。
     * 長さ0の文字列は必ずマッチに成功する。
     * @param seq マッチ対象文字列
     * @return マッチすればtrue
     * @throws IOException 入力エラー
     * @throws JsParseException 入力が終わっている。
     */
    public boolean matchOrDie(CharSequence seq)
            throws IOException, JsParseException{
        int length = seq.length();
        for(int pt = 0; pt < length; pt++){
            if(readOrDie() != seq.charAt(pt)) return false;
        }
        return true;
    }

    /**
     * 1文字読み戻す。
     * 行数カウントへも反映される。
     * @param ch 読み戻す文字
     * @throws IOException バッファあふれもしくはクローズ済み
     */
    public void unread(char ch) throws IOException{
        if(this.closed) throw new IOException(ERRMSG_CLOSED);

        if(this.stackPt >= PUSHBACK_TOKENS){
            throw new IOException(ERRMSG_OVERFLOW);
        }

        this.charStack[this.stackPt++] = ch;

        if(ch == LINEFEED) this.lineNumber--;

        return;
    }

    /**
     * 1文字読み戻す。
     * char型にキャストした引数が次回読み込まれる。
     * 行数カウントへも反映される。
     * @param ch 読み戻す文字。負の符号を含む上位16bitは無視される。
     * @throws IOException バッファあふれもしくはクローズ済み
     */
    public void unread(int ch) throws IOException{
        unread((char) ch);
        return;
    }

    /**
     * whitespace文字を読み飛ばす。
     * @throws IOException 入力エラー
     */
    public void skipWhiteSpace() throws IOException{
        for(;;){
            int chData = read();
            if(chData < 0) break;
            if( ! isWhitespace(chData) ){
                unread(chData);
                break;
            }
        }

        return;
    }

    /**
     * まだ読み込めるデータがあるか判定する。
     * @return まだ読めるデータがあればtrue
     * @throws IOException IO入力エラー
     */
    public boolean hasMore() throws IOException{
        int chData = read();
        if(chData < 0) return false;
        unread(chData);
        return true;
    }

    /**
     * コンストラクタで指定されたReaderを閉じる。
     * クローズ後の読み込みおよび読み戻し動作は全て例外を投げる。
     * @throws IOException 入出力エラー
     * @see java.io.Closeable
     */
    public void close() throws IOException{
        this.closed = true;
        this.stackPt = 0;
        this.reader.close();
        return;
    }

}
