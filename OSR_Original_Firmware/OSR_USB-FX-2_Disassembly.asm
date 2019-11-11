;---------------------------------------Defines---------------------------------------
;Special function registers and direct addressing registers.  Registers starting with
;a double underscore (__) are undefined and should not be used.

.equ R0BNK0, $00
.equ R1BNK0, $01
.equ R2BNK0, $02
.equ R3BNK0, $03
.equ R4BNK0, $04
.equ R5BNK0, $05
.equ R6BNK0, $06
.equ R7BNK0, $07
.equ R0BNK1, $08
.equ R1BNK1, $09
.equ R2BNK1, $0a
.equ R3BNK1, $0b
.equ R4BNK1, $0c
.equ R5BNK1, $0d
.equ R6BNK1, $0e
.equ R7BNK1, $0f
.equ R0BNK2, $10
.equ R1BNK2, $11
.equ R2BNK2, $12
.equ R3BNK2, $13
.equ R4BNK2, $14
.equ R5BNK2, $15
.equ R6BNK2, $16
.equ R7BNK2, $17
.equ R0BNK3, $18
.equ UPPER_MS_DELAY, $19				;Used in millisecond timer - upper byte.
.equ LOWER_MS_DELAY, $1a				;Used in millisecond timer - lower byte.
.equ R3BNK3, $1b
.equ R4BNK3, $1c
.equ R5BNK3, $1d
.equ R6BNK3, $1e
.equ R7BNK3, $1f
.equ R20, $20							;Contains various bit flags.
.equ CFG_DESC_HS_HI, $21				;High-speed configuration descriptor pointer, high byte.
.equ CFG_DESC_HS_LO, $22				;High-speed configuration descriptor pointer, low byte.
.equ DEV_DESC_HI,    $23				;Device descriptor pointer, high byte.
.equ DEV_DESC_LO,    $24				;Device descriptor pointer, low byte.
.equ THIS_CFG_HI,    $25				;Current configuration descriptor pointer, high byte.
.equ THIS_CFG_LO,    $26				;Current configuration descriptor pointer, low byte.
.equ OS_CFG_HI,      $27				;Other speed configuration descriptor pointer, high byte.
.equ OS_CFG_LO,      $28				;Other speed configuration descriptor pointer, low byte.
.equ CFG_DESC_FS_HI, $29				;Full-speed configuration descriptor pointer, high byte.
.equ CFG_DESC_FS_LO, $2a				;Full-speed configuration descriptor pointer, low byte.
.equ DEV_QUAL_HI,    $2b				;Device qualifier pointer, high byte.
.equ DEV_QUAL_LO,    $2c				;Device qualifier pointer, low byte.
.equ STRING_DESC_HI, $2d				;String descriptor pointer, high byte.
.equ STRING_DESC_LO, $2e				;String descriptor pointer, low byte.
.equ INTERFACE_BYTE, $2f				;Stores set/get interface value. Has no real function.
.equ LAST_SW_STATE,  $30				;Stores the last state of the switches.
.equ R31, $31
.equ R32, $32
.equ R33, $33
.equ R34, $34
.equ FIRMWARE_MAJ_PNTR, $35				;Pointer to 7 seg digit for firmware major number.
.equ R36, $36
.equ FIRMWARE_MIN_PNTR, $37				;Pointer to 7 seg digit for firmware minor number.
.equ CONFIG_BYTE,       $38				;Stores set/get config value. Has no real function.						
.equ R39, $39
.equ R3A, $3a
.equ R3B, $3b
.equ R3C, $3c
.equ R3D, $3d
.equ R3E, $3e
.equ R3F, $3f
.equ R40, $40
.equ R41, $41
.equ R42, $42
.equ R43, $43
.equ R44, $44
.equ R45, $45
.equ R46, $46
.equ R47, $47
.equ R48, $48
.equ R49, $49
.equ R4A, $4a
.equ R4B, $4b
.equ R4C, $4c
.equ R4D, $4d
.equ R4E, $4e
.equ R4F, $4f
.equ R50, $50
.equ R51, $51
.equ R52, $52
.equ R53, $53
.equ R54, $54
.equ R55, $55
.equ R56, $56
.equ R57, $57
.equ R58, $58
.equ R59, $59
.equ R5A, $5a
.equ R5B, $5b
.equ R5C, $5c
.equ R5D, $5d
.equ R5E, $5e
.equ R5F, $5f
.equ R60, $60
.equ R61, $61
.equ R62, $62
.equ R63, $63
.equ R64, $64
.equ R65, $65
.equ R66, $66
.equ R67, $67
.equ R68, $68
.equ R69, $69
.equ R6A, $6a
.equ R6B, $6b
.equ R6C, $6c
.equ R6D, $6d
.equ R6E, $6e
.equ R6F, $6f
.equ R70, $70
.equ R71, $71
.equ R72, $72
.equ R73, $73
.equ R74, $74
.equ R75, $75
.equ R76, $76
.equ R77, $77
.equ R78, $78
.equ R79, $79
.equ R7A, $7a
.equ R7B, $7b
.equ R7C, $7c
.equ R7D, $7d
.equ R7E, $7e
.equ R7F, $7f
.equ IOA, $80
.equ SP, $81
.equ DPL0, $82
.equ DPH0, $83
.equ DPL1, $84
.equ DPH1, $85
.equ DPS, $86
.equ PCON, $87
.equ TCON, $88
.equ TMOD, $89
.equ TL0, $8a
.equ TL1, $8b
.equ TH0, $8c
.equ TH1, $8d
.equ CKCON, $8e
.equ __8F, $8f
.equ IOB, $90
.equ EXIF, $91
.equ MPAGE, $92
.equ __93, $93
.equ __94, $94
.equ __95, $95
.equ __96, $96
.equ __97, $97
.equ SCON0, $98
.equ SBUF0, $99
.equ AUTOPTRH1, $9a
.equ AUTOPTRL1, $9b
.equ __9C, $9c
.equ AUTOPTRH2, $9d
.equ AUTOPTRL2, $9e
.equ __9F, $9f
.equ IOC, $a0
.equ INT2CLR, $a1
.equ INT4CLR, $a2
.equ __A3, $a3
.equ __A4, $a4
.equ __A5, $a5
.equ __A6, $a6
.equ __A7, $a7
.equ IE, $a8
.equ __A9, $a9
.equ EP2468STAT, $aa
.equ EP24FIFOLGS, $ab
.equ EP68FIFOLGS, $ac
.equ __AD, $ad
.equ __AE, $ae
.equ AUTOPTR_SETUP, $af
.equ IOD, $b0
.equ IOE, $b1
.equ OEA, $b2
.equ OEB, $b3
.equ OEC, $b4
.equ OED, $b5
.equ OEE, $b6
.equ __B7, $b7
.equ IP, $b8
.equ __B9, $b9
.equ EP01STAT, $ba
.equ GPIFTRIG, $bb
.equ __BC, $bc
.equ GPIFSGLDATH, $bd
.equ GPIFSGLDATLX, $be
.equ GPIFSGLDATLNOX, $bf
.equ SCON1, $c0
.equ SBUF1, $c1
.equ __C2, $c2
.equ __C3, $c3
.equ __C4, $c4
.equ __C5, $c5
.equ __C6, $c6
.equ __C7, $c7
.equ T2CON, $c8
.equ __C9, $c9
.equ RCAP2L, $ca
.equ RCAP2H, $cb
.equ TL2, $cc
.equ TH2, $cd
.equ __CE, $ce
.equ __CF, $cf
.equ PSW, $d0
.equ __D1, $d1
.equ __D2, $d2
.equ __D3, $d3
.equ __D4, $d4
.equ __D5, $d5
.equ __D6, $d6
.equ __D7, $d7
.equ EICON, $d8
.equ __D9, $d9
.equ __DA, $da
.equ __DB, $db
.equ __DC, $dc
.equ __DD, $dd
.equ __DE, $de
.equ __DF, $df
.equ ACC, $e0
.equ __E1, $e1
.equ __E2, $e2
.equ __E3, $e3
.equ __E4, $e4
.equ __E5, $e5
.equ __E6, $e6
.equ __E7, $e7
.equ EIE, $e8
.equ __E9, $e9
.equ __EA, $ea
.equ __EB, $eb
.equ __EC, $ec
.equ __ED, $ed
.equ __EE, $ee
.equ __EF, $ef
.equ B, $f0
.equ __F1, $f1
.equ __F2, $f2
.equ __F3, $f3
.equ __F4, $f4
.equ __F5, $f5
.equ __F6, $f6
.equ __F7, $f7
.equ EIP, $f8
.equ __F9, $f9
.equ __FA, $fa
.equ __FB, $fb
.equ __FC, $fc
.equ __FD, $fd
.equ __FE, $fe
.equ __FF, $ff

;Bit addressable defines.

.equ REMOTE_WAKEUP,  $00				;1=Remote wakeup enabled, 0=Remote wakeup disabled.
.equ SETUP_DAT_PEND, $01				;1=Setup data pending, 0=No setup data pending.
.equ SELF_POWERED,   $02				;Always set to 0 to indicate device is never self powered.
.equ IS_SUSPENDED,   $03				;1=Chip is suspended, 0=Chip is awake.
.equ RENUM_DISABLE,  $04				;1=Disables RENUM, 0=Leave RENUM in current state.
.equ R20_5, $05
.equ R20_6, $06
.equ R20_7, $07
.equ R21_0, $08
.equ R21_1, $09
.equ R21_2, $0a
.equ R21_3, $0b
.equ R21_4, $0c
.equ R21_5, $0d
.equ R21_6, $0e
.equ R21_7, $0f
.equ R22_0, $10
.equ R22_1, $11
.equ R22_2, $12
.equ R22_3, $13
.equ R22_4, $14
.equ R22_5, $15
.equ R22_6, $16
.equ R22_7, $17
.equ R23_0, $18
.equ R23_1, $19
.equ R23_2, $1a
.equ R23_3, $1b
.equ R23_4, $1c
.equ R23_5, $1d
.equ R23_6, $1e
.equ R23_7, $1f
.equ R24_0, $20
.equ R24_1, $21
.equ R24_2, $22
.equ R24_3, $23
.equ R24_4, $24
.equ R24_5, $25
.equ R24_6, $26
.equ R24_7, $27
.equ R25_0, $28
.equ R25_1, $29
.equ R25_2, $2a
.equ R25_3, $2b
.equ R25_4, $2c
.equ R25_5, $2d
.equ R25_6, $2e
.equ R25_7, $2f
.equ R26_0, $30
.equ R26_1, $31
.equ R26_2, $32
.equ R26_3, $33
.equ R26_4, $34
.equ R26_5, $35
.equ R26_6, $36
.equ R26_7, $37
.equ R27_0, $38
.equ R27_1, $39
.equ R27_2, $3a
.equ R27_3, $3b
.equ R27_4, $3c
.equ R27_5, $3d
.equ R27_6, $3e
.equ R27_7, $3f
.equ R28_0, $40
.equ R28_1, $41
.equ R28_2, $42
.equ R28_3, $43
.equ R28_4, $44
.equ R28_5, $45
.equ R28_6, $46
.equ R28_7, $47
.equ R29_0, $48
.equ R29_1, $49
.equ R29_2, $4a
.equ R29_3, $4b
.equ R29_4, $4c
.equ R29_5, $4d
.equ R29_6, $4e
.equ R29_7, $4f
.equ R2A_0, $50
.equ R2A_1, $51
.equ R2A_2, $52
.equ R2A_3, $53
.equ R2A_4, $54
.equ R2A_5, $55
.equ R2A_6, $56
.equ R2A_7, $57
.equ R2B_0, $58
.equ R2B_1, $59
.equ R2B_2, $5a
.equ R2B_3, $5b
.equ R2B_4, $5c
.equ R2B_5, $5d
.equ R2B_6, $5e
.equ R2B_7, $5f
.equ R2C_0, $60
.equ R2C_1, $61
.equ R2C_2, $62
.equ R2C_3, $63
.equ R2C_4, $64
.equ R2C_5, $65
.equ R2C_6, $66
.equ R2C_7, $67
.equ R2D_0, $68
.equ R2D_1, $69
.equ R2D_2, $6a
.equ R2D_3, $6b
.equ R2D_4, $6c
.equ R2D_5, $6d
.equ R2D_6, $6e
.equ R2D_7, $6f
.equ R2E_0, $70
.equ R2E_1, $71
.equ R2E_2, $72
.equ R2E_3, $73
.equ R2E_4, $74
.equ R2E_5, $75
.equ R2E_6, $76
.equ R2E_7, $77
.equ R2F_0, $78
.equ R2F_1, $79
.equ R2F_2, $7a
.equ R2F_3, $7b
.equ R2F_4, $7c
.equ R2F_5, $7d
.equ R2F_6, $7e
.equ R2F_7, $7f
.equ IOA_0, $80
.equ IOA_1, $81
.equ IOA_2, $82
.equ IOA_3, $83
.equ IOA_4, $84
.equ IOA_5, $85
.equ IOA_6, $86
.equ IOA_7, $87
.equ TCON_0, $88
.equ TCON_1, $89
.equ TCON_2, $8a
.equ TCON_3, $8b
.equ TCON_4, $8c
.equ TCON_5, $8d
.equ TCON_6, $8e
.equ TCON_7, $8f
.equ IOB_0, $90
.equ IOB_1, $91
.equ IOB_2, $92
.equ IOB_3, $93
.equ IOB_4, $94
.equ IOB_5, $95
.equ IOB_6, $96
.equ IOB_7, $97
.equ SCON0_0, $98
.equ SCON0_1, $99
.equ SCON0_2, $9a
.equ SCON0_3, $9b
.equ SCON0_4, $9c
.equ SCON0_5, $9d
.equ SCON0_6, $9e
.equ SCON0_7, $9f
.equ IOC_0, $a0
.equ IOC_1, $a1
.equ IOC_2, $a2
.equ IOC_3, $a3
.equ IOC_4, $a4
.equ IOC_5, $a5
.equ IOC_6, $a6
.equ IOC_7, $a7
.equ IE_0, $a8
.equ IE_1, $a9
.equ IE_2, $aa
.equ IE_3, $ab
.equ IE_4, $ac
.equ IE_5, $ad
.equ IE_6, $ae
.equ IE_7, $af
.equ IOD_0, $b0
.equ IOD_1, $b1
.equ IOD_2, $b2
.equ IOD_3, $b3
.equ IOD_4, $b4
.equ IOD_5, $b5
.equ IOD_6, $b6
.equ IOD_7, $b7
.equ IP_0, $b8
.equ IP_1, $b9
.equ IP_2, $ba
.equ IP_3, $bb
.equ IP_4, $bc
.equ IP_5, $bd
.equ IP_6, $be
.equ IP_7, $bf
.equ SCON1_0, $c0
.equ SCON1_1, $c1
.equ SCON1_2, $c2
.equ SCON1_3, $c3
.equ SCON1_4, $c4
.equ SCON1_5, $c5
.equ SCON1_6, $c6
.equ SCON1_7, $c7
.equ T2CON_0, $c8
.equ T2CON_1, $c9
.equ T2CON_2, $ca
.equ T2CON_3, $cb
.equ T2CON_4, $cc
.equ T2CON_5, $cd
.equ T2CON_6, $ce
.equ T2CON_7, $cf
.equ PSW_0, $d0
.equ PSW_1, $d1
.equ PSW_2, $d2
.equ PSW_3, $d3
.equ PSW_4, $d4
.equ PSW_5, $d5
.equ PSW_6, $d6
.equ PSW_7, $d7
.equ EICON_0, $d8
.equ EICON_1, $d9
.equ EICON_2, $da
.equ EICON_3, $db
.equ EICON_4, $dc
.equ EICON_5, $dd
.equ EICON_6, $de
.equ EICON_7, $df
.equ ACC_0, $e0
.equ ACC_1, $e1
.equ ACC_2, $e2
.equ ACC_3, $e3
.equ ACC_4, $e4
.equ ACC_5, $e5
.equ ACC_6, $e6
.equ ACC_7, $e7
.equ EIE_0, $e8
.equ EIE_1, $e9
.equ EIE_2, $ea
.equ EIE_3, $eb
.equ EIE_4, $ec
.equ EIE_5, $ed
.equ EIE_6, $ee
.equ EIE_7, $ef
.equ B_0, $f0
.equ B_1, $f1
.equ B_2, $f2
.equ B_3, $f3
.equ B_4, $f4
.equ B_5, $f5
.equ B_6, $f6
.equ B_7, $f7
.equ EIP_0, $f8
.equ EIP_1, $f9
.equ EIP_2, $fa
.equ EIP_3, $fb
.equ EIP_4, $fc
.equ EIP_5, $fd
.equ EIP_6, $fe
.equ EIP_7, $ff

;------------------------------------USB Registers------------------------------------;

.equ CPUCS								#$E600
.equ IFCONFIG							#$E601

.equ FIFORESET							#$E604

.equ EP1INCFG							#$E611
.equ EP2CFG								#$E612

.equ EP6CFG								#$E614
.equ EP8CFG								#$E615

.equ USBIE								#$E65C
.equ USBIRQ								#$E65D

.equ EPIRQ								#$E65F

.equ INTSETUP							#$E668

.equ XAUTODAT1							#$E67B
.equ XAUTODAT2							#$E67C

.equ USBCS								#$E680
.equ SUSPEND							#$E681
.equ WAKEUPCS							#$E682
.equ TOGCTL								#$E683

.equ EP0BCH								#$E68A
.equ EP0BCL								#$E68B

.equ EP1INBC							#$E68F

.equ EP6BCH								#$E698
.equ EP6BCL								#$E699

.equ EP8BCH								#$E69C
.equ EP8BCL								#$E69D

.equ EP0CS								#$E6A0

.equ EP1INCS							#$E6A2

.equ SUDPTRH							#$E6B3
.equ SUDPTRL							#$E6B4

.equ SETUPDAT_0							#$E6B8
.equ SETUPDAT_1							#$E6B9
.equ SETUPDAT_2							#$E6BA
.equ SETUPDAT_3							#$E6BB
.equ SETUPDAT_4							#$E6BC
.equ SETUPDAT_5							#$E6BD
.equ SETUPDAT_6							#$E6BE
.equ SETUPDAT_7							#$E6BF

.equ EP0BUF								#$E740

.equ EP1INBUF							#$E7C0

;--------------------------------Initial Configuration--------------------------------;

.ebyte	$c2								;Indicate valid EEPROM on I2C bus.
.vid	$0547							;Vendor ID.
.pid	$2131							;Product ID.
.did	$0000							;Device ID.
.cbyte	$04								;Configuration byte. I2C 400KHz.

;----------------------------------Interrupt Vectors----------------------------------;

.org	$0000							;Reset vector.
	ljmp  RESET							;$08B8

.org	$0033							;Resume interrupt vector.
	ljmp  RESUME						;$0C4A

.org	$0043							;USB interrupt vector.
	ljmp USB_AND_IE4					;$0800

.org	$0053							;IE4 interrupt vector.
	ljmp USB_AND_IE4					;$0800

;------------------------------------Start of Code------------------------------------;

.org	$0080							;Size: $5c3

ProcessControlData:
	mov   dptr, SETUPDAT_1				;Get bmRequest byte from setup data.

CheckGetStatus:
	movx  a, @dptr						;bmRequest = GET_STATUS.
	jnz   CheckClearFeature				;
	ajmp  DoGetStatus					;Is bmRequest #$00? If so, branch.

CheckClearFeature:
	dec   a								;bmRequest = CLEAR_FEATURE.
	jnz   CheckSetFeature				;
	ajmp  DoClearFeature				;Is bmRequest #$01? If so, branch.

CheckSetFeature:
	add   a, #$fe						;bmRequest = SET_FEATURE.
	jnz   CheckGetConfig				;
	ajmp  DoSetFeature					;Is bmRequest #$03? If so, branch.

CheckGetConfig:
	add   a, #$fb						;bmRequest = GET_CONFIGURATION.
	jnz   CheckSetConfig				;
	ajmp  DoGetConfig					;Is bmRequest #$08? If so, branch.

CheckSetConfig:
	dec   a								;bmRequest = SET_CONFIGURATION.
	jnz   CheckGetInterface				;
	ajmp  DoSetConfig					;Is bmRequest #$09? If so, branch.

CheckGetInterface:
	dec   a								;bmRequest = GET_INTERFACE.
	jnz   CheckSetInterface				;
	ajmp  DoGetInterface				;Is bmRequest #$0A? If so, branch.

CheckSetInterface:
	dec   a								;bmRequest = SET_INTERFACE.
	jnz   CheckGetDescriptor			;
	ajmp  DoSetInterface				;Is bmRequest #$0B? If so, branch.

CheckGetDescriptor:
	add   a, #$05						;bmRequest = GET_DESCRIPTOR.
	jz    DoGetDescriptor				;Is bmRequest #$06? If so, branch.
	ajmp  DoOtherRequests				;Jump to check for non-standard requests.

DoGetDescriptor:
	lcall SetCarry03					;Set carry bit.
	jc    GetDescriptor					;Branch always.
	ajmp  EndControlTransfer			;

GetDescriptor:
	mov   dptr, SETUPDAT_3				;Get descriptor type byte from setup data.
	movx  a, @dptr						;

CheckCfgDesc:
	add   a, #$fe						;Descriptor type = configuration.
	jz    DoCfgDesc						;Is descriptor type #$02? If so, branch.

CheckStringDesc:
	dec   a								;Descriptor type = string.
	jz    DoStringDesc					;Is descriptor type #$03? If so, branch.

CheckDevQualDesc:
	add   a, #$fd						;Descriptor type = device qualifier.
	jz    DoDevQualDesc					;Is descriptor type #$06? If so, branch.

CheckOSConfigDesc:
	dec   a								;Descriptor type = other speed config.
	jz    DoOSConfigDesc				;Is descriptor type #$07? If so, branch.

CheckDeviceDesc:
	add   a, #$06						;Descriptor type = device.
	jnz   StallControlTransfer01		;Is descriptor type #$01? If not, branch (unsupported descriptor).

DoDeviceDesc:
	mov   a, DEV_DESC_HI				;Setting EP0 byte count not necessary. handled-->
	mov   dptr, SUDPTRH					;automatically by setup data pointer.
	movx  @dptr, a						;
	mov   a, DEV_DESC_LO				;Point the setup data pointer to the device descriptor.
	mov   dptr, SUDPTRL					;The device descriptor is stored at memory location $0600.
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

DoDevQualDesc:
	mov   a, DEV_QUAL_HI				;Setting EP0 byte count not necessary. handled-->
	mov   dptr, SUDPTRH					;automatically by setup data pointer.
	movx  @dptr, a						;
	mov   a, DEV_QUAL_LO				;Point the setup data pointer to the device qualifier.
	mov   dptr, SUDPTRL					;The device qualifier is stored at memory location $0612.
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

DoCfgDesc:
	mov   a, THIS_CFG_HI				;Setting EP0 byte count not necessary. handled-->
	mov   dptr, SUDPTRH					;automatically by setup data pointer.
	movx  @dptr, a						;
	mov   a, THIS_CFG_LO				;
	mov   dptr, SUDPTRL					;Point the setup data pointer to the current config descriptor.
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

DoOSConfigDesc:
	mov   a, OS_CFG_HI					;Setting EP0 byte count not necessary. handled-->
	mov   dptr, SUDPTRH					;automatically by setup data pointer.
	movx  @dptr, a						;
	mov   a, OS_CFG_LO					;
	mov   dptr, SUDPTRL					;Point the setup data pointer to the other speed config descriptor.
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

DoStringDesc:
	mov   dptr, SETUPDAT_2				;
	movx  a, @dptr						;Get string number from setup data.
	mov   r7, a							;
	lcall GetStringDescriptor			;Retrieve string descriptor.

	mov   r2, R6BNK0					;Make a copy of string descriptor to test for null pointer.
	mov   r1, R7BNK0					;
	mov   r3, #$01						;Load r3 with #$01.  Not used for anything.
	mov   a, r2							;Ensure a null pointer to the string descriptor is not passed-->
	orl   a, r1							;to the setup data pointer.
	jz    StallControlTransfer00		;If null pointer to string descriptor, stall the control transfer.

	mov   a, r6							;Setting EP0 byte count not necessary. handled-->
	mov   dptr, SUDPTRH					;automatically by setup data pointer.
	movx  @dptr, a						;
	mov   a, r7							;
	mov   dptr, SUDPTRL					;Point the setup data pointer to the string descriptor.
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

StallControlTransfer00:
	mov   dptr, EP0CS					;
	movx  a, @dptr						;
	orl   a, #$01						;Set the stall bit in the end point 0 control register.
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

StallControlTransfer01:
	mov   dptr, EP0CS					;
	movx  a, @dptr						;
	orl   a, #$01						;Set the stall bit in the end point 0 control register.
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

DoGetInterface:
	lcall GetInterface					;Get interface byte.
	ajmp  EndControlTransfer			;

DoSetInterface:
	lcall SetInterface					;Set interface byte.
	ajmp  EndControlTransfer			;

DoSetConfig:
	lcall SetConfig						;Set config byte.
	ajmp  EndControlTransfer			;

DoGetConfig:
	lcall GetConfig						;Get config byte.
	ajmp  EndControlTransfer			;

DoGetStatus:
	lcall SetCarry00					;
	jc    GetStatus						;Get device status.
	ajmp  EndControlTransfer			;

GetStatus:
	mov   dptr, SETUPDAT_0				;Get bmRequestType from setup data.
	movx  a, @dptr						;

	add   a, #$7f						;Is bmRequestType = 0x81? If so, jump to-->
	jz    SendZeroBytes					;Send two zero bytes as per EZ-USB_TRM page 42.

	dec   a								;Is bmRequestType = 0x82? If so, jump to-->
	jz    GetStallStatus				;check stall status of requested endpoint.

	add   a, #$02						;Is bmRequestType = 0x80? If so, jump to-->
	jz    WakeupAndPowerStatus			;get remote wakeup and self powered status.

	ajmp  StallControlTransfer02		;Unrecognized 'Get Status' request. Jump to stall.

WakeupAndPowerStatus:
	mov   c, REMOTE_WAKEUP				;
	clr   a								;Move remote wakeup status bit into accumulator.
	rlc   a								;
	mov   r7, a							;

	add   a, ACC						;Move bit to second position in byte.
	mov   r7, a							;

	mov   c, SELF_POWERED				;Constant 0 moved into carry bit. Device not self powered.
	clr   a								;
	rlc   a								;Move self powered status bit into accumulator.
	orl   a, r7							;OR the two bytes together to get complete status byte.
	mov   dptr, EP0BUF					;Put byte in endpoint 0 buffer.
	movx  @dptr, a						;

	clr   a								;
	inc   dptr							;Load second byte with 0.
	movx  @dptr, a						;

	mov   dptr, EP0BCH					;
	movx  @dptr, a						;
	mov   dptr, EP0BCL					;Load endpoint 0 byte count with 2.
	mov   a, #$02						;
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

SendZeroBytes:
	clr   a								;
	mov   dptr, EP0BUF					;
	movx  @dptr, a						;Load endpoint 0 buffer with two empty bytes.
	inc   dptr							;
	movx  @dptr, a						;

	mov   dptr, EP0BCH					;
	movx  @dptr, a						;
	mov   dptr, EP0BCL					;Load endpoint 0 byte count with 2.
	mov   a, #$02						;
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

GetStallStatus:
	mov   dptr, SETUPDAT_4				;Get endpoint byte.
	movx  a, @dptr						;
	anl   a, #$7e						;Remove MSB and LSB from byte and save it.
	mov   r7, a							;

	mov   r6, #$00						;Save empty byte in r6.

	movx  a, @dptr						;Get endpoint byte again.

	setb  c								;
	subb  a, #$80						;Is this a stall status request for an-->
	jc    OUTEndpointOffsets			;out endpoint? If so, branch.

INEndpointsOffsets:
	mov   r4, #$00						;
	mov   r5, #$01						;Set offsets for in endpoint data.
	sjmp  GetStallBits					;

OUTEndpointOffsets:
	mov   r4, #$00						;Set offsets for out endpoint data.
	mov   r5, #$00						;

GetStallBits:
	mov   a, r4							;
	orl   a, r6							;Will always be 0.
	mov   r6, a							;

	mov   a, r5							;
	orl   a, r7							;Set pointer into EndpointCSOffsetTbl(lower byte).
	add   a, }EndpointCSOffsetTbl		;
	mov   DPL0, a						;

	mov   a, {EndpointCSOffsetTbl		;
	addc  a, r6							;Set pointer into EndpointCSOffsetTbl(upper byte).
	mov   DPH0, a						;

	clr   a								;
	movc  a, @a+dptr					;Get offset from EndpointCSOffsetTbl.
	mov   r7, a							;

	rlc   a								;Add 1 to upper pointer byte if it crosses-->
	subb  a, ACC						;over the page boundary(should never happen).
	mov   r6, a							;

	mov   a, r7							;Set up lower bit of data pointer.-->
	add   a, #$a1						;It will alwyas be between #$A1 and #$A6.
	mov   r7, a							;

	mov   a, r6							;Set up upper bit of data pointer.-->
	addc  a, #$e6						;It will always be #$E6.

	mov   DPL0, r7						;
	mov   DPH0, a						;
	movx  a, @dptr						;Get control and status bit for selected endpoint.
	anl   a, #$01						;Discard all bits except the stall bit.
	mov   dptr, EP0BUF					;
	movx  @dptr, a						;Place stall bit in enpoint 0 buffer.

	clr   a								;
	inc   dptr							;Load second byte with 0.
	movx  @dptr, a						;

	mov   dptr, EP0BCH					;
	movx  @dptr, a						;
	mov   dptr, EP0BCL					;Load endpoint 0 byte count with 2.
	mov   a, #$02						;
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

StallControlTransfer02:
	mov   dptr, EP0CS					;
	movx  a, @dptr						;
	orl   a, #$01						;Set the stall bit in the end point 0 control register.
	movx  @dptr, a						;
	ajmp  EndControlTransfer			;

DoClearFeature:
	lcall SetCarry01					;Clear device feature.
	jc    ClearFeature					;Jump always.
	ajmp  EndControlTransfer			;

ClearFeature:
	mov   dptr, SETUPDAT_0				;Get bmRequestType.
	movx  a, @dptr						;
	add   a, #$fe						;Is bmRequestType = #$02?-->
	jz    ClearStall					;If so, branch to clear stall.

	add   a, #$02						;Is bmRequestType = #$00?-->
	jz    ClearWakeup					;If so, branch to disable remote wakeup.
	ajmp  EndControlTransfer			;No other clear features supported.

ClearWakeup:
	mov   dptr, SETUPDAT_2				;Get wValueL
	movx  a, @dptr						;
	cjne  a, #$01, StallEP0_1			;#$01 is only valid value.  Any other value will stall.
	clr   REMOTE_WAKEUP					;Clear remote wakeup enable bit.
	ajmp  EndControlTransfer			;

StallEP0_1:
	mov   dptr, EP0CS					;Unrecognized clear command.
	movx  a, @dptr						;
	orl   a, #$01						;
	movx  @dptr, a						;Get endpoint 0 status and set stall bit.
	ajmp  EndControlTransfer			;

ClearStall:
	mov   dptr, SETUPDAT_2				;
	movx  a, @dptr						;If wValueL != #$00, stall endpoint 0.
	jnz   StallEP0_2					;

	mov   dptr, SETUPDAT_4				;Get endpoint byte.
	movx  a, @dptr						;
	anl   a, #$7e						;Remove MSB and LSB from byte and save it.
	mov   r7, a							;

	mov   r6, #$00						;Save empty byte in r6.

	movx  a, @dptr						;Get endpoint byte again.

	setb  c								;
	subb  a, #$80						;Is this a clear stall request for an-->
	jc    _OUTEndpointOffsets			;out endpoint? If so, branch.

_INEndpointsOffsets:
	mov   r4, #$00						;
	mov   r5, #$01						;Set offsets for in endpoint data.
	sjmp  ClearStallBits				;

_OUTEndpointOffsets:
	mov   r4, #$00						;Set offsets for out endpoint data.
	mov   r5, #$00						;

ClearStallBits:
	mov   a, r4							;
	orl   a, r6							;Will always be 0.
	mov   r6, a							;

	mov   a, r5							;
	orl   a, r7							;Set pointer into EndpointCSOffsetTbl(lower byte).
	add   a, }EndpointCSOffsetTbl		;
	mov   DPL0, a						;

	mov   a, {EndpointCSOffsetTbl		;
	addc  a, r6							;Set pointer into EndpointCSOffsetTbl(upper byte).
	mov   DPH0, a						;

	clr   a								;
	movc  a, @a+dptr					;Get offset from EndpointCSOffsetTbl.
	mov   r7, a							;

	rlc   a								;Add 1 to upper pointer byte if it crosses-->
	subb  a, ACC						;over the page boundary(should never happen).
	mov   r6, a							;

	mov   a, r7							;Set up lower bit of data pointer.-->
	add   a, #$a1						;It will alwyas be between #$A1 and #$A6.
	mov   r7, a							;

	mov   a, r6							;Set up upper bit of data pointer.-->
	addc  a, #$e6						;It will always be #$E6.

	mov   DPL0, r7						;
	mov   DPH0, a						;Get endpoint control and status register contents.-->
	movx  a, @dptr						;Clear the stall bit and save the result back into-->
	anl   a, #$fe						;the control and status register.
	movx  @dptr, a						;

	mov   dptr, SETUPDAT_4				;Get endpoint again.
	movx  a, @dptr						;

	anl   a, #$80						;Keep only IN/OUT bit.
	mov   r7, a							;
	rrc   a								;
	rrc   a								;Move I/O bit to bit 4.  This is the I/O-->
	rrc   a								;indicator in TOGCTL.
	anl   a, #$1f						;
	mov   r7, a							;

	movx  a, @dptr						;Get endpoint again.
	anl   a, #$0f						;Extract only the endpoint address.
	add   a, r7							;Add address to I/O bit for a complete TOGCTL byte.

	mov   dptr, TOGCTL					;A two step toggle clear process is used as per page 95-->
	movx  @dptr, a						;of the EZ-USB TRM.
	movx  a, @dptr						;First, write enpoind address and I/O bits.
	orl   a, #$20						;
	movx  @dptr, a						;Second, write the same data with the reset bit high.
	ajmp  EndControlTransfer			;

StallEP0_2:
	mov   dptr, EP0CS					;Unrecognized clear command.
	movx  a, @dptr						;
	orl   a, #$01						;
	movx  @dptr, a						;Get endpoint 0 status and set stall bit. 
	sjmp  EndControlTransfer			;

DoSetFeature:
	lcall SetCarry02					;Set carry.
	jnc   EndControlTransfer			;Never branch.

	mov   dptr, SETUPDAT_0				;Get bmRequestType.
	movx  a, @dptr						;
	add   a, #$fe						;Is bmRequestType = #$02?-->
	jz    SetStall						;If so, branch to set stall.

	add   a, #$02						;Is bmRequestType = #$02?-->
	jnz   EndControlTransfer			;If not, branch to end set feature request.

	mov   dptr, SETUPDAT_2				;Get wValueL.
	movx  a, @dptr						;Is wValueL = #$01? If not, branch to check for-->
	cjne  a, #$01, CheckSetTestMode		;other request types.
	setb  REMOTE_WAKEUP					;Enable remote wakeup.
	sjmp  EndControlTransfer			;

CheckSetTestMode:
	mov   dptr, SETUPDAT_2				;Get wValueL again.
	movx  a, @dptr						;
	xrl   a, #$02						;Is wValueL = #$02? If so, set test mode-->
	jz    EndControlTransfer			;requested, otherwise branch to stall.

StallEP0_3:
	mov   dptr, EP0CS					;Unrecognized set feature command.
	movx  a, @dptr						;
	orl   a, #$01						;
	movx  @dptr, a						;Get endpoint 0 status and set stall bit. 
	sjmp  EndControlTransfer			;

SetStall:
	mov   dptr, SETUPDAT_4				;Get endpoint byte.
	movx  a, @dptr						;
	anl   a, #$7e						;Remove MSB and LSB from byte and save it.
	mov   r7, a							;

	mov   r6, #$00						;Save empty byte in r6.

	movx  a, @dptr						;Get endpoint byte again.

	setb  c								;
	subb  a, #$80						;Is this a set stall request for an-->
	jc    __OUTEndpointOffsets			;out endpoint? If so, branch.

__INEndpointsOffsets:
	mov   r4, #$00						;
	mov   r5, #$01						;Set offsets for in endpoint data.
	sjmp  SetStallBits					;

__OUTEndpointOffsets:
	mov   r4, #$00						;Set offsets for out endpoint data.
	mov   r5, #$00						;

SetStallBits:
	mov   a, r4							;
	orl   a, r6							;Will always be 0.
	mov   r6, a							;

	mov   a, r5							;
	orl   a, r7							;Set pointer into EndpointCSOffsetTbl(lower byte).
	add   a, }EndpointCSOffsetTbl		;
	mov   DPL0, a						;

	mov   a, {EndpointCSOffsetTbl		;
	addc  a, r6							;Set pointer into EndpointCSOffsetTbl(upper byte).
	mov   DPH0, a						;

	clr   a								;
	movc  a, @a+dptr					;Get offset from EndpointCSOffsetTbl.
	mov   r7, a							;

	rlc   a								;Add 1 to upper pointer byte if it crosses-->
	subb  a, ACC						;over the page boundary(should never happen).
	mov   r6, a							;

	mov   a, r7							;Set up lower bit of data pointer.-->
	add   a, #$a1						;It will alwyas be between #$A1 and #$A6.
	mov   r7, a							;

	mov   a, r6							;Set up upper bit of data pointer.-->
	addc  a, #$e6						;It will always be #$E6.

	mov   DPL0, r7						;
	mov   DPH0, a						;Get endpoint control and status register contents.-->
	movx  a, @dptr						;Set the stall bit and save the result back into-->
	orl   a, #$01						;the control and status register.
	movx  @dptr, a						;
	sjmp  EndControlTransfer			;

DoOtherRequests:
	lcall HandleOtherRequests			;Check for any other requests to be handled and-->
	jnc   EndControlTransfer			;end the control transfer.

StallControlTransfer:
	mov   dptr, EP0CS					;
	movx  a, @dptr						;
	orl   a, #$01						;Set the stall bit in the end point 0 control register.
	movx  @dptr, a						;

EndControlTransfer:
	mov   dptr, EP0CS					;
	movx  a, @dptr						;
	orl   a, #$80						;Clear end point 0 NAK.
	movx  @dptr, a						;
	ret									;

InitBoard:
	clr   a								;
	mov   R3BNK2, a						;
	mov   R2BNK2, a						;
	mov   R1BNK2, a						;
	mov   R0BNK2, a						;Clear various registers on startup.
	clr   IS_SUSPENDED					;
	clr   REMOTE_WAKEUP 				;
	clr   SELF_POWERED					;
	clr   SETUP_DAT_PEND				;

	lcall StartupRoutine				;Do some setup and do routine on 7 seg and bargraph.

	mov   r6, #$06						;
	mov   r7, #$00						;
	mov   DEV_DESC_HI, r6				;
	mov   DEV_DESC_LO, r7				;
	mov   DEV_QUAL_HI, #$06				;
	mov   DEV_QUAL_LO, #$12				;
	mov   CFG_DESC_HS_HI, #$06			;Setup descriptor pointers.
	mov   CFG_DESC_HS_LO, #$1c			;
	mov   CFG_DESC_FS_HI, #$06			;
	mov   CFG_DESC_FS_LO, #$44			;
	mov   STRING_DESC_HI, #$06			;
	mov   STRING_DESC_LO, #$6c			;

	mov   a, r6							;If the descriptors are stored in memory above the 8KB mark,-->
	anl   a, #$e0						;The following functions will move the descriptors. This will-->
	jnz   PrepDataTransfer				;never happen with this version of the firmware.
	ajmp  EnableInterrupts				;

;----------------------------------Unused functions-----------------------------------;

;The following functions transfer the USB and string descriptors to lower
;program memory starting at address $0080. These functions are never used
;in this version of the firmware.  If they are used, it will overwrite
;the setup data handling functions and break the firmware.

PrepDataTransfer:
	mov   R4BNK2, #$00					;Save base address for destination of transfer($0080).
	mov   R5BNK2, #$80					;
	mov   R6BNK2, r6					;Save base address for source of transfer.
	mov   R7BNK2, r7					;

	clr   c								;
	mov   a, #$c6						;Subtract base address from end address of USB descriptors to get-->
	subb  a, r7							;the size in bytes of the descriptors.
	mov   r7, a							;
	mov   a, #$06						;It looks like a define was used to store the end address-->
	subb  a, r6							;of the descriptors.
	xch   a, r7							;
	add   a, #$02						;Add 2 for the two empty spots needed for word alignment.
	xch   a, r7							;Save lower byte count.
	addc  a, #$00						;
	mov   r6, a							;Save upper byte count.
	clr   a								;

	mov   R7BNK1, r7					;Setup initial byte count.
	mov   R6BNK1, r6					;
	mov   R5BNK1, a						;
	mov   R4BNK1, a						;
	mov   R3BNK1, a						;Zero byte counter.
	mov   R2BNK1, a						;
	mov   R1BNK1, a						;
	mov   R0BNK1, a						;

ClearByteLoop:
	mov   r7, R7BNK1					;
	mov   r6, R6BNK1					;
	mov   r5, R5BNK1					;
	mov   r4, R4BNK1					;Setup registers for CheckWords function.
	mov   r3, R3BNK1					;
	mov   r2, R2BNK1					;
	mov   r1, R1BNK1					;
	mov   r0, R0BNK1					;

	clr   c								;
	lcall CompareWords					;Is byte transfer complete? if so, branch.
	jnc   ClearCounter					;

	ClearDestByte:
	mov   a, R5BNK2						;Load low byte of destination base address.
	add   a, R3BNK1						;Add offset from the byte counter.
	mov   DPL0, a						;
	mov   a, R4BNK2						;Load high byte of destination base address.
	addc  a, R2BNK1						;Add offset from the byte counter.
	mov   DPH0, a						;
	mov   a, #$cd						;Store #$cd in destination.
	movx  @dptr, a						;

	IncrementCounter00:
	mov   a, R3BNK1						;
	add   a, #$01						;
	mov   R3BNK1, a						;
	clr   a								;
	addc  a, R2BNK1						;
	mov   R2BNK1, a						;Add 1 to the least significant byte of the counter and-->
	clr   a								;carry through the other three bytes.
	addc  a, R1BNK1						;
	mov   R1BNK1, a						;
	clr   a								;
	addc  a, R0BNK1						;
	mov   R0BNK1, a						;
	sjmp  ClearByteLoop					;Loop to clear another byte.

ClearCounter:
	clr   a								;
	mov   R3BNK1, a						;
	mov   R2BNK1, a						;Zero counter.
	mov   R1BNK1, a						;
	mov   R0BNK1, a						;

MoveDescLoop:
	mov   r7, R7BNK1					;Setup registers for CheckWords function.
	mov   r6, R6BNK1					;
	mov   r5, R5BNK1					;
	mov   r4, R4BNK1					;
	mov   r3, R3BNK1					;Counter low byte.
	mov   r2, R2BNK1					;Counter high byte.
	mov   r1, R1BNK1					;These 2 bytes are used in the counter,-->
	mov   r0, R0BNK1					;but should always be 0.

	clr   c								;
	lcall CompareWords					;Is byte transfer complete? if so, branch.
	jnc   MoveDescPointers				;

	MoveDataByte:
	mov   r6, R2BNK1					;Load source pointer into registers.
	mov   r7, R3BNK1					;
	mov   a, R7BNK2						;
	add   a, r7							;Add offset to lower byte and transfer it to the data pointer.
	mov   DPL0, a						;
	mov   a, R6BNK2						;
	addc  a, r6							;Add offset to upper byte and transfer it to the data pointer.
	mov   DPH0, a						;
	movx  a, @dptr						;Get data byte.
	mov   r5, a							;
	mov   a, R5BNK2						;Load destination pointers into registers.
	add   a, r7							;
	mov   DPL0, a						;
	mov   a, R4BNK2						;Add offset to lower byte and transfer it to the data pointer.
	addc  a, r6							;
	mov   DPH0, a						;Add offset to upper byte and transfer it to the data pointer.
	mov   a, r5							;
	movx  @dptr, a						;Store data byte in its new location.

	IncrementCounter01:
	mov   a, r7							;
	add   a, #$01						;
	mov   R3BNK1, a						;
	clr   a								;
	addc  a, r6							;
	mov   R2BNK1, a						;Add 1 to the least significant byte of the counter and-->
	clr   a								;carry through the other three bytes.
	addc  a, R1BNK1						;
	mov   R1BNK1, a						;
	clr   a								;
	addc  a, R0BNK1						;
	mov   R0BNK1, a						;
	sjmp  MoveDescLoop					;Loop to move another byte.

MoveDescPointers:
	mov   DEV_DESC_HI, R4BNK2			;Move device descriptor pointer to $0080 (beginning of upper 128).
	mov   DEV_DESC_LO, R5BNK2			;

	mov   a, #$00						;These must have been macros in to c source code because the-->
	add   a, #$80						;math only work correctly when the descriptor address base is $0600.
	mov   r7, a							;Load r7 with #$80
	mov   a, #$06						;
	addc  a, #$ff						;
	mov   r6, a							;Load r6 with #$05

	clr   c								;
	mov   a, DEV_QUAL_LO				;
	subb  a, r7							;
	mov   DEV_QUAL_LO, a				;Decrease pointer address by 1408 bytes.
	mov   a, DEV_QUAL_HI				;
	subb  a, r6							;
	mov   DEV_QUAL_HI, a				;

	clr   c								;
	mov   a, THIS_CFG_LO				;
	subb  a, r7							;
	mov   THIS_CFG_LO, a				;Decrease pointer address by 1408 bytes.
	mov   a, THIS_CFG_HI				;
	subb  a, r6							;
	mov   THIS_CFG_HI, a				;

	clr   c								;
	mov   a, OS_CFG_LO					;
	subb  a, r7							;
	mov   OS_CFG_LO, a					;Decrease pointer address by 1408 bytes.
	mov   a, OS_CFG_HI					;
	subb  a, r6							;
	mov   OS_CFG_HI, a					;

	clr   c								;
	mov   a, CFG_DESC_HS_LO				;
	subb  a, r7							;
	mov   CFG_DESC_HS_LO, a				;Decrease pointer address by 1408 bytes.
	mov   a, CFG_DESC_HS_HI				;
	subb  a, r6							;
	mov   CFG_DESC_HS_HI, a				;

	clr   c								;
	mov   a, CFG_DESC_FS_LO				;
	subb  a, r7							;
	mov   CFG_DESC_FS_LO, a				;Decrease pointer address by 1408 bytes.
	mov   a, CFG_DESC_FS_HI				;
	subb  a, r6							;
	mov   CFG_DESC_FS_HI, a				;

	clr   c								;
	mov   a, STRING_DESC_LO				;
	subb  a, r7							;
	mov   STRING_DESC_LO, a				;Decrease pointer address by 1408 bytes.
	mov   a, STRING_DESC_HI				;
	subb  a, r6							;
	mov   STRING_DESC_HI, a				;

;-------------------------------------------------------------------------------------;

EnableInterrupts:
	setb  EIE_0							;Enable USB interrupt.
	orl   EICON, #$20					;Enable resume interrupt.
	mov   dptr, INTSETUP				;
	movx  a, @dptr						;
	orl   a, #$09						;
	movx  @dptr, a						;Enable INT2 and INT4 autovectoring.
	mov   dptr, USBIE					;
	movx  a, @dptr						;
	orl   a, #$3d						;
	movx  @dptr, a						;Enable USB interrupts: HSGRANT, URES, SUSP, SUTOK, SUDAV.
	setb  IE_7							;Enable serial port 1 interrupt.
	mov   dptr, USBCS					;
	movx  a, @dptr						;
	jb    ACC_1, SetControlRegs			;Is RENUM disabled? If so, branch.
	setb  RENUM_DISABLE					;Prepare to disable RENUM.
	lcall InitializeUSB					;Setup USB registers.

	SetControlRegs:
	mov   dptr, USBCS					;
	movx  a, @dptr						;
	anl   a, #$f7						;Clear disconnect bit in USBCS.
	movx  @dptr, a						;
	anl   CKCON, #$f8					;Set timer 0, 1 and 2 frequencies to CLKOUT/4.
	clr   IS_SUSPENDED					;Indicate processor is not suspended.

MainLoop:
	jnb   SETUP_DAT_PEND, CheckSuspend	;Any setup data pending? if not, branch.
	lcall ProcessControlData			;Process pending setup data.
	clr   SETUP_DAT_PEND				;Clear setup data pending flag.

	CheckSuspend:
	jnb   IS_SUSPENDED, DoBulkData		;Is processor suspended? If not branch to process any bulk data.
	lcall DisplaySuspended				;Display an 'S' on 7 segment display for "Suspended".
	jnc   DoBulkData					;Never branches. Carry set in DisplaySuspended function.
	clr   IS_SUSPENDED					;Clear suspended flag.

	SuspendLoop:
	lcall PowerDown						;Enter low power state (idle).
	jb    REMOTE_WAKEUP, DoRemoteWakeup	;Remote wakeup enabled. Do some housekeeping.

	CheckWakeup2:
	mov   dptr, WAKEUPCS				;Get wakeup pins status.
	movx  a, @dptr						;
	jnb   ACC_7, CheckWakeup1			;
	movx  a, @dptr						;Was wakeup initiated by wakeup pin 2?-->
	jb    ACC_1, SuspendLoop			;If so, branch to put device back to sleep.

	CheckWakeup1:
	mov   dptr, WAKEUPCS				;Get wakeup pins status.
	movx  a, @dptr						;
	jnb   ACC_6, DoRemoteWakeup			;
	movx  a, @dptr						;Was wakeup initiated by wakeup pin 1?-->
	jb    ACC_0, SuspendLoop			;If so, branch to put device back to sleep.

	DoRemoteWakeup:
	lcall CheckPinWakeup				;Check the source of a wakeup signal.
	DoDisplayActive:
	lcall DisplayActive					;Display an 'A' on the 7 segment display.
	DoBulkData:
	lcall CheckBulkData					;Check for any buffered bulk data and switch status.
	DoMainLoop:
	sjmp  MainLoop						;Loop infinitely.

StartupRoutine:
	mov   dptr, CPUCS					;Get CPU status register ($e600).
	movx  a, @dptr						;
	anl   a, #$e7						;Keep all values except clock speed.
	orl   a, #$10						;Set CPU clock speed to 48 MHz.
	movx  @dptr, a						;Set CPU status register.

	mov   dptr, IFCONFIG				;Get interface configuration ($e601).
	movx  a, @dptr						;
	orl   a, #$40						;Set GPIF/FIFO clock speed to 48MHz.
	movx  @dptr, a						;Set interface configuration.

	mov   OEB, #$ff						;Make all 8 bits of port B outputs.
	mov   OED, #$ff						;Make all 8 bits of port D outputs.
	mov   IOD, #$ff						;Set all of port D bits high.

	mov   a, #$12						;Lower byte of external address.  Base address.
	add   a, FIRMWARE_MAJ_PNTR			;Add offset to base address to find lower address byte.
	mov   DPL0, a						;
	clr   a								;
	addc  a, #$10						;Load upper address in data pointer, plus any carry--> 
	mov   DPH0, a						;from base address addition.
	movx  a, @dptr						;
	cpl   a								;Invert a for common anode output.
	mov   IOB, a						;Display firmware major number on 7 segment display (3).

	mov   r7, #$f4						;
	mov   r6, #$01						;Delay for 500 milliseconds.
	lcall DoDelay						;

	mov   IOB, #$f7						;Display decimal point.

	mov   r7, #$f4						;
	mov   r6, #$01						;Delay for 500 milliseconds.
	lcall DoDelay						;

	mov   a, #$12						;Lower byte of external address.  Base address.
	add   a, FIRMWARE_MIN_PNTR			;Add offset to base address to find lower address byte.
	mov   DPL0, a						;
	clr   a								;
	addc  a, #$10						;Load upper address in data pointer, plus any carry--> 
	mov   DPH0, a						;from base address addition.
	movx  a, @dptr						;
	cpl   a								;Invert a for common anode output.
	mov   IOB, a						;Display firmware minor number on 7 segment display (5).

	mov   r7, #$f4						;
	mov   r6, #$01						;Delay for 500 milliseconds.
	lcall DoDelay						;

	clr   a								;Clear R36 to use as a loop counter to display-->
	mov   R36, a						;bargraph animations twice.

	BargraphAnimBegin:
	clr   a								;
	mov   R33, a						;Use R33 as an indicator for end of current animation sequence.
	mov   R34, a						;Use R34 as a pointer for next bargraph graphic.

	BargraphAnimLoop:
	mov   a, #$00						;
	add   a, R34						;Get index for next bargraph graphic.
	mov   DPL0, a						;
	clr   a								;
	addc  a, #$10						;Setup data pointer to get bargraph display byte.-->
	mov   DPH0, a						;Base address is $1000.
	movx  a, @dptr						;
	cpl   a								;
	mov   IOD, a						;Get bargraph graphic and display on output port.

	mov   r7, #$14						;
	mov   r6, #$00						;Delay for 20 milliseconds.
	lcall DoDelay						;

	inc   R34							;Get next data byte to display from external memory.
	mov   a, R34						;
	jnz   BGCheckEndLoop				;If data byte 0? If not, branch to display.-->
	inc   R33							;Else at the end of animation.
	BGCheckEndLoop:
	clr   c								;
	subb  a, #$09						;
	mov   a, R33						;
	subb  a, #$00						;Have all 9 animation frames been done?-->
	jc    BargraphAnimLoop				;If not, branch to do next frame.
	inc   R36							;At least 1 animation sequence has been done. Increment R36-->
	mov   a, R36						;to indicate this.
	clr   c								;
	subb  a, #$02						;Has the bargraph data been displayed 2 times in a row?-->
	jc    BargraphAnimBegin				;If not, branch to display again.

	clr   a								;
	mov   R33, a						;
	mov   R34, a						;Zero out the counter registers.
	mov   R36, a						;

	nop									;
	nop									;Sync delay.
	nop									;

	mov   dptr, EP8CFG					;Configure endpoint 8 as bulk IN endpoint,-->
	mov   a, #$e0						;512 bytes, double buffered-fixed.
	movx  @dptr, a						;

	nop									;
	nop									;Sync delay.
	nop									;

	mov   dptr, EP6CFG					;Configure endpoint 6 as bulk OUT endpoint,-->
	mov   a, #$a2						;512 bytes, double buffered.
	movx  @dptr, a						;

	nop									;
	nop									;Sync delay.
	nop									;

	mov   dptr, EP6BCL					;
	mov   a, #$80						;Set skip bit so any data in buffer will be ignored.
	movx  @dptr, a						;

	nop									;
	nop									;Sync delay.
	nop									;

	movx  @dptr, a						;Set skip bit again to ignore any double bufferd data.

	nop									;
	nop									;Sync delay.
	nop									;

	orl   AUTOPTR_SETUP, #$01			;Enable auto pointers and disable auto incrementing.
	mov   dptr, EP1INCFG				;
	mov   a, #$b0						;Enable endpoint 1 as an interrupt endpoint.
	movx  @dptr, a						;

	mov   LAST_SW_STATE, IOA			;
	mov   dptr, EP1INBUF				;
	mov   a, LAST_SW_STATE				;
	movx  @dptr, a						;Get initial state of switches and send it to USB host.
	mov   dptr, EP1INBC					;
	mov   a, #$01						;
	movx  @dptr, a						;

	lcall DisableEP2					;Disable endpoint 2.

	mov   dptr, USBCS					;Get USB speed.
	movx  a, @dptr						;
	jnb   ACC_7, DisplayF				;Is USB in High speed mode?  If not, branch.
	mov   IOB, #$89						;Display "H" on 7 seg for High speed.
	sjmp  FinishStartupRoutine			;
	DisplayF:
	mov   IOB, #$8e						;Display "F" on 7 seg for Full speed.

	FinishStartupRoutine:
	setb  REMOTE_WAKEUP					;Enable remote wakeup.
	ret									;

;This function sets the bargraph bits one at a time and loops until all 8 bits are accounted for. 
;Since the bargraph is active low, the bits need to be complimented in order to display properly.
;The following describes what the various registers are used for in this function:
;r7-Current bitmask. $01, $02, $04, $08, $10, $20, $40 or $80.
;r6-Upper word when moving the bitmask to the proper position. Not used in this function 
;   and reset every time the function loops.
;r5-Storage of the original data received from the USB to be loaded into the bargraph.
;r4-Next bit to work on. Ranges from 0 to 8. When r4 = 8, the load process is completed.
;r3-Temporary storage of the data to be loaded into the bargraph.
;r0-Various proccessing requirements.
SetBargraph:
	clr   a								;
	mov   UPPER_MS_DELAY, a				;
	mov   dptr, EP0BCH					;Clear end point 0 byte count.
	movx  @dptr, a						;
	mov   dptr, EP0BCL					;
	movx  @dptr, a						;

	WaitForBusyClear:
	mov   dptr, EP0CS					;
	movx  a, @dptr						;Wait for setup token to arrive.
	jb    ACC_1, WaitForBusyClear		;

	mov   dptr, EP0BUF					;Get bargraph data from end point 0.
	movx  a, @dptr						;

	mov   r5, a							;Store bargraph data in r5.
	clr   a								;
	mov   r4, a							;Initialize r4 for bargraph load loop.

BargraphLoadLoop:
	mov   a, #$01						;Load a with a bit. Bit will be shifted to proper bitmask location.
	mov   r6, #$00						;Clear r6 for upper word of bitmask.  Not used in this function.
	mov   r0, R4BNK0					;Get last bit to be completed from r4.
	inc   r0							;Increment bit count to begin work on next bit.
	sjmp  SetupBargraphBitmask			;

	MakeBargraphBitmask:
	clr   c								;
	rlc   a								;Shift a until the proper bitmask is created. r6-->
	xch   a, r6							;contains the upper word of the bitmask.
	rlc   a								;
	xch   a, r6							;

	SetupBargraphBitmask:
	djnz  r0, MakeBargraphBitmask		;Loop until bitmask bit is in proper location.
	mov   r7, a							;Store a copy of the bitmask just created.
	mov   a, r5							;Get a copy of the bargraph data.
	mov   r3, a							;Store temp copy of bargraph data.
	clr   a								;
	mov   a, r7							;Use bitmask just created to determine if current-->
	anl   a, r3							;bit needs to be set or cleared.
	jz    PrepBargraphSetBit			;Does bargraph bit need to be set? If so, branch.

	PrepBargraphClearBit:
	mov   a, #$01						;Put initial bit to be shifted into a.
	mov   r0, R4BNK0					;Get bit number + 1.
	inc   r0							;
	sjmp  ClearBargraphBit				;Jump to clear bit.

	MakeBargraphClrBitmask:
	clr   c								;Rotate bit in a to the proper bitmask position.
	rlc   a								;

	ClearBargraphBit:
	djnz  r0, MakeBargraphClrBitmask	;Loop to move bit to proper bitmask location.
	cpl   a								;Invert bitmask so only desired bit is cleared.
	anl   IOD, a						;Clear bargraph bit.
	sjmp  CheckBargraphLoop				;Check to see if further bits need to be processed.

	PrepBargraphSetBit:
	mov   a, #$01						;Put initial bit to be shifted into a.
	mov   r0, R4BNK0					;Get bit number + 1.
	inc   r0							;
	sjmp  SetBargraphBit				;Jump to set bit.

	MakeBargraphSetBitmask:
	clr   c								;
	rlc   a								;Rotate bit in a to the proper bitmask position.

	SetBargraphBit:
	djnz  r0, MakeBargraphSetBitmask	;Loop to move bit to proper bitmask location.
	orl   IOD, a						;Set bargraph bit.
	
	CheckBargraphLoop:
	inc   r4							;Indicate this bit has been loaded.
	cjne  r4, #$08, BargraphLoadLoop	;More bits need to be loaded? If so, loop again.

	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;Clear end point 0 byte count.
	mov   dptr, EP0BCL					;
	movx  @dptr, a						;
	clr   c								;Clear carry and return.
	ret									;

Set7Seg:
	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;Clear endpoint 0 byte count.
	mov   dptr, EP0BCL					;
	movx  @dptr, a						;

	EP0BusyLoop:
	mov   dptr, EP0CS					;
	movx  a, @dptr						;Check endpoint 0 busy flag. Loop unil endpoint 0 is not busy.
	jb    ACC_1, EP0BusyLoop			;

	mov   dptr, EP0BUF					;Get 7 segment display byte from endpoint 0 buffer.
	movx  a, @dptr						;
	cpl   a								;
	mov   IOB, a						;Invert and present byte on 7 segment display.

	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;
	mov   dptr, EP0BCL					;Clear endpoint 0 byte count...again.
	movx  @dptr, a						;
	clr   c								;
	ret									;

UnusedInt00: reti

;-----------------------------------USB Descriptors-----------------------------------;

DeviceDescriptor:
L0600:	.db $12, $01, $02, $00, $00, $00, $00, $40, $47, $05, $02, $10, $00, $00, $01, $02
		.db $00, $01 

DeviceQualifier:
L0612:	.db $0a, $06, $02, $00, $00, $00, $00, $40, $01, $00

ConfigDescriptorHS:
L061C:	.db $09, $02, $27, $00, $01, $01, $04, $a0, $32 

DefaultInterfaceHS:
L0625:	.db $09, $04, $00, $00, $03, $ff, $00, $00, $00

EP1DescriptorHS:
L062E:	.db $07, $05, $81, $03, $40, $00, $01

EP6DescriptorHS:
L0635:	.db $07, $05, $06, $02, $00, $02, $00

EP8DescriptorHS:
L063C:	.db $07, $05, $88, $02, $00, $02, $00

.org	$0644							;Size: $27. The .org performs word alignment.

ConfigDescriptorFS:
L0644:	.db $09, $02, $27, $00, $01, $01, $03, $a0, $32

DefaultInterfaceFS:
L064D:	.db $09, $04, $00, $00, $03, $ff, $00, $00, $00

EP1DescriptorFS:
L0656:	.db $07, $05, $81, $03, $40, $00, $01

EP6DescriptorFS:
L065D:	.db $07, $05, $06, $02, $40, $00, $00

EP8DescriptorFS:
L0664:	.db $07, $05, $88, $02, $40, $00, $00

;----------------------------------String Descriptors---------------------------------;

.org	$066c							;Size: $60b. The .org performs word alignment.

;String descriptor 0.
L066c:	.db $04							;Descriptor size-4 bytes.
		.db $03							;Descriptor type-String descriptor.
		.db $09, $04					;English-United States ($0409).

;String descriptor 1.
;Descriptor size and type.
L0670:	.db $10, $03
		;      O         S         R         .         C         O         M
		.db $4f, $00, $53, $00, $52, $00, $2e, $00, $43, $00, $4f, $00, $4d, $00

;String descriptor 2.
;Descriptor size and type.
L067f:	.db $1e, $03
		;      O         S         R        space      U         S         B         -
		.db $4f, $00, $53, $00, $52, $00, $20, $00, $55, $00, $53, $00, $42, $00, $2d, $00
		;      F         X         2        space      L         K
		.db $46, $00, $58, $00, $32, $00, $20, $00, $4c, $00, $4b, $00

;String descriptor 3.
;Descriptor size and type.
L069e:	.db $14, $03
		;      F         u         l         l         S         p         e         e
		.db $46, $00, $75, $00, $6c, $00, $6c, $00, $53, $00, $70, $00, $65, $00, $65, $00
		;      d
		.db $64, $00

;String descriptor 4.
;Descriptor size and type.
L06b2:	.db $14, $03
		;      H         i         g         h         S         p         e         e
		.db $48, $00, $69, $00, $67, $00, $68, $00, $53, $00, $70, $00, $65, $00, $65, $00
		;      d
		.db $64, $00

;End string descriptors.
		.db $00, $00

;-------------------------------------------------------------------------------------;

CheckBulkData:
	mov   a, EP2468STAT					;Is data waiting in end point 6?-->
	jnb   ACC_4, CheckEP8Data			;If so, branch to check end point 8.
	ajmp  CheckSwitches					;No data waiting. Branch to exit.

	CheckEP8Data:
	mov   a, EP2468STAT					;Is end point 8 full? If not,-->
	jnb   ACC_7, MoveBulkData			;branch to put data in it.
	ajmp  CheckSwitches					;End point 8 full.  Branch to exit.

	MoveBulkData:
	mov   r2, #$f8						;Load registers with the start of end point 6 buffer.
	mov   r1, #$00						;Not used.  The registers are either overwritten-->
	mov   r6, #$f8						;or never accessed.
	mov   r7, #$00						;

	mov   a, #$f8						;
	mov   AUTOPTRH1, a					;Point auto pointer 1 to beginning of-->
	mov   a, #$00						;end point 6 buffers.
	mov   AUTOPTRL1, a					;

	mov   r2, #$fc						;Load registers with the start of end point 8 buffer.
	mov   r1, #$00						;Not used.  The registers are either overwritten-->
	mov   r6, #$fc						;or never accessed.
	mov   r7, #$00						;

	mov   a, #$fc						;
	mov   AUTOPTRH2, a					;Point auto pointer 2 to beginning of-->
	mov   a, #$00						;end point 8 buffers.
	mov   AUTOPTRL2, a					;

	mov   dptr, EP6BCH					;
	movx  a, @dptr						;
	mov   r6, a							;
	mov   dptr, EP6BCL					;Save end point 6 byte count in r6 and r7.
	movx  a, @dptr						;r7 is lower byte, r6 is upper byte.
	mov   r4, #$00						;
	add   a, #$00						;
	mov   r7, a							;

	mov   a, r4							;
	addc  a, r6							;Add offsets to r6 and r7.  The offsets are always 0.
	mov   r6, a							;
	clr   a								;
	mov   r5, a							;r5 and r4 are loaded with #$00.

	MoveBytesLoop:
	clr   c								;r6, r7 contains byte count of bytes to send.
	mov   a, r5							;r4, r5 contains byte count of bytes sent.
	subb  a, r7							;
	mov   a, r4							;When r6, r7 = r4, r5 all bytes sent.
	subb  a, r6							;
	jnc   SetBulkBitCount				;If no carry is set, byte transfer is complete.

	mov   dptr, XAUTODAT1				;
	movx  a, @dptr						;Get a byte from end point 6 and move it into-->
	mov   dptr, XAUTODAT2				;end point 8. Pointers will auto increment.
	movx  @dptr, a						;

	inc   r5							;Increment bytes transferred count.
	cjne  r5, #$00, DoNextByteLoop		;Check if carry bit needs to be added to upper byte.
	inc   r4							;Carry bit from lower byte into upper byte.

	DoNextByteLoop:
	sjmp  MoveBytesLoop					;Loop to move next byte.

	SetBulkBitCount:
	mov   dptr, EP6BCH					;
	movx  a, @dptr						;Move upper byte count of end point 6-->
	mov   dptr, EP8BCH					;into upper byte count of end point 8.
	movx  @dptr, a						;

	nop									;
	nop									;Sync delay.
	nop									;

	mov   dptr, EP6BCL					;
	movx  a, @dptr						;Move lower byte count of end point 6-->
	mov   dptr, EP8BCL					;into lower byte count of end point 8.
	movx  @dptr, a						;

	nop									;
	nop									;Sync delay.
	nop									;

	mov   dptr, EP6BCL					;Setting MSB of EP6BCL keeps the data from going to-->
	mov   a, #$80						;the external FIFOs as per page 93 of the EZ-USB TRM.
	movx  @dptr, a						;

	;R33, R34 keep track of how many bulk transfers have occured.
	inc   R34							;
	mov   a, R34						;Increment bulk transfer counter.
	jnz   CheckBulkLED					;
	inc   R33							;

	CheckBulkLED:
	anl   a, #$0f						;Every 16 Bulk transfers turn on another bargraph LED.
	jnz   CheckSwitches					;Jump if not time to turn on another bargraph LED.

	;R36 is an offset into the bargraph data stored in memory locations $1000 thru $1008.
	inc   R36							;Increment offset by 1.
	mov   a, R36						;
	setb  c								;
	subb  a, #$08						;Check if time to reset offset.
	jc    DisplayBulkLED				;Time to reset offset? If not, branch.

	clr   a								;Reset offset.
	mov   R36, a						;

	DisplayBulkLED:
	mov   a, #$00						;Base address of bargraph data is $1000.
	add   a, R36						;
	mov   DPL0, a						;Set lower byte of bargraph data with offset.
	clr   a								;
	addc  a, #$10						;
	mov   DPH0, a						;Set upper byte of bargraph data pointer.

	movx  a, @dptr						;Get bargraph data.
	cpl   a								;
	mov   IOD, a						;Display bulk transfer data on bargraph.

CheckSwitches:
	mov   dptr, EP1INCS					;Get status of end point 1 IN.
	movx  a, @dptr						;
	jb    ACC_1, EndCheckSwitches		;If end point is busy, branch to exit.

	mov   R0BNK3, IOA					;Get current state of the switches.
	mov   a, R0BNK3						;
	xrl   a, LAST_SW_STATE				;Has the switches state changed since-->
	jz    EndCheckSwitches				;last check? If not, branch to exit.

	mov   dptr, EP1INBUF				;
	mov   a, R0BNK3						;Put switch byte in end point 1 data buffer.
	movx  @dptr, a						;

	mov   dptr, EP1INBC					;
	mov   a, #$01						;Set end point 1 byte count to 1 byte.
	movx  @dptr, a						;

	mov   LAST_SW_STATE, R0BNK3			;Save this switches state as last switches state.

	EndCheckSwitches:
	ret									;Return from checking bulk data and switches.

DoDelay:
	mov   UPPER_MS_DELAY, r6			;Load upper and lower millisecond delay bytes.
	mov   LOWER_MS_DELAY, r7			;

	Check12MHz:
	mov   dptr, CPUCS					;Get CPU status.
	movx  a, @dptr						;
	anl   a, #$18						;Discard all bits except CPU clock bits.
	jnz   Check48MHz					;Is processor running at 12 MHz? If not, branch.

	Adjust12MHz:
	mov   a, LOWER_MS_DELAY				;
	add   a, #$01						;
	mov   r7, a							;Get carry bit from lower byte if lower byte = $ff.
	clr   a								;
	addc  a, UPPER_MS_DELAY				;
	clr   c								;
	rrc   a								;Divide upper millisecond byte by 2 and save.
	mov   UPPER_MS_DELAY, a				;
	mov   a, r7							;
	rrc   a								;Divide lower millisecond byte by 2 and save.
	mov   LOWER_MS_DELAY, a				;
	sjmp  TimeDelayLoop					;Branch to begin delay loop.

	Check48MHz:
	mov   dptr, CPUCS					;Get CPU status.
	movx  a, @dptr						;
	anl   a, #$18						;Discard all bits except CPU clock bits.
	mov   r7, a							;
	cjne  r7, #$10, TimeDelayLoop		;Is processor running at 48 MHz? If not, branch.

	Adjust48MHz:
	mov   a, LOWER_MS_DELAY				;
	add   a, ACC						;Multiply lower millisecond delay byte by-->
	mov   LOWER_MS_DELAY, a				;2 and save (a + a = 2a).
	mov   a, UPPER_MS_DELAY				;
	rlc   a								;Multiply upper millisecond delay byte by 2 including-->
	mov   UPPER_MS_DELAY, a				;carry from lower byte and save.

	TimeDelayLoop:
	mov   a, LOWER_MS_DELAY				;Save pre-decremented value.
	dec   LOWER_MS_DELAY				;Decrement lower millisecond byte.
	mov   r6, UPPER_MS_DELAY			;Save pre-decremented value.
	jnz   CheckEndDelay					;Does upper byte need to be decremented? If not, branch.
	dec   UPPER_MS_DELAY				;Decrement upper millisecond byte.

	CheckEndDelay:
	orl   a, r6							;Check to see if upper and lower bytes are both zero.
	jz    DelayExit						;Jump to exit if time delay complete.
	lcall MsWait						;Call the function that burns some time.
	sjmp  TimeDelayLoop					;Loop again.
	
	DelayExit:
	ret									;Delay done. Exit function.

USBRESET_ISR:
	push  ACC							;
	push  DPH0							;Store data pointer and accumulator.
	push  DPL0							;

	mov   THIS_CFG_HI, CFG_DESC_FS_HI	;Load current config descriptor with full speed descriptor pointer.
	mov   THIS_CFG_LO, CFG_DESC_FS_LO	;
	mov   DPL0, THIS_CFG_LO				;
	mov   DPH0, THIS_CFG_HI				;Point to bDescriptorType byte.
	inc   dptr							;
	mov   a, #$02						;Ensure descriptor type is set for configuration.
	movx  @dptr, a						;

	mov   OS_CFG_HI, CFG_DESC_HS_HI		;Load other speed config descriptor with high speed descriptor pointer.
	mov   OS_CFG_LO, CFG_DESC_HS_LO		;
	mov   DPL0, OS_CFG_LO				;
	mov   DPH0, OS_CFG_HI				;Point to bDescriptorType byte.
	inc   dptr							;
	mov   a, #$07						;Ensure descriptor type is set for other speed configuration.
	movx  @dptr, a						;

	anl   EXIF, #$ef					;Clear USB interrupt flag.
	mov   dptr, USBIRQ					;
	mov   a, #$10						;Clear interrupt request.
	movx  @dptr, a						;

	pop   DPL0							;
	pop   DPH0							;Restore data pointer and accumulator.
	pop   ACC							;

	reti								;Return from interrupt.

SetCarry03:
	setb  c								;Set carry bit.
	ret									;Return.

;The following vector table is for the USB and FIFO/GPIF interrupts and is accessed by 
;the USB autovector functionality of the EZ-USB microprocessor.

;USB interrupts.
USB_AND_IE4:
	ljmp SUDAV_ISR
	nop
	ljmp SOF_ISR
	nop
	ljmp SUTOK_ISR
	nop
	ljmp SUSPEND_ISR
	nop
	ljmp USBRESET_ISR
	nop
	ljmp HISPEED_ISR
	nop
	ljmp UnusedInt00					;EP0ACK_ISR
	nop
	ljmp UnusedInt0						;SPARE_ISR
	nop
	ljmp UnusedInt1						;EP0IN _ISR
	nop
	ljmp UnusedInt2						;EP0OUT_ISR
	nop
	ljmp UnusedInt3						;EP1IN _ISR
	nop
	ljmp UnusedInt4						;EP1OUT_ISR
	nop
	ljmp UnusedInt5						;EP2_ISR
	nop
	ljmp UnusedInt6						;EP4_ISR
	nop
	ljmp UnusedInt7						;EP6_ISR
	nop
	ljmp UnusedInt8						;EP8_ISR
	nop
	ljmp UnusedInt9						;IBN_ISR
	nop
	ljmp UnusedInt0						;SPARE_ISR
	nop
	ljmp UnusedIntA						;EP0PING_ISR
	nop
	ljmp UnusedIntB						;EP1PING_ISR
	nop
	ljmp UnusedIntC						;EP2PING_ISR
	nop
	ljmp UnusedIntD						;EP4PING_ISR
	nop
	ljmp UnusedIntE						;EP6PING_ISR
	nop
	ljmp UnusedIntF						;EP8PING_ISR
	nop
	ljmp UnusedInt10					;ERRLIMIT_ISR
	nop
	ljmp UnusedInt0						;SPARE_ISR
	nop
	ljmp UnusedInt0						;SPARE_ISR
	nop
	ljmp UnusedInt0						;SPARE_ISR
	nop
	ljmp UnusedInt11					;EP2ISOERR_ISR
	nop
	ljmp UnusedInt12					;EP4ISOERR_ISR
	nop
	ljmp UnusedInt13					;EP6ISOERR_ISR
	nop
	ljmp UnusedInt14					;EP8ISOERR_ISR
	nop

;FIFO/GPIF interrupts.
	ljmp UnusedInt15					;EP2PF_ISR
	nop
	ljmp UnusedInt16					;EP4PF_ISR
	nop
	ljmp UnusedInt17					;EP6PF_ISR
	nop
	ljmp UnusedInt18					;EP8PF_ISR
	nop
	ljmp UnusedInt19					;EP2EF_ISR
	nop
	ljmp UnusedInt1A					;EP4EF_ISR
	nop
	ljmp UnusedInt1B					;EP6EF_ISR
	nop
	ljmp UnusedInt1C					;EP8EF_ISR
	nop
	ljmp UnusedInt1D					;EP2FF_ISR
	nop
	ljmp UnusedInt1E					;EP4FF_ISR
	nop
	ljmp UnusedInt1F					;EP6FF_ISR
	nop
	ljmp UnusedInt20					;EP8FF_ISR
	nop
	ljmp UnusedInt21					;GPIFDONE_ISR
	nop
	ljmp UnusedInt22					;GPIFWF_ISR
	nop

RESET:
	mov r0, #$7F						;Prepare to clear lower 128.
	clr a								;

	clr_loop:
	mov @r0, a							;Loop to clear all lower 128 registers.
	djnz r0, clr_loop					;

	mov SP, #$38						;point stack pointer to $38.
	ljmp WriteMemoryBlocks				;Write blocks of data to memory.

MemoryWriteExit:
	ljmp InitBoard						;Initialize OSR FX2 board.

;This function writes data to the lower 128 bytes of memory.
;I uses indirect access to perform the writes.
;The registers have the following functions:
;r7 = Count used to determine how many bytes left to write to memory
;r0 = Pointer to next address to be written to.
WriteToLower128:
	clr a								;
	movc a, @a+dptr						;Get starting address for write loop from table.
	inc dptr							;
	mov r0, a							;r0 contains address of next indirect write.

	Write128Loop:
	clr a								;
	movc a, @a+dptr						;Get data byte to write from table.
	inc dptr							;
	jc External128Move					;If carry set, do an external move of data.
	mov @r0, a							;Write data to memory.
	sjmp Next128Byte					;Jump to L08D4.
	External128Move:
	movx @r0, a							;Write data to memory.

	Next128Byte:
	inc r0								;Increment to next memory location to write.
	djnz r7, Write128Loop				;More data to write? If so, branch.
	sjmp GetMemoryBlock					;Done writing memory block. Get next block.

WriteToBit:
	clr a								;
	movc a, @a+dptr						;Get data byte from table.
	inc dptr							;
	mov r0, a							;Store data byte in r0.
	anl a, #$07							;Keep lower 3 bits of a. Used to get bitmask.
	add a, #$0c							;Add $0c to a to get offset into BitMaskTable.
	xch a, r0							;Store offset data and get original byte for futher processing.
	clr c								;
	rlc a								;
	swap a								;
	anl a, #$0F							;Setup memory address to have bit swapped.
	orl a, #$20							;
	xch a, r0							;Swap a with r0.
	movc a, @a+pc						;
	jc SetBit							;If carry set, jump to L08F1.

	ClearBit:
	cpl a								;Invert a and logical and it with memory to clear bit.
	anl a, @r0							;
	sjmp WriteBit						;Jump to store new value in memory.

	SetBit:
	orl a, @r0							;Or bitmask with data byte.

	WriteBit:
	mov @r0, a							;Store new value in memory.
	djnz r7, WriteToBit					;More data to write? If so, branch.
	sjmp GetMemoryBlock					;Done writing memory block. Get next block.

BitMaskTable:
	.db $01, $02, $04, $08, $10, $20, $40, $80

WriteMemoryBlocks:
	mov dptr, MemoryBlocksTable 		;Point data pointer to start of table.

	GetMemoryBlock:
	clr a								;
	mov r6, #$01						;Load r6 with #$01. Minimum 1 byte to read.
	movc a, @a+dptr						;Get first byte in memory block.
	jz MemoryWriteExit					;Exit if at end of memory blocks.

	inc dptr							;Move to next byte in table.
	mov r7, a							;Store configuration byte from memory block.
	anl a, #$3F							;Ignore upper two bits of gonfig byte for now.
	jnb ACC_5, DetermineWriteType		;Two bytes required for loop counter? If not, branch.

	anl a, #$1F							;Keep lower 5 bits and use them as the upper loop counter byte.
	mov r6, a							;
	clr a								;
	movc a, @a+dptr						;Get next data byte and use it as the lower loop counter byte.
	inc dptr							;
	jz DetermineWriteType				;If lower loop counter byte is $00, upper byte needs to be incremented.
	inc r6								;Increment upper counter byte for proper count.

	DetermineWriteType:
	xch a, r7							;Swap a and r7.
	anl a, #$C0							;Save only upper two bits.
	add a, ACC							;Multiply a by 2.
	jz WriteToLower128					;Upper 2 bits of config byte are 10 or 00. Data goes in lower 128.
	jc WriteToBit						;Upper 2 bits of config byte are 11. Data is bit data.
										;Else upper 2 bits of config byte are 01. Data goes in external memory.

;This function writes data bytes to external memory using the movx instruction.
;The registers have the following functions:
;r7 = Lower byte count used to determine how many bytes left to write to memory.
;r6 = Upper byte count used to determine how many bytes left to write to memory.
;r2 = Stores upper byte of data pointer that the byte will be written to.
;r0 = Stores lower byte of data pointer that the byte will be written to.
WriteToExtMem:
	clr   a								;
	movc  a, @a+dptr					;Get upper byte for external memory write.
	inc   dptr							;
	mov   r2, a							;Save upper memory byte temporarily in r2.
	clr   a								;
	movc  a, @a+dptr					;Get lower byte for external memory write.
	inc   dptr							;
	mov   r0, a							;Save lower memory byte temporarily in r0.

	ExtWriteLoop:
	clr   a								;
	movc  a, @a+dptr					;Get byte to write to external memory.
	inc   dptr							;
	xch   a, r0							;
	xch   a, DPL0						;Setup lower byte in data pointer.
	xch   a, r0							;
	xch   a, r2							;
	xch   a, DPH0						;Setup upper byte in data pointer.
	xch   a, r2							;
	movx  @dptr, a						;Write byte to external memory.
	inc   dptr							;Move to next address for any sequential write.
	xch   a, r0							;
	xch   a, DPL0						;Restore original lower byte in data pointer.
	xch   a, r0							;
	xch   a, r2							;
	xch   a, DPH0						;Restore original upper byte in data pointer.
	xch   a, r2							;
	djnz  r7, ExtWriteLoop				;Decrement lower byte in write counter, branch if more to write.
	djnz  r6, ExtWriteLoop				;Decrement upper byte in write counter, branch if more to write.
	sjmp  GetMemoryBlock				;Branch to see if there is another block of data to write.

HandleOtherRequests:
	mov   dptr, SETUPDAT_1				;Get vendor command from bmRequest byte.
	movx  a, @dptr						;Convert vendor command to aJumpTable index.-->
	add   a, #$30						;Valid values are #$D0 thru #$DC.
	cjne  a, #$0d, CheckValidJump		;Set carry if valid entry in table below.
	CheckValidJump:
	jnc   SetCarry						;Is valid entry into table? If not, branch.
	mov   dptr, AjumpTable				;Point data pointer to the beginning of AjumpTable below.
	add   a, ACC						;a = 2 * a. 2 bytes per entry in table below.
	jmp   @a+dptr						;Indirect jump to routine in table below.

AjumpTable:
	ajmp NAKTransfers					;
	ajmp ACKTransfers					;
	ajmp SetCarry						;
	ajmp SetCarry						;
	ajmp DoGet7Seg						;The following is a table of indirect jumps to-->
	ajmp SetCarry						;the individual vendor commands.  The first-->
	ajmp DoGetSwitches					;entry in the table is vendor command $d0. Some-->
	ajmp DoGetBargraph					;of the commands are not implemented.
	ajmp DoSetBargraph					;
	ajmp DoIsHighSpeed					;
	ajmp Reinit							;
	ajmp DoSet7Seg						;
	ajmp DoGetConstants					;

NAKTransfers:
	mov   dptr, FIFORESET				;
	movx  a, @dptr						;Get FIFORESET register.
	mov   R0BNK3, a						;
	orl   R0BNK3, #$80					;Set NAKALL bit.

	nop									;
	nop									;Sync delay.
	nop									;

	mov   a, R0BNK3						;
	movx  @dptr, a						;Update FIFORESET register.
	sjmp  ClearCarry					;

ACKTransfers:
	mov   dptr, FIFORESET				;
	movx  a, @dptr						;Get FIFORESET register.
	mov   R0BNK3, a						;
	anl   R0BNK3, #$7f					;Clear NAKALL bit.

	nop									;
	nop									;Sync delay.
	nop									;

	mov   a, R0BNK3						;
	movx  @dptr, a						;Update FIFORESET register.
	sjmp  ClearCarry					;

DoGet7Seg:
	ljmp  Get7Seg						;Get 7 segment display status.

DoGetSwitches:
	ljmp  GetSwitches					;Get switches status.

DoGetBargraph:
	ljmp  GetBargraph					;Get bargraph status.

DoSetBargraph:
	ljmp  SetBargraph					;Set bargraph LEDs.

DoIsHighSpeed:
	ljmp  IsHighSpeed					;Get high-speed status of the device.

DoSet7Seg:
	ljmp  Set7Seg						;Set 7 segment display.

DoGetConstants:
	ljmp  GetConstants					;Send #$11, #$11 back to host. Unused vendor command?

Reinit:
	mov   dptr, EP0BUF					;Send #$07 to host.
	mov   a, #$07						;
	movx  @dptr, a						;
	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;Set enpoint 0 buffer byte count to 1.
	mov   dptr, EP0BCL					;
	inc   a								;
	movx  @dptr, a						;
	mov   dptr, EP0CS					;
	movx  a, @dptr						;ACK the control transfer.
	orl   a, #$80						;
	movx  @dptr, a						;

	mov   r7, #$e8						;
	mov   r6, #$03						;Delay for 1000 milliseconds.
	lcall DoDelay						;

	setb  RENUM_DISABLE					;
	lcall InitializeUSB					;Reninitialize the USB device.
	sjmp  ClearCarry					;

SetCarry:
	setb  c								;Set carry bit.
	ret									;

ClearCarry:
	clr   c								;Clear carry bit.
	ret									;

;This function works in a similar way as the SetBargraph function.  It extracts
;bits from the bargraph data 1 bit at a time and saves it into r5.
GetBargraph:
	mov   r1, IOD						;Get bargraph data off of Port D.

	clr   a								;
	mov   r5, a							;Initialize registers.
	mov   r4, a							;

GetBargraphLoop:
	mov   a, #$01						;Put initial bit to be shifted into a.
	mov   r6, #$00						;Clear upper byte of bitmask.
	mov   r0, R4BNK0					;Get last bit to be completed from r4.
	inc   r0							;Increment bit count to begin work on next bit.
	sjmp  _SetupBargraphBitmask			;

	_MakeBargraphBitmask:
	clr   c								;
	rlc   a								;Shift a until the proper bitmask is created. r6-->
	xch   a, r6							;contains the upper word of the bitmask.
	rlc   a								;
	xch   a, r6							;

	_SetupBargraphBitmask:
	djnz  r0, _MakeBargraphBitmask		;Loop until bitmask bit is in proper location.
	mov   r7, a							;Store a copy of the bitmask just created.
	mov   a, r1							;Get a copy of the bargraph data.
	mov   r3, a							;Store a temporary copy of the bargraph data.
	clr   a								;
	mov   a, r7							;Use bitmask just created to determine if current-->
	anl   a, r3							;bit needs to be added to final bargraph data byte.
	jnz   _CheckBargraphLoop			;Is bit=0? if so, add bit to bargraph data byte(inverts data).

	inc   a								;Set a to #$01.
	mov   r0, R4BNK0					;
	inc   r0							;Get bit number + 1.
	sjmp  GetBargraphBit				;Jump to set bit.

	MoveBargraphBit:
	clr   c								;Rotate bit in a to the proper bitmask position.
	rlc   a								;

	GetBargraphBit:
	djnz  r0, MoveBargraphBit			;Loop to move bit to proper bitmask location.
	orl   R5BNK0, a						;Add bit to final bargraph data byte.

	_CheckBargraphLoop:
	inc   r4							;Indicate this bit has been loaded.
	cjne  r4, #$08, GetBargraphLoop		;More bits need to be loaded? If so, loop again.

	mov   dptr, EP0BUF					;
	mov   a, r5							;Load bargraph data into the end point 0 buffer.
	movx  @dptr, a						;

	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;Set end point 0 byte count to 1 byte.
	mov   dptr, EP0BCL					;
	inc   a								;
	movx  @dptr, a						;

	clr   c								;Clear carry and exit.
	ret									;

HISPEED_ISR:
	push  ACC							;
	push  DPH0							;Store data pointer and accumulator.
	push  DPL0							;

	mov   dptr, USBCS					;Is device already running in high speed mode?-->
	movx  a, @dptr						;If yes, branch. Nothing more to do.
	jnb   ACC_7, HS_ISR_Exit			;

	mov   THIS_CFG_HI, CFG_DESC_HS_HI	;Load current config descriptor with high speed descriptor pointer.
	mov   THIS_CFG_LO, CFG_DESC_HS_LO	;	
	mov   DPL0, THIS_CFG_LO				;
	mov   DPH0, THIS_CFG_HI				;Point to bDescriptorType byte.
	inc   dptr							;
	mov   a, #$02						;Ensure descriptor type is set for configuration.
	movx  @dptr, a						;

	mov   OS_CFG_HI, CFG_DESC_FS_HI		;Load other speed config descriptor with high speed descriptor pointer.
	mov   OS_CFG_LO, CFG_DESC_FS_LO		;
	mov   DPL0, OS_CFG_LO				;
	mov   DPH0, OS_CFG_HI				;Point to bDescriptorType byte.
	inc   dptr							;
	mov   a, #$07						;Ensure descriptor type is set for other speed configuration.
	movx  @dptr, a						;

	HS_ISR_Exit:
	anl   EXIF, #$ef					;Clear USB interrupt flag.
	mov   dptr, USBIRQ					;
	mov   a, #$20						;Clear interrupt request.
	movx  @dptr, a						;
	
	pop   DPL0							;
	pop   DPH0							;Retore data pointer and accumulator.
	pop   ACC							;
	reti								;

MemoryBlocksTable:						;$0a4a
	.db $02								;2 bytes of data to write to lower 128.
	.db $31								;Starting address is $31.
	.db $11, $11						;Write #$11 to address $31 and $32.

	.db $01								;1 byte of data to write to lower 128.
	.db $35								;Starting address is $35.
	.db $03								;Write #$03 to address $35.

	.db $01								;1 byte of data to write to lower 128.
	.db $37								;Starting address is $37.
	.db $05								;Write #$05 to address $37.

	.db $4A								;External memory write. 10 data bytes to write.
	.db $10, $12						;Starting address is $1012.
	;The following data is written to addresses 1012 through $101B. These 10 bytes represent
	;numbers 0 through 9 converted to be displayed on the 7 segment display.
	.db $D7, $06, $B3, $A7, $66, $E5, $F4, $07, $F7, $67

	.db $49								;External memory write. 9 data bytes to write.
	.db $10, $00						;Starting address is $1000.
	;The following data is written to addresses $1000 through $1008. These 9 bytes represent
	;height of Leds being turned on on the bargraph.  the first value is no lights while
	;the last value is all lights turned on.
	.db $00, $20, $60, $E0, $E1, $E3, $E7, $ef, $ff

	.db $49								;External memory write. 9 data bytes to write.
	.db $10, $09						;Starting address is $1009.
	.db $00, $20, $40, $80, $01, $02, $04, $08, $10

	.db $00								;End memory blocks.

InitializeUSB:
	jnb   RENUM_DISABLE, DisconnectUSB	;Always branch.  Set before function is called.
	mov   dptr, USBCS					;
	movx  a, @dptr						;
	orl   a, #$0a						;Disconnect USB and allow firmware to handle all USB requests.
	movx  @dptr, a						;
	sjmp  USBDelay						;Jump to continue USB setup.

	DisconnectUSB:
	mov   dptr, USBCS					;
	movx  a, @dptr						;
	orl   a, #$08						;Disconnect USB
	movx  @dptr, a						;

	USBDelay:
	mov   r7, #$dc						;
	mov   r6, #$05						;Delay for 1500 milliseconds.
	lcall DoDelay						;

	mov   dptr, USBIRQ					;
	mov   a, #$ff						;Clear any USB interrupt flags.
	movx  @dptr, a						;

	mov   dptr, EPIRQ					;Clear any endpoint interrupt requests.
	movx  @dptr, a						;

	anl   EXIF, #$ef					;Clear USB interrupt request.

	mov   dptr, USBCS					;
	movx  a, @dptr						;
	anl   a, #$f7						;Clear SIGRSUME bit in USB control and status register.
	movx  @dptr, a						;
	ret									;

CheckPinWakeup:
	CheckWakeupPin1:
	mov   dptr, WAKEUPCS				;Get wakeup control and status register contents.
	movx  a, @dptr						;
	
	jnb   ACC_0, CheckWakeupPin2		;Is wakeup 1 enabled? If not, branch to check wakeup 2.
	movx  a, @dptr						;Is wakeup 1 signal active? If so, branch to send-->
	jb    ACC_6, SendSigResume			;on the USB bus.

	CheckWakeupPin2:
	mov   dptr, WAKEUPCS				;Get wakeup control and status register contents.
	movx  a, @dptr						;

	jnb   ACC_1, ExitWakeupRoutine		;Is wakeup 2 enabled? If not, branch to exit.
	movx  a, @dptr						;
	jnb   ACC_7, ExitWakeupRoutine		;Is wakeup 2 signal active? If not, branch to exit.

	SendSigResume:
	mov   dptr, USBCS					;Set SIGRSUME bit in USBCS register.-->
	movx  a, @dptr						;This drives the 'K' state onto the-->
	orl   a, #$01						;bus as per page 297 of the EZ-USB TRM.
	movx  @dptr, a						;

	mov   r7, #$14						;
	mov   r6, #$00						;Delay for 20 milliseconds.
	lcall DoDelay						;

	ClearSigResume:
	mov   dptr, USBCS					;Clear SIGRSUME bit in USBCS register.-->
	movx  a, @dptr						;After a delay of 10 to 15 milliseconds,-->
	anl   a, #$fe						;The 'K' state on the bus should be -->
	movx  @dptr, a						;cleared as per page 297 of the EZ-USB TRM.

	ExitWakeupRoutine:
	ret									;Exit wakeup routine.

GetStringDescriptor:
	mov   r1, R7BNK0					;r7 is loaded with string number to get before function is called.
	mov   r6, STRING_DESC_HI			;
	mov   r7, STRING_DESC_LO			;r6, r7 is loaded with $066c. The beginning of the string descriptor.

	GetStringLoop:
	mov   DPL0, r7						;
	mov   DPH0, r6						;Get second byte of string descriptor to ensure it-->
	inc   dptr							;is a string descriptor.
	movx  a, @dptr						;

	xrl   a, #$03						;Is current descriptor a string descriptor?-->
	jnz   ExitStringDescriptor			;If not, branch to exit.

	mov   r5, R1BNK0					;Get number of strings left to traverse.
	dec   r1							;Prepare to move to next string after this loop if needed.
	mov   a, r5							;
	jnz   SetStringPointer				;Is dptr pointing to the desired string?-->
	ret									;If so, exit.

	SetStringPointer:
	mov   DPL0, r7						;Load data pointer with current string pointer.
	mov   DPH0, r6						;
	movx  a, @dptr						;Load a with length of current string descriptor.
	mov   r4, #$00						;
	add   a, r7							;Get lower data byte of next string descriptor pointer.
	mov   r5, a							;
	mov   a, r4							;
	addc  a, r6							;Get upper data byte of next string descriptor pointer.
	mov   r6, a							;
	mov   r7, R5BNK0					;
	sjmp  GetStringLoop					;Loop to check next string descriptor.

	ExitStringDescriptor:
	mov   r6, #$00						;
	mov   r7, #$00						;Clear string pointer info before exiting.
	ret									;

PowerDown:
	mov   dptr, WAKEUPCS				;
	movx  a, @dptr						;Clear wakeup pin indication flags.
	orl   a, #$c0						;
	movx  @dptr, a						;

	mov   dptr, SUSPEND					;Writes to SUSPEND register and forces the-->
	movx  @dptr, a						;chip to go into a suspend state.
	orl   PCON, #$01					;Place chip into low power state.

	nop									;
	nop									;
	nop									;Wait here until wakeup signal received.
	nop									;
	nop									;
	ret									;

;This function takes 1 millisecond to complete when the processor is running at 24 MHz.
MsWait:
	mov   a, #$00						;
	mov   DPS, a						;Select data pointer 0.
	mov   dptr, #$fda5					;Load data pointer initial value.
	mov   r4, #$05						;Kill some time?  Not referenced again.

	CounterLoop:
	inc   dptr							;Increment data pointer.
	mov   a, DPL0						;
	orl   a, DPH0						;Check if high byte and low byte are both 0.-->
	jnz   CounterLoop					;If not, branch to increment counter again.
	ret									;Loop complete.  Exit function.

SUDAV_ISR:
	push  ACC							;
	push  DPH0							;Save data pointer and accumulator.
	push  DPL0							;

	setb  SETUP_DAT_PEND				;Set bit indicating setup data is pending.
	anl   EXIF, #$ef					;Clear USB interrupt flag.
	mov   dptr, USBIRQ					;
	mov   a, #$01						;Clear SUDAV interrupt flag.
	movx  @dptr, a						;

	pop   DPL0							;
	pop   DPH0							;Restore data pointer and accumulator.
	pop   ACC							;
	reti								;

SUSPEND_ISR:
	push  ACC							;
	push  DPH0							;Save data pointer and accumulator.
	push  DPL0							;

	setb  IS_SUSPENDED					;Set bit indicating the device is suspended.
	anl   EXIF, #$ef					;Clear USB interrupt flag.
	mov   dptr, USBIRQ					;
	mov   a, #$08						;Clear SUSP interrupt flag.
	movx  @dptr, a						;

	pop   DPL0							;
	pop   DPH0							;Restore data pointer and accumulator.
	pop   ACC							;
	reti								;

GetConstants:
	mov   a, R32						;
	mov   dptr, EP0BUF					;
	movx  @dptr, a						;Get data bytes in R32 and R31 and-->
	mov   a, R31						;put them in the end point 0 buffer.
	inc   dptr							;
	movx  @dptr, a						;

	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;Set end point 0 byte count to 2 bytes.
	mov   dptr, EP0BCL					;
	mov   a, #$02						;
	movx  @dptr, a						;

	clr   c								;Clear carry and exit.
	ret									;

IsHighSpeed:
	mov   dptr, USBCS					;
	movx  a, @dptr						;Get USB control and status byte.
	anl   a, #$80						;Save only HSM (hish-speed mode) bit.
	mov   dptr, EP0BUF					;Save bit in end point 0 buffer.
	movx  @dptr, a						;

	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;Set end point 0 byte count to 1 byte.
	mov   dptr, EP0BCL					;
	inc   a								;
	movx  @dptr, a						;

	clr   c								;Clear carry and exit.
	ret									;

SUTOK_ISR:
	push  ACC							;
	push  DPH0							;Save data pointer and accumulator.
	push  DPL0							;

	anl   EXIF, #$ef					;Clear USB interrupt flag.
	mov   dptr, USBIRQ					;
	mov   a, #$04						;Clear SUTOK interrupt flag.
	movx  @dptr, a						;

	pop   DPL0							;
	pop   DPH0							;Restore data pointer and accumulator.
	pop   ACC							;
	reti								;

SOF_ISR:
	push  ACC							;
	push  DPH0							;Save data pointer and accumulator.
	push  DPL0							;

	anl   EXIF, #$ef					;Clear USB interrupt flag.
	mov   dptr, USBIRQ					;
	mov   a, #$02						;Clear SOF interrupt flag.
	movx  @dptr, a						;

	pop   DPL0							;
	pop   DPH0							;Restore data pointer and accumulator.
	pop   ACC							;
	reti								;

Get7Seg:
	mov   a, IOB						;
	cpl   a								;Get 7 segment bits and invert them.
	mov   dptr, EP0BUF					;Put the 7 segment byte into the end point 0 buffer.
	movx  @dptr, a						;

	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;Set end point 0 byte count to 1 byte.
	mov   dptr, EP0BCL					;
	inc   a								;
	movx  @dptr, a						;

	clr   c								;Clear carry and exit.
	ret									;

GetConfig:
	mov   dptr, EP0BUF					;
	mov   a, CONFIG_BYTE				;Get config byte and put in enpoint 0 buffer.
	movx  @dptr, a						;

	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;Set end point 0 byte count to 1 byte.
	mov   dptr, EP0BCL					;
	inc   a								;
	movx  @dptr, a						;

	setb  c								;Set carry and exit.
	ret									;

GetInterface:
	mov   dptr, EP0BUF					;
	mov   a, INTERFACE_BYTE				;Get interface byte and put in enpoint 0 buffer.
	movx  @dptr, a						;

	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;Set end point 0 byte count to 1 byte.
	mov   dptr, EP0BCL					;
	inc   a								;
	movx  @dptr, a						;

	setb  c								;Set carry and exit.
	ret									;

GetSwitches:
	mov   dptr, EP0BUF					;
	mov   a, IOA						;Get switch states.
	movx  @dptr, a						;Load states into endpoint 0 buffer.

	clr   a								;
	mov   dptr, EP0BCH					;
	movx  @dptr, a						;
	mov   dptr, EP0BCL					;Indicate 1 byte of data is in the endpoint 0 buffer.
	inc   a								;
	movx  @dptr, a						;

	clr   c								;Clear carry bit before returning.
	ret									;

DisplayActive:
	mov   IOB, #$88						;Display an 'A' on the 7 segment display for "Active".

	mov   r7, #$f4						;	
	mov   r6, #$01						;Delay for 500 milliseconds.	
	lcall DoDelay						;	

	mov   LAST_SW_STATE, IOA			;Get status of switches and increment it.  This ensures-->
	inc   LAST_SW_STATE					;an interrupt will be sent with the status of the switches-->
	setb  c								;every time the device wakes up from being suspended.
	ret									;

;This function compares two 32 bit words. The first word (w1) is stored in r4, r5, r6, r7 where
;r4 is the most significant byte and r7 is the least significant byte. The second word (w2) is
;stored in r0, r1, r2, r3 there r0 is the most significant byte and r7 is the least significant
;byte.  When w1 > w2, the carry bit will be set upon completion of the function. When w1 = w2,
;the accumulator will be zero.  The carry bit must be cleared before the function is called.
CompareWords:
	mov   a, r3							;
	subb  a, r7							;
	mov   B, a							;
	mov   a, r2							;
	subb  a, r6							;Compare 2 32-bit numbers.
	orl   B, a							;When w1 > w2, set carry.
	mov   a, r1							;when w1 = w2, clear accumulator.
	subb  a, r5							;
	orl   B, a							;
	mov   a, r0							;
	subb  a, r4							;
	orl   a, B							;
	ret									;

DisplaySuspended:
	mov   dptr, #$1017					;
	movx  a, @dptr						;Display an 'S' on the 7 segment display for "Suspended".
	cpl   a								;
	mov   IOB, a						;

	mov   r7, #$f4						;
	mov   r6, #$01						;Delay for 500 milliseconds.
	lcall DoDelay						;

	setb  c								;Set carry bit before returning.
	ret									;

;The following table is used by a 'Get Status' and 'set feature' routine to find the proper 
;register to extract the stall status bit for a specified endpoint.  The base address for the 
;control and status registers is $E6A1.  The following table contains indexes from the base address
;to the proper register for the desired endpoint. The table represents the following endpoints:
;EP1 out, EP1 in, EP2 out, EP2 in, EP4 out, EP4 in, EP6 out, EP6 in, EP8 out, EP8 in.
EndpointCSOffsetTbl:
L0c2a:	.db $00, $01, $02, $02, $03, $03, $04, $04, $05, $05

SetConfig:
	mov   dptr, SETUPDAT_2				;
	movx  a, @dptr						;
	mov   CONFIG_BYTE, a				;Get config byte from setup data and store it.
	setb  c								;
	ret									;

SetInterface:
	mov   dptr, SETUPDAT_2				;
	movx  a, @dptr						;
	mov   INTERFACE_BYTE, a				;Get interface byte from setup data and store it.
	setb  c								;
	ret									;

DisableEP2:
	clr   a								;
	mov   dptr, EP2CFG					;Load endpoint 2 configuration register with $00.-->
	movx  @dptr, a						;This disables endpoint 2.
	ret									;

RESUME:
	anl EICON, #$EF						;Clear wake-up interrupt flag.
	reti								;Return from interrupt.

SetCarry00:
	setb c								;Set carry bit.
	ret									;Return.

SetCarry01:
	setb c								;Set carry bit.
	ret									;Return.

SetCarry02:
	setb c								;Set carry bit.
	ret									;Return.

UnusedInt0:  reti
UnusedInt1:  reti
UnusedInt2:  reti
UnusedInt3:  reti
UnusedInt4:  reti
UnusedInt5:  reti
UnusedInt6:  reti
UnusedInt7:  reti
UnusedInt8:  reti
UnusedInt9:  reti
UnusedIntA:  reti
UnusedIntB:  reti
UnusedIntC:  reti
UnusedIntD:  reti
UnusedIntE:  reti
UnusedIntF:  reti
UnusedInt10: reti
UnusedInt11: reti
UnusedInt12: reti
UnusedInt13: reti
UnusedInt14: reti
UnusedInt15: reti
UnusedInt16: reti
UnusedInt17: reti
UnusedInt18: reti
UnusedInt19: reti
UnusedInt1A: reti
UnusedInt1B: reti
UnusedInt1C: reti
UnusedInt1D: reti
UnusedInt1E: reti
UnusedInt1F: reti
UnusedInt20: reti
UnusedInt21: reti
UnusedInt22: reti

