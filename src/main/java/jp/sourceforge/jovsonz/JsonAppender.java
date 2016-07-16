/*
 * JSON output
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.io.Flushable;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * JSON文字出力用ビジター。
 * <p>
 * JSON Valueのトラバース時にこのビジターを指定すると、
 * 事前に用意した文字出力先にJSONフォーマットで出力される。
 * </p>
 * <p>
 * 出力に伴う{@link java.io.IOException}は
 * {@link JsVisitException}のチェーン例外となる。
 * </p>
 * <p>
 * 前回パースの成功／失敗に関わらず、
 * インスタンスの再利用時の挙動は保証されない。
 * </p>
 */
class JsonAppender implements ValueVisitor {

    /** 改行。 */
    public static final String NEWLINE = "\n";
    /** インデント単位。 */
    public static final String INDENT_UNIT = "\u0020\u0020";
    /** Pair区切り。 */
    public static final String PAIR_SEPARATOR = "\u0020:\u0020";
    /** コンマ区切り。 */
    public static final String COMMA = "\u0020,";
    /** 空要素。 */
    public static final String EMPTY = "\u0020";


    private final Appendable appout;

    private final Stack<DumpContext> contextStack =
            new Stack<>();

    private IOException ioException = null;


    /**
     * コンストラクタ。
     * @param appout 出力先
     * @throws NullPointerException 引数がnull
     */
    public JsonAppender(Appendable appout)
            throws NullPointerException{
        super();
        if(appout == null) throw new NullPointerException();
        this.appout = appout;
        return;
    }

    /**
     * コンテキストをプッシュ退避する。
     * @param composition 現在のコンテキスト
     */
    protected void pushComposition(JsComposition<?> composition){
        DumpContext context = new DumpContext(composition);
        this.contextStack.push(context);
        return;
    }

    /**
     * コンテキストをポップ復帰する。
     * @return スタックトップのコンテキスト
     * @throws EmptyStackException スタック構造が空
     */
    protected JsComposition<?> popComposition() throws EmptyStackException{
        DumpContext context = this.contextStack.pop();
        JsComposition<?> composition = context.getComposition();
        return composition;
    }

    /**
     * ネスト構造の深さを返す。
     * @return 0から始まる深さ
     */
    protected int nestDepth(){
        return this.contextStack.size();
    }

    /**
     * ネスト構造が空(深さ0)か判定する。
     * @return 空ならtrue
     */
    protected boolean isNestEmpty(){
        return this.contextStack.isEmpty();
    }

    /**
     * ネスト後、一つでも子要素が出力されたか判定する。
     * @return 子要素が出力されていればtrue
     */
    protected boolean hasChildDumped(){
        if(isNestEmpty()) return false;
        boolean result = this.contextStack.peek().hasChildDumped();
        return result;
    }

    /**
     * 現時点でのネストに対し、子要素が一つ以上出力済みであると設定する。
     */
    protected void setChildDumped(){
        if(isNestEmpty()) return;
        this.contextStack.peek().setChildDumped();
        return;
    }

    /**
     * 現在のコンテキストがARRAY型配列要素出力中の状態か否か判定する。
     * @return 現在のコンテキストがARRAY型配列要素出力中ならtrue
     */
    protected boolean isArrayContext(){
        if(isNestEmpty()) return false;

        DumpContext context = this.contextStack.peek();
        JsComposition<?> composition = context.getComposition();
        JsTypes type = composition.getJsTypes();
        if(type != JsTypes.ARRAY) return false;

        return true;
    }

    /**
     * 1文字出力。
     * @param ch 文字
     * @throws JsVisitException 出力エラー。
     * @see java.lang.Appendable#append(char)
     */
    protected void append(char ch) throws JsVisitException{
        try{
            this.appout.append(ch);
        }catch(IOException e){
            this.ioException = e;
            throw new JsVisitException(e);
        }
        return;
    }

    /**
     * 文字列出力。
     * @param seq 文字列
     * @throws JsVisitException 出力エラー。
     * @see java.lang.Appendable#append(CharSequence)
     */
    protected void append(CharSequence seq) throws JsVisitException{
        try{
            this.appout.append(seq);
        }catch(IOException e){
            this.ioException = e;
            throw new JsVisitException(e);
        }
        return;
    }

    /**
     * 可能であれば出力先をフラッシュする。
     * @throws JsVisitException 出力エラー
     * @see java.io.Flushable
     */
    protected void flush() throws JsVisitException{
        try{
            if(this.appout instanceof Flushable){
                ((Flushable)this.appout).flush();
            }
        }catch(IOException e){
            this.ioException = e;
            throw new JsVisitException(e);
        }
        return;
    }

    /**
     * トラバース中断の原因となったIOExceptionを返す。
     * @return トラバース中断の原因となったIOException。なければnull。
     */
    public IOException getIOException(){
        return this.ioException;
    }

    /**
     * トラバース中断の原因となったIOExceptionがあるか判定する。
     * @return トラバース中断の原因となったIOExceptionがあればtrue
     */
    public boolean hasIOException(){
        if(this.ioException != null) return true;
        return false;
    }

    /**
     * pairの名前を出力する。
     * @param name pair名
     * @throws JsVisitException 出力エラー
     */
    protected void putPairName(String name) throws JsVisitException{
        try{
            JsString.dumpString(this.appout, name);
        }catch(IOException e){
            this.ioException = e;
            throw new JsVisitException(e);
        }
        return;
    }

    /**
     * pair区切りコロンを出力する。
     * @throws JsVisitException 出力エラー
     */
    protected void putPairSeparator() throws JsVisitException{
        append(PAIR_SEPARATOR);
        return;
    }

    /**
     * 要素間区切りコンマを出力する。
     * JSONでは最後の要素の後にコンマを出力してはいけない。
     * @throws JsVisitException 出力エラー
     */
    protected void putComma() throws JsVisitException{
        append(COMMA);
        return;
    }

    /**
     * 改行を出力する。
     * @throws JsVisitException 出力エラー。
     */
    protected void putNewLine() throws JsVisitException{
        append(NEWLINE);
        return;
    }

    /**
     * インデントを出力する。
     * @throws JsVisitException 出力エラー
     */
    protected void putIndent() throws JsVisitException{
        int level = nestDepth();
        for(int ct = 1; ct <= level; ct++){
            append(INDENT_UNIT);
        }
        return;
    }

    /**
     * OBJECT及びARRAY型の最初の要素の前部分を出力する。
     * @throws JsVisitException 出力エラー
     */
    protected void putBefore1stElement() throws JsVisitException{
        putNewLine();
        putIndent();
        return;
    }

    /**
     * OBJECT及びARRAY型の要素間区切りを出力する。
     * @throws JsVisitException 出力エラー
     */
    protected void putBetweenElement() throws JsVisitException{
        putComma();
        putNewLine();
        putIndent();
        return;
    }

    /**
     * OBJECT及びARRAY型の最後の要素の後部分を出力する。
     * @throws JsVisitException 出力エラー
     */
    protected void putAfterLastElement() throws JsVisitException{
        putNewLine();
        putIndent();
        return;
    }

    /**
     * OBJECT及びARRAY型の空要素を出力する。
     * @throws JsVisitException 出力エラー
     */
    protected void putEmptyElement() throws JsVisitException{
        append(EMPTY);
        return;
    }

    /**
     * パース前の出力を行う。
     * @throws JsVisitException 出力エラー
     */
    protected void putBeforeParse() throws JsVisitException{
        //NOTHING
        return;
    }

    /**
     * パース後の出力を行う。
     * @throws JsVisitException 出力エラー
     */
    protected void putAfterParse() throws JsVisitException{
        putNewLine();
        return;
    }

    /**
     * {@inheritDoc}
     * Valueの出力を行う。
     * @param value {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     */
    @Override
    public void visitValue(JsValue value)
            throws JsVisitException{
        if(isNestEmpty()) putBeforeParse();

        if(isArrayContext()){
            if(hasChildDumped()) putBetweenElement();
            else                 putBefore1stElement();
        }

        JsTypes type = value.getJsTypes();
        switch(type){
        case OBJECT: append('{');              break;
        case ARRAY:  append('[');              break;
        default:     append(value.toString()); break;
        }
        setChildDumped();

        if(type.isComposition()){
            assert value instanceof JsComposition;
            JsComposition<?> composition = (JsComposition) value;
            pushComposition(composition);
        }

        return;
    }

    /**
     * {@inheritDoc}
     * OBJECT内の各pairの名前を出力する。
     * @param pairName {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     */
    @Override
    public void visitPairName(String pairName)
            throws JsVisitException{
        if(hasChildDumped()) putBetweenElement();
        else                 putBefore1stElement();

        putPairName(pairName);
        putPairSeparator();

        setChildDumped();

        return;
    }

    /**
     * {@inheritDoc}
     * 閉じ括弧を出力する。
     * @param closed {@inheritDoc}
     * @throws JsVisitException {@inheritDoc}
     */
    @Override
    public void visitCompositionClose(JsComposition<?> closed)
            throws JsVisitException{
        boolean hasDumped = hasChildDumped();
        JsComposition<?> composition = popComposition();

        if(hasDumped) putAfterLastElement();
        else          putEmptyElement();

        char closeBrace;
        switch(composition.getJsTypes()){
        case OBJECT: closeBrace = '}'; break;
        case ARRAY:  closeBrace = ']'; break;
        default: assert false; throw new AssertionError();
        }
        append(closeBrace);

        if(isNestEmpty()){
            putAfterParse();
            flush();
        }

        return;
    }

    /**
     * ネストされた各JSON集約型コンテキストの出力状況。
     */
    private static class DumpContext{
        private final JsComposition<?> composition;
        private boolean childDumped;

        /**
         * コンストラクタ。
         * 子要素が出力された事実は無い状態で始まる。
         * @param composition レベルに対応するOBJECTもしくはARRAY型Value
         */
        DumpContext(JsComposition<?> composition){
            this.composition = composition;
            this.childDumped = false;
            return;
        }

        /**
         * このレベルに対応するJSON集約型を返す。
         * @return OBJECTもしくはARRAY型Value
         */
        JsComposition<?> getComposition(){
            return this.composition;
        }

        /**
         * このレベルで子要素出力が行われたか判定する。
         * @return 子要素出力が行われていたならtrue
         */
        boolean hasChildDumped(){
            return this.childDumped;
        }

        /**
         * このレベルで子要素出力が行われた事実を設定する。
         */
        void setChildDumped(){
            this.childDumped = true;
            return;
        }

    }

}
