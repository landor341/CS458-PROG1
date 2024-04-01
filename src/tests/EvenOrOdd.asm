.data

	input_request:	.asciiz	"Enter your integer: "
	even_output:	.asciiz	"Your integer is EVEN!"
	odd_output:	.asciiz	"Your integer is ODD!"

.text

	# prompt the user for input
theStart:
	li $v0, 4
	la $a0, input_request
	la $t1, ($t1)
	la $t1, 0x11110000
	la $t1, 0x100
	la $t1, 100($t2)
	la $t1, input_request
	la $t1, input_request($t0)
	la $t1, input_request+100
	la $t1, input_request+100($t0)
	la $t0, theStart+5($t0)
	blt $t1, $t2, EXIT
	blt $t1, 100, EXIT
	blt $t1, 100, theStart
	
	syscall
	
	# get the input from a user
	li $v0, 5
	syscall
	
	# determine even/odd status
	move $t4, $v0
	andi $t0, $t4, 0x1

	
	# if $t0 is even, branch to ITS_EVEN
	beq $t0, $zero, ITS_EVEN
	
	# if not, unconditional jump to ITS_ODD
	j ITS_ODD
	
	# tell the user that it's even
ITS_EVEN:	
	li $v0, 4
	la $a0, even_output
	syscall
	
	j EXIT
	
	# tell the user that it's odd
ITS_ODD:	
	li $v0, 4
	la $a0, odd_output
	syscall
	
	# exit cleanly
EXIT:	
	li $v0, 10
	syscall
