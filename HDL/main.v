`timescale 1ns / 1ps

/********************************** I/O *********************************/

module main(   
    //Main clock.
    input clk,

	 //LEDs.
	 output [7:0]Led,	 
	 
	 //Input buttons.
	 input  [3:0]btn,
	 
	 //I2C controller.
	 inout SCL,             //Clock line.
	 inout SDA,	            //Data line.
	 
	 //UART controller.
	 input  rx,
	 output tx
    );

/**************************** Wires and Regs ****************************/
	 PULLUP PULLUP_SCL (.O(SCL));
	 PULLUP PULLUP_SDA (.O(SDA));

    //NMPSM3.
	 wire ack0;             //IRQ ack set to interrupt flip flops.
	 wire ack1;             //Not used in this design.
	 wire ack2;             //Not used in this design.
	 wire ack3;             //Not used in this design.
	 wire sigout0;          //Interrupt 0 input from ff0.
	 wire sigout1;          //Not used in this design.
	 wire sigout2;          //Not used in this design.
	 wire sigout3;          //Not used in this design.
	 wire reset;            //Global reset.
	 wire read;             //Read strobe.
	 wire write;            //Write strobe.
	 wire [15:0]id;         //ID for peripheral devices.
	 wire [15:0]outdata;    //Data for peripheral devices.
	 wire [35:0]inst;       //Instruction from program ROM.
	 wire [15:0]in_port;    //Input from data MUX.
	 wire [15:0]address;    //Address truncated by bus converter.
	 
	 //I2C controller.
	 wire [15:0]i2cstatus;
	 wire [7:0]i2cdata;
	 
	 //Button debouncer.
	 wire [3:0]out_button;
	 
	 //Timer0
	 wire timer0out;
	 
	 //UART
	 wire [7:0]uartdata;
	 wire [11:0]txcount;
	 wire [11:0]rxcount;
	 
/*************************** Hardware Modules ***************************/
	 
	 //If button 3 pressed, reset processor.
	 assign reset = out_button[3];
	 
	 //timer.
	 timer timer0(.clk(clk), .write(write), .id(id), .din(outdata), .reset(reset), .dout(timer0out));
	 
	 //Flip-flop for interrupt 0.
	 FF ff0(.set(timer0out), .reset(ack0), .sigout(sigout0));
	 
	 //Flip-flop for interrupt 1(not used).
	 FF ff1(.set(1'b0), .reset(ack1), .sigout(sigout1));
	 
	 //Flip-flop for interrupt 2(not used).
	 FF ff2(.set(1'b0), .reset(ack2), .sigout(sigout2));
	 
	 //Flip-flop for interrupt 3(not used).
	 FF ff3(.set(1'b0), .reset(ack3), .sigout(sigout3));
	 
	 //Processor input data MUX.
	 dataMUX datamux(.read(read), .id(id), .i2cdata({8'h00, i2cdata}), .i2cstatus(i2cstatus), .dout(in_port), 
	                 .uartdata({8'h00, uartdata}), .txcount({4'h0, txcount}), .rxcount({4'h0, rxcount}),
						  .buttons({12'h000, out_button}));	 
	 
	 //Program ROM for NMPSM3.
	 prgROM ROM(.clka(clk), .addra(address[8:0]), .douta(inst));	  
	 
	 //NMPSM3 soft processor.	 
	 NMPSM3 nmpsm3(.clk(clk), .reset(reset), .IRQ0(sigout0), .IRQ1(sigout1), .IRQ2(sigout2), .IRQ3(sigout3),
	               .INSTRUCTION(inst), .IN_PORT(in_port), .READ_STROBE(read), .WRITE_STROBE(write), .IRQ_ACK0(ack0),
						.IRQ_ACK1(ack1), .IRQ_ACK2(ack2), .IRQ_ACK3(ack3), .ADDRESS(address), .OUT_PORT(outdata), 
						.PORT_ID(id));
	
	 //LEDS
	 ledio leds(.clk(clk), .reset(reset), .write(write), .id(id), .din(outdata), .ledsout(Led));
	 
	 //I2C controller.
	 I2CTest1 i2c(.SCL(SCL), .SDA(SDA), .reset(reset), .write(write), .clk(clk), .id(id), .din(outdata),
	 .i2cdata(i2cdata), .i2cstatus(i2cstatus));
	 
	 //UART
	 uart u(.clk(clk), .reset(reset), .id(id), .din(outdata), .write(write), .rx(rx),
           .tx(tx), .dout(uartdata), .txcount(txcount), .rxcount(rxcount));
	 
	 //Button debounce module.
	 debounce dbc(.clk(clk), .in_button(btn), .out_button(out_button));

endmodule
