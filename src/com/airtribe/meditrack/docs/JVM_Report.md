
## 1. Class Loader

The Java Virtual Machine (JVM) starts by loading class files into memory before executing the application. This is done by the class loader subsystem. The class loader is responsible for locating, loading, linking, and initializing classes.

### Main responsibilities
- Loading `.class` files from disk, JARs, or network sources
- Verifying class correctness and structure
- Preparing memory for static fields and other metadata
- Resolving symbolic references between classes
- Initializing classes before first active use

### Class loader hierarchy
Java uses a delegation model:

1. Bootstrap Class Loader
   - Loads core JDK classes such as `java.lang`, `java.util`, and `java.io`
   - Built into the JVM
   - Has the highest priority in the parent delegation chain

2. Extension / Platform Class Loader
   - Loads standard extension and platform classes
   - Handles classes beyond the bootstrap classpath

3. Application / System Class Loader
   - Loads application classes from the classpath
   - Usually the loader that loads your project classes

### Delegation principle
When the JVM requests a class, the class loader first delegates the request to its parent. Only if the parent cannot find the class does the child loader try to load it. This helps maintain consistency and avoid duplicate class definitions.

### Important concepts
- `ClassNotFoundException`: thrown when a class cannot be found
- `NoClassDefFoundError`: thrown when a required class was present during compile time but missing during runtime
- Class initialization happens lazily, when the class is first used

### Example
When you run:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```

The JVM loads `Main.class`, then resolves dependencies, initializes static fields, and finally starts execution.

---

## 2. Runtime Data Areas

The JVM allocates memory for different runtime purposes. These are called runtime data areas. The main areas are:

### a) Heap
The heap is the runtime memory area where all objects and arrays are stored.

#### Characteristics
- Created when the JVM starts
- Shared across threads
- Stores dynamically allocated objects
- Subject to garbage collection

#### Purpose
- Holds instance variables
- Holds objects created by `new`
- Stores arrays and dynamic data structures

#### Memory management
The garbage collector reclaims memory that is no longer reachable, helping prevent memory leaks and unused object buildup.

Example:

```java
String name = new String("MediTrack");
```

The `String` object is allocated in the heap.

### b) Stack
Each thread has its own Java stack.

#### Characteristics
- Stores frames
- Follows LIFO (Last In, First Out) structure
- Not shared between threads

#### What is stored in a stack frame?
- Local variables
- Operand stack
- Method call information
- Partial results of computations

#### Example
When a method is called, a new frame is pushed onto the stack. When it returns, the frame is popped.

```java
int add(int a, int b) {
    int result = a + b;
    return result;
}
```

The `a`, `b`, and `result` values are tracked in the stack frame for that method.

### c) Method Area
The method area stores class-level metadata.

#### Contains
- Class structure
- Field information
- Method code
- Static variables
- Constant pool
- Metadata for classes and interfaces

#### Characteristics
- Shared among all threads
- Used by the JVM for class information
- In modern JVMs, method area is often implemented with metaspace (non-heap memory)

### d) PC Register
The Program Counter (PC) Register keeps track of the current instruction being executed by each thread.

#### Purpose
- Holds the address of the next instruction to be executed
- Needed because Java supports multithreading
- Each thread has its own PC register

#### If a thread is executing a native method
The PC register may hold an undefined or null value.

---

## 3. Execution Engine

The execution engine is responsible for executing the bytecode loaded by the class loader and stored in memory.

It performs the actual runtime execution of Java programs.

### Main components
- Interpreter
- JIT Compiler
- Garbage Collector
- Runtime services

### How it works
The JVM reads bytecode and converts it into machine-level instructions that the host OS and CPU can execute.

#### Bytecode example
```java
public class Example {
    public static void main(String[] args) {
        int x = 10;
        int y = 20;
        int z = x + y;
        System.out.println(z);
    }
}
```

This is compiled into bytecode, which is then interpreted or compiled by the JVM for execution.

---

## 4. JIT Compiler vs Interpreter

### Interpreter
An interpreter reads bytecode line by line and executes it immediately.

#### Pros
- Quick startup
- Simple design
- Good for early execution

#### Cons
- Slower execution for repeated code
- Re-interprets the same bytecode repeatedly

### JIT Compiler (Just-In-Time Compiler)
The JIT compiler compiles frequently used bytecode into native machine code at runtime.

#### Pros
- Faster execution for hot code paths
- Optimizes repeated method calls
- Reduces overhead of interpretation

#### Cons
- Warm-up time required before optimization
- Extra compilation overhead initially

### Relationship between them
Modern JVMs use both:
- The interpreter executes code initially
- The JIT compiler optimizes hotspot methods that are called frequently
- This gives a balance between startup speed and performance

### Example
A method in a loop that executes millions of times may be compiled by the JIT compiler into highly optimized native code.

---

## 5. Write Once, Run Anywhere

The slogan “Write Once, Run Anywhere” is one of Java’s biggest advantages.

### Meaning
Java source code is compiled into bytecode, not native machine code. That bytecode is platform-independent.

### Flow
1. Write Java source code
2. Compile it with `javac`
3. Bytecode is generated in `.class` files
4. JVM on any platform interprets or JIT-compiles that bytecode
5. Same program runs on Windows, Linux, macOS, etc.

### Why it works
Because the JVM provides the platform-specific layer.

- The Java code is platform-independent
- The JVM is platform-dependent
- The JVM translates the same bytecode according to the host machine

### Example
If you write a Java application on one machine, you can run the same compiled bytecode on another machine with a compatible JVM, without recompiling the code.

### Important note
This is not literally “any device” in every situation, but it means Java code is portable across operating systems and hardware as long as a compatible JVM exists.

---

## Summary

The JVM is a powerful runtime environment that:
- loads classes via the class loader
- manages memory in runtime data areas such as heap, stack, method area, and PC register
- executes bytecode through the execution engine
- uses a combination of interpretation and JIT compilation for performance
- enables Java’s portability through bytecode and the JVM abstraction

This combination is what makes Java widely used for enterprise applications, Android development, backend services, and cross-platform systems.
