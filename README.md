# JavaToJS
A minimal and raw Java to JS transpiler written in Java, no dependencies, no packages, lightweight, I like to not add extra bloat whenever possible and that has been my guiding philosophy for a lot of stuff.

## Overview
JavaToJS is a minimal transpiler that converts Java to JavaScript, implementing core compiler stages: **tokenization**, **parsing**, **abstract syntax tree (AST) generation**, **semantic analysis**, and **code generation**.
Its modular design allows easy debugging, extension, and potential adaptation to generate other outputs (e.g., ByteCode or Assembly).

This project was built to understand compiler design, Java programming/workflow, and problem-solving, undertaken as a personal challenge to understand the inner workings of compilers.

## Features

- **Supported Java Constructs**:
  - Variable declarations and assignments
  - `if-else` statements
  - `while` loops
  - Unary and postfix expressions
  - Array declarations (1D and multidimensional with `new` keyword, default-filled with `null`)
  - Support for `int` and `float` arrays (note: `double` not yet supported)

- **Limitations**:
  - Multidimensional array literals are partially supported and under active development.
  - Additional Java features (e.g., classes, methods) planned for future iterations.

- **Architecture**:
  - Modular components for tokenization, parsing, semantic analysis, and code generation.
  - Designed for extensibility, allowing easy integration of new features or alternative code generators.
  - Lightweight with zero external dependencies.

## Using this code/trying it out

### Prerequisites
- Java 16 or higher
- A Java IDE (e.g., IntelliJ, VS Code with Java extensions) or command-line tools (`javac`, `java`)

### Installation
1. Clone or download the repository:
   ```bash
   git clone https://github.com/Rjv-RY/JavaToJS.git

2. Navigate to the project directory:
   ```bash
   cd JavaToJS

### Running The Transpiler

1. Open src/Main.java in your IDE or text editor.
2. Locate the testValidExpression function.
3. In the code variable, input your Java code as a string (e.g., int x = 5; if (x > 0) { x = x + 1; }).
4. Run the main function to transpile the code. Ideally your IDE could run it natively without a separate complete compilation step in the foreground.
5. View the generated JavaScript output or any errors in the console.

(Feel free to use and make changes to this code/your version as you see fit, add features, test cases, add more tokens etc, please go ham on it)

## Why this project
JavaToJS was born from the curiosity to understand compilers: "If I don't know how compilers work, do I even know how computers work?"
This project tests and showcases my ability to tackle problems, design modular systems, and learn independently. While also showing how I can work at a lower level without dependencies and abstractions.
