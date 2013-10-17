INGENME 
-------

INGENME is the acronym for INGENIAS Meta-Editor. It is a tool for producing self-contained visual editors for languages defined using an XML file. It is a simpler alternative to Eclipse GMF since it requires minimal input to produce a common visual editor. 

Requirements
------------
ANT 1.6.5 (or higher) and JDK 1.6.0 (or higher) 

Demo
--------------
INGENME has three demo editors inside. The configuration files and resources needed by each editor are stored in the projects folder of the distribution. The editors are INGENED, NODEREL, and FAMLED (preliminary name)

+INGENED is a visual editor that realizes the meta-modeling language GOPRR-like which INGENME uses as input. The editor is created in the $HOME/ingened folder automatically. 

+NODEREL is basic graph editor for educational purposes. It creates diagrams with one type of node and only one type relationship. The relationship has a field Cost. The produced diagrams are useful for exercises requiring weighted graphs. The editor is created in the $HOME/noderel folder automatically.

+FAMLED is an demo implementation of a subset of four entities of the meta-model of FAML, which is  a modeling language for Multi-Agent Systems. The editor is created in the $HOME/famled folder automatically.

By default, INGENME produces the NODEREL editor. The instructions for editor generation have to be coded in the defaultproject.properties file. To switch between editors, overwrite this file with the corresponding properties file stored in each corresponding project folder under the main projects folder. 

To generate the editor, type "ant" in the console inside of the install folder. Then, go to the  corresponding editor folder and type "ant runide"


Licenses
--------
This software is distributed under the terms of the GPLv3 license. A copy is available in the home folder of this distribution.
