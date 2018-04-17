	.file	"arch_spec.c"
	.comm	eax,4,4
	.comm	ebx,4,4
	.comm	ecx,4,4
	.comm	edx,4,4
	.comm	cpu_model,4,4
	.globl	read_time
	.bss
	.align 4
	.type	read_time, @object
	.size	read_time, 4
read_time:
	.zero	4
	.globl	max_pkg
	.align 8
	.type	max_pkg, @object
	.size	max_pkg, 8
max_pkg:
	.zero	8
	.globl	num_core_thread
	.align 8
	.type	num_core_thread, @object
	.size	num_core_thread, 8
num_core_thread:
	.zero	8
	.globl	num_pkg_thread
	.align 8
	.type	num_pkg_thread, @object
	.size	num_pkg_thread, 8
num_pkg_thread:
	.zero	8
	.globl	num_pkg_core
	.align 8
	.type	num_pkg_core, @object
	.size	num_pkg_core, 8
num_pkg_core:
	.zero	8
	.globl	num_pkg
	.align 8
	.type	num_pkg, @object
	.size	num_pkg, 8
num_pkg:
	.zero	8
	.globl	core
	.align 4
	.type	core, @object
	.size	core, 4
core:
	.zero	4
	.globl	coreNum
	.align 4
	.type	coreNum, @object
	.size	coreNum, 4
coreNum:
	.zero	4
	.comm	cpu_info,16,16
	.text
	.globl	get_cpu_model
	.type	get_cpu_model, @function
get_cpu_model:
.LFB2:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	pushq	%rbx
	.cfi_offset 3, -24
	movl	$1, -12(%rbp)
	movl	ecx(%rip), %edx
	movl	-12(%rbp), %eax
	movl	%edx, %ecx
#APP
# 30 "arch_spec.c" 1
	cpuid
# 0 "" 2
#NO_APP
	movl	%ebx, %esi
	movl	%eax, -12(%rbp)
	movl	%esi, ebx(%rip)
	movl	%ecx, ecx(%rip)
	movl	%edx, edx(%rip)
	movl	-12(%rbp), %eax
	shrl	$16, %eax
	sall	$4, %eax
	movzbl	%al, %eax
	movl	-12(%rbp), %edx
	shrl	$4, %edx
	andl	$15, %edx
	addl	%edx, %eax
	movl	%eax, cpu_model(%rip)
	popq	%rbx
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE2:
	.size	get_cpu_model, .-get_cpu_model
	.globl	core_num
	.type	core_num, @function
core_num:
.LFB3:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movl	$83, %edi
	call	sysconf
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE3:
	.size	core_num, .-core_num
	.globl	parse_apic_id
	.type	parse_apic_id, @function
parse_apic_id:
.LFB4:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movq	%rdi, %rax
	movq	%rsi, %r9
	movq	%rax, %rsi
	movq	%rdx, %rdi
	movq	%r9, %rdi
	movq	%rsi, -64(%rbp)
	movq	%rdi, -56(%rbp)
	movq	%rdx, -80(%rbp)
	movq	%rcx, -72(%rbp)
	movq	%r8, -88(%rbp)
	movl	-64(%rbp), %eax
	movl	%eax, %eax
	andl	$31, %eax
	movq	%rax, -40(%rbp)
	movq	-40(%rbp), %rax
	movl	$-1, %edx
	movl	%eax, %ecx
	sall	%cl, %edx
	movl	%edx, %eax
	notl	%eax
	cltq
	movq	%rax, -32(%rbp)
	movl	-52(%rbp), %eax
	movl	%eax, %eax
	andq	-32(%rbp), %rax
	movq	%rax, %rdx
	movq	-88(%rbp), %rax
	movq	%rdx, (%rax)
	movl	-80(%rbp), %eax
	movl	%eax, %eax
	andl	$31, %eax
	movq	%rax, -24(%rbp)
	movq	-24(%rbp), %rax
	movl	$-1, %edx
	movl	%eax, %ecx
	sall	%cl, %edx
	movl	%edx, %eax
	notl	%eax
	cltq
	xorq	-32(%rbp), %rax
	movq	%rax, -16(%rbp)
	movl	-68(%rbp), %eax
	movl	%eax, %eax
	andq	-16(%rbp), %rax
	movq	%rax, %rdx
	movq	-40(%rbp), %rax
	movl	%eax, %ecx
	shrq	%cl, %rdx
	movq	-88(%rbp), %rax
	movq	%rdx, 8(%rax)
	movq	-24(%rbp), %rax
	movl	$-1, %edx
	movl	%eax, %ecx
	sall	%cl, %edx
	movl	%edx, %eax
	cltq
	movq	%rax, -8(%rbp)
	movl	-68(%rbp), %eax
	movl	%eax, %eax
	andq	-8(%rbp), %rax
	movq	%rax, %rdx
	movq	-24(%rbp), %rax
	movl	%eax, %ecx
	shrq	%cl, %rdx
	movq	-88(%rbp), %rax
	movq	%rdx, 16(%rax)
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE4:
	.size	parse_apic_id, .-parse_apic_id
	.globl	cpuid
	.type	cpuid, @function
cpuid:
.LFB5:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movl	%edi, -4(%rbp)
	movl	%esi, -8(%rbp)
	movq	%rdx, -16(%rbp)
	movl	-4(%rbp), %eax
	movl	-8(%rbp), %edx
	movl	%edx, %ecx
#APP
# 59 "arch_spec.c" 1
	cpuid;movl %ebx, %esi;
# 0 "" 2
#NO_APP
	movl	%eax, %edi
	movq	-16(%rbp), %rax
	movl	%edi, (%rax)
	movq	-16(%rbp), %rax
	movl	%esi, 4(%rax)
	movq	-16(%rbp), %rax
	movl	%ecx, 8(%rax)
	movq	-16(%rbp), %rax
	movl	%edx, 12(%rax)
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE5:
	.size	cpuid, .-cpuid
	.globl	getProcessorTopology
	.type	getProcessorTopology, @function
getProcessorTopology:
.LFB6:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$24, %rsp
	movl	%edi, -20(%rbp)
	leaq	-16(%rbp), %rdx
	movl	-20(%rbp), %eax
	movl	%eax, %esi
	movl	$11, %edi
	call	cpuid
	movq	-16(%rbp), %rax
	movq	-8(%rbp), %rdx
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE6:
	.size	getProcessorTopology, .-getProcessorTopology
	.section	.rodata
	.align 8
.LC0:
	.string	"num_core_thread:%d num_pkg_thread:%d num_pkg_core:%d num_pkg:%d\n"
	.text
	.globl	getSocketNum
	.type	getSocketNum, @function
getSocketNum:
.LFB7:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	pushq	%rbx
	subq	$56, %rsp
	.cfi_offset 3, -24
	movl	$0, -56(%rbp)
	movl	$1, -52(%rbp)
	movl	$0, %eax
	call	core_num
	movl	%eax, coreNum(%rip)
	movl	-56(%rbp), %eax
	movl	%eax, %edi
	call	getProcessorTopology
	movq	%rax, -48(%rbp)
	movq	%rdx, -40(%rbp)
	movl	-52(%rbp), %eax
	movl	%eax, %edi
	call	getProcessorTopology
	movq	%rax, -32(%rbp)
	movq	%rdx, -24(%rbp)
	movl	-44(%rbp), %eax
	movl	%eax, %eax
	movzwl	%ax, %eax
	movq	%rax, num_core_thread(%rip)
	movl	-28(%rbp), %eax
	movl	%eax, %eax
	movzwl	%ax, %eax
	movq	%rax, num_pkg_thread(%rip)
	movq	num_pkg_thread(%rip), %rax
	movq	num_core_thread(%rip), %rbx
	movl	$0, %edx
	divq	%rbx
	movq	%rax, num_pkg_core(%rip)
	movl	coreNum(%rip), %eax
	cltq
	movq	num_pkg_thread(%rip), %rbx
	movl	$0, %edx
	divq	%rbx
	movq	%rax, num_pkg(%rip)
	movq	num_pkg(%rip), %rsi
	movq	num_pkg_core(%rip), %rcx
	movq	num_pkg_thread(%rip), %rdx
	movq	num_core_thread(%rip), %rax
	movq	%rsi, %r8
	movq	%rax, %rsi
	movl	$.LC0, %edi
	movl	$0, %eax
	call	printf
	movq	num_pkg(%rip), %rax
	addq	$56, %rsp
	popq	%rbx
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE7:
	.size	getSocketNum, .-getSocketNum
	.ident	"GCC: (Ubuntu 4.8.4-2ubuntu1~14.04.3) 4.8.4"
	.section	.note.GNU-stack,"",@progbits
