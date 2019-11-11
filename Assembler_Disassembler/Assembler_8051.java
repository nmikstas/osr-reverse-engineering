package nick.assembler;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Vector;

//A simple class for storing code lines.
class CodeLine {
    CodeLine() {
        numOperands = 0;
        opcode = 0;
        address = 0;
        isInstruction = false;
        label = "";
    }
    public String label;
    public int numOperands;
    public int opcode;
    public int address;
    public boolean isInstruction;
    public Vector<Integer> operands = new Vector<Integer>();
}

//A simple class to store alias associations.
class AliasSet {
    AliasSet(String as, String rs, int ln) {
        aliasString = as;
        replacementString = rs;
        lineNumber = ln;
    }
    public String aliasString;
    public String replacementString;
    public int lineNumber;
}

//A simple class to store address labels.
class Labels {
    Labels(String ln, int la, int lin) {
        labelName = ln;
        labelAddress = la;
        lineNumber = lin;
    }
    public String labelName;
    public int labelAddress;
    public int lineNumber;
}

//A simple class for storing code blocks.
class CodeBlock {
    CodeBlock() {
        origin = 0;
        length = 0;
    }
    public int origin;
    public int length;
    String addressLo;
    String addressHi;
    String sizeLo;
    String sizeHi;
    public Vector<String> operands = new Vector<String>();
}

//A simple class to store error events.
class Error{
    Error(String es, int el) {
        errorString = es;
        errorLine = el;
    }
    public String errorString;
    public int errorLine;
}

public class Assembler_8051 {
    //Line number where error occurs.
    private int errorLine = 0;
    //EEPROM configuration byte.
    private int ebyte = 0;
    //Vendor ID word.
    private int vid = 0;
    //Product ID word.
    private int pid = 0;
    //Device ID word.
    private int did = 0;
    //I2C configuration byte.
    private int cbyte = 0;
    //Input file.
    private RandomAccessFile read;
    //Output file.
    private RandomAccessFile write;
    //Input vector.
    private Vector<String> input = new Vector<String>();
    //Vector of aliases.
    private Vector<AliasSet> aliases = new Vector<AliasSet>();
    //Vector of addresses corresponding to each program line.
    private Vector<Integer> addresses = new Vector<Integer>();
    //Vector of labels.
    private Vector<Labels> labels = new Vector<Labels>();
    //Vector of error messages.
    private Vector<Error> error = new Vector<Error>();
    //Vector of instructions.
    private Vector<CodeLine> codeLines = new Vector<CodeLine>();
    //Vector of code blocks.
    private Vector<CodeBlock> blocks = new Vector<CodeBlock>();
    //Vector of ascii bytes to be sent to output file.
    private Vector<String> asciiBytes = new Vector<String>();
    //Reserved words not to be used in labels.
    private String[] reserved = {"nop" , "ajmp", "ljmp", "rr"  , "inc" , "jbc" , "acall", "lcall",
                                 "rrc" , "dec" , "jb"  , "ret" , "rl"  , "add" , "jnb"  , "reti" ,
                                 "rlc" , "addc", "jc"  , "orl" , "jnc" , "anl" , "jz"   , "xrl"  ,
                                 "jnz" , "jmp" , "mov" , "sjmp", "movc", "div" , "subb" , "mul"  ,
                                 "cpl" , "cjne", "push", "clr" , "swap", "xch" , "pop"  , "setb" ,
                                 "da"  , "djnz", "xchd", "movx", "clr" ,
                                 "a"   , "@r0" , "@r1" , "r0"  , "r1"  , "r2"  , "r3"   , "r4"   ,
                                 "r5"  , "r6"  , "r7"  , "c"   , "@a+dptr", "@a+pc", "ab", "dptr",
                                 "@dptr"};
    //Invalid label and alias characters.
    private String[] invalidChars = {"!" , "@" , "#" , "$" , "%" , "^" , "&" ,
                                     "*" , "(" , ")" , "+" , "=" , "`" , "~" ,
                                     "\\", "|" , "]" , "[" , "{" , "}" , ";" ,
                                     ":" , "\"", "\'", "-" , "<" , "," , ">" ,
                                     "." , "/" , "?"};

    private String[] labelChars =   {"_", "a", "b", "c", "d", "e", "f", "g", "h",
                                     "i", "j", "k", "l", "m", "n", "o", "p", "q",
                                     "r", "s", "t", "u", "v", "w", "x", "y", "z",
                                     "A", "B", "C", "D", "E", "F", "G", "H", "I",
                                     "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                                     "S", "T", "U", "V", "W", "X", "Y", "Z"};

    //Number of operands per instruction.
    private int numOperands[] = {1, 2, 3, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                 3, 2, 3, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                 3, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                 3, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                 2, 2, 2, 3, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                 2, 2, 2, 3, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                 2, 2, 2, 3, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                 2, 2, 2, 1, 2, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                                 2, 2, 2, 1, 1, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                                 3, 2, 2, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                 2, 2, 2, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                                 2, 2, 2, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                                 2, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                 2, 2, 2, 1, 1, 3, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2,
                                 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    Assembler_8051(String[] arguments) {
        //Open input and output files.
        OpenFiles(arguments);
        //Fill input vector.
        FillInputVector();
        //Remove all comments from input vector.
        CommentRemover();
        //Remove all blank lines from input vector.
        BlankLineRemover();
        //Replace all commas with spaces.
        CommaRemover();
        //Remove consecutive blank spaces from input vector.
        SpaceRemover();
        //Create alias vector.
        AliasVector();
        //Check to make sure alias is not a reserved word or has invalid characters.
        AliasReservedCheck();
        //Check to make sure all config bytes are present and well formed.
        ConfigCheck();
        //Replace aliases in input vector.
        ReplaceAliases();
        //Verify the labels are properly formed and fill the label vector.
        LabelChecker();
        //Get opcode and number of operands.
        GetOperands();
        //Add labels to the codeLines vector.
        AddLabels();
        //Separate operands that are two bytes wide.
        SeparateOperands();
        //Finish finding all the operands.
        FinishOperands();
        //Put code blocks into blocks vector.
        FillCodeBlocks();
        //Break down large code blocks.
        ReduceCodeBlocks();
        //Set address bytes and length bytes in the code blocks.
        SetAddressBytes();
        //Fill asciiBytes vector.
        FillAsciiBytes();
        //Send ascii bytes to file if no errors.
        FillOutput();

        //Print vector for debugging.
        //VectorPrint();
        //Print code blocks for debugging.
        //CodeBlockPrint();
        
        //Print any errors for user to see.
        PrintErrors();
    }

    private void FillOutput() {
        if(!error.isEmpty()) //Only continue if no errors present.
            return;
        try {
            write.setLength(0); //Erase any previous contents.
            for(int i = 0; i < asciiBytes.size(); i++) {
                write.writeBytes(asciiBytes.elementAt(i) + ", ");
                if((i + 1) % 16 == 0 && i != 0) {
                    write.writeByte(0x0D); //Notepad new lines.
                    write.writeByte(0x0A);
                }
            }
        }
        catch (IOException ioException) {
            System.err.println("Error writing to .bin file: " + ioException);
            System.exit(1);
        }
    }

    private void FillAsciiBytes() {
        String filler0;
        int temp;

        //Set EEPROM configuration byte.
        filler0 = ebyte < 0x10 ? "0" : "";
        asciiBytes.addElement(filler0 + Integer.toHexString(ebyte).toUpperCase());

        //Set vendor ID bytes.
        temp = vid;
        temp &= 0xFF;
        filler0 = temp < 0x10 ? "0" : "";
        asciiBytes.addElement(filler0 + Integer.toHexString(temp).toUpperCase());
        temp = vid;
        temp >>= 8;
        filler0 = temp < 0x10 ? "0" : "";
        asciiBytes.addElement(filler0 + Integer.toHexString(temp).toUpperCase());        

        //Set product ID bytes.
        temp = pid;
        temp &= 0xFF;
        filler0 = temp < 0x10 ? "0" : "";
        asciiBytes.addElement(filler0 + Integer.toHexString(temp).toUpperCase());
        temp = pid;
        temp >>= 8;
        filler0 = temp < 0x10 ? "0" : "";
        asciiBytes.addElement(filler0 + Integer.toHexString(temp).toUpperCase());        

        //Set device ID bytes.
        temp = did;
        temp &= 0xFF;
        filler0 = temp < 0x10 ? "0" : "";
        asciiBytes.addElement(filler0 + Integer.toHexString(temp).toUpperCase());
        temp = did;
        temp >>= 8;
        filler0 = temp < 0x10 ? "0" : "";
        asciiBytes.addElement(filler0 + Integer.toHexString(temp).toUpperCase());        

        //Set I2C configuration byte.
        filler0 = cbyte < 0x10 ? "0" : "";
        asciiBytes.addElement(filler0 + Integer.toHexString(cbyte).toUpperCase());

        for(int i = 0; i < blocks.size(); i++) {
            asciiBytes.addElement(blocks.elementAt(i).sizeHi);
            asciiBytes.addElement(blocks.elementAt(i).sizeLo);
            asciiBytes.addElement(blocks.elementAt(i).addressHi);
            asciiBytes.addElement(blocks.elementAt(i).addressLo);
            for(int j = 0; j < blocks.elementAt(i).operands.size(); j++) 
                asciiBytes.addElement(blocks.elementAt(i).operands.elementAt(j));
        }

        //Place end sequence in vector.
        asciiBytes.addElement("80");
        asciiBytes.addElement("01");
        asciiBytes.addElement("E6");
        asciiBytes.addElement("00");
        asciiBytes.addElement("00");

        //Fill the rest with 0xFF;
        for(int i = asciiBytes.size(); i <= 0x3FFF; i++)
            asciiBytes.addElement("FF");
    }

    private void SetAddressBytes() {
        String filler0;
        int temp;
        for(int i = 0; i < blocks.size(); i++) {
            //Set address bytes.
            temp = blocks.elementAt(i).origin;
            temp >>= 8;
            filler0 = temp < 0x10 ? "0" : "";
            blocks.elementAt(i).addressHi = filler0 + Integer.toHexString(temp).toUpperCase();
            temp = blocks.elementAt(i).origin;
            temp &= 0xFF;
            filler0 = temp < 0x10 ? "0" : "";
            blocks.elementAt(i).addressLo = filler0 + Integer.toHexString(temp).toUpperCase();
            
            //Set size bytes.
            temp = blocks.elementAt(i).length;
            temp >>= 8;
            filler0 = temp < 0x10 ? "0" : "";
            blocks.elementAt(i).sizeHi = filler0 + Integer.toHexString(temp).toUpperCase();
            temp = blocks.elementAt(i).length;
            temp &= 0xFF;
            filler0 = temp < 0x10 ? "0" : "";
            blocks.elementAt(i).sizeLo = filler0 + Integer.toHexString(temp).toUpperCase();
        }
    }

    private void ReduceCodeBlocks() {
        for(int i = 0; i < blocks.size(); i++) {
            if(blocks.elementAt(i).length > 1023) { //Block is too big.  break it down.
                blocks.insertElementAt(new CodeBlock(), i + 1);
                blocks.elementAt(i + 1).origin = blocks.elementAt(i).origin + 1023;
                blocks.elementAt(i + 1).length = blocks.elementAt(i).length - 1023;
                while(blocks.elementAt(i).operands.size() > 1023) {
                    blocks.elementAt(i + 1).operands.addElement(blocks.elementAt(i).operands.elementAt(1023));
                    blocks.elementAt(i).operands.removeElementAt(1023);
                }
                blocks.elementAt(i).length = 1023;
            }
        }
    }

    private void FillCodeBlocks() {
        int i = 0;
        boolean firstOrgFound = false;
        boolean nextOrgFound  = false;
        //Find first .org directive.
        while(i < input.size() && !firstOrgFound) {
            String s = input.elementAt(i);
            String[] tokenizedString = s.split(" ");
            if(tokenizedString[0].toLowerCase().equals(".org")) {
                firstOrgFound = true;
            }
            i++;
        }
        //Add first code block to vector.
        blocks.addElement(new CodeBlock());
        blocks.lastElement().origin = codeLines.elementAt(i).address;
        String filler0;

        while(i < input.size()) {
            String s = input.elementAt(i);
            String[] tokenizedString = s.split(" ");

            if(tokenizedString[0].toLowerCase().equals(".org")) {
                blocks.addElement(new CodeBlock());
                blocks.lastElement().origin = codeLines.elementAt(i).address;
            }
            else if(codeLines.elementAt(i).isInstruction) {
                filler0 = codeLines.elementAt(i).opcode < 0x10 ? "0" : "";
                blocks.lastElement().operands.addElement(filler0 + Integer.toHexString(codeLines.elementAt(i).opcode).toUpperCase());
                for(int j = 0; j < codeLines.elementAt(i).operands.size(); j++) {
                    filler0 = codeLines.elementAt(i).operands.elementAt(j) < 0x10 ? "0" : "";
                    blocks.lastElement().operands.addElement(filler0 + Integer.toHexString(codeLines.elementAt(i).operands.elementAt(j)).toUpperCase());
                }
            }
            else if(codeLines.elementAt(i).numOperands > 0) {
                for(int j = 0; j < codeLines.elementAt(i).operands.size(); j++) {
                    filler0 = codeLines.elementAt(i).operands.elementAt(j) < 0x10 ? "0" : "";
                    blocks.lastElement().operands.addElement(filler0 + Integer.toHexString(codeLines.elementAt(i).operands.elementAt(j)).toUpperCase());
                }
            }
            i++;
        }
        //Add the length to the code block objects.
        for(int j = 0; j < blocks.size(); j++) {
            blocks.elementAt(j).length = blocks.elementAt(j).operands.size();
        }
    }

    private void FinishOperands() {
        for(int i = 0; i < input.size(); i++) {
            errorLine = i + 1;
            String s = input.elementAt(i);
            String[] tokenizedString = s.split(" ");

            if(tokenizedString[0].toLowerCase().equals("ajmp")) {
                boolean isLabel = false;
                int address = 0, j = 0;
                //Loop to determine if second token is a label or an address.
                while(!isLabel && j < labelChars.length) {
                    if(tokenizedString[1].startsWith(labelChars[j]))
                        isLabel = true;  //Second token is a label
                    j++;
                }
                if(isLabel) { //Convert label to address.
                    try {
                        boolean labelFound = false;
                        j = 0;
                        while(!labelFound && j < codeLines.size()) {
                            if(codeLines.elementAt(j).label.equals(tokenizedString[1])) {
                                labelFound = true;
                                address = codeLines.elementAt(j).address;
                            }
                            j++;
                        }
                        if(!labelFound) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Label not found", errorLine));
                    }
                }
                else { //Convert string to address.
                    try {
                        address = NumberConverter(tokenizedString[1]);
                        if(address > 0x3FFF) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Invalid address", errorLine));
                    }
                }
                //Compute opcode and operand.
                codeLines.elementAt(i).opcode = ((address & 0x700) >> 3) + 1;
                codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
            
            else if(tokenizedString[0].toLowerCase().equals("acall")) {
                boolean isLabel = false;
                int address = 0, j = 0;
                //Loop to determine if second token is a label or an address.
                while(!isLabel && j < labelChars.length) {
                    if(tokenizedString[1].startsWith(labelChars[j]))
                        isLabel = true;  //Second token is a label
                    j++;
                }
                if(isLabel) { //Convert label to address.
                    try {
                        boolean labelFound = false;
                        j = 0;
                        while(!labelFound && j < codeLines.size()) {
                            if(codeLines.elementAt(j).label.equals(tokenizedString[1])) {
                                labelFound = true;
                                address = codeLines.elementAt(j).address;
                            }
                            j++;
                        }
                        if(!labelFound) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Label not found", errorLine));
                    }
                }
                else { //Convert string to address.
                    try {
                        address = NumberConverter(tokenizedString[1]);
                        if(address > 0x3FFF) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Invalid address", errorLine));
                    }
                }
                //Compute opcode and operand.
                codeLines.elementAt(i).opcode = ((address & 0x700) >> 3) + 0x11;
                codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("dptr") &&
                    !tokenizedString[2].toLowerCase().startsWith("#")) {
                boolean isLabel = false;
                int address = 0, j = 0;

                //Loop to determine if third token is a label or an address.
                while(!isLabel && j < labelChars.length) {
                    if(tokenizedString[2].startsWith(labelChars[j]))
                        isLabel = true;  //Second token is a label
                    j++;
                }
                if(isLabel) { //Convert label to address.
                    try {
                        boolean labelFound = false;
                        j = 0;
                        while(!labelFound && j < codeLines.size()) {
                            if(codeLines.elementAt(j).label.equals(tokenizedString[2])) {
                                labelFound = true;
                                address = codeLines.elementAt(j).address;
                            }
                            j++;
                        }
                        if(!labelFound) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Label not found", errorLine));
                    }
                }
                else { //Convert string to address.
                    try {
                        address = NumberConverter(tokenizedString[2]);
                        if(address > 0x3FFF) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Invalid address", errorLine));
                    }
                }
                codeLines.elementAt(i).operands.addElement(address >> 8);
                codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();
                    
                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("@r0") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("@r0") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("@r1") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("@r1") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r0") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r0") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r1") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r1") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r2") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r2") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r3") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r3") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r4") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r4") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r5") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r5") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r6") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r6") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////            
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r7") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[1].toLowerCase().equals("r7") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[2].toLowerCase().startsWith("{") &&
                        codeLines.elementAt(i).opcode == 0x75) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("mov") && tokenizedString[2].toLowerCase().startsWith("}") &&
                        codeLines.elementAt(i).opcode == 0x75) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("add") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("add") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("addc") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("addc") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("subb") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("subb") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("orl") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("orl") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("orl") && tokenizedString[2].toLowerCase().startsWith("{") &&
                        codeLines.elementAt(i).opcode == 0x43) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("orl") && tokenizedString[2].toLowerCase().startsWith("}") &&
                        codeLines.elementAt(i).opcode == 0x43) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("anl") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("anl") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("anl") && tokenizedString[2].toLowerCase().startsWith("{") &&
                        codeLines.elementAt(i).opcode == 0x53) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("anl") && tokenizedString[2].toLowerCase().startsWith("}") &&
                        codeLines.elementAt(i).opcode == 0x53) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("xrl") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("{")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("xrl") && tokenizedString[1].toLowerCase().equals("a") &&
                    tokenizedString[2].toLowerCase().startsWith("}")) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("xrl") && tokenizedString[2].toLowerCase().startsWith("{") &&
                        codeLines.elementAt(i).opcode == 0x63) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address >> 8);
            }
            else if(tokenizedString[0].toLowerCase().equals("xrl") && tokenizedString[2].toLowerCase().startsWith("}") &&
                        codeLines.elementAt(i).opcode == 0x63) {
                int address = 0, j = 0;

                //Convert label to address.
                try {
                    if(tokenizedString[2].length() < 2) throw new Exception();

                    boolean labelFound = false;
                    j = 0;
                    while(!labelFound && j < codeLines.size()) {
                        if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                            labelFound = true;
                            address = codeLines.elementAt(j).address;
                        }
                        j++;
                    }
                    if(!labelFound) throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label not found", errorLine));
                }
            codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(tokenizedString[0].toLowerCase().equals("ljmp") || tokenizedString[0].toLowerCase().equals("lcall")) {
                boolean isLabel = false;
                int address = 0, j = 0;
                //Loop to determine if second token is a label or an address.
                while(!isLabel && j < labelChars.length) {
                    if(tokenizedString[1].startsWith(labelChars[j]))
                        isLabel = true;  //Second token is a label
                    j++;
                }
                if(isLabel) { //Convert label to address.
                    try {
                        boolean labelFound = false;
                        j = 0;
                        while(!labelFound && j < codeLines.size()) {
                            if(codeLines.elementAt(j).label.equals(tokenizedString[1])) {
                                labelFound = true;
                                address = codeLines.elementAt(j).address;
                            }
                            j++;
                        }
                        if(!labelFound) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Label not found", errorLine));
                    }
                }
                else { //Convert string to address.
                    try {
                        address = NumberConverter(tokenizedString[1]);
                        if(address > 0x3FFF) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Invalid address", errorLine));
                    }
                }
                codeLines.elementAt(i).operands.addElement(address >> 8);
                codeLines.elementAt(i).operands.addElement(address & 0xFF);
            }

            else if(codeLines.elementAt(i).opcode == 0xD5 || tokenizedString[0].toLowerCase().equals("jbc") ||
                    tokenizedString[0].toLowerCase().equals("jb") || tokenizedString[0].toLowerCase().equals("jnb")) {
                boolean isLabel = false;
                int address = 0, j = 0;
                int offset = 0;
                //Loop to determine if third token is a label or an offset.
                while(!isLabel && j < labelChars.length) {
                    if(tokenizedString[2].startsWith(labelChars[j]))
                        isLabel = true;  //Third token is a label
                    j++;
                }
                if(isLabel) { //Convert label to address.
                    try {
                        boolean labelFound = false;
                        j = 0;
                        while(!labelFound && j < codeLines.size()) {
                            if(codeLines.elementAt(j).label.equals(tokenizedString[2])) {
                                labelFound = true;
                                address = codeLines.elementAt(j).address;
                            }
                            j++;
                        }
                        if(!labelFound) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Label not found", errorLine));
                    }
                }
                else { //Convert string to address.
                    try {
                        address = NumberConverter(tokenizedString[2]);
                        if(address > 0x3FFF) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Invalid address", errorLine));
                    }
                }

                try { //Try to convert address into offset.
                    if(address - (codeLines.elementAt(i).address + 3) > 0x7F)
                        throw new Exception();
                    if(address - (codeLines.elementAt(i).address + 3) < -0x80)
                        throw new Exception();

                    offset = address - (codeLines.elementAt(i).address + 3);

                    if(offset < 0)
                        offset &= 0x00ff;

                    codeLines.elementAt(i).operands.addElement(offset);
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Offset out of range", errorLine));                    
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("sjmp") || tokenizedString[0].toLowerCase().equals("jnz") ||
                    tokenizedString[0].toLowerCase().equals("jz") || tokenizedString[0].toLowerCase().equals("jc") ||
                    tokenizedString[0].toLowerCase().equals("jnc")) {
                boolean isLabel = false;
                int address = 0, j = 0;
                int offset = 0;
                //Loop to determine if third token is a label or an offset.
                while(!isLabel && j < labelChars.length) {
                    if(tokenizedString[1].startsWith(labelChars[j]))
                        isLabel = true;  //Third token is a label
                    j++;
                }
                if(isLabel) { //Convert label to address.
                    try {
                        boolean labelFound = false;
                        j = 0;
                        while(!labelFound && j < codeLines.size()) {
                            if(codeLines.elementAt(j).label.equals(tokenizedString[1])) {
                                labelFound = true;
                                address = codeLines.elementAt(j).address;
                            }
                            j++;
                        }
                        if(!labelFound) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Label not found", errorLine));
                    }
                }
                else { //Convert string to address.
                    try {
                        address = NumberConverter(tokenizedString[1]);
                        if(address > 0x3FFF) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Invalid address", errorLine));
                    }
                }

                try { //Try to convert address into offset.
                    if(address - (codeLines.elementAt(i).address + 2) > 0x7F)
                        throw new Exception();
                    if(address - (codeLines.elementAt(i).address + 2) < -0x80)
                        throw new Exception();

                    offset = address - (codeLines.elementAt(i).address + 2);

                    if(offset < 0)
                        offset &= 0x00ff;

                    codeLines.elementAt(i).operands.addElement(offset);
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Offset out of range", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("cjne")) {
                boolean isLabel = false;
                int address = 0, j = 0;
                int offset = 0;

                //Determine if third token is upper part of address.
                if(tokenizedString[2].startsWith("{")) {
                    //Convert label to address.
                    try {
                        if(tokenizedString[2].length() < 2) throw new Exception();

                        boolean labelFound = false;
                        j = 0;
                        while(!labelFound && j < codeLines.size()) {
                            if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                                labelFound = true;
                                address = codeLines.elementAt(j).address;
                            }
                            j++;
                        }
                        if(!labelFound) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Label not found", errorLine));
                    }
                    codeLines.elementAt(i).operands.addElement(address >> 8);
                }

                address = 0;
                j = 0;

                //Determine if third token is lower part of address.
                if(tokenizedString[2].startsWith("}")) {
                    //Convert label to address.
                    try {
                        if(tokenizedString[2].length() < 2) throw new Exception();

                        boolean labelFound = false;
                        j = 0;
                        while(!labelFound && j < codeLines.size()) {
                            if(codeLines.elementAt(j).label.equals(tokenizedString[2].substring(1))) {
                                labelFound = true;
                                address = codeLines.elementAt(j).address;
                            }
                            j++;
                        }
                        if(!labelFound) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Label not found", errorLine));
                    }
                    codeLines.elementAt(i).operands.addElement(address & 0xFF);
                }

                address = 0;
                j = 0;

                //Loop to determine if fourth token is a label or an offset.
                while(!isLabel && j < labelChars.length) {                    
                    if(tokenizedString[3].startsWith(labelChars[j]))
                        isLabel = true;  //Third token is a label
                    j++;                    
                }
                if(isLabel) { //Convert label to address.
                    try {
                        boolean labelFound = false;
                        j = 0;
                        while(!labelFound && j < codeLines.size()) {
                            if(codeLines.elementAt(j).label.equals(tokenizedString[3])) {
                                labelFound = true;
                                address = codeLines.elementAt(j).address;
                            }
                            j++;
                        }
                        if(!labelFound) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Label not found", errorLine));
                    }
                }
                else { //Convert string to address.
                    try {
                        address = NumberConverter(tokenizedString[3]);
                        if(address > 0x3FFF) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Invalid address", errorLine));
                    }
                }

                try { //Try to convert address into offset.
                    if(address - (codeLines.elementAt(i).address + 3) > 0x7F)
                        throw new Exception();
                    if(address - (codeLines.elementAt(i).address + 3) < -0x80)
                        throw new Exception();

                    offset = address - (codeLines.elementAt(i).address + 3);

                    if(offset < 0)
                        offset &= 0x00ff;

                    codeLines.elementAt(i).operands.addElement(offset);
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Offset out of range", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("djnz")) {
                 boolean isLabel = false;
                int address = 0, j = 0;
                int offset = 0;
                //Loop to determine if third token is a label or an offset.
                while(!isLabel && j < labelChars.length) {
                    if(tokenizedString[2].startsWith(labelChars[j]))
                        isLabel = true;  //Third token is a label
                    j++;
                }
                if(isLabel) { //Convert label to address.
                    try {
                        boolean labelFound = false;
                        j = 0;
                        while(!labelFound && j < codeLines.size()) {
                            if(codeLines.elementAt(j).label.equals(tokenizedString[2])) {
                                labelFound = true;
                                address = codeLines.elementAt(j).address;
                            }
                            j++;
                        }
                        if(!labelFound) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Label not found", errorLine));
                    }
                }
                else { //Convert string to address.
                    try {
                        address = NumberConverter(tokenizedString[2]);
                        if(address > 0x3FFF) throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Invalid address", errorLine));
                    }
                }

                try { //Try to convert address into offset.
                    if(address - (codeLines.elementAt(i).address + 2) > 0x7F)
                        throw new Exception();
                    if(address - (codeLines.elementAt(i).address + 2) < -0x80)
                        throw new Exception();

                    offset = address - (codeLines.elementAt(i).address + 2);

                    if(offset < 0)
                        offset &= 0x00ff;

                    codeLines.elementAt(i).operands.addElement(offset);
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Offset out of range", errorLine));
                }
            }
        }
    }

    private void SeparateOperands() {
         int temp0;
         int temp1;
         for(int i = 0; i < input.size(); i++) {
             if(codeLines.elementAt(i).operands.size() > 0 && codeLines.elementAt(i).operands.lastElement() > 0xFF) {                 
                 temp0 = temp1 = codeLines.elementAt(i).operands.lastElement();
                 //Move upper 8 bits to temp0.
                 temp0 >>= 8;
                 //Add temp0 to vector.
                 codeLines.elementAt(i).operands.removeElementAt(codeLines.elementAt(i).operands.size() - 1);
                 codeLines.elementAt(i).operands.addElement(temp0);
                 //Move lower 8 bits to temp1.
                 temp1 &= 0xFF;
                 //Add temp1 to vector.
                 codeLines.elementAt(i).operands.addElement(temp1);
             }
         }
    }

    private void AddLabels() {
        int labelLine = 0;
        for(int i = 0; i < input.size(); i++) {
            if((labelLine < labels.size()) && (i + 1 == labels.elementAt(labelLine).lineNumber)) {
                codeLines.elementAt(i).label = labels.elementAt(labelLine).labelName;
                labelLine++;
            }
        }
    }

    private void GetOperands() {
        int currentAddress = 0;
        String temp;
        for(int i = 0; i < input.size(); i++) {            
            errorLine = i + 1;
            String newString = "";
            String s = input.elementAt(i);
            String[] tokenizedString = s.split(" ");

            //Push new CodeLine object into vector.
            codeLines.addElement(new CodeLine());
            //Determine what info is in the current line based on first token.
            if(tokenizedString[0].length() == 0) { //Empty line.
                codeLines.lastElement().address = currentAddress;
            }
                
            else if(tokenizedString[0].toLowerCase().equals(".org")) {
                try { //Make sure proper number of arguments exist.
                    if(tokenizedString.length != 2)
                        throw new Exception();
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Incorrect number of .ORG arguments", errorLine));
                }
                try { //Try to convert text address into integer address.
                    currentAddress = NumberConverter(tokenizedString[1]);
                    codeLines.lastElement().address = currentAddress;
                    //Erase .ORG directive
                    //input.setElementAt("", i);
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Invalid .ORG argument", errorLine));
                }  
            }

            else if(tokenizedString[0].toLowerCase().equals(".db")) {
                codeLines.lastElement().address = currentAddress;
                codeLines.lastElement().numOperands = tokenizedString.length - 1;
                try {
                    for(int j = 1; j < tokenizedString.length; j++) {//Add data bytes to vector.
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[j]));
                        if(NumberConverter(tokenizedString[j]) > 0xFF)
                            throw new Exception();
                    }
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Invalid data byte", errorLine));
                }
                currentAddress += tokenizedString.length - 1;
            }

            else if(tokenizedString[0].toLowerCase().equals("nop")) {
                try {
                    if(tokenizedString.length != 1)
                        throw new Exception();
                    codeLines.lastElement().address = currentAddress;
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x00;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized NOP opcode", errorLine));
                }
            }

            //ajmp must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("ajmp")) {
                try {
                    if(tokenizedString.length != 2)
                        throw new Exception();
                    codeLines.lastElement().address = currentAddress;
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized AJMP opcode", errorLine));
                }
            }

            //ljmp must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("ljmp")) {
                try {
                    if(tokenizedString.length != 2)
                        throw new Exception();
                    codeLines.lastElement().address = currentAddress;
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0x02;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized LJMP opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("rr")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2 || !tokenizedString[1].toLowerCase().equals("a"))
                        throw new Exception();                    
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x03;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized RR opcode", errorLine));
                }
            }
            
            else if(tokenizedString[0].toLowerCase().equals("inc")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x04;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x06;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x07;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x08;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x09;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x0A;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x0B;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x0C;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x0D;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x0E;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x0F;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("dptr")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xA3;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else {
                    try {
                        if(tokenizedString.length != 2)
                            throw new Exception();
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x05;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized INC opcode", errorLine));
                    }                   
                }
            }

            //jbc must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("jbc")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 3)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0x10;
                    codeLines.lastElement().isInstruction = true;
                    codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                    if(NumberConverter(tokenizedString[1]) > 0xFF)
                        throw new Exception();
                    currentAddress += 3;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized JBC opcode", errorLine));
                }
            }

            //acall must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("acall")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized ACALL opcode", errorLine));
                }
            }

            //lcall must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("lcall")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0x12;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized LCALL opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("rrc")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2 || !tokenizedString[1].toLowerCase().equals("a"))
                        throw new Exception();
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x13;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized RRC opcode", errorLine));
                }
            }
            
            else if(tokenizedString[0].toLowerCase().equals("dec")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x14;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x16;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x17;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x18;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x19;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x1A;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x1B;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x1C;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x1D;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x1E;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x1F;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else {
                    try {
                        if(tokenizedString.length != 2)
                            throw new Exception();
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x15;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized INC opcode", errorLine));
                    }
                }
            }

            //jb must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("jb")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 3)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0x20;
                    codeLines.lastElement().isInstruction = true;
                    codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                    if(NumberConverter(tokenizedString[1]) > 0xFF)
                        throw new Exception();
                    currentAddress += 3;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized JB opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("ret")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 1)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x22;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized RET opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("rl")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2 || !tokenizedString[1].toLowerCase().equals("a"))
                        throw new Exception();
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x23;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized RL opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("add")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x26;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x27;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x28;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x29;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x2A;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x2B;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x2C;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x2D;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x2E;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x2F;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x24;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ADD opcode", errorLine));
                    }
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("{")) {
                    //add with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x24;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("}")) {
                    //add with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x24;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("?")) {
                    //add with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x24;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x25;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ADD opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized ADD opcode", errorLine));
                }
            }

            //jb must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("jnb")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 3)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0x30;
                    codeLines.lastElement().isInstruction = true;
                    codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                    if(NumberConverter(tokenizedString[1]) > 0xFF)
                        throw new Exception();
                    currentAddress += 3;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized JNB opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("reti")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 1)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x32;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized RETI opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("rlc")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2 || !tokenizedString[1].toLowerCase().equals("a"))
                        throw new Exception();
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x33;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized RLC opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("addc")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x36;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x37;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x38;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x39;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x3A;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x3B;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x3C;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x3D;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x3E;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x3F;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x34;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ADDC opcode", errorLine));
                    }
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("{")) {
                    //addc with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x34;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("}")) {
                    //addc with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x34;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x35;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ADDC opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized ADDC opcode", errorLine));
                }
            }

            //jc must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("jc")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x40;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized JC opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("orl")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("c") && tokenizedString[2].startsWith("/")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xA0;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ORL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("c")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x72;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ORL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x46;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x47;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x48;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x49;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x4A;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x4B;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x4C;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x4D;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x4E;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x4F;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x44;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ORL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("{")) {
                    //orl a, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x44;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("}")) {
                    //orl a, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x44;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x45;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ORL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x42;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ORL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x43;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ORL opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[2].startsWith("{")) {
                    try {
                        //orl direct, immediate with a label must have labels calculated later!
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x43;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ORL opcode", errorLine));
                    }
                    currentAddress += 3;
                }

                else if(tokenizedString.length == 3 && tokenizedString[2].startsWith("}")) {
                    try {
                        //orl direct, immediate with a label must have labels calculated later!
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x43;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ORL opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized ORL opcode", errorLine));
                }
            }

            //jnc must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("jnc")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x50;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized JNC opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("anl")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("c") && tokenizedString[2].startsWith("/")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xB0;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ANL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("c")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x82;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ANL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x56;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x57;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x58;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x59;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x5A;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x5B;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x5C;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x5D;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x5E;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x5F;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x54;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ANL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("{")) {
                    //anl a, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x54;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("}")) {
                    //anl a, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x54;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x55;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ANL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x52;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ANL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x53;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ANL opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[2].startsWith("{")) {
                    try {
                        //anl direct, immediate with a label must have labels calculated later!
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x53;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ANL opcode", errorLine));
                    }
                    currentAddress += 3;
                }

                else if(tokenizedString.length == 3 && tokenizedString[2].startsWith("}")) {
                    try {
                        //anl direct, immediate with a label must have labels calculated later!
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x53;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized ANL opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized ANL opcode", errorLine));
                }
            }

            //jz must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("jz")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x60;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized JZ opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("xrl")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x66;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x67;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x68;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x69;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x6A;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x6B;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x6C;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x6D;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x6E;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x6F;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x64;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized XRL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("{")) {
                    //xrl a, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x64;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("}")) {
                    //xrl a, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x64;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x65;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized XRL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x62;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized XRL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x63;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized XRL opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[2].startsWith("{")) {
                    try {
                        //xrl direct, immediate with a label must have labels calculated later!
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x63;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized XRL opcode", errorLine));
                    }
                    currentAddress += 3;
                }

                else if(tokenizedString.length == 3 && tokenizedString[2].startsWith("}")) {
                    try {
                        //xrl direct, immediate with a label must have labels calculated later!
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x63;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized XRL opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized XRL opcode", errorLine));
                }
            }

            //jnz must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("jnz")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x70;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized JNZ opcode", errorLine));
                }
            }

            //jmp must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("jmp")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("@a+dptr")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x73;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized JMP opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("mov")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x74;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("{")) {
                    //mov a, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x74;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("}")) {
                    //mov a, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x74;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r0") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x76;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r0") && tokenizedString[2].startsWith("{")) {
                    //mov @r0, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x76;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r0") && tokenizedString[2].startsWith("}")) {
                    //mov @r0, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x76;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r1") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x77;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r1") && tokenizedString[2].startsWith("{")) {
                    //mov @r1, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x77;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r1") && tokenizedString[2].startsWith("}")) {
                    //mov @r1, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x77;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r0") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x78;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r0") && tokenizedString[2].startsWith("{")) {
                    //mov r0, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x78;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r0") && tokenizedString[2].startsWith("}")) {
                    //mov r0, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x78;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r1") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x79;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r1") && tokenizedString[2].startsWith("{")) {
                    //mov r1, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x79;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r1") && tokenizedString[2].startsWith("}")) {
                    //mov r1, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x79;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r2") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x7A;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r2") && tokenizedString[2].startsWith("{")) {
                    //mov r2, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7A;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r2") && tokenizedString[2].startsWith("}")) {
                    //mov r2, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7A;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r3") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x7B;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r3") && tokenizedString[2].startsWith("{")) {
                    //mov r3, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7B;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r3") && tokenizedString[2].startsWith("}")) {
                    //mov r3, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7B;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r4") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x7C;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r4") && tokenizedString[2].startsWith("{")) {
                    //mov r4, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7C;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r4") && tokenizedString[2].startsWith("}")) {
                    //mov r4, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7C;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r5") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x7D;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r5") && tokenizedString[2].startsWith("{")) {
                    //mov r5, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7D;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r5") && tokenizedString[2].startsWith("}")) {
                    //mov r5, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7D;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r6") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x7E;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r6") && tokenizedString[2].startsWith("{")) {
                    //mov r6, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7E;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r6") && tokenizedString[2].startsWith("}")) {
                    //mov r6, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7E;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r7") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x7F;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 2;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r7") && tokenizedString[2].startsWith("{")) {
                    //mov r7, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7F;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r7") && tokenizedString[2].startsWith("}")) {
                    //mov r7, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x7F;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("dptr") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x90;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());

                        //Special case where leading 0x00 is needed.
                        if(NumberConverter(temp) < 0x0100) {
                            codeLines.lastElement().operands.addElement(0x00);
                            codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        }
                        else {
                            codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        }
                        
                        if(NumberConverter(temp) > 0xFFFF)
                            throw new Exception();
                        currentAddress += 3;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //mov dptr with a label must have labels calculated later!
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("dptr")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0x90;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x75;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                        currentAddress += 3;
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[2].startsWith("{")) {
                    try {
                        //mov direct, immediate with a label must have labels calculated later!
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x75;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 3;
                }

                else if(tokenizedString.length == 3 && tokenizedString[2].startsWith("}")) {
                    try {
                        //mov direct, immediate with a label must have labels calculated later!
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x75;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xE6;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xE7;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xE8;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xE9;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xEA;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xEB;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xEC;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xED;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xEE;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xEF;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xE5;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r0") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xF6;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r1") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xF7;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r0") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xF8;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r1") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xF9;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r2") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xFA;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r3") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xFB;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r4") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xFC;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r5") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xFD;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r6") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xFE;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r7") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xFF;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xF5;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("c")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x92;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();                        
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("c")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xA2;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();                        
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r0")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xA6;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r1")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xA7;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r0")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xA8;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r1")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xA9;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r2")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xAA;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r3")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xAB;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r4")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xAC;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r5")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xAD;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r6")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xAE;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r7")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xAF;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("@r0")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x86;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("@r1")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x87;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("r0")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x88;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("r1")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x89;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("r2")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x8A;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("r3")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x8B;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("r4")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x8C;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("r5")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x8D;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("r6")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x8E;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[2].toLowerCase().equals("r7")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x8F;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0x85;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                    }
                    currentAddress += 3;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOV opcode", errorLine));
                } 
            }

            //sjmp must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("sjmp")) {
                codeLines.lastElement().address = currentAddress;
                try {
                    if(tokenizedString.length != 2)
                        throw new Exception();
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x80;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                catch(Exception exception) {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized SJMP opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("movc")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@a+pc")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x83;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@a+dptr")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x93;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOVC opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("div")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("ab")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x84;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized DIV opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("subb")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x96;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x97;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x98;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x99;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x9A;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x9B;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x9C;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x9D;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x9E;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0x9F;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x94;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized SUBB opcode", errorLine));
                    }
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("{")) {
                    //subb a, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x94;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }

                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("}")) {
                    //subb a, immediate with a label must have labels calculated later!
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0x94;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0x95;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized SUBB opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized SUBB opcode", errorLine));
                }
            }
            
            else if(tokenizedString[0].toLowerCase().equals("mul")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("ab")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xA4;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MUL opcode", errorLine));
                }
            }
            
            else if(tokenizedString[0].toLowerCase().equals("cpl")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("c")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xB3;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xF4;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2) {
                     try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xB2;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CPL opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized CPL opcode", errorLine));
                }
            }

            //cjne must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("cjne")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("@r0") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xB6;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("@r0") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xB6;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("@r0") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xB6;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("@r1") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xB7;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("@r1") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xB7;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("@r1") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xB7;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r0") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xB8;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r0") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xB8;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r0") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xB8;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r1") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xB9;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r1") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xB9;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r1") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xB9;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r2") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xBA;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r2") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBA;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r2") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBA;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r3") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xBB;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r3") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBB;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r3") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBB;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r4") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xBC;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r4") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBC;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r4") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBC;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r5") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xBD;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r5") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBD;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r5") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBD;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r6") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xBE;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r6") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBE;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r6") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBE;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r7") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xBF;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r7") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBF;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("r7") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xBF;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("#")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xB4;
                        codeLines.lastElement().isInstruction = true;
                        temp = tokenizedString[2].substring(1, tokenizedString[2].length());
                        codeLines.lastElement().operands.addElement(NumberConverter(temp));
                        if(NumberConverter(temp) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("{")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xB4;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].startsWith("}")) {
                    codeLines.lastElement().numOperands = 3;
                    codeLines.lastElement().opcode = 0xB4;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 3;
                }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(tokenizedString.length == 4 && tokenizedString[1].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xB5;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                    }
                    currentAddress += 3;
                }
                else {
                     if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized CJNE opcode", errorLine));
                }
            }
            
            else if(tokenizedString[0].toLowerCase().equals("push")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xC0;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized PUSH opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else {
                     if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized PUSH opcode", errorLine));
                }
            }
            
            else if(tokenizedString[0].toLowerCase().equals("clr")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("c")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xC3;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xE4;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2) {
                     try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xC2;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized CLR opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized CLR opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("swap")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xC4;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized SWAP opcode", errorLine));
                }
            }
      
            else if(tokenizedString[0].toLowerCase().equals("xch")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xC6;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xC7;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xC8;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xC9;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xCA;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xCB;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xCC;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xCD;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xCE;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xCF;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a")) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xC5;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[2]));
                        if(NumberConverter(tokenizedString[2]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized XCH opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else {

                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized XCH opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("pop")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xD0;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized POP opcode", errorLine));
                    }
                    currentAddress += 2;
                }
                else {
                     if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized POP opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("setb")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("c")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xD3;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 2) {
                    try {
                        codeLines.lastElement().numOperands = 2;
                        codeLines.lastElement().opcode = 0xD2;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized SETB opcode", errorLine));
                    }
                    currentAddress += 2;

                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized SETB opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("da")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 2 && tokenizedString[1].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xD4;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized DA opcode", errorLine));
                }
            }

            //djnz must have labels calculated later!
            else if(tokenizedString[0].toLowerCase().equals("djnz")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r0")) {
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0xD8;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r1")) {
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0xD9;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r2")) {
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0xDA;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r3")) {
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0xDB;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r4")) {
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0xDC;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r5")) {
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0xDD;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r6")) {
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0xDE;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("r7")) {
                    codeLines.lastElement().numOperands = 2;
                    codeLines.lastElement().opcode = 0xDF;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress += 2;
                }
                else if(tokenizedString.length == 3) {
                    try {
                        codeLines.lastElement().numOperands = 3;
                        codeLines.lastElement().opcode = 0xD5;
                        codeLines.lastElement().isInstruction = true;
                        codeLines.lastElement().operands.addElement(NumberConverter(tokenizedString[1]));
                        if(NumberConverter(tokenizedString[1]) > 0xFF)
                            throw new Exception();
                    }
                    catch(Exception exception) {
                        if(!CheckErrorLine(errorLine))
                            error.addElement(new Error("Unrecognized DJNZ opcode", errorLine));
                    }
                    currentAddress += 3;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized DJNZ opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("xchd")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xD6;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xD7;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized SETB opcode", errorLine));
                }
            }

            else if(tokenizedString[0].toLowerCase().equals("movx")) {
                codeLines.lastElement().address = currentAddress;
                if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@dptr")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xE0;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r0")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xE2;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("a") && tokenizedString[2].toLowerCase().equals("@r1")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xE3;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@dptr") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xF0;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r0") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xF2;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else if(tokenizedString.length == 3 && tokenizedString[1].toLowerCase().equals("@r1") && tokenizedString[2].toLowerCase().equals("a")) {
                    codeLines.lastElement().numOperands = 1;
                    codeLines.lastElement().opcode = 0xF3;
                    codeLines.lastElement().isInstruction = true;
                    currentAddress++;
                }
                else {
                    if(!CheckErrorLine(errorLine))
                        error.addElement(new Error("Unrecognized MOVX opcode", errorLine));
                }
            }
            
            else {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Unrecognized opcode", errorLine));
            }
            
            try {
                if(currentAddress > 0x3FFF)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Address out of range", errorLine));
            }
        }
    }

    private void LabelChecker() {
        for(int i = 0; i < input.size(); i++) {
            errorLine = i + 1;
            String newString = "";
            String s = input.elementAt(i);
            String[] tokenizedString = s.split(" ");
            //Fill labels vector.
            if(tokenizedString[0].length() > 0 && tokenizedString[0].charAt(tokenizedString[0].length() - 1) == ':') {
                labels.addElement(new Labels(tokenizedString[0], 0, errorLine));
                //Remove label from input vector.
                if(tokenizedString.length > 1) {
                    for(int j = 1; j < tokenizedString.length; j++)
                        newString = newString + tokenizedString[j] + " ";
                    newString.trim();
                    input.setElementAt(newString, i);
                }
                else
                    input.setElementAt("", i);
            }
        }
        //Remove colons from labels and throw exceptions on empty labels.
        for(int i = 0; i < labels.size(); i++) {
            try {
                errorLine = labels.elementAt(i).lineNumber;
                labels.elementAt(i).labelName = labels.elementAt(i).labelName.split(":")[0];
            }
             catch(Exception exception) {
                 if(!CheckErrorLine(errorLine))
                     error.addElement(new Error("Invalid label", errorLine));
             }
        }
        //Check for reserved words.
        for(int i = 0; i < labels.size(); i++) {
            try {
                errorLine = labels.elementAt(i).lineNumber;
                for(int j = 0; j < reserved.length; j++)
                    if(labels.elementAt(i).labelName.toLowerCase().equals(reserved[j].toLowerCase()))
                        throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Reserved word used in label", errorLine));
            }
        }
        //Check for invalid characters.
        for(int i = 0; i < labels.size(); i++) {
            try {
                errorLine = labels.elementAt(i).lineNumber;
                for(int j = 0; j < invalidChars.length; j++)
                    if(labels.elementAt(i).labelName.contains(invalidChars[j]))
                        throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Invalid character in label", errorLine));
            }
        }
        //Check to make sure label does not start with a number.
        for(int i = 0; i < labels.size(); i++) {
            try {
                errorLine = labels.elementAt(i).lineNumber;
                if(Character.isDigit(labels.elementAt(i).labelName.charAt(0)))
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label starting with a number", errorLine));
            }
        }
        //Check for duplicate labels.
        for(int i = 0; i < labels.size() - 1; i++) {
            try {
                for(int j = i + 1; j < labels.size(); j++) {
                    errorLine = labels.elementAt(j).lineNumber;
                    if(labels.elementAt(i).labelName.equals(labels.elementAt(j).labelName))
                        throw new Exception();
                }
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Duplicate label", errorLine));
            //System.err.println("Duplicate label. Line " + errorLine);
            //System.exit(1);
            }
        }
        //Check for labels already being used as aliases.
        for(int i = 0; i < labels.size(); i++) {
            try{
                for(int j = 0; j < aliases.size(); j++) {
                    errorLine = labels.elementAt(i).lineNumber;
                    if(labels.elementAt(i).labelName.equals(aliases.elementAt(j).aliasString))
                        throw new Exception();
                }
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Label already used as alias", errorLine));
            }
        }
    }

    private void ReplaceAliases() {
        for(int i = 0; i < input.size(); i++) {
            String newString = "";
            String temp;
            String s = input.elementAt(i);
            String[] tokenizedString = s.split(" ");
            for(int j = 0; j < tokenizedString.length; j++) {
                for(int k = 0; k < aliases.size(); k++) {
                    //Check for alias after forward slash.
                    if(tokenizedString[j].startsWith("/")) {
                        temp = tokenizedString[j].substring(1, tokenizedString[j].length());
                        if(aliases.elementAt(k).aliasString.equals(temp))
                            tokenizedString[j] = "/" + aliases.elementAt(k).replacementString;
                    }
                    if(aliases.elementAt(k).aliasString.equals(tokenizedString[j]))
                        tokenizedString[j] = aliases.elementAt(k).replacementString;
                }
            }
            for(int j = 0; j < tokenizedString.length; j++) {
                newString = newString + tokenizedString[j] + " ";
            }
            newString = newString.trim();
            input.setElementAt(newString, i);
        }
    }

    private void ConfigCheck() {
        boolean ebyteFound = false;
        boolean vidFound   = false;
        boolean pidFound   = false;
        boolean didFound   = false;
        boolean cbyteFound = false;

        for(int i = 0; i < input.size(); i++) {
            errorLine = i + 1;
            String s = input.elementAt(i);
            String[] tokenizedString = s.split(" ");
            try {
                //ebyte directive already found.  Throw exception.
                if(tokenizedString[0].toLowerCase().equals(".ebyte") && ebyteFound == true)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Multiple .EBYTE directives", errorLine));
            }
            try {
                //Check if only one ebyte argument.
                if(tokenizedString[0].toLowerCase().equals(".ebyte") && tokenizedString.length != 2)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Incorrect number of .EBYTE arguments", errorLine));
            }
            try {
                //Attempt to convert ebyte argument to an integer.
                if(tokenizedString[0].toLowerCase().equals(".ebyte")) {
                    ebyte = NumberConverter(tokenizedString[1]);
                    ebyteFound = true;
                    //Remove .ebyte directive
                    input.setElementAt("", i);
                    if(ebyte != 0xc0 && ebyte != 0xc2) {
                        ebyte = 0;
                        throw new Exception();
                    }
                }
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Invalid .EBYTE argument", errorLine));
            }

            try {
                //vid directive already found.  Throw exception.
                if(tokenizedString[0].toLowerCase().equals(".vid") && vidFound == true)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Multiple .VID directives", errorLine));
            }
            try {
                //Check if only one vid argument.
                if(tokenizedString[0].toLowerCase().equals(".vid") && tokenizedString.length != 2)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Incorrect number of .VID arguments", errorLine));
            }
            try {
                //Attempt to convert vid argument to an integer.
                if(tokenizedString[0].toLowerCase().equals(".vid")) {
                    vid = NumberConverter(tokenizedString[1]);
                    vidFound = true;
                    //Remove .vid directive
                    input.setElementAt("", i);
                    if(vid < 0 || vid > 65535) {
                        vid = 0;
                        throw new Exception();
                    }
                }
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Invalid .VID argument", errorLine));
            }

            try {
                //pid directive already found.  Throw exception.
                if(tokenizedString[0].toLowerCase().equals(".pid") && pidFound == true)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Multiple .PID directives", errorLine));
            }
            try {
                //Check if only one pid argument.
                if(tokenizedString[0].toLowerCase().equals(".pid") && tokenizedString.length != 2)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Incorrect number of .PID arguments", errorLine));
            }
            try {
                //Attempt to convert pid argument to an integer.
                if(tokenizedString[0].toLowerCase().equals(".pid")) {
                    pid = NumberConverter(tokenizedString[1]);
                    pidFound = true;
                    //Remove .pid directive
                    input.setElementAt("", i);
                    if(pid < 0 || pid > 65535) {
                        pid = 0;
                        throw new Exception();
                    }
                }
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Invalid .PID argument", errorLine));
            }

            try {
                //did directive already found.  Throw exception.
                if(tokenizedString[0].toLowerCase().equals(".did") && didFound == true)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Multiple .DID directives", errorLine));
            }
            try {
                //Check if only one did argument.
                if(tokenizedString[0].toLowerCase().equals(".did") && tokenizedString.length != 2)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Incorrect number of .DID arguments", errorLine));
            }
            try {
                //Attempt to convert did argument to an integer.
                if(tokenizedString[0].toLowerCase().equals(".did")) {
                    did = NumberConverter(tokenizedString[1]);
                    didFound = true;
                    //Remove .did directive
                    input.setElementAt("", i);
                    if(did < 0 || did > 65535) {
                        did = 0;
                        throw new Exception();
                    }
                }
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Invalid .DID argument", errorLine));
            }

            try {
                //cbyte directive already found.  Throw exception.
                if(tokenizedString[0].toLowerCase().equals(".cbyte") && cbyteFound == true)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Multiple .CBYTE directives", errorLine));
            }
            try {
                //Check if only one cbyte argument.
                if(tokenizedString[0].toLowerCase().equals(".cbyte") && tokenizedString.length != 2)
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Incorrect number of .CBYTE arguments", errorLine));
            }
            try {
                //Attempt to convert cbyte argument to an integer.
                if(tokenizedString[0].toLowerCase().equals(".cbyte")) {
                    cbyte = NumberConverter(tokenizedString[1]);
                    cbyteFound = true;
                    //Remove .cbyte directive
                    input.setElementAt("", i);
                    if(cbyte < 0 || cbyte > 0xff) {
                        cbyte = 0;
                        throw new Exception();
                    }
                }
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Invalid .CBYTE argument", errorLine));
            }
        }
        try {
            //Check if ebyte found.
            if(!ebyteFound)
                throw new Exception();
        }
        catch(Exception exception) {
            error.addElement(new Error("EPROM config byte not found", 0));
        }
        try {
            //Check if vid found.
            if(!vidFound)
                throw new Exception();
        }
        catch(Exception exception) {
            error.addElement(new Error("Vendor ID not found", 0));
        }
        try {
            //Check if pid found.
            if(!pidFound)
                throw new Exception();
        }
        catch(Exception exception) {
            error.addElement(new Error("Product ID not found", 0));
        }
        try {
            //Check if did found.
            if(!didFound)
                throw new Exception();
            }
        catch(Exception exception) {
            error.addElement(new Error("Device ID not found", 0));
        }
        try {
            //Check if cbyte found.
            if(!cbyteFound)
                throw new Exception();
            }
        catch(Exception exception) {
            error.addElement(new Error("I2C config byte not found", 0));
        }
    }

    private void AliasReservedCheck() {
        //Check for reserved words.
        for(int i = 0; i < aliases.size(); i++) {
            try {
                errorLine = aliases.elementAt(i).lineNumber;
                for(int j = 0; j < reserved.length; j++)
                    if(aliases.elementAt(i).aliasString.toLowerCase().equals(reserved[j].toLowerCase()))
                        throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Reserved word used in alias", errorLine));
            }            
        }
        //Check for invalid characters.
        for(int i = 0; i < aliases.size(); i++) {
            try {
                errorLine = aliases.elementAt(i).lineNumber;
                for(int j = 0; j < invalidChars.length; j++)
                    if(aliases.elementAt(i).aliasString.contains(invalidChars[j]))
                        throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Invalid character in alias", errorLine));
            }
        }
        //Check to make sure alias does not start with a number.
        for(int i = 0; i < aliases.size(); i++) {
            try {
                errorLine = aliases.elementAt(i).lineNumber;
                if(Character.isDigit(aliases.elementAt(i).aliasString.charAt(0)))
                    throw new Exception();
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Alias starting with a number", errorLine));
            }
        }
        //Check for duplicate aliases.
        for(int i = 0; i < aliases.size() - 1; i++) {
            try {
                for(int j = i + 1; j < aliases.size(); j++) {
                    errorLine = aliases.elementAt(j).lineNumber;
                    if(aliases.elementAt(i).aliasString.equals(aliases.elementAt(j).aliasString))
                        throw new Exception();
                }
            }
            catch(Exception exception) {
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Duplicate alias", errorLine));
            }
        }
    }

    private void AliasVector() {
        for(int i = 0; i < input.size(); i++) {
            try {
                errorLine = i + 1;
                String s = input.elementAt(i);
                String[] tokenizedString = s.split(" ");
                if(tokenizedString[0].toLowerCase().equals(".equ")) {
                    if(tokenizedString.length != 3)
                        throw new Exception();
                    //Add alias to vector.
                    aliases.addElement(new AliasSet(tokenizedString[1], tokenizedString[2], errorLine));
                    //erase alias from input vector.
                    input.setElementAt("", i);
                }
            }
            catch (Exception exception) { //Make sure error has not already been set.
                if(!CheckErrorLine(errorLine))
                    error.addElement(new Error("Invalid .EQU", errorLine));
            }
        }
    }

    private void SpaceRemover() {
        for(int i = 0; i < input.size(); i++) {
            String s = input.elementAt(i);
            String newString = "";
            String[] tokenizedString = s.split(" ");
            for(int j = 0; j < tokenizedString.length; j++) {
                if(tokenizedString[j].length() != 0)
                    newString = newString + tokenizedString[j] + " ";
            }
            newString = newString.trim();
            input.setElementAt(newString, i);
        }
    }

    private void CommaRemover() {
        for(int i = 0; i < input.size(); i++) {
            String s = input.elementAt(i);
            s = s.replaceAll(",", " ");
            input.setElementAt(s, i);
        }
    }

    private void BlankLineRemover() {
        for(int i = 0; i < input.size(); i++) {
            String s = input.elementAt(i);
            s = s.replaceAll("\t", " ");
            s = s.replaceAll("\n", "");
            s = s.trim();
            input.setElementAt(s, i);
        }
    }

    private void CommentRemover() {
        //Trim all comments from input vector.
        for(int i = 0; i < input.size(); i++) {
            String s = input.elementAt(i);
            String[] tokenizedString = s.split(";");
            input.setElementAt(tokenizedString[0], i);
        }
    }

    private void FillInputVector() {
        try {
            //Fill input vector.
            while (read.getFilePointer() != read.length()) {
                input.add(read.readLine());
            }
        }
        catch (IOException ioException) {
            System.err.println("Error reading file: " + ioException);
            System.exit(1);
        }
    }

    private void OpenFiles(String[] args) {
        //Check for proper number of arguments.
        if (args.length < 1 || args.length > 2) {
            System.out.println("\nThe proper usage of the assembler is as follows:\n" +
                    "\njava -jar Assembler_8051.jar [input.file] {[output.file]}\n\n" +
                    "A minimum of one argument is required which is the input text\n" +
                    "file.  The output file name is optional.  If an output file\n" +
                    "name is not specified, the input file name with a .bin\n" +
                    "extension is created.");
            System.exit(1);
        }

        try {
            //Open input text file to read from.
            read = new RandomAccessFile(args[0], "r");
        }
        catch (IOException ioException) {
            System.err.println("Error opening file: " + ioException);
            System.exit(1);
        }

        try {
            //Open output .bin file to write to.
            if(args.length == 2)
                write = new RandomAccessFile(args[1], "rw");
            else
                write = new RandomAccessFile(args[0].substring(0, args[0].indexOf('.')) + ".bin", "rw");
        }
        catch (IOException ioException) {
            System.err.println("Error opening file: " + ioException);
            System.exit(1);
        }
    }

    private void PrintErrors() {
        if(error.isEmpty()) {
            System.out.println("\nNo errors detected.");
            return;
        }
        System.out.println("\nErrors:");
        for(int i = 0; i < error.size(); i++) {
            System.out.println(error.elementAt(i).errorString + " " + error.elementAt(i).errorLine);
        }
        System.out.println("Total errors: " + error.size());
        System.exit(1);
    }

    int NumberConverter(String s) {
        int number = 0;
        //Radix of number
        int numberType = 0;
        //Check if hex number.
        if(s.charAt(0) == '$') {
            numberType = 16;
            s = s.substring(1);
        }
        //Check if binary number.
        else if(s.charAt(0) == '%') {
            numberType = 2;
            s = s.substring(1);
        }
        else
            numberType = 10;
        //Attempt to parse.  Exception will be caught by calling method.
        number = Integer.parseInt(s, numberType);
        return number;
    }

    boolean CheckErrorLine(int errorLine) {
        //Make sure only one type of error per line.
        for(int i = 0; i < error.size(); i++)
            if(error.elementAt(i).errorLine == errorLine)
                return true;
        return false;
    }

    private void VectorPrint() {
        //Print input vector.  Used during development.
        System.out.println("\nInput Vector:");
        int labelLine = 0;
        String address, filler0, filler1, filler2;
        for(int i = 0; i < input.size(); i++) {
            address = Integer.toHexString(codeLines.elementAt(i).address);
            filler0 = (codeLines.elementAt(i).address < 0x1000) ? "0" : "";
            filler1 = (codeLines.elementAt(i).address < 0x100)  ? "0" : "";
            filler2 = (codeLines.elementAt(i).address < 0x10)   ? "0" : "";
            System.out.print("<Line #: " + (i + 1) + ", Address: $" + filler0 + filler1 + filler2 + address +
                             ", Label: " + codeLines.elementAt(i).label + ", OpCount: " + codeLines.elementAt(i).numOperands +
                             ", Operands:");

            if(codeLines.elementAt(i).isInstruction) {
                filler0 = codeLines.elementAt(i).opcode < 0x10 ? "0" : "";
                System.out.print(" $" + filler0 + Integer.toHexString(codeLines.elementAt(i).opcode));
            }

            for(int j = 0; j < codeLines.elementAt(i).operands.size(); j++) {
                filler0 = codeLines.elementAt(i).operands.elementAt(j) < 0x10 ? "0" : "";
                System.out.print(" $" + filler0 + Integer.toHexString(codeLines.elementAt(i).operands.elementAt(j)));
            }

            System.out.print("> *** ");
           
            System.out.println(input.elementAt(i));
        }
    }

    private void CodeBlockPrint() {
        String filler0, filler1, filler2;
        int opCount;
        for(int i = 0; i < blocks.size(); i++) {
            opCount = 0;
            filler0 = blocks.elementAt(i).origin < 0x1000 ? "0" : "";
            filler1 = blocks.elementAt(i).origin < 0x100  ? "0" : "";
            filler2 = blocks.elementAt(i).origin < 0x10   ? "0" : "";
            System.out.println(".org " + filler0 + filler1 + filler2 + Integer.toHexString(blocks.elementAt(i).origin) +
                               ", " +  blocks.elementAt(i).addressHi +  blocks.elementAt(i).addressLo + ", size: " +
                               blocks.elementAt(i).length + " bytes, " + blocks.elementAt(i).sizeHi + blocks.elementAt(i).sizeLo + "\n");
            while(opCount < blocks.elementAt(i).operands.size()) {
                System.out.print(blocks.elementAt(i).operands.elementAt(opCount) + ", ");
                if(opCount == blocks.elementAt(i).operands.size() - 1)
                    System.out.println("\n");

                if((opCount + 1) % 16 == 0 && opCount != 0)
                    System.out.println("");
                opCount++;
            }
        }
    }
    
    public static void main(String[] args) {
        Assembler_8051 assembler = new Assembler_8051(args);
    }
}
