## SprintAPI

#### SprintAPI - Open Source High Performance Java Rest API Framework with native image support

---

### **SprintAPI Rest Java Framework – Quick Overview**

#### ** Key Features (Developed Parts)**

#### **1. Compile-Time Dependency Injection (DI)**

- Uses **annotation processor** to scan and register beans (`@Service`, `@Repository`, `@Component`, `@Controller`).
- **No reflection** → All dependencies are **wired at compile time**.
- **Faster startup** (no runtime scanning).
- **GraalVM-friendly** (no reflection).

---

#### **2. Route Registry (Compile-Time Route Registration)**

- `@GET`, `@POST`, `@PUT`, `@DELETE` annotations automatically register routes at compile time.
- Maps HTTP methods & paths to **controller methods** without reflection.
- **Faster request processing** (no runtime route lookup and method invocation).
- **Fully annotation-driven** → Simple and intuitive API.

---

#### **3. Middleware System (Flexible & Automatic)**
- `@Middleware(order=1)` registers middleware automatically in a chosen order.
- Middleware can be applied **to one specific route or globally**.
- Built-in middleware: **Logging, Authentication, CORS, Rate Limiting** (not developed, in progress).
- **Highly flexible** middleware integration.

---

#### **4.GraalVM Support (Optimized for Native Images)**
- **No runtime reflection**, making it fully **GraalVM-compatible**.
- Eliminates classpath scanning and runtime proxies.
- **Ultra-fast startup times** (ideal for microservices & serverless).
- **Lower memory footprint** (better performance on cloud deployments).
- **Seamless native image generation** with **GraalVM**.

---

#### **5. Annotation Processor (Core of the Framework)**
- Scans all classes at **compile time**.
- Generates required **DI container, route registry, and middleware registry**.
- **Blazing fast execution** (all processing done at compile time).
- **Minimal memory usage** (no runtime scanning).
- **GraalVM-optimized** (fully native-friendly).

---

## **Why SprintAPI Framework?**
**Ultra-fast startup** (no reflection, no runtime scanning).  
**Less configuration** (Common featues are built in).  
**Annotation-driven** (easy to use & intuitive).  
**GraalVM-friendly** (perfect for microservices & serverless).  
**Flexible middleware** (automatic and configurable).

Would you like to try it?

**[SprintAPI demo project demonstartion video](https://youtu.be/ak-0mXnif40?si=S7gYm65kfmv8UXdJ)**

**[SprintAPI native image creation demo video](https://youtu.be/_lHsi88XU4c?si=AI9VaH6QY-oGdh_m)**

**[SprintAPI archetechture demo](https://youtu.be/DmM0PLSw1W4?si=iOUwIOy31zUK4v0f)**

