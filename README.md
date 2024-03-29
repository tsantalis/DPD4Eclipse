# General info
DPD4Eclipse in an Eclipse plugin that detects 16 GoF design patterns and visualizes the detected instances.
It is based on the [Design Pattern Detection (DPD) tool](https://users.encs.concordia.ca/~nikolaos/pattern_detection.html) version 4.13 - build 25/02/2020

The supported design patterns are:
1. Factory Method
2. Prototype
3. Singleton
4. Object Adapter
5. Command
6. Composite
7. Decorator
8. Observer
9. State
10. Strategy
11. Bridge
12. Template Method
13. Visitor
14. Proxy
15. Proxy variation
16. Chain of Responsibility

# How to run the plug-in

1. Clone the DPD4Eclipse repository
2. Import the project into Eclipse: File -> Import... -> General -> Existing Projects into Workspace
3. Right-click on the project and select: Run as -> Eclipse Application
4. This will open a second Eclipse IDE window with the DPD4Eclipse plug-in installed in it.
5. In the second Eclipse IDE window select: Window -> Show View -> Other... -> Design Pattern Detection (folder in Show View dialog) -> Design Pattern Detection
6. Import a project in the second Eclipse IDE window, select the project in the Package Explorer, and then click on the **i** button (Detect Design Patterns) on the Design Pattern Detection view.

# Key features
- Double-clicking on a detected design pattern instance role, opens the corresponding program element (class, method, field) in the Eclipse editor.
- Each design pattern instance can be visualized in a hybrid UML Class diagram showing the dependencies between the program elements participating in the design pattern instance.

![Screenshot from 2022-02-26 17-11-35](https://user-images.githubusercontent.com/1483516/155861037-74e6822d-35e9-4b6e-a67a-1ed4f6460bee.png)

![Screenshot from 2022-03-06 08-32-29](https://user-images.githubusercontent.com/1483516/156925528-8c7d1592-a531-4301-a9ac-0268a90d6dfb.png)

# Related research

N. Tsantalis, A. Chatzigeorgiou, G. Stephanides, S. T. Halkidis, "[Design Pattern Detection Using Similarity Scoring](https://users.encs.concordia.ca/~nikolaos/publications/TSE_2006.pdf)," IEEE Transactions on Software Engineering, vol. 32, no. 11, pp. 896-909, November, 2006.


    @article{Tsantalis:2006:DPD:1248727.1248777,
	author = {Tsantalis, Nikolaos and Chatzigeorgiou, Alexander and Stephanides, George and Halkidis, Spyros T.},
	title = {Design Pattern Detection Using Similarity Scoring},
	journal = {IEEE Transactions on Software Engineering},
	issue_date = {November 2006},
	volume = {32},
	number = {11},
	month = nov,
	year = {2006},
	issn = {0098-5589},
	pages = {896--909},
	numpages = {14},
	url = {http://dx.doi.org/10.1109/TSE.2006.112},
	doi = {10.1109/TSE.2006.112},
	acmid = {1248777},
	publisher = {IEEE Press},
	address = {Piscataway, NJ, USA},
    }
