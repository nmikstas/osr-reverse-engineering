.size 512

;----------------------------------------------------------------------------------------;
;----------------------------------------Defines-----------------------------------------;
;----------------------------------------------------------------------------------------;

;**************************************Output Ports**************************************;
.alias SET_BAUD				#$0200				;
.alias TX_STORE_BYTE		#$0201				;
.alias TX_FLUSH				#$0202				;UART control.
.alias TX_PURGE				#$0203				;
.alias RX_NEXT_BYTE			#$0204				;
.alias RX_PURGE				#$0205				;

.alias LOAD_CLOCK_RATE		#$8000				;
.alias LOAD_MASTER_TX_DATA	#$8001				;
.alias LOAD_MASTER_TYPE		#$8002				;I2C control.
.alias MASTER_ENABLED		#$8003				;
.alias MASTER_TX_VALID		#$8004				;
.alias RX_VALID				#$8005				;

.alias TIMER0_LOWER_WORD	#$0010				;Timer control.
.alias TIMER0_UPPER_WORD	#$0011				;

.alias LEDIO				#$002A				;LED control.

;**************************************Input Ports***************************************;
.alias I2C_DATA				#$0001
.alias I2C_STATUS			#$0002
.alias UART_DATA			#$0003
.alias TX_COUNT				#$0004
.alias RX_COUNT				#$0005
.alias BUTTONS				#$0006

;***********************************Processor Registers**********************************;
.alias temp_reg0			$0000				;Used for misc. processing.

.alias i2c_tx_data			$0001
.alias i2c_type				$0002
.alias i2c_status_in		$0003
.alias i2c_read_byte		$0004

.alias uart_tx_byte			$0005
.alias uart_tx_count		$0006
.alias uart_rx_count		$0007

.alias i2c_adr_lo			$0008
.alias i2c_adr_hi			$0009

.alias byte_count			$000A
.alias burn_byte_count		$000B

.alias i2c_tx_pointer		$000F

.alias burn_adr_lo			$0020				;Destination address for write.
.alias burn_adr_hi			$0021				;

.alias wait_reg_lo			$0022
.alias wait_reg_hi			$0023

.alias idle_reg_lo			$0024
.alias idle_reg_hi			$0025

.alias rdy_pat_reg			$0026

.alias button_status		$0027				;Input button statuses.

.alias progress_reg			$0028				;Task status.

.alias uart_rx_state		$0029				;Upload state.
.alias uart_rx_byte			$002A				;Received uart byte.
.alias uart_work_reg		$002B				;Reg used for converting ascii to bytes.

.alias i2c_tx_buffer		$0040				;Through $007F

;****************************************Constants***************************************;
;I2C types.
.alias MASTER_P				#$0000
.alias MASTER_S				#$0001
.alias MASTER_TX_C			#$0002
.alias MASTER_RX_C			#$0003

;I2C Addresses.
;.alias SOURCE_WRITE		#$A0
;.alias SOURCE_READ			#$A1
.alias SOURCE_WRITE			#$A2
.alias SOURCE_READ			#$A3
.alias DEST_WRITE			#$A2
.alias DEST_READ			#$A3

;Buffer markers.
.alias BUFFER_START			#$40
.alias BUFFER_NEXT_TO_END	#$7F
.alias BUFFER_END			#$80

;States
.alias GET_UPPER_NIBBLE		#0
.alias GET_LOWER_NIBBLE		#1

;ASCII numbers.
.alias ZERO					#$30
.alias ONE					#$31
.alias TWO					#$32
.alias THREE				#$33
.alias FOUR					#$34
.alias FIVE					#$35
.alias SIX					#$36
.alias SEVEN				#$37
.alias EIGHT				#$38
.alias NINE					#$39

;ASCII letters.
.alias A					#$41
.alias B					#$42
.alias C					#$43
.alias D					#$44
.alias E					#$45
.alias F					#$46
.alias G					#$47
.alias H					#$48
.alias I					#$49
.alias J					#$4A
.alias K					#$4B
.alias L					#$4C
.alias M					#$4D
.alias N					#$4E
.alias O					#$4F
.alias P					#$50
.alias Q					#$51
.alias R					#$52
.alias S					#$53
.alias T					#$54
.alias U					#$55
.alias V					#$56
.alias W					#$57
.alias X					#$58
.alias Y					#$59
.alias Z					#$5A

;ASCII symbols.
.alias SPACE				#$20
.alias COLON				#$3A
.alias COMMA				#$2C
.alias PERIOD				#$2E
.alias CR					#$0D				;Carriage return.
.alias FSLASH				#$2F				;Forward slash.
.alias LBAR					#$5F				;Underscore.
.alias MBAR					#$2D				;Minus sign.
.alias UBAR					#$FF				;Upper bar.
.alias STAR					#$2A				;Multiply sign.

;****************************************Bitmasks****************************************;
.alias I2C_MASTER_TX_VALID	#%0000000000000001	;I2C controller
.alias I2C_MASTER_INACTIVE	#%0000000000000010	;status bits.
.alias I2C_MASTER_RX_VALID	#%0000000000000100	;

;----------------------------------------------------------------------------------------;
;-------------------------------------Start Of Code--------------------------------------;
;----------------------------------------------------------------------------------------;

jump Reset										;
jump Interrupt0									;
jump Interrupt1									;Reset and interrupt vectors.
jump Interrupt2									;
jump Interrupt3									;

Interrupt0:										;
Interrupt1:										;
Interrupt2:										;Unused interrupts.
Interrupt3:										;
    rtie										;

Reset: 
	call ClearRegs								;Zero all hardware regs.

	load i2c_tx_data #$3F						;Set i2c clock to 400KHz.
	out  i2c_tx_data LOAD_CLOCK_RATE			;

	load uart_tx_byte #$D9						;Set UART baud rate to 115200.
	out  uart_tx_byte SET_BAUD					;

	call LCD_Init								;Initialize the LCD.

MainLoop:
    call Idle									;Update display while idle.
	call CheckButtons							;Check for any button presses.
	jump MainLoop								;Loop forever.

ClearRegs:										;
    load 0 #$3FF								;
    load 1 #0									;
    ClearRegsLoop:								;Clear all internal
    stor 1 (0)									;processor registers.
    sub  0 #1									;
    jpnz ClearRegsLoop							;
    ret											;

BurnEEPROM:
	call BurnMessage							;Load initial LCD message.

	load byte_count #0							;Zero out count and progress registers.
	load progress_reg #0						;
	load uart_rx_state #0						;Reset state.
	out  uart_tx_byte RX_PURGE					;Zero out UART buffer.

	DoBurn:
	call UpdateProgress							;Update progress bar on LCD.
	call GetUARTBytes							;Get 16 bytes from UART.
	load burn_byte_count byte_count				;Burn_byte_count should start out 1
	sub  burn_byte_count #$40					;block behind byte count.
	call I2CWrite16								;Write 16 bytes to EPROM.

	comp byte_count #$4000						;Has 16k bytes been received?
	jpnz DoBurn									;If not, loop to receive more.

	call ClearLCD								;Clear the LCD display.
	ret											;

CopyEEPROM:
	call CopyMessage							;Load initial LCD message.

	load byte_count #0							;Zero out count and progress registers.
	load progress_reg #0						;

	DoCopy:

	call UpdateProgress							;Update progress bar on LCD.
	call I2CRead16								;Get 16 bytes of data from the EPROM.
	load burn_byte_count byte_count				;Burn_byte_count should start out 1
	sub  burn_byte_count #$40					;block behind byte count.
	call I2CWrite16								;Write 16 bytes to EPROM.

	comp byte_count #$4000						;Has 16k bytes been transmitted?
	jpnz DoCopy									;If not, loop to transmit more.

	call ClearLCD								;Clear the LCD display.
	ret											;

DumpEEPROM:
	call DumpMessage							;Load initial LCD message.

	load byte_count #0							;Zero out count and progress registers.
	load progress_reg #0						;

	DoDump:
	call UpdateProgress							;Update progress bar on LCD.
	call I2CRead16								;Get 16 bytes of data from the EPROM.
	call LoadUARTBufferAndFlush					;Send bytes out the UART.

	comp byte_count #$4000						;Has 16k bytes been transmitted?
	jpnz DoDump									;If not, loop to transmit more.

	call ClearLCD								;Clear the LCD display.
	ret											;

GetUARTBytes:
	load i2c_tx_pointer BUFFER_START			;Point to beginning of buffer.

	WaitForBytes:
	in   uart_rx_count RX_COUNT					;Are bytes waiting in rx buffer?
	comp uart_rx_count #0						;
	jpz  WaitForBytes							;If not, loop to wait for some.

	in   uart_rx_byte UART_DATA					;Get UART byte.
	out  uart_rx_byte RX_NEXT_BYTE				;Move to next position.

	comp uart_rx_byte #$30						;Is byte less that ascii 0?
	jpc  WaitForBytes							;If so, not a valid data byte.

	comp uart_rx_byte #$3A						;Is byte between ascii 0 and 9?
	jpc  IsValidNibble							;If so, it is a valid data bye.

	comp uart_rx_byte #$41						;Is byte less than ascii A?
	jpc  WaitForBytes							;If so, not a valid data byte.

	comp uart_rx_byte #$47						;Is byte between ascii A and F?
	jpnc WaitForBytes 							;If not, it is not a valid data byte.

	IsValidNibble:
	sub  uart_rx_byte #$30						;Convert ascii to byte.
	comp uart_rx_byte #$0A						;Is value betweeon 0 and 9?
	jpc  FindState								;If so, done here.
	sub  uart_rx_byte #$7						;Convert letter to binary.

	FindState:
	comp uart_rx_state #0						;Waiting for first byte?
	jpz  SetUpperNibble							;If so, set the upper nibble.
	jump SetLowerNibble							;Else set the lower nibble.

	SetUpperNibble:
	load  uart_rx_state #1						;Get ready for next state.
	asl  uart_rx_byte							;
	asl  uart_rx_byte							;Shift value to upper nibble.
	asl  uart_rx_byte							;
	asl  uart_rx_byte							;
	stor uart_rx_byte uart_work_reg				;Save upper nibble.	
	jump WaitForBytes							;Wait for lower nibble.

	SetLowerNibble:
	load  uart_rx_state #0						;Get ready for next state.
	add   byte_count #1							;Completed 1 byte.

	add  uart_work_reg uart_rx_byte				;Combine upper and lower nibbles.
	stor uart_work_reg (i2c_tx_pointer)			;Save byte.

	add  i2c_tx_pointer #1						;Increment to next buffer space.

	comp i2c_tx_pointer BUFFER_END				;Have 16 bytes been read?
	jpnz WaitForBytes							;If not, loop to get more.
	ret											;

;This function updates the progress bar on the second line of the LCD while
;transfers are in progress.  One star represents 1k od data transferred.
UpdateProgress:
	comp byte_count progress_reg				;Time to update progress?
	rtnz										;If not, exit.
	add progress_reg #$400						;Next update is after 1k of data.

	load i2c_tx_data	#$007C					;
	load i2c_type		MASTER_TX_C				;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data	#$0040					;No more control bytes before stop,
	load i2c_type		MASTER_TX_C				;
	call I2CTXByte								;

	load i2c_tx_data	STAR					;
	load i2c_type		MASTER_P				;Write '*' to display.
	call I2CTXByte								;

	call Wait_For_Stop							;Wait for I2C controller to go idle.
	ret											;

;This function takes 16 bytes of buffered data and copies them into an EPROM.
I2CWrite16:
    load i2c_tx_pointer BUFFER_START			;Point to beginning of buffer.

	load burn_adr_hi burn_byte_count			;
	lsr  burn_adr_hi							;
	lsr  burn_adr_hi							;
	lsr  burn_adr_hi							;
	lsr  burn_adr_hi							;Move upper byte to lower byte.
	lsr  burn_adr_hi							;
	lsr  burn_adr_hi							;
	lsr  burn_adr_hi							;
	lsr  burn_adr_hi							;

	load burn_adr_lo burn_byte_count			;Keep only lower byte.
	and  burn_adr_lo #$00FF						;

	load i2c_tx_data DEST_WRITE					;Address EPROM, write.
	load i2c_type MASTER_TX_C					;
	call I2CTXByte								;

	load i2c_tx_data burn_adr_hi				;Address high byte.
	load i2c_type MASTER_TX_C					;
	call I2CTXByte								;

	load i2c_tx_data burn_adr_lo				;Address low byte.
	load i2c_type MASTER_TX_C					;
	call I2CTXByte								;

	I2C_Write_Loop:
	load i2c_tx_data (i2c_tx_pointer)			;
	load i2c_type MASTER_TX_C					;Transmit byte to EPROM.
	call I2CTXByte								;

	add  i2c_tx_pointer #1						;Increment pointer.
	comp i2c_tx_pointer BUFFER_NEXT_TO_END		;15 bytes transmitted?

	jpnz I2C_Write_Loop							;Loop if more bytes to transmit.

	load i2c_tx_data (i2c_tx_pointer)			;
	load i2c_type MASTER_P						;Transmit final byte.
	call I2CTXByte								;

	call Wait_For_Stop							;Wait for I2C controller to go idle.
	call Wait_For_Delay							;Give EPROM time to write.
	ret											;

;This function takes all 16 bytes from the I2C buffer and converts them into ascii
;and transmits them out the serial port. It adds a new line after the bytes.
LoadUARTBufferAndFlush:
	load i2c_tx_pointer BUFFER_START			;Point to beginning of buffer.

	UARTFillLoop:
	load uart_tx_byte (i2c_tx_pointer)			;Get byte from buffer.
	
	load temp_reg0 uart_tx_byte					;
	lsr  temp_reg0								;
	lsr  temp_reg0								;Move upper nibble to lower nibble.
	lsr  temp_reg0								;
	lsr  temp_reg0								;
	add  temp_reg0 #$30							;Convert to ASCII.

	comp temp_reg0 #$3A							;Check if needs to be converted to
	jpc  UpperNibbleOut							;letter (A through F).

	add  temp_reg0 #$7							;Convert to letter.

	UpperNibbleOut:
	out  temp_reg0 TX_STORE_BYTE				;Transmit upper nibble.

	load temp_reg0 uart_tx_byte					;Keep only lower nibble.
	and  temp_reg0 #$F							;
	add  temp_reg0 #$30							;Convert to ASCII.

	comp temp_reg0 #$3A							;Check if needs to be converted to
	jpc  LowerNibbleOut							;letter (A through F).

	add  temp_reg0 #$7							;Convert to letter.

	LowerNibbleOut:
	out  temp_reg0 TX_STORE_BYTE				;Transmit lower nibble.

	load temp_reg0 #$2C							;Transmit a comma.
	out  temp_reg0 TX_STORE_BYTE				;

	load temp_reg0 #$20							;Transmit a space.
	out  temp_reg0 TX_STORE_BYTE				;

	out  temp_reg0 TX_FLUSH						;Flush transmit buffer.

	add  i2c_tx_pointer #1						;Move to next byte in buffer.

	load temp_reg0 i2c_tx_pointer				;
	and  temp_reg0 #$F							;Is it time for a new line?
	comp temp_reg0 #0							;If not, keep looping.
	jpnz UARTFillLoop							;

	load uart_tx_byte #$0D						;Transmit a carriage return.
	out  uart_tx_byte TX_STORE_BYTE				;
	load uart_tx_byte #$0A						;Transmit a newline.
	out  uart_tx_byte TX_STORE_BYTE				;
				
	comp i2c_tx_pointer BUFFER_END				;
	jpnz UARTFillLoop							;Loop only if more bytes in buffer.

	call Wait_For_UART							;Wait for UART to be empty.
	ret											;

;This function reads 16 bytes from the EEPROM and stores them in a buffer.
I2CRead16:
	load i2c_tx_pointer BUFFER_START			;Point to beginning of buffer.

    load i2c_adr_hi byte_count					;
	lsr  i2c_adr_hi								;
	lsr  i2c_adr_hi								;
	lsr  i2c_adr_hi								;
	lsr  i2c_adr_hi								;Move upper byte to lower byte.
	lsr  i2c_adr_hi								;
	lsr  i2c_adr_hi								;
	lsr  i2c_adr_hi								;
	lsr  i2c_adr_hi								;

	load i2c_adr_lo byte_count					;Keep only lower byte.
	and  i2c_adr_lo #$00FF						;

	load i2c_tx_data SOURCE_WRITE				;
	load i2c_type MASTER_TX_C					;Address EPROM, write.
	call I2CTXByte								;

	load i2c_tx_data i2c_adr_hi					;
	load i2c_type MASTER_TX_C					;Address high byte.
	call I2CTXByte								;

	load i2c_tx_data i2c_adr_lo					;
	load i2c_type MASTER_S						;Address low byte.
	call I2CTXByte								;

	load i2c_tx_data SOURCE_READ				;
	load i2c_type MASTER_RX_C					;Address EPROM, read.
	call I2CTXByte								;
	
	I2CReadLoop:
	load i2c_type MASTER_RX_C					;
	call I2CTXByte								;Read next byte from SDA line.
	call I2CRXByte								;

	stor i2c_read_byte (i2c_tx_pointer)			;Save byte into buffer.
	add  i2c_tx_pointer #1						;Increment buffer pointer.
	
	add  byte_count #1							;Increment byte count.
	load temp_reg0 byte_count					;
	and  temp_reg0 #$003F						;
	comp temp_reg0 #$003F						;15 bytes transmitted?

	jpnz I2CReadLoop							;Loop if more bytes to transmit.

	load i2c_type MASTER_P						;
	call I2CTXByte								;Read next byte from SDA line.
	call I2CRXByte								;

	stor i2c_read_byte (i2c_tx_pointer)			;Save final byte into buffer.
	add  byte_count #1							;

	call Wait_For_Stop							;Wait for I2C controller to go idle.
	ret											;

;The following functions write various messages to the LCD display.
CopyMessage:
	load i2c_tx_data	#$007C					;
	load i2c_type		MASTER_TX_C				;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data	#$0000				    ;
	load i2c_type		MASTER_TX_C				;No more control bytes before stop.
	call I2CTXByte								;

	load i2c_tx_data	#$0080					;
	load i2c_type		MASTER_P				;Switch to first line of display.
	call I2CTXByte								;

	call Wait_For_Stop							;Wait for I2C bus to go idle.

	load i2c_tx_data	#$007C					;
	load i2c_type		MASTER_TX_C				;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data	#$0040					;No more control bytes before stop,
	load i2c_type		MASTER_TX_C				;write message "BURNING EPROM...".
	call I2CTXByte								;

	load i2c_tx_data	C						;
	load i2c_type		MASTER_TX_C				;Write 'C' to display.
	call I2CTXByte								;

	load i2c_tx_data	O						;
	load i2c_type		MASTER_TX_C				;Write 'O' to display.
	call I2CTXByte								;

	load i2c_tx_data	P						;
	load i2c_type		MASTER_TX_C				;Write 'P' to display.
	call I2CTXByte								;

	load i2c_tx_data	Y						;
	load i2c_type		MASTER_TX_C				;Write 'Y' to display.
	call I2CTXByte								;
	
	jump FinishMessage							;Finish displaying LCD message.
	ret

DumpMessage:
	load i2c_tx_data	#$007C					;
	load i2c_type		MASTER_TX_C				;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data	#$0000					;
	load i2c_type		MASTER_TX_C				;No more control bytes before stop.
	call I2CTXByte								;

	load i2c_tx_data	#$0080					;
	load i2c_type		MASTER_P				;Switch to first line of display.
	call I2CTXByte								;

	call Wait_For_Stop							;Wait for I2C bus to go idle.

	load i2c_tx_data	#$007C					;
	load i2c_type		MASTER_TX_C				;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data	#$0040					;No more control bytes before stop,
	load i2c_type		MASTER_TX_C				;write message "BURNING EPROM...".
	call I2CTXByte								;

	load i2c_tx_data	D						;
	load i2c_type		MASTER_TX_C				;Write 'D' to display.
	call I2CTXByte								;

	load i2c_tx_data	U						;
	load i2c_type		MASTER_TX_C				;Write 'U' to display.
	call I2CTXByte								;

	load i2c_tx_data	M						;
	load i2c_type		MASTER_TX_C				;Write 'M' to display.
	call I2CTXByte								;

	load i2c_tx_data	P						;
	load i2c_type		MASTER_TX_C				;Write 'P' to display.
	call I2CTXByte								;
	
	jump FinishMessage							;Finish displaying LCD message.

BurnMessage:
	load i2c_tx_data	#$007C					;
	load i2c_type		MASTER_TX_C				;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data	#$0000					;
	load i2c_type		MASTER_TX_C				;No more control bytes before stop.
	call I2CTXByte								;

	load i2c_tx_data	#$0080					;
	load i2c_type		MASTER_P				;Switch to first line of display.
	call I2CTXByte								;

	call Wait_For_Stop							;Wait for I2C bus to go idle.

	load i2c_tx_data	#$007C					;
	load i2c_type		MASTER_TX_C				;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data	#$0040					;No more control bytes before stop,
	load i2c_type		MASTER_TX_C				;write message "BURNING EPROM...".
	call I2CTXByte								;

	load i2c_tx_data	B						;
	load i2c_type		MASTER_TX_C				;Write 'B' to display.
	call I2CTXByte								;

	load i2c_tx_data	U						;
	load i2c_type		MASTER_TX_C				;Write 'U' to display.
	call I2CTXByte								;

	load i2c_tx_data	R						;
	load i2c_type		MASTER_TX_C				;Write 'R' to display.
	call I2CTXByte								;

	load i2c_tx_data	N						;
	load i2c_type		MASTER_TX_C				;Write 'N' to display.
	call I2CTXByte								;

FinishMessage:
	load i2c_tx_data	I						;
	load i2c_type		MASTER_TX_C				;Write 'I' to display.
	call I2CTXByte								;

	load i2c_tx_data	N						;
	load i2c_type		MASTER_TX_C				;Write 'N' to display.
	call I2CTXByte								;

	load i2c_tx_data	G						;
	load i2c_type		MASTER_TX_C				;Write 'G' to display.
	call I2CTXByte								;

	load i2c_tx_data	SPACE					;
	load i2c_type		MASTER_TX_C				;Write ' ' to display.
	call I2CTXByte								;

	load i2c_tx_data	E						;
	load i2c_type		MASTER_TX_C				;Write 'E' to display.
	call I2CTXByte								;

	load i2c_tx_data	P						;
	load i2c_type		MASTER_TX_C				;Write 'P' to display.
	call I2CTXByte								;

	load i2c_tx_data	R						;
	load i2c_type		MASTER_TX_C				;Write 'R' to display.
	call I2CTXByte								;

	load i2c_tx_data	O						;
	load i2c_type		MASTER_TX_C				;Write 'O' to display.
	call I2CTXByte								;

	load i2c_tx_data	M						;
	load i2c_type		MASTER_TX_C				;Write 'M' to display.
	call I2CTXByte								;

	load i2c_tx_data	PERIOD					;
	load i2c_type		MASTER_TX_C				;Write '.' to display.
	call I2CTXByte								;

	load i2c_tx_data	PERIOD					;
	load i2c_type		MASTER_TX_C				;Write '.' to display.
	call I2CTXByte								;

	load i2c_tx_data	PERIOD					;
	load i2c_type		MASTER_P				;Write '.' to display.
	call I2CTXByte								;

	call Wait_For_Stop							;Wait for I2C bus to go idle.

	load i2c_tx_data #$007C						;
	load i2c_type MASTER_TX_C					;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data #$0000						;
	load i2c_type MASTER_TX_C					;No more control bytes before stop.
	call I2CTXByte								;

	load i2c_tx_data #$00C0						;
	load i2c_type MASTER_P						;Switch to second line of display.
	call I2CTXByte								;

	call Wait_For_Stop							;Wait for I2C bus to go idle.
	ret											;

;The following function gets the input button statuses and runs the proper 
;routine associated with that button.
CheckButtons:
	in   button_status BUTTONS					;Get buttons status.

	load temp_reg0     button_status			;
	and  temp_reg0     #$0004					;If button 2 pressed,
	comp temp_reg0     #$0004					;run Copy_EEPROM function.
	jpz  CopyEEPROM								;
	
	load temp_reg0     button_status			;
	and  temp_reg0     #$0002					;If button 1 pressed,
	comp temp_reg0     #$0002					;run Dump_EEPROM function.
	jpz  DumpEEPROM								;

	load temp_reg0     button_status			;
	and  temp_reg0     #$0001					;If button 0 pressed,
	comp temp_reg0     #$0001					;run Burn_EEPROM function.
	jpz  BurnEEPROM								;

	ret											;No buttons pressed, return.

;This function is called once on reset and sends all the necessary commands to configure
;the LCD display. This function must be called before characters can be displayed.
LCD_Init:
    load i2c_tx_data #$007C						;
	load i2c_type MASTER_TX_C					;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data #$0000						;No more control bytes before stop,
	load i2c_type MASTER_TX_C					;write to config registers.
	call I2CTXByte								;

	load i2c_tx_data #$0038						;
	load i2c_type MASTER_P						;Wake up display.
	call I2CTXByte								;

	call Wait_For_Delay							;Delay while display wakes up.

	load i2c_tx_data #$007C						;
	load i2c_type MASTER_TX_C					;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data #$0000						;No more control bytes before stop,
	load i2c_type MASTER_TX_C					;write to config registers.
	call I2CTXByte								;

	load i2c_tx_data #$0039						;Function set. Interface data = 8 bits.
	load i2c_type MASTER_P						;Line numbers = 2.  Single height font.
	call I2CTXByte								;Instruction table 1.

	call Wait_For_Delay							;Wait for last instruction to finish.

	ClearLCD:
	load i2c_tx_data #$007C						;
	load i2c_type MASTER_TX_C					;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data #$0000						;No more control bytes before stop,
	load i2c_type MASTER_TX_C					;write to config registers.
	call I2CTXByte								;

	load i2c_tx_data #$0014						;
	load i2c_type MASTER_TX_C					;Internal OSC frequency.
	call I2CTXByte								;

	load i2c_tx_data #$0078						;
	load i2c_type MASTER_TX_C					;Contrast set.
	call I2CTXByte								;

	load i2c_tx_data #$005C						;
	load i2c_type MASTER_TX_C					;ICON control.
	call I2CTXByte								;

	load i2c_tx_data #$006F						;
	load i2c_type MASTER_TX_C					;Follower control.
	call I2CTXByte								;

	load i2c_tx_data #$000C						;
	load i2c_type MASTER_TX_C					;Display ON. Entire display on.
	call I2CTXByte								;

	load i2c_tx_data #$0001						;
	load i2c_type MASTER_TX_C					;Clear display.
	call I2CTXByte								;

	load i2c_tx_data #$0006						;
	load i2c_type MASTER_P						;Entry mode set.
	call I2CTXByte								;

    call Wait_For_Delay							;Wait for configuration to take effect.
	ret											;

;This function updates the animation on the LCD display when the
;ready message is displayed. 
ReadyUpdate:
	load idle_reg_lo #0							;Zero out animation delay regs.
	load idle_reg_hi #0							;

	load i2c_tx_data	#$007C					;
	load i2c_type		MASTER_TX_C				;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data	#$0000					;
	load i2c_type		MASTER_TX_C				;No more control bytes before stop.
	call I2CTXByte								;

	load i2c_tx_data	#$0080					;
	load i2c_type		MASTER_P				;Switch to first line of display.
	call I2CTXByte								;

	call Wait_For_Stop							;Wait for I2C bus to go idle.

	load i2c_tx_data	#$007C					;
	load i2c_type		MASTER_TX_C				;Address LCD display, write.
	call I2CTXByte								;

	load i2c_tx_data	#$0040					;No more control bytes before stop,
	load i2c_type		MASTER_TX_C				;write message to display "READY".
	call I2CTXByte								;

	load i2c_tx_data	R						;
	load i2c_type		MASTER_TX_C				;Write 'R' to display.
	call I2CTXByte								;

	load i2c_tx_data	E						;
	load i2c_type		MASTER_TX_C				;Write 'E' to display.
	call I2CTXByte								;

	load i2c_tx_data	A						;
	load i2c_type		MASTER_TX_C				;Write 'A' to display.
	call I2CTXByte								;

	load i2c_tx_data	D						;
	load i2c_type		MASTER_TX_C				;Write 'D' to display.
	call I2CTXByte								;

	load i2c_tx_data	Y						;
	load i2c_type		MASTER_TX_C				;Write 'Y' to display.
	call I2CTXByte								;

	and  rdy_pat_reg	#3						;Discard all but the lower 2 bits.

	comp rdy_pat_reg	#0						;
	clz  UpperBar								;
												;
	comp rdy_pat_reg	#1						;
	clz  MiddleBar								;Find the proper animation 
												;based on rdy_pat_reg.
	comp rdy_pat_reg	#2						;
	clz  LowerBar								;
												;
	comp rdy_pat_reg	#3						;
	clz  MiddleBar								;

	load i2c_type		MASTER_P				;End LCD display write.
	call I2CTXByte								;

	add  rdy_pat_reg	#1						;Increment rdy_pat_reg.

	call Wait_For_Stop							;Wait for I2C bus to go idle.
	ret											;

LowerBar:
	load i2c_tx_data	LBAR					;Load lower bar animation.
	ret											;

MiddleBar:
	load i2c_tx_data	MBAR					;Load middle bar animation.
	ret											;

UpperBar:	
	load i2c_tx_data	UBAR					;Load upper bar animation.
	ret											;

;This function is called while the device is idle.  It checks to see if it is time
;to update the animation after the "READY" message.
Idle:
	clrc										;
	addc idle_reg_lo	#1						;Increment lo reg.
	addc idle_reg_hi	#0						;Add carry (if any) to hi reg.

	comp idle_reg_hi	#2						;Update animation if necessary.
	clz ReadyUpdate								;
    
	ret											;Done with LCD update.

;This function consumes processor cycles for a defined period of time. It is used
;to delay tasks while the LCD display is updating.
Wait_For_Delay:
	load wait_reg_lo #0							;Zero out wait regs.
	load wait_reg_hi #0							;

	clrc										;
	WaitLoop:									;
	addc wait_reg_lo #1							;Loop to kill some time.
	addc wait_reg_hi #0							;
	comp wait_reg_hi #1							;
	
	rtz											;Delay done. Exit function.
    
	jump WaitLoop								;Wait some more.

;Wait for the I2C line to go idle.
Wait_For_Stop:
    in   i2c_status_in I2C_STATUS				;
	and  i2c_status_in I2C_MASTER_INACTIVE		;Wait for I2C controller
	comp i2c_status_in I2C_MASTER_INACTIVE		;to be inactive.
    jpnz Wait_For_Stop							;
	ret											;

;Wait for the UART transmit buffer to empty.
Wait_For_UART:
    in   uart_tx_count TX_COUNT					;Check to see how many bytes
	comp uart_tx_count #0						;are in the UART buffer.
	jpnz Wait_For_UART							;If none, done waiting.
    ret											;

;This function transmits a byte of information out the I2C line.
I2CTXByte:
    in   i2c_status_in I2C_STATUS				;
	and  i2c_status_in I2C_MASTER_TX_VALID		;Check I2C valid status.
	comp i2c_status_in #1						;

	jpnz  I2CTXByte								;Loop until buffer is empty.

	out  i2c_type LOAD_MASTER_TYPE				;Load byte type.
	out  i2c_tx_data LOAD_MASTER_TX_DATA		;Load TX buffer data.
	load temp_reg0 #1							;
	out  temp_reg0 MASTER_TX_VALID				;Validate byte to send.
	out  temp_reg0 MASTER_ENABLED				;Start I2C tx(if not already started).
    ret											;

;This function receives a byte of information from the I2C line.
I2CRXByte:
    in   i2c_status_in I2C_STATUS				;
	and  i2c_status_in I2C_MASTER_RX_VALID		;Check I2C valid status.
	comp i2c_status_in #4						;

	jpnz  I2CRXByte								;Loop until rx buffer is valid.

	in   i2c_read_byte I2C_DATA					;Read in received byte.
	load temp_reg0 #0							;
	out  temp_reg0 RX_VALID						;Rx byte has been read.
    ret											;
	