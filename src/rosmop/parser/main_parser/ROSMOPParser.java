/* Generated By:JavaCC: Do not edit this line. ROSMOPParser.java */
package rosmop.parser.main_parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import rosmop.parser.ast.*;

/**
 * @author A. Cody Schuffelen
 * @author Cansu Erdogan
 *
 * Generic ROS specification file parser
 *
 */
public class ROSMOPParser implements ROSMOPParserConstants {
  private static ROSMOPParser parser;
  public static boolean eof = false;
    /**
     * Parse a complete RVM file specification.
     * @param s The text of the file.
     * @return The parsed file as a Java object.
     */
    public static MonitorFile parse(final String s) {
        try {
                return parse(new InputStreamReader(new FileInputStream(s)));
        } catch (IOException e) {
                        throw new RuntimeException(e);
                }
    }

    /**
     * Parse a complete RVM file specification.
     * @param reader The place to read the specification from.
     * @return The parsed file as a Java object.
     */
    public static MonitorFile parse(final Reader reader) {
        if (parser == null)
                        parser = new ROSMOPParser(reader);
                else
                        parser.ReInit(reader);

        try {
            return parser.rvFile();
        } catch(ParseException e) {
            throw new RuntimeException(e);
        } catch(TokenMgrError e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read a backtick segment with matched backticks, e.g. `a`, ``a``, ```a```, or ``a`b``.
     * Assumes the first backtick has already been read.
     * @return The text inside the backticks.
     */
    private String parseBacktickSegment() throws ParseException {
        try {
            int backtickDepth = 1;
            char c;
            while((c =jj_input_stream.readChar()) == '`') {
                backtickDepth++;
            }
            StringBuilder innerSegment = new StringBuilder();
            innerSegment.append(c);
            while(true) {
                while((c = jj_input_stream.readChar()) != '`') {
                    innerSegment.append(c);
                }
                int endingTicks;
                for(endingTicks = 1; endingTicks < backtickDepth; endingTicks++) {
                    c = jj_input_stream.readChar();
                    if(c != '`') {
                        for(int i = 0; i < endingTicks; i++) {
                            innerSegment.append('`');
                        }
                        innerSegment.append(c);
                        break;
                    }
                }
                if(endingTicks == backtickDepth) {
                    return innerSegment.toString();
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return "";
        }
    }

    /**
     * Reads a segment from the character stream with matching open and close characters, 
     * e.g. (((--)--(--))--).
     * @param open The opening character.
     * @param close The closing character.
     * @param depth The number of open parentheses already parsed.
     * @return The full text, including the first open and close parentheses.
     */
    private String parseMatchingSegment(final char open, final char close, int depth)
            throws ParseException {
        try {
            StringBuilder innerSegment = new StringBuilder();
            for(int i = 0; i < depth; i++) {
                innerSegment.append(open);
            }
            while(depth > 0) {
                char c = jj_input_stream.readChar();
                innerSegment.append(c);
                if(c == open) {
                    depth++;
                } else if(c == close) {
                    depth--;
                }
            }
            return innerSegment.toString();
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return "";
        }
    }

    /**
     * Parse matching parentheses from the character stream, assuming one is already read.
     * @return The string read, including the first and last parentheses.
     */
    private String parseMatchingParens() throws ParseException {
        return parseMatchingSegment('(', ')', 1);
    }

    /**
     * Parse matching curly brackets from the character stream, assuming one is already read.
     * @return The string read, including the first and last curly brackets.
     */
    private String parseMatchingCurlyBrackets() throws ParseException {
        return parseMatchingSegment('{', '}', 1);
    }

    /**
     * Read characters from the character stream until a string is exactly matched. Pushes the
     * matched text back onto the character stream.
     * @param end The string to match.
     * @return All the text until the matched string.
     */
    private String parseUntil(final String end) throws ParseException {
        try {
            StringBuilder innerSegment = new StringBuilder();
            while(true) {
                char c;
                while((c = jj_input_stream.readChar()) != end.charAt(0)) {
                    innerSegment.append(c);
                }
                StringBuilder partialMatch = new StringBuilder();
                partialMatch.append(c);
                for(int i = 1; i < end.length(); i++) {
                    c = jj_input_stream.readChar();
                    if(c == end.charAt(i)) {
                        partialMatch.append(c);
                    } else {
                        break;
                    }
                }
                if(partialMatch.toString().equals(end)) {
                    jj_input_stream.backup(end.length());
                    return innerSegment.toString();
                } else {
                    innerSegment.append(partialMatch);
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return "";
        }
    }

    /**
     * Read lines from the character stream until a regular expression matches the line. Pushes
     * the matching line back into the character stream.
     * @param pattern The regular expression to match against each line.
     * @return The text until the matching line.
     */
    private String parseUntilLineMatches(final Pattern pattern) {
        try {
            StringBuilder allText = new StringBuilder();
            StringBuilder line = new StringBuilder();
            while(!pattern.matcher(line.toString()).find()) {
                allText.append(line);
                line = new StringBuilder();
                char c;
                while((c = jj_input_stream.readChar()) != '\u005cn') {
                    line.append(c);
                }
                line.append(c);
            }
            jj_input_stream.backup(line.length());
            return allText.toString();
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return "";
        }
    }

  final public MonitorFile rvFile() throws ParseException {
    String preamble;
    ArrayList<Specification> specs = new ArrayList<Specification>();
    Specification spec;
     preamble = parseUntilLineMatches(Pattern.compile("^([0-9_a-zA-Z\u005c\u005cs-]+)((\u005c\u005c()|(\u005c\u005c{))"));
    label_1:
    while (true) {
      spec = specification();
                              specs.add(spec);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ID:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
    }
    jj_consume_token(0);
        ROSMOPParser.eof = true;
        {if (true) return new MonitorFile(preamble, specs);}
    throw new Error("Missing return statement in function");
  }

  final public Specification specification() throws ParseException {
    String preDeclarations = "";
    ArrayList<String> languageModifiers = new ArrayList<String>();
    Token modifier;
    String name;
    String languageParameters = "";
    String languageDeclarations = "";
    String init = "";
    ArrayList<Event> events = new ArrayList<Event>();
    Event myEvent;
    ArrayList<Property> properties = new ArrayList<Property>();
    Property myProperty;
    label_2:
    while (true) {
      modifier = jj_consume_token(ID);
                      languageModifiers.add(modifier.image);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ID:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_2;
      }
    }
        name = languageModifiers.get(languageModifiers.size() - 1);
        languageModifiers.remove(languageModifiers.size() - 1);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LPAREN:
    case BACKTICK:
      languageParameters = delimitedNoCurly();
      break;
    default:
      jj_la1[2] = jj_gen;
      ;
    }
    jj_consume_token(LBRACE);
         languageDeclarations = parseUntilLineMatches(Pattern.compile(
            "^([-a-zA-Z\u005c\u005cs_]*)(init|event([a-zA-Z_\u005c\u005cs0-9]+))\u005c\u005c("));
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INIT:
      jj_consume_token(INIT);
      jj_consume_token(LPAREN);
      jj_consume_token(RPAREN);
      jj_consume_token(LBRACE);
                                       init = parseMatchingCurlyBrackets();
      break;
    default:
      jj_la1[3] = jj_gen;
      ;
    }
    label_3:
    while (true) {
      myEvent = event(name);
                                                events.add(myEvent);
      if (jj_2_1(2)) {
        ;
      } else {
        break label_3;
      }
    }
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ID:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_4;
      }
      myProperty = propertyAndHandlers(name);
                                                 properties.add(myProperty);
    }
    jj_consume_token(RBRACE);
       {if (true) return new Specification(preDeclarations, languageModifiers, name, languageParameters,
            languageDeclarations, init, events, properties);}
    throw new Error("Missing return statement in function");
  }

  final public Event event(String specName) throws ParseException {
    ArrayList<String> modifiers = new ArrayList<String>();
    Token modifier;
    Token name;
    ArrayList<String> definitionModifiers = new ArrayList<String>();
    Token definitionModifier;
    String eventDefinition = "";
    Token topic;
    Token msgType;
    HashMap<String, String> pattern = new HashMap<String, String>();
    String eventAction = "";
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ID:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_5;
      }
      modifier = jj_consume_token(ID);
                       modifiers.add(modifier.image);
    }
    jj_consume_token(EVENT);
    name = jj_consume_token(ID);
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ID:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_6;
      }
      definitionModifier = jj_consume_token(ID);
                                 definitionModifiers.add(definitionModifier.image);
    }
    eventDefinition = delimitedSegment();
    topic = jj_consume_token(NAMING);
    msgType = jj_consume_token(NAMING);
    jj_consume_token(18);
         pattern = pattern(pattern, ""); /*pattern = parseUntil("'"); /*System.out.println(pattern);*/
    jj_consume_token(18);
    jj_consume_token(LBRACE);
         eventAction = parseMatchingCurlyBrackets();
     {if (true) return new Event(modifiers, name.image, definitionModifiers, eventDefinition, topic.image,
        msgType.image, pattern, eventAction, specName);}
    throw new Error("Missing return statement in function");
  }

  final public Property propertyAndHandlers(String specName) throws ParseException {
    Token name;
    Token notAt;
    String syntax = "";
    ArrayList<PropertyHandler> propertyHandlers = new ArrayList<PropertyHandler>();
    PropertyHandler handler;
    name = jj_consume_token(ID);
    jj_consume_token(COLON);
     syntax = parseUntil("@");
    label_7:
    while (true) {
      handler = propertyHandler();
                                  propertyHandlers.add(handler);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AT:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_7;
      }
    }
     {if (true) return new Property(name.image, syntax, propertyHandlers, specName);}
    throw new Error("Missing return statement in function");
  }

  final public PropertyHandler propertyHandler() throws ParseException {
    Token name;
    String languageAction = "";
    jj_consume_token(AT);
    name = jj_consume_token(ID);
    languageAction = delimitedSegment();
     {if (true) return new PropertyHandler(name.image, languageAction);}
    throw new Error("Missing return statement in function");
  }

  final public String delimitedSegment() throws ParseException {
    String other;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LPAREN:
    case BACKTICK:
      other = delimitedNoCurly();
                                  {if (true) return other;}
      break;
    case LBRACE:
      jj_consume_token(LBRACE);
           {if (true) return parseMatchingCurlyBrackets();}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public String delimitedNoCurly() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BACKTICK:
      jj_consume_token(BACKTICK);
           {if (true) return parseBacktickSegment();}
      break;
    case LPAREN:
      jj_consume_token(LPAREN);
           {if (true) return parseMatchingParens();}
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public HashMap<String, String> pattern(HashMap<String, String> patterncol, String str) throws ParseException {
   HashMap<String, String> patterns = patterncol;
    jj_consume_token(LBRACE);
                  innerpattern(patterncol, str);
    label_8:
    while (true) {
      if (jj_2_2(2)) {
        ;
      } else {
        break label_8;
      }
      jj_consume_token(COMMA);
                                         innerpattern(patterncol, str);
    }
    jj_consume_token(RBRACE);
          {if (true) return patterns;}
    throw new Error("Missing return statement in function");
  }

  final public void innerpattern(HashMap<String, String> patterncol, String str) throws ParseException {
        String t = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ID:
    case NAMING:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NAMING:
        jj_consume_token(NAMING);
        break;
      case ID:
        jj_consume_token(ID);
        break;
      default:
        jj_la1[10] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
                            t = token.image; str += t;
      jj_consume_token(COLON);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ID:
      case NAMING:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NAMING:
          jj_consume_token(NAMING);
          break;
        case ID:
          jj_consume_token(ID);
          break;
        default:
          jj_la1[11] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
                                        patterncol.put((String) token.image, str);
        break;
      default:
        jj_la1[12] = jj_gen;
                                    str += "."; pattern(patterncol, str);
      }
      break;
    default:
      jj_la1[13] = jj_gen;
      ;
    }
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_3_1() {
    if (jj_3R_9()) return true;
    return false;
  }

  private boolean jj_3R_10() {
    if (jj_scan_token(ID)) return true;
    return false;
  }

  private boolean jj_3R_9() {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_10()) { jj_scanpos = xsp; break; }
    }
    if (jj_scan_token(EVENT)) return true;
    if (jj_scan_token(ID)) return true;
    return false;
  }

  private boolean jj_3_2() {
    if (jj_scan_token(COMMA)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ROSMOPParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[14];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x1000,0x1000,0x8040,0x800,0x1000,0x1000,0x1000,0x200,0x8050,0x8040,0x21000,0x21000,0x21000,0x21000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[2];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public ROSMOPParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ROSMOPParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ROSMOPParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public ROSMOPParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ROSMOPParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public ROSMOPParser(ROSMOPParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ROSMOPParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 14; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[19];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 14; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 19; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 2; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
