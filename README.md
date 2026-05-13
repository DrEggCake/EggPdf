# EggPdf Viewer

A lightweight PDF viewer written using [OpenGL](https://www.opengl.org/) and [Apache PdfBox](https://pdfbox.apache.org/).

The goal of this project is to make a lightweight feature rich and high-performance PDF viewer 
by taking advantage of GPU acceleration.

---

## Current Status

### Implemented
- Basic OpenGL setup
- Basic PdfBox setup
- Rendering hardcoded PDF file, (loading the entire PDF into memory before rendering)

### Planned
- A UI
- Annotations, search, selection, any other basic features
- Multiple PDF support (via easy to use ui)
- Optimizations of the graphics pipeline

---

## Libraries

- LWJGL (OpenGL)
- Apache PDFBox

---

## Getting Started

### Requirements

- Java (17+)
- OpenGL supporting GPU
- Gradle or Maven

### Clone Repository

```bash
git clone <this-repo>
cd <project-name>
```

### Compile and run

```bash
./gradlew run
```
### Usage
- Use W, S for scrolling the PDF.
- Use Up and Down arrow keys to zoom in and out.

---

## Screenshots

![screenshot1.png](screenshots/screenshot1.png)

---

## Goals

This project exists mainly for:
- learning OpenGL rendering in Java
- an experiment in GPU-accelerated PDF rendering
- a lightweight alternative to UI heavy PDF viewers

---

## Disclaimer

Project is in early stages and may not work as expected.

---

## License

MIT License
