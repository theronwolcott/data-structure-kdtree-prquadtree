# KD-Trees and PR-QuadTrees

## Overview
This project is an implementation of two essential spatial data structures: **KD-Trees** and **Point-Region (PR) QuadTrees**. It showcases the application of **object-oriented programming** principles, specifically **inheritance** and **polymorphism**, to build efficient, modular, and reusable components. The project demonstrates expertise in handling spatial data, designing algorithms for spatial queries, and implementing advanced data structures for multi-dimensional indexing.

## Features

### 1. KD-Tree Implementation
- **Node Management**: Implemented the `KDTreeNode` class, enabling hierarchical multi-dimensional indexing.
- **Nearest Neighbor Queries**: Efficiently performed \( k \)-nearest neighbor searches using heuristic and branch-and-bound techniques.
- **Bounded Priority Queue**: Designed a custom `BoundedPriorityQueue` to manage the top-\( k \) nearest neighbors, adhering to elementary efficiency principles.
- **Range Queries**: Implemented range search queries to retrieve all points within a specified range efficiently.

### 2. PR-QuadTree Implementation
- **Dynamic Node Management**: Implemented `PRQuadBlackNode` and `PRQuadGrayNode` classes to handle black and gray nodes dynamically based on the state of the tree.
- **Spatial Queries**: Designed and implemented range and \( k \)-nearest neighbor search algorithms optimized for PR-QuadTrees.
- **Region Splitting**: Developed logic for region splitting with precise geometric boundaries, handling edge cases with a `CentroidAccuracyException` for minimum region sizes.
- **Polymorphism**: Leveraged the abstract `PRQuadNode` class to handle node operations polymorphically.

## Project Highlights

### Code Structure
The project is organized into two main components:

#### KD-Trees
- `KDTree`: The main class provided, delegating operations to `KDTreeNode`.
- `KDTreeNode`: Handles core KD-Tree functionality like insertion, deletion, and spatial queries.
- `BoundedPriorityQueue`: Manages the top-\( k \) neighbors efficiently for nearest neighbor queries.

#### PR-QuadTrees
- `PRQuadTree`: The main class provided, delegating operations to the node classes.
- `PRQuadBlackNode`: Represents leaf nodes containing points.
- `PRQuadGrayNode`: Represents internal nodes, managing up to four child quadrants.
- `PRQuadNode`: Abstract class defining the common interface for all node types.

### Design Principles
- **Efficiency**: Avoided naive search methods, employing heuristic-driven spatial query algorithms.
- **Modularity**: Built reusable and extensible components adhering to object-oriented programming best practices.
- **Error Handling**: Introduced exception handling (`CentroidAccuracyException`) to manage edge cases robustly.

### Algorithms
- Implemented range queries using a combination of depth-first traversal and geometric intersection checks.
- Designed nearest neighbor queries with descending (greedy) and ascending (pruning) phases for optimal performance.
- Ensured \( k \)-NN queries exclude the anchor point, as required.

### Testing
- Comprehensive unit tests for all components, including edge cases and performance benchmarks.
- Visualizations of trees using the provided `treeDescription()` methods for KD-Trees and PR-QuadTrees.

## Challenges Overcome
- Designed recursive algorithms for dynamic node transformations in PR-QuadTrees.
- Handled mutable `KDPoint` objects without aliasing by employing deep copies.
- Optimized spatial queries to minimize unnecessary traversal and maximize efficiency.

## Key Learnings
- Gained proficiency in implementing advanced spatial data structures and algorithms.
- Deepened understanding of geometric computations and their application in computer science.
- Strengthened object-oriented programming skills, including inheritance, polymorphism, and encapsulation.

## Usage
This project can be used as a reference or foundation for applications in:
- Geographic Information Systems (GIS)
- Computer Graphics
- Robotics (e.g., obstacle avoidance)
- Game Development (e.g., collision detection)
