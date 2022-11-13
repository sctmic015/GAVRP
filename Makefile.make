# Makefile: SCTMIC015

JAVAC = /usr/bin/javac
.SUFFIXES: .java .class

SRCDIR=src
BINDIR=bin
DOCDIR=doc

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES=DataManagement.class Utility.class Fitness.class City.class MersenneTwisterFast.class Configuration.class Gene.class Route.class GeneticAlgorithm.class Application.class Tune.class

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

docs:
	javadoc -d $(DOCDIR) $(SRCDIR)/*.java
clean:
	rm $(BINDIR)/*.class
run:
	java -cp bin Tune
cleandocs:
	rm -r docs/*