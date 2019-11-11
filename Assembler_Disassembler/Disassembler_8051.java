package nick.disassembler;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Vector;

//A simple class to store instruction details.
class Instruction {
    public int numOps;      //Number of operands of this instruction.
    int ops[] = new int[3]; //Maximum of 3 operands per instruction.
    public int address;     //The memory address of the instruction.
    boolean isInst = false; //Determine if byte is instruction or data.
    public String instName; //Name of this instruction.
    public StringBuffer instDesc = new StringBuffer();
}

//A simple class to store code blocks.
class CodeBlock {
    public int originLo;
    public int originHi;
    public int origin;
    public int lengthLo;
    public int lengthHi;
    public int length;
    public Vector<Integer> codeBlock = new Vector<Integer>();
    public Vector<Instruction> instruction = new Vector<Instruction>();
}

public class Disassembler_8051 {
    //Input file.
    private RandomAccessFile read;
    //Output file.
    private RandomAccessFile write;
    //Input vector for lines from dump file.
    private Vector<String> inputLines = new Vector<String>();
    //Input vector for bytes from dump file.
    private Vector<Integer> inputBytes = new Vector<Integer>();
    //Line where error occurs.
    private int errorLine;
    //Byte where end sequence starts.
    private int endSeqIndex;
    //Vector of code blocks.
    private Vector<CodeBlock> codeBlocks = new Vector<CodeBlock>();
    //Vector of addresses to jump to while looking for instructions.
    //private Vector<Integer> jumpQueue = new Vector<Integer>();
    //Instruction names.
    private String instructions[] = {"nop  ", "ajmp ", "ljmp ", "rr   ", "inc  ", "inc  ", "inc  ", "inc  ",
                                     "inc  ", "inc  ", "inc  ", "inc  ", "inc  ", "inc  ", "inc  ", "inc  ",
                                     "jbc  ", "acall", "lcall", "rrc  ", "dec  ", "dec  ", "dec  ", "dec  ",
                                     "dec  ", "dec  ", "dec  ", "dec  ", "dec  ", "dec  ", "dec  ", "dec  ",
                                     "jb   ", "ajmp ", "ret  ", "rl   ", "add  ", "add  ", "add  ", "add  ",
                                     "add  ", "add  ", "add  ", "add  ", "add  ", "add  ", "add  ", "add  ",
                                     "jnb  ", "acall", "reti ", "rlc  ", "addc ", "addc ", "addc ", "addc ",
                                     "addc ", "addc ", "addc ", "addc ", "addc ", "addc ", "addc ", "addc ",
                                     "jc   ", "ajmp ", "orl  ", "orl  ", "orl  ", "orl  ", "orl  ", "orl  ",
                                     "orl  ", "orl  ", "orl  ", "orl  ", "orl  ", "orl  ", "orl  ", "orl  ",
                                     "jnc  ", "acall", "anl  ", "anl  ", "anl  ", "anl  ", "anl  ", "anl  ",
                                     "anl  ", "anl  ", "anl  ", "anl  ", "anl  ", "anl  ", "anl  ", "anl  ",
                                     "jz   ", "ajmp ", "xrl  ", "xrl  ", "xrl  ", "xrl  ", "xrl  ", "xrl  ",
                                     "xrl  ", "xrl  ", "xrl  ", "xrl  ", "xrl  ", "xrl  ", "xrl  ", "xrl  ",
                                     "jnz  ", "acall", "orl  ", "jmp  ", "mov  ", "mov  ", "mov  ", "mov  ",
                                     "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ",
                                     "sjmp ", "ajmp ", "anl  ", "movc ", "div  ", "mov  ", "mov  ", "mov  ",
                                     "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ",
                                     "mov  ", "acall", "mov  ", "movc ", "subb ", "subb ", "subb ", "subb ",
                                     "subb ", "subb ", "subb ", "subb ", "subb ", "subb ", "subb ", "subb ",
                                     "orl  ", "ajmp ", "mov  ", "inc  ", "mul  ", "???  ", "mov  ", "mov  ",
                                     "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ",
                                     "anl  ", "acall", "cpl  ", "cpl  ", "cjne ", "cjne ", "cjne ", "cjne ",
                                     "cjne ", "cjne ", "cjne ", "cjne ", "cjne ", "cjne ", "cjne ", "cjne ",
                                     "push ", "ajmp ", "clr  ", "clr  ", "swap ", "xch  ", "xch  ", "xch  ",
                                     "xch  ", "xch  ", "xch  ", "xch  ", "xch  ", "xch  ", "xch  ", "xch  ",
                                     "pop  ", "acall", "setb ", "setb ", "da   ", "djnz ", "xchd ", "xchd ",
                                     "djnz ", "djnz ", "djnz ", "djnz ", "djnz ", "djnz ", "djnz ", "djnz ",
                                     "movx ", "ajmp ", "movx ", "movx ", "clr  ", "mov  ", "mov  ", "mov  ",
                                     "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ",
                                     "movx ", "acall", "movx ", "movx ", "cpl  ", "mov  ", "mov  ", "mov  ",
                                     "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  ", "mov  "};

    //Number of operands per instruction above.
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
    
    //Names of bits able to be manupulated with bit commands.
    private String bits[] = {"R20_0"  , "R20_1"  , "R20_2"  , "R20_3"  , "R20_4"  , "R20_5"  , "R20_6"  , "R20_7"  ,
                             "R21_0"  , "R21_1"  , "R21_2"  , "R21_3"  , "R21_4"  , "R21_5"  , "R21_6"  , "R21_7"  ,
                             "R22_0"  , "R22_1"  , "R22_2"  , "R22_3"  , "R22_4"  , "R22_5"  , "R22_6"  , "R22_7"  ,
                             "R23_0"  , "R23_1"  , "R23_2"  , "R23_3"  , "R23_4"  , "R23_5"  , "R23_6"  , "R23_7"  ,
                             "R24_0"  , "R24_1"  , "R24_2"  , "R24_3"  , "R24_4"  , "R24_5"  , "R24_6"  , "R24_7"  ,
                             "R25_0"  , "R25_1"  , "R25_2"  , "R25_3"  , "R25_4"  , "R25_5"  , "R25_6"  , "R25_7"  ,
                             "R26_0"  , "R26_1"  , "R26_2"  , "R26_3"  , "R26_4"  , "R26_5"  , "R26_6"  , "R26_7"  ,
                             "R27_0"  , "R27_1"  , "R27_2"  , "R27_3"  , "R27_4"  , "R27_5"  , "R27_6"  , "R27_7"  ,
                             "R28_0"  , "R28_1"  , "R28_2"  , "R28_3"  , "R28_4"  , "R28_5"  , "R28_6"  , "R28_7"  ,
                             "R29_0"  , "R29_1"  , "R29_2"  , "R29_3"  , "R29_4"  , "R29_5"  , "R29_6"  , "R29_7"  ,
                             "R2A_0"  , "R2A_1"  , "R2A_2"  , "R2A_3"  , "R2A_4"  , "R2A_5"  , "R2A_6"  , "R2A_7"  ,
                             "R2B_0"  , "R2B_1"  , "R2B_2"  , "R2B_3"  , "R2B_4"  , "R2B_5"  , "R2B_6"  , "R2B_7"  ,
                             "R2C_0"  , "R2C_1"  , "R2C_2"  , "R2C_3"  , "R2C_4"  , "R2C_5"  , "R2C_6"  , "R2C_7"  ,
                             "R2D_0"  , "R2D_1"  , "R2D_2"  , "R2D_3"  , "R2D_4"  , "R2D_5"  , "R2D_6"  , "R2D_7"  ,
                             "R2E_0"  , "R2E_1"  , "R2E_2"  , "R2E_3"  , "R2E_4"  , "R2E_5"  , "R2E_6"  , "R2E_7"  ,
                             "R2F_0"  , "R2F_1"  , "R2F_2"  , "R2F_3"  , "R2F_4"  , "R2F_5"  , "R2F_6"  , "R2F_7"  ,
                             "IOA_0"  , "IOA_1"  , "IOA_2"  , "IOA_3"  , "IOA_4"  , "IOA_5"  , "IOA_6"  , "IOA_7"  ,
                             "TCON_0" , "TCON_1" , "TCON_2" , "TCON_3" , "TCON_4" , "TCON_5" , "TCON_6" , "TCON_7" ,
                             "IOB_0"  , "IOB_1"  , "IOB_2"  , "IOB_3"  , "IOB_4"  , "IOB_5"  , "IOB_6"  , "IOB_7"  ,
                             "SCON0_0", "SCON0_1", "SCON0_2", "SCON0_3", "SCON0_4", "SCON0_5", "SCON0_6", "SCON0_7",
                             "IOC_0"  , "IOC_1"  , "IOC_2"  , "IOC_3"  , "IOC_4"  , "IOC_5"  , "IOC_6"  , "IOC_7"  ,
                             "IE_0"   , "IE_1"   , "IE_2"   , "IE_3"   , "IE_4"   , "IE_5"   , "IE_6"   , "IE_7"   ,
                             "IOD_0"  , "IOD_1"  , "IOD_2"  , "IOD_3"  , "IOD_4"  , "IOD_5"  , "IOD_6"  , "IOD_7"  ,
                             "IP_0"   , "IP_1"   , "IP_2"   , "IP_3"   , "IP_4"   , "IP_5"   , "IP_6"   , "IP_7"   ,
                             "SCON1_0", "SCON1_1", "SCON1_2", "SCON1_3", "SCON1_4", "SCON1_5", "SCON1_6", "SCON1_7",
                             "T2CON_0", "T2CON_1", "T2CON_2", "T2CON_3", "T2CON_4", "T2CON_5", "T2CON_6", "T2CON_7",
                             "PSW_0"  , "PSW_1"  , "PSW_2"  , "PSW_3"  , "PSW_4"  , "PSW_5"  , "PSW_6"  , "PSW_7"  ,
                             "EICON_0", "EICON_1", "EICON_2", "EICON_3", "EICON_4", "EICON_5", "EICON_6", "EICON_7",
                             "ACC_0"  , "ACC_1"  , "ACC_2"  , "ACC_3"  , "ACC_4"  , "ACC_5"  , "ACC_6"  , "ACC_7"  ,
                             "EIE_0"  , "EIE_1"  , "EIE_2"  , "EIE_3"  , "EIE_4"  , "EIE_5"  , "EIE_6"  , "EIE_7"  ,
                             "B_0"    , "B_1"    , "B_2"    , "B_3"    , "B_4"    , "B_5"    , "B_6"    , "B_7"    ,
                             "EIP_0"  , "EIP_1"  , "EIP_2"  , "EIP_3"  , "EIP_4"  , "EIP_5"  , "EIP_6"  , "EIP_7"  };

    //Directly addressable registers.
    private String daRegs[] = {"R0BNK0", "R1BNK0" , "R2BNK0"    , "R3BNK0"     , "R4BNK0"     , "R5BNK0"     , "R6BNK0"      , "R7BNK0"        ,
                               "R0BNK1", "R1BNK1" , "R2BNK1"    , "R3BNK1"     , "R4BNK1"     , "R5BNK1"     , "R6BNK1"      , "R7BNK1"        ,
                               "R0BNK2", "R1BNK2" , "R2BNK2"    , "R3BNK2"     , "R4BNK2"     , "R5BNK2"     , "R6BNK2"      , "R7BNK2"        ,
                               "R0BNK3", "R1BNK3" , "R2BNK3"    , "R3BNK3"     , "R4BNK3"     , "R5BNK3"     , "R6BNK3"      , "R7BNK3"        ,
                               "R20"   , "R21"    , "R22"       , "R23"        , "R24"        , "R25"        , "R26"         , "R27"           ,
                               "R28"   , "R29"    , "R2A"       , "R2B"        , "R2C"        , "R2D"        , "R2E"         , "R2F"           ,
                               "R30"   , "R31"    , "R32"       , "R33"        , "R34"        , "R35"        , "R36"         , "R37"           ,
                               "R38"   , "R39"    , "R3A"       , "R3B"        , "R3C"        , "R3D"        , "R3E"         , "R3F"           ,
                               "R40"   , "R41"    , "R42"       , "R43"        , "R44"        , "R45"        , "R46"         , "R47"           ,
                               "R48"   , "R49"    , "R4A"       , "R4B"        , "R4C"        , "R4D"        , "R4E"         , "R4F"           ,
                               "R50"   , "R51"    , "R52"       , "R53"        , "R54"        , "R55"        , "R56"         , "R57"           ,
                               "R58"   , "R59"    , "R5A"       , "R5B"        , "R5C"        , "R5D"        , "R5E"         , "R5F"           ,
                               "R60"   , "R61"    , "R62"       , "R63"        , "R64"        , "R65"        , "R66"         , "R67"           ,
                               "R68"   , "R69"    , "R6A"       , "R6B"        , "R6C"        , "R6D"        , "R6E"         , "R6F"           ,
                               "R70"   , "R71"    , "R72"       , "R73"        , "R74"        , "R75"        , "R76"         , "R77"           ,
                               "R78"   , "R79"    , "R7A"       , "R7B"        , "R7C"        , "R7D"        , "R7E"         , "R7F"           ,
                               "IOA"   , "SP"     , "DPL0"      , "DPH0"       , "DPL1"       , "DPH1"       , "DPS"         , "PCON"          ,
                               "TCON"  , "TMOD"   , "TL0"       , "TL1"        , "TH0"        , "TH1"        , "CKCON"       , "__8F"          ,
                               "IOB"   , "EXIF"   , "MPAGE"     , "__93"       , "__94"       , "__95"       , "__96"        , "__97"          ,
                               "SCON0" , "SBUF0"  , "AUTOPTRH1" , "AUTOPTRL1"  , "__9C"       , "AUTOPTRH2"  , "AUTOPTRL2"   , "__9F"          ,
                               "IOC"   , "INT2CLR", "INT4CLR"   , "__A3"       , "__A4"       , "__A5"       , "__A6"        , "__A7"          ,
                               "IE"    , "__A9"   , "EP2468STAT", "EP24FIFOLGS", "EP68FIFOLGS", "__AD"       , "__AE"        , "AUTOPTR_SETUP" ,
                               "IOD"   , "IOE"    , "OEA"       , "OEB"        , "OEC"        , "OED"        , "OEE"         , "__B7"          ,
                               "IP"    , "__B9"   , "EP01STAT"  , "GPIFTRIG"   , "__BC"       , "GPIFSGLDATH", "GPIFSGLDATLX", "GPIFSGLDATLNOX",
                               "SCON1" , "SBUF1"  , "__C2"      , "__C3"       , "__C4"       , "__C5"       , "__C6"        , "__C7"          ,
                               "T2CON" , "__C9"   , "RCAP2L"    , "RCAP2H"     , "TL2"        , "TH2"        , "__CE"        , "__CF"          ,
                               "PSW"   , "__D1"   , "__D2"      , "__D3"       , "__D4"       , "__D5"       , "__D6"        , "__D7"          ,
                               "EICON" , "__D9"   , "__DA"      , "__DB"       , "__DC"       , "__DD"       , "__DE"        , "__DF"          ,
                               "ACC"   , "__E1"   , "__E2"      , "__E3"       , "__E4"       , "__E5"       , "__E6"        , "__E7"          ,
                               "EIE"   , "__E9"   , "__EA"      , "__EB"       , "__EC"       , "__ED"       , "__EE"        , "__EF"          ,
                               "B"     , "__F1"   , "__F2"      , "__F3"       , "__F4"       , "__F5"       , "__F6"        , "__F7"          ,
                               "EIP"   , "__F9"   , "__FA"      , "__FB"       , "__FC"       , "__FD"       , "__FE"        , "__FF"          };

    Disassembler_8051(String[] args) {
        //Open input and output files.
        OpenFiles(args);
        //Get lines of hex data from the raw dump file.
        FillInputLines();
        //Extract bytes of data from input lines.
        FillInputBytes();
        //Add the first 8 bytes to the output file.
        FillHeader();
        //Exit if EPROM byte is 0xC0...nothing left to do.
        if(inputBytes.elementAt(0) == 0xC0) return;
        //Add all the .equ lines for the Cypress EZ-USB chip.
        WriteDefines();
        //Find the end sequence.
        FindEndSequence();
        //Extract individual code blocks.
        GetCodeBlocks();
        //Combine sequential code blocks.
        CombineCodeBlocks();
        //Separate out the operands into a new vector.
        GetOperands();
        //Place interrupt vectors into the jump queue.  Separates code from data.
        //InitJumpQueue();
        //Fill out instruction string buffer.
        PrintToStringBuffer();
        //Save disassembly to file.
        PrintToFile();  
    }

    private void PrintToFile() {
        String addfil0, addfil1, addfil2;

        try {
            for(int i = 0; i < codeBlocks.size(); i++) {
                addfil0 = codeBlocks.elementAt(i).origin < 0x10   ? "0" : "";
                addfil1 = codeBlocks.elementAt(i).origin < 0x100  ? "0" : "";
                addfil2 = codeBlocks.elementAt(i).origin < 0x1000 ? "0" : "";
                write.writeBytes("\n.org\t$" + addfil2 + addfil1 + addfil0 + Integer.toHexString(codeBlocks.elementAt(i).origin));
                write.writeBytes("\t;Size: $" + Integer.toHexString(codeBlocks.elementAt(i).length) + "\n\n");

                for(int j = 0; j < codeBlocks.elementAt(i).instruction.size(); j++) {
                    write.writeBytes(codeBlocks.elementAt(i).instruction.elementAt(j).instDesc.toString() + "\n");
                }
            }
        }
        catch (IOException ioException) {
            System.err.println("Error writing to file: " + ioException);
            System.exit(1);
        }
    }

    private void PrintToStringBuffer() {
        String addfil0, addfil1, addfil2;
        String filler0, filler1, filler2;
        for(int i = 0; i < codeBlocks.size(); i++) {
            for(int j = 0; j < codeBlocks.elementAt(i).instruction.size(); j++) {
                addfil0 = codeBlocks.elementAt(i).instruction.elementAt(j).address < 0x10   ? "0" : "";
                addfil1 = codeBlocks.elementAt(i).instruction.elementAt(j).address < 0x100  ? "0" : "";
                addfil2 = codeBlocks.elementAt(i).instruction.elementAt(j).address < 0x1000 ? "0" : "";

                //Put address label in string buffer.
                codeBlocks.elementAt(i).instruction.elementAt(j).instDesc.append("L").append(addfil2).append(addfil1).append(
                        addfil0).append(Integer.toHexString(codeBlocks.elementAt(i).instruction.elementAt(j).address)).append(
                        ": ");
                //Add instruction name to string buffer.
                codeBlocks.elementAt(i).instruction.elementAt(j).instDesc.append(instructions[codeBlocks.elementAt(
                        i).instruction.elementAt(j).ops[0]]).append(" ");

                AddInstDetails(codeBlocks.elementAt(i).instruction.elementAt(j));

                //Add binary instruction in comments.
                while(codeBlocks.elementAt(i).instruction.elementAt(j).instDesc.length() < 40)
                    codeBlocks.elementAt(i).instruction.elementAt(j).instDesc.append(" ");
                codeBlocks.elementAt(i).instruction.elementAt(j).instDesc.append(";");

                if(codeBlocks.elementAt(i).instruction.elementAt(j).numOps == 1) {
                    filler0 = codeBlocks.elementAt(i).instruction.elementAt(j).ops[0] < 0x10 ? "0" : "";
                    codeBlocks.elementAt(i).instruction.elementAt(j).instDesc.append("$").append(filler0).append(
                            Integer.toHexString(codeBlocks.elementAt(i).instruction.elementAt(j).ops[0]));
                }
                else if (codeBlocks.elementAt(i).instruction.elementAt(j).numOps == 2) {
                    filler0 = codeBlocks.elementAt(i).instruction.elementAt(j).ops[0] < 0x10 ? "0" : "";
                    filler1 = codeBlocks.elementAt(i).instruction.elementAt(j).ops[1] < 0x10 ? "0" : "";
                    codeBlocks.elementAt(i).instruction.elementAt(j).instDesc.append("$").append(filler0).append(
                            Integer.toHexString(codeBlocks.elementAt(i).instruction.elementAt(j).ops[0])).append(
                            " $").append(filler1).append(Integer.toHexString(codeBlocks.elementAt(i).instruction.elementAt(j).ops[1]));
                }
                else {
                    filler0 = codeBlocks.elementAt(i).instruction.elementAt(j).ops[0] < 0x10 ? "0" : "";
                    filler1 = codeBlocks.elementAt(i).instruction.elementAt(j).ops[1] < 0x10 ? "0" : "";
                    filler2 = codeBlocks.elementAt(i).instruction.elementAt(j).ops[2] < 0x10 ? "0" : "";
                    codeBlocks.elementAt(i).instruction.elementAt(j).instDesc.append("$").append(filler0).append(
                            Integer.toHexString(codeBlocks.elementAt(i).instruction.elementAt(j).ops[0])).append(
                            " $").append(filler1).append(Integer.toHexString(codeBlocks.elementAt(i).instruction.elementAt(j).ops[1])).append(
                            " $").append(filler2).append(Integer.toHexString(codeBlocks.elementAt(i).instruction.elementAt(j).ops[2]));
                }
            }           
        }
    }

    void AddInstDetails(Instruction inst) {
        String filler0, filler1, filler2;
        int address;
        
        switch(inst.ops[0]) {
            case 0x00: //No further info required.
            case 0x22:
            case 0x32:
            case 0xA5: //Invalid opcode.  No further info required.
                break;
                
            case 0x01:
            case 0x02:
            case 0x21:
            case 0x41:
            case 0x61:
            case 0x80:
            case 0x81:
            case 0xA1:
            case 0xC1:
            case 0xE1:
                address = CalcJumpAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("$").append(filler0).append(filler1).append(
                        filler2).append(Integer.toHexString(address));
                break;
                
            case 0x03:
            case 0x04:
            case 0x13:
            case 0x14:
            case 0x23:
            case 0x33:
            case 0xC4:
            case 0xD4:
            case 0xE4:
            case 0xF4:
                inst.instDesc.append("a");
                break;

            case 0x05:
            case 0x15:
            case 0xC0:
            case 0xD0:
                inst.instDesc.append(daRegs[inst.ops[1]]);
                break;

            case 0x06:
            case 0x16:
                inst.instDesc.append("@r0");
                break;
                
            case 0x07:
            case 0x17:
                inst.instDesc.append("@r1");
                break;

            case 0x08:
            case 0x18:
                inst.instDesc.append("r0");
                break;

            case 0x09:
            case 0x19:
                inst.instDesc.append("r1");
                break;
                
            case 0x0A:
            case 0x1A:
                inst.instDesc.append("r2");
                break;

            case 0x0B:
            case 0x1B:
                inst.instDesc.append("r3");
                break;

            case 0x0C:
            case 0x1C:
                inst.instDesc.append("r4");
                break;

            case 0x0D:
            case 0x1D:
                inst.instDesc.append("r5");
                break;

            case 0x0E:
            case 0x1E:
                inst.instDesc.append("r6");
                break;

            case 0x0F:
            case 0x1F:
                inst.instDesc.append("r7");
                break;
                
            case 0x10:
            case 0x20:
            case 0x30:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(bits[inst.ops[1]]).append(", $").append(filler0).append(filler1).append(
                        filler2).append(Integer.toHexString(address));
                break;

            case 0x11:
            case 0x12:
            case 0x31:
            case 0x51:
            case 0x71:
            case 0x91:
            case 0xB1:
            case 0xD1:
            case 0xF1:
                address = CalcCallAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("$").append(filler0).append(filler1).append(
                        filler2).append(Integer.toHexString(address));
                break;
           
            case 0x24:
            case 0x34:
            case 0x44:
            case 0x54:
            case 0x64:
            case 0x74:
            case 0x94:
                filler0 = inst.ops[1] < 0x10 ? "0" : "";
                inst.instDesc.append("a, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;
                
            case 0x25:
            case 0x35:
            case 0x45:
            case 0x55:
            case 0x65:
            case 0x95:
            case 0xC5:
            case 0xE5:
                inst.instDesc.append("a, ").append(daRegs[inst.ops[1]]);
                break;
                
            case 0x26:
            case 0x36:
            case 0x46:
            case 0x56:
            case 0x66:
            case 0x96:
            case 0xC6:
            case 0xD6:
            case 0xE2:
            case 0xE6:
                inst.instDesc.append("a, @r0");
                break;

            case 0x27:
            case 0x37:
            case 0x47:
            case 0x57:
            case 0x67:
            case 0x97:
            case 0xC7:
            case 0xD7:
            case 0xE3:
            case 0xE7:
                inst.instDesc.append("a, @r1");
                break;

            case 0x28:
            case 0x38:
            case 0x48:
            case 0x58:
            case 0x68:
            case 0x98:
            case 0xC8:
            case 0xE8:
                inst.instDesc.append("a, r0");
                break;

            case 0x29:
            case 0x39:
            case 0x49:
            case 0x59:
            case 0x69:
            case 0x99:
            case 0xC9:
            case 0xE9:
                inst.instDesc.append("a, r1");
                break;

            case 0x2A:
            case 0x3A:
            case 0x4A:
            case 0x5A:
            case 0x6A:
            case 0x9A:
            case 0xCA:
            case 0xEA:
                inst.instDesc.append("a, r2");
                break;

            case 0x2B:
            case 0x3B:
            case 0x4B:
            case 0x5B:
            case 0x6B:
            case 0x9B:
            case 0xCB:
            case 0xEB:
                inst.instDesc.append("a, r3");
                break;

            case 0x2C:
            case 0x3C:
            case 0x4C:
            case 0x5C:
            case 0x6C:
            case 0x9C:
            case 0xCC:
            case 0xEC:
                inst.instDesc.append("a, r4");
                break;

            case 0x2D:
            case 0x3D:
            case 0x4D:
            case 0x5D:
            case 0x6D:
            case 0x9D:
            case 0xCD:
            case 0xED:
                inst.instDesc.append("a, r5");
                break;

            case 0x2E:
            case 0x3E:
            case 0x4E:
            case 0x5E:
            case 0x6E:
            case 0x9E:
            case 0xCE:
            case 0xEE:
                inst.instDesc.append("a, r6");
                break;

            case 0x2F:
            case 0x3F:
            case 0x4F:
            case 0x5F:
            case 0x6F:
            case 0x9F:
            case 0xCF:
            case 0xEF:
                inst.instDesc.append("a, r7");
                break;

            case 0x40:
            case 0x50:
            case 0x60:
            case 0x70:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("$").append(filler0).append(filler1).append(
                        filler2).append(Integer.toHexString(address));
                break;
                
            case 0x42:
            case 0x52:
            case 0x62:
            case 0xF5:
                try {
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", a");
                }
                catch (Exception e) {
                    inst.instDesc.append("---ERROR---");
                }
                break;

            case 0x43:
            case 0x53:
            case 0x63:
            case 0x75:
                filler0 = inst.ops[2] < 0x10   ? "0" : "";
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", #$").append(
                        filler0).append(Integer.toHexString(inst.ops[2]));
                break;

            case 0x72:
            case 0x82:
            case 0xA2:
                inst.instDesc.append("c, ").append(bits[inst.ops[1]]);
                break;
                
            case 0x73:
                inst.instDesc.append("@a+dptr");
                break;
                
            case 0x76:
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("@r0, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;
                
            case 0x77:
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("@r1, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;

            case 0x78:
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r0, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;

            case 0x79:
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r1, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;

            case 0x7A:
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r2, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;

            case 0x7B:
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r3, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;

            case 0x7C:
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r4, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;

            case 0x7D:
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r5, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;

            case 0x7E:
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r6, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;

            case 0x7F:
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r7, #$").append(filler0).append(Integer.toHexString(inst.ops[1]));
                break;
                
            case 0x83:
                inst.instDesc.append("a, @a+pc");
                break;
                
            case 0x84:
            case 0xA4:
                inst.instDesc.append("ab");
                break;
                
            case 0x85:
                inst.instDesc.append(daRegs[inst.ops[2]]).append(", ").append(daRegs[inst.ops[1]]);
                break;

            case 0x86:
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", @r0");
                break;

            case 0x87:
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", @r1");
                break;
                
            case 0x88:
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", r0");
                break;

            case 0x89:
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", r1");
                break;

            case 0x8A:
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", r2");
                break;

            case 0x8B:
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", r3");
                break;

            case 0x8C:
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", r4");
                break;

            case 0x8D:
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", r5");
                break;

            case 0x8E:
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", r6");
                break;

            case 0x8F:
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", r7");
                break;
                
            case 0x90:
                address = inst.ops[1] * 0x100 + inst.ops[2];
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("dptr, #$").append(filler2).append(filler1).append(
                        filler0).append(Integer.toHexString(address));
                break;
                
            case 0x92:
                inst.instDesc.append(bits[inst.ops[1]]).append(", c");
                break;

            case 0x93:
                inst.instDesc.append("a, @a+dptr");
                break;
                
            case 0xA0:
            case 0xB0:
                inst.instDesc.append("c, /").append(bits[inst.ops[1]]);
                break;
                
            case 0xA3:
                inst.instDesc.append("dptr");
                break;
          
            case 0xA6:
                inst.instDesc.append("@r0, ").append(daRegs[inst.ops[1]]);
                break;
                
            case 0xA7:
                inst.instDesc.append("@r1, ").append(daRegs[inst.ops[1]]);
                break;

            case 0xA8:
                inst.instDesc.append("r0, ").append(daRegs[inst.ops[1]]);
                break;

            case 0xA9:
                inst.instDesc.append("r1, ").append(daRegs[inst.ops[1]]);
                break;

            case 0xAA:
                inst.instDesc.append("r2, ").append(daRegs[inst.ops[1]]);
                break;

            case 0xAB:
                inst.instDesc.append("r3, ").append(daRegs[inst.ops[1]]);
                break;

            case 0xAC:
                inst.instDesc.append("r4, ").append(daRegs[inst.ops[1]]);
                break;

            case 0xAD:
                inst.instDesc.append("r5, ").append(daRegs[inst.ops[1]]);
                break;

            case 0xAE:
                inst.instDesc.append("r6, ").append(daRegs[inst.ops[1]]);
                break;

            case 0xAF:
                inst.instDesc.append("r7, ").append(daRegs[inst.ops[1]]);
                break;
                
            case 0xB2:
            case 0xC2:
            case 0xD2:
                inst.instDesc.append(bits[inst.ops[1]]);
                break;

            case 0xB3:
            case 0xC3:
            case 0xD3:
                inst.instDesc.append("c");
                break;
                
            case 0xB4:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("a, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;
                
            case 0xB5:
                address = CalcBranchAddr(inst);
                inst.instDesc.append("a, ").append(daRegs[inst.ops[1]]).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;
                
            case 0xB6:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("@r0, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xB7:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("@r1, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;
                
            case 0xB8:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r0, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xB9:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r1, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xBA:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r2, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xBB:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r3, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xBC:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r4, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xBD:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r5, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xBE:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r6, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xBF:
                address = CalcBranchAddr(inst);
                filler0 = inst.ops[1] < 0x10   ? "0" : "";
                inst.instDesc.append("r7, #$").append(filler0).append(Integer.toHexString(
                        inst.ops[1])).append(", $");
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(filler2).append(filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xD5:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append(daRegs[inst.ops[1]]).append(", $").append(filler2).append(
                        filler1).append(filler0).append(Integer.toHexString(address));
                break;
                
            case 0xD8:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("r0, $").append(filler2).append(
                        filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xD9:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("r1, $").append(filler2).append(
                        filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xDA:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("r2, $").append(filler2).append(
                        filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xDB:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("r3, $").append(filler2).append(
                        filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xDC:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("r4, $").append(filler2).append(
                        filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xDD:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("r5, $").append(filler2).append(
                        filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xDE:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("r6, $").append(filler2).append(
                        filler1).append(filler0).append(Integer.toHexString(address));
                break;

            case 0xDF:
                address = CalcBranchAddr(inst);
                filler0 = address < 0x10   ? "0" : "";
                filler1 = address < 0x100  ? "0" : "";
                filler2 = address < 0x1000 ? "0" : "";
                inst.instDesc.append("r7, $").append(filler2).append(
                        filler1).append(filler0).append(Integer.toHexString(address));
                break;
                
            case 0xE0:
                inst.instDesc.append("a, @dptr");
                break;

            case 0xF0:
                inst.instDesc.append("@dptr, a");
                break;

            case 0xF2:
            case 0xF6:
                inst.instDesc.append("@r0, a");
                break;

            case 0xF3:
            case 0xF7:
                inst.instDesc.append("@r1, a");
                break;

            case 0xF8:
                inst.instDesc.append("r0, a");
                break;

            case 0xF9:
                inst.instDesc.append("r1, a");
                break;

            case 0xFA:
                inst.instDesc.append("r2, a");
                break;

            case 0xFB:
                inst.instDesc.append("r3, a");
                break;

            case 0xFC:
                inst.instDesc.append("r4, a");
                break;

            case 0xFD:
                inst.instDesc.append("r5, a");
                break;

            case 0xFE:
                inst.instDesc.append("r6, a");
                break;

            case 0xFF:
                inst.instDesc.append("r7, a");
                break;
                
            default: //Nothing to do here.
                break;
        }
    }

    private int CalcCallAddr(Instruction inst) {
        if(inst.ops[0] == 0x12 || inst.ops[0] == 0x02) {
            int address = inst.ops[1] * 0x100; //Get upper address byte.
            address += inst.ops[2];            //Add lower address byte.
            return address;
        }
        else {
            int block = inst.address + 2;     //Get address of next instruction.
            block &= 0xF800;                  //Keep only upper 5 bits.

            int offset = inst.ops[0] & 0xE0;  //Keep upper three bits of opcode
            offset <<= 3;                     //Move bits left by three.
            offset |= inst.ops[1];            //Concat. second byte of instruction.
            return block | offset;            //Concat. block and offset.
        }
    }

    private int CalcBranchAddr(Instruction inst) {
        int offset;
        int nextInst;

        //Three byte instructions.
        if(inst.ops[0] == 0x10 || inst.ops[0] == 0x20 || inst.ops[0] == 0x30 || inst.ops[0] == 0xB4 ||
           inst.ops[0] == 0xB5 || inst.ops[0] == 0xB6 || inst.ops[0] == 0xB7 || inst.ops[0] == 0xB8 ||
           inst.ops[0] == 0xB9 || inst.ops[0] == 0xBA || inst.ops[0] == 0xBB || inst.ops[0] == 0xBC ||
           inst.ops[0] == 0xBD || inst.ops[0] == 0xBE || inst.ops[0] == 0xBF || inst.ops[0] == 0xD5) {
            offset = inst.ops[2];
            nextInst = 3;
        }
        //Two byte instructions.
        else {
            offset = inst.ops[1];
            nextInst = 2;
        }

        if((offset & 0x80) != 0) {            
            offset ^= 0x7F;       //one's compliments.
            offset &= 0x7F;       //Keep lower 7 bits.
            offset++;             //Two's compliment.
            offset -= 2 * offset; //Make negative.
        }
        
        return inst.address + nextInst + offset;
    }

    private int CalcJumpAddr(Instruction inst) {
        if(inst.ops[0] == 0x80) //Signed jump.
            return CalcBranchAddr(inst);
        else //All other jumps except indirect jump.
            return CalcCallAddr(inst);
    }

    private void GetOperands() {
        int codeIndex;
        int addressIndex;
        for(int i = 0; i < codeBlocks.size(); i++) { //Check all blocks.
            codeIndex = 0;
            addressIndex = codeBlocks.elementAt(i).origin;

            while(codeIndex < codeBlocks.elementAt(i).length) {                
                //Add new Instruction class to code block.
                codeBlocks.elementAt(i).instruction.addElement(new Instruction());

                if(numOperands[codeBlocks.elementAt(i).codeBlock.elementAt(codeIndex)] == 1) {
                    //Set number of operands to 1.
                    codeBlocks.elementAt(i).instruction.lastElement().numOps = 1;
                    //Add first operand to ops array.
                    codeBlocks.elementAt(i).instruction.lastElement().ops[0] = codeBlocks.elementAt(i).codeBlock.elementAt(codeIndex);
                    //Move codeIndex forward by 1.
                    codeIndex++;
                }
                else if(numOperands[codeBlocks.elementAt(i).codeBlock.elementAt(codeIndex)] == 2) {
                    //Set number of operands to 2.
                    codeBlocks.elementAt(i).instruction.lastElement().numOps = 2;
                    //Add first operand to ops array.
                    codeBlocks.elementAt(i).instruction.lastElement().ops[0] = codeBlocks.elementAt(i).codeBlock.elementAt(codeIndex);
                    //Check if ok to add second operand.
                    if (codeIndex + 1 < codeBlocks.elementAt(i).length) //Add operand.
                        codeBlocks.elementAt(i).instruction.lastElement().ops[1] = codeBlocks.elementAt(i).codeBlock.elementAt(codeIndex + 1);
                    else //Add -1.
                        codeBlocks.elementAt(i).instruction.lastElement().ops[1] = -1;
                    //Move codeIndex forward by 2.
                    codeIndex += 2;
                }                
                else {
                    //Set number of operands to 3.
                    codeBlocks.elementAt(i).instruction.lastElement().numOps = 3;
                    //Add first operand to ops array.
                    codeBlocks.elementAt(i).instruction.lastElement().ops[0] = codeBlocks.elementAt(i).codeBlock.elementAt(codeIndex);
                    //Check if ok to add second operand.
                    if (codeIndex + 1 < codeBlocks.elementAt(i).length) //Add operand.
                        codeBlocks.elementAt(i).instruction.lastElement().ops[1] = codeBlocks.elementAt(i).codeBlock.elementAt(codeIndex + 1);
                    else //Add -1.
                        codeBlocks.elementAt(i).instruction.lastElement().ops[1] = -1;
                    //Check if ok to add third operand.
                    if (codeIndex + 2 < codeBlocks.elementAt(i).length) //Add operand.
                        codeBlocks.elementAt(i).instruction.lastElement().ops[2] = codeBlocks.elementAt(i).codeBlock.elementAt(codeIndex + 2);
                    else //Add -1.
                        codeBlocks.elementAt(i).instruction.lastElement().ops[2] = -1;
                    //Move codeIndex forward by 3.
                    codeIndex += 3;
                }
                //Set address for this instruction.
                codeBlocks.elementAt(i).instruction.lastElement().address = addressIndex;
                //Update addressIndex.
                addressIndex += codeBlocks.elementAt(i).instruction.lastElement().numOps;
            }
        }
    }

    private void CombineCodeBlocks() {
        //No need to combine blocks if only one block exists.
        if(codeBlocks.size() < 2) return;

        for(int i = 0; i < codeBlocks.size() - 1; i++) {
            if((codeBlocks.elementAt(i).length + codeBlocks.elementAt(i).origin) == codeBlocks.elementAt(i + 1).origin) {
                //Combine sequential code blocks.
                for(int j = 0; j < codeBlocks.elementAt(i + 1).length; j++)
                    codeBlocks.elementAt(i).codeBlock.addElement(codeBlocks.elementAt(i + 1).codeBlock.elementAt(j));
                //Modify code block length.
                codeBlocks.elementAt(i).length += codeBlocks.elementAt(i + 1).length;
                //Remove the other code block.
                codeBlocks.remove(i + 1);
            }
        }        
    }

    private void GetCodeBlocks() {
        int codeIndex = 8;

        //Must have at least 1 code block.
        if(endSeqIndex <= 8) {
            System.err.println("No code blocks found");
            System.exit(1);
        }

        do {
            codeBlocks.addElement(new CodeBlock());
            codeBlocks.lastElement().lengthHi = inputBytes.elementAt(codeIndex++);
            codeBlocks.lastElement().lengthLo = inputBytes.elementAt(codeIndex++);
            codeBlocks.lastElement().originHi = inputBytes.elementAt(codeIndex++);
            codeBlocks.lastElement().originLo = inputBytes.elementAt(codeIndex++);

            codeBlocks.lastElement().length = codeBlocks.lastElement().lengthHi * 0x0100 +
                                              codeBlocks.lastElement().lengthLo;

            codeBlocks.lastElement().origin = codeBlocks.lastElement().originHi * 0x0100 +
                                              codeBlocks.lastElement().originLo;

            if(codeBlocks.lastElement().origin > 0x3FFF) {
                System.err.println("Code block out of addressable range");
                System.exit(1);
            }
            if(codeBlocks.lastElement().length > 0x03FF) {
                System.err.println("Code block too large");
                System.exit(1);
            }
            for(int currentIndex = 0; currentIndex < codeBlocks.lastElement().length; currentIndex++)
                codeBlocks.lastElement().codeBlock.addElement(inputBytes.elementAt(codeIndex++));
        } while(codeIndex < endSeqIndex);        
    }

    private void FindEndSequence() {
        boolean endSeqFound = false;
        endSeqIndex = inputBytes.size() - 5;

        while(!endSeqFound && endSeqIndex > 0) {
            endSeqIndex--;
            if(inputBytes.elementAt(endSeqIndex) == 0x80 &&
               inputBytes.elementAt(endSeqIndex + 1) == 0x01 &&
               inputBytes.elementAt(endSeqIndex + 2) == 0xE6 &&
               inputBytes.elementAt(endSeqIndex + 3) == 0x00 &&
               inputBytes.elementAt(endSeqIndex + 4) == 0x00)
                endSeqFound = true;
        }
        if(!endSeqFound) {
            System.err.println("End sequence not found");
            System.exit(1);
        }
    }

    private void WriteDefines() {
        String filler0;
        try {
            write.writeBytes("\n;---------------------------------------Defines---------------------------------------\n");
            write.writeBytes(";Special function registers and direct addressing registers.  Registers starting with\n");
            write.writeBytes(";a double underscore (__) are undefined and should not be used.\n\n");
            for(int i = 0; i < daRegs.length; i++) {
                filler0 = i < 0x10 ? "0" : "";
                write.writeBytes(".equ " + daRegs[i] + ", $" + filler0 + Integer.toHexString(i) + "\n");
            }
            write.writeBytes("\n;Bit addressable defines.\n\n");
            for(int i = 0; i < bits.length; i++) {
               filler0 = i < 0x10 ? "0" : "";
                write.writeBytes(".equ " + bits[i] + ", $" + filler0 + Integer.toHexString(i) + "\n");
            }
            write.writeBytes("\n;------------------------------------Start of Code------------------------------------\n");
        }
        catch (IOException ioException) {
            System.err.println("Error writing to file: " + ioException);
            System.exit(1);
        }
    }

    private void FillHeader() {
        String fillerHi;
        String fillerLo;
        String epromByte = ".ebyte\t$" + Integer.toHexString(inputBytes.elementAt(0)) + "\n";
        fillerHi = (inputBytes.elementAt(2) < 0x10) ? "0" : "";
        fillerLo = (inputBytes.elementAt(1) < 0x10) ? "0" : "";
        String vid = ".vid\t$" + fillerHi + Integer.toHexString(inputBytes.elementAt(2)) +
                     fillerLo + Integer.toHexString(inputBytes.elementAt(1)) + "\n";
        fillerHi = (inputBytes.elementAt(4) < 0x10) ? "0" : "";
        fillerLo = (inputBytes.elementAt(3) < 0x10) ? "0" : "";
        String pid = ".pid\t$" + fillerHi + Integer.toHexString(inputBytes.elementAt(4)) +
                     fillerLo + Integer.toHexString(inputBytes.elementAt(3)) + "\n";
        fillerHi = (inputBytes.elementAt(6) < 0x10) ? "0" : "";
        fillerLo = (inputBytes.elementAt(5) < 0x10) ? "0" : "";
        String did = ".did\t$" + fillerHi + Integer.toHexString(inputBytes.elementAt(6)) +
                     fillerLo + Integer.toHexString(inputBytes.elementAt(5)) + "\n";
        fillerLo = (inputBytes.elementAt(7) < 0x10) ? "0" : "";
        String configByte = ".cbyte\t$" + fillerLo + Integer.toHexString(inputBytes.elementAt(7)) + "\n";
        try {
            write.setLength(0);             //Erase any previous contents.
            write.writeBytes(epromByte);    //Write EPROM byte.
            write.writeBytes(vid);          //Write Vendor ID word.
            write.writeBytes(pid);          //Write Product ID word.
            write.writeBytes(did);          //Write Device ID word.
            write.writeBytes(configByte);   //Write I2C config byte.
        }
        catch (IOException ioException) {
            System.err.println("Error writing to file: " + ioException);
            System.exit(1);
        }
    }

    private void FillInputBytes() {
        errorLine = 1;
        int number = 0;
        for(int i = 0; i < inputLines.size(); i++) {
            String s = inputLines.elementAt(i);
            String[] tokenizedString = s.split(", ");

            //Check for errors in data.
            for(int j = 0; j < tokenizedString.length; j++) {
                if(tokenizedString[j].length() != 2) {
                    System.err.println("Invalid data at line " + errorLine + "..." + tokenizedString[j] + "...");
                    System.exit(1);
                }
                try {
                    //Attempt to parse.
                    number = Integer.parseInt(tokenizedString[j], 16);
                }
                catch (Exception exception){
                    System.err.println("Invalid data at line " + errorLine);
                    System.exit(1);
                }
                //Add byte to inputBytes vector.
                inputBytes.add(number);
            }            
            errorLine++;
        }
        //EPROM byte must be either 0xC0 or 0xC2.
        if(!(inputBytes.elementAt(0) == 0xC2  || inputBytes.elementAt(0) == 0xC0)) {
            System.err.println("Invalid EPROM byte");
            System.exit(1);
        }
        //Must have at least 8 bytes of data in vector.
        if(inputBytes.size() < 8) {
            System.err.println("Invalid EPROM dump");
            System.exit(1);
        }
    }

    private void FillInputLines() {
        try {
            //Fill inputLines vector.
            while (read.getFilePointer() != read.length())
                inputLines.add(read.readLine());
        }
        catch (IOException ioException) {
            System.err.println("Error reading file: " + ioException);
            System.exit(1);
        }
    }

    private void OpenFiles(String[] args) {
        //Check for proper number of arguments.
        if (args.length < 1 || args.length > 2) {
            System.out.println("\nThe proper usage of the disassembler is as follows:\n" +
                    "\njava -jar Disassembler_8051.jar [input.file] {[output.file]}\n\n" +
                    "A minimum of one argument is required which is the input ascii\n" +
                    "binary file.  The output file name is optional.  If an output file\n" +
                    "name is not specified, the input file name with a .asm extension\n" +
                    "is created.");
            System.exit(1);
        }

        try {
            //Open input ascii binary file to read from.
            read = new RandomAccessFile(args[0], "r");
        }
        catch (IOException ioException) {
            System.err.println("Error opening file: " + ioException);
            System.exit(1);
        }

        try {
            //Open output .asm file to write to.
            if(args.length == 2)
                write = new RandomAccessFile(args[1], "rw");
            else
                write = new RandomAccessFile(args[0].substring(0, args[0].indexOf('.')) + ".asm", "rw");
        }
        catch (IOException ioException) {
            System.err.println("Error opening file: " + ioException);
            System.exit(1);
        }
    }

    /* To be completed at another time...perhaps.
    private void InitJumpQueue() {
        for(int i = 0; i < codeBlocks.size(); i++) { //Check all blocks.
            for(int j = 0; j < codeBlocks.elementAt(i).instruction.size(); j++) { //Check all instructions.
                if(IsCall(codeBlocks.elementAt(i).instruction.elementAt(j)) &&
                   codeBlocks.elementAt(i).instruction.elementAt(j).address <= 0x0063) {
                    jumpQueue.addElement(CalcCallAddr(codeBlocks.elementAt(i).instruction.elementAt(j)));
                }
                else if(IsBranch(codeBlocks.elementAt(i).instruction.elementAt(j)) &&
                   codeBlocks.elementAt(i).instruction.elementAt(j).address <= 0x0063) {
                    jumpQueue.addElement(CalcBranchAddr(codeBlocks.elementAt(i).instruction.elementAt(j)));
                }
                else if(IsJump(codeBlocks.elementAt(i).instruction.elementAt(j)) &&
                   codeBlocks.elementAt(i).instruction.elementAt(j).address <= 0x0063) {
                    jumpQueue.addElement(CalcJumpAddr(codeBlocks.elementAt(i).instruction.elementAt(j)));
                }

                //Mark all code in vector addresses as instructions.
                if(codeBlocks.elementAt(i).instruction.elementAt(j).address <= 0x0063)
                    codeBlocks.elementAt(i).instruction.elementAt(j).isInst = true;
            }
        }
    } */

    /*
    private boolean IsCall(Instruction inst) {
        if(inst.ops[0] == 0x11 || inst.ops[0] == 0x12 || inst.ops[0] == 0x31 || inst.ops[0] == 0x51 ||
           inst.ops[0] == 0x71 || inst.ops[0] == 0x91 || inst.ops[0] == 0xB1 || inst.ops[0] == 0xD1 ||
           inst.ops[0] == 0xF1)
            return true;
        else
            return false;
    }

    private boolean IsBranch(Instruction inst) {
        if(inst.ops[0] == 0x10 || inst.ops[0] == 0x20 || inst.ops[0] == 0x30 || inst.ops[0] == 0x40 ||
           inst.ops[0] == 0x50 || inst.ops[0] == 0x60 || inst.ops[0] == 0x70 || inst.ops[0] == 0xB4 ||
           inst.ops[0] == 0xB5 || inst.ops[0] == 0xB6 || inst.ops[0] == 0xB7 || inst.ops[0] == 0xB8 ||
           inst.ops[0] == 0xB9 || inst.ops[0] == 0xBA || inst.ops[0] == 0xBB || inst.ops[0] == 0xBC ||
           inst.ops[0] == 0xBD || inst.ops[0] == 0xBE || inst.ops[0] == 0xBF || inst.ops[0] == 0xD5 ||
           inst.ops[0] == 0xD8 || inst.ops[0] == 0xD9 || inst.ops[0] == 0xDA || inst.ops[0] == 0xDB ||
           inst.ops[0] == 0xDC || inst.ops[0] == 0xDD || inst.ops[0] == 0xDE || inst.ops[0] == 0xDF)
            return true;
        else
            return false;
    }

    private boolean IsJump(Instruction inst) {
        if(inst.ops[0] == 0x01 || inst.ops[0] == 0x02 || inst.ops[0] == 0x21 || inst.ops[0] == 0x41 ||
           inst.ops[0] == 0x61 || inst.ops[0] == 0x73 || inst.ops[0] == 0x80 || inst.ops[0] == 0x81 ||
           inst.ops[0] == 0xA1 || inst.ops[0] == 0xC1 || inst.ops[0] == 0xE1)
            return true;
        else
            return false;
    }
    */

    public static void main(String[] args) {
        Disassembler_8051 dis_8051 = new Disassembler_8051(args);
    }
}
