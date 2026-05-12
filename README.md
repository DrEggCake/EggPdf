# EggPdf Viewer

A lightweight pdf viewer written using [OpenGL](https://www.opengl.org/) and [Apache PdfBox](https://pdfbox.apache.org/).

The goal of this project is to make a lightweight feature rich and high performant pdf viewer 
by taking advantage of GPU-acceleration.

---

## Current Status

### Implemented
- Basic OpenGL setup
- Basic PdfBox setup
- Rendering hardcoded pdf, (loading entire pdf into memory before rendering)

### Planned
- A UI
- Annotations, search, selection, any other basic features
- Multiple pdf support (via easy to use ui)
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
- Use W, S for scrolling the pdf.
- Use Up arrow, down arrow for zooming in and out.

---

## Screenshots

![screenshot1.png](screenshots/screenshot1.png)

---

## Goals

This project exists mainly for:
- learning OpenGL rendering in Java
- an experiment in GPU-accelerated PDF rendering
- an lightweight alternative to ui heavy pdf viewers

---

## Disclaimer

Project is in early stages and may not work as expected.

---

## License

MIT License
