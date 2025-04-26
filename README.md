# JavaToJS
A minimal and raw Java to JS transpiler written in Java

## Overview
This is a minimal Java to JS transpiler, it follows all the steps a compiler does from tokenizing, parsing to AST, 
analyzing semantics and checking validity etc. It just generates JS. As such it is possible to just change the 
code generation to ByteCode or Assembly to make it a proper compiler.

It is modularized and sufficiently easy to debug(isolate errors) and add/expand without breaking existing functionality. 
And as various parts are separate to each other, it would not be difficult to just take semantic analyzer output and 
run it through a different/fresh codegen.

## Current Support/Limitations
Currently it supports 
- Variable Declerations 
- if else statements 
- while loops 
- unary exprs 
- postfix exprs 
- separate var declerations and instantiations 
- array declerations both 1D and multi dimensional [ still a wip :,( ]

but, it lacks for loops, function declerations, switch case, and also array indexing.


## Using this code/trying it out
(WILL UPDATE THIS LATER, I PROMISE)

## Footer/thoughts
This undertaking was initiated due to a simple idea:-
If I don't know how compilers work, do I even know how computers work?
