# Spell-Management-System

## Short Description

- A CLI tool for graph-based dependency management, cycle detection, and circular dependency resolution in command-driven systems.
- This project was completed for an assignment at Macquarie University during the "Algorithms and Data Structures" unit.

---

## Project Overview

**Spellbook Dependency Engine**

- A command-line utility for parsing and validating command-driven dependency graphs.

**Technical Specs:**

- **Graph Architecture:** Directed graph using an adjacency list representation.
- **Cycle Detection:** DFS-based algorithm for identification of circular dependencies (paradoxes).
- **Dependency Resolution (HD Implementation):** * Resolution logic for both Largest and Smallest cycle minimization.
- Identifies "most recent" dependency bottlenecks to suggest resolution paths.

- **Diagnostic Mode:** Fail-safe execution state that halts command processing upon paradox detection, preventing runtime errors (e.g., stack overflows) and outputting actionable dependency data.

**Disclaimer:**

This implementation utilises manual data structure management to prioritize a first-principles understanding of graph traversal and dependency resolution. While optimised versions would leverage Java’s standard collections framework—specifically HashMap and TreeMap—for improved time complexity and abstraction, this manual approach fostered made me appreciate the performance benefits and design efficiency that standard library data structures provide. This was a harsh lesson for me - in future implementations I will be leveraging standard libraries to my advantage.

**Operations Supported:**

- `PREREQ`: Dependency validation and cycle analysis.
- `LEARN` / `FORGET`: State management (disabled during diagnostic failure).
- `ENUM`: Graph enumeration.

*Extra info:*

- Some sample `.in` and `.out` text files are included for an example of how the input works.
- For pedagogical reasons (focus was on graphs), the program was strictly limited to reading the specific format that the `.in` and `.out` files are written in.
